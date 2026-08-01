package ke.co.smartroundclinic.doctor.domain.model

data class Referral(
    val id: String,
    val sourceAppointmentId: String,
    val patientId: String,
    val patientName: String?,
    val receivingDoctorId: String,
    val receivingDoctorName: String?,
    val reason: String,
    val status: String,
    val createdAt: String,
)

data class ReferralEligibility(val eligible: Boolean, val reasons: List<String>)
