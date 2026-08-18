package ke.co.smartroundclinic.doctor.presentation.main.bookings.ui

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.WarningAmber
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.runtime.derivedStateOf
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
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
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil3.compose.AsyncImage
import ke.co.smartroundclinic.doctor.core.util.formatAppointmentDate
import ke.co.smartroundclinic.doctor.core.util.formatAppointmentTimeRange
import ke.co.smartroundclinic.doctor.core.util.parseAppointmentInstant
import ke.co.smartroundclinic.doctor.domain.model.Appointment
import ke.co.smartroundclinic.doctor.domain.model.AppointmentStatus
import ke.co.smartroundclinic.doctor.domain.model.MedicalRecord
import ke.co.smartroundclinic.doctor.domain.model.PatientBio
import ke.co.smartroundclinic.doctor.domain.model.Rating
import kotlinx.coroutines.delay
import kotlinx.datetime.Clock
import ke.co.smartroundclinic.doctor.presentation.common.composables.PrimaryButton
import ke.co.smartroundclinic.doctor.presentation.common.composables.StarRatingDisplay
import ke.co.smartroundclinic.doctor.presentation.common.composables.StarRatingInput
import ke.co.smartroundclinic.doctor.presentation.theme.ActionDestructive
import ke.co.smartroundclinic.doctor.presentation.theme.ActionDisabled
import ke.co.smartroundclinic.doctor.presentation.theme.ActionEnabled
import ke.co.smartroundclinic.doctor.presentation.theme.CardBackground
import ke.co.smartroundclinic.doctor.presentation.theme.DetailIconChip
import ke.co.smartroundclinic.doctor.presentation.theme.Error40
import ke.co.smartroundclinic.doctor.presentation.theme.Error90
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
import org.jetbrains.compose.resources.painterResource
import smartroundclinic.composeapp.generated.resources.Res
import smartroundclinic.composeapp.generated.resources.refer_icon

@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun AppointmentDetailScreen(
    appointment: Appointment,
    isActioning: Boolean,
    onBack: () -> Unit,
    onConfirm: () -> Unit,
    onComplete: () -> Unit,
    onCancel: (String?) -> Unit,
    onRefer: () -> Unit = {},
    medicalRecord: MedicalRecord? = null,
    patientHistory: List<MedicalRecord> = emptyList(),
    patientBio: PatientBio? = null,
    onAddMedicalRecord: () -> Unit = {},
    myRatingOfPatient: Rating? = null,
    isLoadingRatings: Boolean = false,
    isSubmittingRating: Boolean = false,
    onSubmitRating: (Int, String?) -> Unit = { _, _ -> },
    onUpdateRating: (Int, String?) -> Unit = { _, _ -> },
    onDeleteRating: () -> Unit = {},
    patientAverageRating: Double = 0.0,
    patientTotalReviews: Int = 0,
    patientReviews: List<Rating> = emptyList(),
    isLoadingPatientReviews: Boolean = false,
    isLoadingMorePatientReviews: Boolean = false,
    hasMorePatientReviews: Boolean = false,
    onOpenPatientReviews: () -> Unit = {},
    onLoadMorePatientReviews: () -> Unit = {},
    modifier: Modifier = Modifier,
) {
    val canAddRecord = appointment.status == AppointmentStatus.CONFIRMED || appointment.status == AppointmentStatus.COMPLETED
    var now by remember { mutableStateOf(Clock.System.now()) }
    LaunchedEffect(Unit) {
        while (true) {
            delay(30_000)
            now = Clock.System.now()
        }
    }
    val slotStartInstant = remember(appointment.date, appointment.slotStart) {
        parseAppointmentInstant(appointment.date, appointment.slotStart)
    }
    val canComplete = slotStartInstant == null || now >= slotStartInstant
    var showCancelDialog by remember { mutableStateOf(false) }
    var showCompleteConfirmSheet by remember { mutableStateOf(false) }
    var showPatientBioSheet by remember { mutableStateOf(false) }
    var showHistorySheet by remember { mutableStateOf(false) }
    var showReviewsSheet by remember { mutableStateOf(false) }
    val bioSheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    val historySheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

    if (showReviewsSheet) {
        LaunchedEffect(Unit) { onOpenPatientReviews() }
        PatientRatingsSheet(
            averageRating = patientAverageRating,
            totalReviews = patientTotalReviews,
            reviews = patientReviews,
            isLoading = isLoadingPatientReviews,
            isLoadingMore = isLoadingMorePatientReviews,
            hasMore = hasMorePatientReviews,
            onLoadMore = onLoadMorePatientReviews,
            onDismiss = { showReviewsSheet = false },
        )
    }

    if (showCancelDialog) {
        CancelBottomSheet(
            onDismiss = { showCancelDialog = false },
            onConfirm = { reason ->
                showCancelDialog = false
                onCancel(reason)
            },
        )
    }

    if (showCompleteConfirmSheet) {
        CompleteConfirmationBottomSheet(
            onDismiss = { showCompleteConfirmSheet = false },
            onConfirm = {
                showCompleteConfirmSheet = false
                onComplete()
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
                // The status now lives on the summary card itself, so the bar carries only the title.
                title = { Text("Appointment Details", style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold)) },
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
                AccentCard {
                    Column {
                        Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.fillMaxWidth()) {
                            Text(
                                text = "Appointment Date/Time",
                                style = MaterialTheme.typography.bodyMedium,
                                color = Neutral40,
                                modifier = Modifier.weight(1f),
                            )
                            AppointmentStatusBadge(status = appointment.status)
                        }

                        Spacer(Modifier.height(12.dp))
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier.size(24.dp).clip(CircleShape).background(DetailIconChip),
                                contentAlignment = Alignment.Center,
                            ) {
                                Icon(
                                    imageVector = Icons.Filled.Schedule,
                                    contentDescription = null,
                                    tint = Color.White,
                                    modifier = Modifier.size(14.dp),
                                )
                            }
                            Spacer(Modifier.width(10.dp))
                            Text(
                                text = formatAppointmentDate(appointment.date),
                                style = MaterialTheme.typography.bodyMedium,
                            )
                            Spacer(Modifier.width(10.dp))
                            Box(Modifier.size(6.dp).clip(CircleShape).background(DetailIconChip.copy(alpha = 0.48f)))
                            Spacer(Modifier.width(10.dp))
                            Text(
                                text = formatAppointmentTimeRange(appointment.slotStart, appointment.slotEnd),
                                style = MaterialTheme.typography.bodyMedium,
                            )
                        }

                        HorizontalDivider(
                            modifier = Modifier.padding(vertical = 12.dp),
                            thickness = 1.dp,
                            color = DetailIconChip.copy(alpha = 0.26f),
                        )

                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.fillMaxWidth(),
                        ) {
                            Box(
                                modifier = Modifier.size(47.dp).clip(CircleShape).background(Secondary90),
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
                                    Icon(imageVector = Icons.Filled.Person, contentDescription = null, tint = Secondary40, modifier = Modifier.size(28.dp))
                                }
                            }
                            Spacer(Modifier.width(12.dp))
                            Text(
                                text = appointment.patientName,
                                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                                maxLines = 2,
                                overflow = TextOverflow.Ellipsis,
                                modifier = Modifier.weight(1f),
                            )
                            Spacer(Modifier.width(8.dp))
                            Column(horizontalAlignment = Alignment.End, verticalArrangement = Arrangement.spacedBy(4.dp)) {
                                OutlinedButton(
                                    onClick = { showPatientBioSheet = true },
                                    shape = ShapePill,
                                    border = androidx.compose.foundation.BorderStroke(1.dp, Primary40),
                                    contentPadding = PaddingValues(horizontal = 10.dp),
                                    modifier = Modifier.height(20.dp),
                                ) {
                                    Icon(imageVector = Icons.Filled.Person, contentDescription = null, tint = Primary40, modifier = Modifier.size(11.dp))
                                    Spacer(Modifier.width(4.dp))
                                    Text("Bio", style = MaterialTheme.typography.labelSmall.copy(fontSize = 10.sp, letterSpacing = 0.2.sp), color = Primary40)
                                }
                                if (hasHistory) {
                                    Text(
                                        text = "Medical History",
                                        style = MaterialTheme.typography.labelSmall.copy(
                                            fontSize = 11.sp,
                                            letterSpacing = 0.2.sp,
                                            textDecoration = TextDecoration.Underline,
                                        ),
                                        color = Tertiary40,
                                        modifier = Modifier.clickable(
                                            indication = null,
                                            interactionSource = remember { MutableInteractionSource() },
                                            onClick = { showHistorySheet = true },
                                        ),
                                    )
                                }
                            }
                        }
                        if (appointment.referralId != null) {
                            HorizontalDivider(modifier = Modifier.padding(vertical = 12.dp), thickness = 0.5.dp, color = MaterialTheme.colorScheme.outlineVariant)
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Box(
                                    modifier = Modifier.size(28.dp).clip(CircleShape).background(Tertiary90),
                                    contentAlignment = Alignment.Center,
                                ) {
                                    if (appointment.referredByDoctorPicture != null) {
                                        AsyncImage(
                                            model = appointment.referredByDoctorPicture,
                                            contentDescription = appointment.referredByDoctorName,
                                            contentScale = ContentScale.Crop,
                                            modifier = Modifier.fillMaxSize().clip(CircleShape),
                                        )
                                    } else {
                                        Icon(imageVector = Icons.Filled.Person, contentDescription = null, tint = Tertiary40, modifier = Modifier.size(16.dp))
                                    }
                                }
                                Spacer(Modifier.width(10.dp))
                                Column {
                                    Text(text = "Referred by", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                    Text(
                                        text = "Dr. ${appointment.referredByDoctorName ?: "another doctor"}",
                                        style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.SemiBold),
                                    )
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
                        if (appointment.doctorSpecialities.isNotEmpty() || appointment.amount != null) {
                            HorizontalDivider(modifier = Modifier.padding(vertical = 12.dp), thickness = 0.5.dp, color = MaterialTheme.colorScheme.outlineVariant)
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                            ) {
                                if (appointment.doctorSpecialities.isNotEmpty()) {
                                    InfoRow(label = "Speciality", value = appointment.doctorSpecialities.joinToString(", "))
                                }
                                appointment.amount?.let { amount ->
                                    Column(horizontalAlignment = Alignment.End) {
                                        Text(
                                            text = "Amount",
                                            style = MaterialTheme.typography.bodyMedium,
                                            color = Neutral40,
                                        )
                                        Spacer(Modifier.height(2.dp))
                                        Text(
                                            text = "${appointment.currency} ${amount.toAmountString()}",
                                            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                                        )
                                    }
                                }
                            }
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
                            DetailFilledButton(
                                text = "Confirm Appointment",
                                onClick = onConfirm,
                                enabled = true,
                            )
                            DetailOutlinedButton(
                                text = "Cancel Appointment",
                                onClick = { showCancelDialog = true },
                                color = ActionDestructive,
                            )
                        }
                        AppointmentStatus.CONFIRMED -> {
                            DetailFilledButton(
                                text = "Mark As Complete",
                                onClick = { showCompleteConfirmSheet = true },
                                enabled = canComplete,
                            )
                            DetailOutlinedButton(
                                text = "Cancel Appointment",
                                onClick = { showCancelDialog = true },
                                color = ActionDestructive,
                            )
                            if (!canComplete) {
                                LockedActionNote(
                                    text = "The \"Mark as Complete\" button will be enabled once the appointment starts",
                                )
                            }
                        }
                        AppointmentStatus.COMPLETED -> {
                            // Referring only requires the appointment to be COMPLETED — see
                            // CR-SMRC-0001 §5.1.1 (the rating/prescription/summary prerequisites
                            // originally specified there were dropped per user direction).
                            ReferPatientCard(onRefer = onRefer)
                            RatingSection(
                                myRating = myRatingOfPatient,
                                isSubmitting = isSubmittingRating,
                                onSubmit = onSubmitRating,
                                onUpdate = onUpdateRating,
                                onDelete = onDeleteRating,
                                patientPicture = appointment.patientProfilePicture,
                                patientName = appointment.patientName,
                                onViewAllReviews = { showReviewsSheet = true },
                            )
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
        colors = CardDefaults.cardColors(containerColor = CardBackground),
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
                colors = CardDefaults.cardColors(containerColor = CardBackground),
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
                                if (!item.instructions.isNullOrBlank()) {
                                    Text(
                                        text = item.instructions,
                                        style = MaterialTheme.typography.labelSmall,
                                        color = Tertiary40,
                                        modifier = Modifier.padding(start = 10.dp),
                                    )
                                }
                            }
                        }
                    }
                    if (record.labRequests.isNotEmpty()) {
                        Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                            Text("Lab Requests", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                            record.labRequests.forEach { lab ->
                                Text("• $lab", style = MaterialTheme.typography.bodySmall)
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
                    if (!record.additionalNotes.isNullOrBlank()) {
                        Column {
                            Text("Additional Notes", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                            Text(record.additionalNotes, style = MaterialTheme.typography.bodySmall)
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

// ── Amended appointment-detail design system ─────────────────────────────────
// Cards carry a 7dp rounded accent bar on the leading edge and sit flat on the
// background; actions are 32dp tall with a 5dp radius.

private val DetailActionShape = RoundedCornerShape(5.dp)
private val DetailActionHeight = 32.dp

/** Flat card with the leading accent bar, sized to its content. */
@Composable
private fun AccentCard(
    modifier: Modifier = Modifier,
    accent: Color = Primary40,
    content: @Composable ColumnScope.() -> Unit,
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .height(IntrinsicSize.Min)
            .clip(ShapeCard)
            .background(CardBackground),
    ) {
        Box(Modifier.width(7.dp).fillMaxHeight().background(accent))
        Column(
            modifier = Modifier.weight(1f).padding(start = 21.dp, end = 22.dp, top = 10.dp, bottom = 16.dp),
            content = content,
        )
    }
}

@Composable
private fun DetailFilledButton(
    text: String,
    onClick: () -> Unit,
    enabled: Boolean,
    modifier: Modifier = Modifier,
) {
    Button(
        onClick = onClick,
        enabled = enabled,
        modifier = modifier.fillMaxWidth().height(DetailActionHeight),
        shape = DetailActionShape,
        colors = ButtonDefaults.buttonColors(
            containerColor = ActionEnabled,
            contentColor = Color.White,
            // The locked state is a flat grey fill rather than a faded green.
            disabledContainerColor = ActionDisabled,
            disabledContentColor = Color.White,
        ),
        contentPadding = PaddingValues(horizontal = 12.dp),
    ) {
        Text(text, style = detailActionTextStyle(), maxLines = 1, overflow = TextOverflow.Ellipsis)
    }
}

@Composable
private fun DetailOutlinedButton(
    text: String,
    onClick: () -> Unit,
    color: Color,
    modifier: Modifier = Modifier,
) {
    OutlinedButton(
        onClick = onClick,
        modifier = modifier.fillMaxWidth().height(DetailActionHeight),
        shape = DetailActionShape,
        border = androidx.compose.foundation.BorderStroke(1.dp, color),
        colors = ButtonDefaults.outlinedButtonColors(containerColor = Color.White, contentColor = color),
        contentPadding = PaddingValues(horizontal = 12.dp),
    ) {
        Text(text, style = detailActionTextStyle(), maxLines = 1, overflow = TextOverflow.Ellipsis)
    }
}

@Composable
private fun detailActionTextStyle() =
    MaterialTheme.typography.titleSmall.copy(fontSize = 13.sp, fontWeight = FontWeight.Bold)

/** Green ⓘ note explaining why the primary action is still greyed out. */
@Composable
private fun LockedActionNote(text: String) {
    Row(verticalAlignment = Alignment.Top) {
        Icon(
            imageVector = Icons.Filled.Info,
            contentDescription = null,
            tint = ActionEnabled,
            modifier = Modifier.size(18.dp),
        )
        Spacer(Modifier.width(10.dp))
        Text(
            text = text,
            style = MaterialTheme.typography.bodySmall,
            color = Neutral40,
        )
    }
}

/**
 * "Need To Refer Your Patient?" — unlike the summary and rating cards this one has **no**
 * accent bar, and splits into an illustration column and a text column.
 */
@Composable
private fun ReferPatientCard(onRefer: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(ShapeCard)
            .background(CardBackground)
            .padding(vertical = 18.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Box(modifier = Modifier.weight(149f), contentAlignment = Alignment.Center) {
            // Fills its column rather than a fixed size — the artwork carries its own internal
            // padding, so a fixed 92dp left the motif looking small against the text beside it.
            Image(
                painter = painterResource(Res.drawable.refer_icon),
                contentDescription = null,
                contentScale = ContentScale.Fit,
                modifier = Modifier.fillMaxWidth().heightIn(max = 150.dp),
            )
        }
        Column(modifier = Modifier.weight(200f).padding(end = 16.dp)) {
            Text(
                text = "Need To Refer Your Patient?",
                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
            )
            Spacer(Modifier.height(8.dp))
            Text(
                text = "Would you like another doctor to take over this case? Then click the " +
                    "button below to select the doctor and send a referral to the patient.",
                style = MaterialTheme.typography.bodySmall,
                color = Neutral40,
            )
            Spacer(Modifier.height(14.dp))
            OutlinedButton(
                onClick = onRefer,
                modifier = Modifier.fillMaxWidth().height(DetailActionHeight),
                shape = DetailActionShape,
                border = androidx.compose.foundation.BorderStroke(1.dp, Primary40),
                contentPadding = PaddingValues(horizontal = 12.dp),
            ) {
                Text("Refer Patient", style = detailActionTextStyle(), color = Primary40)
            }
        }
    }
}

private fun Double.toAmountString(): String {
    val whole = toLong()
    return if (this == whole.toDouble()) whole.toString() else toString()
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

/**
 * Also reused by [ke.co.smartroundclinic.doctor.presentation.main.chat.ui.PatientChatSheet] so
 * the patient-history view opened from chat can't drift out of sync with the one opened from an
 * appointment — one rendering of a [MedicalRecord], not two copies to keep aligned by hand.
 */
@Composable
internal fun HistoryRecordCard(
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
                    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                        Text(
                            text = record.createdAt.take(10),
                            style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.SemiBold),
                            color = Primary40,
                        )
                        if (!record.doctorName.isNullOrBlank()) {
                            Text(
                                text = "· Dr. ${record.doctorName}",
                                style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                            )
                        }
                    }
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
                                if (!item.instructions.isNullOrBlank()) {
                                    Text(
                                        text = item.instructions,
                                        style = MaterialTheme.typography.labelSmall,
                                        color = Tertiary40,
                                    )
                                }
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

                if (!record.additionalNotes.isNullOrBlank()) {
                    Spacer(Modifier.height(10.dp))
                    Text(
                        text = "Additional Notes",
                        style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.SemiBold),
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                    Spacer(Modifier.height(4.dp))
                    Text(text = record.additionalNotes, style = MaterialTheme.typography.bodySmall)
                }
            }
        }
    }
}

/**
 * "Rate and Review Patient" — the amended design folds the old rate card and
 * ratings-history card into one, with the edit/delete affordances and the
 * "See All Ratings" link stacked in the top-right corner.
 */
@Composable
private fun RatingSection(
    myRating: Rating?,
    isSubmitting: Boolean,
    onSubmit: (Int, String?) -> Unit,
    onUpdate: (Int, String?) -> Unit,
    onDelete: () -> Unit,
    patientPicture: String?,
    patientName: String,
    onViewAllReviews: () -> Unit,
) {
    var isEditing by remember(myRating) { mutableStateOf(myRating == null) }
    var showDeleteDialog by remember { mutableStateOf(false) }

    if (showDeleteDialog) {
        DeleteRatingDialog(
            onDismiss = { showDeleteDialog = false },
            onConfirm = { showDeleteDialog = false; onDelete() },
        )
    }

    AccentCard {
        Text(
            text = "Rate and Review Patient",
            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
        )
        Spacer(Modifier.height(12.dp))

        Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.Top) {
            Box(
                modifier = Modifier.size(47.dp).clip(CircleShape).background(Secondary90),
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
            Spacer(Modifier.weight(1f))
            Column(horizontalAlignment = Alignment.End) {
                if (myRating != null && !isEditing) {
                    Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                        IconButton(onClick = { isEditing = true }, modifier = Modifier.size(28.dp)) {
                            Icon(Icons.Filled.Edit, contentDescription = "Edit rating", tint = Tertiary40, modifier = Modifier.size(20.dp))
                        }
                        IconButton(onClick = { showDeleteDialog = true }, modifier = Modifier.size(28.dp)) {
                            Icon(Icons.Filled.Delete, contentDescription = "Delete rating", tint = ActionDestructive, modifier = Modifier.size(20.dp))
                        }
                    }
                    Spacer(Modifier.height(4.dp))
                }
                Text(
                    text = "See All Ratings",
                    style = MaterialTheme.typography.labelSmall.copy(
                        fontSize = 11.sp,
                        letterSpacing = 0.2.sp,
                        textDecoration = TextDecoration.Underline,
                    ),
                    color = Primary40,
                    modifier = Modifier.clickable(
                        indication = null,
                        interactionSource = remember { MutableInteractionSource() },
                        onClick = onViewAllReviews,
                    ),
                )
            }
        }

        Spacer(Modifier.height(12.dp))

        if (isEditing) {
            RatingForm(
                initialRating = myRating?.rating ?: 0,
                initialComment = myRating?.comment.orEmpty(),
                isSubmitting = isSubmitting,
                submitLabel = if (myRating != null) "Save Changes" else "Submit Rating",
                onCancel = if (myRating != null) ({ isEditing = false }) else null,
                onSubmit = { rating, comment ->
                    if (myRating != null) onUpdate(rating, comment) else onSubmit(rating, comment)
                },
            )
        } else if (myRating != null) {
            StarRatingDisplay(rating = myRating.rating.toDouble(), starSize = 22.dp)
            if (!myRating.comment.isNullOrBlank()) {
                Spacer(Modifier.height(14.dp))
                Text(
                    text = "Review",
                    style = MaterialTheme.typography.bodyMedium,
                    color = Neutral40,
                )
                Spacer(Modifier.height(6.dp))
                Text(
                    text = myRating.comment,
                    style = MaterialTheme.typography.bodySmall,
                    color = Neutral40,
                    textAlign = TextAlign.Justify,
                )
            }
        }
    }
}

@Composable
private fun RatingForm(
    initialRating: Int,
    initialComment: String,
    isSubmitting: Boolean,
    submitLabel: String,
    onCancel: (() -> Unit)?,
    onSubmit: (Int, String?) -> Unit,
) {
    var rating by remember { mutableStateOf(initialRating) }
    var comment by remember { mutableStateOf(initialComment) }

    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
        StarRatingInput(rating = rating, onRatingChange = { rating = it })
        OutlinedTextField(
            value = comment,
            onValueChange = { comment = it },
            placeholder = { Text("Add a comment (optional)", style = MaterialTheme.typography.bodySmall) },
            shape = ShapeInput,
            minLines = 2,
            modifier = Modifier.fillMaxWidth(),
        )
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            if (onCancel != null) {
                OutlinedButton(
                    onClick = onCancel,
                    modifier = Modifier.weight(1f),
                    shape = ShapePill,
                ) {
                    Text("Cancel", style = MaterialTheme.typography.labelLarge, modifier = Modifier.padding(vertical = 4.dp))
                }
            }
            PrimaryButton(
                onClick = { onSubmit(rating, comment.ifBlank { null }) },
                enabled = rating > 0 && !isSubmitting,
                modifier = Modifier.weight(1f),
            ) {
                if (isSubmitting) {
                    CircularProgressIndicator(modifier = Modifier.size(18.dp), strokeWidth = 2.dp, color = Color.White)
                } else {
                    Text(
                        text = submitLabel,
                        style = MaterialTheme.typography.labelLarge,
                        color = Color.White,
                        modifier = Modifier.padding(vertical = 4.dp),
                    )
                }
            }
        }
    }
}

@Composable
private fun DeleteRatingDialog(
    onDismiss: () -> Unit,
    onConfirm: () -> Unit,
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Delete Rating") },
        text = { Text("Are you sure you want to delete this rating?", style = MaterialTheme.typography.bodyMedium) },
        confirmButton = {
            TextButton(onClick = onConfirm) {
                Text("Delete", color = MaterialTheme.colorScheme.error)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Cancel") }
        },
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun PatientRatingsSheet(
    averageRating: Double,
    totalReviews: Int,
    reviews: List<Rating>,
    isLoading: Boolean,
    isLoadingMore: Boolean,
    hasMore: Boolean,
    onLoadMore: () -> Unit,
    onDismiss: () -> Unit,
) {
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    val listState = rememberLazyListState()

    val nearBottom by remember {
        derivedStateOf {
            val info = listState.layoutInfo
            val lastVisible = info.visibleItemsInfo.lastOrNull()?.index ?: return@derivedStateOf false
            lastVisible >= info.totalItemsCount - 3
        }
    }
    LaunchedEffect(nearBottom) {
        if (nearBottom && hasMore && !isLoading && !isLoadingMore) onLoadMore()
    }

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        containerColor = MaterialTheme.colorScheme.background,
    ) {
        Column(modifier = Modifier.fillMaxWidth().fillMaxHeight(0.85f)) {
            Row(
                modifier = Modifier.fillMaxWidth().padding(horizontal = 20.dp, vertical = 12.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = "Patient Ratings",
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                    )
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(imageVector = Icons.Filled.Star, contentDescription = null, tint = Primary40, modifier = Modifier.size(14.dp))
                        Spacer(Modifier.width(4.dp))
                        Text(
                            text = "${averageRating.formatOneDecimal()} · $totalReviews rating${if (totalReviews == 1) "" else "s"} from doctors",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                        )
                    }
                }
                IconButton(onClick = onDismiss) {
                    Icon(imageVector = Icons.Filled.Close, contentDescription = "Close")
                }
            }
            HorizontalDivider(thickness = 0.5.dp, color = MaterialTheme.colorScheme.outlineVariant)

            when {
                isLoading && reviews.isEmpty() -> {
                    Box(modifier = Modifier.fillMaxWidth().padding(vertical = 48.dp), contentAlignment = Alignment.Center) {
                        CircularProgressIndicator(color = Primary40)
                    }
                }
                reviews.isEmpty() -> {
                    Box(modifier = Modifier.fillMaxWidth().padding(32.dp), contentAlignment = Alignment.Center) {
                        Text(
                            text = "No previous doctors have rated this patient yet.",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                        )
                    }
                }
                else -> {
                    LazyColumn(
                        state = listState,
                        modifier = Modifier.fillMaxWidth().navigationBarsPadding(),
                        contentPadding = PaddingValues(horizontal = 16.dp, vertical = 12.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp),
                    ) {
                        items(reviews, key = { it.id }) { review -> ReviewItem(review) }
                        if (isLoadingMore) {
                            item(key = "loading_more") {
                                Box(modifier = Modifier.fillMaxWidth().padding(vertical = 16.dp), contentAlignment = Alignment.Center) {
                                    CircularProgressIndicator(modifier = Modifier.size(20.dp), strokeWidth = 2.dp, color = Primary40)
                                }
                            }
                        } else if (!hasMore) {
                            item(key = "end_of_list") {
                                Text(
                                    text = "· End of reviews ·",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                                    textAlign = TextAlign.Center,
                                    modifier = Modifier.fillMaxWidth().padding(vertical = 12.dp),
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun ReviewItem(review: Rating) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = ShapeCard,
        colors = CardDefaults.cardColors(containerColor = CardBackground),
        elevation = CardDefaults.cardElevation(0.dp),
    ) {
        Column(modifier = Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
            Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier.size(28.dp).clip(CircleShape).background(Secondary90),
                    contentAlignment = Alignment.Center,
                ) {
                    if (review.raterProfilePicture != null) {
                        AsyncImage(
                            model = review.raterProfilePicture,
                            contentDescription = review.raterName,
                            contentScale = ContentScale.Crop,
                            modifier = Modifier.fillMaxSize().clip(CircleShape),
                        )
                    } else {
                        Icon(imageVector = Icons.Filled.Person, contentDescription = null, tint = Secondary40, modifier = Modifier.size(16.dp))
                    }
                }
                Spacer(Modifier.width(8.dp))
                Text(
                    text = review.raterName ?: "Doctor",
                    style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.SemiBold),
                    modifier = Modifier.weight(1f),
                )
                Text(
                    text = review.createdAt.take(10),
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }
            StarRatingDisplay(rating = review.rating.toDouble(), starSize = 14.dp)
            if (!review.comment.isNullOrBlank()) {
                Text(
                    text = review.comment,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurface,
                )
            }
        }
    }
}

private fun Double.formatOneDecimal(): String {
    val scaled = (this * 10).toInt()
    return "${scaled / 10}.${scaled % 10}"
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun CompleteConfirmationBottomSheet(
    onDismiss: () -> Unit,
    onConfirm: () -> Unit,
) {
    var acknowledged by remember { mutableStateOf(false) }
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        containerColor = MaterialTheme.colorScheme.surface,
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp)
                .padding(bottom = 8.dp)
                .navigationBarsPadding(),
            verticalArrangement = Arrangement.spacedBy(16.dp),
        ) {
            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                Icon(imageVector = Icons.Filled.WarningAmber, contentDescription = null, tint = Error40)
                Text(
                    text = "Mark Appointment as Complete?",
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                )
            }
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(ShapeInput)
                    .background(Error90)
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(6.dp),
            ) {
                Text(
                    text = "You're about to mark this appointment as complete.",
                    style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.SemiBold),
                    color = Error40,
                )
                Text(
                    text = "Doing so without having actually conducted the consultation with the patient is a violation of our Terms of Service and could lead to licence termination and blacklisting from the platform.",
                    style = MaterialTheme.typography.bodySmall,
                    color = Error40,
                )
            }
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable(
                        indication = null,
                        interactionSource = remember { MutableInteractionSource() },
                    ) { acknowledged = !acknowledged },
            ) {
                Checkbox(
                    checked = acknowledged,
                    onCheckedChange = { acknowledged = it },
                    colors = CheckboxDefaults.colors(checkedColor = MaterialTheme.colorScheme.primary),
                )
                Text(
                    text = "I confirm I conducted this appointment with the patient and can now mark it as completed.",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurface,
                )
            }
            PrimaryButton(onClick = onConfirm, enabled = acknowledged) {
                Text(
                    text = "Mark as Complete",
                    style = MaterialTheme.typography.labelLarge,
                    color = Color.White,
                    modifier = Modifier.padding(vertical = 14.dp),
                )
            }
            TextButton(onClick = onDismiss, modifier = Modifier.fillMaxWidth()) {
                Text(text = "Cancel", style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.SemiBold))
            }
            Spacer(Modifier.height(8.dp))
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun CancelBottomSheet(
    onDismiss: () -> Unit,
    onConfirm: (String) -> Unit,
) {
    var reason by remember { mutableStateOf("") }
    var showError by remember { mutableStateOf(false) }
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        containerColor = MaterialTheme.colorScheme.surface,
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp)
                .padding(bottom = 8.dp)
                .navigationBarsPadding(),
            verticalArrangement = Arrangement.spacedBy(16.dp),
        ) {
            Text(
                text = "Cancel Appointment",
                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
            )
            Text(
                text = "Are you sure you want to cancel this appointment? This action cannot be undone.",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
            OutlinedTextField(
                value = reason,
                onValueChange = {
                    reason = it
                    if (showError && it.isNotBlank()) showError = false
                },
                label = { Text("Reason") },
                placeholder = { Text("Enter a reason…") },
                isError = showError,
                supportingText = {
                    if (showError) Text("A reason is required to cancel this appointment")
                },
                minLines = 3,
                maxLines = 5,
                shape = ShapeInput,
                modifier = Modifier.fillMaxWidth(),
            )
            Button(
                onClick = {
                    if (reason.isBlank()) showError = true
                    else onConfirm(reason.trim())
                },
                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error),
                shape = ShapeInput,
                modifier = Modifier.fillMaxWidth().height(52.dp),
            ) {
                Text(
                    text = "Yes, Cancel",
                    style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.SemiBold),
                )
            }
            TextButton(onClick = onDismiss, modifier = Modifier.fillMaxWidth()) {
                Text(
                    text = "Keep Appointment",
                    style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.SemiBold),
                )
            }
            Spacer(Modifier.height(8.dp))
        }
    }
}

