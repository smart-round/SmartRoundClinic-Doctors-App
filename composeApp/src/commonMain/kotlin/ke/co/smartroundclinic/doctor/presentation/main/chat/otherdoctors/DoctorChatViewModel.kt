package ke.co.smartroundclinic.doctor.presentation.main.chat.otherdoctors

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import io.github.aakira.napier.Napier
import io.ktor.client.HttpClient
import io.ktor.client.plugins.websocket.webSocket
import io.ktor.websocket.DefaultWebSocketSession
import io.ktor.websocket.Frame
import io.ktor.websocket.readText
import ke.co.smartroundclinic.doctor.common.Constants
import ke.co.smartroundclinic.doctor.common.Resource
import ke.co.smartroundclinic.doctor.core.snackbar.SnackbarController
import ke.co.smartroundclinic.doctor.data.remote.dto.response.DoctorCallAnsweredEventData
import ke.co.smartroundclinic.doctor.data.remote.dto.response.DoctorCallCancelledEventData
import ke.co.smartroundclinic.doctor.data.remote.dto.response.DoctorCallDeclinedEventData
import ke.co.smartroundclinic.doctor.data.remote.dto.response.DoctorCallInviteEventData
import ke.co.smartroundclinic.doctor.data.remote.dto.response.DoctorChatMessageData
import ke.co.smartroundclinic.doctor.data.remote.dto.response.DoctorChatWsEventPeek
import ke.co.smartroundclinic.doctor.data.remote.dto.response.DoctorChatWsOutgoing
import ke.co.smartroundclinic.doctor.data.remote.dto.response.toDomain
import ke.co.smartroundclinic.doctor.domain.model.CallJoinInfo
import ke.co.smartroundclinic.doctor.domain.model.Doctor
import ke.co.smartroundclinic.doctor.domain.model.DoctorChatMessage
import ke.co.smartroundclinic.doctor.domain.model.DoctorChatThread
import ke.co.smartroundclinic.doctor.domain.repository.DoctorChatRepository
import ke.co.smartroundclinic.doctor.domain.repository.UserLocalRepository
import ke.co.smartroundclinic.doctor.domain.usecase.directory.GetRecommendedDoctorsUseCase
import ke.co.smartroundclinic.doctor.domain.usecase.doctorchat.InitiateDoctorChatUseCase
import ke.co.smartroundclinic.doctor.presentation.main.chat.PendingFile
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlinx.serialization.json.Json

private val wsJson = Json { ignoreUnknownKeys = true; isLenient = true; explicitNulls = false }
private const val HISTORY_PAGE_SIZE = 20

/** Local, ChatRoot-scoped ringing state — see this feature's scope notes: unlike consultation's
 * calls, doctor-chat calling doesn't integrate with CallKit/background push this round, so a
 * plain ViewModel-owned state (instead of a global singleton) is enough — navigation between the
 * conversation, outgoing-call, and in-call screens all happens within one ChatRoot composition
 * sharing this same instance. */
sealed class DoctorOutgoingCallStatus {
    data class Calling(val callId: String) : DoctorOutgoingCallStatus()
    data object Answered : DoctorOutgoingCallStatus()
    data object Declined : DoctorOutgoingCallStatus()
}

data class DoctorIncomingCall(
    val callId: String,
    val callerId: String,
    val callerName: String?,
    val isVideo: Boolean,
    val ringTimeoutSeconds: Long,
)

/**
 * Mirrors the message-send/receive slice of [ke.co.smartroundclinic.doctor.presentation.main.chat.ConsultationViewModel],
 * simplified: no typing/presence relay (see :doctor-chat backend's own scope notes) and calling
 * state is local rather than routed through the global OutgoingCallState/IncomingCallHandler
 * singletons, which stay patient-call-only.
 */
class DoctorChatViewModel(
    private val repository: DoctorChatRepository,
    private val initiateDoctorChatUseCase: InitiateDoctorChatUseCase,
    private val getRecommendedDoctorsUseCase: GetRecommendedDoctorsUseCase,
    private val userLocalRepository: UserLocalRepository,
    private val httpClient: HttpClient,
    private val snackbarController: SnackbarController,
) : ViewModel() {

    var threads by mutableStateOf<List<DoctorChatThread>>(emptyList())
        private set
    var isLoadingThreads by mutableStateOf(false)
        private set

    // A general-purpose pool fetched once for the "search any doctor by name" box — filters
    // client-side rather than hitting a dedicated name-search endpoint, which doesn't exist yet.
    var searchableDoctors by mutableStateOf<List<Doctor>>(emptyList())
        private set
    var isLoadingSearchableDoctors by mutableStateOf(false)
        private set

    fun loadSearchableDoctors() {
        if (searchableDoctors.isNotEmpty() || isLoadingSearchableDoctors) return
        viewModelScope.launch {
            isLoadingSearchableDoctors = true
            when (val result = getRecommendedDoctorsUseCase(null, 1, 50, currentUserId.ifBlank { null })) {
                is Resource.Success -> searchableDoctors = result.data?.doctors ?: emptyList()
                else -> {}
            }
            isLoadingSearchableDoctors = false
        }
    }

    fun startChatWith(doctorId: String, onThreadReady: (DoctorChatThread) -> Unit) {
        viewModelScope.launch {
            when (val result = initiateDoctorChatUseCase(doctorId)) {
                is Resource.Success -> result.data?.let(onThreadReady)
                is Resource.Error -> snackbarController.show(result.message ?: "Failed to start chat", isError = true)
                else -> {}
            }
        }
    }

    var currentUserId by mutableStateOf("")
        private set
    var currentUserProfilePicture by mutableStateOf<String?>(null)
        private set

    private var currentThreadId: String? = null

    val messages = mutableStateListOf<DoctorChatMessage>()
    val pendingFiles = mutableStateListOf<PendingFile>()
    var isConnected by mutableStateOf(false)
        private set

    var isLoadingHistory by mutableStateOf(false)
        private set
    var isLoadingMoreHistory by mutableStateOf(false)
        private set
    var hasMoreHistory by mutableStateOf(false)
        private set
    private var nextHistoryCursor: String? = null

    val isUploadingFile: Boolean get() = pendingFiles.any { !it.failed }

    var callJoinState by mutableStateOf<Resource<CallJoinInfo>?>(null)
        private set
    var outgoingCallStatus by mutableStateOf<DoctorOutgoingCallStatus?>(null)
        private set
    var incomingCall by mutableStateOf<DoctorIncomingCall?>(null)
        private set

    private var wsJob: Job? = null
    private var wsSession: DefaultWebSocketSession? = null

    init {
        viewModelScope.launch {
            userLocalRepository.observeUser().collect { user ->
                currentUserId = user?.id ?: ""
                currentUserProfilePicture = user?.profilePicture
            }
        }
        loadThreads()
    }

    fun loadThreads() {
        viewModelScope.launch {
            isLoadingThreads = true
            when (val result = repository.listThreads()) {
                is Resource.Success -> threads = result.data ?: emptyList()
                is Resource.Error -> snackbarController.show(result.message ?: "Failed to load conversations", isError = true)
                else -> {}
            }
            isLoadingThreads = false
        }
    }

    fun loadHistory(threadId: String) {
        viewModelScope.launch {
            isLoadingHistory = true
            messages.clear()
            when (val result = repository.getHistory(threadId, null, HISTORY_PAGE_SIZE)) {
                is Resource.Success -> {
                    val page = result.data
                    messages.addAll(page?.items.orEmpty().asReversed())
                    nextHistoryCursor = page?.nextCursor
                    hasMoreHistory = page?.nextCursor != null
                }
                is Resource.Error -> snackbarController.show(result.message ?: "Failed to load conversation", isError = true)
                else -> {}
            }
            isLoadingHistory = false
        }
    }

    fun loadMoreHistory(threadId: String) {
        val cursor = nextHistoryCursor ?: return
        if (isLoadingMoreHistory) return
        viewModelScope.launch {
            isLoadingMoreHistory = true
            when (val result = repository.getHistory(threadId, cursor, HISTORY_PAGE_SIZE)) {
                is Resource.Success -> {
                    val page = result.data
                    val existingIds = messages.map { it.id }.toSet()
                    val older = page?.items.orEmpty().asReversed().filterNot { it.id in existingIds }
                    messages.addAll(0, older)
                    nextHistoryCursor = page?.nextCursor
                    hasMoreHistory = page?.nextCursor != null
                }
                is Resource.Error -> snackbarController.show(result.message ?: "Failed to load more messages", isError = true)
                else -> {}
            }
            isLoadingMoreHistory = false
        }
    }

    fun connectToThread(threadId: String) {
        if (currentThreadId == threadId && wsJob?.isActive == true) return
        wsJob?.cancel()
        currentThreadId = threadId
        isConnected = false
        wsJob = viewModelScope.launch(Dispatchers.IO) {
            val wsBase = Constants.BASE_URL.replace("https://", "wss://").replace("http://", "ws://")
            var attempt = 0
            while (isActive) {
                try {
                    httpClient.webSocket("${wsBase}doctor-chat/threads/$threadId") {
                        wsSession = this
                        withContext(Dispatchers.Main) { isConnected = true }
                        attempt = 0

                        for (frame in incoming) {
                            if (frame is Frame.Text) {
                                val raw = frame.readText()
                                try {
                                    when (wsJson.decodeFromString<DoctorChatWsEventPeek>(raw).type) {
                                        "CALL_INVITE" -> {
                                            val event = wsJson.decodeFromString<DoctorCallInviteEventData>(raw)
                                            withContext(Dispatchers.Main) {
                                                incomingCall = DoctorIncomingCall(event.callId, event.callerId, event.callerName, event.isVideo, event.ringTimeoutSeconds)
                                            }
                                        }
                                        "CALL_ANSWERED" -> {
                                            wsJson.decodeFromString<DoctorCallAnsweredEventData>(raw)
                                            withContext(Dispatchers.Main) { outgoingCallStatus = DoctorOutgoingCallStatus.Answered }
                                        }
                                        "CALL_DECLINED" -> {
                                            wsJson.decodeFromString<DoctorCallDeclinedEventData>(raw)
                                            withContext(Dispatchers.Main) { outgoingCallStatus = DoctorOutgoingCallStatus.Declined }
                                        }
                                        "CALL_CANCELLED" -> {
                                            wsJson.decodeFromString<DoctorCallCancelledEventData>(raw)
                                            withContext(Dispatchers.Main) { incomingCall = null }
                                        }
                                        else -> {
                                            val msg = wsJson.decodeFromString<DoctorChatMessageData>(raw).toDomain()
                                            withContext(Dispatchers.Main) {
                                                if (messages.none { it.id == msg.id }) {
                                                    messages.add(msg)
                                                    if (msg.messageType.uppercase() == "FILE" && msg.senderId == currentUserId) {
                                                        pendingFiles.removeAll { p -> msg.files.any { it.fileName == p.fileName } }
                                                    }
                                                }
                                            }
                                        }
                                    }
                                } catch (e: Exception) {
                                    Napier.w(tag = "DoctorChat", message = "Failed to decode frame: ${e.message}")
                                }
                            }
                        }
                    }
                } catch (e: CancellationException) {
                    throw e
                } catch (_: Exception) {
                } finally {
                    wsSession = null
                    withContext(Dispatchers.Main) { isConnected = false }
                }

                if (isActive) {
                    attempt++
                    delay(minOf(1_000L shl minOf(attempt - 1, 5), 30_000L))
                }
            }
        }
    }

    fun disconnect() {
        wsJob?.cancel()
        wsJob = null
        currentThreadId = null
        isConnected = false
    }

    fun sendText(text: String) {
        if (text.isBlank()) return
        val session = wsSession
        if (session == null) {
            snackbarController.show("Not connected. Please wait a moment and try again.", isError = true)
            return
        }
        viewModelScope.launch(Dispatchers.IO) {
            try {
                session.send(Frame.Text(wsJson.encodeToString(DoctorChatWsOutgoing(type = "TEXT", message = text))))
            } catch (_: Exception) {
                snackbarController.show("Failed to send message", isError = true)
            }
        }
    }

    fun sendFile(threadId: String, fileName: String, contentType: String, bytes: ByteArray) {
        val pending = PendingFile(localId = "p${kotlin.random.Random.nextInt()}", fileName = fileName, contentType = contentType, bytes = bytes)
        pendingFiles.add(pending)
        viewModelScope.launch {
            when (val result = repository.uploadFile(threadId, fileName, contentType, bytes)) {
                is Resource.Success -> {
                    val msg = result.data
                    if (msg != null && messages.none { it.id == msg.id }) {
                        messages.add(msg)
                        pendingFiles.removeAll { it.localId == pending.localId }
                    }
                }
                is Resource.Error -> {
                    snackbarController.show(result.message ?: "Failed to send file", isError = true)
                    val index = pendingFiles.indexOfFirst { it.localId == pending.localId }
                    if (index >= 0) pendingFiles[index] = pending.copy(failed = true)
                }
                else -> {}
            }
        }
    }

    fun startCall(threadId: String, isVideo: Boolean) {
        outgoingCallStatus = null
        viewModelScope.launch {
            when (val result = repository.inviteToCall(threadId, isVideo)) {
                is Resource.Success -> {
                    val invite = result.data
                    if (invite != null) outgoingCallStatus = DoctorOutgoingCallStatus.Calling(invite.callId)
                }
                is Resource.Error -> {
                    snackbarController.show(result.message ?: "Failed to start call", isError = true)
                    outgoingCallStatus = null
                }
                else -> {}
            }
        }
    }

    fun cancelOutgoingCall(threadId: String, callId: String) {
        viewModelScope.launch { repository.cancelCall(threadId, callId) }
        outgoingCallStatus = null
    }

    fun clearOutgoingCallStatus() {
        outgoingCallStatus = null
    }

    fun declineIncomingCall(threadId: String) {
        val call = incomingCall ?: return
        incomingCall = null
        viewModelScope.launch { repository.declineCall(threadId, call.callId) }
    }

    fun clearIncomingCall() {
        incomingCall = null
    }

    fun joinCall(threadId: String) {
        if (callJoinState is Resource.Success) return
        viewModelScope.launch {
            callJoinState = Resource.Loading()
            callJoinState = repository.joinCall(threadId)
        }
    }

    fun endCall(threadId: String) {
        callJoinState = null
        viewModelScope.launch { repository.endCall(threadId) }
    }
}
