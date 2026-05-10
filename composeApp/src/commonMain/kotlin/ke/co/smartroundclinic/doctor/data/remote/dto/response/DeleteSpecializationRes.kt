package ke.co.smartroundclinic.doctor.data.remote.dto.response


import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class DeleteSpecializationRes(
    val `data`: Boolean, // true
    val httpStatusCode: Int, // 200
    val message: String, // Specialization removed successfully
    val status: Boolean // true
)