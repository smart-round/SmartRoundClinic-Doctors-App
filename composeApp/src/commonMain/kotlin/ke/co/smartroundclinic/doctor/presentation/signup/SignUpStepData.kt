package ke.co.smartroundclinic.doctor.presentation.signup

import kotlinx.serialization.Serializable

// Only primitive/string fields — no ByteArray. ByteArrays live in SignUpFilesViewModel
// to avoid TransactionTooLargeException when Navigation3 serializes the back stack.

@Serializable
data class PersonalInfoData(
    val fullName: String,
    val gender: String,
    val dateOfBirth: String,
    val email: String,
    val phoneNumber: String,
    val kraPin: String,
    val password: String,
)

@Serializable
data class SpecializationData(
    val specializationId: String,
    val specializationName: String,
)

@Serializable
data class BioData(
    val kmpdcRegNumber: String,
    val title: String,
    val bio: String,
    val yearsOfExperience: String,
    val languages: String,
    val facilityName: String,
)
