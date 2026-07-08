package ke.co.smartroundclinic.doctor.data.remote.dto.request

import kotlinx.serialization.Serializable

@Serializable
data class SubmitPatientRatingReq(
    val appointmentId: String,
    val patientId: String,
    val rating: Int,
    val comment: String? = null,
)

@Serializable
data class UpdateRatingReq(
    val rating: Int? = null,
    val comment: String? = null,
)
