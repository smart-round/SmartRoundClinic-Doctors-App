package ke.co.smartroundclinic.doctor.presentation.main.bookings.ui

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
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
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import ke.co.smartroundclinic.doctor.data.remote.dto.request.PrescriptionItemReq
import ke.co.smartroundclinic.doctor.presentation.common.composables.PrimaryButton
import ke.co.smartroundclinic.doctor.presentation.main.bookings.MedicalRecordViewModel
import ke.co.smartroundclinic.doctor.presentation.theme.CardBackground
import ke.co.smartroundclinic.doctor.presentation.theme.Neutral40
import ke.co.smartroundclinic.doctor.presentation.theme.Neutral80
import ke.co.smartroundclinic.doctor.presentation.theme.Primary40
import ke.co.smartroundclinic.doctor.presentation.theme.Primary90
import ke.co.smartroundclinic.doctor.presentation.theme.ShapeCard
import ke.co.smartroundclinic.doctor.presentation.theme.ShapeInput
import ke.co.smartroundclinic.doctor.presentation.theme.ShapePill

private val FREQUENCY_OPTIONS = listOf(
    "Once daily",
    "Twice daily",
    "Three times daily",
    "Every 8 hours",
    "Every 12 hours",
    "As needed (PRN)",
    "Weekly",
)

private val DOSAGE_UNITS = listOf("mg", "ml", "mcg", "IU")
private val DURATION_UNITS = listOf("Days", "Weeks", "Months")

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
                title = {
                    Column {
                        Text(
                            text = "Medical Record",
                            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                        )
                        Text(
                            text = "Fill in clinical details below",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                        )
                    }
                },
            )
        },
        bottomBar = {
            Column(
                modifier = Modifier
                    .background(MaterialTheme.colorScheme.surface)
                    .navigationBarsPadding(),
            ) {
                HorizontalDivider(thickness = 0.5.dp, color = MaterialTheme.colorScheme.outlineVariant)
                Box(modifier = Modifier.padding(horizontal = 20.dp, vertical = 12.dp)) {
                    PrimaryButton(
                        onClick = { viewModel.save(appointmentId, consultationId, patientId) },
                        enabled = !viewModel.isSaving,
                    ) {
                        if (viewModel.isSaving) {
                            CircularProgressIndicator(
                                modifier = Modifier.size(20.dp),
                                strokeWidth = 2.dp,
                                color = Color.White,
                            )
                        } else {
                            Text(
                                text = "Save & Send to Patient",
                                style = MaterialTheme.typography.labelLarge,
                                color = Color.White,
                                modifier = Modifier.padding(vertical = 14.dp),
                            )
                        }
                    }
                }
            }
        },
        contentWindowInsets = WindowInsets(0),
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .background(MaterialTheme.colorScheme.background),
        ) {
            HorizontalDivider(thickness = 0.5.dp, color = MaterialTheme.colorScheme.outlineVariant)

            if (viewModel.isLoading) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator(color = Primary40)
                }
            } else {
                Column(
                    modifier = Modifier
                        .weight(1f)
                        .verticalScroll(rememberScrollState())
                        .padding(horizontal = 20.dp)
                        .padding(top = 24.dp, bottom = 12.dp),
                    verticalArrangement = Arrangement.spacedBy(28.dp),
                ) {
                    // ── Diagnosis ───────────────────────────────────────────────
                    FormSection(label = "Diagnosis") {
                        OutlinedTextField(
                            value = viewModel.diagnosis,
                            onValueChange = { viewModel.diagnosis = it },
                            placeholder = {
                                Text(
                                    "Describe the patient's diagnosis…",
                                    style = MaterialTheme.typography.bodySmall,
                                )
                            },
                            shape = ShapeInput,
                            modifier = Modifier.fillMaxWidth(),
                            minLines = 3,
                            colors = inputColors(),
                        )
                    }

                    // ── Prescription ────────────────────────────────────────────
                    FormSection(label = "Prescription") {
                        Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                            viewModel.prescription.forEachIndexed { index, item ->
                                PrescriptionRow(
                                    item = item,
                                    index = index,
                                    onUpdate = { viewModel.updatePrescriptionItem(index, it) },
                                    onRemove = { viewModel.removePrescriptionItem(index) },
                                )
                            }
                            AddRowButton(
                                label = "Add Drug",
                                accent = true,
                                onClick = { viewModel.addPrescriptionItem() },
                            )
                        }
                    }

                    // ── Lab Requests ────────────────────────────────────────────
                    FormSection(label = "Lab Requests") {
                        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                            viewModel.labRequests.forEachIndexed { index, value ->
                                OutlinedTextField(
                                    value = value,
                                    onValueChange = { viewModel.updateLabRequest(index, it) },
                                    placeholder = {
                                        Text(
                                            "e.g. Full Blood Count",
                                            style = MaterialTheme.typography.bodySmall,
                                        )
                                    },
                                    shape = ShapeInput,
                                    modifier = Modifier.fillMaxWidth(),
                                    singleLine = true,
                                    colors = inputColors(),
                                    leadingIcon = {
                                        Text(
                                            text = "${index + 1}",
                                            style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                                            color = Primary40,
                                            modifier = Modifier.padding(start = 4.dp),
                                        )
                                    },
                                    trailingIcon = {
                                        IconButton(
                                            onClick = { viewModel.removeLabRequest(index) },
                                            modifier = Modifier.size(36.dp),
                                        ) {
                                            Icon(
                                                imageVector = Icons.Default.Close,
                                                contentDescription = "Remove",
                                                tint = MaterialTheme.colorScheme.error,
                                                modifier = Modifier.size(16.dp),
                                            )
                                        }
                                    },
                                )
                            }
                            AddRowButton(
                                label = "Add Lab Request",
                                accent = false,
                                onClick = { viewModel.addLabRequest() },
                            )
                        }
                    }

                    // ── Clinical Notes ──────────────────────────────────────────
                    FormSection(label = "Clinical Notes") {
                        Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                            OutlinedTextField(
                                value = viewModel.summary,
                                onValueChange = { viewModel.summary = it },
                                label = { Text("Summary") },
                                placeholder = {
                                    Text(
                                        "Summary of the consultation",
                                        style = MaterialTheme.typography.bodySmall,
                                    )
                                },
                                shape = ShapeInput,
                                modifier = Modifier.fillMaxWidth(),
                                minLines = 3,
                                colors = inputColors(),
                            )
                            OutlinedTextField(
                                value = viewModel.referralNote,
                                onValueChange = { viewModel.referralNote = it },
                                label = { Text("Referral Note (optional)") },
                                placeholder = {
                                    Text(
                                        "Referral details if applicable",
                                        style = MaterialTheme.typography.bodySmall,
                                    )
                                },
                                shape = ShapeInput,
                                modifier = Modifier.fillMaxWidth(),
                                minLines = 2,
                                colors = inputColors(),
                            )
                            OutlinedTextField(
                                value = viewModel.additionalNotes,
                                onValueChange = { viewModel.additionalNotes = it },
                                label = { Text("Additional Notes (optional)") },
                                placeholder = {
                                    Text(
                                        "Any other notes",
                                        style = MaterialTheme.typography.bodySmall,
                                    )
                                },
                                shape = ShapeInput,
                                modifier = Modifier.fillMaxWidth(),
                                minLines = 2,
                                colors = inputColors(),
                            )
                        }
                    }

                    Spacer(Modifier.height(4.dp))
                }
            }
        }
    }
}

@Composable
private fun PrescriptionRow(
    item: PrescriptionItemReq,
    index: Int,
    onUpdate: (PrescriptionItemReq) -> Unit,
    onRemove: () -> Unit,
) {
    val (initDosageAmount, initDosageUnit) = remember(item.drug) {
        val parts = item.dosage.trim().split(" ")
        val unit = parts.getOrNull(1)?.let { u -> DOSAGE_UNITS.firstOrNull { it.equals(u, ignoreCase = true) } } ?: "mg"
        (parts.getOrNull(0)?.filter { it.isDigit() || it == '.' } ?: "") to unit
    }
    val initFrequency = remember(item.drug) {
        FREQUENCY_OPTIONS.firstOrNull { it.equals(item.frequency.trim(), ignoreCase = true) } ?: item.frequency
    }
    val (initDurationCount, initDurationUnit) = remember(item.drug) {
        val parts = item.duration.trim().split(" ")
        val unit = parts.getOrNull(1)?.let { u -> DURATION_UNITS.firstOrNull { it.equals(u, ignoreCase = true) } } ?: "Days"
        (parts.getOrNull(0)?.filter { it.isDigit() } ?: "") to unit
    }

    var dosageAmount by remember { mutableStateOf(initDosageAmount) }
    var dosageUnit by remember { mutableStateOf(initDosageUnit) }
    var selectedFrequency by remember { mutableStateOf(initFrequency) }
    var durationCount by remember { mutableStateOf(initDurationCount) }
    var durationUnit by remember { mutableStateOf(initDurationUnit) }
    var instructions by remember { mutableStateOf(item.instructions ?: "") }

    fun pushUpdate(
        drug: String = item.drug,
        amount: String = dosageAmount,
        unit: String = dosageUnit,
        freq: String = selectedFrequency,
        count: String = durationCount,
        durUnit: String = durationUnit,
        instr: String = instructions,
    ) {
        val dosage = if (amount.isNotBlank()) "$amount $unit" else ""
        val duration = if (count.isNotBlank()) "$count $durUnit" else ""
        onUpdate(item.copy(drug = drug, dosage = dosage, frequency = freq, duration = duration, instructions = instr.ifBlank { null }))
    }

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = ShapeCard,
        colors = CardDefaults.cardColors(containerColor = CardBackground),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
    ) {
        Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(14.dp)) {

            // Header: numbered badge + label + remove
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier.size(26.dp).clip(CircleShape).background(Primary40),
                    contentAlignment = Alignment.Center,
                ) {
                    Text(
                        text = "${index + 1}",
                        style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                        color = Color.White,
                    )
                }
                Spacer(Modifier.width(10.dp))
                Text(
                    text = "Drug ${index + 1}",
                    style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.SemiBold),
                    color = Primary40,
                    modifier = Modifier.weight(1f),
                )
                Box(
                    modifier = Modifier
                        .size(28.dp)
                        .clip(CircleShape)
                        .background(MaterialTheme.colorScheme.errorContainer)
                        .clickable(
                            indication = null,
                            interactionSource = remember { MutableInteractionSource() },
                            onClick = onRemove,
                        ),
                    contentAlignment = Alignment.Center,
                ) {
                    Icon(
                        imageVector = Icons.Default.Close,
                        contentDescription = "Remove drug",
                        tint = MaterialTheme.colorScheme.error,
                        modifier = Modifier.size(14.dp),
                    )
                }
            }

            HorizontalDivider(thickness = 0.5.dp, color = MaterialTheme.colorScheme.outlineVariant)

            // Drug name
            OutlinedTextField(
                value = item.drug,
                onValueChange = { pushUpdate(drug = it) },
                label = { Text("Drug Name") },
                placeholder = { Text("Generic / brand name", style = MaterialTheme.typography.bodySmall) },
                shape = ShapeInput,
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                colors = inputColors(),
            )

            // Dosage and Duration stacked — full card width for each
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                    FieldLabel("Dosage")
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(10.dp),
                    ) {
                        OutlinedTextField(
                            value = dosageAmount,
                            onValueChange = { v ->
                                dosageAmount = v.filter { it.isDigit() || it == '.' }
                                pushUpdate(amount = dosageAmount)
                            },
                            placeholder = { Text("e.g. 500", style = MaterialTheme.typography.bodySmall) },
                            shape = ShapeInput,
                            modifier = Modifier.weight(1f),
                            singleLine = true,
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                            colors = inputColors(),
                        )
                        UnitToggle(
                            options = listOf("mg", "ml"),
                            selected = dosageUnit,
                            onSelect = { dosageUnit = it; pushUpdate(unit = it) },
                        )
                    }
                }

                Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                    FieldLabel("Duration")
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(10.dp),
                    ) {
                        OutlinedTextField(
                            value = durationCount,
                            onValueChange = { v ->
                                durationCount = v.filter { it.isDigit() }
                                pushUpdate(count = durationCount)
                            },
                            placeholder = { Text("e.g. 7", style = MaterialTheme.typography.bodySmall) },
                            shape = ShapeInput,
                            modifier = Modifier.weight(1f),
                            singleLine = true,
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                            colors = inputColors(),
                        )
                        UnitToggle(
                            options = listOf("Days", "Wks", "Mos"),
                            optionValues = listOf("Days", "Weeks", "Months"),
                            selected = when (durationUnit) { "Weeks" -> "Wks"; "Months" -> "Mos"; else -> "Days" },
                            onSelect = { value ->
                                durationUnit = value
                                pushUpdate(durUnit = value)
                            },
                        )
                    }
                }
            }

            // Frequency — 2-column grid instead of unpredictable FlowRow
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                FieldLabel("Frequency")
                Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                    FREQUENCY_OPTIONS.chunked(2).forEach { pair ->
                        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            pair.forEach { option ->
                                SelectionChip(
                                    label = option,
                                    selected = selectedFrequency == option,
                                    onClick = { selectedFrequency = option; pushUpdate(freq = option) },
                                    modifier = Modifier.weight(1f),
                                )
                            }
                            if (pair.size == 1) Spacer(Modifier.weight(1f))
                        }
                    }
                }
            }

            // Instructions — plain text field, no chip wall
            OutlinedTextField(
                value = instructions,
                onValueChange = { instructions = it; pushUpdate(instr = it) },
                label = { Text("Instructions (optional)") },
                placeholder = { Text("e.g. Take with food, at bedtime", style = MaterialTheme.typography.bodySmall) },
                shape = ShapeInput,
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                colors = inputColors(),
            )
        }
    }
}

@Composable
private fun FormSection(label: String, content: @Composable () -> Unit) {
    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(10.dp),
        ) {
            Box(
                modifier = Modifier
                    .size(width = 3.dp, height = 18.dp)
                    .clip(RoundedCornerShape(2.dp))
                    .background(Primary40),
            )
            Text(
                text = label,
                style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                color = MaterialTheme.colorScheme.onSurface,
            )
        }
        content()
    }
}

@Composable
private fun AddRowButton(label: String, accent: Boolean, onClick: () -> Unit) {
    val borderColor = if (accent) Primary40.copy(alpha = 0.5f) else MaterialTheme.colorScheme.outlineVariant
    val iconTint = if (accent) Primary40 else MaterialTheme.colorScheme.onSurfaceVariant
    val textColor = if (accent) Primary40 else MaterialTheme.colorScheme.onSurfaceVariant
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(ShapeCard)
            .border(1.dp, borderColor, ShapeCard)
            .clickable(
                indication = null,
                interactionSource = remember { MutableInteractionSource() },
                onClick = onClick,
            )
            .padding(vertical = 14.dp),
        contentAlignment = Alignment.Center,
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            Icon(
                imageVector = Icons.Default.Add,
                contentDescription = null,
                tint = iconTint,
                modifier = Modifier.size(18.dp),
            )
            Text(
                text = label,
                style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.SemiBold),
                color = textColor,
            )
        }
    }
}

@Composable
private fun UnitToggle(
    options: List<String>,
    selected: String,
    onSelect: (String) -> Unit,
    optionValues: List<String> = options,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier
            .border(1.dp, Neutral80, ShapeInput)
            .background(Color.White, ShapeInput),
        horizontalArrangement = Arrangement.Start,
    ) {
        options.forEachIndexed { index, label ->
            val isSelected = label == selected
            Box(
                modifier = Modifier
                    .background(if (isSelected) Primary40 else Color.Transparent, ShapeInput)
                    .clickable { onSelect(optionValues.getOrElse(index) { label }) }
                    .padding(horizontal = 10.dp, vertical = 8.dp),
                contentAlignment = Alignment.Center,
            ) {
                Text(
                    text = label,
                    style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Medium),
                    color = if (isSelected) Color.White else Neutral40,
                )
            }
        }
    }
}

@Composable
private fun SelectionChip(
    label: String,
    selected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Box(
        modifier = modifier
            .border(BorderStroke(1.dp, if (selected) Primary40 else Neutral80), ShapePill)
            .background(if (selected) Primary90 else Color.Transparent, ShapePill)
            .clickable(onClick = onClick)
            .padding(horizontal = 12.dp, vertical = 9.dp),
        contentAlignment = Alignment.Center,
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Medium),
            color = if (selected) Primary40 else Neutral40,
            textAlign = TextAlign.Center,
        )
    }
}

@Composable
private fun FieldLabel(text: String) {
    Text(
        text = text,
        style = MaterialTheme.typography.labelSmall,
        color = MaterialTheme.colorScheme.onSurfaceVariant,
    )
}

@Composable
private fun inputColors() = OutlinedTextFieldDefaults.colors(
    focusedBorderColor = Primary40,
    unfocusedBorderColor = Neutral80,
    focusedLabelColor = Primary40,
    cursorColor = Primary40,
)

