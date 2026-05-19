package ke.co.smartroundclinic.doctor.data.remote.dto.response

import ke.co.smartroundclinic.doctor.domain.model.Licence
import kotlinx.serialization.Serializable

@Serializable
data class GetAllMyLicencesRes(
    val `data`: List<GetAllMyLicenceData>,
    val httpStatusCode: Int, // 200
    val message: String, // Success
    val status: Boolean // true
)

@Serializable
data class GetAllMyLicenceData(
    val createdAt: String, // 2026-05-16T17:19:06.364815626Z
    val doctorId: String, // 6a08a70a8751f92f85e39bae
    val id: String, // 6a08a70a8751f92f85e39bb4
    val licenceName: String, // Orthopedics
    val licenceUrl: String,
)

fun GetAllMyLicenceData.toDomain() = Licence(
    id = id,
    licenceName = licenceName,
    licenceUrl = licenceUrl,
    createdAt = createdAt,
)