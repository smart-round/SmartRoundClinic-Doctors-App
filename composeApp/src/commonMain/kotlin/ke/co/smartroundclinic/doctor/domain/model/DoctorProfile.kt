package ke.co.smartroundclinic.doctor.domain.model

data class DoctorProfile(
    val id: String,
    val doctorId: String,
    val kmpdcRegNumber: String?,
    val title: String?,
    val bio: String?,
    val yearsOfExperience: Int?,
    val languages: List<String>,
    val facilityName: String?,
    val averageRating: Double,
    val totalReviews: Int,
    val totalConsultations: Int,
    val createdAt: String,
    val updatedAt: String?,
)
