package ke.co.smartroundclinic.doctor.presentation.main.chat.destinations

import androidx.navigation3.runtime.NavKey
import kotlinx.serialization.Serializable

@Serializable data object ChatList : NavKey

// doctorId is always the currently-signed-in doctor (this is the doctor app), so it isn't carried
// here — patientId identifies the permanent thread. latestAppointmentId is only used to enrich the
// header (fallback profile picture) from the appointments list.
@Serializable data class Conversation(val patientId: String, val patientName: String, val latestAppointmentId: String) : NavKey
@Serializable data class Call(val otherUserId: String, val isVideo: Boolean) : NavKey
