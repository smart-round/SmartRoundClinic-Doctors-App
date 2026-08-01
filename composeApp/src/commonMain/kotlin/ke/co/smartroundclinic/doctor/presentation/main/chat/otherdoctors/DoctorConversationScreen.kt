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
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.InsertDriveFile
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.filled.AttachFile
import androidx.compose.material.icons.filled.Call
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Videocam
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
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
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage
import io.github.vinceglb.filekit.dialogs.FileKitMode
import io.github.vinceglb.filekit.dialogs.FileKitType
import io.github.vinceglb.filekit.dialogs.compose.rememberFilePickerLauncher
import io.github.vinceglb.filekit.name
import io.github.vinceglb.filekit.readBytes
import ke.co.smartroundclinic.doctor.domain.model.DoctorChatFileAttachment
import ke.co.smartroundclinic.doctor.domain.model.DoctorChatMessage
import ke.co.smartroundclinic.doctor.presentation.main.chat.PendingFile
import ke.co.smartroundclinic.doctor.presentation.theme.CardBackground
import ke.co.smartroundclinic.doctor.presentation.theme.Primary40
import ke.co.smartroundclinic.doctor.presentation.theme.Primary90
import kotlinx.coroutines.launch

/**
 * Simplified sibling of [ke.co.smartroundclinic.doctor.presentation.main.chat.ui.ConversationScreen]:
 * text + file messages and call buttons, no typing/online-status indicators (see this feature's
 * scope notes) and no medical-record/patient-bio side panels, since there's no patient here.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun DoctorConversationScreen(
    counterpartName: String,
    counterpartPicture: String?,
    messages: List<DoctorChatMessage>,
    isLoadingHistory: Boolean,
    isLoadingMoreHistory: Boolean,
    hasMoreHistory: Boolean,
    onLoadMoreHistory: () -> Unit,
    isConnected: Boolean,
    isUploadingFile: Boolean,
    pendingFiles: List<PendingFile>,
    currentUserId: String,
    incomingCall: DoctorIncomingCall?,
    onAcceptIncomingCall: () -> Unit,
    onDeclineIncomingCall: () -> Unit,
    onBack: () -> Unit,
    onVoiceCall: () -> Unit,
    onVideoCall: () -> Unit,
    onSendText: (String) -> Unit,
    onSendFile: (String, String, ByteArray) -> Unit,
    modifier: Modifier = Modifier,
) {
    var inputText by remember { mutableStateOf("") }
    val listState = rememberLazyListState()
    val scope = rememberCoroutineScope()

    val nearTop by remember {
        derivedStateOf { listState.firstVisibleItemIndex <= 2 }
    }
    LaunchedEffect(nearTop) {
        if (nearTop && hasMoreHistory && !isLoadingHistory && !isLoadingMoreHistory) onLoadMoreHistory()
    }

    val fileLauncher = rememberFilePickerLauncher(mode = FileKitMode.Single, type = FileKitType.File()) { file ->
        file?.let {
            scope.launch { onSendFile(it.name.ifBlank { "file" }, mimeFromName(it.name), it.readBytes()) }
        }
    }

    if (incomingCall != null) {
        AlertDialog(
            onDismissRequest = onDeclineIncomingCall,
            title = { Text(if (incomingCall.isVideo) "Incoming video call" else "Incoming voice call") },
            text = { Text("Dr. ${incomingCall.callerName ?: counterpartName} is calling you") },
            confirmButton = { TextButton(onClick = onAcceptIncomingCall) { Text("Accept") } },
            dismissButton = { TextButton(onClick = onDeclineIncomingCall) { Text("Decline", color = MaterialTheme.colorScheme.error) } },
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
                title = {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(modifier = Modifier.size(36.dp).clip(CircleShape).background(Primary90), contentAlignment = Alignment.Center) {
                            if (counterpartPicture != null) {
                                AsyncImage(model = counterpartPicture, contentDescription = counterpartName, contentScale = ContentScale.Crop, modifier = Modifier.fillMaxSize().clip(CircleShape))
                            } else {
                                Icon(imageVector = Icons.Filled.Person, contentDescription = null, tint = Primary40, modifier = Modifier.size(18.dp))
                            }
                        }
                        Spacer(Modifier.size(8.dp))
                        Text(text = "Dr. $counterpartName", style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.SemiBold))
                    }
                },
                actions = {
                    IconButton(onClick = onVoiceCall) { Icon(imageVector = Icons.Filled.Call, contentDescription = "Voice call", tint = Primary40) }
                    IconButton(onClick = onVideoCall) { Icon(imageVector = Icons.Filled.Videocam, contentDescription = "Video call", tint = Primary40) }
                },
            )
        },
    ) { paddingValues ->
        Column(modifier = Modifier.fillMaxSize().padding(paddingValues)) {
            if (isLoadingHistory) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) { CircularProgressIndicator() }
            } else {
                LazyColumn(
                    state = listState,
                    modifier = Modifier.weight(1f).fillMaxWidth(),
                    contentPadding = androidx.compose.foundation.layout.PaddingValues(horizontal = 12.dp, vertical = 12.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                ) {
                    if (isLoadingMoreHistory) {
                        item(key = "loading_more") {
                            Box(modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
                                CircularProgressIndicator(modifier = Modifier.size(20.dp), strokeWidth = 2.dp)
                            }
                        }
                    }
                    items(messages, key = { it.id }) { message ->
                        MessageBubble(message = message, isMine = message.senderId == currentUserId)
                    }
                    items(pendingFiles, key = { "pending-${it.localId}" }) { pending ->
                        PendingFileBubble(pending)
                    }
                }
            }

            if (!isConnected) {
                Text(
                    text = "Reconnecting…",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 4.dp),
                )
            }

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .navigationBarsPadding()
                    .imePadding()
                    .padding(horizontal = 8.dp, vertical = 8.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                IconButton(onClick = { fileLauncher.launch() }, enabled = !isUploadingFile) {
                    Icon(imageVector = Icons.Filled.AttachFile, contentDescription = "Attach file", tint = Primary40)
                }
                OutlinedTextField(
                    value = inputText,
                    onValueChange = { inputText = it },
                    placeholder = { Text("Message") },
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(24.dp),
                )
                IconButton(
                    onClick = {
                        if (inputText.isNotBlank()) {
                            onSendText(inputText.trim())
                            inputText = ""
                        }
                    },
                    colors = IconButtonDefaults.iconButtonColors(contentColor = Primary40),
                ) {
                    Icon(imageVector = Icons.AutoMirrored.Filled.Send, contentDescription = "Send")
                }
            }
        }
    }
}

@Composable
private fun MessageBubble(message: DoctorChatMessage, isMine: Boolean) {
    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = if (isMine) Arrangement.End else Arrangement.Start) {
        Column(
            modifier = Modifier
                .widthIn(max = 280.dp)
                .clip(RoundedCornerShape(16.dp))
                .background(if (isMine) Primary90 else CardBackground)
                .padding(horizontal = 12.dp, vertical = 8.dp),
        ) {
            if (message.messageType.uppercase() == "FILE") {
                message.files.forEach { FileChip(it) }
            } else {
                Text(text = message.message ?: "", style = MaterialTheme.typography.bodyMedium)
            }
        }
    }
}

@Composable
private fun FileChip(file: DoctorChatFileAttachment) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Icon(imageVector = Icons.AutoMirrored.Filled.InsertDriveFile, contentDescription = null, tint = Primary40, modifier = Modifier.size(18.dp))
        Spacer(Modifier.size(6.dp))
        Text(text = file.fileName, style = MaterialTheme.typography.bodySmall)
    }
}

@Composable
private fun PendingFileBubble(pending: PendingFile) {
    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.End) {
        Row(
            modifier = Modifier
                .clip(RoundedCornerShape(16.dp))
                .background(Primary90.copy(alpha = if (pending.failed) 0.4f else 1f))
                .padding(horizontal = 12.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            if (!pending.failed) CircularProgressIndicator(modifier = Modifier.size(14.dp), strokeWidth = 2.dp)
            Spacer(Modifier.size(6.dp))
            Text(text = if (pending.failed) "${pending.fileName} (failed)" else pending.fileName, style = MaterialTheme.typography.bodySmall)
        }
    }
}

private fun mimeFromName(name: String): String = when (name.substringAfterLast('.', "").lowercase()) {
    "jpg", "jpeg" -> "image/jpeg"
    "png" -> "image/png"
    "pdf" -> "application/pdf"
    "doc" -> "application/msword"
    "docx" -> "application/vnd.openxmlformats-officedocument.wordprocessingml.document"
    else -> "application/octet-stream"
}
