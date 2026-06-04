package ke.co.smartroundclinic.doctor.core.database.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import ke.co.smartroundclinic.doctor.domain.model.User

@Entity(tableName = "users")
data class UserEntity(
    @PrimaryKey val id: String,
    val fullName: String,
    val email: String,
    val gender: String,
    val phoneNumber: String?,
    val dateOfBirth: String?,
    val profilePicture: String?,
    val role: String,
    val accountStatus: String,
    val verificationStatus: String,
    val kraPin: String?,
    val createdAt: String,
)

fun UserEntity.toDomain() = User(
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

fun User.toEntity() = UserEntity(
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
