package ke.co.smartroundclinic.doctor.data.remote.dto.response.directory

import ke.co.smartroundclinic.doctor.domain.model.Doctor
import kotlinx.serialization.Serializable

@Serializable
data class GetRecommendedDoctorsRes(
    val data: RecommendedDoctorsPageData,
    val httpStatusCode: Int,
    val message: String,
    val status: Boolean,
)

@Serializable
data class RecommendedDoctorsPageData(
    val items: List<RecommendedDoctorData>,
    val total: Long,
    val page: Int,
    val size: Int,
)

@Serializable
data class RecommendedDoctorData(
    val profileId: String,
    val doctorId: String,
    val doctorName: String? = null,
    val profilePicture: String? = null,
    val kmpdcRegNumber: String? = null,
    val bio: String? = null,
    val yearsOfExperience: Int? = null,
    val languages: List<String> = emptyList(),
    val facilityName: String? = null,
    val averageRating: Double = 0.0,
    val totalReviews: Int = 0,
    val totalBookings: Int = 0,
    val specializations: List<SpecializationNameData> = emptyList(),
)

@Serializable
data class SpecializationNameData(val specializationName: String)

fun RecommendedDoctorData.toDomain() = Doctor(
    id = doctorId,
    profileId = profileId,
    name = doctorName ?: "Unknown Doctor",
    profilePicture = profilePicture,
    specialization = specializations.firstOrNull()?.specializationName,
    facilityName = facilityName,
    averageRating = averageRating,
    totalReviews = totalReviews,
    totalBookings = totalBookings,
    bio = bio,
    yearsOfExperience = yearsOfExperience,
    languages = languages,
    kmpdcRegNumber = kmpdcRegNumber,
)
