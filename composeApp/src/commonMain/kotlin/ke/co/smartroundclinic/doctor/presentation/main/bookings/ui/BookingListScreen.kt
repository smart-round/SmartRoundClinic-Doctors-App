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
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.TabRowDefaults.SecondaryIndicator
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
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
import ke.co.smartroundclinic.doctor.domain.model.Referral

import kotlinx.datetime.LocalDate
import ke.co.smartroundclinic.doctor.core.platform.todayDay
import ke.co.smartroundclinic.doctor.core.platform.todayMonth
import ke.co.smartroundclinic.doctor.core.platform.todayYear
import ke.co.smartroundclinic.doctor.presentation.common.composables.DashboardHeader
import ke.co.smartroundclinic.doctor.presentation.theme.CardBackground
import ke.co.smartroundclinic.doctor.presentation.theme.DetailIconChip
import ke.co.smartroundclinic.doctor.presentation.theme.Error40
import ke.co.smartroundclinic.doctor.presentation.theme.Primary40
import ke.co.smartroundclinic.doctor.presentation.theme.Primary90
import ke.co.smartroundclinic.doctor.presentation.theme.Secondary40
import ke.co.smartroundclinic.doctor.presentation.theme.Secondary90
import ke.co.smartroundclinic.doctor.presentation.theme.ShapeCard
import ke.co.smartroundclinic.doctor.presentation.theme.ShapePill
import ke.co.smartroundclinic.doctor.presentation.theme.Tertiary40
import ke.co.smartroundclinic.doctor.presentation.theme.Tertiary90

internal enum class BookingTab(val label: String, val filter: String?) {
    UPCOMING("Upcoming", "upcoming"),
    PAST("Past", "past"),
}

/** Consultation (default) holds the existing Upcoming/Past sub-tabs unchanged; Referral is a
 * sibling showing this doctor's own referral list — both directions. */
internal enum class BookingTopTab(val label: String) {
    CONSULTATION("Consultations"),
    REFERRAL("Referrals"),
}

/** Referral tab sub-tabs: patients referred to this doctor vs. patients this doctor referred out. */
internal enum class ReferralSubTab(val label: String) {
    RECEIVED("Received"),
    SENT("Sent"),
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun BookingListScreen(
    appointments: List<Appointment>,
    isLoading: Boolean,
    onRefresh: () -> Unit,
    onBookingClick: (Appointment) -> Unit,
    selectedTopTab: BookingTopTab,
    onTopTabSelected: (BookingTopTab) -> Unit,
    selectedTab: BookingTab,
    onTabSelected: (BookingTab) -> Unit,
    receivedReferrals: List<Referral>,
    sentReferrals: List<Referral>,
    isLoadingReceivedReferrals: Boolean,
    isLoadingSentReferrals: Boolean,
    selectedReferralSubTab: ReferralSubTab,
    onReferralSubTabSelected: (ReferralSubTab) -> Unit,
    onReferralClick: (Referral) -> Unit,
    onRefreshReferrals: () -> Unit,
    onProfileClick: () -> Unit = {},
    onNotificationsClick: () -> Unit = {},
    modifier: Modifier = Modifier,
) {
    val today: LocalDate = remember { LocalDate(todayYear(), todayMonth(), todayDay()) }

    val pendingStatuses = setOf(AppointmentStatus.BOOKED, AppointmentStatus.CONFIRMED)
    val terminalStatuses = setOf(AppointmentStatus.COMPLETED, AppointmentStatus.CANCELLED, AppointmentStatus.NO_SHOW)
    val consultationFiltered = appointments
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

    val selectedIndex = BookingTopTab.entries.indexOf(selectedTopTab)

    Scaffold(
        modifier = modifier,
        topBar = {
            DashboardHeader(
                title = "Bookings",
                onProfileClick = onProfileClick,
                onNotificationsClick = onNotificationsClick,
                bottomContent = {
                    TabRow(
                        selectedTabIndex = selectedIndex,
                        containerColor = Color.Transparent,
                        contentColor = Color.White,
                        indicator = { tabPositions ->
                            SecondaryIndicator(
                                modifier = Modifier.tabIndicatorOffset(tabPositions[selectedIndex]),
                                color = Color.White,
                            )
                        },
                        divider = {},
                    ) {
                        BookingTopTab.entries.forEachIndexed { index, tab ->
                            Tab(
                                selected = selectedIndex == index,
                                onClick = { onTopTabSelected(tab) },
                                text = {
                                    Text(
                                        text = tab.label,
                                        style = MaterialTheme.typography.labelMedium,
                                        color = if (selectedIndex == index) Color.White else Color.White.copy(alpha = 0.65f),
                                    )
                                },
                            )
                        }
                    }
                },
            )
        },
        contentWindowInsets = WindowInsets(0),
    ) { paddingValues ->
        Column(modifier = Modifier.fillMaxSize().padding(paddingValues)) {
            when (selectedTopTab) {
                BookingTopTab.CONSULTATION -> {
                    BookingTabRow(selectedTab = selectedTab, onTabSelected = onTabSelected)
                    PullToRefreshBox(
                        isRefreshing = isLoading,
                        onRefresh = onRefresh,
                        modifier = Modifier.weight(1f),
                    ) {
                        if (consultationFiltered.isEmpty() && !isLoading) {
                            EmptyBookingsView(tab = selectedTab, modifier = Modifier.fillMaxSize())
                        } else {
                            LazyColumn(
                                modifier = Modifier.fillMaxSize(),
                                contentPadding = PaddingValues(horizontal = 16.dp, vertical = 12.dp),
                                verticalArrangement = Arrangement.spacedBy(12.dp),
                            ) {
                                items(consultationFiltered, key = { it.id }) { appt ->
                                    BookingCard(appointment = appt, onClick = { onBookingClick(appt) })
                                }
                            }
                        }
                    }
                }
                BookingTopTab.REFERRAL -> {
                    ReferralSubTabRow(selectedTab = selectedReferralSubTab, onTabSelected = onReferralSubTabSelected)
                    val (listForSubTab, isLoadingSubTab) = when (selectedReferralSubTab) {
                        ReferralSubTab.RECEIVED -> receivedReferrals to isLoadingReceivedReferrals
                        ReferralSubTab.SENT -> sentReferrals to isLoadingSentReferrals
                    }
                    PullToRefreshBox(
                        isRefreshing = isLoadingSubTab,
                        onRefresh = onRefreshReferrals,
                        modifier = Modifier.weight(1f),
                    ) {
                        if (listForSubTab.isEmpty() && !isLoadingSubTab) {
                            EmptyReferralsView(subTab = selectedReferralSubTab, modifier = Modifier.fillMaxSize())
                        } else {
                            LazyColumn(
                                modifier = Modifier.fillMaxSize(),
                                contentPadding = PaddingValues(horizontal = 16.dp, vertical = 12.dp),
                                verticalArrangement = Arrangement.spacedBy(12.dp),
                            ) {
                                items(listForSubTab, key = { it.id }) { referral ->
                                    ReferralCard(
                                        referral = referral,
                                        direction = selectedReferralSubTab,
                                        // Sent referrals are read-only — nothing to do once it's been
                                        // handed off to another doctor, so the card isn't clickable at all.
                                        onClick = if (selectedReferralSubTab == ReferralSubTab.RECEIVED) {
                                            { onReferralClick(referral) }
                                        } else null,
                                    )
                                }
                            }
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
private fun ReferralSubTabRow(
    selectedTab: ReferralSubTab,
    onTabSelected: (ReferralSubTab) -> Unit,
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
            ReferralSubTab.entries.forEach { tab ->
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
private fun EmptyReferralsView(subTab: ReferralSubTab, modifier: Modifier = Modifier) {
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
            text = if (subTab == ReferralSubTab.RECEIVED) "No Referrals Received" else "No Referrals Sent",
            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
            textAlign = TextAlign.Center,
        )
        Spacer(Modifier.height(8.dp))
        Text(
            text = if (subTab == ReferralSubTab.RECEIVED) {
                "Patients other doctors refer to you will appear here as soon as the referral is made"
            } else {
                "Patients you refer to other doctors will appear here"
            },
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            textAlign = TextAlign.Center,
        )
    }
}

@Composable
private fun ReferralCard(referral: Referral, direction: ReferralSubTab, onClick: (() -> Unit)?, modifier: Modifier = Modifier) {
    val (statusLabel, statusColor, statusBg) = when (referral.status.uppercase()) {
        "ACCEPTED" -> Triple("Accepted", Tertiary40, Tertiary40.copy(alpha = 0.12f))
        "DECLINED" -> Triple("Declined", Error40, Error40.copy(alpha = 0.12f))
        else -> Triple("Pending", Primary40, Primary40.copy(alpha = 0.12f))
    }
    val counterpartName = if (direction == ReferralSubTab.RECEIVED) referral.referringDoctorName else referral.receivingDoctorName
    val counterpartPicture = if (direction == ReferralSubTab.RECEIVED) referral.referringDoctorPicture else referral.receivingDoctorPicture
    val isBooked = referral.resultingAppointmentId != null
    val cardColors = CardDefaults.cardColors(
        containerColor = CardBackground,
        disabledContainerColor = CardBackground,
        contentColor = MaterialTheme.colorScheme.onSurface,
        disabledContentColor = MaterialTheme.colorScheme.onSurface,
    )
    val cardElevation = CardDefaults.cardElevation(defaultElevation = 3.dp, pressedElevation = 6.dp)
    val cardContent: @Composable () -> Unit = {
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
                        text = if (direction == ReferralSubTab.RECEIVED) "Referred by" else "Referred to",
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
                Spacer(Modifier.height(6.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier.size(32.dp).clip(CircleShape).background(Tertiary90),
                        contentAlignment = Alignment.Center,
                    ) {
                        if (counterpartPicture != null) {
                            AsyncImage(
                                model = counterpartPicture,
                                contentDescription = counterpartName,
                                contentScale = ContentScale.Crop,
                                modifier = Modifier.fillMaxSize().clip(CircleShape),
                            )
                        } else {
                            Icon(imageVector = Icons.Filled.Person, contentDescription = null, tint = Tertiary40, modifier = Modifier.size(18.dp))
                        }
                    }
                    Spacer(Modifier.width(8.dp))
                    Text(
                        text = "Dr. ${counterpartName ?: "Unknown"}",
                        style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.SemiBold),
                        modifier = Modifier.weight(1f),
                    )
                }
                Spacer(Modifier.height(8.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier.size(20.dp).clip(CircleShape).background(Secondary90),
                        contentAlignment = Alignment.Center,
                    ) {
                        if (referral.patientProfilePicture != null) {
                            AsyncImage(
                                model = referral.patientProfilePicture,
                                contentDescription = referral.patientName,
                                contentScale = ContentScale.Crop,
                                modifier = Modifier.fillMaxSize().clip(CircleShape),
                            )
                        } else {
                            Icon(imageVector = Icons.Filled.Person, contentDescription = null, tint = Secondary40, modifier = Modifier.size(12.dp))
                        }
                    }
                    Spacer(Modifier.width(6.dp))
                    Text(
                        text = referral.patientName ?: "Patient",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                }
                // Referrals created before the reason screen was removed still carry one.
                if (referral.reason.isNotBlank()) {
                    Spacer(Modifier.height(6.dp))
                    Text(
                        text = referral.reason,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                }
                Spacer(Modifier.height(8.dp))
                Text(
                    text = when {
                        isBooked -> if (onClick != null) "Booked — tap to view appointment" else "Booked"
                        referral.status.uppercase() == "DECLINED" -> "Declined by patient"
                        referral.status.uppercase() == "ACCEPTED" -> "Accepted — awaiting booking"
                        else -> "Awaiting patient response"
                    },
                    style = MaterialTheme.typography.labelSmall,
                    color = if (isBooked) Primary40 else MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }
        }
    }

    if (onClick != null) {
        Card(
            onClick = onClick,
            enabled = isBooked,
            modifier = modifier.fillMaxWidth(),
            shape = ShapeCard,
            colors = cardColors,
            elevation = cardElevation,
            content = { cardContent() },
        )
    } else {
        Card(
            modifier = modifier.fillMaxWidth(),
            shape = ShapeCard,
            colors = cardColors,
            elevation = cardElevation,
            content = { cardContent() },
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
                if (appointment.referralId != null) {
                    Spacer(Modifier.height(8.dp))
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier.size(20.dp).clip(CircleShape).background(Tertiary90),
                            contentAlignment = Alignment.Center,
                        ) {
                            if (appointment.referredByDoctorPicture != null) {
                                AsyncImage(
                                    model = appointment.referredByDoctorPicture,
                                    contentDescription = appointment.referredByDoctorName,
                                    contentScale = ContentScale.Crop,
                                    modifier = Modifier.fillMaxSize().clip(CircleShape),
                                )
                            } else {
                                Icon(
                                    imageVector = Icons.Filled.Person,
                                    contentDescription = null,
                                    tint = Tertiary40,
                                    modifier = Modifier.size(12.dp),
                                )
                            }
                        }
                        Spacer(Modifier.width(6.dp))
                        Text(
                            text = "Referred by: ${appointment.referredByDoctorName ?: "another doctor"}",
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                        )
                    }
                }
                // Splits the card in half: appointment date/time above, patient and View below —
                // the same hairline the appointment detail card draws under its date/time row.
                HorizontalDivider(
                    modifier = Modifier.padding(vertical = 10.dp),
                    thickness = 1.dp,
                    color = DetailIconChip.copy(alpha = 0.26f),
                )
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
