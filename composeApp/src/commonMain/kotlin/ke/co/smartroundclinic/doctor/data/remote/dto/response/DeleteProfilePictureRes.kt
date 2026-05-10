package ke.co.smartroundclinic.doctor.data.remote.dto.response


import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class DeleteProfilePictureRes(
    val `data`: DeleteProfilePictureData,
    val httpStatusCode: Int, // 200
    val message: String, // Profile picture removed successfully
    val status: Boolean // true
)

@Serializable
data class DeleteProfilePictureData(
    val accountStatus: String, // INACTIVE
    val createdAt: String, // 2026-05-04T11:35:08.258221310Z
    val email: String, // pasakamutuku@outlook.com
    val fullName: String, // Mutuku kyalo
    val gender: String, // MALE
    val id: String, // 69f8846c319d59e154fdab3c
    val kraPin: String, // A1234589h
    val role: String, // DOCTOR
    val verificationStatus: String // PENDING_APPROVAL
)