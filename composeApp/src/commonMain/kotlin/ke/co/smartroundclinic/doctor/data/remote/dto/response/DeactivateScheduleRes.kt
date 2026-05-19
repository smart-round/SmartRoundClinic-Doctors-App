package ke.co.smartroundclinic.doctor.data.remote.dto.response

import ke.co.smartroundclinic.doctor.domain.model.DoctorAvailability
import ke.co.smartroundclinic.doctor.domain.model.ScheduleBreakBlock
import kotlinx.serialization.Serializable

@Serializable
data class DeactivateScheduleRes(
    val `data`: DeactivateData,
    val httpStatusCode: Int, // 200
    val message: String, // Schedule deactivated successfully
    val status: Boolean // true
)

@Serializable
data class DeactivateData(
    val breakBlocks: List<BreakBlocks?>,
    val createdAt: String, // 2026-05-16T17:29:44.861325649Z
    val dayOfWeek: Int, // 0
    val doctorId: String, // 6a08a70a8751f92f85e39bae
    val id: String, // 6a08a9888751f92f85e39bba
    val isActive: Boolean, // false
    val slotDuration: Int, // 30
    val timezone: String, // Africa/Nairobi
    val updatedAt: String, // 2026-05-19T16:50:13.434789Z
    val windowEnd: String, // 17:00
    val windowStart: String // 08:00
)

@Serializable
data class BreakBlocks(
    val start: String,
    val end: String
)

fun DeactivateData.toDomain(): DoctorAvailability = DoctorAvailability(
    id = id,
    dayOfWeek = dayOfWeek,
    windowStart = windowStart,
    windowEnd = windowEnd,
    slotDuration = slotDuration,
    breakBlocks = breakBlocks.filterNotNull().map { ScheduleBreakBlock(it.start, it.end) },
    isActive = isActive,
    timezone = timezone,
)