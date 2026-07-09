package ke.co.smartroundclinic.doctor.presentation.signup.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.ButtonDefaults
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
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import ke.co.smartroundclinic.doctor.presentation.common.composables.PrimaryButton
import ke.co.smartroundclinic.doctor.presentation.signup.BioData
import ke.co.smartroundclinic.doctor.presentation.signup.SignUpFormViewModel
import ke.co.smartroundclinic.doctor.presentation.theme.ShapeInput

private val TITLE_OPTIONS = listOf("Dr.", "Prof.", "Mr.", "Mrs.", "Ms.")

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DoctorBioScreen(
    formViewModel: SignUpFormViewModel,
    onNext: (BioData) -> Unit,
    onBack: () -> Unit,
) {
    var titleExpanded by remember { mutableStateOf(false) }

    val isFormValid = formViewModel.bio.isNotBlank() && formViewModel.yearsOfExperience.isNotBlank()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .imePadding(),
    ) {
        // Scrollable form content
        Column(
            modifier = Modifier
                .weight(1f)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 16.dp, vertical = 16.dp),
        ) {

            SignUpField(
                value = formViewModel.kmpdcRegNumber,
                onValueChange = { formViewModel.kmpdcRegNumber = it },
                label = "KMPDC Reg. Number (Optional)",
                placeholder = "e.g. MED-12345",
            )

            Spacer(Modifier.height(12.dp))

            OutlinedTextField(
                value = formViewModel.bio,
                onValueChange = { formViewModel.bio = it },
                label = { Text("Bio *", style = MaterialTheme.typography.bodySmall) },
                placeholder = { Text("Tell patients about yourself", style = MaterialTheme.typography.bodySmall) },
                shape = ShapeInput,
                minLines = 4,
                maxLines = 6,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Text),
                modifier = Modifier.fillMaxWidth(),
            )

            Spacer(Modifier.height(12.dp))

            SignUpField(
                value = formViewModel.yearsOfExperience,
                onValueChange = { formViewModel.yearsOfExperience = it.filter { c -> c.isDigit() } },
                label = "Years of Experience *",
                placeholder = "e.g. 5",
                keyboardType = KeyboardType.Number,
            )

            Spacer(Modifier.height(12.dp))

            SignUpField(
                value = formViewModel.languages,
                onValueChange = { formViewModel.languages = it },
                label = "Languages Spoken (Optional)",
                placeholder = "e.g. English, Swahili, French",
            )

            Spacer(Modifier.height(12.dp))

            SignUpField(
                value = formViewModel.facilityName,
                onValueChange = { formViewModel.facilityName = it },
                label = "Facility / Hospital Name (Optional)",
                placeholder = "e.g. Nairobi Hospital",
            )

            Spacer(Modifier.height(8.dp))
        }

        // Fixed footer
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .background(MaterialTheme.colorScheme.background)
                .padding(start = 16.dp, end = 16.dp, top = 12.dp, bottom = 16.dp),
        ) {
            PrimaryButton(
                onClick = {
                    onNext(
                        BioData(
                            kmpdcRegNumber = formViewModel.kmpdcRegNumber,
                            title = formViewModel.doctorTitle,
                            bio = formViewModel.bio,
                            yearsOfExperience = formViewModel.yearsOfExperience,
                            languages = formViewModel.languages,
                            facilityName = formViewModel.facilityName,
                        )
                    )
                },
                enabled = isFormValid,
            ) {
                Text(
                    text = "Next",
                    style = MaterialTheme.typography.labelLarge,
                    color = MaterialTheme.colorScheme.onPrimary,
                    modifier = Modifier.padding(vertical = 14.dp),
                )
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
}
