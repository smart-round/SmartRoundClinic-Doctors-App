package ke.co.smartroundclinic.doctor.presentation.main.chat.ui

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
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ChatBubbleOutline
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import ke.co.smartroundclinic.doctor.presentation.main.chat.ChatUi
import ke.co.smartroundclinic.doctor.presentation.theme.Primary40
import ke.co.smartroundclinic.doctor.presentation.theme.Primary90
import ke.co.smartroundclinic.doctor.presentation.theme.Secondary40
import ke.co.smartroundclinic.doctor.presentation.theme.Secondary90

@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun ChatListScreen(
    chats: List<ChatUi>,
    onChatClick: (ChatUi) -> Unit,
    modifier: Modifier = Modifier,
) {
    Scaffold(
        modifier = modifier,
        topBar = {
            TopAppBar(title = { Text("Chats", style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)) })
        },
        contentWindowInsets = WindowInsets(0),
    ) { paddingValues ->
        Column(modifier = Modifier.fillMaxSize().padding(paddingValues)) {
            if (chats.isEmpty()) {
                EmptyChatsView(modifier = Modifier.weight(1f))
            } else {
                LazyColumn(modifier = Modifier.weight(1f)) {
                    items(chats, key = { it.id }) { chat ->
                        ChatRow(chat = chat, onClick = { onChatClick(chat) })
                        HorizontalDivider(thickness = 0.5.dp, color = MaterialTheme.colorScheme.outlineVariant, modifier = Modifier.padding(start = 76.dp))
                    }
                }
            }
        }
    }
}

@Composable
private fun EmptyChatsView(modifier: Modifier = Modifier) {
    Column(
        modifier = modifier.fillMaxWidth().padding(horizontal = 32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
    ) {
        Box(modifier = Modifier.size(80.dp).clip(CircleShape).background(Primary90), contentAlignment = Alignment.Center) {
            Icon(imageVector = Icons.Filled.ChatBubbleOutline, contentDescription = null, tint = Primary40, modifier = Modifier.size(40.dp))
        }
        Spacer(Modifier.height(16.dp))
        Text(text = "No Chats Yet", style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold))
        Spacer(Modifier.height(8.dp))
        Text(text = "Messages will appear as soon as patients reach out", style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant, textAlign = TextAlign.Center)
    }
}

@Composable
private fun ChatRow(chat: ChatUi, onClick: () -> Unit, modifier: Modifier = Modifier) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .clickable(indication = null, interactionSource = remember { MutableInteractionSource() }, onClick = onClick)
            .padding(horizontal = 16.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        Box(modifier = Modifier.size(48.dp).clip(CircleShape).background(Secondary90), contentAlignment = Alignment.Center) {
            Icon(imageVector = Icons.Filled.Person, contentDescription = null, tint = Secondary40, modifier = Modifier.size(28.dp))
        }
        Column(modifier = Modifier.weight(1f)) {
            Text(text = chat.participantName, style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.SemiBold))
            Spacer(Modifier.height(2.dp))
            Text(text = chat.lastMessage, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant, maxLines = 1, overflow = TextOverflow.Ellipsis)
        }
        Column(horizontalAlignment = Alignment.End, verticalArrangement = Arrangement.spacedBy(4.dp)) {
            Text(text = chat.time, style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
            if (chat.unreadCount > 0) {
                Box(modifier = Modifier.size(18.dp).clip(CircleShape).background(Primary40), contentAlignment = Alignment.Center) {
                    Text(text = chat.unreadCount.toString(), style = MaterialTheme.typography.labelSmall, color = Color.White)
                }
            }
        }
    }
}
