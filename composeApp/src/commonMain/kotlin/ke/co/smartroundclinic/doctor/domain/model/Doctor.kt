package ke.co.smartroundclinic.doctor.domain.model

data class Doctor(
    val id: String,
    val profileId: String,
    val name: String,
    val profilePicture: String?,
    val specialization: String?,
    val facilityName: String?,
    val averageRating: Double,
    val totalReviews: Int,
    val totalBookings: Int = 0,
    val bio: String? = null,
    val yearsOfExperience: Int? = null,
    val languages: List<String> = emptyList(),
    val kmpdcRegNumber: String? = null,
)
