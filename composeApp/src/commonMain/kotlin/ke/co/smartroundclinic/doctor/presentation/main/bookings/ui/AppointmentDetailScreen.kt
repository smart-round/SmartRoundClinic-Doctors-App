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
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.Videocam
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import ke.co.smartroundclinic.doctor.presentation.main.bookings.BookingStatus
import ke.co.smartroundclinic.doctor.presentation.main.bookings.BookingUi
import ke.co.smartroundclinic.doctor.presentation.common.composables.PrimaryButton
import ke.co.smartroundclinic.doctor.presentation.theme.CardBackground
import ke.co.smartroundclinic.doctor.presentation.theme.Neutral80
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
    booking: BookingUi,
    onBack: () -> Unit,
    modifier: Modifier = Modifier,
) {
    var selectedRating by remember { mutableIntStateOf(booking.existingRating) }
    var reviewText by remember { mutableStateOf(booking.existingReview) }
    var reviewSubmitted by remember { mutableStateOf(booking.existingRating > 0) }

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
                    BookingStatusBadge(status = booking.status)
                    Spacer(Modifier.width(4.dp))
                    if (booking.status == BookingStatus.UPCOMING) {
                        IconButton(onClick = {}) {
                            Icon(imageVector = Icons.Filled.Phone, contentDescription = "Call patient", tint = Primary40)
                        }
                        IconButton(onClick = {}) {
                            Icon(imageVector = Icons.Filled.Videocam, contentDescription = "Video call patient", tint = Primary40)
                        }
                    }
                    Spacer(Modifier.width(4.dp))
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
                Card(shape = ShapeCard, colors = CardDefaults.cardColors(containerColor = CardBackground), elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        InfoRow(label = "Date/Time", value = booking.dateTime)
                        HorizontalDivider(modifier = Modifier.padding(vertical = 12.dp), thickness = 0.5.dp, color = MaterialTheme.colorScheme.outlineVariant)
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(modifier = Modifier.size(40.dp).clip(CircleShape).background(Secondary90), contentAlignment = Alignment.Center) {
                                Icon(imageVector = Icons.Filled.Person, contentDescription = null, tint = Secondary40, modifier = Modifier.size(24.dp))
                            }
                            Spacer(Modifier.width(12.dp))
                            Column {
                                Text(text = "Patient", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                Text(text = booking.patientName, style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.SemiBold))
                            }
                        }
                        HorizontalDivider(modifier = Modifier.padding(vertical = 12.dp), thickness = 0.5.dp, color = MaterialTheme.colorScheme.outlineVariant)
                        InfoRow(label = "Consultation Fee", value = booking.fee)
                    }
                }

                when (booking.status) {
                    BookingStatus.UPCOMING -> UpcomingActions()
                    BookingStatus.COMPLETED -> {
                        if (reviewSubmitted) {
                            SubmittedReviewCard(patientName = booking.patientName, rating = selectedRating, review = reviewText)
                        } else {
                            RateReviewForm(
                                selectedRating = selectedRating,
                                onRatingSelected = { selectedRating = it },
                                reviewText = reviewText,
                                onReviewTextChange = { reviewText = it },
                                onSubmit = { reviewSubmitted = true },
                            )
                        }
                    }
                    BookingStatus.CANCELLED -> CancelledNotice()
                }

                Spacer(Modifier.height(8.dp))
            }
        }
    }
}

@Composable
internal fun BookingStatusBadge(status: BookingStatus, modifier: Modifier = Modifier) {
    val (label, bgColor, textColor) = when (status) {
        BookingStatus.UPCOMING -> Triple("Upcoming", Tertiary90, Tertiary40)
        BookingStatus.COMPLETED -> Triple("Completed", Tertiary90, Tertiary40)
        BookingStatus.CANCELLED -> Triple("Cancelled", MaterialTheme.colorScheme.errorContainer, MaterialTheme.colorScheme.error)
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
private fun UpcomingActions(modifier: Modifier = Modifier) {
    Column(modifier = modifier.padding(bottom = 8.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
        PrimaryButton(onClick = {}) {
            Text(text = "Add Prescription", style = MaterialTheme.typography.labelLarge, color = Color.White, modifier = Modifier.padding(vertical = 14.dp))
        }
        Button(onClick = {}, modifier = Modifier.fillMaxWidth(), shape = ShapePill, colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.errorContainer, contentColor = MaterialTheme.colorScheme.error)) {
            Text(text = "Cancel Appointment", style = MaterialTheme.typography.labelLarge, modifier = Modifier.padding(vertical = 10.dp))
        }
    }
}

@Composable
private fun RateReviewForm(
    selectedRating: Int,
    onRatingSelected: (Int) -> Unit,
    reviewText: String,
    onReviewTextChange: (String) -> Unit,
    onSubmit: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Card(modifier = modifier.fillMaxWidth(), shape = ShapeCard, colors = CardDefaults.cardColors(containerColor = CardBackground), elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)) {
        Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
            Text(text = "Rate & Review Patient", style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold))
            StarRatingRow(selectedRating = selectedRating, onRatingSelected = onRatingSelected)
            OutlinedTextField(value = reviewText, onValueChange = onReviewTextChange, placeholder = { Text("Write your review here...") }, shape = ShapeInput, modifier = Modifier.fillMaxWidth().height(120.dp), maxLines = Int.MAX_VALUE)
            PrimaryButton(onClick = onSubmit, enabled = selectedRating > 0) {
                Text(text = "Submit Review", style = MaterialTheme.typography.labelLarge, color = Color.White, modifier = Modifier.padding(vertical = 12.dp))
            }
        }
    }
}

@Composable
private fun StarRatingRow(selectedRating: Int, onRatingSelected: (Int) -> Unit, modifier: Modifier = Modifier) {
    Row(modifier = modifier, horizontalArrangement = Arrangement.spacedBy(4.dp)) {
        repeat(5) { index ->
            val starIndex = index + 1
            Icon(
                imageVector = Icons.Filled.Star,
                contentDescription = "Rate $starIndex",
                tint = if (starIndex <= selectedRating) Primary40 else Neutral80,
                modifier = Modifier.size(32.dp).clickable(indication = null, interactionSource = remember { MutableInteractionSource() }) { onRatingSelected(starIndex) },
            )
        }
    }
}

@Composable
private fun SubmittedReviewCard(patientName: String, rating: Int, review: String, modifier: Modifier = Modifier) {
    Card(modifier = modifier.fillMaxWidth(), shape = ShapeCard, colors = CardDefaults.cardColors(containerColor = CardBackground), elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)) {
        Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
            Text(text = "Patient Rating & Review", style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold))
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(modifier = Modifier.size(36.dp).clip(CircleShape).background(Secondary90), contentAlignment = Alignment.Center) {
                    Icon(imageVector = Icons.Filled.Person, contentDescription = null, tint = Secondary40, modifier = Modifier.size(22.dp))
                }
                Spacer(Modifier.width(10.dp))
                Column {
                    Text(text = "Dr. Abel", style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.SemiBold))
                    Text(text = "Review for $patientName", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
            }
            Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                repeat(5) { index -> Icon(imageVector = Icons.Filled.Star, contentDescription = null, tint = if (index + 1 <= rating) Primary40 else Neutral80, modifier = Modifier.size(20.dp)) }
            }
            if (review.isNotBlank()) {
                Text(text = review, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
        }
    }
}

@Composable
private fun CancelledNotice(modifier: Modifier = Modifier) {
    Card(modifier = modifier.fillMaxWidth(), shape = ShapeCard, colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.errorContainer), elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)) {
        Text(text = "This appointment was cancelled.", style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.error, modifier = Modifier.padding(16.dp))
    }
}
