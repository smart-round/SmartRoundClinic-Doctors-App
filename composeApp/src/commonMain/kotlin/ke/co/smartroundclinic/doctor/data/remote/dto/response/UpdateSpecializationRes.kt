package ke.co.smartroundclinic.doctor.data.remote.dto.response


import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class UpdateSpecializationRes(
    val data: UpdateSpecializationData,
    val httpStatusCode: Int, // 200
    val message: String, // Specialization updated successfully
    val status: Boolean // true
)

@Serializable
data class UpdateSpecializationData(
    val createdAt: String, // 2026-05-04T11:35:08.258198501Z
    val doctorId: String, // 69f8846c319d59e154fdab3c
    val id: String, // 69f8846c319d59e154fdab40
    val specializationId: String // 69dbb5ac73f215116ff7fdab
)