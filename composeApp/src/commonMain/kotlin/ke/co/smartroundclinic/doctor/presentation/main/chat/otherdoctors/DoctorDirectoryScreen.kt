package ke.co.smartroundclinic.doctor.presentation.main.chat.otherdoctors

import androidx.compose.animation.expandHorizontally
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkHorizontally
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
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
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage
import ke.co.smartroundclinic.doctor.domain.model.Doctor
import ke.co.smartroundclinic.doctor.presentation.theme.Primary40
import ke.co.smartroundclinic.doctor.presentation.theme.Primary90
import ke.co.smartroundclinic.doctor.presentation.theme.ShapePill

/**
 * Standalone "All Doctors" browse screen, reached via the FAB on [DoctorChatsListScreen] — a
 * paginated, always-populated directory of every verified doctor on the platform, fetched
 * immediately and infinite-scrolled as the user nears the bottom. The search box only filters
 * what's already loaded, it never gates the list behind an empty state.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun DoctorDirectoryScreen(
    doctors: List<Doctor>,
    isLoadingDoctors: Boolean,
    isLoadingMoreDoctors: Boolean,
    hasMoreDoctors: Boolean,
    onLoadMoreDoctors: () -> Unit,
    onBack: () -> Unit,
    onDoctorClick: (Doctor) -> Unit,
    modifier: Modifier = Modifier,
) {
    var isSearching by remember { mutableStateOf(false) }
    var searchQuery by remember { mutableStateOf("") }
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
                title = { Text("All Doctors") },
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
                hintText = "Chat with any verified doctor on the platform",
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
                        DoctorResultRow(doctor = doctor, onClick = { onDoctorClick(doctor) })
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
}

/** Shared by [DoctorDirectoryScreen] and [DoctorChatsListScreen] so both search boxes look/animate identically. */
@Composable
internal fun SearchHeaderRow(
    isSearching: Boolean,
    onToggleSearch: () -> Unit,
    searchQuery: String,
    onSearchQueryChange: (String) -> Unit,
    hintText: String,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Box(modifier = Modifier.weight(1f)) {
            androidx.compose.animation.AnimatedVisibility(visible = !isSearching, enter = fadeIn(), exit = fadeOut()) {
                Text(
                    text = hintText,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }
            // Grows from the search icon's position (anchored to the end) rather than springing
            // in at full width all at once.
            androidx.compose.animation.AnimatedVisibility(
                visible = isSearching,
                enter = expandHorizontally(expandFrom = Alignment.End) + fadeIn(),
                exit = shrinkHorizontally(shrinkTowards = Alignment.End) + fadeOut(),
            ) {
                CompactSearchField(
                    value = searchQuery,
                    onValueChange = onSearchQueryChange,
                    modifier = Modifier.fillMaxWidth(),
                )
            }
        }
        Spacer(Modifier.size(4.dp))
        IconButton(onClick = onToggleSearch) {
            Icon(
                imageVector = if (isSearching) Icons.Filled.Close else Icons.Filled.Search,
                contentDescription = if (isSearching) "Close search" else "Search doctors",
                tint = Primary40,
            )
        }
    }
}

@Composable
internal fun DoctorResultRow(doctor: Doctor, onClick: () -> Unit) {
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
internal fun Avatar(pictureUrl: String?, size: Int = 48) {
    Box(
        modifier = Modifier.size(size.dp).clip(CircleShape).background(Primary90),
        contentAlignment = Alignment.Center,
    ) {
        if (pictureUrl != null) {
            AsyncImage(model = pictureUrl, contentDescription = null, contentScale = ContentScale.Crop, modifier = Modifier.fillMaxSize().clip(CircleShape))
        } else {
            Icon(imageVector = Icons.Filled.Person, contentDescription = null, tint = Primary40, modifier = Modifier.size((size * 22 / 48).dp))
        }
    }
}

@Composable
internal fun CompactSearchField(
    value: String,
    onValueChange: (String) -> Unit,
    modifier: Modifier = Modifier,
) {
    val focusRequester = remember { FocusRequester() }
    LaunchedEffect(Unit) { focusRequester.requestFocus() }

    Box(
        modifier = modifier
            .height(36.dp)
            .clip(ShapePill)
            .background(MaterialTheme.colorScheme.surfaceVariant)
            .padding(horizontal = 14.dp),
        contentAlignment = Alignment.CenterStart,
    ) {
        if (value.isEmpty()) {
            Text(
                text = "Search doctors...",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
        }
        BasicTextField(
            value = value,
            onValueChange = onValueChange,
            singleLine = true,
            textStyle = MaterialTheme.typography.bodySmall.copy(color = MaterialTheme.colorScheme.onSurface),
            cursorBrush = SolidColor(Primary40),
            modifier = Modifier.fillMaxWidth().focusRequester(focusRequester),
        )
    }
}
