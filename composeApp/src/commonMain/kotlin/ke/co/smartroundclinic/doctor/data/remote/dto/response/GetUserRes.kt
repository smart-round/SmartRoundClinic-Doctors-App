package ke.co.smartroundclinic.doctor.data.remote.dto.response

import ke.co.smartroundclinic.doctor.domain.model.User
import kotlinx.serialization.Serializable

@Serializable
data class GetUserRes(
    val data: UserData,
    val httpStatusCode: Int, // 200
    val message: String, // User found
    val status: Boolean // true
)

@Serializable
data class UserData(
    val accountStatus: String, // INACTIVE
    val createdAt: String, // 2026-05-04T11:35:08.258221310Z
    val email: String, // pasakamutuku@outlook.com
    val fullName: String, // Mutuku kyalo
    val gender: String, // MALE
    val id: String, // 69f8846c319d59e154fdab3c
    val kraPin: String? = null,
    val role: String, // DOCTOR
    val verificationStatus: String, // PENDING_APPROVAL
    val profilePicture: String? = null,
    val dateOfBirth: String? = null,
    val phoneNumber: String? = null,
)

fun UserData.toDomain() = User(
    id = id,
    fullName = fullName,
    email = email,
    gender = gender,
    phoneNumber = phoneNumber,
    dateOfBirth = dateOfBirth,
    profilePicture = profilePicture,
    role = role,
    accountStatus = accountStatus,
    verificationStatus = verificationStatus,
    kraPin = kraPin,
    createdAt = createdAt,
)
