package ke.co.smartroundclinic.doctor.data.remote.dto.response

import kotlinx.serialization.Serializable

@Serializable
data class SuccessRes(
    val httpStatusCode: Int,
    val message: String,
    val status: Boolean
)