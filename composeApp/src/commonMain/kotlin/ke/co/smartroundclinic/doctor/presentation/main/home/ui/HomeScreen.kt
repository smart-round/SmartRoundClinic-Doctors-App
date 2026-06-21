package ke.co.smartroundclinic.doctor.presentation.main.home.ui

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.outlined.CalendarMonth
import androidx.compose.material.icons.outlined.ChatBubbleOutline
import androidx.compose.material.icons.outlined.Notifications
import androidx.compose.material.icons.outlined.Search
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import ke.co.smartroundclinic.doctor.presentation.main.notifications.NotificationsViewModel
import kotlinx.coroutines.launch
import androidx.compose.ui.Alignment
import androidx.compose.ui.layout.ContentScale
import coil3.compose.AsyncImage
import ke.co.smartroundclinic.doctor.domain.model.Appointment
import ke.co.smartroundclinic.doctor.domain.model.AppointmentStatus

import kotlinx.datetime.LocalDate
import ke.co.smartroundclinic.doctor.core.platform.todayDay
import ke.co.smartroundclinic.doctor.core.platform.todayMonth
import ke.co.smartroundclinic.doctor.core.platform.todayYear
import ke.co.smartroundclinic.doctor.presentation.main.bookings.BookingsViewModel
import ke.co.smartroundclinic.doctor.presentation.main.profile.PersonalInfoViewModel
import ke.co.smartroundclinic.doctor.presentation.main.profile.ScheduleViewModel
import org.koin.compose.viewmodel.koinViewModel
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import ke.co.smartroundclinic.doctor.presentation.common.composables.PrimaryButton
import ke.co.smartroundclinic.doctor.presentation.rememberSvgPainter
import ke.co.smartroundclinic.doctor.presentation.theme.CardBackground
import ke.co.smartroundclinic.doctor.presentation.theme.Error40
import ke.co.smartroundclinic.doctor.presentation.theme.GradientEnd
import ke.co.smartroundclinic.doctor.presentation.theme.GradientStart
import ke.co.smartroundclinic.doctor.presentation.theme.Primary40
import ke.co.smartroundclinic.doctor.presentation.theme.Primary90
import ke.co.smartroundclinic.doctor.presentation.theme.SearchBarOverlay
import ke.co.smartroundclinic.doctor.presentation.theme.Secondary40
import ke.co.smartroundclinic.doctor.presentation.theme.Secondary90
import ke.co.smartroundclinic.doctor.presentation.theme.ShapeCard
import ke.co.smartroundclinic.doctor.presentation.theme.ShapePill
import ke.co.smartroundclinic.doctor.presentation.theme.StatusPending
import ke.co.smartroundclinic.doctor.presentation.theme.Tertiary40
import kotlinx.datetime.DayOfWeek
import kotlinx.datetime.Month
import org.jetbrains.compose.resources.painterResource
import smartroundclinic.composeapp.generated.resources.Res
import smartroundclinic.composeapp.generated.resources.notification


private fun formatAppointmentDateTime(date: String, slotStart: String, slotEnd: String): String {
    val localDate = runCatching { LocalDate.parse(date) }.getOrNull()
    val dayAbbrev = when (localDate?.dayOfWeek) {
        DayOfWeek.MONDAY -> "Mon"
        DayOfWeek.TUESDAY -> "Tue"
        DayOfWeek.WEDNESDAY -> "Wed"
        DayOfWeek.THURSDAY -> "Thu"
        DayOfWeek.FRIDAY -> "Fri"
        DayOfWeek.SATURDAY -> "Sat"
        DayOfWeek.SUNDAY -> "Sun"
        null -> ""
    }
    val monthAbbrev = when (localDate?.month) {
        Month.JANUARY -> "Jan"; Month.FEBRUARY -> "Feb"; Month.MARCH -> "Mar"
        Month.APRIL -> "Apr"; Month.MAY -> "May"; Month.JUNE -> "Jun"
        Month.JULY -> "Jul"; Month.AUGUST -> "Aug"; Month.SEPTEMBER -> "Sep"
        Month.OCTOBER -> "Oct"; Month.NOVEMBER -> "Nov"; Month.DECEMBER -> "Dec"
        null -> ""
    }
    val day = localDate?.dayOfMonth ?: return "$date  $slotStart – $slotEnd"
    return "$dayAbbrev, $monthAbbrev $day  •  $slotStart – $slotEnd"
}

@Composable
fun HomeScreen(
    onProfileClick: () -> Unit = {},
    onNotificationsClick: () -> Unit = {},
    onSeeAllAppointments: () -> Unit = {},
    onSeeAllConsultations: () -> Unit = {},
    onOpenConsultation: (appointmentId: String, patientName: String) -> Unit = { _, _ -> },
    onViewAppointment: (appointmentId: String) -> Unit = {},
    onSetUpCalendar: () -> Unit = {},
    modifier: Modifier = Modifier,
    profileViewModel: PersonalInfoViewModel = koinViewModel(),
    bookingsViewModel: BookingsViewModel = koinViewModel(),
    scheduleViewModel: ScheduleViewModel = koinViewModel(),
    notificationsViewModel: NotificationsViewModel = koinViewModel(),
) {
    val user by profileViewModel.user.collectAsState()
    val allAppointments by bookingsViewModel.appointments.collectAsState()
    val schedule by scheduleViewModel.schedule.collectAsState()
    val scope = rememberCoroutineScope()
    val isRefreshing = profileViewModel.isRefreshing || bookingsViewModel.isLoading

    fun refresh() {
        scope.launch {
            profileViewModel.refreshUser()
            bookingsViewModel.loadAppointments()
            scheduleViewModel.refresh()
            notificationsViewModel.load()
        }
    }

    val today: LocalDate = remember {
        LocalDate(todayYear(), todayMonth(), todayDay())
    }
    val upcomingAppointments = remember(allAppointments, today) {
        allAppointments
            .filter { appt ->
                val apptDate = runCatching { LocalDate.parse(appt.date) }.getOrNull()
                apptDate != null && apptDate >= today &&
                        (appt.status == AppointmentStatus.BOOKED || appt.status == AppointmentStatus.CONFIRMED)
            }
            .sortedBy { it.date + it.slotStart }
            .take(3)
    }

    val isCalendarBlocked = schedule.none { it.isActive }

    val recentConsultations = remember(allAppointments) {
        allAppointments
            .filter { it.status == AppointmentStatus.CONFIRMED || it.status == AppointmentStatus.COMPLETED }
            .sortedByDescending { it.date + it.slotStart }
            .take(2)
    }

    Scaffold(
        modifier = modifier,
        topBar = {
            DashboardHeader(
                onProfileClick = onProfileClick,
                onNotificationsClick = onNotificationsClick,
                fullName = user?.fullName ?: "",
                profilePicture = user?.profilePicture,
                unreadNotifications = notificationsViewModel.unreadCount,
            )
        },
        contentWindowInsets = WindowInsets(0),
    ) { paddingValues ->
        PullToRefreshBox(
            isRefreshing = isRefreshing,
            onRefresh = ::refresh,
            modifier = Modifier.fillMaxSize().padding(paddingValues),
        ) {
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(horizontal = 16.dp, vertical = 16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp),
            ) {
                if (isCalendarBlocked) {
                    item { CalendarBlockedCard(onSetUpCalendar = onSetUpCalendar) }
                } else {
                    item {
                        AppointmentsSection(
                            appointments = upcomingAppointments,
                            onSeeAll = onSeeAllAppointments,
                            onViewAppointment = onViewAppointment,
                        )
                    }
                    item {
                        RecentMessagesSection(
                            consultations = recentConsultations,
                            onSeeAll = onSeeAllConsultations,
                            onOpenConsultation = onOpenConsultation,
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun DashboardHeader(
    onProfileClick: () -> Unit,
    onNotificationsClick: () -> Unit,
    fullName: String,
    profilePicture: String?,
    unreadNotifications: Int,
    modifier: Modifier = Modifier,
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .background(
                brush = Brush.horizontalGradient(
                    colors = listOf(
                        GradientStart,
                        GradientEnd
                    )
                )
            )
            .statusBarsPadding()
            .padding(horizontal = 16.dp, vertical = 12.dp),
    ) {
        Column {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(40.dp)
                        .clip(CircleShape)
                        .background(Color.White.copy(alpha = 0.25f))
                        .clickable(
                            indication = null,
                            interactionSource = remember { MutableInteractionSource() },
                            onClick = onProfileClick,
                        ),
                    contentAlignment = Alignment.Center,
                ) {
                    Icon(
                        imageVector = Icons.Filled.Person,
                        contentDescription = "Profile",
                        tint = Color.White,
                        modifier = Modifier.size(24.dp)
                    )
                    if (profilePicture != null) {
                        AsyncImage(
                            model = profilePicture,
                            contentDescription = "Profile picture",
                            contentScale = ContentScale.Crop,
                            modifier = Modifier.fillMaxSize(),
                        )
                    }
                }
                Spacer(Modifier.width(8.dp))
                val greeting =
                    if (fullName.isNotBlank()) "Hello Dr. $fullName 👋" else "Hello Doctor 👋"
                Text(
                    text = greeting,
                    style = MaterialTheme.typography.titleMedium,
                    color = Color.White,
                    modifier = Modifier.weight(1f)
                )
                Box(
                    modifier = Modifier.clickable(
                        enabled = true,
                        onClick = onNotificationsClick
                    )
                ) {
                    Icon(
                        painter = painterResource(Res.drawable.notification),
                        contentDescription = "Notifications",
                        tint = Color.White,
                        modifier = Modifier.size(24.dp)
                    )
                    if (unreadNotifications > 0) {
                        Box(
                            modifier = Modifier
                                .size(10.dp)
                                .clip(CircleShape)
                                .background(Color.Red)
                                .align(Alignment.TopEnd)
                                .offset(x=4.dp, y = (-14).dp)
                        )
                    }
                }
            }
            Spacer(Modifier.height(8.dp))
            Text(
                text = "A smarter way to expand access to healthcare",
                style = MaterialTheme.typography.bodySmall,
                color = Color.White.copy(alpha = 0.85f)
            )
        }
    }
}

@Composable
private fun CalendarBlockedCard(onSetUpCalendar: () -> Unit, modifier: Modifier = Modifier) {
    Card(
        modifier = modifier.fillMaxWidth(),
        shape = ShapeCard,
        colors = CardDefaults.cardColors(containerColor = CardBackground),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.Top,
            horizontalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            Box(
                modifier = Modifier.size(56.dp).clip(CircleShape).background(Primary90),
                contentAlignment = Alignment.Center,
            ) {
                Icon(
                    imageVector = Icons.Outlined.CalendarMonth,
                    contentDescription = null,
                    tint = Primary40,
                    modifier = Modifier.size(32.dp)
                )
            }
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = "Your Calendar Is Blocked",
                    style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                    color = MaterialTheme.colorScheme.onSurface
                )
                Spacer(Modifier.height(4.dp))
                Text(
                    text = "Patients can't book appointments until you set your availability.",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Spacer(Modifier.height(12.dp))
                PrimaryButton(onClick = onSetUpCalendar) {
                    Text(
                        text = "Set Up Your Calendar Now",
                        style = MaterialTheme.typography.labelMedium,
                        color = Color.White,
                        modifier = Modifier.padding(vertical = 10.dp)
                    )
                }
            }
        }
    }
}

@Composable
private fun AppointmentsSection(
    appointments: List<Appointment>,
    onSeeAll: () -> Unit,
    onViewAppointment: (appointmentId: String) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier) {
        SectionHeader(title = "Upcoming Appointments", onSeeAll = onSeeAll)
        Spacer(Modifier.height(8.dp))
        if (appointments.isEmpty()) {
            EmptyPlaceholder(
                icon = Icons.Outlined.CalendarMonth,
                title = "No Appointments Yet",
                subtitle = "Your consultation bookings will appear here"
            )
        } else {
            appointments.forEach { appt ->
                AppointmentCard(
                    dateTime = formatAppointmentDateTime(appt.date, appt.slotStart, appt.slotEnd),
                    patientName = appt.patientName,
                    patientProfilePicture = appt.patientProfilePicture,
                    isConfirmed = appt.status == AppointmentStatus.CONFIRMED,
                    onView = { onViewAppointment(appt.id) },
                )
                Spacer(Modifier.height(8.dp))
            }
        }
    }
}

@Composable
private fun RecentMessagesSection(
    consultations: List<Appointment>,
    onSeeAll: () -> Unit,
    onOpenConsultation: (appointmentId: String, patientName: String) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier) {
        SectionHeader(title = "Recent Messages", onSeeAll = onSeeAll)
        Spacer(Modifier.height(8.dp))
        if (consultations.isEmpty()) {
            EmptyPlaceholder(
                icon = Icons.Outlined.ChatBubbleOutline,
                title = "No Messages Yet",
                subtitle = "Messages will appear as soon as patients reach out"
            )
        } else {
            consultations.forEach { appt ->
                MessageRow(
                    senderName = appt.patientName,
                    preview = "Consultation  •  ${appt.slotStart} – ${appt.slotEnd}",
                    timestamp = appt.date,
                    profilePicture = appt.patientProfilePicture,
                    onClick = { onOpenConsultation(appt.id, appt.patientName) }
                )
                Spacer(Modifier.height(8.dp))
            }
        }
    }
}

@Composable
private fun SectionHeader(title: String, onSeeAll: () -> Unit, modifier: Modifier = Modifier) {
    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = title,
            style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold)
        )
        TextButton(onClick = onSeeAll, contentPadding = PaddingValues(horizontal = 4.dp)) {
            Text(text = "See All", color = Primary40, style = MaterialTheme.typography.labelMedium)
        }
    }
}

@Composable
private fun EmptyPlaceholder(
    icon: ImageVector,
    title: String,
    subtitle: String,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier.fillMaxWidth().padding(vertical = 24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        Box(
            modifier = Modifier.size(56.dp).clip(CircleShape).background(Primary90),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = Primary40,
                modifier = Modifier.size(28.dp)
            )
        }
        Text(
            text = title,
            style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold)
        )
        Text(
            text = subtitle,
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            textAlign = TextAlign.Center
        )
    }
}

@Composable
private fun AppointmentCard(
    dateTime: String,
    patientName: String,
    patientProfilePicture: String?,
    isConfirmed: Boolean,
    onView: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        shape = ShapeCard,
        colors = CardDefaults.cardColors(containerColor = CardBackground),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            Text(
                text = "Appointment Date/Time",
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Spacer(Modifier.height(2.dp))
            Text(
                text = dateTime,
                style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.SemiBold),
                color = Primary40
            )
            Spacer(Modifier.height(8.dp))
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                Box(
                    modifier = Modifier.size(8.dp).clip(CircleShape)
                        .background(if (isConfirmed) Primary40 else StatusPending)
                )
                Spacer(Modifier.width(8.dp))
                Box(
                    modifier = Modifier.size(32.dp).clip(CircleShape).background(Secondary90),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Filled.Person,
                        contentDescription = null,
                        tint = Secondary40,
                        modifier = Modifier.size(20.dp)
                    )
                    if (patientProfilePicture != null) {
                        AsyncImage(
                            model = patientProfilePicture,
                            contentDescription = null,
                            contentScale = ContentScale.Crop,
                            modifier = Modifier.fillMaxSize(),
                        )
                    }
                }
                Spacer(Modifier.width(8.dp))
                Text(
                    text = patientName,
                    style = MaterialTheme.typography.bodyMedium,
                    modifier = Modifier.weight(1f)
                )
                OutlinedButton(
                    onClick = onView,
                    shape = ShapePill,
                    contentPadding = PaddingValues(horizontal = 16.dp, vertical = 4.dp),
                    border = BorderStroke(1.dp, Tertiary40)
                ) {
                    Text(
                        text = "View",
                        style = MaterialTheme.typography.labelMedium,
                        color = Tertiary40
                    )
                }
            }
            if (isConfirmed) {
                Spacer(Modifier.height(4.dp))
                Text(
                    text = "Confirmed",
                    style = MaterialTheme.typography.labelSmall,
                    color = Primary40,
                    modifier = Modifier.padding(start = 16.dp)
                )
            }
        }
    }
}

@Composable
private fun MessageRow(
    senderName: String,
    preview: String,
    timestamp: String,
    profilePicture: String?,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        onClick = onClick,
        shape = ShapeCard,
        colors = CardDefaults.cardColors(containerColor = CardBackground),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Row(
            modifier = Modifier.padding(12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Box(
                modifier = Modifier.size(40.dp).clip(CircleShape).background(Secondary90),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Filled.Person,
                    contentDescription = null,
                    tint = Secondary40,
                    modifier = Modifier.size(24.dp)
                )
                if (profilePicture != null) {
                    AsyncImage(
                        model = profilePicture,
                        contentDescription = null,
                        contentScale = ContentScale.Crop,
                        modifier = Modifier.fillMaxSize(),
                    )
                }
            }
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = senderName,
                    style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.SemiBold)
                )
                Text(
                    text = preview,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }
            Text(
                text = timestamp,
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}
