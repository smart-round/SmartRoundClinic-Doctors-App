package ke.co.smartroundclinic.doctor.data.remote.dto.request

import kotlinx.serialization.Serializable

@Serializable
data class InitiateDoctorChatReq(val otherDoctorId: String)

@Serializable
data class InviteToDoctorCallReq(val isVideo: Boolean = true)

@Serializable
data class DoctorCallActionReq(val callId: String)
