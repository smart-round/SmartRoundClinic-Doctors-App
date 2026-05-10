package ke.co.smartroundclinic.doctor.domain.model

data class DoctorSpecialization(
    val id: String,
    val doctorId: String,
    val createdAt: String,
    val specializationId: String,
    val title: String,
    val description: String,
    val color: String,
    val iconUrl: String,
)
