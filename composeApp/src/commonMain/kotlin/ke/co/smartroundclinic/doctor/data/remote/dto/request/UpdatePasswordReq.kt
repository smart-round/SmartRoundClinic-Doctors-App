package ke.co.smartroundclinic.doctor.data.remote.dto.request


import kotlinx.serialization.Serializable

@Serializable
data class UpdatePasswordReq(
    val email: String, // dev.pasaka@gmail.com
    val newPassword: String, // pasaka001
    val otpCode: String // 544983
)