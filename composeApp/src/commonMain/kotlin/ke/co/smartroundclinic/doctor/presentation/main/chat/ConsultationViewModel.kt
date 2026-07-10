package ke.co.smartroundclinic.doctor.presentation.main.chat

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import io.ktor.client.HttpClient
import io.ktor.client.plugins.websocket.webSocket
import io.ktor.websocket.CloseReason
import io.ktor.websocket.DefaultWebSocketSession
import io.ktor.websocket.Frame
import io.ktor.websocket.close
import io.ktor.websocket.readText
import ke.co.smartroundclinic.doctor.common.Constants
import ke.co.smartroundclinic.doctor.common.Resource
import ke.co.smartroundclinic.doctor.core.snackbar.SnackbarController
import ke.co.smartroundclinic.doctor.data.remote.dto.response.ConsultationMessageData
import ke.co.smartroundclinic.doctor.data.remote.dto.response.ConsultationPresenceEventData
import ke.co.smartroundclinic.doctor.data.remote.dto.response.ConsultationReadReceiptEventData
import ke.co.smartroundclinic.doctor.data.remote.dto.response.ConsultationTypingEventData
import ke.co.smartroundclinic.doctor.data.remote.dto.response.ConsultationWsEventPeek
import ke.co.smartroundclinic.doctor.data.remote.dto.response.ConsultationWsOutgoing
import ke.co.smartroundclinic.doctor.data.remote.dto.response.toDomain
import ke.co.smartroundclinic.doctor.domain.model.Appointment
import ke.co.smartroundclinic.doctor.domain.model.ConsultationMessage
import ke.co.smartroundclinic.doctor.domain.model.ConsultationSession
import ke.co.smartroundclinic.doctor.domain.model.ConversationThread
import ke.co.smartroundclinic.doctor.domain.repository.AppointmentLocalRepository
import ke.co.smartroundclinic.doctor.domain.repository.UserLocalRepository
import ke.co.smartroundclinic.doctor.domain.usecase.consultation.DeleteConversationThreadUseCase
import ke.co.smartroundclinic.doctor.domain.usecase.consultation.GetMergedConsultationHistoryUseCase
import ke.co.smartroundclinic.doctor.domain.usecase.consultation.JoinConsultationCallUseCase
import ke.co.smartroundclinic.doctor.domain.usecase.consultation.ListConversationThreadsUseCase
import ke.co.smartroundclinic.doctor.domain.usecase.consultation.StartConsultationUseCase
import ke.co.smartroundclinic.doctor.domain.model.CallJoinInfo
import ke.co.smartroundclinic.doctor.domain.repository.ConsultationRepository
import ke.co.smartroundclinic.doctor.domain.usecase.scheduling.CompleteAppointmentUseCase
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

// Keep the initial/each older-page fetch small — the full history loads incrementally as the
// doctor scrolls up, rather than pulling an entire multi-consultation thread up front.
private const val HISTORY_PAGE_SIZE = 5

data class PendingFile(
    val localId: String,
    val fileName: String,
    val contentType: String,
    val bytes: ByteArray,
    val failed: Boolean = false,
) {
    override fun equals(other: Any?) = other is PendingFile && localId == other.localId
    override fun hashCode() = localId.hashCode()
}

class ConsultationViewModel(
    private val consultationRepository: ConsultationRepository,
    private val startConsultationUseCase: StartConsultationUseCase,
    private val joinCallUseCase: JoinConsultationCallUseCase,
    private val completeAppointmentUseCase: CompleteAppointmentUseCase,
    private val appointmentLocalRepository: AppointmentLocalRepository,
    private val userLocalRepository: UserLocalRepository,
    private val httpClient: HttpClient,
    private val snackbarController: SnackbarController,
    private val listConversationThreadsUseCase: ListConversationThreadsUseCase,
    private val getMergedHistoryUseCase: GetMergedConsultationHistoryUseCase,
    private val deleteConversationThreadUseCase: DeleteConversationThreadUseCase,
) : ViewModel() {

    var appointments by mutableStateOf<List<Appointment>>(emptyList())
        private set

    var threads by mutableStateOf<List<ConversationThread>>(emptyList())
        private set
    var isLoadingThreads by mutableStateOf(false)
        private set

    var currentUserId by mutableStateOf("")
        private set
    var currentUserProfilePicture by mutableStateOf<String?>(null)
        private set

    var activeSession by mutableStateOf<ConsultationSession?>(null)
        private set
    var isStartingSession by mutableStateOf(false)
        private set

    val messages = mutableStateListOf<ConsultationMessage>()
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

    // ─── Typing / presence / read-receipts for the currently open conversation ─────────────
    var otherPartyTyping by mutableStateOf(false)
        private set
    var otherPartyOnline by mutableStateOf(false)
        private set
    var otherPartyLastSeenAt by mutableStateOf<String?>(null)
        private set
    var otherPartyLastReadAt by mutableStateOf<String?>(null)
        private set
    var otherPartyLastDeliveredAt by mutableStateOf<String?>(null)
        private set
    private var typingClearJob: Job? = null
    private var lastTypingSentTrue = false

    // Derived from pendingFiles — true when any non-failed upload is in progress
    val isUploadingFile: Boolean get() = pendingFiles.any { !it.failed }

    // Call join state — set when the user enters CallScreen and clears when they leave
    var callJoinState by mutableStateOf<Resource<CallJoinInfo>?>(null)
        private set

    private var wsJob: Job? = null
    private var wsSession: DefaultWebSocketSession? = null

    init {
        loadCurrentUser()
        loadAppointments()
        loadThreads()
    }

    private fun loadCurrentUser() {
        viewModelScope.launch {
            userLocalRepository.observeUser().collect { user ->
                currentUserId = user?.id ?: ""
                currentUserProfilePicture = user?.profilePicture
            }
        }
    }

    fun loadAppointments() {
        viewModelScope.launch {
            appointmentLocalRepository.observeAppointments().collect { list ->
                appointments = list.filter {
                    it.status == ke.co.smartroundclinic.doctor.domain.model.AppointmentStatus.CONFIRMED ||
                    it.status == ke.co.smartroundclinic.doctor.domain.model.AppointmentStatus.COMPLETED
                }
            }
        }
    }

    fun startConsultation(appointmentId: String) {
        if (isStartingSession) return
        viewModelScope.launch {
            isStartingSession = true
            when (val result = startConsultationUseCase(appointmentId)) {
                is Resource.Success -> {
                    val session = result.data ?: return@launch
                    activeSession = session
                    connectWebSocket(session.id)
                }
                is Resource.Error -> snackbarController.show(result.message ?: "Failed to start consultation", isError = true)
                else -> {}
            }
            isStartingSession = false
        }
    }

    fun loadThreads() {
        viewModelScope.launch {
            isLoadingThreads = true
            when (val result = listConversationThreadsUseCase()) {
                is Resource.Success -> threads = result.data ?: emptyList()
                is Resource.Error -> snackbarController.show(result.message ?: "Failed to load conversations", isError = true)
                else -> {}
            }
            isLoadingThreads = false
        }
    }

    // Bumped on every loadMergedHistory call so a slow, superseded loadMoreHistory (or an old
    // loadMergedHistory itself) can detect it's stale and discard its result instead of mutating
    // `messages` out from under a newer load — this, plus the id dedup below, is what prevents the
    // duplicate-key LazyColumn crash under rapid scroll.
    private var historyGeneration = 0

    /** Loads the merged history for a doctor-patient pair — replaces whatever is currently shown. */
    fun loadMergedHistory(doctorId: String, patientId: String) {
        val generation = ++historyGeneration
        isLoadingHistory = true
        messages.clear()
        nextHistoryCursor = null
        hasMoreHistory = false
        // Seed presence from the already-fetched thread list — PRESENCE frames on the live socket
        // will keep it fresh from here.
        threads.firstOrNull { it.doctorId == doctorId && it.patientId == patientId }?.let {
            otherPartyOnline = it.isOnline
            otherPartyLastSeenAt = it.lastSeenAt
        }
        viewModelScope.launch {
            try {
                when (val result = getMergedHistoryUseCase(doctorId, patientId, size = HISTORY_PAGE_SIZE)) {
                    is Resource.Success -> {
                        if (generation != historyGeneration) return@launch
                        val page = result.data
                        // Backend returns newest-first (for cursor paging); render ascending, oldest at top.
                        messages.addAll(page?.items.orEmpty().asReversed())
                        nextHistoryCursor = page?.nextCursor
                        hasMoreHistory = page?.nextCursor != null
                        otherPartyLastReadAt = page?.counterpartLastReadAt
                        otherPartyLastDeliveredAt = page?.counterpartLastDeliveredAt
                    }
                    is Resource.Error -> snackbarController.show(result.message ?: "Failed to load conversation", isError = true)
                    else -> {}
                }
            } finally {
                if (generation == historyGeneration) isLoadingHistory = false
            }
        }
    }

    /** Loads the next (older) page of history and prepends it. No-op if there's nothing more or a load is already in flight. */
    fun loadMoreHistory(doctorId: String, patientId: String) {
        val cursor = nextHistoryCursor ?: return
        if (isLoadingMoreHistory) return
        // Set synchronously (not inside the coroutine) — otherwise a fast fling can fire this
        // multiple times before the first launch even starts, each fetching and inserting the
        // same page and producing a duplicate message id, which crashes the LazyColumn.
        isLoadingMoreHistory = true
        val generation = historyGeneration
        viewModelScope.launch {
            try {
                when (val result = getMergedHistoryUseCase(doctorId, patientId, before = cursor, size = HISTORY_PAGE_SIZE)) {
                    is Resource.Success -> {
                        if (generation == historyGeneration) {
                            val page = result.data
                            val existingIds = messages.mapTo(HashSet()) { it.id }
                            messages.addAll(0, page?.items.orEmpty().asReversed().filterNot { it.id in existingIds })
                            nextHistoryCursor = page?.nextCursor
                            hasMoreHistory = page?.nextCursor != null
                        }
                    }
                    is Resource.Error -> snackbarController.show(result.message ?: "Failed to load more messages", isError = true)
                    else -> {}
                }
            } finally {
                isLoadingMoreHistory = false
            }
        }
    }

    private fun connectWebSocket(sessionId: String) {
        wsJob?.cancel()
        isConnected = false
        wsJob = viewModelScope.launch(Dispatchers.IO) {
            val wsBase = Constants.BASE_URL
                .replace("https://", "wss://")
                .replace("http://", "ws://")
            var attempt = 0
            while (isActive) {
                try {
                    httpClient.webSocket("${wsBase}consultation/$sessionId/chat") {
                        wsSession = this
                        withContext(Dispatchers.Main) { isConnected = true }
                        attempt = 0

                        // Ping every 25 s; close the session on failure so the reconnect loop fires
                        launch {
                            while (isActive) {
                                delay(25_000L)
                                try {
                                    send(Frame.Ping(ByteArray(0)))
                                } catch (_: Exception) {
                                    try { close(CloseReason(CloseReason.Codes.GOING_AWAY, "")) } catch (_: Exception) {}
                                    break
                                }
                            }
                        }

                        for (frame in incoming) {
                            if (frame is Frame.Text) {
                                val raw = frame.readText()
                                try {
                                    when (wsJson.decodeFromString<ConsultationWsEventPeek>(raw).type) {
                                        "TYPING" -> {
                                            val event = wsJson.decodeFromString<ConsultationTypingEventData>(raw)
                                            withContext(Dispatchers.Main) { handleTypingEvent(event.isTyping) }
                                        }
                                        "PRESENCE" -> {
                                            val event = wsJson.decodeFromString<ConsultationPresenceEventData>(raw)
                                            withContext(Dispatchers.Main) {
                                                otherPartyOnline = event.isOnline
                                                otherPartyLastSeenAt = event.lastSeenAt
                                            }
                                        }
                                        "READ" -> {
                                            val event = wsJson.decodeFromString<ConsultationReadReceiptEventData>(raw)
                                            withContext(Dispatchers.Main) { otherPartyLastReadAt = event.lastReadAt }
                                        }
                                        else -> {
                                            val msg = wsJson.decodeFromString<ConsultationMessageData>(raw).toDomain()
                                            withContext(Dispatchers.Main) {
                                                if (messages.none { it.id == msg.id }) {
                                                    messages.add(msg)
                                                    // Remove matching pending once server echoes the upload back
                                                    if (msg.messageType.uppercase() == "FILE" && msg.senderId == currentUserId) {
                                                        pendingFiles.removeAll { p ->
                                                            msg.files.any { it.fileName == p.fileName }
                                                        }
                                                    }
                                                }
                                            }
                                        }
                                    }
                                } catch (_: Exception) {}
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

                // Exponential back-off: 1 s, 2 s, 4 s … up to 30 s
                if (isActive) {
                    attempt++
                    delay(minOf(1_000L shl minOf(attempt - 1, 5), 30_000L))
                }
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
                session.send(Frame.Text(wsJson.encodeToString(ConsultationWsOutgoing(type = "TEXT", message = text))))
            } catch (_: Exception) {
                snackbarController.show("Failed to send message", isError = true)
            }
        }
    }

    // Auto-clears after a few seconds in case the counterpart's client never sends the
    // "stopped typing" (isTyping=false) event — e.g. they background the app mid-type.
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
        val session = wsSession ?: return
        if (isTyping && lastTypingSentTrue) return
        lastTypingSentTrue = isTyping
        viewModelScope.launch(Dispatchers.IO) {
            try {
                session.send(Frame.Text(wsJson.encodeToString(ConsultationWsOutgoing(type = "TYPING", isTyping = isTyping))))
            } catch (_: Exception) {}
        }
    }

    fun deleteThread(doctorId: String, patientId: String) {
        viewModelScope.launch {
            when (val result = deleteConversationThreadUseCase(doctorId, patientId)) {
                is Resource.Success -> threads = threads.filterNot { it.doctorId == doctorId && it.patientId == patientId }
                is Resource.Error -> snackbarController.show(result.message ?: "Failed to delete conversation", isError = true)
                else -> {}
            }
        }
    }

    fun sendFile(fileName: String, contentType: String, bytes: ByteArray) {
        val sessionId = activeSession?.id
        if (sessionId == null) {
            snackbarController.show("No active session", isError = true)
            return
        }
        val pending = PendingFile(
            localId = "p${kotlin.random.Random.nextInt()}",
            fileName = fileName,
            contentType = contentType,
            bytes = bytes,
        )
        pendingFiles.add(pending)

        viewModelScope.launch {
            when (val result = consultationRepository.uploadFile(sessionId, fileName, contentType, bytes)) {
                is Resource.Success -> {
                    // The WebSocket change-stream broadcast will deliver the message
                    // and remove the pending entry. If the broadcast somehow misses,
                    // append the response message directly so the user still sees it.
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

    fun joinCall(sessionId: String) {
        if (callJoinState is Resource.Success) return
        viewModelScope.launch {
            callJoinState = Resource.Loading()
            callJoinState = joinCallUseCase(sessionId)
        }
    }

    fun endCall() {
        val sessionId = activeSession?.id ?: run { clearCallState(); return }
        val appointmentId = activeSession?.appointmentId ?: ""
        viewModelScope.launch {
            consultationRepository.endCall(sessionId)
            if (appointmentId.isNotEmpty()) {
                completeAppointmentUseCase(appointmentId)
            }
            clearCallState()
        }
    }

    fun clearCallState() {
        callJoinState = null
    }

    private fun markFailed(pending: PendingFile) {
        val idx = pendingFiles.indexOfFirst { it.localId == pending.localId }
        if (idx >= 0) pendingFiles[idx] = pending.copy(failed = true)
    }

    fun endConsultation() {
        val sessionId = activeSession?.id ?: return
        viewModelScope.launch {
            consultationRepository.endConsultation(sessionId)
            closeSession()
        }
    }

    fun closeSession() {
        wsJob?.cancel()
        typingClearJob?.cancel()
        wsSession = null
        isConnected = false
        activeSession = null
        messages.clear()
        pendingFiles.clear()
        nextHistoryCursor = null
        hasMoreHistory = false
        otherPartyTyping = false
        otherPartyOnline = false
        otherPartyLastSeenAt = null
        otherPartyLastReadAt = null
        otherPartyLastDeliveredAt = null
        lastTypingSentTrue = false
        loadThreads() // refresh list previews now that this thread may have new messages
    }

    override fun onCleared() {
        super.onCleared()
        wsJob?.cancel()
    }
}
