package ke.co.smartroundclinic.doctor.presentation.main.bookings.ui

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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Person
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
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
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
import coil3.compose.AsyncImage
import ke.co.smartroundclinic.doctor.domain.model.Appointment
import ke.co.smartroundclinic.doctor.domain.model.AppointmentStatus
import ke.co.smartroundclinic.doctor.presentation.common.composables.PrimaryButton
import ke.co.smartroundclinic.doctor.presentation.theme.CardBackground
import ke.co.smartroundclinic.doctor.presentation.theme.Primary40
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
    modifier: Modifier = Modifier,
) {
    var showCancelDialog by remember { mutableStateOf(false) }

    if (showCancelDialog) {
        CancelDialog(
            onDismiss = { showCancelDialog = false },
            onConfirm = { reason ->
                showCancelDialog = false
                onCancel(reason.ifBlank { null })
            },
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
                        Row(verticalAlignment = Alignment.CenterVertically) {
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
                            Column {
                                Text(text = "Patient", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                Text(text = appointment.patientName, style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.SemiBold))
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
private fun InfoRow(label: String, value: String) {
    Column {
        Text(text = label, style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
        Spacer(Modifier.height(2.dp))
        Text(text = value, style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.SemiBold))
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
