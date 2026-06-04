package ke.co.smartroundclinic.doctor.data.remote.dto.response


import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class UploadProfilePictureRes(
    val `data`: UploadProfilePictureData,
    val httpStatusCode: Int, // 200
    val message: String, // Profile picture updated successfully
    val status: Boolean // true
)

@Serializable
data class UploadProfilePictureData(
    val accountStatus: String, // INACTIVE
    val createdAt: String, // 2026-05-04T11:35:08.258221310Z
    val email: String, // pasakamutuku@outlook.com
    val fullName: String, // Mutuku kyalo
    val gender: String, // MALE
    val id: String, // 69f8846c319d59e154fdab3c
    val kraPin: String? = null,
    val profilePicture: String, // https://868c9d8e015c6365c5f70beed2b85140.r2.cloudflarestorage.com/smartroundclinic-private/profile-pictures/69f8846c319d59e154fdab3c.jpeg?x-id=GetObject&X-Amz-Algorithm=AWS4-HMAC-SHA256&X-Amz-Credential=ecf53ae43fb6944a55fda7f081e3b1c1%2F20260510%2Fauto%2Fs3%2Faws4_request&X-Amz-Date=20260510T084930Z&X-Amz-Expires=86400&X-Amz-SignedHeaders=host&X-Amz-Signature=716754266f26692d7657831ce89ca188376c1e426208158a2919139355564ad8
    val role: String, // DOCTOR
    val verificationStatus: String // PENDING_APPROVAL
)