package ke.co.smartroundclinic.doctor.presentation.main.chat.ui

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Videocam
import androidx.compose.material.icons.filled.ChatBubbleOutline
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import ke.co.smartroundclinic.doctor.domain.model.ThreadPreviewKind
import ke.co.smartroundclinic.doctor.domain.model.ConversationThread
import ke.co.smartroundclinic.doctor.domain.model.DoctorChatThread
import ke.co.smartroundclinic.doctor.presentation.common.composables.DashboardHeader
import ke.co.smartroundclinic.doctor.presentation.main.chat.otherdoctors.DoctorChatsListScreen
import ke.co.smartroundclinic.doctor.presentation.theme.Neutral20
import ke.co.smartroundclinic.doctor.presentation.theme.Neutral60
import ke.co.smartroundclinic.doctor.presentation.theme.Primary40
import ke.co.smartroundclinic.doctor.presentation.theme.Primary90
import ke.co.smartroundclinic.doctor.presentation.theme.ShapePill
import ke.co.smartroundclinic.doctor.presentation.theme.Tertiary40
import kotlinx.datetime.Clock
import kotlinx.datetime.Instant
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime

/** Mirrors bookings' BookingTopTab pattern: Consultations is the existing patient-chat list,
 * unchanged; Other Doctors is the doctor directory + doctor-to-doctor chat (see :doctor-chat
 * backend). Rendered as the same pill segmented control bookings uses below its header, with the
 * header's own slot given over to the search field. */
internal enum class ChatTopTab(val label: String) {
    CONSULTATIONS("Consultations"),
    OTHER_DOCTORS("Other Doctors"),
}

// ── Amended chat card (369×78 in the 414pt Figma frame) ──────────────────────
/** 369 wide in a 414 frame — the same 23dp gutter the amended Articles screens hang off. */
internal val ChatCardGutter = 23.dp
internal val ChatCardHeight = 78.dp
internal val ChatCardPadding = 18.dp
internal val ChatCardAvatarGap = 31.dp
internal const val ChatCardAvatarSize = 53
internal val ChatCardShape = RoundedCornerShape(12.dp)

/** #393938 at 3% — a wash just strong enough to separate the card from the page. */
internal val ChatCardBackground = Neutral20.copy(alpha = 0.03f)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun ChatListScreen(
    threads: List<ConversationThread>,
    onThreadClick: (ConversationThread) -> Unit,
    onDeleteThread: (ConversationThread) -> Unit = {},
    selectedTopTab: ChatTopTab,
    onTopTabSelected: (ChatTopTab) -> Unit,
    doctorThreads: List<DoctorChatThread> = emptyList(),
    onDoctorThreadClick: (DoctorChatThread) -> Unit = {},
    onOpenAllDoctors: () -> Unit = {},
    onProfileClick: () -> Unit = {},
    onNotificationsClick: () -> Unit = {},
    modifier: Modifier = Modifier,
) {
    var threadPendingDelete by remember { mutableStateOf<ConversationThread?>(null) }
    var searchQuery by remember { mutableStateOf("") }

    val visibleThreads = if (searchQuery.isNotBlank()) {
        threads.filter { it.counterpartName.contains(searchQuery, ignoreCase = true) }
    } else {
        threads
    }

    Scaffold(
        modifier = modifier,
        topBar = {
            DashboardHeader(
                title = "Chat",
                onProfileClick = onProfileClick,
                onNotificationsClick = onNotificationsClick,
                bottomContent = {
                    ChatSearchField(
                        query = searchQuery,
                        onQueryChange = { searchQuery = it },
                        placeholder = when (selectedTopTab) {
                            ChatTopTab.CONSULTATIONS -> "Search patients..."
                            ChatTopTab.OTHER_DOCTORS -> "Search doctors..."
                        },
                    )
                },
            )
        },
        contentWindowInsets = WindowInsets(0),
    ) { paddingValues ->
        Column(modifier = Modifier.fillMaxSize().padding(paddingValues)) {
            ChatTopTabRow(selectedTab = selectedTopTab, onTabSelected = onTopTabSelected)

            when (selectedTopTab) {
                ChatTopTab.CONSULTATIONS -> {
                    if (visibleThreads.isEmpty()) {
                        EmptyView(hasQuery = searchQuery.isNotBlank(), modifier = Modifier.weight(1f))
                    } else {
                        LazyColumn(
                            modifier = Modifier.weight(1f),
                            contentPadding = PaddingValues(horizontal = ChatCardGutter, vertical = 12.dp),
                            verticalArrangement = Arrangement.spacedBy(10.dp),
                        ) {
                            items(visibleThreads, key = { it.threadId }) { thread ->
                                ThreadCard(
                                    thread = thread,
                                    onClick = { onThreadClick(thread) },
                                    onLongClick = { threadPendingDelete = thread },
                                )
                            }
                        }
                    }
                }
                ChatTopTab.OTHER_DOCTORS -> {
                    DoctorChatsListScreen(
                        threads = doctorThreads,
                        searchQuery = searchQuery,
                        onThreadClick = onDoctorThreadClick,
                        onOpenAllDoctors = onOpenAllDoctors,
                        modifier = Modifier.weight(1f),
                    )
                }
            }
        }
    }

    threadPendingDelete?.let { thread ->
        AlertDialog(
            onDismissRequest = { threadPendingDelete = null },
            title = { Text("Delete conversation?") },
            text = { Text("This removes your conversation with ${thread.counterpartName} from this list. It will reappear if they send a new message.") },
            confirmButton = {
                TextButton(onClick = { onDeleteThread(thread); threadPendingDelete = null }) {
                    Text("Delete", color = MaterialTheme.colorScheme.error)
                }
            },
            dismissButton = {
                TextButton(onClick = { threadPendingDelete = null }) { Text("Cancel") }
            },
        )
    }
}

/**
 * The same pill segmented control bookings uses for Upcoming/Past, so the two tabbed lists in the
 * app read the same way.
 */
@Composable
private fun ChatTopTabRow(
    selectedTab: ChatTopTab,
    onTabSelected: (ChatTopTab) -> Unit,
    modifier: Modifier = Modifier,
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 12.dp)
            .shadow(elevation = 4.dp, shape = ShapePill)
            .clip(ShapePill)
            .background(MaterialTheme.colorScheme.surfaceVariant)
            .padding(4.dp),
    ) {
        Row {
            ChatTopTab.entries.forEach { tab ->
                val isSelected = selectedTab == tab
                Box(
                    contentAlignment = Alignment.Center,
                    modifier = Modifier
                        .weight(1f)
                        .clip(ShapePill)
                        .then(if (isSelected) Modifier.background(Color.White) else Modifier)
                        .clickable(
                            indication = null,
                            interactionSource = remember { MutableInteractionSource() },
                            onClick = { onTabSelected(tab) },
                        )
                        .padding(vertical = 10.dp),
                ) {
                    Text(
                        text = tab.label,
                        style = MaterialTheme.typography.labelMedium,
                        fontWeight = if (isSelected) FontWeight.SemiBold else FontWeight.Normal,
                        color = if (isSelected) {
                            MaterialTheme.colorScheme.onSurface
                        } else {
                            MaterialTheme.colorScheme.onSurfaceVariant
                        },
                    )
                }
            }
        }
    }
}

/**
 * Services' in-header search field, with the magnifier moved to the trailing edge — it doubles as
 * the clear button once there is a query to clear.
 */
@Composable
private fun ChatSearchField(
    query: String,
    onQueryChange: (String) -> Unit,
    placeholder: String,
    modifier: Modifier = Modifier,
) {
    val keyboardController = LocalSoftwareKeyboardController.current

    OutlinedTextField(
        value = query,
        onValueChange = onQueryChange,
        placeholder = {
            Text(
                text = placeholder,
                style = MaterialTheme.typography.bodyMedium,
                color = Color.White.copy(alpha = 0.6f),
            )
        },
        trailingIcon = {
            if (query.isEmpty()) {
                Icon(
                    imageVector = Icons.Filled.Search,
                    contentDescription = null,
                    tint = Color.White.copy(alpha = 0.8f),
                    modifier = Modifier.size(20.dp),
                )
            } else {
                IconButton(onClick = { onQueryChange("") }) {
                    Icon(
                        imageVector = Icons.Filled.Clear,
                        contentDescription = "Clear search",
                        tint = Color.White.copy(alpha = 0.8f),
                        modifier = Modifier.size(18.dp),
                    )
                }
            }
        },
        singleLine = true,
        keyboardOptions = KeyboardOptions(imeAction = ImeAction.Search),
        keyboardActions = KeyboardActions(onSearch = { keyboardController?.hide() }),
        shape = RoundedCornerShape(12.dp),
        colors = OutlinedTextFieldDefaults.colors(
            focusedTextColor = Color.White,
            unfocusedTextColor = Color.White,
            focusedBorderColor = Color.White.copy(alpha = 0.6f),
            unfocusedBorderColor = Color.White.copy(alpha = 0.35f),
            cursorColor = Color.White,
            focusedContainerColor = Color.White.copy(alpha = 0.15f),
            unfocusedContainerColor = Color.White.copy(alpha = 0.1f),
        ),
        textStyle = MaterialTheme.typography.bodyMedium,
        modifier = modifier.fillMaxWidth(),
    )
}

@Composable
private fun EmptyView(hasQuery: Boolean = false, modifier: Modifier = Modifier) {
    Column(
        modifier = modifier.fillMaxWidth().padding(horizontal = 32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
    ) {
        Box(modifier = Modifier.size(80.dp).clip(CircleShape).background(Primary90), contentAlignment = Alignment.Center) {
            Icon(imageVector = Icons.Filled.ChatBubbleOutline, contentDescription = null, tint = Primary40, modifier = Modifier.size(40.dp))
        }
        Spacer(Modifier.height(16.dp))
        Text(
            text = if (hasQuery) "No conversations found" else "No Conversations Yet",
            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
        )
        if (!hasQuery) {
            Spacer(Modifier.height(8.dp))
            Text(
                text = "Conversations with your patients will appear here\nonce you have a confirmed appointment.",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                textAlign = TextAlign.Center,
            )
        }
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
private fun ThreadCard(thread: ConversationThread, onClick: () -> Unit, onLongClick: () -> Unit = {}, modifier: Modifier = Modifier) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .height(ChatCardHeight)
            .clip(ChatCardShape)
            .background(ChatCardBackground)
            .combinedClickable(
                indication = null,
                interactionSource = remember { MutableInteractionSource() },
                onClick = onClick,
                onLongClick = onLongClick,
            )
            .padding(horizontal = ChatCardPadding),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Box {
            PatientAvatar(name = thread.counterpartName, picture = thread.counterpartPicture, size = ChatCardAvatarSize)
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

        Spacer(Modifier.width(ChatCardAvatarGap))

        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = thread.counterpartName,
                style = MaterialTheme.typography.bodyMedium.copy(
                    fontWeight = FontWeight.Bold,
                    fontSize = 14.sp,
                    letterSpacing = 0.sp,
                ),
                color = Neutral20,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
            )

            Spacer(Modifier.height(6.dp))

            // Preview and timestamp share the second line in the amended card, rather than the
            // timestamp sitting centred against the full row height.
            Row(verticalAlignment = Alignment.CenterVertically) {
                val previewIcon = when (thread.lastMessageKind) {
                    ThreadPreviewKind.PHOTO -> Icons.Filled.CameraAlt
                    ThreadPreviewKind.VIDEO -> Icons.Filled.Videocam
                    else -> null
                }
                if (previewIcon != null) {
                    Icon(
                        imageVector = previewIcon,
                        contentDescription = null,
                        tint = Neutral60,
                        modifier = Modifier.size(14.dp),
                    )
                    Spacer(Modifier.width(4.dp))
                }
                Text(
                    text = thread.lastMessagePreview ?: "No messages yet",
                    style = MaterialTheme.typography.bodyMedium.copy(fontSize = 13.sp, letterSpacing = 0.sp),
                    color = Neutral60,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier.weight(1f),
                )
                if (thread.lastMessageAt != null) {
                    Spacer(Modifier.width(8.dp))
                    Text(
                        text = formatThreadTimestamp(thread.lastMessageAt),
                        style = MaterialTheme.typography.bodyMedium.copy(fontSize = 13.sp, letterSpacing = 0.sp),
                        color = Neutral60,
                        maxLines = 1,
                    )
                }
            }
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
