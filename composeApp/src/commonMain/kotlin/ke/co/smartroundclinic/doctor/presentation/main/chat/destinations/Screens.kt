package ke.co.smartroundclinic.doctor.presentation.main.chat.destinations

import androidx.navigation3.runtime.NavKey
import kotlinx.serialization.Serializable

@Serializable data object ChatList : NavKey

// doctorId is always the currently-signed-in doctor (this is the doctor app), so it isn't carried
// here — patientId identifies the permanent thread. latestAppointmentId is only used to enrich the
// header (fallback profile picture) from the appointments list.
@Serializable data class Conversation(val patientId: String, val patientName: String, val latestAppointmentId: String) : NavKey
// callId identifies the invite this join is answering/completing — required by the backend's
// atomic-join gate (POST .../call/join now rejects a join with no live invite behind it).
@Serializable data class Call(val otherUserId: String, val isVideo: Boolean, val callId: String) : NavKey

// Ringing screen shown to the caller between InviteToCallUseCase and the callee answering —
// see OutgoingCallState. otherUserId doubles as the patientId (doctor app is doctor-only).
@Serializable data class OutgoingCall(val otherUserId: String, val calleeName: String, val isVideo: Boolean, val calleePicture: String? = null) : NavKey
