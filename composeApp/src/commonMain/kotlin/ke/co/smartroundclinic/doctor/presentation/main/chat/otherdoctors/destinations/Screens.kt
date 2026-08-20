package ke.co.smartroundclinic.doctor.presentation.main.chat.otherdoctors.destinations

import androidx.navigation3.runtime.NavKey
import kotlinx.serialization.Serializable

@Serializable data object OtherDoctorsList : NavKey
@Serializable data class DoctorConversation(val threadId: String, val counterpartName: String, val counterpartPicture: String?) : NavKey
@Serializable data class DoctorProfileView(val doctorId: String, val name: String, val picture: String?) : NavKey
// callId identifies the invite this join is answering/completing — required by the backend's
// atomic-join gate (POST .../call/join now rejects a join with no live invite behind it).
@Serializable data class DoctorCall(val threadId: String, val isVideo: Boolean, val callId: String) : NavKey
@Serializable data class OutgoingDoctorCall(val threadId: String, val calleeName: String, val isVideo: Boolean, val calleePicture: String? = null) : NavKey
