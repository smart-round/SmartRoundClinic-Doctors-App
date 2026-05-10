package ke.co.smartroundclinic.doctor.data.remote.dto.response


import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class AddSpecializationRes(
    val data: AddSpecializationData,
    val httpStatusCode: Int, // 201
    val message: String, // Specialization added successfully
    val status: Boolean // true
)

@Serializable
data class AddSpecializationData(
    val createdAt: String, // 2026-05-10T11:40:08.374554Z
    val doctorId: String, // 69f8846c319d59e154fdab3c
    val id: String, // 6a006e984be969008b8125fc
    val specializationId: String // 69dbb5ac73f215116ff7fda9
)