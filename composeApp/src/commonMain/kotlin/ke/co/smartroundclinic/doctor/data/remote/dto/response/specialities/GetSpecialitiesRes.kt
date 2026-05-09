package ke.co.smartroundclinic.doctor.data.remote.dto.response.specialities

import ke.co.smartroundclinic.doctor.domain.model.Speciality
import kotlinx.serialization.Serializable

@Serializable
data class GetSpecialitiesRes(
    val data: List<Data>,
    val httpStatusCode: Int,
    val message: String,
    val status: Boolean
)

@Serializable
data class Data(
    val color: String,
    val description: String,
    val iconUrl: String?,
    val id: String,
    val serviceCategoryId: String?,
    val serviceTierId: String?,
    val title: String
)

fun GetSpecialitiesRes.toModel(): List<Speciality> = data.map { it.toDomain() }

fun Data.toDomain(): Speciality = Speciality(
    id = id,
    title = title,
    description = description,
    color = color,
    iconUrl = iconUrl,
    serviceCategoryId = serviceCategoryId,
    serviceTierId = serviceTierId
)
