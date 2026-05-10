package ke.co.smartroundclinic.doctor.data.remote.dto.response

import ke.co.smartroundclinic.doctor.domain.model.DoctorSpecialization
import kotlinx.serialization.Serializable

@Serializable
data class GetSpecializationRes(
    val `data`: List<SpecializationData>,
    val httpStatusCode: Int, // 200
    val message: String, // Success
    val status: Boolean // true
)

@Serializable
data class SpecializationData(
    val createdAt: String, // 2026-05-04T11:35:08.258198501Z
    val doctorId: String, // 69f8846c319d59e154fdab3c
    val id: String, // 69f8846c319d59e154fdab40
    val specialization: Specialization
)

@Serializable
data class Specialization(
    val color: String, // #DC2626
    val description: String, // Diagnosis and treatment of diseases and conditions of the heart and cardiovascular system, from routine heart health to complex interventional procedures.
    val iconUrl: String, // https://unpkg.com/lucide-static@latest/icons/heart-pulse.svg
    val id: String, // 69dbb5ac73f215116ff7fdab
    val title: String // Cardiology
)

fun SpecializationData.toDomain() = DoctorSpecialization(
    id = id,
    doctorId = doctorId,
    createdAt = createdAt,
    specializationId = specialization.id,
    title = specialization.title,
    description = specialization.description,
    color = specialization.color,
    iconUrl = specialization.iconUrl,
)