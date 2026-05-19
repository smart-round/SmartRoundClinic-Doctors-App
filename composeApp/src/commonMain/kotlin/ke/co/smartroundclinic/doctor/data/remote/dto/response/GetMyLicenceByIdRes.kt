package ke.co.smartroundclinic.doctor.data.remote.dto.response


import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class GetMyLicenceByIdRes(
    val `data`: GetMyLicenceData,
    val httpStatusCode: Int, // 200
    val message: String,
    val status: Boolean // false
)

@Serializable
data class GetMyLicenceData(
    val createdAt: String, // 2026-05-16T17:19:06.364815626Z
    val doctorId: String, // 6a08a70a8751f92f85e39bae
    val id: String, // 6a08a70a8751f92f85e39bb4
    val licenceName: String, // Orthopedics
    val licenceUrl: String // https://868c9d8e015c6365c5f70beed2b85140.r2.cloudflarestorage.com/smartroundclinic-private/practitioner-licence/6a08a70a8751f92f85e39bb4.pdf?x-id=GetObject&X-Amz-Algorithm=AWS4-HMAC-SHA256&X-Amz-Credential=ecf53ae43fb6944a55fda7f081e3b1c1%2F20260519%2Fauto%2Fs3%2Faws4_request&X-Amz-Date=20260519T182534Z&X-Amz-Expires=86400&X-Amz-SignedHeaders=host&X-Amz-Signature=bc5b337cae6092b2b262570a3219dbf206a759c6624f96ee3c4fae9e6cc4e12e
)