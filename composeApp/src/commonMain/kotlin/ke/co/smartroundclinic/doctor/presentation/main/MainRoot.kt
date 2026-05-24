package ke.co.smartroundclinic.doctor.presentation.main

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.tween
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
import androidx.compose.material.icons.outlined.HourglassTop
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.retain.retain
import androidx.compose.runtime.setValue
import ke.co.smartroundclinic.doctor.presentation.main.profile.destinations.LicenceManagement
import ke.co.smartroundclinic.doctor.presentation.main.profile.destinations.ProfileList
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.navigation3.runtime.NavKey
import androidx.navigation3.runtime.entryProvider
import androidx.navigation3.ui.NavDisplay
import ke.co.smartroundclinic.doctor.presentation.main.articles.ArticlesRoot
import ke.co.smartroundclinic.doctor.presentation.main.bookings.BookingsRoot
import ke.co.smartroundclinic.doctor.presentation.main.chat.ChatRoot
import ke.co.smartroundclinic.doctor.presentation.main.destinations.Articles
import ke.co.smartroundclinic.doctor.presentation.main.destinations.Bookings
import ke.co.smartroundclinic.doctor.presentation.main.destinations.Chat
import ke.co.smartroundclinic.doctor.presentation.main.destinations.Home
import ke.co.smartroundclinic.doctor.presentation.main.home.HomeRoot
import ke.co.smartroundclinic.doctor.presentation.rememberSvgPainter
import ke.co.smartroundclinic.doctor.presentation.theme.Error40
import ke.co.smartroundclinic.doctor.presentation.theme.GradientEnd
import ke.co.smartroundclinic.doctor.presentation.theme.GradientStart
import ke.co.smartroundclinic.doctor.presentation.theme.Primary40
import ke.co.smartroundclinic.doctor.presentation.theme.ShapeCard
import org.koin.compose.viewmodel.koinViewModel

private data class BottomTab(
    val destination: NavKey,
    val label: String,
    val iconPath: String,
)

private val tabs = listOf(
    BottomTab(Home, "Home", "files/icons/home.svg"),
    BottomTab(Bookings, "Bookings", "files/icons/bookings.svg"),
    BottomTab(Articles, "Articles", "files/icons/articles.svg"),
    BottomTab(Chat, "Chat", "files/icons/chat.svg"),
)

@Composable
fun MainRoot(modifier: Modifier = Modifier, onSignOut: () -> Unit = {}) {
    val backStack = retain { mutableStateListOf<NavKey>(Home) }
    val currentTab = backStack.lastOrNull() ?: Home

    var isAtRoot by remember { mutableStateOf(true) }
    var pendingHomeDestinations by remember { mutableStateOf<List<NavKey>>(emptyList()) }
    var inComplianceFixMode by remember { mutableStateOf(false) }

    // When the user navigates back to root after fixing their profile, re-show the dialog
    LaunchedEffect(isAtRoot) {
        if (isAtRoot && inComplianceFixMode) {
            inComplianceFixMode = false
        }
    }

    val complianceViewModel: ComplianceViewModel = koinViewModel()
    val complianceStatus = complianceViewModel.complianceStatus
    val showComplianceDialog = complianceStatus != null && !complianceStatus.isApproved && !inComplianceFixMode

    fun selectTab(dest: NavKey) {
        if (currentTab != dest) {
            backStack.clear()
            backStack.add(dest)
            isAtRoot = true
        }
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
                        pendingDestinations = pendingHomeDestinations,
                        onPendingNavigated = { pendingHomeDestinations = emptyList() },
                    )
                }
                entry<Bookings> { BookingsRoot(onAtRootChanged = { isAtRoot = it }) }
                entry<Articles> { ArticlesRoot(onAtRootChanged = { isAtRoot = it }) }
                entry<Chat> { ChatRoot(onAtRootChanged = { isAtRoot = it }) }
            },
        )
    }

    if (showComplianceDialog) {
        val isRejected = complianceStatus?.status == "REJECTED"
        ComplianceDialog(
            isRejected = isRejected,
            failedApprovalReason = complianceStatus?.failedApprovalReason,
            isChecking = complianceViewModel.isChecking,
            isSigningOut = complianceViewModel.isSigningOut,
            onCheckStatus = { complianceViewModel.checkStatus() },
            onSignOut = { complianceViewModel.signOut(onSignOut) },
            onGoToLicences = {
                inComplianceFixMode = true
                selectTab(Home)
                pendingHomeDestinations = listOf(ProfileList, LicenceManagement)
            },
        )
    }
}

@Composable
private fun ComplianceDialog(
    isRejected: Boolean,
    failedApprovalReason: String?,
    isChecking: Boolean,
    isSigningOut: Boolean,
    onCheckStatus: () -> Unit,
    onSignOut: () -> Unit,
    onGoToLicences: () -> Unit,
) {
    val headerGradient = if (isRejected) {
        Brush.horizontalGradient(listOf(Error40, Error40.copy(alpha = 0.7f)))
    } else {
        Brush.horizontalGradient(listOf(GradientStart, GradientEnd))
    }

    Dialog(
        onDismissRequest = {},
        properties = DialogProperties(
            dismissOnBackPress = false,
            dismissOnClickOutside = false,
        ),
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .clip(ShapeCard)
                .background(MaterialTheme.colorScheme.surface),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            // Gradient header band
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(
                        brush = headerGradient,
                        shape = RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp),
                    )
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
        Icon(
            painter = rememberSvgPainter(tab.iconPath),
            contentDescription = tab.label,
            tint = iconColor,
            modifier = Modifier.size(24.dp),
        )
        Spacer(Modifier.height(2.dp))
        Text(
            text = tab.label,
            style = MaterialTheme.typography.labelSmall,
            color = labelColor,
        )
    }
}
