package ke.co.smartroundclinic.doctor.data.remote.dto.response

import ke.co.smartroundclinic.doctor.domain.model.DoctorProfile
import kotlinx.serialization.Serializable

@Serializable
data class DoctorProfileRes(
    val `data`: DoctorProfileData,
    val httpStatusCode: Int,
    val message: String,
    val status: Boolean,
)

@Serializable
data class DoctorProfileData(
    val id: String,
    val doctorId: String,
    val kmpdcRegNumber: String? = null,
    val title: String? = null,
    val bio: String? = null,
    val yearsOfExperience: Int? = null,
    val languages: List<String> = emptyList(),
    val facilityName: String? = null,
    val averageRating: Double = 0.0,
    val totalReviews: Int = 0,
    val totalConsultations: Int = 0,
    val createdAt: String,
    val updatedAt: String? = null,
)

fun DoctorProfileData.toDomain() = DoctorProfile(
    id = id,
    doctorId = doctorId,
    kmpdcRegNumber = kmpdcRegNumber,
    title = title,
    bio = bio,
    yearsOfExperience = yearsOfExperience,
    languages = languages,
    facilityName = facilityName,
    averageRating = averageRating,
    totalReviews = totalReviews,
    totalConsultations = totalConsultations,
    createdAt = createdAt,
    updatedAt = updatedAt,
)
