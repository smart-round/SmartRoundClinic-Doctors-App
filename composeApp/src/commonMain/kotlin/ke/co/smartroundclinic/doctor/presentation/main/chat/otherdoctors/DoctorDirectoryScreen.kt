package ke.co.smartroundclinic.doctor.presentation.main.chat.otherdoctors

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage
import ke.co.smartroundclinic.doctor.domain.model.Doctor
import ke.co.smartroundclinic.doctor.domain.model.DoctorChatThread
import ke.co.smartroundclinic.doctor.presentation.theme.Primary40
import ke.co.smartroundclinic.doctor.presentation.theme.Primary90
import ke.co.smartroundclinic.doctor.presentation.theme.Tertiary40
import kotlinx.datetime.Clock
import kotlinx.datetime.Instant
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime

/**
 * Existing conversations (newest-message-first, server-sorted) always render first, each with
 * online status + last message + timestamp — mirrors the Consultations tab's thread list. Below
 * that sits the always-populated, paginated doctor directory (fetched immediately, infinite-scrolled
 * as the user nears the bottom, and excludes doctors already shown above). The search box only
 * filters what's already loaded, it never gates either section behind an empty state.
 */
@Composable
internal fun DoctorDirectoryScreen(
    threads: List<DoctorChatThread>,
    doctors: List<Doctor>,
    isLoadingDoctors: Boolean,
    isLoadingMoreDoctors: Boolean,
    hasMoreDoctors: Boolean,
    onLoadMoreDoctors: () -> Unit,
    isSearching: Boolean,
    onToggleSearch: () -> Unit,
    searchQuery: String,
    onSearchQueryChange: (String) -> Unit,
    onThreadClick: (DoctorChatThread) -> Unit,
    onDoctorClick: (Doctor) -> Unit,
    modifier: Modifier = Modifier,
) {
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

    val threadCounterpartIds = threads.map { it.counterpartId }.toSet()
    val directoryDoctors = doctors.filter { it.id !in threadCounterpartIds }

    val visibleThreads = if (searchQuery.isNotBlank()) {
        threads.filter { it.counterpartName.contains(searchQuery, ignoreCase = true) }
    } else {
        threads
    }
    val visibleDoctors = if (searchQuery.isNotBlank()) {
        directoryDoctors.filter { it.name.contains(searchQuery, ignoreCase = true) }
    } else {
        directoryDoctors
    }

    Column(modifier = modifier.fillMaxSize()) {
        Row(
            modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            if (isSearching) {
                OutlinedTextField(
                    value = searchQuery,
                    onValueChange = onSearchQueryChange,
                    placeholder = { Text("Search doctors by name...") },
                    singleLine = true,
                    modifier = Modifier.weight(1f),
                )
            } else {
                Text(
                    text = "Chat with any verified doctor on the platform",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.weight(1f),
                )
            }
            IconButton(onClick = onToggleSearch) {
                Icon(imageVector = Icons.Filled.Search, contentDescription = if (isSearching) "Close search" else "Search doctors", tint = Primary40)
            }
        }
        HorizontalDivider(thickness = 0.5.dp, color = MaterialTheme.colorScheme.outlineVariant)

        if (isLoadingDoctors && doctors.isEmpty() && threads.isEmpty()) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) { CircularProgressIndicator() }
        } else if (visibleThreads.isEmpty() && visibleDoctors.isEmpty()) {
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
                if (visibleThreads.isNotEmpty()) {
                    item(key = "recent_header") { SectionHeader("Recent") }
                    items(visibleThreads, key = { "thread_${it.threadId}" }) { thread ->
                        ThreadRow(thread = thread, onClick = { onThreadClick(thread) })
                        HorizontalDivider(thickness = 0.5.dp, color = MaterialTheme.colorScheme.outlineVariant, modifier = Modifier.padding(start = 76.dp))
                    }
                }
                if (visibleDoctors.isNotEmpty()) {
                    if (visibleThreads.isNotEmpty()) item(key = "all_doctors_header") { SectionHeader("All Doctors") }
                    items(visibleDoctors, key = { "doctor_${it.id}" }) { doctor ->
                        DoctorResultRow(doctor = doctor, onClick = { onDoctorClick(doctor) })
                        HorizontalDivider(thickness = 0.5.dp, color = MaterialTheme.colorScheme.outlineVariant, modifier = Modifier.padding(start = 76.dp))
                    }
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

@Composable
private fun SectionHeader(label: String) {
    Text(
        text = label,
        style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.SemiBold),
        color = MaterialTheme.colorScheme.onSurfaceVariant,
        modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 8.dp),
    )
}

@Composable
private fun ThreadRow(thread: DoctorChatThread, onClick: () -> Unit) {
    Row(
        modifier = Modifier.fillMaxWidth().clickable(onClick = onClick).padding(horizontal = 16.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Box {
            Avatar(pictureUrl = thread.counterpartPicture)
            if (thread.isOnline) {
                Box(
                    modifier = Modifier
                        .align(Alignment.BottomEnd)
                        .size(13.dp)
                        .clip(CircleShape)
                        .background(MaterialTheme.colorScheme.surface)
                        .padding(2.dp)
                        .clip(CircleShape)
                        .background(Tertiary40),
                )
            }
        }
        Spacer(Modifier.size(12.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(text = "Dr. ${thread.counterpartName}", style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.SemiBold), maxLines = 1, overflow = TextOverflow.Ellipsis)
            Text(
                text = thread.lastMessagePreview ?: "No messages yet",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
            )
        }
        if (thread.lastMessageAt != null) {
            Text(
                text = formatThreadTimestamp(thread.lastMessageAt),
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
        }
    }
}

@Composable
private fun DoctorResultRow(doctor: Doctor, onClick: () -> Unit) {
    Row(
        modifier = Modifier.fillMaxWidth().clickable(onClick = onClick).padding(horizontal = 16.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Avatar(pictureUrl = doctor.profilePicture)
        Spacer(Modifier.size(12.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(text = doctor.name, style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.SemiBold), maxLines = 1, overflow = TextOverflow.Ellipsis)
            if (doctor.specialization != null) {
                Text(text = doctor.specialization, style = MaterialTheme.typography.bodySmall, color = Primary40, maxLines = 1, overflow = TextOverflow.Ellipsis)
            }
        }
    }
}

@Composable
private fun Avatar(pictureUrl: String?) {
    Box(
        modifier = Modifier.size(48.dp).clip(RoundedCornerShape(12.dp)).background(Primary90),
        contentAlignment = Alignment.Center,
    ) {
        if (pictureUrl != null) {
            AsyncImage(model = pictureUrl, contentDescription = null, contentScale = ContentScale.Crop, modifier = Modifier.fillMaxSize().clip(RoundedCornerShape(12.dp)))
        } else {
            Icon(imageVector = Icons.Filled.Person, contentDescription = null, tint = Primary40, modifier = Modifier.size(22.dp))
        }
    }
}

private fun formatThreadTimestamp(iso: String): String = try {
    val instant = Instant.parse(iso)
    val zone = TimeZone.currentSystemDefault()
    val dateTime = instant.toLocalDateTime(zone)
    val today = Clock.System.now().toLocalDateTime(zone).date
    if (dateTime.date == today) {
        val hour = dateTime.hour
        val ampm = if (hour < 12) "AM" else "PM"
        val h = if (hour % 12 == 0) 12 else hour % 12
        "$h:${dateTime.minute.toString().padStart(2, '0')} $ampm"
    } else {
        "${dateTime.date.month.name.take(3).lowercase().replaceFirstChar { it.uppercase() }} ${dateTime.date.dayOfMonth}"
    }
} catch (_: Exception) { "" }
