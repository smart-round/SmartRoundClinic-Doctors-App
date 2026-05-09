package ke.co.smartroundclinic.doctor.data.remote.dto.response.refreshToken


import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class RefreshTokenRes(
    val `data`: Data,
    val httpStatusCode: Int, // 200
    val message: String, // Token refreshed successfully
    val status: Boolean // true
)