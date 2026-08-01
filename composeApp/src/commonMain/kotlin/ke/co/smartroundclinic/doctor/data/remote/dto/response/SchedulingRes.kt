package ke.co.smartroundclinic.doctor.data.remote.dto.response

import ke.co.smartroundclinic.doctor.domain.model.Appointment
import ke.co.smartroundclinic.doctor.domain.model.AppointmentStatus
import ke.co.smartroundclinic.doctor.domain.model.DoctorAvailability
import ke.co.smartroundclinic.doctor.domain.model.NextAppointment
import ke.co.smartroundclinic.doctor.domain.model.ScheduleBreakBlock
import kotlinx.serialization.Serializable

@Serializable
data class GetScheduleRes(
    val data: List<AvailabilityEntryData>,
)

@Serializable
data class AvailabilityEntryData(
    val id: String,
    val doctorId: String,
    val dayOfWeek: Int,
    val windowStart: String,
    val windowEnd: String,
    val slotDuration: Int,
    val breakBlocks: List<BreakBlockData> = emptyList(),
    val isActive: Boolean,
    val timezone: String,
    val createdAt: String = "",
    val updatedAt: String = "",
)

@Serializable
data class BreakBlockData(val start: String, val end: String)

@Serializable
data class UpsertAvailabilityRes(val data: AvailabilityEntryData)

@Serializable
data class GetAppointmentsRes(val data: List<AppointmentData>)

@Serializable
data class AppointmentData(
    val id: String,
    val doctorId: String,
    val patientId: String,
    val patientName: String,
    val patientProfilePicture: String? = null,
    val doctorSpecialities: List<String> = emptyList(),
    val date: String,
    val slotStart: String,
    val slotEnd: String,
    val status: String,
    val bookedAt: String = "",
    val notes: String? = null,
    val cancellationReason: String? = null,
    val cancelledBy: String? = null,
    val updatedAt: String? = null,
    val referralId: String? = null,
    val referredByDoctorName: String? = null,
    val referredByDoctorPicture: String? = null,
)

@Serializable
data class AppointmentActionRes(val data: AppointmentData)

@Serializable
data class NextAppointmentRes(
    val httpStatusCode: Int,
    val message: String,
    val status: Boolean,
    val data: NextAppointmentData? = null,
)

@Serializable
data class NextAppointmentData(
    val id: String,
    val doctorId: String,
    val patientId: String,
    val date: String,
    val slotStart: String,
    val slotEnd: String,
    val status: String,
)

fun AvailabilityEntryData.toDomain() = DoctorAvailability(
    id = id,
    dayOfWeek = dayOfWeek,
    windowStart = windowStart,
    windowEnd = windowEnd,
    slotDuration = slotDuration,
    breakBlocks = breakBlocks.map { ScheduleBreakBlock(it.start, it.end) },
    isActive = isActive,
    timezone = timezone,
)

fun AppointmentData.toDomain() = Appointment(
    id = id,
    patientId = patientId,
    patientName = patientName,
    patientProfilePicture = patientProfilePicture,
    doctorSpecialities = doctorSpecialities,
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
)

fun NextAppointmentData.toDomain() = NextAppointment(
    date = date,
    slotStart = slotStart,
    slotEnd = slotEnd,
    status = status,
)
