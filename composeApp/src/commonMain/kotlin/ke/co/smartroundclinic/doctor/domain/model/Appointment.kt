package ke.co.smartroundclinic.doctor.domain.model

data class Appointment(
    val id: String,
    val patientId: String,
    val patientName: String,
    val patientProfilePicture: String?,
    val doctorSpecialities: List<String>,
    val date: String,
    val slotStart: String,
    val slotEnd: String,
    val status: AppointmentStatus,
    val notes: String?,
    val cancellationReason: String?,
    /** Set when this appointment resulted from an accepted referral. */
    val referralId: String? = null,
    val referredByDoctorName: String? = null,
    val referredByDoctorPicture: String? = null,
)

enum class AppointmentStatus { BOOKED, CONFIRMED, COMPLETED, CANCELLED, NO_SHOW }
