package ke.co.smartroundclinic.doctor.presentation.main.chat.otherdoctors.destinations

import androidx.navigation3.runtime.NavKey
import kotlinx.serialization.Serializable

@Serializable data object OtherDoctorsList : NavKey
@Serializable data class DoctorConversation(val threadId: String, val counterpartName: String, val counterpartPicture: String?) : NavKey
@Serializable data class DoctorCall(val threadId: String, val isVideo: Boolean) : NavKey
@Serializable data class OutgoingDoctorCall(val threadId: String, val calleeName: String, val isVideo: Boolean) : NavKey
