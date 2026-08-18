package ke.co.smartroundclinic.doctor.data.remote.dto.request

import kotlinx.serialization.Serializable

@Serializable
data class CreateReferralReq(
    val appointmentId: String,
    val receivingDoctorId: String,
    val reason: String,
)
