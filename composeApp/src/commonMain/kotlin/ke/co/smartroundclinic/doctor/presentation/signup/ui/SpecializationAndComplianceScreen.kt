package ke.co.smartroundclinic.doctor.presentation.signup.ui

import androidx.compose.foundation.background
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CreateNewFolder
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuAnchorType
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.MenuDefaults
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import io.github.vinceglb.filekit.dialogs.FileKitType
import io.github.vinceglb.filekit.dialogs.compose.rememberFilePickerLauncher
import io.github.vinceglb.filekit.mimeType
import io.github.vinceglb.filekit.name
import io.github.vinceglb.filekit.readBytes
import ke.co.smartroundclinic.doctor.presentation.common.composables.PrimaryButton
import ke.co.smartroundclinic.doctor.presentation.signup.SignUpFilesViewModel
import ke.co.smartroundclinic.doctor.presentation.signup.SpecializationComplianceViewModel
import ke.co.smartroundclinic.doctor.presentation.signup.SpecializationData
import ke.co.smartroundclinic.doctor.presentation.theme.ShapeInput
import kotlinx.coroutines.launch
import org.jetbrains.compose.resources.painterResource
import org.koin.compose.viewmodel.koinViewModel
import smartroundclinic.composeapp.generated.resources.Res
import smartroundclinic.composeapp.generated.resources.file_icon
import androidx.compose.ui.graphics.Color
import smartroundclinic.composeapp.generated.resources.get_files

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SpecializationAndComplianceScreen(
    filesViewModel: SignUpFilesViewModel,
    formViewModel: ke.co.smartroundclinic.doctor.presentation.signup.SignUpFormViewModel,
    onNext: (SpecializationData) -> Unit,
    onBack: () -> Unit,
    viewModel: SpecializationComplianceViewModel = koinViewModel(),
) {
    val allSpecialities by viewModel.specialities.collectAsStateWithLifecycle()
    val isLoading by viewModel.isLoading.collectAsStateWithLifecycle()
    val error by viewModel.error.collectAsStateWithLifecycle()

    // UI-only state — fine to reset; pre-fill query from persisted selection so the
    // dropdown shows the chosen name when navigating back
    var specialityQuery by remember { mutableStateOf(formViewModel.selectedSpecialityName) }
    var specialityExpanded by remember { mutableStateOf(false) }
    val scope = rememberCoroutineScope()

    val filteredSpecialities = remember(specialityQuery, allSpecialities) {
        if (specialityQuery.isEmpty()) allSpecialities
        else allSpecialities.filter { it.title.contains(specialityQuery, ignoreCase = true) }
    }

    val isFormValid = formViewModel.selectedSpecialityId.isNotBlank() &&
        filesViewModel.licenseFileBytes != null

    val filePickerLauncher = rememberFilePickerLauncher(type = FileKitType.File()) { file ->
        scope.launch {
            filesViewModel.licenseFileName = file?.name ?: ""
            filesViewModel.licenseFileMimeType = file?.mimeType()?.toString() ?: "application/octet-stream"
            filesViewModel.licenseFileBytes = file?.readBytes()
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 16.dp, vertical = 16.dp),
    ) {
        ExposedDropdownMenuBox(
            expanded = specialityExpanded && filteredSpecialities.isNotEmpty(),
            onExpandedChange = { specialityExpanded = it },
            modifier = Modifier.fillMaxWidth(),
        ) {
            OutlinedTextField(
                value = specialityQuery,
                onValueChange = {
                    specialityQuery = it
                    formViewModel.selectedSpecialityId = ""
                    formViewModel.selectedSpecialityName = ""
                    specialityExpanded = true
                },
                label = { Text("Field of Practice", style = MaterialTheme.typography.bodySmall) },
                placeholder = { Text("Search field of Practice", style = MaterialTheme.typography.bodySmall) },
                trailingIcon = {
                    if (isLoading) CircularProgressIndicator(modifier = Modifier.size(20.dp), strokeWidth = 2.dp)
                    else ExposedDropdownMenuDefaults.TrailingIcon(specialityExpanded)
                },
                enabled = !isLoading,
                singleLine = true,
                shape = ShapeInput,
                modifier = Modifier
                    .fillMaxWidth()
                    .menuAnchor(ExposedDropdownMenuAnchorType.PrimaryEditable),
            )
            ExposedDropdownMenu(
                expanded = specialityExpanded && filteredSpecialities.isNotEmpty(),
                onDismissRequest = { specialityExpanded = false },
                containerColor = MaterialTheme.colorScheme.background
            ) {
                filteredSpecialities.forEach { speciality ->
                    DropdownMenuItem(
                        text = { Text(speciality.title, style = MaterialTheme.typography.bodyMedium) },
                        colors = MenuDefaults.itemColors(
                            textColor = MaterialTheme.colorScheme.onBackground
                        ),
                        onClick = {
                            formViewModel.selectedSpecialityId = speciality.id
                            formViewModel.selectedSpecialityName = speciality.title
                            specialityQuery = speciality.title
                            specialityExpanded = false
                        },
                    )
                }
            }
        }

        if (error != null) {
            Text(
                text = error!!,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.error,
                modifier = Modifier.padding(top = 4.dp, start = 4.dp),
            )
        }
        Spacer(Modifier.height(20.dp))

        val primaryColor = MaterialTheme.colorScheme.primary
        val backgroundColor = MaterialTheme.colorScheme.background
        // Outer wrapper — label floats over the top border line
        Box(modifier = Modifier.fillMaxWidth()) {
            // Dashed border box pushed down so the top border sits behind the label
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 9.dp)
                    .drawBehind {
                        drawRoundRect(
                            color = primaryColor,
                            style = Stroke(
                                width = 1.dp.toPx(),
                                pathEffect = PathEffect.dashPathEffect(floatArrayOf(12f, 8f), 0f),
                            ),
                            cornerRadius = CornerRadius(4.dp.toPx()),
                        )
                    }
                    .padding(horizontal = 20.dp, vertical = 20.dp),
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.fillMaxWidth(),
                ) {
                    Box(
                        contentAlignment = Alignment.Center,
                        modifier = Modifier
                            .size(64.dp)
                            .clip(CircleShape)
                            .background(MaterialTheme.colorScheme.primaryContainer),
                    ) {
                        Icon(
                            painter = painterResource(Res.drawable.get_files),
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.onPrimaryContainer,
                            modifier = Modifier.size(32.dp),
                        )
                    }

                    Text(
                        text = "Upload Your License Of Practice",
                        style = MaterialTheme.typography.titleSmall,
                        color = MaterialTheme.colorScheme.onBackground,
                        textAlign = TextAlign.Center,
                    )

                    Text(
                        text = if (filesViewModel.licenseFileName.isEmpty())
                            "PDF/JPEG/PNG (Max 5MB)"
                        else
                            filesViewModel.licenseFileName,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        textAlign = TextAlign.Center,
                    )

                    Spacer(Modifier.height(4.dp))

                    OutlinedButton(
                        onClick = { filePickerLauncher.launch() },
                        shape = ShapeInput,
                        colors = ButtonDefaults.outlinedButtonColors(
                            contentColor = MaterialTheme.colorScheme.primary,
                        ),
                        border = androidx.compose.foundation.BorderStroke(
                            1.dp,
                            MaterialTheme.colorScheme.primary,
                        ),
                        modifier = Modifier.fillMaxWidth(0.6f),
                    ) {
                        Text(
                            text = "Choose File",
                            style = MaterialTheme.typography.bodyMedium,
                            modifier = Modifier.padding(vertical = 4.dp),
                        )
                    }
                }
            }

            // Label sits on the top border line with a background cutout
            Text(
                text = "License of Practice",
                style = MaterialTheme.typography.labelSmall,
                color = primaryColor,
                modifier = Modifier
                    .align(Alignment.TopStart)
                    .padding(start = 16.dp)
                    .background(backgroundColor)
                    .padding(horizontal = 4.dp),
            )
        }

        Spacer(Modifier.height(8.dp))

        Text(
            text = "Your document is secure and shall only be used for account verification purposes.",
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )

        Spacer(Modifier.height(32.dp))

        PrimaryButton(
            onClick = {
                onNext(
                    SpecializationData(
                        specializationId = formViewModel.selectedSpecialityId,
                        specializationName = formViewModel.selectedSpecialityName,
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
