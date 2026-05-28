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
import ke.co.smartroundclinic.doctor.data.remote.dto.response.ConsultationWsOutgoing
import ke.co.smartroundclinic.doctor.data.remote.dto.response.toDomain
import ke.co.smartroundclinic.doctor.domain.model.Appointment
import ke.co.smartroundclinic.doctor.domain.model.ConsultationMessage
import ke.co.smartroundclinic.doctor.domain.model.ConsultationSession
import ke.co.smartroundclinic.doctor.domain.repository.AppointmentLocalRepository
import ke.co.smartroundclinic.doctor.domain.repository.UserLocalRepository
import ke.co.smartroundclinic.doctor.domain.usecase.consultation.GetConsultationMessagesUseCase
import ke.co.smartroundclinic.doctor.domain.usecase.consultation.JoinConsultationCallUseCase
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
    private val getMessagesUseCase: GetConsultationMessagesUseCase,
    private val joinCallUseCase: JoinConsultationCallUseCase,
    private val completeAppointmentUseCase: CompleteAppointmentUseCase,
    private val appointmentLocalRepository: AppointmentLocalRepository,
    private val userLocalRepository: UserLocalRepository,
    private val httpClient: HttpClient,
    private val snackbarController: SnackbarController,
) : ViewModel() {

    var appointments by mutableStateOf<List<Appointment>>(emptyList())
        private set

    var currentUserId by mutableStateOf("")
        private set

    var activeSession by mutableStateOf<ConsultationSession?>(null)
        private set
    var isStartingSession by mutableStateOf(false)
        private set

    val messages = mutableStateListOf<ConsultationMessage>()
    val pendingFiles = mutableStateListOf<PendingFile>()
    var isConnected by mutableStateOf(false)
        private set

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
    }

    private fun loadCurrentUser() {
        viewModelScope.launch {
            userLocalRepository.observeUser().collect { user ->
                currentUserId = user?.id ?: ""
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
                    loadHistory(session.id)
                    connectWebSocket(session.id)
                }
                is Resource.Error -> snackbarController.show(result.message ?: "Failed to start consultation", isError = true)
                else -> {}
            }
            isStartingSession = false
        }
    }

    private suspend fun loadHistory(sessionId: String) {
        when (val result = getMessagesUseCase(sessionId)) {
            is Resource.Success -> {
                messages.clear()
                messages.addAll(result.data ?: emptyList())
            }
            else -> {}
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
                                try {
                                    val dto = wsJson.decodeFromString<ConsultationMessageData>(frame.readText())
                                    val msg = dto.toDomain()
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
        viewModelScope.launch(Dispatchers.IO) {
            try {
                wsSession?.send(Frame.Text(wsJson.encodeToString(ConsultationWsOutgoing(type = "TEXT", message = text))))
            } catch (_: Exception) {
                snackbarController.show("Failed to send message", isError = true)
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
        wsSession = null
        isConnected = false
        activeSession = null
        messages.clear()
        pendingFiles.clear()
    }

    override fun onCleared() {
        super.onCleared()
        wsJob?.cancel()
    }
}
