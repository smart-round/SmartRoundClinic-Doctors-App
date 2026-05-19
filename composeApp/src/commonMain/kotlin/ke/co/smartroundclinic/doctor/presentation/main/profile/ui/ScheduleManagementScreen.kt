package ke.co.smartroundclinic.doctor.presentation.main.profile.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.ArrowBack
import androidx.compose.material.icons.outlined.AccessTime
import androidx.compose.material.icons.outlined.Add
import androidx.compose.material.icons.outlined.ArrowBackIosNew
import androidx.compose.material.icons.outlined.ArrowForwardIos
import androidx.compose.material.icons.outlined.Close
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.OutlinedTextField
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TimePicker
import androidx.compose.material3.TimePickerDefaults
import androidx.compose.material3.rememberTimePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
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
import ke.co.smartroundclinic.doctor.domain.model.Appointment
import ke.co.smartroundclinic.doctor.domain.model.AppointmentStatus
import ke.co.smartroundclinic.doctor.domain.model.DoctorAvailability
import ke.co.smartroundclinic.doctor.domain.model.ScheduleBreakBlock
import ke.co.smartroundclinic.doctor.presentation.common.composables.PrimaryButton
import ke.co.smartroundclinic.doctor.presentation.main.profile.ScheduleViewModel
import ke.co.smartroundclinic.doctor.presentation.theme.CardBackground
import ke.co.smartroundclinic.doctor.presentation.theme.Error40
import ke.co.smartroundclinic.doctor.presentation.theme.GradientEnd
import ke.co.smartroundclinic.doctor.presentation.theme.GradientStart
import ke.co.smartroundclinic.doctor.presentation.theme.Primary40
import ke.co.smartroundclinic.doctor.presentation.theme.Primary90
import ke.co.smartroundclinic.doctor.presentation.theme.ShapeCard
import ke.co.smartroundclinic.doctor.presentation.theme.ShapeInput
import ke.co.smartroundclinic.doctor.presentation.theme.ShapePill
import ke.co.smartroundclinic.doctor.presentation.theme.Tertiary40
import kotlinx.datetime.Instant
import kotlinx.datetime.LocalDate
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import org.koin.compose.viewmodel.koinViewModel
import kotlin.time.Clock


private val dayLabels = listOf("Mon", "Tue", "Wed", "Thu", "Fri", "Sat", "Sun")
private val calDayHeaders = listOf("M", "T", "W", "T", "F", "S", "S")
private val monthNames = listOf(
    "January", "February", "March", "April", "May", "June",
    "July", "August", "September", "October", "November", "December",
)
private enum class ScheduleTab { AVAILABILITY, CALENDAR }

private fun parseHhmm(value: String): Pair<Int, Int> {
    val parts = value.split(":")
    return Pair(parts[0].toIntOrNull() ?: 0, parts[1].toIntOrNull() ?: 0)
}

private fun formatHhmm(hour: Int, minute: Int): String {
    val h = hour.toString().padStart(2, '0')
    val m = minute.toString().padStart(2, '0')
    return "$h:$m"
}

private fun daysInMonth(year: Int, month: Int): Int {
    val nextM = if (month == 12) 1 else month + 1
    val nextY = if (month == 12) year + 1 else year
    return LocalDate(nextY, nextM, 1).dayOfMonth.let {
        // Go to last day of requested month
        LocalDate(year, month, 1).let { first ->
            // Count by finding the first day of next month minus 1
            var d = 28
            while (true) {
                try {
                    LocalDate(year, month, d + 1)
                    d++
                } catch (_: Exception) { break }
                if (d >= 31) break
            }
            d
        }
    }
}

@OptIn(ExperimentalLayoutApi::class, ExperimentalMaterial3Api::class)
@Composable
internal fun ScheduleManagementScreen(
    onBack: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: ScheduleViewModel = koinViewModel(),
) {
    val schedule by viewModel.schedule.collectAsState()
    val appointments by viewModel.appointments.collectAsState()
    val isSaving = viewModel.isSaving
    val isDeactivating = viewModel.isDeactivating
    val isLoading = viewModel.isLoading

    var selectedTab by remember { mutableStateOf(ScheduleTab.AVAILABILITY) }

    LaunchedEffect(selectedTab) { viewModel.refresh() }

    // Days that already have an active schedule on the server
    val scheduledDays = remember(schedule) { schedule.filter { it.isActive }.map { it.dayOfWeek }.toSet() }

    // Availability form state
    var selectedDay by remember { mutableStateOf<Int?>(null) }
    var windowStart by remember { mutableStateOf("08:00") }
    var windowEnd by remember { mutableStateOf("17:00") }
    var slotDuration by remember { mutableStateOf(30) }
    var breakBlocks by remember { mutableStateOf(listOf<ScheduleBreakBlock>()) }

    // Calendar state
    val today: LocalDate = remember { Instant.fromEpochMilliseconds(Clock.System.now().toEpochMilliseconds()).toLocalDateTime(TimeZone.currentSystemDefault()).date }
    var calYear by remember { mutableStateOf(today.year) }
    var calMonth by remember { mutableStateOf(today.monthNumber) }
    var selectedDate by remember { mutableStateOf<LocalDate?>(today) }

    // Pre-fill form once when schedule first loads
    var initialized by remember { mutableStateOf(false) }
    LaunchedEffect(schedule) {
        if (!initialized && schedule.isNotEmpty()) {
            initialized = true
            val active = schedule.filter { it.isActive }
            selectedDay = active.firstOrNull()?.dayOfWeek
            active.firstOrNull()?.let {
                windowStart = it.windowStart
                windowEnd = it.windowEnd
                slotDuration = it.slotDuration
                breakBlocks = it.breakBlocks
            }
        }
    }

    Scaffold(
        modifier = modifier,
        contentWindowInsets = WindowInsets(0),
    ) { paddingValues ->
        Column(modifier = Modifier.fillMaxSize().padding(paddingValues)) {
            ScheduleHeader(onBack = onBack)

            // Tab bar
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                ScheduleTab.entries.forEach { tab ->
                    val isSelected = selectedTab == tab
                    Box(
                        contentAlignment = Alignment.Center,
                        modifier = Modifier
                            .weight(1f)
                            .clip(ShapePill)
                            .background(if (isSelected) Primary40 else MaterialTheme.colorScheme.surfaceVariant)
                            .clickable(indication = null, interactionSource = remember { MutableInteractionSource() }) { selectedTab = tab }
                            .padding(vertical = 10.dp),
                    ) {
                        Text(
                            text = if (tab == ScheduleTab.AVAILABILITY) "Availability" else "Calendar",
                            style = MaterialTheme.typography.labelMedium,
                            color = if (isSelected) Color.White else MaterialTheme.colorScheme.onSurfaceVariant,
                        )
                    }
                }
            }

            HorizontalDivider(thickness = 0.5.dp, color = MaterialTheme.colorScheme.outlineVariant)

            if (isLoading && selectedTab == ScheduleTab.AVAILABILITY) {
                Box(modifier = Modifier.fillMaxWidth().padding(24.dp), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator(modifier = Modifier.size(32.dp), strokeWidth = 2.dp)
                }
            }

            when (selectedTab) {
                ScheduleTab.AVAILABILITY -> AvailabilityContent(
                    selectedDay = selectedDay,
                    scheduledDays = scheduledDays,
                    onDaySelect = { index -> selectedDay = if (selectedDay == index) null else index },
                    windowStart = windowStart,
                    onWindowStartChange = { windowStart = it },
                    windowEnd = windowEnd,
                    onWindowEndChange = { windowEnd = it },
                    breakBlocks = breakBlocks,
                    onAddBreak = { breakBlocks = breakBlocks + ScheduleBreakBlock("12:00", "13:00") },
                    onBreakStartChange = { i, v -> breakBlocks = breakBlocks.toMutableList().also { it[i] = it[i].copy(start = v) } },
                    onBreakEndChange = { i, v -> breakBlocks = breakBlocks.toMutableList().also { it[i] = it[i].copy(end = v) } },
                    onRemoveBreak = { i -> breakBlocks = breakBlocks.filterIndexed { idx, _ -> idx != i } },
                    slotDuration = slotDuration,
                    onSlotDurationChange = { slotDuration = it },
                    isSaving = isSaving,
                    isDeactivating = isDeactivating,
                    onSave = {
                        viewModel.saveSchedule(
                            enabledDays = scheduledDays + setOfNotNull(selectedDay),
                            windowStart = windowStart,
                            windowEnd = windowEnd,
                            slotDuration = slotDuration,
                            breakBlocks = breakBlocks,
                            onSuccess = {},
                        )
                    },
                    onDeactivate = {
                        selectedDay?.let { viewModel.deactivateSchedule(it) }
                    },
                )
                ScheduleTab.CALENDAR -> CalendarContent(
                    year = calYear,
                    month = calMonth,
                    today = today,
                    selectedDate = selectedDate,
                    schedule = schedule,
                    appointments = appointments,
                    onPrevMonth = {
                        if (calMonth == 1) { calMonth = 12; calYear-- } else calMonth--
                        selectedDate = null
                    },
                    onNextMonth = {
                        if (calMonth == 12) { calMonth = 1; calYear++ } else calMonth++
                        selectedDate = null
                    },
                    onDayClick = { selectedDate = it },
                )
            }
        }
    }
}

// ─── Availability Tab ─────────────────────────────────────────────────────────

@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun AvailabilityContent(
    selectedDay: Int?,
    scheduledDays: Set<Int>,
    onDaySelect: (Int) -> Unit,
    windowStart: String,
    onWindowStartChange: (String) -> Unit,
    windowEnd: String,
    onWindowEndChange: (String) -> Unit,
    slotDuration: Int,
    onSlotDurationChange: (Int) -> Unit,
    breakBlocks: List<ScheduleBreakBlock>,
    onAddBreak: () -> Unit,
    onBreakStartChange: (Int, String) -> Unit,
    onBreakEndChange: (Int, String) -> Unit,
    onRemoveBreak: (Int) -> Unit,
    isSaving: Boolean,
    isDeactivating: Boolean,
    onSave: () -> Unit,
    onDeactivate: () -> Unit,
) {
    val slotPresets = remember { listOf(10, 20, 30) }
    var useCustomSlot by remember { mutableStateOf(slotDuration !in listOf(10, 20, 30)) }
    var customSlotText by remember { mutableStateOf(if (slotDuration !in listOf(10, 20, 30)) slotDuration.toString() else "") }
    val customSlotValid = customSlotText.toIntOrNull()?.let { it in 5..60 } ?: false

    // Sync when slotDuration is set externally (pre-fill from server)
    LaunchedEffect(slotDuration) {
        if (slotDuration !in slotPresets && !useCustomSlot) {
            useCustomSlot = true
            customSlotText = slotDuration.toString()
        }
    }
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 16.dp),
    ) {
        Spacer(Modifier.height(16.dp))

        SectionLabel("Working Days")
        Spacer(Modifier.height(4.dp))
        Text(
            text = "Select the days you are available for appointments.",
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )
        Spacer(Modifier.height(10.dp))

        FlowRow(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            dayLabels.forEachIndexed { index, label ->
                DayChip(
                    label = label,
                    isSelected = index == selectedDay,
                    isScheduled = index in scheduledDays,
                    onClick = { onDaySelect(index) },
                )
            }
        }

        Spacer(Modifier.height(20.dp))
        HorizontalDivider(thickness = 0.5.dp, color = MaterialTheme.colorScheme.outlineVariant)
        Spacer(Modifier.height(16.dp))

        SectionLabel("Working Hours")
        Spacer(Modifier.height(4.dp))
        Text(
            text = "These hours apply to all selected working days.",
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )
        Spacer(Modifier.height(12.dp))

        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            TimePickerField(label = "Start time", value = windowStart, onValueChange = onWindowStartChange, modifier = Modifier.weight(1f))
            TimePickerField(label = "End time", value = windowEnd, onValueChange = onWindowEndChange, modifier = Modifier.weight(1f))
        }

        Spacer(Modifier.height(16.dp))

        SectionLabel("Slot Duration")
        Spacer(Modifier.height(4.dp))
        Text(
            text = "How long is each appointment slot?",
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )
        Spacer(Modifier.height(10.dp))
        FlowRow(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            slotPresets.forEach { preset ->
                DayChip(
                    label = "$preset min",
                    isSelected = !useCustomSlot && slotDuration == preset,
                    onClick = {
                        useCustomSlot = false
                        onSlotDurationChange(preset)
                    },
                )
            }
            DayChip(
                label = "Custom",
                isSelected = useCustomSlot,
                onClick = {
                    useCustomSlot = true
                    if (customSlotText.isEmpty()) customSlotText = slotDuration.toString()
                },
            )
        }
        if (useCustomSlot) {
            Spacer(Modifier.height(10.dp))
            OutlinedTextField(
                value = customSlotText,
                onValueChange = { input ->
                    customSlotText = input.filter { it.isDigit() }.take(2)
                    val v = customSlotText.toIntOrNull()
                    if (v != null && v in 5..60) onSlotDurationChange(v)
                },
                label = { Text("Duration (5–60 min)", style = MaterialTheme.typography.bodySmall) },
                isError = customSlotText.isNotEmpty() && !customSlotValid,
                supportingText = {
                    if (customSlotText.isNotEmpty() && !customSlotValid)
                        Text("Enter a value between 5 and 60")
                },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                singleLine = true,
                trailingIcon = {
                    Text("min", style = MaterialTheme.typography.bodySmall, modifier = Modifier.padding(end = 12.dp))
                },
                shape = ShapeInput,
                modifier = Modifier.fillMaxWidth(),
            )
        }

        Spacer(Modifier.height(20.dp))
        HorizontalDivider(thickness = 0.5.dp, color = MaterialTheme.colorScheme.outlineVariant)
        Spacer(Modifier.height(16.dp))

        Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.SpaceBetween) {
            Column {
                SectionLabel("Break Blocks")
                Text(
                    text = "e.g. lunch break — applies to all working days.",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }
            TextButton(onClick = onAddBreak) {
                Icon(imageVector = Icons.Outlined.Add, contentDescription = null, modifier = Modifier.size(16.dp))
                Spacer(Modifier.width(4.dp))
                Text("Add", style = MaterialTheme.typography.labelMedium)
            }
        }

        if (breakBlocks.isNotEmpty()) {
            Spacer(Modifier.height(8.dp))
            breakBlocks.forEachIndexed { index, block ->
                BreakBlockRow(
                    block = block,
                    onStartChange = { onBreakStartChange(index, it) },
                    onEndChange = { onBreakEndChange(index, it) },
                    onRemove = { onRemoveBreak(index) },
                )
                if (index < breakBlocks.lastIndex) Spacer(Modifier.height(8.dp))
            }
        }

        Spacer(Modifier.height(24.dp))

        PrimaryButton(
            onClick = onSave,
            enabled = selectedDay != null && !isSaving && !isDeactivating && (!useCustomSlot || customSlotValid),
        ) {
            if (isSaving) {
                CircularProgressIndicator(color = Color.White, modifier = Modifier.size(20.dp), strokeWidth = 2.dp)
            } else {
                Text(
                    text = "Save Availability",
                    style = MaterialTheme.typography.labelLarge,
                    color = Color.White,
                    modifier = Modifier.padding(vertical = 14.dp),
                )
            }
        }

        Spacer(Modifier.height(8.dp))

        TextButton(
            onClick = onDeactivate,
            enabled = selectedDay != null && !isSaving && !isDeactivating,
            modifier = Modifier.fillMaxWidth(),
        ) {
            if (isDeactivating) {
                CircularProgressIndicator(modifier = Modifier.size(18.dp), strokeWidth = 2.dp, color = Error40)
            } else {
                Text(
                    text = "Deactivate Schedule",
                    style = MaterialTheme.typography.labelLarge,
                    color = if (selectedDay != null) Error40 else MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }
        }

        Spacer(Modifier.height(16.dp))
    }
}

// ─── Calendar Tab ─────────────────────────────────────────────────────────────

@Composable
private fun CalendarContent(
    year: Int,
    month: Int,
    today: LocalDate,
    selectedDate: LocalDate?,
    schedule: List<DoctorAvailability>,
    appointments: List<Appointment>,
    onPrevMonth: () -> Unit,
    onNextMonth: () -> Unit,
    onDayClick: (LocalDate) -> Unit,
) {
    val workingDayOrdinals = remember(schedule) {
        schedule.filter { it.isActive }.map { it.dayOfWeek }.toSet()
    }
    val appointmentsByDate = remember(appointments) {
        appointments.groupBy { it.date }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState()),
    ) {
        // Month navigation
        Row(
            modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween,
        ) {
            IconButton(onClick = onPrevMonth) {
                Icon(imageVector = Icons.Outlined.ArrowBackIosNew, contentDescription = "Previous month", modifier = Modifier.size(18.dp))
            }
            Text(
                text = "${monthNames[month - 1]} $year",
                style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.SemiBold),
            )
            IconButton(onClick = onNextMonth) {
                Icon(imageVector = Icons.Outlined.ArrowForwardIos, contentDescription = "Next month", modifier = Modifier.size(18.dp))
            }
        }

        // Day-of-week header
        Row(modifier = Modifier.fillMaxWidth().padding(horizontal = 8.dp)) {
            calDayHeaders.forEach { h ->
                Text(
                    text = h,
                    modifier = Modifier.weight(1f),
                    textAlign = TextAlign.Center,
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }
        }

        Spacer(Modifier.height(4.dp))

        // Calendar grid
        MonthGrid(
            year = year,
            month = month,
            today = today,
            selectedDate = selectedDate,
            workingDayOrdinals = workingDayOrdinals,
            appointmentsByDate = appointmentsByDate,
            onDayClick = onDayClick,
        )

        // Selected day detail
        selectedDate?.let { date ->
            Spacer(Modifier.height(8.dp))
            HorizontalDivider(thickness = 0.5.dp, color = MaterialTheme.colorScheme.outlineVariant)
            DayDetailSection(
                date = date,
                schedule = schedule,
                appointments = appointmentsByDate[date.toString()] ?: emptyList(),
            )
        }

        Spacer(Modifier.height(24.dp))
    }
}

@Composable
private fun MonthGrid(
    year: Int,
    month: Int,
    today: LocalDate,
    selectedDate: LocalDate?,
    workingDayOrdinals: Set<Int>,
    appointmentsByDate: Map<String, List<Appointment>>,
    onDayClick: (LocalDate) -> Unit,
) {
    val firstDay = LocalDate(year, month, 1)
    // dayOfWeek.ordinal: 0=Mon (ISO), matches our API and UI chip indices
    val startOffset = firstDay.dayOfWeek.ordinal
    val daysCount = computeDaysInMonth(year, month)

    val totalCells = startOffset + daysCount
    val rows = (totalCells + 6) / 7

    Column(modifier = Modifier.padding(horizontal = 8.dp)) {
        for (row in 0 until rows) {
            Row(modifier = Modifier.fillMaxWidth()) {
                for (col in 0 until 7) {
                    val cellIndex = row * 7 + col
                    val dayNum = cellIndex - startOffset + 1
                    if (dayNum < 1 || dayNum > daysCount) {
                        Box(modifier = Modifier.weight(1f).aspectRatio(1f))
                    } else {
                        val date = LocalDate(year, month, dayNum)
                        val isWorking = date.dayOfWeek.ordinal in workingDayOrdinals
                        val apptCount = appointmentsByDate[date.toString()]?.size ?: 0
                        CalendarDayCell(
                            date = date,
                            isWorkingDay = isWorking,
                            appointmentCount = apptCount,
                            isSelected = date == selectedDate,
                            isToday = date == today,
                            onClick = { onDayClick(date) },
                            modifier = Modifier.weight(1f),
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun CalendarDayCell(
    date: LocalDate,
    isWorkingDay: Boolean,
    appointmentCount: Int,
    isSelected: Boolean,
    isToday: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val bgColor = when {
        isSelected -> Primary40
        isWorkingDay -> Primary90
        else -> Color.Transparent
    }
    val textColor = when {
        isSelected -> Color.White
        isToday -> Primary40
        isWorkingDay -> Primary40
        else -> MaterialTheme.colorScheme.onSurface
    }

    Box(
        modifier = modifier
            .aspectRatio(1f)
            .padding(2.dp)
            .clip(CircleShape)
            .background(bgColor)
            .clickable(indication = null, interactionSource = remember { MutableInteractionSource() }, onClick = onClick),
        contentAlignment = Alignment.Center,
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.Center) {
            Text(
                text = date.dayOfMonth.toString(),
                style = MaterialTheme.typography.labelSmall.copy(
                    fontWeight = if (isToday) FontWeight.Bold else FontWeight.Normal,
                ),
                color = textColor,
            )
            if (appointmentCount > 0) {
                Spacer(Modifier.height(1.dp))
                Box(
                    modifier = Modifier
                        .size(4.dp)
                        .background(if (isSelected) Color.White else Primary40, CircleShape),
                )
            }
        }
    }
}

@Composable
private fun DayDetailSection(
    date: LocalDate,
    schedule: List<DoctorAvailability>,
    appointments: List<Appointment>,
) {
    // dayOfWeek.ordinal: 0=Mon matches API's 0=Monday
    val dayEntry = schedule.find { it.dayOfWeek == date.dayOfWeek.ordinal }
    val dayName = dayLabels.getOrElse(date.dayOfWeek.ordinal) { "" }

    Column(modifier = Modifier.padding(horizontal = 16.dp, vertical = 12.dp)) {
        Text(
            text = "$dayName, ${monthNames[date.monthNumber - 1]} ${date.dayOfMonth}, ${date.year}",
            style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.SemiBold),
        )
        Spacer(Modifier.height(10.dp))

        Card(
            shape = ShapeCard,
            colors = CardDefaults.cardColors(containerColor = CardBackground),
            elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
            modifier = Modifier.fillMaxWidth(),
        ) {
            Column(modifier = Modifier.padding(12.dp)) {
                if (dayEntry != null && dayEntry.isActive) {
                    DetailRow(label = "Working hours", value = "${dayEntry.windowStart} – ${dayEntry.windowEnd}")
                    DetailRow(label = "Slot duration", value = "${dayEntry.slotDuration} min")
                    if (dayEntry.breakBlocks.isNotEmpty()) {
                        DetailRow(
                            label = "Breaks",
                            value = dayEntry.breakBlocks.joinToString("  •  ") { "${it.start} – ${it.end}" },
                        )
                    }
                } else {
                    Text(
                        text = "Off day — no appointments can be booked",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                }
            }
        }

        if (appointments.isNotEmpty()) {
            Spacer(Modifier.height(10.dp))
            Text(
                text = "Appointments (${appointments.size})",
                style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.SemiBold),
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
            Spacer(Modifier.height(6.dp))
            appointments.sortedBy { it.slotStart }.forEach { appt ->
                AppointmentRow(appointment = appt)
                Spacer(Modifier.height(6.dp))
            }
        }
    }
}

@Composable
private fun DetailRow(label: String, value: String) {
    Row(modifier = Modifier.fillMaxWidth().padding(vertical = 3.dp)) {
        Text(text = label, style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant, modifier = Modifier.width(96.dp))
        Text(text = value, style = MaterialTheme.typography.bodySmall)
    }
}

@Composable
private fun AppointmentRow(appointment: Appointment) {
    val (statusLabel, statusColor, statusBg) = when (appointment.status) {
        AppointmentStatus.BOOKED -> Triple("Booked", Tertiary40, Tertiary40.copy(alpha = 0.12f))
        AppointmentStatus.CONFIRMED -> Triple("Confirmed", Primary40, Primary40.copy(alpha = 0.12f))
        AppointmentStatus.COMPLETED -> Triple("Completed", Tertiary40, Tertiary40.copy(alpha = 0.12f))
        AppointmentStatus.CANCELLED -> Triple("Cancelled", Error40, Error40.copy(alpha = 0.12f))
        AppointmentStatus.NO_SHOW -> Triple("No-Show", Error40, Error40.copy(alpha = 0.12f))
    }
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        Text(
            text = "${appointment.slotStart} – ${appointment.slotEnd}",
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.width(96.dp),
        )
        Text(text = appointment.patientName, style = MaterialTheme.typography.bodySmall, modifier = Modifier.weight(1f))
        Box(
            modifier = Modifier
                .clip(ShapePill)
                .background(statusBg)
                .padding(horizontal = 8.dp, vertical = 2.dp),
        ) {
            Text(text = statusLabel, style = MaterialTheme.typography.labelSmall, color = statusColor)
        }
    }
}

// ─── Shared helpers ───────────────────────────────────────────────────────────

private fun computeDaysInMonth(year: Int, month: Int): Int {
    var d = 28
    while (d < 31) {
        try { LocalDate(year, month, d + 1); d++ } catch (_: Exception) { break }
    }
    return d
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun TimePickerField(
    label: String,
    value: String,
    onValueChange: (String) -> Unit,
    modifier: Modifier = Modifier,
) {
    var showPicker by remember { mutableStateOf(false) }
    val (initHour, initMinute) = remember(value) { parseHhmm(value) }
    val pickerState = rememberTimePickerState(initialHour = initHour, initialMinute = initMinute, is24Hour = true)

    if (showPicker) {
        AlertDialog(
            onDismissRequest = { showPicker = false },
            title = { Text(label, style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.SemiBold)) },
            text = {
                Box(modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
                    TimePicker(
                        state = pickerState,
                        colors = TimePickerDefaults.colors(
                            clockDialColor = MaterialTheme.colorScheme.surfaceVariant,
                            clockDialSelectedContentColor = Color.White,
                            clockDialUnselectedContentColor = MaterialTheme.colorScheme.onSurface,
                            selectorColor = Primary40,
                            containerColor = MaterialTheme.colorScheme.surface,
                            periodSelectorBorderColor = Primary40,
                            timeSelectorSelectedContainerColor = Primary40,
                            timeSelectorUnselectedContainerColor = MaterialTheme.colorScheme.surfaceVariant,
                            timeSelectorSelectedContentColor = Color.White,
                            timeSelectorUnselectedContentColor = MaterialTheme.colorScheme.onSurfaceVariant,
                        ),
                    )
                }
            },
            confirmButton = {
                TextButton(onClick = { onValueChange(formatHhmm(pickerState.hour, pickerState.minute)); showPicker = false }) {
                    Text("OK", color = Primary40)
                }
            },
            dismissButton = { TextButton(onClick = { showPicker = false }) { Text("Cancel") } },
            shape = RoundedCornerShape(20.dp),
        )
    }

    OutlinedTextField(
        value = value,
        onValueChange = {},
        readOnly = true,
        label = { Text(label, style = MaterialTheme.typography.bodySmall) },
        trailingIcon = { Icon(imageVector = Icons.Outlined.AccessTime, contentDescription = null, tint = Primary40) },
        shape = ShapeInput,
        modifier = modifier
            .fillMaxWidth()
            .clickable(indication = null, interactionSource = remember { MutableInteractionSource() }) { showPicker = true },
        enabled = false,
    )
}

@Composable
private fun BreakBlockRow(
    block: ScheduleBreakBlock,
    onStartChange: (String) -> Unit,
    onEndChange: (String) -> Unit,
    onRemove: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Row(modifier = modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
        TimePickerField(label = "From", value = block.start, onValueChange = onStartChange, modifier = Modifier.weight(1f))
        TimePickerField(label = "To", value = block.end, onValueChange = onEndChange, modifier = Modifier.weight(1f))
        IconButton(onClick = onRemove, modifier = Modifier.size(40.dp)) {
            Icon(imageVector = Icons.Outlined.Close, contentDescription = "Remove", tint = MaterialTheme.colorScheme.error, modifier = Modifier.size(20.dp))
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
                Icon(imageVector = Icons.AutoMirrored.Outlined.ArrowBack, contentDescription = "Back", tint = Color.White)
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
    Text(text = text, style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.SemiBold), modifier = modifier)
}

@Composable
private fun DayChip(
    label: String,
    isSelected: Boolean,
    isScheduled: Boolean = false,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val bgColor = when {
        isSelected  -> Primary40.copy(alpha = 0.10f)
        isScheduled -> Primary40
        else        -> MaterialTheme.colorScheme.surfaceVariant
    }
    val textColor = when {
        isSelected  -> Primary40
        isScheduled -> Color.White
        else        -> MaterialTheme.colorScheme.onSurfaceVariant
    }

    Box(
        contentAlignment = Alignment.Center,
        modifier = modifier
            .clip(ShapePill)
            .background(bgColor)
            .border(width = if (isSelected) 1.5.dp else 0.dp, color = if (isSelected) Primary40 else Color.Transparent, shape = ShapePill)
            .clickable(indication = null, interactionSource = remember { MutableInteractionSource() }, onClick = onClick)
            .padding(horizontal = 16.dp, vertical = 8.dp),
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.labelMedium.copy(fontWeight = if (isSelected || isScheduled) FontWeight.SemiBold else FontWeight.Normal),
            color = textColor,
        )
    }
}
