package ke.co.smartroundclinic.doctor.data.remote.dto.request

import kotlinx.serialization.Serializable

@Serializable
data class UpdateDoctorProfileReq(
    val kmpdcRegNumber: String? = null,
    val title: String? = null,
    val bio: String? = null,
    val yearsOfExperience: Int? = null,
    val languages: List<String>? = null,
    val facilityName: String? = null,
)
