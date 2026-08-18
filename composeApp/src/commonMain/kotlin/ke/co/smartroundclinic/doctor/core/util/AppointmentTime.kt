package ke.co.smartroundclinic.doctor.core.util

import kotlinx.datetime.Instant
import kotlinx.datetime.LocalDate
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toInstant

private val APPOINTMENT_TIMEZONE = TimeZone.of("Africa/Nairobi")

private val WEEKDAY_LABELS = listOf("Mon", "Tue", "Wed", "Thu", "Fri", "Sat", "Sun")
private val MONTH_LABELS = listOf(
    "Jan", "Feb", "Mar", "Apr", "May", "Jun",
    "Jul", "Aug", "Sep", "Oct", "Nov", "Dec",
)

/** Formats an appointment `date` ("yyyy-MM-dd") as "Wed Jun 20". Falls back to the raw string when unparseable. */
fun formatAppointmentDate(date: String): String = runCatching {
    val parsed = LocalDate.parse(date)
    "${WEEKDAY_LABELS[parsed.dayOfWeek.ordinal]} ${MONTH_LABELS[parsed.monthNumber - 1]} ${parsed.dayOfMonth}"
}.getOrDefault(date)

/** Formats a 24h "HH:mm" pair as "8:00 - 8:20 AM", collapsing a shared meridiem onto the end. */
fun formatAppointmentTimeRange(slotStart: String, slotEnd: String): String {
    val start = splitMeridiem(slotStart) ?: return "$slotStart - $slotEnd"
    val end = splitMeridiem(slotEnd) ?: return "$slotStart - $slotEnd"
    return if (start.second == end.second) {
        "${start.first} - ${end.first} ${end.second}"
    } else {
        "${start.first} ${start.second} - ${end.first} ${end.second}"
    }
}

/** Returns "8:00" to "AM" for "08:00", or null when the input isn't "HH:mm". */
private fun splitMeridiem(time: String): Pair<String, String>? = runCatching {
    val parts = time.split(":")
    val hour = parts[0].toInt()
    val minute = parts[1].take(2)
    val meridiem = if (hour < 12) "AM" else "PM"
    val displayHour = if (hour % 12 == 0) 12 else hour % 12
    "$displayHour:$minute" to meridiem
}.getOrNull()

/** Parses an appointment's `date` ("yyyy-MM-dd") + `slotStart`/`slotEnd` ("HH:mm") into an [Instant], anchored to the clinic's timezone — mirrors the backend's parsing in CompleteAppointmentUseCase. */
fun parseAppointmentInstant(date: String, time: String): Instant? = runCatching {
    val (h, m) = time.split(":").map { it.toInt() }
    val dateParts = date.split("-").map { it.toInt() }
    LocalDateTime(dateParts[0], dateParts[1], dateParts[2], h, m, 0, 0)
        .toInstant(APPOINTMENT_TIMEZONE)
}.getOrNull()
