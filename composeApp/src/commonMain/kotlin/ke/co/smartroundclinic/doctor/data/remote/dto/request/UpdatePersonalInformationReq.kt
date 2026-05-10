package ke.co.smartroundclinic.doctor.data.remote.dto.request


import kotlinx.serialization.Serializable

@Serializable
data class UpdatePersonalInformationReq(
    val dateOfBirth: String? = null, // 03/30/2002
    val email: String? = null, // dev.pasaka@gmail.com
    val fullName: String ? = null, // James Bond
    val gender: Gender? = null, // NON_BINARY
    val phoneNumber: String // +254717722323
)
enum class Gender{
    NON_BINARY, MALE, FEMALE
}