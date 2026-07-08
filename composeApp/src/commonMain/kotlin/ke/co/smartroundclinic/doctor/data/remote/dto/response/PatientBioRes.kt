package ke.co.smartroundclinic.doctor.data.remote.dto.response

import ke.co.smartroundclinic.doctor.domain.model.PatientBio
import kotlinx.serialization.Serializable

@Serializable
data class PatientBioData(
    val weight: Double? = null,
    val weightIn: String? = null,
    val height: Double? = null,
    val heightIn: String? = null,
    val bloodGroup: String? = null,
    val maritalStatus: String? = null,
    val allergies: List<String> = emptyList(),
    val chronicConditions: List<String> = emptyList(),
    val currentMedications: List<String> = emptyList(),
    val averageRating: Double = 0.0,
    val totalReviews: Int = 0,
)

@Serializable
data class PatientBioResponse(
    val data: PatientBioData? = null,
    val httpStatusCode: Int = 0,
    val message: String = "",
    val status: Boolean = false,
)

fun PatientBioData.toDomain() = PatientBio(
    weight = weight,
    weightIn = weightIn,
    height = height,
    heightIn = heightIn,
    bloodGroup = bloodGroup,
    maritalStatus = maritalStatus?.lowercase()?.replaceFirstChar { it.uppercase() },
    allergies = allergies,
    chronicConditions = chronicConditions,
    currentMedications = currentMedications,
    averageRating = averageRating,
    totalReviews = totalReviews,
)
