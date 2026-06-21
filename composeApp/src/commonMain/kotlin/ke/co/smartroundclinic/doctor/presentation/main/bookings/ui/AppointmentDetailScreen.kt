package ke.co.smartroundclinic.doctor.presentation.main.bookings.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
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
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material.icons.filled.Person
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.material3.AlertDialog
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
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SheetState
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil3.compose.AsyncImage
import ke.co.smartroundclinic.doctor.domain.model.Appointment
import ke.co.smartroundclinic.doctor.domain.model.AppointmentStatus
import ke.co.smartroundclinic.doctor.domain.model.MedicalRecord
import ke.co.smartroundclinic.doctor.domain.model.PatientBio
import ke.co.smartroundclinic.doctor.presentation.common.composables.PrimaryButton
import ke.co.smartroundclinic.doctor.presentation.theme.CardBackground
import ke.co.smartroundclinic.doctor.presentation.theme.Neutral40
import ke.co.smartroundclinic.doctor.presentation.theme.Neutral80
import ke.co.smartroundclinic.doctor.presentation.theme.Neutral90
import ke.co.smartroundclinic.doctor.presentation.theme.Primary40
import ke.co.smartroundclinic.doctor.presentation.theme.Primary90
import ke.co.smartroundclinic.doctor.presentation.theme.Secondary40
import ke.co.smartroundclinic.doctor.presentation.theme.Secondary90
import ke.co.smartroundclinic.doctor.presentation.theme.ShapeBadge
import ke.co.smartroundclinic.doctor.presentation.theme.ShapeCard
import ke.co.smartroundclinic.doctor.presentation.theme.ShapeInput
import ke.co.smartroundclinic.doctor.presentation.theme.ShapePill
import ke.co.smartroundclinic.doctor.presentation.theme.Tertiary40
import ke.co.smartroundclinic.doctor.presentation.theme.Tertiary90

@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun AppointmentDetailScreen(
    appointment: Appointment,
    isActioning: Boolean,
    onBack: () -> Unit,
    onConfirm: () -> Unit,
    onComplete: () -> Unit,
    onNoShow: () -> Unit,
    onCancel: (String?) -> Unit,
    medicalRecord: MedicalRecord? = null,
    patientHistory: List<MedicalRecord> = emptyList(),
    patientBio: PatientBio? = null,
    onAddMedicalRecord: () -> Unit = {},
    modifier: Modifier = Modifier,
) {
    val canAddRecord = appointment.status == AppointmentStatus.CONFIRMED || appointment.status == AppointmentStatus.COMPLETED
    var showCancelDialog by remember { mutableStateOf(false) }
    var showPatientBioSheet by remember { mutableStateOf(false) }
    var showHistorySheet by remember { mutableStateOf(false) }
    val bioSheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    val historySheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

    if (showCancelDialog) {
        CancelDialog(
            onDismiss = { showCancelDialog = false },
            onConfirm = { reason ->
                showCancelDialog = false
                onCancel(reason.ifBlank { null })
            },
        )
    }

    if (showPatientBioSheet) {
        PatientBioSheet(
            patientName = appointment.patientName,
            patientPicture = appointment.patientProfilePicture,
            bio = patientBio,
            sheetState = bioSheetState,
            onDismiss = { showPatientBioSheet = false },
        )
    }

    if (showHistorySheet) {
        MedicalHistorySheet(
            records = patientHistory,
            currentAppointmentId = appointment.id,
            canAddRecord = canAddRecord,
            sheetState = historySheetState,
            onDismiss = { showHistorySheet = false },
            onAddOrEdit = onAddMedicalRecord,
        )
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
                title = { Text("Appointment Details") },
                actions = {
                    AppointmentStatusBadge(status = appointment.status)
                    Spacer(Modifier.width(8.dp))
                },
            )
        },
        contentWindowInsets = WindowInsets(0),
    ) { paddingValues ->
        val hasHistory = patientHistory.isNotEmpty() || canAddRecord

        Column(modifier = Modifier.fillMaxSize().padding(paddingValues)) {
            HorizontalDivider(thickness = 0.5.dp, color = MaterialTheme.colorScheme.outlineVariant)

            Column(
                modifier = Modifier
                    .weight(1f)
                    .verticalScroll(rememberScrollState())
                    .padding(horizontal = 16.dp, vertical = 16.dp)
                    .navigationBarsPadding(),
                verticalArrangement = Arrangement.spacedBy(16.dp),
            ) {
                Card(
                    shape = ShapeCard,
                    colors = CardDefaults.cardColors(containerColor = CardBackground),
                    elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        InfoRow(label = "Date", value = appointment.date)
                        HorizontalDivider(modifier = Modifier.padding(vertical = 12.dp), thickness = 0.5.dp, color = MaterialTheme.colorScheme.outlineVariant)
                        InfoRow(label = "Time", value = "${appointment.slotStart} – ${appointment.slotEnd}")
                        HorizontalDivider(modifier = Modifier.padding(vertical = 12.dp), thickness = 0.5.dp, color = MaterialTheme.colorScheme.outlineVariant)
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.fillMaxWidth(),
                        ) {
                            Box(
                                modifier = Modifier.size(40.dp).clip(CircleShape).background(Secondary90),
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
                                    Icon(imageVector = Icons.Filled.Person, contentDescription = null, tint = Secondary40, modifier = Modifier.size(24.dp))
                                }
                            }
                            Spacer(Modifier.width(12.dp))
                            Column(modifier = Modifier.weight(1f)) {
                                Text(text = "Patient", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                Text(text = appointment.patientName, style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.SemiBold))
                            }
                            Column(horizontalAlignment = Alignment.End, verticalArrangement = Arrangement.spacedBy(2.dp)) {
                                OutlinedButton(
                                    onClick = { showPatientBioSheet = true },
                                    shape = ShapePill,
                                    border = androidx.compose.foundation.BorderStroke(1.dp, Primary40),
                                    contentPadding = androidx.compose.foundation.layout.PaddingValues(horizontal = 12.dp, vertical = 4.dp),
                                ) {
                                    Icon(imageVector = Icons.Filled.Person, contentDescription = null, tint = Primary40, modifier = Modifier.size(13.dp))
                                    Spacer(Modifier.width(4.dp))
                                    Text("Bio", style = MaterialTheme.typography.labelSmall, color = Primary40)
                                }
                                if (hasHistory) {
                                    TextButton(
                                        onClick = { showHistorySheet = true },
                                        contentPadding = androidx.compose.foundation.layout.PaddingValues(horizontal = 4.dp, vertical = 0.dp),
                                    ) {
                                        Text("Medical History", style = MaterialTheme.typography.labelSmall, color = Tertiary40)
                                    }
                                }
                            }
                        }
                        if (!appointment.notes.isNullOrBlank()) {
                            HorizontalDivider(modifier = Modifier.padding(vertical = 12.dp), thickness = 0.5.dp, color = MaterialTheme.colorScheme.outlineVariant)
                            InfoRow(label = "Notes", value = appointment.notes)
                        }
                        if (!appointment.cancellationReason.isNullOrBlank()) {
                            HorizontalDivider(modifier = Modifier.padding(vertical = 12.dp), thickness = 0.5.dp, color = MaterialTheme.colorScheme.outlineVariant)
                            InfoRow(label = "Cancellation Reason", value = appointment.cancellationReason)
                        }
                        if (appointment.doctorSpecialities.isNotEmpty()) {
                            HorizontalDivider(modifier = Modifier.padding(vertical = 12.dp), thickness = 0.5.dp, color = MaterialTheme.colorScheme.outlineVariant)
                            InfoRow(label = "Speciality", value = appointment.doctorSpecialities.joinToString(", "))
                        }
                    }
                }

                if (isActioning) {
                    Box(modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
                        CircularProgressIndicator(modifier = Modifier.size(32.dp), strokeWidth = 2.dp)
                    }
                } else {
                    when (appointment.status) {
                        AppointmentStatus.BOOKED -> {
                            PrimaryButton(onClick = onConfirm) {
                                Text("Confirm Appointment", style = MaterialTheme.typography.labelLarge, color = Color.White, modifier = Modifier.padding(vertical = 14.dp))
                            }
                            Button(
                                onClick = { showCancelDialog = true },
                                modifier = Modifier.fillMaxWidth(),
                                shape = ShapePill,
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = MaterialTheme.colorScheme.errorContainer,
                                    contentColor = MaterialTheme.colorScheme.error,
                                ),
                            ) {
                                Text("Cancel Appointment", style = MaterialTheme.typography.labelLarge, modifier = Modifier.padding(vertical = 10.dp))
                            }
                        }
                        AppointmentStatus.CONFIRMED -> {
                            PrimaryButton(onClick = onComplete) {
                                Text("Mark as Complete", style = MaterialTheme.typography.labelLarge, color = Color.White, modifier = Modifier.padding(vertical = 14.dp))
                            }
                            Button(
                                onClick = onNoShow,
                                modifier = Modifier.fillMaxWidth(),
                                shape = ShapePill,
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = MaterialTheme.colorScheme.surfaceVariant,
                                    contentColor = MaterialTheme.colorScheme.onSurfaceVariant,
                                ),
                            ) {
                                Text("Patient No-Show", style = MaterialTheme.typography.labelLarge, modifier = Modifier.padding(vertical = 10.dp))
                            }
                            Button(
                                onClick = { showCancelDialog = true },
                                modifier = Modifier.fillMaxWidth(),
                                shape = ShapePill,
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = MaterialTheme.colorScheme.errorContainer,
                                    contentColor = MaterialTheme.colorScheme.error,
                                ),
                            ) {
                                Text("Cancel Appointment", style = MaterialTheme.typography.labelLarge, modifier = Modifier.padding(vertical = 10.dp))
                            }
                        }
                        AppointmentStatus.COMPLETED -> {
                            Card(
                                modifier = Modifier.fillMaxWidth(),
                                shape = ShapeCard,
                                colors = CardDefaults.cardColors(containerColor = Tertiary90),
                            ) {
                                Text(
                                    text = "This appointment has been completed.",
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = Tertiary40,
                                    modifier = Modifier.padding(16.dp),
                                )
                            }
                        }
                        AppointmentStatus.CANCELLED -> {
                            Card(
                                modifier = Modifier.fillMaxWidth(),
                                shape = ShapeCard,
                                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.errorContainer),
                            ) {
                                Text(
                                    text = "This appointment was cancelled.",
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = MaterialTheme.colorScheme.error,
                                    modifier = Modifier.padding(16.dp),
                                )
                            }
                        }
                        AppointmentStatus.NO_SHOW -> {
                            Card(
                                modifier = Modifier.fillMaxWidth(),
                                shape = ShapeCard,
                                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.errorContainer),
                            ) {
                                Text(
                                    text = "Patient did not attend this appointment.",
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = MaterialTheme.colorScheme.error,
                                    modifier = Modifier.padding(16.dp),
                                )
                            }
                        }
                    }
                }

                Spacer(Modifier.height(8.dp))
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
internal fun PatientBioSheet(
    patientName: String,
    patientPicture: String?,
    bio: PatientBio?,
    sheetState: SheetState,
    onDismiss: () -> Unit,
) {
    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        containerColor = MaterialTheme.colorScheme.background,
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 20.dp)
                .navigationBarsPadding()
                .padding(bottom = 24.dp),
            verticalArrangement = Arrangement.spacedBy(20.dp),
        ) {
            // Header
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier.size(48.dp).clip(CircleShape).background(Secondary90),
                    contentAlignment = Alignment.Center,
                ) {
                    if (patientPicture != null) {
                        AsyncImage(
                            model = patientPicture,
                            contentDescription = patientName,
                            contentScale = ContentScale.Crop,
                            modifier = Modifier.fillMaxSize().clip(CircleShape),
                        )
                    } else {
                        Icon(imageVector = Icons.Filled.Person, contentDescription = null, tint = Secondary40, modifier = Modifier.size(28.dp))
                    }
                }
                Spacer(Modifier.width(12.dp))
                Column {
                    Text(
                        text = patientName,
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.SemiBold),
                    )
                    Text(
                        text = "Patient Profile",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                }
            }

            HorizontalDivider(thickness = 0.5.dp, color = MaterialTheme.colorScheme.outlineVariant)

            if (bio == null) {
                Box(modifier = Modifier.fillMaxWidth().padding(vertical = 32.dp), contentAlignment = Alignment.Center) {
                    Text(
                        text = "No bio on record",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                }
            } else {
                // Physical metrics
                BioSectionHeader("Physical Metrics")
                Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    BioMetricCard(
                        label = "Weight",
                        value = if (bio.weight != null) "${bio.weight} ${bio.weightIn ?: ""}".trim() else "—",
                        modifier = Modifier.weight(1f),
                    )
                    BioMetricCard(
                        label = "Height",
                        value = if (bio.height != null) "${bio.height} ${bio.heightIn ?: ""}".trim() else "—",
                        modifier = Modifier.weight(1f),
                    )
                    BioMetricCard(
                        label = "Blood Group",
                        value = bio.bloodGroup ?: "—",
                        modifier = Modifier.weight(1f),
                    )
                }
                if (!bio.maritalStatus.isNullOrBlank()) {
                    BioInfoRow(label = "Marital Status", value = bio.maritalStatus)
                }

                // Allergies
                if (bio.allergies.isNotEmpty()) {
                    BioSectionHeader("Allergies")
                    FlowRow(horizontalArrangement = Arrangement.spacedBy(8.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        bio.allergies.forEach { allergy ->
                            BioPill(text = allergy, containerColor = MaterialTheme.colorScheme.errorContainer, textColor = MaterialTheme.colorScheme.error)
                        }
                    }
                }

                // Chronic conditions
                if (bio.chronicConditions.isNotEmpty()) {
                    BioSectionHeader("Chronic Conditions")
                    FlowRow(horizontalArrangement = Arrangement.spacedBy(8.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        bio.chronicConditions.forEach { condition ->
                            BioPill(text = condition, containerColor = Primary90, textColor = Primary40)
                        }
                    }
                }

                // Current medications
                if (bio.currentMedications.isNotEmpty()) {
                    BioSectionHeader("Current Medications")
                    Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                        bio.currentMedications.forEachIndexed { index, med ->
                            Row(verticalAlignment = Alignment.Top) {
                                Text(
                                    text = "${index + 1}.",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = Neutral40,
                                    modifier = Modifier.width(20.dp),
                                )
                                Text(text = med, style = MaterialTheme.typography.bodySmall)
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun BioSectionHeader(title: String) {
    Text(
        text = title.uppercase(),
        style = MaterialTheme.typography.labelSmall.copy(letterSpacing = 1.sp, fontWeight = FontWeight.Bold),
        color = MaterialTheme.colorScheme.onSurfaceVariant,
    )
}

@Composable
private fun BioMetricCard(label: String, value: String, modifier: Modifier = Modifier) {
    Card(
        modifier = modifier,
        shape = ShapeCard,
        colors = CardDefaults.cardColors(containerColor = Neutral90),
        elevation = CardDefaults.cardElevation(0.dp),
    ) {
        Column(
            modifier = Modifier.padding(12.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(4.dp),
        ) {
            Text(
                text = value,
                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                color = MaterialTheme.colorScheme.onSurface,
            )
            Text(
                text = label,
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
        }
    }
}

@Composable
private fun BioInfoRow(label: String, value: String) {
    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
        Text(text = label, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
        Text(text = value, style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.SemiBold))
    }
}

@Composable
private fun BioPill(text: String, containerColor: Color, textColor: Color) {
    Box(
        modifier = Modifier.background(containerColor, ShapePill).padding(horizontal = 10.dp, vertical = 4.dp),
    ) {
        Text(text = text, style = MaterialTheme.typography.labelSmall, color = textColor)
    }
}

@Composable
private fun MedicalRecordSection(
    record: MedicalRecord?,
    canEdit: Boolean,
    onAddOrEdit: () -> Unit,
) {
    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween,
        ) {
            Text("Medical Record", style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.SemiBold))
            if (canEdit) {
                TextButton(onClick = onAddOrEdit) {
                    Text(if (record != null) "Edit" else "Add", style = MaterialTheme.typography.labelMedium)
                }
            }
        }

        if (record != null) {
            Card(
                shape = ShapeCard,
                colors = CardDefaults.cardColors(containerColor = Primary40.copy(alpha = 0.06f)),
            ) {
                Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    if (!record.diagnosis.isNullOrBlank()) {
                        Column {
                            Text("Diagnosis", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                            Text(record.diagnosis, style = MaterialTheme.typography.bodyMedium)
                        }
                    }
                    if (record.prescription.isNotEmpty()) {
                        Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                            Text("Prescription", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                            record.prescription.forEach { item ->
                                Text(
                                    text = "• ${item.drug} — ${item.dosage}, ${item.frequency} for ${item.duration}",
                                    style = MaterialTheme.typography.bodySmall,
                                )
                            }
                        }
                    }
                    if (!record.summary.isNullOrBlank()) {
                        Column {
                            Text("Summary", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                            Text(record.summary, style = MaterialTheme.typography.bodySmall)
                        }
                    }
                    if (!record.referralNote.isNullOrBlank()) {
                        Column {
                            Text("Referral", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                            Text(record.referralNote, style = MaterialTheme.typography.bodySmall)
                        }
                    }
                }
            }
        } else if (canEdit) {
            OutlinedButton(
                onClick = onAddOrEdit,
                modifier = Modifier.fillMaxWidth(),
                shape = ShapePill,
                border = androidx.compose.foundation.BorderStroke(1.dp, Tertiary40),
                colors = ButtonDefaults.outlinedButtonColors(contentColor = Tertiary40),
            ) {
                Text(
                    "Add Medical Record",
                    style = MaterialTheme.typography.labelLarge,
                    color = Tertiary40,
                    modifier = Modifier.padding(vertical = 10.dp),
                )
            }
        }
    }
}

@Composable
internal fun AppointmentStatusBadge(status: AppointmentStatus, modifier: Modifier = Modifier) {
    val (label, bgColor, textColor) = when (status) {
        AppointmentStatus.BOOKED -> Triple("Booked", Tertiary90, Tertiary40)
        AppointmentStatus.CONFIRMED -> Triple("Confirmed", Primary40.copy(alpha = 0.12f), Primary40)
        AppointmentStatus.COMPLETED -> Triple("Completed", Tertiary90, Tertiary40)
        AppointmentStatus.CANCELLED -> Triple("Cancelled", MaterialTheme.colorScheme.errorContainer, MaterialTheme.colorScheme.error)
        AppointmentStatus.NO_SHOW -> Triple("No-Show", MaterialTheme.colorScheme.errorContainer, MaterialTheme.colorScheme.error)
    }
    Box(modifier = modifier.clip(ShapeBadge).background(bgColor).padding(horizontal = 10.dp, vertical = 4.dp)) {
        Text(text = label, style = MaterialTheme.typography.labelSmall, color = textColor)
    }
}

@Composable
private fun InfoRow(label: String, value: String, modifier: Modifier = Modifier) {
    Column(modifier = modifier) {
        Text(text = label, style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
        Spacer(Modifier.height(2.dp))
        Text(text = value, style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.SemiBold))
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun MedicalHistorySheet(
    records: List<MedicalRecord>,
    currentAppointmentId: String,
    canAddRecord: Boolean,
    sheetState: SheetState,
    onDismiss: () -> Unit,
    onAddOrEdit: () -> Unit,
) {
    val sorted = records.sortedByDescending { it.createdAt }
    val hasCurrentRecord = records.any { it.appointmentId == currentAppointmentId }

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        containerColor = MaterialTheme.colorScheme.background,
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 20.dp)
                .navigationBarsPadding()
                .padding(bottom = 24.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = "Medical History",
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                    )
                    if (records.isNotEmpty()) {
                        Text(
                            text = "${records.size} record${if (records.size == 1) "" else "s"}",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                        )
                    }
                }
                if (canAddRecord) {
                    OutlinedButton(
                        onClick = { onDismiss(); onAddOrEdit() },
                        shape = ShapePill,
                        border = androidx.compose.foundation.BorderStroke(1.dp, Tertiary40),
                        contentPadding = androidx.compose.foundation.layout.PaddingValues(horizontal = 14.dp, vertical = 6.dp),
                    ) {
                        Text(
                            text = if (hasCurrentRecord) "Edit Record" else "Add Medical Record",
                            style = MaterialTheme.typography.labelSmall,
                            color = Tertiary40,
                        )
                    }
                }
            }

            HorizontalDivider(thickness = 0.5.dp, color = MaterialTheme.colorScheme.outlineVariant)

            if (sorted.isEmpty()) {
                Box(
                    modifier = Modifier.fillMaxWidth().padding(vertical = 32.dp),
                    contentAlignment = Alignment.Center,
                ) {
                    Text(
                        text = "No medical records yet",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                }
            } else {
                sorted.forEach { record ->
                    HistoryRecordCard(
                        record = record,
                        isEditable = record.appointmentId == currentAppointmentId && canAddRecord,
                        onEdit = { onDismiss(); onAddOrEdit() },
                    )
                }
            }
        }
    }
}

@Composable
private fun HistoryRecordCard(
    record: MedicalRecord,
    isEditable: Boolean = false,
    onEdit: () -> Unit = {},
) {
    var expanded by remember { mutableStateOf(false) }
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = ShapeCard,
        colors = CardDefaults.cardColors(containerColor = CardBackground),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable(
                        indication = null,
                        interactionSource = remember { MutableInteractionSource() },
                        onClick = { expanded = !expanded },
                    ),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = record.createdAt.take(10),
                        style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.SemiBold),
                        color = Primary40,
                    )
                    if (!record.diagnosis.isNullOrBlank()) {
                        Spacer(Modifier.height(2.dp))
                        Text(
                            text = record.diagnosis,
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            maxLines = if (expanded) Int.MAX_VALUE else 1,
                            overflow = TextOverflow.Ellipsis,
                        )
                    }
                }
                if (isEditable) {
                    TextButton(
                        onClick = onEdit,
                        contentPadding = androidx.compose.foundation.layout.PaddingValues(horizontal = 8.dp, vertical = 4.dp),
                    ) {
                        Text("Edit", style = MaterialTheme.typography.labelSmall, color = Tertiary40)
                    }
                }
                Icon(
                    imageVector = if (expanded) Icons.Default.KeyboardArrowUp else Icons.Default.KeyboardArrowDown,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.size(20.dp),
                )
            }

            if (expanded) {
                Spacer(Modifier.height(8.dp))
                HorizontalDivider(thickness = 0.5.dp, color = MaterialTheme.colorScheme.outlineVariant)
                Spacer(Modifier.height(10.dp))

                if (record.prescription.isNotEmpty()) {
                    Text(
                        text = "Prescription",
                        style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.SemiBold),
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                    Spacer(Modifier.height(6.dp))
                    record.prescription.forEach { item ->
                        Row(
                            modifier = Modifier.padding(vertical = 3.dp),
                            verticalAlignment = Alignment.Top,
                        ) {
                            Box(
                                modifier = Modifier
                                    .padding(top = 5.dp)
                                    .size(5.dp)
                                    .clip(CircleShape)
                                    .background(Primary40),
                            )
                            Spacer(Modifier.width(8.dp))
                            Column {
                                Text(
                                    text = item.drug,
                                    style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.SemiBold),
                                )
                                Text(
                                    text = listOfNotNull(
                                        item.dosage.ifBlank { null },
                                        item.frequency.ifBlank { null },
                                        item.duration.ifBlank { null },
                                    ).joinToString(" · "),
                                    style = MaterialTheme.typography.labelSmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                                )
                            }
                        }
                    }
                }

                if (record.labRequests.isNotEmpty()) {
                    Spacer(Modifier.height(10.dp))
                    Text(
                        text = "Lab Requests",
                        style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.SemiBold),
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                    Spacer(Modifier.height(4.dp))
                    record.labRequests.forEach { lab ->
                        Text(
                            text = "• $lab",
                            style = MaterialTheme.typography.bodySmall,
                            modifier = Modifier.padding(vertical = 1.dp),
                        )
                    }
                }

                if (!record.summary.isNullOrBlank()) {
                    Spacer(Modifier.height(10.dp))
                    Text(
                        text = "Summary",
                        style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.SemiBold),
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                    Spacer(Modifier.height(4.dp))
                    Text(text = record.summary, style = MaterialTheme.typography.bodySmall)
                }

                if (!record.referralNote.isNullOrBlank()) {
                    Spacer(Modifier.height(10.dp))
                    Text(
                        text = "Referral Note",
                        style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.SemiBold),
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                    Spacer(Modifier.height(4.dp))
                    Text(text = record.referralNote, style = MaterialTheme.typography.bodySmall)
                }
            }
        }
    }
}

@Composable
private fun CancelDialog(
    onDismiss: () -> Unit,
    onConfirm: (String) -> Unit,
) {
    var reason by remember { mutableStateOf("") }
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Cancel Appointment") },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text("Are you sure you want to cancel this appointment?", style = MaterialTheme.typography.bodyMedium)
                OutlinedTextField(
                    value = reason,
                    onValueChange = { reason = it },
                    placeholder = { Text("Reason (optional)", style = MaterialTheme.typography.bodySmall) },
                    shape = ShapeInput,
                    modifier = Modifier.fillMaxWidth(),
                )
            }
        },
        confirmButton = {
            TextButton(onClick = { onConfirm(reason) }) {
                Text("Cancel Appointment", color = MaterialTheme.colorScheme.error)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Keep") }
        },
    )
}

