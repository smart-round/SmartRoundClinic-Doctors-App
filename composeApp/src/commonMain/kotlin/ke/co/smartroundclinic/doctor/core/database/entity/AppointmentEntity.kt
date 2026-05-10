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
    val doctorSpecialitiesJson: String, // comma-separated
    val date: String,
    val slotStart: String,
    val slotEnd: String,
    val status: String,
    val notes: String?,
    val cancellationReason: String?,
)

fun AppointmentEntity.toDomain() = Appointment(
    id = id,
    patientId = patientId,
    patientName = patientName,
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
)

fun Appointment.toEntity() = AppointmentEntity(
    id = id,
    patientId = patientId,
    patientName = patientName,
    doctorSpecialitiesJson = doctorSpecialities.joinToString(","),
    date = date,
    slotStart = slotStart,
    slotEnd = slotEnd,
    status = status.name,
    notes = notes,
    cancellationReason = cancellationReason,
)
