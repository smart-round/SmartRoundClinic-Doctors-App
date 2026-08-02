package ke.co.smartroundclinic.doctor.domain.model

data class Referral(
    val id: String,
    val sourceAppointmentId: String,
    val referringDoctorId: String,
    val referringDoctorName: String?,
    val referringDoctorPicture: String?,
    val patientId: String,
    val patientName: String?,
    val receivingDoctorId: String,
    val receivingDoctorName: String?,
    val receivingDoctorPicture: String?,
    val reason: String,
    val status: String,
    val resultingAppointmentId: String?,
    val createdAt: String,
    val respondedAt: String?,
)

data class ReferralEligibility(val eligible: Boolean, val reasons: List<String>)
