package ke.co.smartroundclinic.doctor.presentation.signup.ui

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuAnchorType
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.MenuDefaults
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import ke.co.smartroundclinic.doctor.domain.model.Bank
import ke.co.smartroundclinic.doctor.domain.model.BankBranch
import ke.co.smartroundclinic.doctor.presentation.common.composables.PrimaryButton
import ke.co.smartroundclinic.doctor.presentation.signup.BankDetailsViewModel
import ke.co.smartroundclinic.doctor.presentation.signup.PersonalInfoData
import ke.co.smartroundclinic.doctor.presentation.signup.SignUpFilesViewModel
import ke.co.smartroundclinic.doctor.presentation.signup.SpecializationData
import ke.co.smartroundclinic.doctor.presentation.theme.ShapeInput
import org.koin.compose.viewmodel.koinViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BankDetailsScreen(
    filesViewModel: SignUpFilesViewModel,
    personalInfo: PersonalInfoData,
    specializationData: SpecializationData,
    onNext: () -> Unit,
    onBack: () -> Unit,
    viewModel: BankDetailsViewModel = koinViewModel(),
) {
    val allBanks by viewModel.banks.collectAsStateWithLifecycle()
    val isBanksLoading by viewModel.isLoading.collectAsStateWithLifecycle()
    val banksError by viewModel.error.collectAsStateWithLifecycle()
    val isSubmitting by viewModel.isSubmitting.collectAsStateWithLifecycle()

    var bankQuery by remember { mutableStateOf("") }
    var bankExpanded by remember { mutableStateOf(false) }
    var selectedBank by remember { mutableStateOf<Bank?>(null) }
    var branchQuery by remember { mutableStateOf("") }
    var branchExpanded by remember { mutableStateOf(false) }
    var selectedBranch by remember { mutableStateOf<BankBranch?>(null) }
    var accountNumber by remember { mutableStateOf("") }
    var accountName by remember { mutableStateOf("") }
    var agreedToTerms by remember { mutableStateOf(false) }

    val filteredBanks = remember(bankQuery, allBanks) {
        if (bankQuery.isEmpty()) allBanks
        else allBanks.filter { it.bankName.contains(bankQuery, ignoreCase = true) }
    }

    val filteredBranches = remember(branchQuery, selectedBank) {
        val branches = selectedBank?.branches ?: emptyList()
        if (branchQuery.isEmpty()) branches
        else branches.filter { it.branchName.contains(branchQuery, ignoreCase = true) }
    }

    val isFormValid = selectedBank != null &&
        selectedBranch != null &&
        accountNumber.isNotBlank() &&
        accountName.isNotBlank() &&
        agreedToTerms &&
        filesViewModel.licenseFileBytes != null

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 16.dp, vertical = 16.dp),
    ) {
        // Bank search
        ExposedDropdownMenuBox(
            expanded = bankExpanded && filteredBanks.isNotEmpty(),
            onExpandedChange = { bankExpanded = it },
            modifier = Modifier.fillMaxWidth(),
        ) {
            OutlinedTextField(
                value = bankQuery,
                onValueChange = {
                    bankQuery = it
                    selectedBank = null
                    selectedBranch = null
                    branchQuery = ""
                    bankExpanded = true
                },
                label = { Text("Bank", style = MaterialTheme.typography.bodySmall) },
                placeholder = { Text("Search bank by name", style = MaterialTheme.typography.bodySmall) },
                trailingIcon = {
                    if (isBanksLoading) CircularProgressIndicator(modifier = Modifier.size(20.dp), strokeWidth = 2.dp)
                    else ExposedDropdownMenuDefaults.TrailingIcon(bankExpanded)
                },
                enabled = !isBanksLoading,
                singleLine = true,
                shape = ShapeInput,
                modifier = Modifier
                    .fillMaxWidth()
                    .menuAnchor(ExposedDropdownMenuAnchorType.PrimaryEditable),
            )
            ExposedDropdownMenu(
                containerColor =  MaterialTheme.colorScheme.background,
                expanded = bankExpanded && filteredBanks.isNotEmpty(),
                onDismissRequest = { bankExpanded = false },
            ) {
                filteredBanks.forEach { bank ->
                    DropdownMenuItem(
                        colors = MenuDefaults.itemColors(textColor = MaterialTheme.colorScheme.onBackground),
                        text = { Text(bank.bankName, style = MaterialTheme.typography.bodyMedium) },
                        onClick = {
                            selectedBank = bank
                            bankQuery = bank.bankName
                            selectedBranch = null
                            branchQuery = ""
                            bankExpanded = false
                        },
                    )
                }
            }
        }

        if (banksError != null) {
            Text(
                text = banksError!!,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.error,
                modifier = Modifier.padding(top = 4.dp, start = 4.dp),
            )
        }

        Spacer(Modifier.height(12.dp))

        // Branch search — visible once a bank is selected
        if (selectedBank != null) {
            ExposedDropdownMenuBox(
                expanded = branchExpanded && filteredBranches.isNotEmpty(),
                onExpandedChange = { branchExpanded = it },
                modifier = Modifier.fillMaxWidth(),
            ) {
                OutlinedTextField(
                    value = branchQuery,
                    onValueChange = {
                        branchQuery = it
                        selectedBranch = null
                        branchExpanded = true
                    },
                    label = { Text("Branch", style = MaterialTheme.typography.bodySmall) },
                    placeholder = { Text("Search branch by name", style = MaterialTheme.typography.bodySmall) },
                    trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(branchExpanded) },
                    singleLine = true,
                    shape = ShapeInput,
                    modifier = Modifier
                        .fillMaxWidth()
                        .menuAnchor(ExposedDropdownMenuAnchorType.PrimaryEditable),
                )
                ExposedDropdownMenu(
                    expanded = branchExpanded && filteredBranches.isNotEmpty(),
                    onDismissRequest = { branchExpanded = false },
                    containerColor = MaterialTheme.colorScheme.background,
                ) {
                    filteredBranches.forEach { branch ->
                        DropdownMenuItem(
                            colors = MenuDefaults.itemColors(textColor = MaterialTheme.colorScheme.onBackground),
                            text = {
                                Text(
                                    "${branch.branchName} (${branch.branchCode})",
                                    style = MaterialTheme.typography.bodyMedium,
                                )
                            },
                            onClick = {
                                selectedBranch = branch
                                branchQuery = branch.branchName
                                branchExpanded = false
                            },
                        )
                    }
                }
            }

            Spacer(Modifier.height(12.dp))
        }

        SignUpField(
            value = accountNumber,
            onValueChange = { accountNumber = it },
            label = "Account Number",
            placeholder = "Enter account number",
            keyboardType = KeyboardType.Number,
        )

        Spacer(Modifier.height(12.dp))

        SignUpField(
            value = accountName,
            onValueChange = { accountName = it },
            label = "Account Holder Name",
            placeholder = "Enter account holder name",
        )

        Spacer(Modifier.height(16.dp))

        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.fillMaxWidth(),
        ) {
            Checkbox(
                checked = agreedToTerms,
                onCheckedChange = { agreedToTerms = it },
                colors = CheckboxDefaults.colors(checkedColor = MaterialTheme.colorScheme.primary),
            )
            Text(
                text = buildAnnotatedString {
                    append("I agree to the ")
                    withStyle(SpanStyle(color = MaterialTheme.colorScheme.primary)) { append("Terms and Conditions") }
                    append(" / ")
                    withStyle(SpanStyle(color = MaterialTheme.colorScheme.primary)) { append("Privacy Policy") }
                },
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onBackground,
            )
        }

        Spacer(Modifier.height(8.dp))

        Text(
            text = "Your details are complete and will only be used to credit your PayBase account for incoming payments.",
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.padding(horizontal = 4.dp),
        )

        Spacer(Modifier.height(24.dp))

        PrimaryButton(
            onClick = {
                val bank = selectedBank ?: return@PrimaryButton
                val branch = selectedBranch ?: return@PrimaryButton
                viewModel.signUp(
                    personalInfo = personalInfo,
                    specialization = specializationData,
                    licenseFileBytes = filesViewModel.licenseFileBytes!!,
                    licenseFileName = specializationData.specializationName,
                    profilePictureBytes = filesViewModel.profilePictureBytes,
                    bankName = bank.bankName,
                    bankCode = bank.bankCode,
                    branchCode = branch.branchCode,
                    branchName = branch.branchName,
                    accountName = accountName,
                    accountNumber = accountNumber,
                    onSuccess = onNext,
                )
            },
            enabled = isFormValid && !isSubmitting,
        ) {
            if (isSubmitting) {
                Box(contentAlignment = Alignment.Center, modifier = Modifier.padding(vertical = 14.dp)) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(20.dp),
                        strokeWidth = 2.dp,
                        color = MaterialTheme.colorScheme.onPrimary,
                    )
                }
            } else {
                Text(
                    text = "Submit",
                    style = MaterialTheme.typography.labelLarge,
                    color = MaterialTheme.colorScheme.onPrimary,
                    modifier = Modifier.padding(vertical = 14.dp),
                )
            }
        }

        Spacer(Modifier.height(4.dp))

        TextButton(
            onClick = onBack,
            modifier = Modifier.fillMaxWidth(),
            colors = ButtonDefaults.textButtonColors(contentColor = MaterialTheme.colorScheme.onBackground),
        ) {
            Text(text = "Back", style = MaterialTheme.typography.bodyMedium)
        }
    }
}
