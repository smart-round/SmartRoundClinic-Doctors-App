package ke.co.smartroundclinic.doctor.presentation.main.wallet.ui

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
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
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.AccountBalanceWallet
import androidx.compose.material.icons.outlined.ArrowUpward
import androidx.compose.material.icons.outlined.CheckCircle
import androidx.compose.material.icons.outlined.ContentCopy
import androidx.compose.material.icons.outlined.MoneyOff
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.TabRowDefaults.SecondaryIndicator
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import ke.co.smartroundclinic.doctor.presentation.common.composables.DashboardHeader
import ke.co.smartroundclinic.doctor.domain.model.DoctorPayment
import ke.co.smartroundclinic.doctor.domain.model.PaymentSummary
import ke.co.smartroundclinic.doctor.domain.model.Withdrawal
import ke.co.smartroundclinic.doctor.domain.model.WithdrawalBalance
import ke.co.smartroundclinic.doctor.presentation.main.wallet.WalletViewModel
import ke.co.smartroundclinic.doctor.presentation.theme.CardBackground
import ke.co.smartroundclinic.doctor.presentation.theme.Error40
import ke.co.smartroundclinic.doctor.presentation.theme.GradientEnd
import ke.co.smartroundclinic.doctor.presentation.theme.GradientStart
import ke.co.smartroundclinic.doctor.presentation.theme.Primary40
import ke.co.smartroundclinic.doctor.presentation.theme.Primary90
import ke.co.smartroundclinic.doctor.presentation.theme.ShapeCard
import ke.co.smartroundclinic.doctor.presentation.theme.Tertiary40
import kotlinx.coroutines.launch
import org.jetbrains.compose.resources.painterResource
import org.koin.compose.viewmodel.koinViewModel
import smartroundclinic.composeapp.generated.resources.Res
import smartroundclinic.composeapp.generated.resources.card_payment
import smartroundclinic.composeapp.generated.resources.mpesa

private enum class WalletTab(val label: String) {
    OVERVIEW("Overview"),
    TRANSACTIONS("Transactions"),
    WITHDRAWALS("Withdrawals"),
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun WalletScreen(
    modifier: Modifier = Modifier,
    onProfileClick: () -> Unit = {},
    onNotificationsClick: () -> Unit = {},
    vm: WalletViewModel = koinViewModel(),
) {
    val scope = rememberCoroutineScope()
    val clipboard = LocalClipboardManager.current

    var selectedTab by remember { mutableIntStateOf(0) }
    var showWithdrawSheet by remember { mutableStateOf(false) }
    var selectedPayment by remember { mutableStateOf<DoctorPayment?>(null) }
    var selectedWithdrawalId by remember { mutableStateOf<String?>(null) }

    val withdrawSheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    val paymentDetailSheetState = rememberModalBottomSheetState(skipPartiallyExpanded = false)
    val withdrawalDetailSheetState = rememberModalBottomSheetState(skipPartiallyExpanded = false)

    // Fetch detail when a withdrawal is tapped
    LaunchedEffect(selectedWithdrawalId) {
        selectedWithdrawalId?.let { vm.loadWithdrawalDetail(it) }
    }

    Scaffold(
        modifier = modifier,
        containerColor = MaterialTheme.colorScheme.background,
        contentWindowInsets = WindowInsets(0),
        topBar = {
            DashboardHeader(
                title = "Wallet",
                onProfileClick = onProfileClick,
                onNotificationsClick = onNotificationsClick,
                bottomContent = {
                    TabRow(
                        selectedTabIndex = selectedTab,
                        containerColor = Color.Transparent,
                        contentColor = Color.White,
                        indicator = { tabPositions ->
                            SecondaryIndicator(
                                modifier = Modifier.tabIndicatorOffset(tabPositions[selectedTab]),
                                color = Color.White,
                            )
                        },
                        divider = {},
                    ) {
                        WalletTab.entries.forEachIndexed { index, tab ->
                            Tab(
                                selected = selectedTab == index,
                                onClick = { selectedTab = index },
                                text = {
                                    Text(
                                        text = tab.label,
                                        style = MaterialTheme.typography.labelMedium,
                                        color = if (selectedTab == index) Color.White else Color.White.copy(alpha = 0.65f),
                                    )
                                },
                            )
                        }
                    }
                },
            )
        },
    ) { paddingValues ->
        Column(modifier = Modifier.fillMaxSize().padding(paddingValues)) {

            // ── Tab content ───────────────────────────────────────────────────
            PullToRefreshBox(
                isRefreshing = vm.isLoading,
                onRefresh = { vm.load() },
                modifier = Modifier.fillMaxSize(),
            ) {
                when (WalletTab.entries[selectedTab]) {
                    WalletTab.OVERVIEW -> OverviewTab(
                        balance = vm.balance,
                        summary = vm.summary,
                        isLoading = vm.isLoading,
                        onWithdraw = { showWithdrawSheet = true },
                    )
                    WalletTab.TRANSACTIONS -> TransactionsTab(
                        payments = vm.payments,
                        isLoading = vm.isLoading,
                        onPaymentClick = { selectedPayment = it },
                    )
                    WalletTab.WITHDRAWALS -> WithdrawalsTab(
                        withdrawals = vm.withdrawals,
                        isLoading = vm.isLoading,
                        onWithdrawalClick = { selectedWithdrawalId = it.id },
                    )
                }
            }
        }
    }

    // ── Withdraw bottom sheet ─────────────────────────────────────────────────
    if (showWithdrawSheet) {
        ModalBottomSheet(
            onDismissRequest = { if (!vm.isWithdrawing) showWithdrawSheet = false },
            sheetState = withdrawSheetState,
            containerColor = MaterialTheme.colorScheme.background,
        ) {
            WithdrawSheetContent(
                availableBalance = vm.balance?.availableBalance ?: 0.0,
                minimumWithdrawal = vm.balance?.minimumWithdrawal ?: 100.0,
                isWithdrawing = vm.isWithdrawing,
                onDismiss = {
                    scope.launch { withdrawSheetState.hide() }.invokeOnCompletion { showWithdrawSheet = false }
                },
                onConfirm = { idNumber, amount ->
                    vm.withdraw(idNumber, amount) {
                        scope.launch { withdrawSheetState.hide() }.invokeOnCompletion { showWithdrawSheet = false }
                    }
                },
            )
        }
    }

    // ── Payment detail sheet ──────────────────────────────────────────────────
    selectedPayment?.let { payment ->
        ModalBottomSheet(
            onDismissRequest = { selectedPayment = null },
            sheetState = paymentDetailSheetState,
            containerColor = MaterialTheme.colorScheme.background,
        ) {
            PaymentDetailSheetContent(
                payment = payment,
                onCopy = { clipboard.setText(AnnotatedString(it)) },
                onDismiss = {
                    scope.launch { paymentDetailSheetState.hide() }.invokeOnCompletion { selectedPayment = null }
                },
            )
        }
    }

    // ── Withdrawal detail sheet ───────────────────────────────────────────────
    if (selectedWithdrawalId != null) {
        ModalBottomSheet(
            onDismissRequest = {
                selectedWithdrawalId = null
                vm.clearWithdrawalDetail()
            },
            sheetState = withdrawalDetailSheetState,
            containerColor = MaterialTheme.colorScheme.background,
        ) {
            WithdrawalDetailSheetContent(
                withdrawal = vm.withdrawalDetail,
                isLoading = vm.isLoadingDetail,
                onCopy = { clipboard.setText(AnnotatedString(it)) },
                onDismiss = {
                    scope.launch { withdrawalDetailSheetState.hide() }.invokeOnCompletion {
                        selectedWithdrawalId = null
                        vm.clearWithdrawalDetail()
                    }
                },
            )
        }
    }
}

// ── Overview tab ──────────────────────────────────────────────────────────────

@Composable
private fun OverviewTab(
    balance: WithdrawalBalance?,
    summary: PaymentSummary?,
    isLoading: Boolean,
    onWithdraw: () -> Unit,
) {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
    ) {
        item { BalanceCard(balance = balance, isLoading = isLoading, onWithdraw = onWithdraw) }
        if (summary != null) {
            item { SummaryCard(summary = summary) }
        }
    }
}

// ── Transactions tab ──────────────────────────────────────────────────────────

@Composable
private fun TransactionsTab(
    payments: List<DoctorPayment>,
    isLoading: Boolean,
    onPaymentClick: (DoctorPayment) -> Unit,
) {
    if (payments.isEmpty() && !isLoading) {
        EmptyState(
            icon = Icons.Outlined.AccountBalanceWallet,
            title = "No Transactions Yet",
            subtitle = "Payment history will appear here once patients start booking consultations.",
        )
    } else {
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            items(payments, key = { it.id }) { payment ->
                PaymentCard(payment = payment, onClick = { onPaymentClick(payment) })
            }
        }
    }
}

// ── Withdrawals tab ───────────────────────────────────────────────────────────

@Composable
private fun WithdrawalsTab(
    withdrawals: List<Withdrawal>,
    isLoading: Boolean,
    onWithdrawalClick: (Withdrawal) -> Unit,
) {
    if (withdrawals.isEmpty() && !isLoading) {
        EmptyState(
            icon = Icons.Outlined.ArrowUpward,
            title = "No Withdrawals Yet",
            subtitle = "Your withdrawal history will appear here after your first withdrawal.",
        )
    } else {
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            items(withdrawals, key = { it.id }) { withdrawal ->
                WithdrawalCard(withdrawal = withdrawal, onClick = { onWithdrawalClick(withdrawal) })
            }
        }
    }
}

// ── Balance card ──────────────────────────────────────────────────────────────

@Composable
private fun BalanceCard(
    balance: WithdrawalBalance?,
    isLoading: Boolean,
    onWithdraw: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .clip(ShapeCard)
            .background(Brush.horizontalGradient(listOf(GradientStart, GradientEnd)))
            .padding(20.dp),
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.fillMaxWidth()) {
            Text("Available Balance", style = MaterialTheme.typography.bodyMedium, color = Color.White.copy(alpha = 0.85f))
            Spacer(Modifier.height(8.dp))
            if (isLoading && balance == null) {
                CircularProgressIndicator(color = Color.White, modifier = Modifier.size(32.dp), strokeWidth = 2.dp)
            } else {
                Text(
                    "KES ${formatAmount(balance?.availableBalance ?: 0.0)}",
                    style = MaterialTheme.typography.headlineMedium.copy(fontWeight = FontWeight.Bold, fontSize = 32.sp),
                    color = Color.White,
                )
            }
            Spacer(Modifier.height(4.dp))
            Text(
                "Min. withdrawal: KES ${formatAmount(balance?.minimumWithdrawal ?: 100.0)}",
                style = MaterialTheme.typography.labelSmall,
                color = Color.White.copy(alpha = 0.7f),
            )
            Spacer(Modifier.height(20.dp))
            Row(horizontalArrangement = Arrangement.spacedBy(24.dp)) {
                BalanceStat("Net Earned", balance?.totalNetEarnings)
                BalanceStat("Withdrawn", balance?.totalWithdrawn)
                BalanceStat("Pending", balance?.totalPending)
            }
            Spacer(Modifier.height(20.dp))
            Button(
                onClick = onWithdraw,
                enabled = (balance?.availableBalance ?: 0.0) >= (balance?.minimumWithdrawal ?: 100.0),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color.White,
                    contentColor = GradientStart,
                    disabledContentColor = Color.White.copy(0.6f)
                ),
                modifier = Modifier.fillMaxWidth(),
            ) {
                Icon(Icons.Outlined.ArrowUpward, contentDescription = null, modifier = Modifier.size(18.dp))
                Spacer(Modifier.width(6.dp))
                Text("Withdraw", style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.SemiBold), modifier = Modifier.padding(vertical = 4.dp))
            }
        }
    }
}

@Composable
private fun BalanceStat(label: String, value: Double?) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(
            text = if (value != null) "KES ${formatAmount(value)}" else "—",
            style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.SemiBold),
            color = Color.White,
        )
        Text(text = label, style = MaterialTheme.typography.labelSmall, color = Color.White.copy(alpha = 0.7f))
    }
}

// ── Earnings summary card ─────────────────────────────────────────────────────

@Composable
private fun SummaryCard(summary: PaymentSummary, modifier: Modifier = Modifier) {
    Card(
        modifier = modifier.fillMaxWidth(),
        shape = ShapeCard,
        colors = CardDefaults.cardColors(containerColor = CardBackground),
        elevation = CardDefaults.cardElevation(1.dp),
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text("Earnings Summary", style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.SemiBold))
            Spacer(Modifier.height(12.dp))
            SummaryRow(Icons.Outlined.AccountBalanceWallet, "Gross Earnings", summary.totalGross, Primary40)
            HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp), thickness = 0.5.dp)
            SummaryRow(Icons.Outlined.MoneyOff, "Platform Fees", summary.totalPlatformFees, Error40)
            HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp), thickness = 0.5.dp)
            SummaryRow(Icons.Outlined.CheckCircle, "Net Earnings", summary.totalNetEarnings, Tertiary40)
            HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp), thickness = 0.5.dp)
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                StatChip("Completed", summary.completedCount.toString(), Primary40)
                StatChip("Pending", summary.pendingCount.toString(), Tertiary40)
                StatChip("Total", summary.totalTransactions.toString(), MaterialTheme.colorScheme.onSurfaceVariant)
            }
        }
    }
}

@Composable
private fun SummaryRow(icon: ImageVector, label: String, amount: Double, tint: Color) {
    Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
        Box(modifier = Modifier.size(32.dp).clip(CircleShape).background(tint.copy(alpha = 0.12f)), contentAlignment = Alignment.Center) {
            Icon(icon, contentDescription = null, tint = tint, modifier = Modifier.size(16.dp))
        }
        Spacer(Modifier.width(10.dp))
        Text(text = label, style = MaterialTheme.typography.bodySmall, modifier = Modifier.weight(1f), color = MaterialTheme.colorScheme.onSurfaceVariant)
        Text(text = "KES ${formatAmount(amount)}", style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.SemiBold))
    }
}

@Composable
private fun StatChip(label: String, value: String, color: Color) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(text = value, style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold), color = color)
        Text(text = label, style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
    }
}

// ── Payment card ──────────────────────────────────────────────────────────────

@Composable
private fun PaymentCard(payment: DoctorPayment, onClick: () -> Unit, modifier: Modifier = Modifier) {
    val (statusColor, statusLabel) = paymentStatusStyle(payment.status)
    Card(
        onClick = onClick,
        modifier = modifier.fillMaxWidth(),
        shape = ShapeCard,
        colors = CardDefaults.cardColors(containerColor = CardBackground),
        elevation = CardDefaults.cardElevation(1.dp),
    ) {
        Row(modifier = Modifier.padding(12.dp), verticalAlignment = Alignment.CenterVertically) {
            PaymentMethodIcon(payment.paymentMethod, modifier = Modifier.size(40.dp).clip(CircleShape))
            Spacer(Modifier.width(12.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(paymentMethodLabel(payment.paymentMethod), style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.SemiBold))
                Text(formatPaymentDate(payment.createdAt), style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
            Column(horizontalAlignment = Alignment.End) {
                Text("KES ${formatAmount(payment.netEarnings)}", style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold), color = Tertiary40)
                StatusBadge(statusLabel, statusColor)
            }
        }
    }
}

// ── Withdrawal card ───────────────────────────────────────────────────────────

@Composable
private fun WithdrawalCard(withdrawal: Withdrawal, onClick: () -> Unit, modifier: Modifier = Modifier) {
    val (statusColor, statusLabel) = withdrawalStatusStyle(withdrawal.status)
    Card(
        onClick = onClick,
        modifier = modifier.fillMaxWidth(),
        shape = ShapeCard,
        colors = CardDefaults.cardColors(containerColor = CardBackground),
        elevation = CardDefaults.cardElevation(1.dp),
    ) {
        Row(modifier = Modifier.padding(12.dp), verticalAlignment = Alignment.CenterVertically) {
            Box(modifier = Modifier.size(40.dp).clip(CircleShape).background(Primary90), contentAlignment = Alignment.Center) {
                Icon(Icons.Outlined.ArrowUpward, contentDescription = null, tint = Primary40, modifier = Modifier.size(20.dp))
            }
            Spacer(Modifier.width(12.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(withdrawal.provider.replaceFirstChar { it.uppercase() }, style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.SemiBold))
                Text(formatPaymentDate(withdrawal.createdAt), style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
            Column(horizontalAlignment = Alignment.End) {
                Text("KES ${formatAmount(withdrawal.amount)}", style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold), color = Error40)
                StatusBadge(statusLabel, statusColor)
            }
        }
    }
}

@Composable
private fun StatusBadge(label: String, color: Color) {
    Box(modifier = Modifier.clip(RoundedCornerShape(100.dp)).background(color.copy(alpha = 0.12f)).padding(horizontal = 8.dp, vertical = 2.dp)) {
        Text(text = label, style = MaterialTheme.typography.labelSmall, color = color)
    }
}

// ── Payment method icon ───────────────────────────────────────────────────────

@Composable
private fun PaymentMethodIcon(paymentMethod: String?, modifier: Modifier = Modifier) {
    val method = paymentMethod?.uppercase() ?: ""
    when {
        "MPESA" in method -> Image(
            painter = painterResource(Res.drawable.mpesa),
            contentDescription = "M-Pesa",
            contentScale = ContentScale.Fit,
            modifier = modifier.background(Color.White).padding(4.dp),
        )
        "CARD" in method -> Image(
            painter = painterResource(Res.drawable.card_payment),
            contentDescription = "Card",
            contentScale = ContentScale.Fit,
            modifier = modifier.background(Primary90).padding(6.dp),
        )
        else -> Box(modifier = modifier.background(Primary90), contentAlignment = Alignment.Center) {
            Icon(Icons.Outlined.AccountBalanceWallet, contentDescription = "Payment", tint = Primary40, modifier = Modifier.size(20.dp))
        }
    }
}

// ── Payment detail sheet ──────────────────────────────────────────────────────

@Composable
private fun PaymentDetailSheetContent(
    payment: DoctorPayment,
    onCopy: (String) -> Unit,
    onDismiss: () -> Unit,
) {
    val (statusColor, statusLabel) = paymentStatusStyle(payment.status)
    Column(
        modifier = Modifier.fillMaxWidth().navigationBarsPadding().padding(horizontal = 20.dp, vertical = 8.dp),
    ) {
        Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.fillMaxWidth()) {
            PaymentMethodIcon(payment.paymentMethod, modifier = Modifier.size(48.dp).clip(CircleShape))
            Spacer(Modifier.width(12.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(paymentMethodLabel(payment.paymentMethod), style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold))
                Text(formatPaymentDate(payment.createdAt), style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
            StatusBadge(statusLabel, statusColor)
        }
        Spacer(Modifier.height(20.dp))
        AmountsStrip(payment.amount, payment.platformFee, payment.netEarnings, payment.currency)
        Spacer(Modifier.height(16.dp))
        DetailRow("Currency", payment.currency)
        DetailRow("Commission Rate", "${payment.commissionRate}%")
        if (!payment.transactionRef.isNullOrBlank()) CopyableDetailRow("Transaction Ref", payment.transactionRef, onCopy)
        if (!payment.invoiceId.isNullOrBlank()) CopyableDetailRow("Invoice ID", payment.invoiceId, onCopy)
        if (!payment.appointmentId.isNullOrBlank()) CopyableDetailRow("Appointment ID", payment.appointmentId, onCopy)
        if (!payment.notes.isNullOrBlank()) DetailRow("Notes", payment.notes)
        if (!payment.updatedAt.isNullOrBlank()) DetailRow("Last Updated", formatPaymentDate(payment.updatedAt))
        Spacer(Modifier.height(16.dp))
        TextButton(onClick = onDismiss, modifier = Modifier.align(Alignment.CenterHorizontally)) {
            Text("Close", color = MaterialTheme.colorScheme.onSurfaceVariant)
        }
        Spacer(Modifier.height(8.dp))
    }
}

// ── Withdrawal detail sheet ───────────────────────────────────────────────────

@Composable
private fun WithdrawalDetailSheetContent(
    withdrawal: Withdrawal?,
    isLoading: Boolean,
    onCopy: (String) -> Unit,
    onDismiss: () -> Unit,
) {
    Column(
        modifier = Modifier.fillMaxWidth().navigationBarsPadding().padding(horizontal = 20.dp, vertical = 8.dp),
    ) {
        if (isLoading || withdrawal == null) {
            Box(modifier = Modifier.fillMaxWidth().height(200.dp), contentAlignment = Alignment.Center) {
                CircularProgressIndicator(color = Primary40)
            }
        } else {
            val (statusColor, statusLabel) = withdrawalStatusStyle(withdrawal.status)
            Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.fillMaxWidth()) {
                Box(modifier = Modifier.size(48.dp).clip(CircleShape).background(Primary90), contentAlignment = Alignment.Center) {
                    Icon(Icons.Outlined.ArrowUpward, contentDescription = null, tint = Primary40, modifier = Modifier.size(24.dp))
                }
                Spacer(Modifier.width(12.dp))
                Column(modifier = Modifier.weight(1f)) {
                    Text(withdrawal.provider.replaceFirstChar { it.uppercase() }, style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold))
                    Text(formatPaymentDate(withdrawal.createdAt), style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
                StatusBadge(statusLabel, statusColor)
            }
            Spacer(Modifier.height(20.dp))
            // Amount strip
            Box(
                modifier = Modifier.fillMaxWidth().clip(ShapeCard).background(Brush.horizontalGradient(listOf(GradientStart, GradientEnd))).padding(16.dp),
            ) {
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceEvenly) {
                    AmountStat("Amount", withdrawal.amount, withdrawal.currency)
                    Box(modifier = Modifier.size(width = 1.dp, height = 36.dp).background(Color.White.copy(alpha = 0.3f)))
                    AmountStat("Commission", withdrawal.platformCommission, withdrawal.currency)
                }
            }
            Spacer(Modifier.height(16.dp))
            DetailRow("Currency", withdrawal.currency)
            CopyableDetailRow("Tracking ID", withdrawal.trackingId, onCopy)
            if (!withdrawal.updatedAt.isNullOrBlank()) DetailRow("Last Updated", formatPaymentDate(withdrawal.updatedAt))
        }
        Spacer(Modifier.height(16.dp))
        TextButton(onClick = onDismiss, modifier = Modifier.align(Alignment.CenterHorizontally)) {
            Text("Close", color = MaterialTheme.colorScheme.onSurfaceVariant)
        }
        Spacer(Modifier.height(8.dp))
    }
}

// ── Shared sheet components ───────────────────────────────────────────────────

@Composable
private fun AmountsStrip(gross: Double, fee: Double, net: Double, currency: String) {
    Box(
        modifier = Modifier.fillMaxWidth().clip(ShapeCard).background(Brush.horizontalGradient(listOf(GradientStart, GradientEnd))).padding(16.dp),
    ) {
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceEvenly) {
            AmountStat("Gross", gross, currency)
            Box(modifier = Modifier.size(width = 1.dp, height = 36.dp).background(Color.White.copy(alpha = 0.3f)))
            AmountStat("Platform Fee", fee, currency)
            Box(modifier = Modifier.size(width = 1.dp, height = 36.dp).background(Color.White.copy(alpha = 0.3f)))
            AmountStat("Net Earned", net, currency)
        }
    }
}

@Composable
private fun AmountStat(label: String, amount: Double, currency: String) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(label, style = MaterialTheme.typography.labelSmall, color = Color.White.copy(alpha = 0.75f))
        Spacer(Modifier.height(4.dp))
        Text("$currency ${formatAmount(amount)}", style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold), color = Color.White, textAlign = TextAlign.Center)
    }
}

@Composable
private fun DetailRow(label: String, value: String) {
    Column {
        Row(modifier = Modifier.fillMaxWidth().padding(vertical = 10.dp), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.Top) {
            Text(label, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant, modifier = Modifier.weight(0.45f))
            Text(value, style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.SemiBold), modifier = Modifier.weight(0.55f), textAlign = TextAlign.End)
        }
        HorizontalDivider(thickness = 0.5.dp, color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f))
    }
}

@Composable
private fun CopyableDetailRow(label: String, value: String, onCopy: (String) -> Unit) {
    Column {
        Row(modifier = Modifier.fillMaxWidth().padding(vertical = 6.dp), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
            Text(label, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant, modifier = Modifier.weight(0.4f))
            Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.weight(0.6f), horizontalArrangement = Arrangement.End) {
                Text(value, style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.SemiBold), textAlign = TextAlign.End, modifier = Modifier.weight(1f, fill = false))
                IconButton(onClick = { onCopy(value) }, modifier = Modifier.size(28.dp).padding(start = 4.dp)) {
                    Icon(Icons.Outlined.ContentCopy, contentDescription = "Copy $label", tint = MaterialTheme.colorScheme.onSurfaceVariant, modifier = Modifier.size(14.dp))
                }
            }
        }
        HorizontalDivider(thickness = 0.5.dp, color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f))
    }
}

// ── Withdraw sheet ────────────────────────────────────────────────────────────

@Composable
private fun WithdrawSheetContent(
    availableBalance: Double,
    minimumWithdrawal: Double,
    isWithdrawing: Boolean,
    onDismiss: () -> Unit,
    onConfirm: (idNumber: String, amount: Double) -> Unit,
) {
    var idNumber by remember { mutableStateOf("") }
    var amountText by remember { mutableStateOf("") }
    var amountError by remember { mutableStateOf<String?>(null) }
    val amount = amountText.toDoubleOrNull()

    fun validate(): Boolean = when {
        idNumber.isBlank() -> false
        amount == null -> { amountError = "Enter a valid amount"; false }
        amount < minimumWithdrawal -> { amountError = "Minimum is KES ${formatAmount(minimumWithdrawal)}"; false }
        amount > availableBalance -> { amountError = "Exceeds available KES ${formatAmount(availableBalance)}"; false }
        else -> { amountError = null; true }
    }

    Column(modifier = Modifier.fillMaxWidth().navigationBarsPadding().padding(horizontal = 20.dp), verticalArrangement = Arrangement.spacedBy(16.dp)) {
        Box(modifier = Modifier.size(width = 40.dp, height = 4.dp).clip(CircleShape).background(MaterialTheme.colorScheme.outlineVariant).align(Alignment.CenterHorizontally))
        Text("Withdraw Funds", style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold))
        Box(modifier = Modifier.fillMaxWidth().clip(ShapeCard).background(Primary40.copy(alpha = 0.08f)).padding(12.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Outlined.AccountBalanceWallet, contentDescription = null, tint = Primary40, modifier = Modifier.size(20.dp))
                Spacer(Modifier.width(8.dp))
                Text("Available: ", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                Text("KES ${formatAmount(availableBalance)}", style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Bold), color = Primary40)
            }
        }
        OutlinedTextField(value = idNumber, onValueChange = { idNumber = it }, label = { Text("National ID Number") }, singleLine = true, modifier = Modifier.fillMaxWidth(), keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number))
        OutlinedTextField(
            value = amountText,
            onValueChange = { amountText = it; amountError = null },
            label = { Text("Amount (KES)") },
            singleLine = true,
            isError = amountError != null,
            supportingText = amountError?.let { err -> { Text(err, color = Error40) } },
            modifier = Modifier.fillMaxWidth(),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
        )
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            TextButton(onClick = onDismiss, enabled = !isWithdrawing, modifier = Modifier.weight(1f)) { Text("Cancel") }
            Button(
                onClick = { if (validate()) onConfirm(idNumber, amount!!) },
                enabled = !isWithdrawing && idNumber.isNotBlank() && amountText.isNotBlank(),
                colors = ButtonDefaults.buttonColors(containerColor = Primary40),
                modifier = Modifier.weight(1f),
            ) {
                if (isWithdrawing) {
                    CircularProgressIndicator(modifier = Modifier.size(16.dp), color = Color.White, strokeWidth = 2.dp)
                    Spacer(Modifier.width(6.dp))
                    Text("Processing…")
                } else {
                    Text("Withdraw")
                }
            }
        }
        Spacer(Modifier.height(4.dp))
    }
}

// ── Empty state ───────────────────────────────────────────────────────────────

@Composable
private fun EmptyState(icon: ImageVector, title: String, subtitle: String, modifier: Modifier = Modifier) {
    Box(modifier = modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.padding(32.dp)) {
            Box(modifier = Modifier.size(80.dp).clip(CircleShape).background(Primary90), contentAlignment = Alignment.Center) {
                Icon(icon, contentDescription = null, tint = Primary40, modifier = Modifier.size(36.dp))
            }
            Spacer(Modifier.height(16.dp))
            Text(title, style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold))
            Spacer(Modifier.height(6.dp))
            Text(subtitle, style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant, textAlign = TextAlign.Center)
        }
    }
}

// ── Helpers ───────────────────────────────────────────────────────────────────

private fun paymentMethodLabel(method: String?): String {
    val m = method?.uppercase() ?: ""
    return when {
        "MPESA" in m -> "M-Pesa"
        "CARD" in m -> "Card Payment"
        else -> method?.replace("_", " ")?.lowercase()?.replaceFirstChar { it.uppercase() } ?: "Payment"
    }
}

@Composable
private fun paymentStatusStyle(status: String): Pair<Color, String> = when (status.uppercase()) {
    "COMPLETED" -> Tertiary40 to "Completed"
    "PENDING" -> Primary40 to "Pending"
    "PROCESSING" -> Primary40 to "Processing"
    "FAILED" -> Error40 to "Failed"
    "REFUNDED" -> Error40 to "Refunded"
    else -> Pair(MaterialTheme.colorScheme.onSurfaceVariant, status)
}

@Composable
private fun withdrawalStatusStyle(status: String): Pair<Color, String> = when (status.uppercase()) {
    "COMPLETED" -> Tertiary40 to "Completed"
    "FAILED" -> Error40 to "Failed"
    else -> Primary40 to "Pending"
}

private fun formatAmount(value: Double): String {
    val rounded = kotlin.math.round(value * 100).toLong()
    val intPart = (rounded / 100).toString()
    val fracPart = (rounded % 100).toString().padStart(2, '0')
    val intFormatted = intPart.reversed().chunked(3).joinToString(",").reversed()
    return "$intFormatted.$fracPart"
}

private fun formatPaymentDate(iso: String): String = try {
    val date = iso.substringBefore("T")
    val time = iso.substringAfter("T").substringBefore(".").substringBefore("Z")
    val parts = time.split(":")
    val hour = parts[0].toInt()
    val min = parts.getOrElse(1) { "00" }
    val ampm = if (hour < 12) "AM" else "PM"
    val h = if (hour % 12 == 0) 12 else hour % 12
    "$date · $h:$min $ampm"
} catch (e: Exception) { iso }
