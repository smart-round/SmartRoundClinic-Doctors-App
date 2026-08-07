package ke.co.smartroundclinic.doctor.core.database.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import ke.co.smartroundclinic.doctor.domain.model.Appointment
import ke.co.smartroundclinic.doctor.domain.model.AppointmentStatus

@Entity(tableName = "appointments")
data class AppointmentEntity(
    @PrimaryKey val id: String,
    val patientId: String,
    val patientName: String,
    val patientProfilePicture: String?,
    val doctorSpecialitiesJson: String,
    val date: String,
    val slotStart: String,
    val slotEnd: String,
    val status: String,
    val notes: String?,
    val cancellationReason: String?,
    val referralId: String? = null,
    val referredByDoctorName: String? = null,
    val referredByDoctorPicture: String? = null,
    val amount: Double? = null,
    val currency: String = "KES",
)

fun AppointmentEntity.toDomain() = Appointment(
    id = id,
    patientId = patientId,
    patientName = patientName,
    patientProfilePicture = patientProfilePicture,
    doctorSpecialities = if (doctorSpecialitiesJson.isBlank()) emptyList()
                         else doctorSpecialitiesJson.split(","),
    date = date,
    slotStart = slotStart,
    slotEnd = slotEnd,
    status = when (status) {
        "CONFIRMED" -> AppointmentStatus.CONFIRMED
        "COMPLETED" -> AppointmentStatus.COMPLETED
        "CANCELLED" -> AppointmentStatus.CANCELLED
        "NO_SHOW" -> AppointmentStatus.NO_SHOW
        else -> AppointmentStatus.BOOKED
    },
    notes = notes,
    cancellationReason = cancellationReason,
    referralId = referralId,
    referredByDoctorName = referredByDoctorName,
    referredByDoctorPicture = referredByDoctorPicture,
    amount = amount,
    currency = currency,
)

fun Appointment.toEntity() = AppointmentEntity(
    id = id,
    patientId = patientId,
    patientName = patientName,
    patientProfilePicture = patientProfilePicture,
    doctorSpecialitiesJson = doctorSpecialities.joinToString(","),
    date = date,
    slotStart = slotStart,
    slotEnd = slotEnd,
    status = status.name,
    notes = notes,
    cancellationReason = cancellationReason,
    referralId = referralId,
    referredByDoctorName = referredByDoctorName,
    referredByDoctorPicture = referredByDoctorPicture,
    amount = amount,
    currency = currency,
)
