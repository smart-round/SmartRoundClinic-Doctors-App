package ke.co.smartroundclinic.doctor.data.remote.dto.response

import ke.co.smartroundclinic.doctor.domain.model.Rating
import kotlinx.serialization.Serializable

@Serializable
data class RatingData(
    val id: String,
    val appointmentId: String,
    val doctorId: String,
    val patientId: String,
    val rating: Int,
    val comment: String? = null,
    val createdAt: String,
    val updatedAt: String? = null,
    val doctorName: String? = null,
    val doctorProfilePicture: String? = null,
)

@Serializable
data class RatingResponse(
    val data: RatingData? = null,
    val httpStatusCode: Int,
    val message: String,
    val status: Boolean,
)

@Serializable
data class RatingPaginatedData(
    val items: List<RatingData> = emptyList(),
    val total: Int = 0,
    val page: Int = 1,
    val size: Int = 20,
)

@Serializable
data class RatingListResponse(
    val data: RatingPaginatedData? = null,
    val httpStatusCode: Int,
    val message: String,
    val status: Boolean,
)

fun RatingData.toDomain() = Rating(
    id = id,
    appointmentId = appointmentId,
    doctorId = doctorId,
    patientId = patientId,
    rating = rating,
    comment = comment,
    createdAt = createdAt,
    updatedAt = updatedAt,
    raterName = doctorName,
    raterProfilePicture = doctorProfilePicture,
)
