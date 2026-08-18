package ke.co.smartroundclinic.doctor.data.remote.dto.request

import kotlinx.serialization.Serializable

@Serializable
data class CreateReferralReq(
    val appointmentId: String,
    val receivingDoctorId: String,
    // Referring goes straight to the doctor picker — no free-text reason is collected any more.
    val reason: String = "",
)
