package ke.co.smartroundclinic.doctor.presentation.main.bookings.ui

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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import ke.co.smartroundclinic.doctor.data.remote.dto.request.PrescriptionItemReq
import ke.co.smartroundclinic.doctor.presentation.common.composables.PrimaryButton
import ke.co.smartroundclinic.doctor.presentation.main.bookings.MedicalRecordViewModel
import ke.co.smartroundclinic.doctor.presentation.theme.CardBackground
import ke.co.smartroundclinic.doctor.presentation.theme.Primary40
import ke.co.smartroundclinic.doctor.presentation.theme.ShapeCard
import ke.co.smartroundclinic.doctor.presentation.theme.ShapeInput

@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun MedicalRecordScreen(
    viewModel: MedicalRecordViewModel,
    appointmentId: String,
    consultationId: String?,
    patientId: String,
    onBack: () -> Unit,
    modifier: Modifier = Modifier,
) {
    LaunchedEffect(appointmentId) { viewModel.loadRecord(appointmentId) }
    LaunchedEffect(viewModel.saveSuccess) {
        if (viewModel.saveSuccess) {
            viewModel.resetSaveSuccess()
            onBack()
        }
    }

    Scaffold(
        modifier = modifier,
        topBar = {
            TopAppBar(
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(imageVector = Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
                title = { Text("Medical Record") },
            )
        },
        contentWindowInsets = WindowInsets(0),
    ) { paddingValues ->
        Column(modifier = Modifier.fillMaxSize().padding(paddingValues)) {
            HorizontalDivider(thickness = 0.5.dp, color = MaterialTheme.colorScheme.outlineVariant)

            if (viewModel.isLoading) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator()
                }
            } else {
                Column(
                    modifier = Modifier
                        .weight(1f)
                        .verticalScroll(rememberScrollState())
                        .padding(horizontal = 16.dp, vertical = 16.dp)
                        .navigationBarsPadding(),
                    verticalArrangement = Arrangement.spacedBy(16.dp),
                ) {
                    SectionCard(title = "Diagnosis") {
                        OutlinedTextField(
                            value = viewModel.diagnosis,
                            onValueChange = { viewModel.diagnosis = it },
                            placeholder = { Text("Enter diagnosis", style = MaterialTheme.typography.bodySmall) },
                            shape = ShapeInput,
                            modifier = Modifier.fillMaxWidth(),
                            minLines = 3,
                        )
                    }

                    SectionCard(title = "Prescription") {
                        viewModel.prescription.forEachIndexed { index, item ->
                            PrescriptionRow(
                                item = item,
                                onUpdate = { viewModel.updatePrescriptionItem(index, it) },
                                onRemove = { viewModel.removePrescriptionItem(index) },
                            )
                            if (index < viewModel.prescription.lastIndex) {
                                HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp), thickness = 0.5.dp, color = MaterialTheme.colorScheme.outlineVariant)
                            }
                        }
                        if (viewModel.prescription.isNotEmpty()) Spacer(Modifier.height(8.dp))
                        TextButton(
                            onClick = { viewModel.addPrescriptionItem() },
                            modifier = Modifier.fillMaxWidth(),
                        ) {
                            Icon(imageVector = Icons.Default.Add, contentDescription = null, tint = Primary40, modifier = Modifier.size(18.dp))
                            Text(" Add Drug", color = Primary40, style = MaterialTheme.typography.labelMedium)
                        }
                    }

                    SectionCard(title = "Lab Requests") {
                        viewModel.labRequests.forEachIndexed { index, value ->
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                OutlinedTextField(
                                    value = value,
                                    onValueChange = { viewModel.updateLabRequest(index, it) },
                                    placeholder = { Text("e.g. Full Blood Count", style = MaterialTheme.typography.bodySmall) },
                                    shape = ShapeInput,
                                    modifier = Modifier.weight(1f),
                                )
                                IconButton(onClick = { viewModel.removeLabRequest(index) }) {
                                    Icon(imageVector = Icons.Default.Close, contentDescription = "Remove", tint = MaterialTheme.colorScheme.error, modifier = Modifier.size(18.dp))
                                }
                            }
                            if (index < viewModel.labRequests.lastIndex) Spacer(Modifier.height(4.dp))
                        }
                        TextButton(
                            onClick = { viewModel.addLabRequest() },
                            modifier = Modifier.fillMaxWidth(),
                        ) {
                            Icon(imageVector = Icons.Default.Add, contentDescription = null, tint = Primary40, modifier = Modifier.size(18.dp))
                            Text(" Add Lab Request", color = Primary40, style = MaterialTheme.typography.labelMedium)
                        }
                    }

                    SectionCard(title = "Clinical Summary") {
                        OutlinedTextField(
                            value = viewModel.summary,
                            onValueChange = { viewModel.summary = it },
                            placeholder = { Text("Summary of the consultation", style = MaterialTheme.typography.bodySmall) },
                            shape = ShapeInput,
                            modifier = Modifier.fillMaxWidth(),
                            minLines = 3,
                        )
                    }

                    SectionCard(title = "Referral Note") {
                        OutlinedTextField(
                            value = viewModel.referralNote,
                            onValueChange = { viewModel.referralNote = it },
                            placeholder = { Text("Referral details (optional)", style = MaterialTheme.typography.bodySmall) },
                            shape = ShapeInput,
                            modifier = Modifier.fillMaxWidth(),
                            minLines = 2,
                        )
                    }

                    SectionCard(title = "Additional Notes") {
                        OutlinedTextField(
                            value = viewModel.additionalNotes,
                            onValueChange = { viewModel.additionalNotes = it },
                            placeholder = { Text("Any other notes", style = MaterialTheme.typography.bodySmall) },
                            shape = ShapeInput,
                            modifier = Modifier.fillMaxWidth(),
                            minLines = 2,
                        )
                    }

                    PrimaryButton(
                        onClick = { viewModel.save(appointmentId, consultationId, patientId) },
                        enabled = !viewModel.isSaving,
                    ) {
                        if (viewModel.isSaving) {
                            CircularProgressIndicator(modifier = Modifier.size(20.dp), strokeWidth = 2.dp, color = Color.White)
                        } else {
                            Text("Save & Send to Patient", style = MaterialTheme.typography.labelLarge, color = Color.White, modifier = Modifier.padding(vertical = 14.dp))
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun SectionCard(title: String, content: @Composable () -> Unit) {
    Card(
        shape = ShapeCard,
        colors = CardDefaults.cardColors(containerColor = CardBackground),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
    ) {
        Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
            Text(text = title, style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.SemiBold))
            content()
        }
    }
}

@Composable
private fun PrescriptionRow(
    item: PrescriptionItemReq,
    onUpdate: (PrescriptionItemReq) -> Unit,
    onRemove: () -> Unit,
) {
    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text("Drug", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant, modifier = Modifier.weight(1f))
            IconButton(onClick = onRemove) {
                Icon(imageVector = Icons.Default.Close, contentDescription = "Remove drug", tint = MaterialTheme.colorScheme.error, modifier = Modifier.size(18.dp))
            }
        }
        OutlinedTextField(
            value = item.drug,
            onValueChange = { onUpdate(item.copy(drug = it)) },
            placeholder = { Text("Drug name", style = MaterialTheme.typography.bodySmall) },
            shape = ShapeInput,
            modifier = Modifier.fillMaxWidth(),
        )
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            OutlinedTextField(
                value = item.dosage,
                onValueChange = { onUpdate(item.copy(dosage = it)) },
                placeholder = { Text("Dosage", style = MaterialTheme.typography.bodySmall) },
                shape = ShapeInput,
                modifier = Modifier.weight(1f),
            )
            OutlinedTextField(
                value = item.frequency,
                onValueChange = { onUpdate(item.copy(frequency = it)) },
                placeholder = { Text("Frequency", style = MaterialTheme.typography.bodySmall) },
                shape = ShapeInput,
                modifier = Modifier.weight(1f),
            )
        }
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            OutlinedTextField(
                value = item.duration,
                onValueChange = { onUpdate(item.copy(duration = it)) },
                placeholder = { Text("Duration", style = MaterialTheme.typography.bodySmall) },
                shape = ShapeInput,
                modifier = Modifier.weight(1f),
            )
            OutlinedTextField(
                value = item.instructions ?: "",
                onValueChange = { onUpdate(item.copy(instructions = it.ifBlank { null })) },
                placeholder = { Text("Instructions", style = MaterialTheme.typography.bodySmall) },
                shape = ShapeInput,
                modifier = Modifier.weight(1f),
            )
        }
    }
}
