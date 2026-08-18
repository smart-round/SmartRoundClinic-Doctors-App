package ke.co.smartroundclinic.doctor.presentation.main.chat.otherdoctors

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.Videocam
import androidx.compose.material.icons.automirrored.filled.Chat
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import ke.co.smartroundclinic.doctor.domain.model.ThreadPreviewKind
import ke.co.smartroundclinic.doctor.domain.model.DoctorChatThread
import ke.co.smartroundclinic.doctor.presentation.main.chat.ui.ChatCardAvatarGap
import ke.co.smartroundclinic.doctor.presentation.main.chat.ui.ChatCardAvatarSize
import ke.co.smartroundclinic.doctor.presentation.main.chat.ui.ChatCardBackground
import ke.co.smartroundclinic.doctor.presentation.main.chat.ui.ChatCardGutter
import ke.co.smartroundclinic.doctor.presentation.main.chat.ui.ChatCardHeight
import ke.co.smartroundclinic.doctor.presentation.main.chat.ui.ChatCardPadding
import ke.co.smartroundclinic.doctor.presentation.main.chat.ui.ChatCardShape
import ke.co.smartroundclinic.doctor.presentation.theme.Neutral20
import ke.co.smartroundclinic.doctor.presentation.theme.Neutral60
import ke.co.smartroundclinic.doctor.presentation.theme.Primary40
import ke.co.smartroundclinic.doctor.presentation.theme.Primary90
import ke.co.smartroundclinic.doctor.presentation.theme.Tertiary40
import kotlinx.datetime.Clock
import kotlinx.datetime.Instant
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime

/**
 * The Other Doctors tab's default content — just this doctor's existing doctor-to-doctor
 * conversations ("Chats"), newest-message-first. Browsing the full doctor directory to start a
 * new conversation now lives in a separate screen ([DoctorDirectoryScreen]), reached via the FAB
 * here rather than being inlined below this list.
 */
@Composable
internal fun DoctorChatsListScreen(
    threads: List<DoctorChatThread>,
    onThreadClick: (DoctorChatThread) -> Unit,
    onOpenAllDoctors: () -> Unit,
    // Owned by the Chat header's search field, which spans both tabs.
    searchQuery: String = "",
    modifier: Modifier = Modifier,
) {
    // A thread exists as soon as either side hits "Connect", before any message is actually
    // sent — only show it here once there's something to actually show a preview/timestamp for.
    val threadsWithMessages = threads.filter { it.lastMessageAt != null }
    val visibleThreads = if (searchQuery.isNotBlank()) {
        threadsWithMessages.filter { it.counterpartName.contains(searchQuery, ignoreCase = true) }
    } else {
        threadsWithMessages
    }

    Box(modifier = modifier.fillMaxSize()) {
        Column(modifier = Modifier.fillMaxSize()) {
            if (visibleThreads.isEmpty()) {
                EmptyChatsView(hasQuery = searchQuery.isNotBlank(), modifier = Modifier.weight(1f))
            } else {
                LazyColumn(
                    modifier = Modifier.weight(1f),
                    contentPadding = PaddingValues(
                        start = ChatCardGutter,
                        end = ChatCardGutter,
                        top = 12.dp,
                        // Clears the "browse all doctors" FAB in the bottom-end corner.
                        bottom = 88.dp,
                    ),
                    verticalArrangement = Arrangement.spacedBy(10.dp),
                ) {
                    items(visibleThreads, key = { it.threadId }) { thread ->
                        ThreadCard(thread = thread, onClick = { onThreadClick(thread) })
                    }
                }
            }
        }

        FloatingActionButton(
            onClick = onOpenAllDoctors,
            modifier = Modifier.align(Alignment.BottomEnd).padding(16.dp),
            containerColor = Primary40,
            contentColor = Color.White,
        ) {
            Icon(imageVector = Icons.AutoMirrored.Filled.Chat, contentDescription = "Browse all doctors")
        }
    }
}

@Composable
private fun EmptyChatsView(hasQuery: Boolean, modifier: Modifier = Modifier) {
    Column(
        modifier = modifier.fillMaxWidth().padding(horizontal = 32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
    ) {
        Box(modifier = Modifier.size(80.dp).clip(CircleShape).background(Primary90), contentAlignment = Alignment.Center) {
            Icon(imageVector = Icons.Filled.Person, contentDescription = null, tint = Primary40, modifier = Modifier.size(40.dp))
        }
        Spacer(Modifier.height(16.dp))
        Text(
            text = if (hasQuery) "No chats found" else "No Conversations Yet",
            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
            textAlign = TextAlign.Center,
        )
        if (!hasQuery) {
            Spacer(Modifier.height(8.dp))
            Text(
                text = "Tap the chat button below to browse doctors and start a conversation.",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                textAlign = TextAlign.Center,
            )
        }
    }
}

@Composable
private fun ThreadCard(thread: DoctorChatThread, onClick: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(ChatCardHeight)
            .clip(ChatCardShape)
            .background(ChatCardBackground)
            .clickable(onClick = onClick)
            .padding(horizontal = ChatCardPadding),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Box {
            Avatar(pictureUrl = thread.counterpartPicture, size = ChatCardAvatarSize)
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
                text = "Dr. ${thread.counterpartName}",
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
