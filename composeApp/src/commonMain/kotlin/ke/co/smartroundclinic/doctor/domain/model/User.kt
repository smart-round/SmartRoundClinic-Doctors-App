package ke.co.smartroundclinic.doctor.domain.model

data class User(
    val id: String,
    val fullName: String,
    val email: String,
    val gender: String,
    val phoneNumber: String?,
    val dateOfBirth: String?,
    val profilePicture: String?,
    val role: String,
    val accountStatus: String,
    val verificationStatus: String,
    val kraPin: String,
    val createdAt: String,
)
