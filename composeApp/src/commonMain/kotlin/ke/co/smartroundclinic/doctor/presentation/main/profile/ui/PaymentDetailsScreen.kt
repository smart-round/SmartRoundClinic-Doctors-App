package ke.co.smartroundclinic.doctor.presentation.main.profile.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
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
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.ArrowBack
import androidx.compose.material.icons.outlined.ChevronRight
import androidx.compose.material.icons.outlined.Close
import androidx.compose.material.icons.outlined.Search
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuAnchorType
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
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
import androidx.compose.ui.unit.dp
import ke.co.smartroundclinic.doctor.domain.model.Bank
import ke.co.smartroundclinic.doctor.domain.model.BankBranch
import ke.co.smartroundclinic.doctor.presentation.common.composables.PrimaryButton
import ke.co.smartroundclinic.doctor.presentation.main.profile.PaymentDetailsViewModel
import ke.co.smartroundclinic.doctor.presentation.theme.GradientEnd
import ke.co.smartroundclinic.doctor.presentation.theme.GradientStart
import ke.co.smartroundclinic.doctor.presentation.theme.ShapeInput
import org.koin.compose.viewmodel.koinViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun PaymentDetailsScreen(
    onBack: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: PaymentDetailsViewModel = koinViewModel(),
) {
    val paymentDetails by viewModel.paymentDetails.collectAsState()
    val banks = viewModel.banks
    val isSaving = viewModel.isSaving

    var accountName by remember { mutableStateOf("") }
    var accountNumber by remember { mutableStateOf("") }
    var selectedBank by remember { mutableStateOf<Bank?>(null) }
    var selectedBranch by remember { mutableStateOf<BankBranch?>(null) }
    var branchExpanded by remember { mutableStateOf(false) }

    var showBankPicker by remember { mutableStateOf(false) }
    val bankPickerSheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

    // Auto-fill from cached DB data
    LaunchedEffect(paymentDetails?.id) {
        paymentDetails?.let { details ->
            accountName = details.accountName
            accountNumber = details.accountNumber
            val bank = banks.firstOrNull { it.bankCode == details.bankCode }
            selectedBank = bank
            selectedBranch = bank?.branches?.firstOrNull { it.branchCode == details.branchCode }
        }
    }

    // Re-run fill when banks finish loading (they may arrive after paymentDetails)
    LaunchedEffect(banks.size) {
        if (selectedBank == null && paymentDetails != null) {
            val bank = banks.firstOrNull { it.bankCode == paymentDetails!!.bankCode }
            selectedBank = bank
            selectedBranch = bank?.branches?.firstOrNull { it.branchCode == paymentDetails!!.branchCode }
        }
    }

    if (showBankPicker) {
        BankPickerBottomSheet(
            sheetState = bankPickerSheetState,
            banks = banks,
            onDismiss = { showBankPicker = false },
            onBankSelected = { bank ->
                selectedBank = bank
                selectedBranch = null
                showBankPicker = false
            },
        )
    }

    Scaffold(
        modifier = modifier,
        contentWindowInsets = WindowInsets(0),
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues),
        ) {
            PaymentDetailsHeader(onBack = onBack)

            Column(
                modifier = Modifier
                    .weight(1f)
                    .verticalScroll(rememberScrollState())
                    .padding(horizontal = 20.dp, vertical = 20.dp),
            ) {
                Text(
                    text = "Banking Details",
                    style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.SemiBold),
                    color = MaterialTheme.colorScheme.onSurface,
                )
                Spacer(Modifier.height(4.dp))
                Text(
                    text = "Your details are used to credit your account for incoming payments.",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
                Spacer(Modifier.height(16.dp))

                // Bank selector — read-only, taps open picker sheet
                OutlinedTextField(
                    value = selectedBank?.bankName ?: "",
                    onValueChange = {},
                    readOnly = true,
                    label = { Text("Bank", style = MaterialTheme.typography.bodySmall) },
                    trailingIcon = {
                        Icon(
                            imageVector = Icons.Outlined.Search,
                            contentDescription = "Select bank",
                            tint = MaterialTheme.colorScheme.onSurfaceVariant,
                        )
                    },
                    shape = ShapeInput,
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { showBankPicker = true },
                    enabled = false,
                )
                Spacer(Modifier.height(12.dp))

                // Branch selector — dropdown of branches from selected bank
                ExposedDropdownMenuBox(
                    expanded = branchExpanded,
                    onExpandedChange = { if (selectedBank != null) branchExpanded = it },
                ) {
                    OutlinedTextField(
                        value = selectedBranch?.branchName ?: "",
                        onValueChange = {},
                        readOnly = true,
                        label = { Text("Branch", style = MaterialTheme.typography.bodySmall) },
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = branchExpanded) },
                        shape = ShapeInput,
                        enabled = selectedBank != null,
                        modifier = Modifier
                            .fillMaxWidth()
                            .menuAnchor(ExposedDropdownMenuAnchorType.PrimaryNotEditable),
                    )
                    ExposedDropdownMenu(
                        expanded = branchExpanded,
                        onDismissRequest = { branchExpanded = false },
                    ) {
                        selectedBank?.branches?.forEach { branch ->
                            DropdownMenuItem(
                                text = { Text(branch.branchName) },
                                onClick = {
                                    selectedBranch = branch
                                    branchExpanded = false
                                },
                            )
                        }
                    }
                }
                Spacer(Modifier.height(12.dp))

                OutlinedTextField(
                    value = accountName,
                    onValueChange = { accountName = it },
                    label = { Text("Account Name", style = MaterialTheme.typography.bodySmall) },
                    shape = ShapeInput,
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth(),
                )
                Spacer(Modifier.height(12.dp))
                OutlinedTextField(
                    value = accountNumber,
                    onValueChange = { accountNumber = it },
                    label = { Text("Account Number", style = MaterialTheme.typography.bodySmall) },
                    shape = ShapeInput,
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth(),
                )

                Spacer(Modifier.height(24.dp))
                PrimaryButton(
                    onClick = {
                        val bank = selectedBank ?: return@PrimaryButton
                        val branch = selectedBranch ?: return@PrimaryButton
                        viewModel.savePaymentDetails(
                            accountName = accountName,
                            accountNumber = accountNumber,
                            bank = bank,
                            branch = branch,
                        )
                    },
                    enabled = selectedBank != null && selectedBranch != null &&
                        accountName.isNotBlank() && accountNumber.isNotBlank() && !isSaving,
                ) {
                    if (isSaving) {
                        CircularProgressIndicator(
                            color = Color.White,
                            modifier = Modifier.size(20.dp),
                            strokeWidth = 2.dp,
                        )
                    } else {
                        Text(
                            text = "Save Changes",
                            style = MaterialTheme.typography.labelLarge,
                            color = Color.White,
                            modifier = Modifier.padding(vertical = 14.dp),
                        )
                    }
                }
                Spacer(Modifier.height(16.dp))
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun BankPickerBottomSheet(
    sheetState: androidx.compose.material3.SheetState,
    banks: List<Bank>,
    onDismiss: () -> Unit,
    onBankSelected: (Bank) -> Unit,
) {
    var query by remember { mutableStateOf("") }
    val filtered = remember(query, banks) {
        if (query.isBlank()) banks
        else banks.filter { it.bankName.contains(query, ignoreCase = true) || it.bankCode.contains(query) }
    }

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        containerColor = MaterialTheme.colorScheme.surface,
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .navigationBarsPadding(),
        ) {
            // Header
            Column(modifier = Modifier.padding(horizontal = 20.dp)) {
                Text(
                    text = "Select Bank",
                    style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
                )
                Spacer(Modifier.height(2.dp))
                Text(
                    text = if (query.isBlank()) "${banks.size} banks available"
                           else "${filtered.size} result${if (filtered.size == 1) "" else "s"}",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
                Spacer(Modifier.height(16.dp))
                OutlinedTextField(
                    value = query,
                    onValueChange = { query = it },
                    placeholder = { Text("Search by name or code…", style = MaterialTheme.typography.bodyMedium) },
                    leadingIcon = {
                        Icon(
                            Icons.Outlined.Search,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.onSurfaceVariant,
                        )
                    },
                    trailingIcon = if (query.isNotBlank()) {
                        {
                            IconButton(onClick = { query = "" }) {
                                Icon(
                                    Icons.Outlined.Close,
                                    contentDescription = "Clear",
                                    tint = MaterialTheme.colorScheme.onSurfaceVariant,
                                    modifier = Modifier.size(18.dp),
                                )
                            }
                        }
                    } else null,
                    shape = ShapeInput,
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth(),
                )
                Spacer(Modifier.height(12.dp))
            }

            HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant, thickness = 0.5.dp)

            if (filtered.isEmpty()) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 48.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                ) {
                    Icon(
                        imageVector = Icons.Outlined.Search,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.size(40.dp),
                    )
                    Text(
                        text = "No banks found",
                        style = MaterialTheme.typography.titleSmall,
                        color = MaterialTheme.colorScheme.onSurface,
                    )
                    Text(
                        text = "Try a different search term",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                }
            } else {
                LazyColumn {
                    items(filtered, key = { it.id }) { bank ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable(
                                    indication = null,
                                    interactionSource = remember { MutableInteractionSource() },
                                    onClick = { onBankSelected(bank) },
                                )
                                .padding(horizontal = 20.dp, vertical = 12.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(14.dp),
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(42.dp)
                                    .clip(CircleShape)
                                    .background(
                                        brush = Brush.linearGradient(
                                            listOf(GradientStart.copy(alpha = 0.15f), GradientEnd.copy(alpha = 0.15f))
                                        )
                                    ),
                                contentAlignment = Alignment.Center,
                            ) {
                                Text(
                                    text = bank.bankName.first().uppercaseChar().toString(),
                                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                                    color = GradientStart,
                                )
                            }
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = bank.bankName,
                                    style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Medium),
                                    color = MaterialTheme.colorScheme.onSurface,
                                )
                                Text(
                                    text = "Code: ${bank.bankCode} · ${bank.branches.size} branch${if (bank.branches.size == 1) "" else "es"}",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                                )
                            }
                            Icon(
                                imageVector = Icons.Outlined.ChevronRight,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.onSurfaceVariant,
                                modifier = Modifier.size(18.dp),
                            )
                        }
                        HorizontalDivider(
                            color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f),
                            thickness = 0.5.dp,
                            modifier = Modifier.padding(start = 76.dp, end = 20.dp),
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun PaymentDetailsHeader(onBack: () -> Unit, modifier: Modifier = Modifier) {
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
                text = "Banking Details",
                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.SemiBold),
                color = Color.White,
                modifier = Modifier
                    .align(Alignment.Center)
                    .padding(vertical = 16.dp),
            )
        }
    }
}
