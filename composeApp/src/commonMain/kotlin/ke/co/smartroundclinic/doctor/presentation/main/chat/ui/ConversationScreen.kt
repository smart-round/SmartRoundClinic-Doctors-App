package ke.co.smartroundclinic.doctor.presentation.main.chat.ui

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.WindowInsetsSides
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.ime
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.only
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.union
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.InsertDriveFile
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.filled.AttachFile
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.Circle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Download
import androidx.compose.material.icons.filled.Folder
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Photo
import androidx.compose.material.icons.filled.PictureAsPdf
import androidx.compose.material.icons.filled.StopCircle
import androidx.compose.material.icons.filled.Videocam
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage
import io.github.vinceglb.filekit.dialogs.FileKitMode
import io.github.vinceglb.filekit.dialogs.FileKitType
import io.github.vinceglb.filekit.dialogs.compose.rememberCameraPickerLauncher
import io.github.vinceglb.filekit.dialogs.compose.rememberFilePickerLauncher
import io.github.vinceglb.filekit.name
import io.github.vinceglb.filekit.readBytes
import ke.co.smartroundclinic.doctor.data.remote.dto.response.MedicalRecordData
import ke.co.smartroundclinic.doctor.domain.model.ConsultationFileAttachment
import ke.co.smartroundclinic.doctor.domain.model.ConsultationMessage
import ke.co.smartroundclinic.doctor.domain.model.ConsultationSession
import ke.co.smartroundclinic.doctor.domain.model.MedicalRecord
import ke.co.smartroundclinic.doctor.domain.model.PatientBio
import ke.co.smartroundclinic.doctor.presentation.main.chat.PendingFile
import ke.co.smartroundclinic.doctor.presentation.theme.Error40
import ke.co.smartroundclinic.doctor.presentation.theme.Neutral40
import ke.co.smartroundclinic.doctor.presentation.theme.Neutral90
import ke.co.smartroundclinic.doctor.presentation.theme.Primary40
import ke.co.smartroundclinic.doctor.presentation.theme.Primary90
import ke.co.smartroundclinic.doctor.presentation.theme.ShapeCard
import ke.co.smartroundclinic.doctor.presentation.theme.Tertiary90
import kotlinx.serialization.json.Json
import ke.co.smartroundclinic.doctor.presentation.theme.Secondary40
import ke.co.smartroundclinic.doctor.presentation.theme.Secondary90
import ke.co.smartroundclinic.doctor.presentation.theme.ShapePill
import ke.co.smartroundclinic.doctor.presentation.theme.Tertiary40
import kotlinx.coroutines.launch
import kotlinx.datetime.Instant
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime

@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun ConversationScreen(
    patientName: String,
    patientPicture: String? = null,
    session: ConsultationSession?,
    messages: List<ConsultationMessage>,
    pendingFiles: List<PendingFile> = emptyList(),
    isStartingSession: Boolean,
    isLoadingHistory: Boolean = false,
    isLoadingMoreHistory: Boolean = false,
    hasMoreHistory: Boolean = false,
    onLoadMoreHistory: () -> Unit = {},
    isConnected: Boolean,
    isUploadingFile: Boolean,
    isCallEnabled: Boolean,
    currentUserId: String,
    patientBio: PatientBio? = null,
    patientHistory: List<MedicalRecord> = emptyList(),
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
    var viewerFile by remember { mutableStateOf<ConsultationFileAttachment?>(null) }
    var showAttachMenu by remember { mutableStateOf(false) }
    var showPatientSheet by remember { mutableStateOf(false) }

    // NOT remember()'d: `messages` is a SnapshotStateList mutated in place (add/addAll/clear), so its
    // identity never changes — remember(messages) would cache the first result forever and silently
    // go stale on every websocket-delivered message. Recomputing here is cheap and always correct.
    val conversationItems = buildConversationItems(messages)
    val totalItems = conversationItems.size + pendingFiles.size

    // Only the newest message changing (initial load, or a live append) should snap to bottom —
    // loadMoreHistory prepends older messages at the top and must NOT fight the user's scroll.
    val lastMessageId = messages.lastOrNull()?.id
    LaunchedEffect(lastMessageId, pendingFiles.size) {
        if (totalItems > 0) listState.animateScrollToItem(totalItems - 1)
    }

    LaunchedEffect(listState, hasMoreHistory) {
        snapshotFlow { listState.firstVisibleItemIndex }.collect { index ->
            if (hasMoreHistory && !isLoadingMoreHistory && index <= 2) {
                onLoadMoreHistory()
            }
        }
    }

    fun sendPickedFile(name: String, bytes: ByteArray) {
        scope.launch { onSendFile(name, mimeFromName(name), bytes) }
    }

    // Camera photos may have no recognisable extension — force .jpg so MIME is correct
    val cameraLauncher = rememberCameraPickerLauncher { file ->
        file?.let {
            val name = it.name.takeIf { n -> n.isNotBlank() && n.contains('.') } ?: "photo.jpg"
            scope.launch { sendPickedFile(name, it.readBytes()) }
        }
    }
    val galleryLauncher = rememberFilePickerLauncher(mode = FileKitMode.Single, type = FileKitType.Image) { file ->
        file?.let { scope.launch { sendPickedFile(it.name.ifBlank { "image.jpg" }, it.readBytes()) } }
    }
    val fileLauncher = rememberFilePickerLauncher(mode = FileKitMode.Single, type = FileKitType.File()) { file ->
        file?.let { scope.launch { sendPickedFile(it.name.ifBlank { "file" }, it.readBytes()) } }
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
                    Row(
                        modifier = Modifier.clickable(
                            indication = null,
                            interactionSource = remember { MutableInteractionSource() },
                        ) { showPatientSheet = true },
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        PatientAvatar(name = patientName, picture = patientPicture, size = 36)
                        Spacer(Modifier.width(10.dp))
                        Column {
                            Text(
                                text = patientName,
                                style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis,
                            )
                            if (session != null) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(4.dp),
                                ) {
                                    Icon(
                                        imageVector = Icons.Filled.Circle,
                                        contentDescription = null,
                                        tint = if (isConnected) Tertiary40 else MaterialTheme.colorScheme.onSurfaceVariant,
                                        modifier = Modifier.size(7.dp),
                                    )
                                    Text(
                                        text = if (isConnected) "Online" else "Connecting…",
                                        style = MaterialTheme.typography.labelSmall,
                                        color = if (isConnected) Tertiary40 else MaterialTheme.colorScheme.onSurfaceVariant,
                                    )
                                }
                            }
                        }
                    }
                },
                actions = {
                    if (session != null && isCallEnabled) {
                        IconButton(onClick = onVideoCall) {
                            Icon(imageVector = Icons.Filled.Videocam, contentDescription = "Video call", tint = Primary40)
                        }
                    }
                },
            )
        },
        contentWindowInsets = WindowInsets(0),
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(top = paddingValues.calculateTopPadding())
                .background(MaterialTheme.colorScheme.surface),
        ) {
            HorizontalDivider(thickness = 0.5.dp, color = MaterialTheme.colorScheme.outlineVariant)

            when {
                isLoadingHistory && messages.isEmpty() -> {
                    Box(modifier = Modifier.weight(1f).fillMaxWidth(), contentAlignment = Alignment.Center) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.spacedBy(12.dp)) {
                            CircularProgressIndicator(color = Primary40)
                            Text("Loading conversation…", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        }
                    }
                }
                messages.isEmpty() && pendingFiles.isEmpty() -> {
                    Box(modifier = Modifier.weight(1f).fillMaxWidth(), contentAlignment = Alignment.Center) {
                        Text(
                            text = if (isStartingSession) "Starting session…" else "Say hello to start the conversation!",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                        )
                    }
                }
                else -> {
                    LazyColumn(
                        state = listState,
                        modifier = Modifier.weight(1f),
                        contentPadding = PaddingValues(horizontal = 12.dp, vertical = 12.dp),
                        verticalArrangement = Arrangement.spacedBy(4.dp),
                    ) {
                        if (isLoadingMoreHistory) {
                            item(key = "loading_more") {
                                Box(modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp), contentAlignment = Alignment.Center) {
                                    CircularProgressIndicator(modifier = Modifier.size(20.dp), color = Primary40, strokeWidth = 2.dp)
                                }
                            }
                        }
                        items(
                            conversationItems,
                            key = { item ->
                                when (item) {
                                    is ConversationItem.ConsultationDivider -> "divider_${item.consultationId}"
                                    is ConversationItem.MessageItem -> item.message.id
                                }
                            },
                        ) { item ->
                            when (item) {
                                is ConversationItem.ConsultationDivider -> ConsultationDividerRow(label = item.label)
                                is ConversationItem.MessageItem -> MessageBubble(
                                    message = item.message,
                                    fromMe = item.message.senderId == currentUserId,
                                    onFileClick = { viewerFile = it },
                                )
                            }
                        }
                        // Optimistic pending messages — appear immediately, removed when server echoes back
                        items(pendingFiles, key = { "p_${it.localId}" }) { pending ->
                            PendingFileBubble(pending = pending)
                        }
                    }
                }
            }

            MessageInput(
                modifier = Modifier.windowInsetsPadding(
                    WindowInsets.navigationBars.union(WindowInsets.ime)
                ),
                value = inputText,
                onValueChange = { inputText = it },
                isUploading = isUploadingFile,
                enabled = session != null,
                onSend = {
                    if (inputText.isNotBlank()) {
                        onSendText(inputText.trim())
                        inputText = ""
                    }
                },
                onAttach = { showAttachMenu = true },
            )
        }
    }

    if (showAttachMenu) {
        AttachMenuSheet(
            onDismiss = { showAttachMenu = false },
            onCamera = {
                showAttachMenu = false
                cameraLauncher.launch()
            },
            onGallery = {
                showAttachMenu = false
                galleryLauncher.launch()
            },
            onFile = {
                showAttachMenu = false
                fileLauncher.launch()
            },
        )
    }

    viewerFile?.let { file ->
        FileViewerSheet(file = file, onDismiss = { viewerFile = null })
    }

    if (showPatientSheet) {
        PatientChatSheet(
            patientName = patientName,
            patientPicture = patientPicture,
            bio = patientBio,
            history = patientHistory,
            onDismiss = { showPatientSheet = false },
        )
    }
}

private sealed class ConversationItem {
    data class ConsultationDivider(val label: String, val consultationId: String) : ConversationItem()
    data class MessageItem(val message: ConsultationMessage) : ConversationItem()
}

// The merged history spans every consultation a doctor-patient pair has had — insert a divider
// whenever the consultation changes so users can tell which visit a run of messages belongs to.
private fun buildConversationItems(messages: List<ConsultationMessage>): List<ConversationItem> {
    val items = mutableListOf<ConversationItem>()
    var lastConsultationId: String? = null
    for (message in messages) {
        if (message.consultationId != lastConsultationId) {
            items += ConversationItem.ConsultationDivider(consultationDividerLabel(message.createdAt), message.consultationId)
            lastConsultationId = message.consultationId
        }
        items += ConversationItem.MessageItem(message)
    }
    return items
}

private fun consultationDividerLabel(iso: String): String = try {
    val date = Instant.parse(iso).toLocalDateTime(TimeZone.currentSystemDefault()).date
    val month = date.month.name.lowercase().replaceFirstChar { it.uppercase() }
    "Consultation — $month ${date.dayOfMonth}, ${date.year}"
} catch (_: Exception) { "Consultation" }

@Composable
private fun ConsultationDividerRow(label: String, modifier: Modifier = Modifier) {
    Row(
        modifier = modifier.fillMaxWidth().padding(vertical = 10.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        HorizontalDivider(modifier = Modifier.weight(1f), thickness = 0.5.dp, color = MaterialTheme.colorScheme.outlineVariant)
        Text(
            text = label,
            style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.SemiBold),
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )
        HorizontalDivider(modifier = Modifier.weight(1f), thickness = 0.5.dp, color = MaterialTheme.colorScheme.outlineVariant)
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun AttachMenuSheet(
    onDismiss: () -> Unit,
    onCamera: () -> Unit,
    onGallery: () -> Unit,
    onFile: () -> Unit,
) {
    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true),
        containerColor = MaterialTheme.colorScheme.surface,
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .windowInsetsPadding(WindowInsets.navigationBars)
                .padding(start = 16.dp, end = 16.dp, bottom = 24.dp),
        ) {
            Text(
                text = "Share",
                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                modifier = Modifier.padding(vertical = 12.dp),
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(16.dp),
            ) {
                AttachOption(
                    icon = Icons.Filled.CameraAlt,
                    label = "Camera",
                    tint = Color(0xFF1976D2),
                    background = Color(0xFFE3F2FD),
                    onClick = onCamera,
                    modifier = Modifier.weight(1f),
                )
                AttachOption(
                    icon = Icons.Filled.Photo,
                    label = "Gallery",
                    tint = Color(0xFF7B1FA2),
                    background = Color(0xFFF3E5F5),
                    onClick = onGallery,
                    modifier = Modifier.weight(1f),
                )
                AttachOption(
                    icon = Icons.Filled.Folder,
                    label = "Files",
                    tint = Color(0xFFE65100),
                    background = Color(0xFFFFF3E0),
                    onClick = onFile,
                    modifier = Modifier.weight(1f),
                )
            }

            Spacer(Modifier.height(8.dp))
        }
    }
}

@Composable
private fun AttachOption(
    icon: ImageVector,
    label: String,
    tint: Color,
    background: Color,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier
            .clickable(
                indication = null,
                interactionSource = remember { MutableInteractionSource() },
                onClick = onClick,
            )
            .padding(vertical = 8.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        Box(
            modifier = Modifier
                .size(56.dp)
                .clip(CircleShape)
                .background(background),
            contentAlignment = Alignment.Center,
        ) {
            Icon(imageVector = icon, contentDescription = label, tint = tint, modifier = Modifier.size(28.dp))
        }
        Text(text = label, style = MaterialTheme.typography.labelMedium, color = MaterialTheme.colorScheme.onSurface)
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun FileViewerSheet(
    file: ConsultationFileAttachment,
    onDismiss: () -> Unit,
) {
    val uriHandler = LocalUriHandler.current
    val isImage = file.fileName.isImageFile() || file.contentType.startsWith("image/")
    val isPdf = file.fileName.endsWith(".pdf", ignoreCase = true) || file.contentType == "application/pdf"

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true),
        dragHandle = null,
        containerColor = if (isImage) Color.Black else MaterialTheme.colorScheme.surface,
        modifier = Modifier.fillMaxHeight(0.94f).statusBarsPadding(),
    ) {
        if (isImage) {
            Box(modifier = Modifier.fillMaxSize()) {
                AsyncImage(
                    model = file.url,
                    contentDescription = file.fileName,
                    contentScale = ContentScale.Fit,
                    modifier = Modifier.fillMaxSize(),
                )
                Row(
                    modifier = Modifier
                        .align(Alignment.TopCenter)
                        .fillMaxWidth()
                        .background(Color.Black.copy(alpha = 0.5f))
                        .padding(horizontal = 4.dp, vertical = 4.dp),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    IconButton(onClick = onDismiss) {
                        Icon(Icons.Filled.Close, contentDescription = "Close", tint = Color.White)
                    }
                    Text(
                        text = file.fileName,
                        style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Medium),
                        color = Color.White,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        modifier = Modifier.weight(1f),
                    )
                    IconButton(onClick = { uriHandler.openUri(file.url) }) {
                        Icon(Icons.Filled.Download, contentDescription = "Download", tint = Color.White)
                    }
                }
            }
        } else {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 24.dp, vertical = 16.dp),
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    IconButton(onClick = onDismiss) {
                        Icon(Icons.Filled.Close, contentDescription = "Close")
                    }
                    Text(
                        text = "Document",
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                        modifier = Modifier.weight(1f).padding(start = 4.dp),
                    )
                }

                Spacer(Modifier.height(32.dp))

                Column(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(12.dp),
                ) {
                    Box(
                        modifier = Modifier
                            .size(80.dp)
                            .clip(RoundedCornerShape(16.dp))
                            .background(if (isPdf) Color(0xFFFFEBEE) else MaterialTheme.colorScheme.surfaceVariant),
                        contentAlignment = Alignment.Center,
                    ) {
                        Icon(
                            imageVector = if (isPdf) Icons.Filled.PictureAsPdf else Icons.AutoMirrored.Filled.InsertDriveFile,
                            contentDescription = null,
                            tint = if (isPdf) Color(0xFFE53935) else Primary40,
                            modifier = Modifier.size(48.dp),
                        )
                    }

                    Text(
                        text = file.fileName,
                        style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.SemiBold),
                        textAlign = TextAlign.Center,
                    )

                    if (file.sizeBytes > 0) {
                        Text(
                            text = formatFileSize(file.sizeBytes),
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                        )
                    }

                    Spacer(Modifier.height(8.dp))

                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(ShapePill)
                            .background(Primary40)
                            .clickable(
                                indication = null,
                                interactionSource = remember { MutableInteractionSource() },
                            ) {
                                uriHandler.openUri(file.url)
                                onDismiss()
                            }
                            .padding(vertical = 14.dp),
                        contentAlignment = Alignment.Center,
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                        ) {
                            Icon(Icons.Filled.Download, contentDescription = null, tint = Color.White, modifier = Modifier.size(20.dp))
                            Text("Open / Download", style = MaterialTheme.typography.labelLarge, color = Color.White)
                        }
                    }
                }
            }
        }
    }
}

@Composable
internal fun PatientAvatar(name: String, picture: String? = null, size: Int, modifier: Modifier = Modifier) {
    val initials = name.split(" ").take(2).mapNotNull { it.firstOrNull()?.uppercase() }.joinToString("")
    Box(
        modifier = modifier.size(size.dp).clip(CircleShape).background(Secondary90),
        contentAlignment = Alignment.Center,
    ) {
        if (!picture.isNullOrBlank()) {
            AsyncImage(
                model = picture,
                contentDescription = name,
                contentScale = ContentScale.Crop,
                modifier = Modifier.fillMaxSize(),
            )
        } else {
            Text(
                text = initials.ifBlank { "P" },
                style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
                color = Secondary40,
            )
        }
    }
}

@Composable
private fun MessageBubble(
    message: ConsultationMessage,
    fromMe: Boolean,
    onFileClick: (ConsultationFileAttachment) -> Unit,
    modifier: Modifier = Modifier,
) {
    val isFile = message.messageType.uppercase() == "FILE"
    val isPrescription = message.messageType.uppercase() == "PRESCRIPTION"
    Box(
        modifier = modifier.fillMaxWidth().padding(vertical = 2.dp),
        contentAlignment = if (fromMe) Alignment.CenterEnd else Alignment.CenterStart,
    ) {
        Column(
            modifier = Modifier.fillMaxWidth(0.90f),
            horizontalAlignment = if (fromMe) Alignment.End else Alignment.Start,
        ) {
            if (!fromMe) {
                Text(
                    text = message.senderName,
                    style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.SemiBold),
                    color = Tertiary40,
                    modifier = Modifier.padding(start = 4.dp, bottom = 2.dp),
                )
            }
            when {
                isPrescription -> PrescriptionMessageCard(jsonMessage = message.message ?: "", time = formatTime(message.createdAt))
                isFile -> FileBubble(message = message, fromMe = fromMe, onFileClick = onFileClick)
                else -> TextBubble(text = message.message ?: "", fromMe = fromMe, time = formatTime(message.createdAt))
            }
            if (isFile) {
                Text(
                    text = formatTime(message.createdAt),
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.padding(top = 2.dp, start = 4.dp, end = 4.dp),
                )
            }
        }
    }
}

@Composable
private fun PrescriptionMessageCard(jsonMessage: String, time: String, modifier: Modifier = Modifier) {
    val record = remember(jsonMessage) {
        runCatching {
            Json { ignoreUnknownKeys = true }.decodeFromString<MedicalRecordData>(jsonMessage)
        }.getOrNull()
    }

    Box(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(topStart = 18.dp, topEnd = 18.dp, bottomStart = 4.dp, bottomEnd = 18.dp))
            .background(Tertiary90)
            .padding(16.dp),
    ) {
        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                Text("Prescription", style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold), color = Tertiary40)
            }
            if (record == null) {
                Text("Unable to display prescription", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
            } else {
                if (!record.diagnosis.isNullOrBlank()) {
                    Column {
                        Text("Diagnosis", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        Text(record.diagnosis, style = MaterialTheme.typography.bodySmall)
                    }
                }
                if (record.prescription.isNotEmpty()) {
                    Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
                        Text("Drugs", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        record.prescription.forEach { item ->
                            Text("• ${item.drug} — ${item.dosage}, ${item.frequency} for ${item.duration}", style = MaterialTheme.typography.bodySmall)
                        }
                    }
                }
                if (!record.summary.isNullOrBlank()) {
                    Column {
                        Text("Notes", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        Text(record.summary, style = MaterialTheme.typography.bodySmall)
                    }
                }
            }
            Text(time, style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant, modifier = Modifier.align(Alignment.End))
        }
    }
}

@Composable
private fun TextBubble(text: String, fromMe: Boolean, time: String) {
    val bubbleColor = if (fromMe) Primary40 else MaterialTheme.colorScheme.surfaceVariant
    val textColor = if (fromMe) Color.White else MaterialTheme.colorScheme.onSurface
    val timeColor = if (fromMe) Color.White.copy(alpha = 0.7f) else MaterialTheme.colorScheme.onSurfaceVariant

    Box(
        modifier = Modifier
            .widthIn(min = 80.dp, max = 280.dp)
            .clip(
                RoundedCornerShape(
                    topStart = 18.dp, topEnd = 18.dp,
                    bottomStart = if (fromMe) 18.dp else 4.dp,
                    bottomEnd = if (fromMe) 4.dp else 18.dp,
                )
            )
            .background(bubbleColor)
            .padding(horizontal = 12.dp, vertical = 8.dp),
    ) {
        Column {
            Text(text = text, style = MaterialTheme.typography.bodyMedium, color = textColor)
            Text(
                text = time,
                style = MaterialTheme.typography.labelSmall,
                color = timeColor,
                modifier = Modifier.align(Alignment.End).padding(top = 2.dp),
            )
        }
    }
}

@Composable
private fun FileBubble(
    message: ConsultationMessage,
    fromMe: Boolean,
    onFileClick: (ConsultationFileAttachment) -> Unit,
) {
    val file = message.files.firstOrNull() ?: return
    val fileName = file.fileName.ifBlank { message.message ?: "File" }
    val isImage = fileName.isImageFile() || file.contentType.startsWith("image/")
    val isPdf = fileName.endsWith(".pdf", ignoreCase = true) || file.contentType == "application/pdf"

    val shape = RoundedCornerShape(
        topStart = 18.dp, topEnd = 18.dp,
        bottomStart = if (fromMe) 18.dp else 4.dp,
        bottomEnd = if (fromMe) 4.dp else 18.dp,
    )

    Box(
        modifier = Modifier
            .widthIn(max = 260.dp)
            .clip(shape)
            .clickable(
                indication = null,
                interactionSource = remember { MutableInteractionSource() },
            ) { onFileClick(file) },
    ) {
        if (isImage) {
            Box {
                AsyncImage(
                    model = file.url,
                    contentDescription = fileName,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.size(240.dp, 200.dp),
                )
                Box(
                    modifier = Modifier
                        .align(Alignment.BottomEnd)
                        .padding(6.dp)
                        .clip(RoundedCornerShape(10.dp))
                        .background(Color.Black.copy(alpha = 0.45f))
                        .padding(horizontal = 6.dp, vertical = 2.dp),
                ) {
                    Text(
                        text = formatTime(message.createdAt),
                        style = MaterialTheme.typography.labelSmall,
                        color = Color.White,
                    )
                }
            }
        } else {
            Row(
                modifier = Modifier
                    .background(if (fromMe) Primary40 else MaterialTheme.colorScheme.surfaceVariant)
                    .padding(horizontal = 12.dp, vertical = 12.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(10.dp),
            ) {
                Box(
                    modifier = Modifier
                        .size(46.dp)
                        .clip(RoundedCornerShape(10.dp))
                        .background(if (fromMe) Color.White.copy(alpha = 0.15f) else if (isPdf) Color(0xFFFFEBEE) else MaterialTheme.colorScheme.surface),
                    contentAlignment = Alignment.Center,
                ) {
                    Icon(
                        imageVector = if (isPdf) Icons.Filled.PictureAsPdf else Icons.AutoMirrored.Filled.InsertDriveFile,
                        contentDescription = null,
                        tint = if (isPdf) Color(0xFFE53935) else if (fromMe) Color.White else Primary40,
                        modifier = Modifier.size(28.dp),
                    )
                }
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = fileName,
                        style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.SemiBold),
                        color = if (fromMe) Color.White else MaterialTheme.colorScheme.onSurface,
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis,
                    )
                    if (file.sizeBytes > 0) {
                        Text(
                            text = formatFileSize(file.sizeBytes),
                            style = MaterialTheme.typography.labelSmall,
                            color = if (fromMe) Color.White.copy(alpha = 0.7f) else MaterialTheme.colorScheme.onSurfaceVariant,
                        )
                    }
                }
                Icon(
                    imageVector = Icons.Filled.Download,
                    contentDescription = "View",
                    tint = if (fromMe) Color.White.copy(alpha = 0.8f) else Primary40,
                    modifier = Modifier.size(18.dp),
                )
            }
        }
    }
}

// WhatsApp-style optimistic bubble shown immediately while the file is uploading
@Composable
private fun PendingFileBubble(pending: PendingFile) {
    val isImage = pending.contentType.startsWith("image/") || pending.fileName.isImageFile()
    val isPdf = pending.fileName.endsWith(".pdf", ignoreCase = true) || pending.contentType == "application/pdf"
    val shape = RoundedCornerShape(topStart = 18.dp, topEnd = 18.dp, bottomStart = 18.dp, bottomEnd = 4.dp)

    Box(
        modifier = Modifier.fillMaxWidth().padding(vertical = 2.dp),
        contentAlignment = Alignment.CenterEnd,
    ) {
        if (isImage) {
            Box(Modifier.widthIn(max = 260.dp).clip(shape)) {
                AsyncImage(
                    model = pending.bytes,
                    contentDescription = pending.fileName,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.size(240.dp, 200.dp),
                )
                Box(
                    modifier = Modifier
                        .matchParentSize()
                        .background(Color.Black.copy(alpha = if (pending.failed) 0.6f else 0.35f)),
                    contentAlignment = Alignment.Center,
                ) {
                    if (pending.failed) {
                        Text("Failed", style = MaterialTheme.typography.labelMedium, color = Color.White)
                    } else {
                        CircularProgressIndicator(color = Color.White, modifier = Modifier.size(32.dp), strokeWidth = 3.dp)
                    }
                }
            }
        } else {
            Row(
                modifier = Modifier
                    .widthIn(max = 260.dp)
                    .clip(shape)
                    .background(if (pending.failed) MaterialTheme.colorScheme.errorContainer else Primary40)
                    .padding(horizontal = 12.dp, vertical = 12.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(10.dp),
            ) {
                Box(
                    modifier = Modifier
                        .size(46.dp)
                        .clip(RoundedCornerShape(10.dp))
                        .background(Color.White.copy(alpha = 0.15f)),
                    contentAlignment = Alignment.Center,
                ) {
                    Icon(
                        imageVector = if (isPdf) Icons.Filled.PictureAsPdf else Icons.AutoMirrored.Filled.InsertDriveFile,
                        contentDescription = null,
                        tint = if (pending.failed) MaterialTheme.colorScheme.onErrorContainer else Color.White,
                        modifier = Modifier.size(28.dp),
                    )
                }
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = pending.fileName,
                        style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.SemiBold),
                        color = if (pending.failed) MaterialTheme.colorScheme.onErrorContainer else Color.White,
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis,
                    )
                    Text(
                        text = if (pending.failed) "Failed to send" else "Sending…",
                        style = MaterialTheme.typography.labelSmall,
                        color = if (pending.failed)
                            MaterialTheme.colorScheme.onErrorContainer.copy(alpha = 0.7f)
                        else
                            Color.White.copy(alpha = 0.7f),
                    )
                }
                if (!pending.failed) {
                    CircularProgressIndicator(modifier = Modifier.size(18.dp), color = Color.White, strokeWidth = 2.dp)
                }
            }
        }
    }
}

@Composable
private fun MessageInput(
    value: String,
    onValueChange: (String) -> Unit,
    onSend: () -> Unit,
    onAttach: () -> Unit,
    isUploading: Boolean,
    enabled: Boolean,
    modifier: Modifier = Modifier,
) {
    Column(modifier = modifier) {
        HorizontalDivider(thickness = 0.5.dp, color = MaterialTheme.colorScheme.outlineVariant)

        AnimatedVisibility(
            visible = isUploading,
            enter = expandVertically(),
            exit = shrinkVertically(),
        ) {
            Column {
                LinearProgressIndicator(
                    modifier = Modifier.fillMaxWidth(),
                    color = Primary40,
                    trackColor = Primary40.copy(alpha = 0.2f),
                )
                Text(
                    text = "Uploading file…",
                    style = MaterialTheme.typography.labelSmall,
                    color = Primary40,
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 4.dp),
                )
            }
        }

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(MaterialTheme.colorScheme.surface)
                .padding(horizontal = 8.dp, vertical = 8.dp),
            verticalAlignment = Alignment.Bottom,
            horizontalArrangement = Arrangement.spacedBy(4.dp),
        ) {
            // Attach icon button
            if (isUploading) {
                Box(modifier = Modifier.size(44.dp), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator(modifier = Modifier.size(22.dp), color = Primary40, strokeWidth = 2.5.dp)
                }
            } else {
                Box(
                    modifier = Modifier
                        .size(44.dp)
                        .clip(CircleShape)
                        .clickable(
                            enabled = enabled,
                            indication = null,
                            interactionSource = remember { MutableInteractionSource() },
                            onClick = onAttach,
                        ),
                    contentAlignment = Alignment.Center,
                ) {
                    Icon(
                        imageVector = Icons.Filled.AttachFile,
                        contentDescription = "Attach",
                        tint = if (enabled) Primary40 else MaterialTheme.colorScheme.outlineVariant,
                        modifier = Modifier.size(24.dp),
                    )
                }
            }

            // Text input pill
            Box(
                modifier = Modifier
                    .weight(1f)
                    .clip(ShapePill)
                    .background(MaterialTheme.colorScheme.surfaceVariant)
                    .padding(horizontal = 16.dp, vertical = 12.dp),
            ) {
                if (value.isEmpty()) {
                    Text(
                        text = if (enabled) "Type a message…" else "Start a session to chat",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                }
                BasicTextField(
                    value = value,
                    onValueChange = onValueChange,
                    textStyle = MaterialTheme.typography.bodyMedium.copy(color = MaterialTheme.colorScheme.onSurface),
                    cursorBrush = SolidColor(Primary40),
                    modifier = Modifier.fillMaxWidth(),
                    maxLines = 5,
                    enabled = enabled,
                )
            }

            // Send button
            Box(
                modifier = Modifier
                    .size(44.dp)
                    .clip(CircleShape)
                    .background(if (value.isNotBlank() && enabled) Primary40 else MaterialTheme.colorScheme.surfaceVariant)
                    .clickable(
                        enabled = value.isNotBlank() && enabled,
                        indication = null,
                        interactionSource = remember { MutableInteractionSource() },
                        onClick = onSend,
                    ),
                contentAlignment = Alignment.Center,
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.Send,
                    contentDescription = "Send",
                    tint = if (value.isNotBlank() && enabled) Color.White else MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.size(22.dp),
                )
            }
        }
    }
}

private fun mimeFromName(name: String): String = when {
    name.endsWith(".pdf", ignoreCase = true) -> "application/pdf"
    name.endsWith(".jpg", ignoreCase = true) || name.endsWith(".jpeg", ignoreCase = true) -> "image/jpeg"
    name.endsWith(".png", ignoreCase = true) -> "image/png"
    name.endsWith(".webp", ignoreCase = true) -> "image/webp"
    name.endsWith(".docx", ignoreCase = true) -> "application/vnd.openxmlformats-officedocument.wordprocessingml.document"
    else -> "application/octet-stream"
}

private fun String.isImageFile() =
    endsWith(".jpg", ignoreCase = true) ||
    endsWith(".jpeg", ignoreCase = true) ||
    endsWith(".png", ignoreCase = true) ||
    endsWith(".webp", ignoreCase = true) ||
    endsWith(".gif", ignoreCase = true)

private fun formatTime(iso: String): String = try {
    val parts = iso.split("T").lastOrNull()?.split(":") ?: return iso
    val hour = parts[0].toIntOrNull() ?: return iso
    val minute = parts.getOrNull(1) ?: "00"
    val ampm = if (hour < 12) "AM" else "PM"
    val h = if (hour % 12 == 0) 12 else hour % 12
    "$h:$minute $ampm"
} catch (_: Exception) { iso }

private fun formatFileSize(bytes: Long): String = when {
    bytes >= 1_048_576 -> { val t = bytes * 10L / 1_048_576L; "${t / 10}.${t % 10} MB" }
    bytes >= 1024 -> "${bytes / 1024} KB"
    else -> "$bytes B"
}

@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
private fun PatientChatSheet(
    patientName: String,
    patientPicture: String?,
    bio: PatientBio?,
    history: List<MedicalRecord>,
    onDismiss: () -> Unit,
) {
    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true),
        containerColor = MaterialTheme.colorScheme.background,
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 20.dp)
                .windowInsetsPadding(WindowInsets.navigationBars)
                .padding(bottom = 24.dp),
            verticalArrangement = Arrangement.spacedBy(20.dp),
        ) {
            // Patient header
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier.size(48.dp).clip(CircleShape).background(Secondary90),
                    contentAlignment = Alignment.Center,
                ) {
                    if (patientPicture != null) {
                        AsyncImage(model = patientPicture, contentDescription = patientName, contentScale = ContentScale.Crop, modifier = Modifier.fillMaxSize().clip(CircleShape))
                    } else {
                        Icon(imageVector = Icons.Filled.Person, contentDescription = null, tint = Secondary40, modifier = Modifier.size(28.dp))
                    }
                }
                Spacer(Modifier.width(12.dp))
                Column {
                    Text(patientName, style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.SemiBold))
                    Text("Patient Overview", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
            }

            HorizontalDivider(thickness = 0.5.dp, color = MaterialTheme.colorScheme.outlineVariant)

            // Bio section
            if (bio != null) {
                Text("PROFILE", style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold), color = MaterialTheme.colorScheme.onSurfaceVariant)

                Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                    ChatBioCard("Weight", if (bio.weight != null) "${bio.weight} ${bio.weightIn ?: ""}".trim() else "—", Modifier.weight(1f))
                    ChatBioCard("Height", if (bio.height != null) "${bio.height} ${bio.heightIn ?: ""}".trim() else "—", Modifier.weight(1f))
                    ChatBioCard("Blood", bio.bloodGroup ?: "—", Modifier.weight(1f))
                }

                if (bio.allergies.isNotEmpty()) {
                    Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                        Text("Allergies", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        FlowRow(horizontalArrangement = Arrangement.spacedBy(6.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                            bio.allergies.forEach { allergy ->
                                Box(
                                    modifier = Modifier.background(MaterialTheme.colorScheme.errorContainer, ShapePill).padding(horizontal = 10.dp, vertical = 4.dp),
                                ) { Text(allergy, style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.error) }
                            }
                        }
                    }
                }

                if (bio.chronicConditions.isNotEmpty()) {
                    Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                        Text("Chronic Conditions", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                            bio.chronicConditions.forEach { condition ->
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .clip(ShapeCard)
                                        .background(Primary90)
                                        .padding(horizontal = 14.dp, vertical = 12.dp),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                                ) {
                                    Box(modifier = Modifier.size(8.dp).clip(CircleShape).background(Primary40))
                                    Text(condition, style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Medium), color = MaterialTheme.colorScheme.onSurface)
                                }
                            }
                        }
                    }
                }

                if (bio.currentMedications.isNotEmpty()) {
                    Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                        Text("Current Medications", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                            bio.currentMedications.forEach { med ->
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .clip(ShapeCard)
                                        .background(Neutral90)
                                        .padding(horizontal = 14.dp, vertical = 12.dp),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                                ) {
                                    Box(modifier = Modifier.size(8.dp).clip(CircleShape).background(Tertiary40))
                                    Text(med, style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Medium), color = MaterialTheme.colorScheme.onSurface)
                                }
                            }
                        }
                    }
                }
            } else {
                Text("No patient bio on record", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
            }

            // Medical history section
            if (history.isNotEmpty()) {
                HorizontalDivider(thickness = 0.5.dp, color = MaterialTheme.colorScheme.outlineVariant)
                Text("MEDICAL HISTORY", style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold), color = MaterialTheme.colorScheme.onSurfaceVariant)

                history.forEach { record ->
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = ShapeCard,
                        colors = CardDefaults.cardColors(containerColor = Neutral90),
                        elevation = CardDefaults.cardElevation(0.dp),
                    ) {
                        Column(modifier = Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                            Text(
                                text = record.createdAt.take(10),
                                style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                            )
                            if (!record.diagnosis.isNullOrBlank()) {
                                Text(
                                    text = record.diagnosis,
                                    style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.SemiBold),
                                )
                            }
                            if (record.prescription.isNotEmpty()) {
                                HorizontalDivider(thickness = 0.5.dp, color = MaterialTheme.colorScheme.outlineVariant)
                                Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                                    Text(
                                        text = "Prescription",
                                        style = MaterialTheme.typography.labelSmall,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                                    )
                                    record.prescription.forEach { item ->
                                        Row(
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .clip(RoundedCornerShape(8.dp))
                                                .background(MaterialTheme.colorScheme.surface)
                                                .padding(horizontal = 10.dp, vertical = 8.dp),
                                            horizontalArrangement = Arrangement.spacedBy(10.dp),
                                            verticalAlignment = Alignment.Top,
                                        ) {
                                            Box(modifier = Modifier.padding(top = 5.dp).size(6.dp).clip(CircleShape).background(Primary40))
                                            Column(verticalArrangement = Arrangement.spacedBy(1.dp)) {
                                                Text(
                                                    text = item.drug,
                                                    style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.SemiBold),
                                                    color = MaterialTheme.colorScheme.onSurface,
                                                )
                                                Text(
                                                    text = "${item.dosage}  ·  ${item.frequency}  ·  ${item.duration}",
                                                    style = MaterialTheme.typography.labelSmall,
                                                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                                                )
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun ChatBioCard(label: String, value: String, modifier: Modifier = Modifier) {
    Card(
        modifier = modifier,
        shape = ShapeCard,
        colors = CardDefaults.cardColors(containerColor = Neutral90),
        elevation = CardDefaults.cardElevation(0.dp),
    ) {
        Column(
            modifier = Modifier.padding(10.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(2.dp),
        ) {
            Text(value, style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold))
            Text(label, style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
        }
    }
}
