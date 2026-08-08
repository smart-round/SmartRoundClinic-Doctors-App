package ke.co.smartroundclinic.doctor.presentation.main.chat.otherdoctors

import kotlinx.io.RawSource
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
import ke.co.smartroundclinic.doctor.core.notification.ActiveCallNotifier
import ke.co.smartroundclinic.doctor.core.notification.IncomingCallHandler
import ke.co.smartroundclinic.doctor.core.notification.OutgoingDoctorCallState
import ke.co.smartroundclinic.doctor.core.snackbar.SnackbarController
import ke.co.smartroundclinic.doctor.data.remote.dto.response.DoctorCallAnsweredEventData
import ke.co.smartroundclinic.doctor.data.remote.dto.response.DoctorCallCancelledEventData
import ke.co.smartroundclinic.doctor.data.remote.dto.response.DoctorCallDeclinedEventData
import ke.co.smartroundclinic.doctor.data.remote.dto.response.DoctorCallInviteEventData
import ke.co.smartroundclinic.doctor.data.remote.dto.response.DoctorChatMessageData
import ke.co.smartroundclinic.doctor.data.remote.dto.response.DoctorChatWsEventPeek
import ke.co.smartroundclinic.doctor.data.remote.dto.response.DoctorChatWsOutgoing
import ke.co.smartroundclinic.doctor.data.remote.dto.response.DoctorPresenceEventData
import ke.co.smartroundclinic.doctor.data.remote.dto.response.DoctorTypingEventData
import ke.co.smartroundclinic.doctor.data.remote.dto.response.toDomain
import ke.co.smartroundclinic.doctor.domain.model.CallJoinInfo
import ke.co.smartroundclinic.doctor.domain.model.Doctor
import ke.co.smartroundclinic.doctor.domain.model.DoctorChatMessage
import ke.co.smartroundclinic.doctor.domain.model.DoctorChatThread
import ke.co.smartroundclinic.doctor.domain.repository.DoctorChatRepository
import ke.co.smartroundclinic.doctor.domain.repository.UserLocalRepository
import ke.co.smartroundclinic.doctor.domain.usecase.directory.GetDoctorByIdUseCase
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
private const val DOCTORS_PAGE_SIZE = 20

/**
 * Mirrors [ke.co.smartroundclinic.doctor.presentation.main.chat.ConsultationViewModel]'s
 * message-send/receive/typing/presence/calling slice. Calling now goes through the same
 * IncomingCallHandler / IncomingDoctorCallState / OutgoingDoctorCallState singletons the
 * push-notification path uses (see IncomingCallHandler.onDoctorCallInvite and friends) —
 * whichever channel (this WS connection, or a push while backgrounded/killed) the signal
 * arrives on, it ends up in the same place, and the native full-screen/CallKit ringing UI
 * fires either way, exactly like patient calls.
 */
class DoctorChatViewModel(
    private val repository: DoctorChatRepository,
    private val initiateDoctorChatUseCase: InitiateDoctorChatUseCase,
    private val getRecommendedDoctorsUseCase: GetRecommendedDoctorsUseCase,
    private val getDoctorByIdUseCase: GetDoctorByIdUseCase,
    private val userLocalRepository: UserLocalRepository,
    private val httpClient: HttpClient,
    private val snackbarController: SnackbarController,
) : ViewModel() {

    // Existing conversations, newest-message-first (server-sorted) — rendered above the directory
    // in DoctorDirectoryScreen so recent chats always surface at the top, with online status and
    // last-message timestamp, mirroring the Consultations tab's thread list.
    var threads by mutableStateOf<List<DoctorChatThread>>(emptyList())
        private set
    var isLoadingThreads by mutableStateOf(false)
        private set

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

    // Full profile for the "view profile" action off a doctor-chat thread's info sheet — reused
    // from Services' DoctorProfileScreen, which needs a full Doctor (bio, specialization, rating,
    // etc.) that the thread/info-sheet only carry a name+picture slice of.
    var viewedDoctorProfile by mutableStateOf<Doctor?>(null)
        private set
    var isLoadingViewedDoctorProfile by mutableStateOf(false)
        private set

    fun loadDoctorProfile(doctorId: String) {
        viewModelScope.launch {
            isLoadingViewedDoctorProfile = true
            when (val result = getDoctorByIdUseCase(doctorId)) {
                is Resource.Success -> viewedDoctorProfile = result.data
                is Resource.Error -> snackbarController.show(result.message ?: "Failed to load doctor profile", isError = true)
                else -> {}
            }
            isLoadingViewedDoctorProfile = false
        }
    }

    fun clearViewedDoctorProfile() {
        viewedDoctorProfile = null
    }

    private var threadsPollJob: Job? = null

    // Mirrors ConsultationViewModel's startThreadsPolling/stopThreadsPolling — the Other Doctors
    // list has no socket of its own to keep online dots/last-seen live, so this is what notices a
    // doctor going offline (or coming back online) while the doctor is looking at that list.
    fun startThreadsPolling() {
        if (threadsPollJob?.isActive == true) return
        threadsPollJob = viewModelScope.launch {
            while (isActive) {
                delay(10_000L)
                when (val result = repository.listThreads()) {
                    is Resource.Success -> threads = result.data ?: threads
                    else -> {}
                }
            }
        }
    }

    fun stopThreadsPolling() {
        threadsPollJob?.cancel()
        threadsPollJob = null
    }

    // The tab's secondary content: a paginated, always-populated directory of verified doctors (not
    // gated behind search — search only filters what's already loaded, see onSearchQueryChange
    // usage in DoctorDirectoryScreen). Fetched immediately on open and infinite-scrolled.
    var doctors by mutableStateOf<List<Doctor>>(emptyList())
        private set
    var isLoadingDoctors by mutableStateOf(false)
        private set
    var isLoadingMoreDoctors by mutableStateOf(false)
        private set
    var hasMoreDoctors by mutableStateOf(false)
        private set
    private var doctorsPage = 0
    private var doctorsTotalPages = 1

    fun loadDoctors() {
        if (doctors.isNotEmpty() || isLoadingDoctors) return
        viewModelScope.launch {
            isLoadingDoctors = true
            when (val result = getRecommendedDoctorsUseCase(null, 1, DOCTORS_PAGE_SIZE, currentUserId.ifBlank { null })) {
                is Resource.Success -> {
                    val page = result.data
                    // Defensive filter in addition to the backend's excludeDoctorId param — this
                    // call can fire before currentUserId finishes loading from Room (see init{}),
                    // in which case excludeDoctorId is sent as null and the backend can't exclude
                    // us; filtering here guarantees self never shows regardless of that race.
                    doctors = page?.doctors.orEmpty().filterNot { it.id == currentUserId }
                    doctorsPage = page?.page ?: 1
                    doctorsTotalPages = page?.totalPages ?: 1
                    hasMoreDoctors = doctorsPage < doctorsTotalPages
                }
                is Resource.Error -> snackbarController.show(result.message ?: "Failed to load doctors", isError = true)
                else -> {}
            }
            isLoadingDoctors = false
        }
    }

    fun loadMoreDoctors() {
        if (!hasMoreDoctors || isLoadingMoreDoctors || isLoadingDoctors) return
        viewModelScope.launch {
            isLoadingMoreDoctors = true
            when (val result = getRecommendedDoctorsUseCase(null, doctorsPage + 1, DOCTORS_PAGE_SIZE, currentUserId.ifBlank { null })) {
                is Resource.Success -> {
                    val page = result.data
                    val existingIds = doctors.map { it.id }.toSet()
                    doctors = doctors + page?.doctors.orEmpty().filterNot { it.id in existingIds || it.id == currentUserId }
                    doctorsPage = page?.page ?: doctorsPage
                    doctorsTotalPages = page?.totalPages ?: doctorsTotalPages
                    hasMoreDoctors = doctorsPage < doctorsTotalPages
                }
                is Resource.Error -> snackbarController.show(result.message ?: "Failed to load more doctors", isError = true)
                else -> {}
            }
            isLoadingMoreDoctors = false
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

    var otherPartyTyping by mutableStateOf(false)
        private set
    var otherPartyOnline by mutableStateOf(false)
        private set
    var otherPartyLastSeenAt by mutableStateOf<String?>(null)
        private set
    private var typingClearJob: Job? = null
    private var lastTypingSentTrue = false

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
        loadDoctors()
    }

    fun loadHistory(threadId: String) {
        // Seed presence from the already-fetched thread list — PRESENCE frames on the live socket
        // will keep it fresh from here.
        threads.firstOrNull { it.threadId == threadId }?.let {
            otherPartyOnline = it.isOnline
            otherPartyLastSeenAt = it.lastSeenAt
        }
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
                                        "TYPING" -> {
                                            val event = wsJson.decodeFromString<DoctorTypingEventData>(raw)
                                            withContext(Dispatchers.Main) { handleTypingEvent(event.isTyping) }
                                        }
                                        "PRESENCE" -> {
                                            val event = wsJson.decodeFromString<DoctorPresenceEventData>(raw)
                                            withContext(Dispatchers.Main) {
                                                otherPartyOnline = event.isOnline
                                                otherPartyLastSeenAt = event.lastSeenAt
                                            }
                                        }
                                        "CALL_INVITE" -> {
                                            val event = wsJson.decodeFromString<DoctorCallInviteEventData>(raw)
                                            withContext(Dispatchers.Main) {
                                                IncomingCallHandler.onDoctorCallInvite(
                                                    callId = event.callId,
                                                    callerId = event.callerId,
                                                    callerName = event.callerName,
                                                    callerPicture = event.callerPicture,
                                                    threadId = threadId,
                                                    isVideo = event.isVideo,
                                                    ringTimeoutSeconds = event.ringTimeoutSeconds,
                                                )
                                            }
                                        }
                                        "CALL_ANSWERED" -> {
                                            val event = wsJson.decodeFromString<DoctorCallAnsweredEventData>(raw)
                                            withContext(Dispatchers.Main) { IncomingCallHandler.onDoctorCallAnswered(event.callId) }
                                        }
                                        "CALL_DECLINED" -> {
                                            val event = wsJson.decodeFromString<DoctorCallDeclinedEventData>(raw)
                                            withContext(Dispatchers.Main) { IncomingCallHandler.onDoctorCallDeclined(event.callId) }
                                        }
                                        "CALL_CANCELLED" -> {
                                            val event = wsJson.decodeFromString<DoctorCallCancelledEventData>(raw)
                                            withContext(Dispatchers.Main) { IncomingCallHandler.onDoctorCallCancelled(event.callId) }
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
        typingClearJob?.cancel()
        otherPartyTyping = false
        otherPartyOnline = false
        otherPartyLastSeenAt = null
        lastTypingSentTrue = false
    }

    private fun handleTypingEvent(isTyping: Boolean) {
        typingClearJob?.cancel()
        otherPartyTyping = isTyping
        if (isTyping) {
            typingClearJob = viewModelScope.launch {
                delay(6_000L)
                otherPartyTyping = false
            }
        }
    }

    /** Debounced — only sends isTyping=true once per burst of typing; isTyping=false always sends immediately. */
    fun sendTypingEvent(isTyping: Boolean) {
        val session = wsSession
        if (session == null) {
            Napier.w(tag = "DoctorChatTyping", message = "sendTypingEvent(isTyping=$isTyping) dropped — no open wsSession (isConnected=$isConnected)")
            return
        }
        if (isTyping && lastTypingSentTrue) return
        lastTypingSentTrue = isTyping
        viewModelScope.launch(Dispatchers.IO) {
            try {
                session.send(Frame.Text(wsJson.encodeToString(DoctorChatWsOutgoing(type = "TYPING", isTyping = isTyping))))
            } catch (e: Exception) {
                Napier.w(tag = "DoctorChatTyping", message = "Failed to send TYPING isTyping=$isTyping: ${e.message}")
            }
        }
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

    /**
     * Queues an attachment. [openSource] is a factory rather than bytes so the upload can stream
     * the file straight to storage without ever holding it in memory.
     */
    fun sendFile(
        threadId: String,
        fileName: String,
        contentType: String,
        sizeBytes: Long,
        previewBytes: ByteArray?,
        openSource: () -> RawSource,
    ) {
        val pending = PendingFile(
            localId = "p${kotlin.random.Random.nextInt()}",
            fileName = fileName,
            contentType = contentType,
            previewBytes = previewBytes,
            totalBytes = sizeBytes,
        )
        pendingFiles.add(pending)

        // Backstop for callers that didn't check the size first.
        if (sizeBytes > Constants.MAX_CHAT_FILE_BYTES) {
            markFailed(pending, Constants.FILE_TOO_LARGE_MESSAGE)
            return
        }

        viewModelScope.launch {
            val result = repository.uploadFile(
                threadId = threadId,
                fileName = fileName,
                contentType = contentType,
                sizeBytes = sizeBytes,
                openSource = openSource,
                onProgress = { sent, total -> updateProgress(pending.localId, sent, total) },
            )
            when (result) {
                is Resource.Success -> {
                    val msg = result.data
                    if (msg != null && messages.none { it.id == msg.id }) {
                        messages.add(msg)
                        pendingFiles.removeAll { it.localId == pending.localId }
                    }
                }
                is Resource.Error -> {
                    snackbarController.show(result.message ?: "Failed to send file", isError = true)
                    markFailed(pending)
                }
                else -> {}
            }
        }
    }

    private fun updateProgress(localId: String, sent: Long, total: Long) {
        val idx = pendingFiles.indexOfFirst { it.localId == localId }
        if (idx >= 0) pendingFiles[idx] = pendingFiles[idx].copy(sentBytes = sent, totalBytes = total)
    }

    private fun markFailed(pending: PendingFile, errorText: String? = null) {
        val idx = pendingFiles.indexOfFirst { it.localId == pending.localId }
        if (idx >= 0) pendingFiles[idx] = pending.copy(failed = true, errorText = errorText)
    }

    /** Shows a failed attachment for a file rejected on size before it was ever read. */
    fun rejectOversizedFile(fileName: String, contentType: String) {
        pendingFiles.add(
            PendingFile(
                localId = "p${kotlin.random.Random.nextInt()}",
                fileName = fileName,
                contentType = contentType,
                failed = true,
                errorText = Constants.FILE_TOO_LARGE_MESSAGE,
            ),
        )
    }

    /** Shows a failed attachment for a file we could not read at all (revoked URI, etc). */
    fun rejectUnreadableFile(fileName: String, contentType: String) {
        pendingFiles.add(
            PendingFile(
                localId = "p${kotlin.random.Random.nextInt()}",
                fileName = fileName,
                contentType = contentType,
                failed = true,
                errorText = "Couldn't read this file. Please try again.",
            ),
        )
    }

    fun startCall(threadId: String, isVideo: Boolean, calleeName: String?) {
        OutgoingDoctorCallState.clear()
        viewModelScope.launch {
            when (val result = repository.inviteToCall(threadId, isVideo)) {
                is Resource.Success -> {
                    val invite = result.data
                    if (invite != null) OutgoingDoctorCallState.calling(invite.callId, threadId, calleeName, isVideo)
                }
                is Resource.Error -> {
                    snackbarController.show(result.message ?: "Failed to start call", isError = true)
                    OutgoingDoctorCallState.clear()
                }
                else -> {}
            }
        }
    }

    fun cancelOutgoingCall(threadId: String, callId: String) {
        OutgoingDoctorCallState.clear()
        viewModelScope.launch { repository.cancelCall(threadId, callId) }
    }

    fun joinCall(threadId: String) {
        if (callJoinState is Resource.Success) return
        viewModelScope.launch {
            callJoinState = Resource.Loading()
            callJoinState = repository.joinCall(threadId)
        }
    }

    fun endCall(threadId: String) {
        // Tells iOS's CallKit the call is actually over — without this, doctor-to-doctor calls
        // left CallKit's system call UI (status bar pill / Dynamic Island / lock screen banner)
        // thinking a call was still active after the in-app screen was already gone, and could
        // block CallKit from reporting the doctor's next call at all (single CXProvider, max 1
        // call). See ConsultationViewModel.endCall(), which already does this for patient calls.
        ActiveCallNotifier.notifyCallEnded()
        callJoinState = null
        viewModelScope.launch { repository.endCall(threadId) }
    }
}
