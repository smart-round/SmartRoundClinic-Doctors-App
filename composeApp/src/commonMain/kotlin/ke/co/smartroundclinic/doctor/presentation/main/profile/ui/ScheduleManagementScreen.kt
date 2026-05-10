package ke.co.smartroundclinic.doctor.presentation.main.profile.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material.icons.outlined.ChevronLeft
import androidx.compose.material.icons.outlined.ChevronRight
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import ke.co.smartroundclinic.doctor.presentation.common.composables.PrimaryButton
import ke.co.smartroundclinic.doctor.presentation.theme.GradientEnd
import ke.co.smartroundclinic.doctor.presentation.theme.GradientStart
import ke.co.smartroundclinic.doctor.presentation.theme.Primary40
import ke.co.smartroundclinic.doctor.presentation.theme.Primary90
import ke.co.smartroundclinic.doctor.presentation.theme.ShapePill
import kotlinx.datetime.Clock
import kotlinx.datetime.DayOfWeek
import kotlinx.datetime.LocalDate
import kotlinx.datetime.LocalTime
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime


private data class TimeSlot(val start: LocalTime, val end: LocalTime)

private fun LocalTime.toDisplayString(): String {
    val h = if (hour % 12 == 0) 12 else hour % 12
    val m = minute.toString().padStart(2, '0')
    val period = if (hour < 12) "AM" else "PM"
    return "$h:$m $period"
}

private fun LocalTime.plusHour(): LocalTime {
    val totalMinutes = hour * 60 + minute + 60
    return LocalTime(totalMinutes / 60 % 24, totalMinutes % 60)
}

private val defaultSlots = listOf(
    TimeSlot(LocalTime(8, 0), LocalTime(9, 0)),
    TimeSlot(LocalTime(9, 0), LocalTime(10, 0)),
)

private val dayAbbreviations = listOf("Mon", "Tue", "Wed", "Thu", "Fri", "Sat", "Sun")
private val dayOfWeekOrder = listOf(
    DayOfWeek.MONDAY, DayOfWeek.TUESDAY, DayOfWeek.WEDNESDAY, DayOfWeek.THURSDAY,
    DayOfWeek.FRIDAY, DayOfWeek.SATURDAY, DayOfWeek.SUNDAY,
)
private val calendarHeaders = listOf("Mo", "Tu", "We", "Th", "Fr", "Sa", "Su")

private val monthNames = listOf(
    "January", "February", "March", "April", "May", "June",
    "July", "August", "September", "October", "November", "December",
)

private fun daysInMonth(year: Int, month: Int): Int {
    return when (month) {
        1, 3, 5, 7, 8, 10, 12 -> 31
        4, 6, 9, 11 -> 30
        2 -> if (year % 4 == 0 && (year % 100 != 0 || year % 400 == 0)) 29 else 28
        else -> 30
    }
}

private fun firstDayOfWeekIndex(year: Int, month: Int): Int {
    val d = LocalDate(year, month, 1)
    return dayOfWeekOrder.indexOf(d.dayOfWeek)
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
internal fun ScheduleManagementScreen(
    onBack: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val today = Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault()).date
    var displayYear by remember { mutableStateOf(today.year) }
    var displayMonth by remember { mutableStateOf(today.month.ordinal + 1) }
    var selectedDates by remember { mutableStateOf(setOf<Int>()) }
    var enabledDays by remember { mutableStateOf(setOf<Int>()) }
    var timeSlots by remember { mutableStateOf(defaultSlots) }

    Scaffold(
        modifier = modifier,
        contentWindowInsets = WindowInsets(0),
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues),
        ) {
            ScheduleHeader(onBack = onBack)

            Column(
                modifier = Modifier
                    .weight(1f)
                    .verticalScroll(rememberScrollState())
                    .padding(horizontal = 16.dp),
            ) {
                Spacer(Modifier.height(16.dp))

                SectionLabel("Pick a Date")
                Spacer(Modifier.height(8.dp))

                MonthCalendar(
                    year = displayYear,
                    month = displayMonth,
                    selectedDates = selectedDates,
                    today = today,
                    onPrevMonth = {
                        if (displayMonth == 1) { displayMonth = 12; displayYear-- }
                        else displayMonth--
                        selectedDates = emptySet()
                    },
                    onNextMonth = {
                        if (displayMonth == 12) { displayMonth = 1; displayYear++ }
                        else displayMonth++
                        selectedDates = emptySet()
                    },
                    onDateClick = { day ->
                        selectedDates = if (day in selectedDates) selectedDates - day
                        else selectedDates + day
                    },
                )

                Spacer(Modifier.height(20.dp))
                HorizontalDivider(thickness = 0.5.dp, color = MaterialTheme.colorScheme.outlineVariant)
                Spacer(Modifier.height(16.dp))

                SectionLabel("Select Days")
                Spacer(Modifier.height(10.dp))

                FlowRow(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                ) {
                    dayAbbreviations.forEachIndexed { index, label ->
                        val isEnabled = index in enabledDays
                        DayChip(
                            label = label,
                            isSelected = isEnabled,
                            onClick = {
                                enabledDays = if (isEnabled) enabledDays - index else enabledDays + index
                            },
                        )
                    }
                }

                if (enabledDays.isNotEmpty()) {
                    Spacer(Modifier.height(20.dp))
                    HorizontalDivider(thickness = 0.5.dp, color = MaterialTheme.colorScheme.outlineVariant)
                    Spacer(Modifier.height(16.dp))

                    SectionLabel("Select Time")
                    Spacer(Modifier.height(10.dp))

                    timeSlots.forEachIndexed { index, slot ->
                        TimeSlotRow(
                            slot = slot,
                            onRemove = if (timeSlots.size > 1) {
                                { timeSlots = timeSlots.toMutableList().also { it.removeAt(index) } }
                            } else null,
                        )
                        Spacer(Modifier.height(8.dp))
                    }

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(ShapePill)
                            .clickable(
                                indication = null,
                                interactionSource = remember { MutableInteractionSource() },
                                onClick = {
                                    val last = timeSlots.last()
                                    timeSlots = timeSlots + TimeSlot(last.end, last.end.plusHour())
                                },
                            )
                            .padding(vertical = 10.dp),
                        horizontalArrangement = Arrangement.Center,
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        Icon(imageVector = Icons.Filled.Add, contentDescription = null, tint = Primary40, modifier = Modifier.size(18.dp))
                        Spacer(Modifier.width(4.dp))
                        Text(text = "Add Time Slot", style = MaterialTheme.typography.labelMedium, color = Primary40)
                    }
                }

                Spacer(Modifier.height(24.dp))

                PrimaryButton(onClick = {}) {
                    Text(
                        text = "Save Availability",
                        style = MaterialTheme.typography.labelLarge,
                        color = Color.White,
                        modifier = Modifier.padding(vertical = 14.dp),
                    )
                }

                Spacer(Modifier.height(24.dp))
            }
        }
    }
}

@Composable
private fun ScheduleHeader(onBack: () -> Unit, modifier: Modifier = Modifier) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .wrapContentHeight()
            .background(brush = Brush.horizontalGradient(colors = listOf(GradientStart, GradientEnd)))
            .statusBarsPadding(),
    ) {
        Box(modifier = Modifier.fillMaxWidth()) {
            IconButton(onClick = onBack) {
                Icon(
                    imageVector = Icons.AutoMirrored.Outlined.ArrowBack,
                    contentDescription = "Back",
                    tint = Color.White,
                )
            }
            Text(
                text = "Schedule Management",
                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.SemiBold),
                color = Color.White,
                modifier = Modifier.align(Alignment.Center).padding(vertical = 12.dp),
            )
        }
    }
}

@Composable
private fun SectionLabel(text: String, modifier: Modifier = Modifier) {
    Text(
        text = text,
        style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.SemiBold),
        modifier = modifier,
    )
}

@Composable
private fun MonthCalendar(
    year: Int,
    month: Int,
    selectedDates: Set<Int>,
    today: LocalDate,
    onPrevMonth: () -> Unit,
    onNextMonth: () -> Unit,
    onDateClick: (Int) -> Unit,
    modifier: Modifier = Modifier,
) {
    val totalDays = daysInMonth(year, month)
    val startOffset = firstDayOfWeekIndex(year, month)
    val totalCells = startOffset + totalDays
    val rows = (totalCells + 6) / 7

    Column(
        modifier = modifier
            .fillMaxWidth()
            .background(MaterialTheme.colorScheme.surface, shape = MaterialTheme.shapes.medium)
            .padding(12.dp),
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween,
        ) {
            IconButton(onClick = onPrevMonth, modifier = Modifier.size(36.dp)) {
                Icon(imageVector = Icons.Outlined.ChevronLeft, contentDescription = "Prev", tint = MaterialTheme.colorScheme.onSurface)
            }
            Text(
                text = "${monthNames[month - 1]} $year",
                style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.SemiBold),
            )
            IconButton(onClick = onNextMonth, modifier = Modifier.size(36.dp)) {
                Icon(imageVector = Icons.Outlined.ChevronRight, contentDescription = "Next", tint = MaterialTheme.colorScheme.onSurface)
            }
        }

        Spacer(Modifier.height(8.dp))

        Row(modifier = Modifier.fillMaxWidth()) {
            calendarHeaders.forEach { header ->
                Text(
                    text = header,
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.weight(1f),
                )
            }
        }

        Spacer(Modifier.height(4.dp))

        repeat(rows) { row ->
            Row(modifier = Modifier.fillMaxWidth()) {
                repeat(7) { col ->
                    val cellIndex = row * 7 + col
                    val day = cellIndex - startOffset + 1
                    val isValid = day in 1..totalDays
                    val isSelected = isValid && day in selectedDates
                    val isToday = isValid && today.year == year && (today.month.ordinal + 1) == month && today.dayOfMonth == day

                    Box(
                        contentAlignment = Alignment.Center,
                        modifier = Modifier
                            .weight(1f)
                            .aspectRatio(1f)
                            .padding(2.dp)
                            .clip(CircleShape)
                            .background(
                                when {
                                    isSelected -> Primary40
                                    isToday -> Primary90
                                    else -> Color.Transparent
                                }
                            )
                            .then(
                                if (isValid) Modifier.clickable(
                                    indication = null,
                                    interactionSource = remember { MutableInteractionSource() },
                                ) { onDateClick(day) } else Modifier
                            ),
                    ) {
                        if (isValid) {
                            Text(
                                text = day.toString(),
                                style = MaterialTheme.typography.bodySmall,
                                color = when {
                                    isSelected -> Color.White
                                    isToday -> Primary40
                                    else -> MaterialTheme.colorScheme.onSurface
                                },
                                fontWeight = if (isToday || isSelected) FontWeight.SemiBold else FontWeight.Normal,
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun DayChip(
    label: String,
    isSelected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Box(
        contentAlignment = Alignment.Center,
        modifier = modifier
            .clip(ShapePill)
            .background(if (isSelected) Primary40 else MaterialTheme.colorScheme.surfaceVariant)
            .clickable(
                indication = null,
                interactionSource = remember { MutableInteractionSource() },
                onClick = onClick,
            )
            .padding(horizontal = 16.dp, vertical = 8.dp),
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.labelMedium,
            color = if (isSelected) Color.White else MaterialTheme.colorScheme.onSurfaceVariant,
        )
    }
}

@Composable
private fun TimeSlotRow(
    slot: TimeSlot,
    onRemove: (() -> Unit)?,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .background(MaterialTheme.colorScheme.surface, shape = MaterialTheme.shapes.small)
            .padding(horizontal = 12.dp, vertical = 10.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        Text(
            text = slot.start.toDisplayString(),
            style = MaterialTheme.typography.bodyMedium,
            modifier = Modifier.weight(1f),
        )
        Text(
            text = "—",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )
        Text(
            text = slot.end.toDisplayString(),
            style = MaterialTheme.typography.bodyMedium,
            modifier = Modifier.weight(1f),
        )
        if (onRemove != null) {
            Box(
                modifier = Modifier
                    .size(28.dp)
                    .clip(CircleShape)
                    .background(MaterialTheme.colorScheme.errorContainer)
                    .clickable(
                        indication = null,
                        interactionSource = remember { MutableInteractionSource() },
                        onClick = onRemove,
                    ),
                contentAlignment = Alignment.Center,
            ) {
                Icon(
                    imageVector = Icons.Filled.Remove,
                    contentDescription = "Remove slot",
                    tint = MaterialTheme.colorScheme.error,
                    modifier = Modifier.size(16.dp),
                )
            }
        }
    }
}
