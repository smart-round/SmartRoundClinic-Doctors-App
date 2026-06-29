package ke.co.smartroundclinic.doctor.presentation.main.bookings.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material.icons.filled.LockClock
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material.icons.filled.Watch
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage
import ke.co.smartroundclinic.doctor.domain.model.Appointment
import ke.co.smartroundclinic.doctor.domain.model.AppointmentStatus

import kotlinx.datetime.LocalDate
import ke.co.smartroundclinic.doctor.core.platform.todayDay
import ke.co.smartroundclinic.doctor.core.platform.todayMonth
import ke.co.smartroundclinic.doctor.core.platform.todayYear
import ke.co.smartroundclinic.doctor.presentation.common.composables.DashboardHeader
import ke.co.smartroundclinic.doctor.presentation.theme.CardBackground
import ke.co.smartroundclinic.doctor.presentation.theme.Error40
import ke.co.smartroundclinic.doctor.presentation.theme.Primary40
import ke.co.smartroundclinic.doctor.presentation.theme.Primary90
import ke.co.smartroundclinic.doctor.presentation.theme.Secondary40
import ke.co.smartroundclinic.doctor.presentation.theme.Secondary90
import ke.co.smartroundclinic.doctor.presentation.theme.ShapeCard
import ke.co.smartroundclinic.doctor.presentation.theme.ShapePill
import ke.co.smartroundclinic.doctor.presentation.theme.Tertiary40

internal enum class BookingTab(val label: String, val filter: String?) {
    UPCOMING("Upcoming", "upcoming"),
    PAST("Past", "past"),
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun BookingListScreen(
    appointments: List<Appointment>,
    isLoading: Boolean,
    onRefresh: () -> Unit,
    onBookingClick: (Appointment) -> Unit,
    selectedTab: BookingTab,
    onTabSelected: (BookingTab) -> Unit,
    onProfileClick: () -> Unit = {},
    onNotificationsClick: () -> Unit = {},
    modifier: Modifier = Modifier,
) {
    val today: LocalDate = remember { LocalDate(todayYear(), todayMonth(), todayDay()) }

    val pendingStatuses = setOf(AppointmentStatus.BOOKED, AppointmentStatus.CONFIRMED)
    val terminalStatuses = setOf(AppointmentStatus.COMPLETED, AppointmentStatus.CANCELLED, AppointmentStatus.NO_SHOW)
    val filtered = appointments
        .filter { appt ->
            val apptDate = runCatching { LocalDate.parse(appt.date) }.getOrNull() ?: return@filter false
            when (selectedTab) {
                BookingTab.UPCOMING -> apptDate >= today && appt.status in pendingStatuses
                BookingTab.PAST -> appt.status in terminalStatuses ||
                    (apptDate < today && appt.status in pendingStatuses)
            }
        }
        .sortedWith(
            when (selectedTab) {
                BookingTab.UPCOMING -> compareBy({ it.date }, { it.slotStart })
                BookingTab.PAST -> compareByDescending<Appointment> { it.date }.thenBy { it.slotStart }
            }
        )

    Scaffold(
        modifier = modifier,
        topBar = { DashboardHeader(title = "Bookings", onProfileClick = onProfileClick, onNotificationsClick = onNotificationsClick) },
        contentWindowInsets = WindowInsets(0),
    ) { paddingValues ->
        Column(modifier = Modifier.fillMaxSize().padding(paddingValues)) {
            BookingTabRow(selectedTab = selectedTab, onTabSelected = onTabSelected)

            PullToRefreshBox(
                isRefreshing = isLoading,
                onRefresh = onRefresh,
                modifier = Modifier.weight(1f),
            ) {
                if (filtered.isEmpty() && !isLoading) {
                    EmptyBookingsView(tab = selectedTab, modifier = Modifier.fillMaxSize())
                } else {
                    LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        contentPadding = PaddingValues(horizontal = 16.dp, vertical = 12.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp),
                    ) {
                        items(filtered, key = { it.id }) { appt ->
                            BookingCard(appointment = appt, onClick = { onBookingClick(appt) })
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun BookingTabRow(
    selectedTab: BookingTab,
    onTabSelected: (BookingTab) -> Unit,
    modifier: Modifier = Modifier,
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 12.dp)
            .shadow(elevation = 4.dp, shape = ShapePill)
            .clip(ShapePill)
            .background(MaterialTheme.colorScheme.surfaceVariant)
            .padding(4.dp),
    ) {
        Row {
            BookingTab.entries.forEach { tab ->
                val isSelected = selectedTab == tab
                Box(
                    contentAlignment = Alignment.Center,
                    modifier = Modifier
                        .weight(1f)
                        .clip(ShapePill)
                        .then(if (isSelected) Modifier.background(Color.White) else Modifier)
                        .clickable(
                            indication = null,
                            interactionSource = remember { MutableInteractionSource() },
                            onClick = { onTabSelected(tab) },
                        )
                        .padding(vertical = 10.dp),
                ) {
                    Text(
                        text = tab.label,
                        style = MaterialTheme.typography.labelMedium,
                        fontWeight = if (isSelected) FontWeight.SemiBold else FontWeight.Normal,
                        color = if (isSelected) MaterialTheme.colorScheme.onSurface else MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                }
            }
        }
    }
}

@Composable
private fun EmptyBookingsView(tab: BookingTab, modifier: Modifier = Modifier) {
    Column(
        modifier = modifier.fillMaxWidth().padding(horizontal = 32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
    ) {
        Box(modifier = Modifier.size(80.dp).clip(CircleShape).background(Primary90), contentAlignment = Alignment.Center) {
            Icon(imageVector = Icons.Filled.Person, contentDescription = null, tint = Primary40, modifier = Modifier.size(40.dp))
        }
        Spacer(Modifier.height(16.dp))
        Text(
            text = when (tab) {
                BookingTab.UPCOMING -> "No Upcoming Appointments"
                BookingTab.PAST -> "No Past Appointments"
            },
            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
            textAlign = TextAlign.Center,
        )
        Spacer(Modifier.height(8.dp))
        Text(
            text = when (tab) {
                BookingTab.UPCOMING -> "Booked and confirmed appointments for today and future dates will appear here"
                BookingTab.PAST -> "Completed, cancelled, and no-show appointments will appear here"
            },
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            textAlign = TextAlign.Center,
        )
    }
}

@Composable
private fun BookingCard(appointment: Appointment, onClick: () -> Unit, modifier: Modifier = Modifier) {
    val (dotColor, statusLabel, statusColor, statusBg) = when (appointment.status) {
        AppointmentStatus.BOOKED -> Quadruple(Tertiary40, "Booked", Tertiary40, Tertiary40.copy(alpha = 0.12f))
        AppointmentStatus.CONFIRMED -> Quadruple(Primary40, "Confirmed", Primary40, Primary40.copy(alpha = 0.12f))
        AppointmentStatus.COMPLETED -> Quadruple(Tertiary40, "Completed", Tertiary40, Tertiary40.copy(alpha = 0.12f))
        AppointmentStatus.CANCELLED -> Quadruple(Error40, "Cancelled", Error40, Error40.copy(alpha = 0.12f))
        AppointmentStatus.NO_SHOW -> Quadruple(Error40, "No-Show", Error40, Error40.copy(alpha = 0.12f))
    }
    Card(
        onClick = onClick,
        modifier = modifier.fillMaxWidth(),
        shape = ShapeCard,
        colors = CardDefaults.cardColors(containerColor = CardBackground),
        elevation = CardDefaults.cardElevation(defaultElevation = 3.dp, pressedElevation = 6.dp),
    ) {
        Row(modifier = Modifier.height(IntrinsicSize.Min)) {
            Box(
                modifier = Modifier
                    .width(4.dp)
                    .fillMaxHeight()
                    .background(Primary40),
            )
            Column(modifier = Modifier.weight(1f).padding(horizontal = 12.dp, vertical = 10.dp)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = "Appointment Date/Time",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.weight(1f),
                    )
                    Box(
                        modifier = Modifier
                            .clip(ShapePill)
                            .background(statusBg)
                            .padding(horizontal = 8.dp, vertical = 2.dp),
                    ) {
                        Text(text = statusLabel, style = MaterialTheme.typography.labelSmall, color = statusColor)
                    }
                }
                Spacer(Modifier.height(4.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Filled.Schedule,
                        contentDescription = null,
                        tint = Primary40,
                        modifier = Modifier.size(13.dp),
                    )
                    Spacer(Modifier.width(4.dp))
                    Text(
                        text = "${appointment.date}  ·  ${appointment.slotStart} – ${appointment.slotEnd}",
                        style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Medium),
                        color = Primary40,
                    )
                }
                Spacer(Modifier.height(10.dp))
                Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.fillMaxWidth()) {
                    Box(
                        modifier = Modifier.size(36.dp).clip(CircleShape).background(Secondary90),
                        contentAlignment = Alignment.Center,
                    ) {
                        if (appointment.patientProfilePicture != null) {
                            AsyncImage(
                                model = appointment.patientProfilePicture,
                                contentDescription = appointment.patientName,
                                contentScale = ContentScale.Crop,
                                modifier = Modifier.fillMaxSize().clip(CircleShape),
                            )
                        } else {
                            Icon(
                                imageVector = Icons.Filled.Person,
                                contentDescription = null,
                                tint = Secondary40,
                                modifier = Modifier.size(20.dp),
                            )
                        }
                    }
                    Spacer(Modifier.width(8.dp))
                    Text(
                        text = appointment.patientName,
                        style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.SemiBold),
                        modifier = Modifier.weight(1f),
                    )
                    OutlinedButton(
                        onClick = onClick,
                        shape = MaterialTheme.shapes.small,
                        contentPadding = PaddingValues(horizontal = 16.dp, vertical = 4.dp),
                        border = androidx.compose.foundation.BorderStroke(1.dp, Primary40),
                    ) {
                        Text(text = "View", style = MaterialTheme.typography.labelMedium, color = Primary40, modifier = Modifier.padding(horizontal = 8.dp))
                    }
                }
            }
        }
    }
}

private data class Quadruple<A, B, C, D>(val first: A, val second: B, val third: C, val fourth: D)
