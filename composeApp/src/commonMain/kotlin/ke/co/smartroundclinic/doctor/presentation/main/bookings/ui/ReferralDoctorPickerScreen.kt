package ke.co.smartroundclinic.doctor.presentation.main.bookings.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import ke.co.smartroundclinic.doctor.domain.model.Doctor
import ke.co.smartroundclinic.doctor.presentation.main.chat.otherdoctors.DoctorResultRow
import ke.co.smartroundclinic.doctor.presentation.main.chat.otherdoctors.SearchHeaderRow
import ke.co.smartroundclinic.doctor.presentation.theme.Primary40
import ke.co.smartroundclinic.doctor.presentation.theme.Primary90

/**
 * Doctor picker for the "refer this patient out" flow — same searchable, paginated flat-list
 * design as the Chat tab's "All Doctors" screen ([ke.co.smartroundclinic.doctor.presentation.main.chat.otherdoctors.DoctorDirectoryScreen]),
 * reusing its shared search/row composables, rather than the specialty-category browsing UI.
 * Selecting a doctor asks for confirmation (this sends a real referral) before submitting.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun ReferralDoctorPickerScreen(
    doctors: List<Doctor>,
    isLoadingDoctors: Boolean,
    isLoadingMoreDoctors: Boolean,
    hasMoreDoctors: Boolean,
    onLoadMoreDoctors: () -> Unit,
    isSubmitting: Boolean,
    onBack: () -> Unit,
    onDoctorSelected: (Doctor) -> Unit,
    modifier: Modifier = Modifier,
) {
    var isSearching by remember { mutableStateOf(false) }
    var searchQuery by remember { mutableStateOf("") }
    var doctorPendingConfirm by remember { mutableStateOf<Doctor?>(null) }
    val listState = rememberLazyListState()

    val nearBottom by remember {
        derivedStateOf {
            val layoutInfo = listState.layoutInfo
            val lastVisible = layoutInfo.visibleItemsInfo.lastOrNull()?.index ?: 0
            layoutInfo.totalItemsCount > 0 && lastVisible >= layoutInfo.totalItemsCount - 4
        }
    }
    LaunchedEffect(nearBottom, hasMoreDoctors, isLoadingMoreDoctors, isLoadingDoctors) {
        if (nearBottom && hasMoreDoctors && !isLoadingMoreDoctors && !isLoadingDoctors) onLoadMoreDoctors()
    }

    val visibleDoctors = if (searchQuery.isNotBlank()) {
        doctors.filter { it.name.contains(searchQuery, ignoreCase = true) }
    } else {
        doctors
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
                title = { Text("Refer to a Doctor") },
            )
        },
        contentWindowInsets = WindowInsets(0),
    ) { paddingValues ->
        Column(modifier = Modifier.fillMaxSize().padding(paddingValues)) {
            SearchHeaderRow(
                isSearching = isSearching,
                onToggleSearch = { isSearching = !isSearching; if (!isSearching) searchQuery = "" },
                searchQuery = searchQuery,
                onSearchQueryChange = { searchQuery = it },
                hintText = "Search doctors to refer this patient to",
            )
            HorizontalDivider(thickness = 0.5.dp, color = MaterialTheme.colorScheme.outlineVariant)

            if (isLoadingDoctors && doctors.isEmpty()) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) { CircularProgressIndicator() }
            } else if (visibleDoctors.isEmpty()) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Box(modifier = Modifier.size(72.dp).clip(CircleShape).background(Primary90), contentAlignment = Alignment.Center) {
                            Icon(imageVector = Icons.Filled.Person, contentDescription = null, tint = Primary40, modifier = Modifier.size(32.dp))
                        }
                        Spacer(Modifier.size(12.dp))
                        Text(
                            text = if (searchQuery.isNotBlank()) "No doctors found" else "No doctors available right now",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                        )
                    }
                }
            } else {
                LazyColumn(state = listState, modifier = Modifier.fillMaxSize()) {
                    items(visibleDoctors, key = { it.id }) { doctor ->
                        DoctorResultRow(doctor = doctor, onClick = { doctorPendingConfirm = doctor })
                        HorizontalDivider(thickness = 0.5.dp, color = MaterialTheme.colorScheme.outlineVariant, modifier = Modifier.padding(start = 76.dp))
                    }
                    if (isLoadingMoreDoctors) {
                        item(key = "loading_more") {
                            Box(modifier = Modifier.fillMaxWidth().padding(vertical = 16.dp), contentAlignment = Alignment.Center) {
                                CircularProgressIndicator(modifier = Modifier.size(20.dp), strokeWidth = 2.dp)
                            }
                        }
                    }
                }
            }
        }
    }

    doctorPendingConfirm?.let { doctor ->
        AlertDialog(
            onDismissRequest = { if (!isSubmitting) doctorPendingConfirm = null },
            title = { Text("Refer this patient?") },
            text = { Text("This patient's medical summary will be shared with Dr. ${doctor.name}.") },
            confirmButton = {
                TextButton(
                    enabled = !isSubmitting,
                    onClick = { onDoctorSelected(doctor); doctorPendingConfirm = null },
                ) { Text("Refer") }
            },
            dismissButton = {
                TextButton(enabled = !isSubmitting, onClick = { doctorPendingConfirm = null }) { Text("Cancel") }
            },
        )
    }
}
