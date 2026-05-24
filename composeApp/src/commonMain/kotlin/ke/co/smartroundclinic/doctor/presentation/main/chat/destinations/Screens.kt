package ke.co.smartroundclinic.doctor.presentation.main.chat.destinations

import androidx.navigation3.runtime.NavKey
import kotlinx.serialization.Serializable

@Serializable data object ChatList : NavKey
@Serializable data class Conversation(val appointmentId: String, val patientName: String) : NavKey
@Serializable data class Call(val sessionId: String, val isVideo: Boolean) : NavKey
