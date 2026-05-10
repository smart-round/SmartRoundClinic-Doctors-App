package ke.co.smartroundclinic.doctor.domain.model

data class DoctorAvailability(
    val id: String,
    val dayOfWeek: Int,
    val windowStart: String,
    val windowEnd: String,
    val slotDuration: Int,
    val breakBlocks: List<ScheduleBreakBlock>,
    val isActive: Boolean,
    val timezone: String,
)

data class ScheduleBreakBlock(val start: String, val end: String)
