package ke.co.smartroundclinic.doctor.presentation.main

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.AccountBalanceWallet
import androidx.compose.material.icons.outlined.HourglassTop
import androidx.compose.material.icons.outlined.MedicalServices
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.ModalBottomSheetProperties
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SheetValue
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.retain.retain
import androidx.compose.runtime.setValue
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.LocalLifecycleOwner
import ke.co.smartroundclinic.doctor.presentation.main.profile.destinations.LicenceManagement
import ke.co.smartroundclinic.doctor.presentation.main.profile.destinations.ProfileList
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.navigation3.runtime.NavKey
import androidx.navigation3.runtime.entryProvider
import androidx.navigation3.ui.NavDisplay
import ke.co.smartroundclinic.doctor.core.notification.NotificationDeepLink
import ke.co.smartroundclinic.doctor.core.notification.NotificationEvent
import ke.co.smartroundclinic.doctor.presentation.main.articles.ArticlesRoot
import ke.co.smartroundclinic.doctor.presentation.main.bookings.BookingsRoot
import ke.co.smartroundclinic.doctor.presentation.main.chat.ChatRoot
import ke.co.smartroundclinic.doctor.presentation.main.chat.destinations.Call
import ke.co.smartroundclinic.doctor.presentation.main.chat.destinations.Conversation
import ke.co.smartroundclinic.doctor.presentation.main.destinations.Articles
import ke.co.smartroundclinic.doctor.presentation.main.destinations.Bookings
import ke.co.smartroundclinic.doctor.presentation.main.destinations.Chat
import ke.co.smartroundclinic.doctor.presentation.main.destinations.Home
import ke.co.smartroundclinic.doctor.presentation.main.destinations.Services
import ke.co.smartroundclinic.doctor.presentation.main.destinations.Wallet
import ke.co.smartroundclinic.doctor.presentation.main.home.HomeRoot
import ke.co.smartroundclinic.doctor.presentation.main.home.destinations.Notifications
import ke.co.smartroundclinic.doctor.presentation.main.profile.PersonalInfoViewModel
import ke.co.smartroundclinic.doctor.presentation.main.services.ServicesRoot
import ke.co.smartroundclinic.doctor.presentation.main.wallet.WalletRoot
import ke.co.smartroundclinic.doctor.presentation.rememberSvgPainter
import ke.co.smartroundclinic.doctor.core.snackbar.SnackbarController
import ke.co.smartroundclinic.doctor.presentation.theme.Error40
import ke.co.smartroundclinic.doctor.presentation.theme.GradientEnd
import ke.co.smartroundclinic.doctor.presentation.theme.GradientStart
import ke.co.smartroundclinic.doctor.presentation.theme.Primary40
import org.jetbrains.compose.resources.DrawableResource
import org.jetbrains.compose.resources.painterResource
import org.koin.compose.koinInject
import org.koin.compose.viewmodel.koinViewModel
import androidx.compose.runtime.collectAsState
import smartroundclinic.composeapp.generated.resources.Res
import smartroundclinic.composeapp.generated.resources.bottom_bar_bookings
import smartroundclinic.composeapp.generated.resources.bottom_bar_chat
import smartroundclinic.composeapp.generated.resources.bottom_bar_cost_articles
import smartroundclinic.composeapp.generated.resources.bottom_bar_home

private sealed class TabIcon {
    class Vector(val icon: ImageVector) : TabIcon()
    class Drawable(val res: DrawableResource) : TabIcon()
}

private data class BottomTab(
    val destination: NavKey,
    val label: String,
    val icon: TabIcon,
)

private val tabs = listOf(
    BottomTab(Home, "Home", TabIcon.Drawable(Res.drawable.bottom_bar_home)),
    BottomTab(Services, "Services", TabIcon.Vector(Icons.Outlined.MedicalServices)),
    BottomTab(Bookings, "Bookings", TabIcon.Drawable(Res.drawable.bottom_bar_bookings)),
    BottomTab(Articles, "Articles", TabIcon.Drawable(Res.drawable.bottom_bar_cost_articles)),
    BottomTab(Chat, "Chat", TabIcon.Drawable(Res.drawable.bottom_bar_chat)),
    BottomTab(Wallet, "Wallet", TabIcon.Vector(Icons.Outlined.AccountBalanceWallet))
)

@Composable
fun MainRoot(modifier: Modifier = Modifier, onSignOut: () -> Unit = {}) {
    val backStack = retain { mutableStateListOf<NavKey>(Home) }
    val currentTab = backStack.lastOrNull() ?: Home

    var isAtRoot by remember { mutableStateOf(true) }
    var pendingHomeDestinations by remember { mutableStateOf<List<NavKey>>(emptyList()) }
    var pendingChatConversation by remember { mutableStateOf<Conversation?>(null) }
    var pendingCall by remember { mutableStateOf<Call?>(null) }
    var pendingBookingId by remember { mutableStateOf<String?>(null) }
    var pendingSupportTicketId by remember { mutableStateOf<String?>(null) }
    var inComplianceFixMode by remember { mutableStateOf(false) }

    fun selectTab(dest: NavKey) {
        if (currentTab != dest) {
            backStack.clear()
            backStack.add(dest)
            isAtRoot = true
        }
    }

    LaunchedEffect(isAtRoot) {
        if (isAtRoot && inComplianceFixMode) {
            inComplianceFixMode = false
        }
    }

    LaunchedEffect(Unit) {
        NotificationDeepLink.pendingEvent.collect { event ->
            if (event == null) return@collect
            NotificationDeepLink.consume()
            when (event) {
                is NotificationEvent.ToNotifications -> {
                    selectTab(Home)
                    pendingHomeDestinations = listOf(Notifications)
                }
                is NotificationEvent.ToBookingDetail -> {
                    selectTab(Bookings)
                    pendingBookingId = event.appointmentId
                }
                is NotificationEvent.ToConversation -> {
                    selectTab(Chat)
                    pendingChatConversation = Conversation(event.patientId, event.patientName, event.appointmentId)
                }
                is NotificationEvent.ToCall -> {
                    selectTab(Chat)
                    pendingChatConversation = Conversation(event.patientId, event.patientName, event.appointmentId)
                    pendingCall = Call(event.patientId, isVideo = true)
                }
                is NotificationEvent.ToArticles -> selectTab(Articles)
                is NotificationEvent.ToSupportTicket -> {
                    selectTab(Home)
                    pendingSupportTicketId = event.ticketId
                }
            }
        }
    }

    val complianceViewModel: ComplianceViewModel = koinViewModel()
    val complianceStatus = complianceViewModel.complianceStatus
    val showComplianceDialog = complianceStatus != null && !complianceStatus.isApproved && !inComplianceFixMode

    // Re-check compliance status every time the app comes back to the foreground — a doctor's
    // account can be rejected/suspended by an admin at any time, so re-fetching on every resume
    // (rather than only once at cold start) is what lets the blocking sheet re-appear as soon as
    // possible instead of waiting for the doctor to happen to tap "Check Status" themselves.
    val lifecycleOwner = LocalLifecycleOwner.current
    DisposableEffect(lifecycleOwner, complianceViewModel) {
        val observer = LifecycleEventObserver { _, event ->
            if (event == Lifecycle.Event.ON_START) complianceViewModel.checkStatus()
        }
        lifecycleOwner.lifecycle.addObserver(observer)
        onDispose { lifecycleOwner.lifecycle.removeObserver(observer) }
    }

    Scaffold(
        modifier = modifier.fillMaxSize(),
        bottomBar = { if (isAtRoot) BottomNavBar(currentTab = currentTab, onTabSelected = ::selectTab) },
        contentWindowInsets = WindowInsets(0, 0, 0, 0),
    ) { paddingValues ->
        NavDisplay(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues),
            backStack = backStack,
            onBack = {
                if (currentTab != Home) {
                    backStack.clear()
                    backStack.add(Home)
                    isAtRoot = true
                }
            },
            entryProvider = entryProvider {
                entry<Home> {
                    HomeRoot(
                        onAtRootChanged = { isAtRoot = it },
                        onSignOut = onSignOut,
                        onSeeAllAppointments = { selectTab(Bookings) },
                        onSeeAllConsultations = { selectTab(Chat) },
                        onViewAppointment = { appointmentId ->
                            selectTab(Bookings)
                            pendingBookingId = appointmentId
                        },
                        onOpenConsultation = { appointmentId, patientId, patientName ->
                            selectTab(Chat)
                            pendingChatConversation = Conversation(patientId, patientName, appointmentId)
                        },
                        pendingDestinations = pendingHomeDestinations,
                        onPendingNavigated = { pendingHomeDestinations = emptyList() },
                        pendingSupportTicketId = pendingSupportTicketId,
                        onPendingSupportTicketNavigated = { pendingSupportTicketId = null },
                    )
                }
                entry<Services> {
                    val personalInfoViewModel: PersonalInfoViewModel = koinViewModel()
                    val user by personalInfoViewModel.user.collectAsState()
                    val snackbarController: SnackbarController = koinInject()
                    ServicesRoot(
                        selfDoctorId = user?.id,
                        onAtRootChanged = { isAtRoot = it },
                        onProfileClick = { selectTab(Home); pendingHomeDestinations = listOf(ProfileList) },
                        onNotificationsClick = { selectTab(Home); pendingHomeDestinations = listOf(Notifications) },
                        // Doctor-to-doctor chat lands in a follow-up change (CR-SMRC-0001 round 1,
                        // Flow 4) — until then, Connect just confirms the doctor was found.
                        onPrimaryAction = { doctor -> snackbarController.show("Doctor-to-doctor chat is coming soon") },
                    )
                }
                entry<Bookings> {
                    BookingsRoot(
                        onAtRootChanged = { isAtRoot = it },
                        pendingBookingId = pendingBookingId,
                        onPendingNavigated = { pendingBookingId = null },
                        onProfileClick = { selectTab(Home); pendingHomeDestinations = listOf(ProfileList) },
                        onNotificationsClick = { selectTab(Home); pendingHomeDestinations = listOf(Notifications) },
                    )
                }
                entry<Articles> {
                    ArticlesRoot(
                        onAtRootChanged = { isAtRoot = it },
                        onProfileClick = { selectTab(Home); pendingHomeDestinations = listOf(ProfileList) },
                        onNotificationsClick = { selectTab(Home); pendingHomeDestinations = listOf(Notifications) },
                    )
                }
                entry<Wallet> {
                    WalletRoot(
                        onAtRootChanged = { isAtRoot = it },
                        onProfileClick = { selectTab(Home); pendingHomeDestinations = listOf(ProfileList) },
                        onNotificationsClick = { selectTab(Home); pendingHomeDestinations = listOf(Notifications) },
                    )
                }
                entry<Chat> {
                    ChatRoot(
                        onAtRootChanged = { isAtRoot = it },
                        pendingConversation = pendingChatConversation,
                        onPendingNavigated = { pendingChatConversation = null },
                        pendingCall = pendingCall,
                        onPendingCallNavigated = { pendingCall = null },
                        onProfileClick = { selectTab(Home); pendingHomeDestinations = listOf(ProfileList) },
                        onNotificationsClick = { selectTab(Home); pendingHomeDestinations = listOf(Notifications) },
                    )
                }
            },
        )
    }

    if (showComplianceDialog) {
        val isRejected = complianceStatus?.status == "REJECTED"
        ComplianceBottomSheet(
            isRejected = isRejected,
            failedApprovalReason = complianceStatus?.failedApprovalReason,
            hasPendingCorrection = complianceStatus?.hasPendingCorrection ?: false,
            isChecking = complianceViewModel.isChecking,
            isSigningOut = complianceViewModel.isSigningOut,
            isConfirmingCorrection = complianceViewModel.isConfirmingCorrection,
            onCheckStatus = { complianceViewModel.checkStatus() },
            onSignOut = { complianceViewModel.signOut(onSignOut) },
            onConfirmCorrection = { complianceViewModel.confirmCorrection() },
            onGoToLicences = {
                inComplianceFixMode = true
                selectTab(Home)
                pendingHomeDestinations = listOf(ProfileList, LicenceManagement)
            },
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun ComplianceBottomSheet(
    isRejected: Boolean,
    failedApprovalReason: String?,
    hasPendingCorrection: Boolean,
    isChecking: Boolean,
    isSigningOut: Boolean,
    isConfirmingCorrection: Boolean,
    onCheckStatus: () -> Unit,
    onSignOut: () -> Unit,
    onConfirmCorrection: () -> Unit,
    onGoToLicences: () -> Unit,
) {
    val headerGradient = if (isRejected) {
        Brush.horizontalGradient(listOf(Error40, Error40.copy(alpha = 0.7f)))
    } else {
        Brush.horizontalGradient(listOf(GradientStart, GradientEnd))
    }

    // The doctor must resolve their compliance status before using the app — swipe-to-dismiss,
    // scrim tap, and the system back gesture are all disabled, and confirmValueChange blocks the
    // sheet from ever reaching Hidden, so it stays locked open until isApproved flips to true and
    // this composable is removed from composition entirely (see showComplianceDialog above).
    ModalBottomSheet(
        onDismissRequest = {},
        sheetState = rememberModalBottomSheetState(
            skipPartiallyExpanded = true,
            confirmValueChange = { it != SheetValue.Hidden },
        ),
        sheetGesturesEnabled = false,
        properties = ModalBottomSheetProperties(shouldDismissOnBackPress = false),
        containerColor = MaterialTheme.colorScheme.surface,
        dragHandle = null,
        contentWindowInsets = { WindowInsets(0) },
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .navigationBarsPadding(),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            // Gradient header band
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(brush = headerGradient)
                    .padding(vertical = 24.dp),
                contentAlignment = Alignment.Center,
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Icon(
                        imageVector = Icons.Outlined.HourglassTop,
                        contentDescription = null,
                        tint = Color.White,
                        modifier = Modifier.size(40.dp),
                    )
                    Spacer(Modifier.height(8.dp))
                    Text(
                        text = if (isRejected) "Account Not Approved" else "Account Pending Approval",
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.SemiBold),
                        color = Color.White,
                    )
                }
            }

            Spacer(Modifier.height(20.dp))

            // Body
            Column(
                modifier = Modifier.padding(horizontal = 24.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                if (isRejected) {
                    Text(
                        text = "Your account did not pass our compliance review. Please update your profile with the required documents and resubmit for approval.",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        textAlign = TextAlign.Center,
                    )
                    if (!failedApprovalReason.isNullOrBlank()) {
                        Spacer(Modifier.height(12.dp))
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(8.dp))
                                .background(Error40.copy(alpha = 0.08f))
                                .padding(horizontal = 12.dp, vertical = 10.dp),
                        ) {
                            Text(
                                text = "Rejection Reason",
                                style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.SemiBold),
                                color = Error40,
                            )
                            Spacer(Modifier.height(4.dp))
                            Text(
                                text = failedApprovalReason,
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurface,
                            )
                        }
                    }
                } else {
                    Text(
                        text = "Your account is currently under review. Our team is verifying your credentials and licence to ensure you meet all compliance requirements.",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        textAlign = TextAlign.Center,
                    )
                    Spacer(Modifier.height(8.dp))
                    Text(
                        text = "You will be notified once your account has been approved and you can start offering your services.",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        textAlign = TextAlign.Center,
                    )
                }

                Spacer(Modifier.height(24.dp))

                if (isRejected) {
                    // Update Profile button (primary action for rejected)
                    Button(
                        onClick = onGoToLicences,
                        enabled = !isChecking && !isSigningOut,
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = Primary40),
                    ) {
                        Text(
                            text = "Update Profile",
                            style = MaterialTheme.typography.labelLarge,
                            color = Color.White,
                            modifier = Modifier.padding(vertical = 6.dp),
                        )
                    }
                    Spacer(Modifier.height(4.dp))

                    if (hasPendingCorrection) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(8.dp))
                                .background(Primary40.copy(alpha = 0.08f))
                                .padding(horizontal = 12.dp, vertical = 10.dp),
                        ) {
                            Text(
                                text = "Corrections Submitted",
                                style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.SemiBold),
                                color = Primary40,
                            )
                            Spacer(Modifier.height(4.dp))
                            Text(
                                text = "Your corrections have been submitted and are awaiting admin review.",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurface,
                            )
                        }
                        Spacer(Modifier.height(4.dp))
                    } else {
                        OutlinedButton(
                            onClick = onConfirmCorrection,
                            enabled = !isChecking && !isSigningOut && !isConfirmingCorrection,
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(12.dp),
                            border = BorderStroke(1.dp, Primary40),
                            colors = ButtonDefaults.outlinedButtonColors(contentColor = Primary40),
                        ) {
                            if (isConfirmingCorrection) {
                                CircularProgressIndicator(
                                    color = Primary40,
                                    modifier = Modifier.size(18.dp),
                                    strokeWidth = 2.dp,
                                )
                                Spacer(Modifier.width(8.dp))
                                Text(
                                    text = "Submitting…",
                                    style = MaterialTheme.typography.labelLarge,
                                    color = Primary40,
                                    modifier = Modifier.padding(vertical = 6.dp),
                                )
                            } else {
                                Text(
                                    text = "I've Made the Corrections",
                                    style = MaterialTheme.typography.labelLarge,
                                    color = Primary40,
                                    modifier = Modifier.padding(vertical = 6.dp),
                                )
                            }
                        }
                        Spacer(Modifier.height(4.dp))
                    }
                }

                // Check Status button
                Button(
                    onClick = onCheckStatus,
                    enabled = !isChecking && !isSigningOut,
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Primary40),
                ) {
                    if (isChecking) {
                        CircularProgressIndicator(
                            color = Color.White,
                            modifier = Modifier.size(18.dp),
                            strokeWidth = 2.dp,
                        )
                        Spacer(Modifier.width(8.dp))
                        Text(
                            text = "Checking…",
                            style = MaterialTheme.typography.labelLarge,
                            color = Color.White,
                            modifier = Modifier.padding(vertical = 6.dp),
                        )
                    } else {
                        Text(
                            text = "Check Status",
                            style = MaterialTheme.typography.labelLarge,
                            color = Color.White,
                            modifier = Modifier.padding(vertical = 6.dp),
                        )
                    }
                }

                Spacer(Modifier.height(4.dp))

                // Sign Out text button
                TextButton(
                    onClick = onSignOut,
                    enabled = !isChecking && !isSigningOut,
                    modifier = Modifier.fillMaxWidth(),
                ) {
                    if (isSigningOut) {
                        CircularProgressIndicator(
                            color = Error40,
                            modifier = Modifier.size(16.dp),
                            strokeWidth = 2.dp,
                        )
                        Spacer(Modifier.width(8.dp))
                        Text(
                            text = "Signing out…",
                            style = MaterialTheme.typography.labelLarge,
                            color = Error40,
                        )
                    } else {
                        Text(
                            text = "Sign Out",
                            style = MaterialTheme.typography.labelLarge,
                            color = Error40,
                        )
                    }
                }
            }

            Spacer(Modifier.height(16.dp))
        }
    }
}

@Composable
private fun BottomNavBar(
    currentTab: NavKey,
    onTabSelected: (NavKey) -> Unit,
) {
    Column {
        HorizontalDivider(thickness = 0.5.dp, color = MaterialTheme.colorScheme.outlineVariant)
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(MaterialTheme.colorScheme.surface)
                .navigationBarsPadding(),
        ) {
            tabs.forEach { tab ->
                val isSelected = currentTab::class == tab.destination::class
                BottomNavItem(
                    tab = tab,
                    isSelected = isSelected,
                    onSelected = { onTabSelected(tab.destination) },
                    modifier = Modifier.weight(1f),
                )
            }
        }
    }
}

@Composable
private fun BottomNavItem(
    tab: BottomTab,
    isSelected: Boolean,
    onSelected: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val iconColor by animateColorAsState(
        targetValue = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant,
        animationSpec = tween(200),
        label = "tabIconColor",
    )
    val labelColor by animateColorAsState(
        targetValue = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant,
        animationSpec = tween(200),
        label = "tabLabelColor",
    )

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = modifier
            .clickable(
                indication = null,
                interactionSource = remember { MutableInteractionSource() },
                onClick = onSelected,
            )
            .padding(bottom = 10.dp),
    ) {
        Box(
            modifier = Modifier
                .size(width = 32.dp, height = 3.dp)
                .clip(RoundedCornerShape(bottomStart = 4.dp, bottomEnd = 4.dp))
                .background(
                    if (isSelected) MaterialTheme.colorScheme.primary
                    else MaterialTheme.colorScheme.surface
                ),
        )
        Spacer(Modifier.height(6.dp))
        when (val icon = tab.icon) {
            is TabIcon.Vector -> Icon(
                imageVector = icon.icon,
                contentDescription = tab.label,
                tint = iconColor,
                modifier = Modifier.size(24.dp),
            )
            is TabIcon.Drawable -> Icon(
                painter = painterResource(icon.res),
                contentDescription = tab.label,
                tint = iconColor,
                modifier = Modifier.size(24.dp),
            )
        }
        Spacer(Modifier.height(2.dp))
        Text(
            text = tab.label,
            style = MaterialTheme.typography.labelSmall,
            color = labelColor,
        )
    }
}
