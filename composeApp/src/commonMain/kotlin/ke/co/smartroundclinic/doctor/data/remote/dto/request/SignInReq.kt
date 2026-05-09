package ke.co.smartroundclinic.doctor.data.remote.dto.request

import kotlinx.serialization.Serializable

@Serializable
data class SignInReq(
    val email: String,
    val password: String,
)