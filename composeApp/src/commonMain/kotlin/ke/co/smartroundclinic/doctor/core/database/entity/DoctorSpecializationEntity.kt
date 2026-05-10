package ke.co.smartroundclinic.doctor.core.database.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import ke.co.smartroundclinic.doctor.domain.model.DoctorSpecialization

@Entity(tableName = "doctor_specializations")
data class DoctorSpecializationEntity(
    @PrimaryKey val id: String,
    val doctorId: String,
    val createdAt: String,
    val specializationId: String,
    val title: String,
    val description: String,
    val color: String,
    val iconUrl: String,
)

fun DoctorSpecializationEntity.toDomain() = DoctorSpecialization(
    id = id,
    doctorId = doctorId,
    createdAt = createdAt,
    specializationId = specializationId,
    title = title,
    description = description,
    color = color,
    iconUrl = iconUrl,
)

fun DoctorSpecialization.toEntity() = DoctorSpecializationEntity(
    id = id,
    doctorId = doctorId,
    createdAt = createdAt,
    specializationId = specializationId,
    title = title,
    description = description,
    color = color,
    iconUrl = iconUrl,
)
