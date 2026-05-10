package ke.co.smartroundclinic.doctor.presentation.main.chat.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.filled.AttachFile
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material.icons.filled.Videocam
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
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import ke.co.smartroundclinic.doctor.presentation.main.chat.ChatUi
import ke.co.smartroundclinic.doctor.presentation.main.chat.MessageUi
import ke.co.smartroundclinic.doctor.presentation.theme.Primary40
import ke.co.smartroundclinic.doctor.presentation.theme.Secondary40
import ke.co.smartroundclinic.doctor.presentation.theme.Secondary90
import ke.co.smartroundclinic.doctor.presentation.theme.ShapePill
import kotlin.time.Clock
import kotlin.time.ExperimentalTime

@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun ConversationScreen(
    chat: ChatUi,
    onBack: () -> Unit,
    onVoiceCall: () -> Unit,
    onVideoCall: () -> Unit,
    modifier: Modifier = Modifier,
) {
    var messages by remember { mutableStateOf(chat.messages) }
    var inputText by remember { mutableStateOf("") }
    val listState = rememberLazyListState()

    LaunchedEffect(messages.size) {
        if (messages.isNotEmpty()) listState.animateScrollToItem(messages.size - 1)
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
                title = {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier.size(32.dp).clip(CircleShape).background(Secondary90),
                            contentAlignment = Alignment.Center,
                        ) {
                            Icon(imageVector = Icons.Filled.Person, contentDescription = null, tint = Secondary40, modifier = Modifier.size(20.dp))
                        }
                        Spacer(Modifier.width(8.dp))
                        Text(text = chat.participantName, style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold))
                    }
                },
                actions = {
                    IconButton(onClick = onVoiceCall) {
                        Icon(imageVector = Icons.Filled.Phone, contentDescription = "Voice call", tint = Primary40)
                    }
                    IconButton(onClick = onVideoCall) {
                        Icon(imageVector = Icons.Filled.Videocam, contentDescription = "Video call", tint = Primary40)
                    }
                },
            )
        },
        contentWindowInsets = WindowInsets(0),
    ) { paddingValues ->
        Column(modifier = Modifier.fillMaxSize().padding(paddingValues)) {
            HorizontalDivider(thickness = 0.5.dp, color = MaterialTheme.colorScheme.outlineVariant)

            if (messages.isEmpty()) {
                Box(modifier = Modifier.weight(1f).fillMaxWidth(), contentAlignment = Alignment.Center) {
                    Text(text = "No messages yet. Say hello!", style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
            } else {
                LazyColumn(
                    state = listState,
                    modifier = Modifier.weight(1f),
                    contentPadding = PaddingValues(horizontal = 16.dp, vertical = 12.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                ) {
                    items(messages, key = { it.id }) { message -> MessageBubble(message = message) }
                }
            }

            MessageInput(
                value = inputText,
                onValueChange = { inputText = it },
                onSend = {
                    if (inputText.isNotBlank()) {
                        @OptIn(ExperimentalTime::class)
                        val epochMs = Clock.System.now().toEpochMilliseconds()
                        val totalMin = (epochMs / 60_000).toInt()
                        val now = "${((totalMin / 60) % 24).toString().padStart(2, '0')}:${(totalMin % 60).toString().padStart(2, '0')}"
                        messages = messages + MessageUi(id = messages.size + 1, text = inputText.trim(), fromMe = true, time = now)
                        inputText = ""
                    }
                },
            )
        }
    }
}

@Composable
private fun MessageBubble(message: MessageUi, modifier: Modifier = Modifier) {
    Box(
        modifier = modifier.fillMaxWidth(),
        contentAlignment = if (message.fromMe) Alignment.CenterEnd else Alignment.CenterStart,
    ) {
        Column(horizontalAlignment = if (message.fromMe) Alignment.End else Alignment.Start) {
            Box(
                modifier = Modifier
                    .widthIn(max = 260.dp)
                    .clip(RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp, bottomStart = if (message.fromMe) 16.dp else 4.dp, bottomEnd = if (message.fromMe) 4.dp else 16.dp))
                    .background(if (message.fromMe) Primary40 else MaterialTheme.colorScheme.surfaceVariant)
                    .padding(horizontal = 12.dp, vertical = 8.dp),
            ) {
                Text(text = message.text, style = MaterialTheme.typography.bodyMedium, color = if (message.fromMe) Color.White else MaterialTheme.colorScheme.onSurface)
            }
            Text(text = message.time, style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant, modifier = Modifier.padding(top = 2.dp, start = 4.dp, end = 4.dp))
        }
    }
}

@Composable
private fun MessageInput(value: String, onValueChange: (String) -> Unit, onSend: () -> Unit, modifier: Modifier = Modifier) {
    Column(modifier = modifier) {
        HorizontalDivider(thickness = 0.5.dp, color = MaterialTheme.colorScheme.outlineVariant)
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(MaterialTheme.colorScheme.surface)
                .navigationBarsPadding()
                .imePadding()
                .padding(horizontal = 12.dp, vertical = 8.dp),
            verticalAlignment = Alignment.Bottom,
            horizontalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            IconButton(onClick = {}) {
                Icon(imageVector = Icons.Filled.AttachFile, contentDescription = "Attach file", tint = MaterialTheme.colorScheme.onSurfaceVariant)
            }
            Box(
                modifier = Modifier.weight(1f).clip(ShapePill).background(MaterialTheme.colorScheme.surfaceVariant).padding(horizontal = 16.dp, vertical = 10.dp),
                contentAlignment = Alignment.CenterStart,
            ) {
                if (value.isEmpty()) {
                    Text(text = "Type a message here...", style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
                BasicTextField(value = value, onValueChange = onValueChange, textStyle = MaterialTheme.typography.bodyMedium.copy(color = MaterialTheme.colorScheme.onSurface), cursorBrush = SolidColor(Primary40), modifier = Modifier.fillMaxWidth(), maxLines = 4)
            }
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .clip(CircleShape)
                    .background(if (value.isNotBlank()) Primary40 else MaterialTheme.colorScheme.surfaceVariant)
                    .clickable(indication = null, interactionSource = remember { MutableInteractionSource() }, onClick = onSend),
                contentAlignment = Alignment.Center,
            ) {
                Icon(imageVector = Icons.AutoMirrored.Filled.Send, contentDescription = "Send", tint = if (value.isNotBlank()) Color.White else MaterialTheme.colorScheme.onSurfaceVariant, modifier = Modifier.size(20.dp))
            }
        }
    }
}
