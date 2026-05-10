package ke.co.smartroundclinic.doctor.data.remote.dto.request

import kotlinx.serialization.Serializable

@Serializable
data class UpsertAvailabilityReq(
    val dayOfWeek: Int,
    val windowStart: String,
    val windowEnd: String,
    val slotDuration: Int,
    val breakBlocks: List<BreakBlockReq> = emptyList(),
    val isActive: Boolean = true,
    val timezone: String = "Africa/Nairobi",
)

@Serializable
data class BreakBlockReq(val start: String, val end: String)

@Serializable
data class UpdateAvailabilityDayReq(val isActive: Boolean)

@Serializable
data class CancelAppointmentReq(val reason: String? = null)
