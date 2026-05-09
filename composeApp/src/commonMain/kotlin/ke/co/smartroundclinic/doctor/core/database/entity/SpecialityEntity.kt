package ke.co.smartroundclinic.doctor.core.database.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import ke.co.smartroundclinic.doctor.domain.model.Speciality

@Entity(tableName = "specialities")
data class SpecialityEntity(
    @PrimaryKey val id: String,
    val color: String,
    val description: String,
    val iconUrl: String?,
    val serviceCategoryId: String?,
    val serviceTierId: String?,
    val title: String
)

fun SpecialityEntity.toDomain(): Speciality = Speciality(
    id = id,
    title = title,
    description = description,
    color = color,
    iconUrl = iconUrl,
    serviceCategoryId = serviceCategoryId,
    serviceTierId = serviceTierId
)

fun Speciality.toEntity(): SpecialityEntity = SpecialityEntity(
    id = id,
    title = title,
    description = description,
    color = color,
    iconUrl = iconUrl,
    serviceCategoryId = serviceCategoryId,
    serviceTierId = serviceTierId
)
