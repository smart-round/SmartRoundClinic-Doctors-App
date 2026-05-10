package ke.co.smartroundclinic.doctor.core.database.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import ke.co.smartroundclinic.doctor.domain.model.DoctorAvailability
import ke.co.smartroundclinic.doctor.domain.model.ScheduleBreakBlock

@Entity(tableName = "doctor_availability")
data class DoctorAvailabilityEntity(
    @PrimaryKey val dayOfWeek: Int,
    val id: String,
    val windowStart: String,
    val windowEnd: String,
    val slotDuration: Int,
    val breakBlocksJson: String, // comma-separated "start|end" pairs
    val isActive: Boolean,
    val timezone: String,
)

fun DoctorAvailabilityEntity.toDomain() = DoctorAvailability(
    id = id,
    dayOfWeek = dayOfWeek,
    windowStart = windowStart,
    windowEnd = windowEnd,
    slotDuration = slotDuration,
    breakBlocks = if (breakBlocksJson.isBlank()) emptyList() else
        breakBlocksJson.split(",").mapNotNull {
            val parts = it.split("|")
            if (parts.size == 2) ScheduleBreakBlock(parts[0], parts[1]) else null
        },
    isActive = isActive,
    timezone = timezone,
)

fun DoctorAvailability.toEntity() = DoctorAvailabilityEntity(
    dayOfWeek = dayOfWeek,
    id = id,
    windowStart = windowStart,
    windowEnd = windowEnd,
    slotDuration = slotDuration,
    breakBlocksJson = breakBlocks.joinToString(",") { "${it.start}|${it.end}" },
    isActive = isActive,
    timezone = timezone,
)
