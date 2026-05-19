package ke.co.smartroundclinic.doctor.data.remote.dto.response

import ke.co.smartroundclinic.doctor.domain.model.Licence
import kotlinx.serialization.Serializable

@Serializable
data class UploadLicenceRes(
    val `data`: UploadLicenceData,
    val httpStatusCode: Int, // 200
    val message: String, // Licence added successfully
    val status: Boolean // true
)

@Serializable
data class UploadLicenceData(
    val createdAt: String, // 2026-05-19T18:21:06.835342Z
    val doctorId: String, // 6a08a70a8751f92f85e39bae
    val id: String, // 6a0caa1247db65a24c3b0454
    val licenceName: String, // General Practitioner
    val licenceUrl: String,
)

fun UploadLicenceData.toDomain() = Licence(
    id = id,
    licenceName = licenceName,
    licenceUrl = licenceUrl,
    createdAt = createdAt,
)