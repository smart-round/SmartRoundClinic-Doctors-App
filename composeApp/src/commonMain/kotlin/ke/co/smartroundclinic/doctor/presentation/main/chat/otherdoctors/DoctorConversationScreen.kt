package ke.co.smartroundclinic.doctor.presentation.main.chat.otherdoctors

import ke.co.smartroundclinic.doctor.presentation.main.chat.util.isPdf
import ke.co.smartroundclinic.doctor.presentation.main.chat.util.attachmentLabel
import ke.co.smartroundclinic.doctor.presentation.main.chat.util.attachmentKind
import ke.co.smartroundclinic.doctor.presentation.main.chat.util.AttachmentKind
import ke.co.smartroundclinic.doctor.presentation.main.chat.ui.PdfViewer
import ke.co.smartroundclinic.doctor.presentation.main.chat.ui.UploadProgressRing
import ke.co.smartroundclinic.doctor.presentation.main.chat.ui.attachmentIcon
import ke.co.smartroundclinic.doctor.presentation.main.chat.ui.formatBytes
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.animation.shrinkVertically
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
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.ime
import androidx.compose.foundation.layout.navigationBars
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.InsertDriveFile
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.filled.AttachFile
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.Circle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Download
import androidx.compose.material.icons.filled.Folder
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Photo
import androidx.compose.material.icons.filled.PictureAsPdf
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
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Fullscreen
import chaintech.videoplayer.model.VideoPlayerConfig
import chaintech.videoplayer.ui.preview.VideoPreviewComposable
import chaintech.videoplayer.host.MediaPlayerHost
import chaintech.videoplayer.ui.video.VideoPlayerComposable
import coil3.compose.AsyncImage
import io.github.vinceglb.filekit.dialogs.FileKitMode
import io.github.vinceglb.filekit.dialogs.FileKitType
import io.github.vinceglb.filekit.dialogs.compose.rememberCameraPickerLauncher
import io.github.vinceglb.filekit.dialogs.compose.rememberFilePickerLauncher
import io.github.vinceglb.filekit.PlatformFile
import io.github.vinceglb.filekit.size
import io.github.vinceglb.filekit.source
import io.github.vinceglb.filekit.name
import io.github.vinceglb.filekit.readBytes
import ke.co.smartroundclinic.doctor.domain.model.DoctorChatFileAttachment
import ke.co.smartroundclinic.doctor.domain.model.DoctorChatMessage
import ke.co.smartroundclinic.doctor.presentation.main.chat.PendingFile
import ke.co.smartroundclinic.doctor.presentation.main.chat.ui.PatientAvatar
import ke.co.smartroundclinic.doctor.presentation.theme.Neutral90
import ke.co.smartroundclinic.doctor.presentation.theme.Primary40
import ke.co.smartroundclinic.doctor.presentation.theme.Secondary40
import ke.co.smartroundclinic.doctor.presentation.theme.Secondary90
import ke.co.smartroundclinic.doctor.presentation.theme.ShapePill
import ke.co.smartroundclinic.doctor.presentation.theme.Tertiary40
import kotlinx.coroutines.delay
import kotlinx.io.RawSource
import kotlinx.coroutines.withContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import ke.co.smartroundclinic.doctor.common.Constants
import kotlinx.coroutines.launch
import kotlinx.datetime.Clock
import kotlinx.datetime.DateTimeUnit
import kotlinx.datetime.Instant
import kotlinx.datetime.TimeZone
import kotlinx.datetime.minus
import kotlinx.datetime.toLocalDateTime

/**
 * Deliberate near-duplicate of [ke.co.smartroundclinic.doctor.presentation.main.chat.ui.ConversationScreen]
 * (message bubbles, date dividers, attach-menu sheet, file viewer, scroll-to-bottom, input bar,
 * typing indicator, and header presence label are all ported as-is) per the "exactly the same as
 * consultations" UI requirement. The only deliberate difference is that there's genuinely no
 * appointment concept here — calls are always available once connected — so only a single
 * video-call action is shown, same as ConversationScreen itself only wires up a video button
 * despite accepting an onVoiceCall param. Incoming calls are handled entirely outside this screen
 * now (native full-screen/CallKit ringing via IncomingCallHandler, same as patient calls) — no
 * in-app dialog here, matching ConversationScreen exactly.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun DoctorConversationScreen(
    counterpartName: String,
    counterpartPicture: String?,
    messages: List<DoctorChatMessage>,
    pendingFiles: List<PendingFile> = emptyList(),
    isLoadingHistory: Boolean = false,
    isLoadingMoreHistory: Boolean = false,
    hasMoreHistory: Boolean = false,
    onLoadMoreHistory: () -> Unit = {},
    isConnected: Boolean,
    isUploadingFile: Boolean,
    currentUserId: String,
    otherPartyTyping: Boolean = false,
    otherPartyOnline: Boolean = false,
    otherPartyLastSeenAt: String? = null,
    onTyping: (Boolean) -> Unit = {},
    onBack: () -> Unit,
    onViewProfile: () -> Unit = {},
    onVoiceCall: () -> Unit = {},
    onVideoCall: () -> Unit,
    onSendText: (String) -> Unit,
    onSendFile: (String, String, Long, ByteArray?, () -> RawSource) -> Unit,
    onFileTooLarge: (String, String, Long) -> Unit = { _, _, _ -> },
    onDismissPendingFile: (String) -> Unit = {},
    onSendFileFailed: (String, String) -> Unit = { _, _ -> },
    modifier: Modifier = Modifier,
) {
    var inputText by remember { mutableStateOf("") }
    val listState = rememberLazyListState()
    val scope = rememberCoroutineScope()
    var viewerFile by remember { mutableStateOf<DoctorChatFileAttachment?>(null) }
    var showAttachMenu by remember { mutableStateOf(false) }
    var showInfoSheet by remember { mutableStateOf(false) }

    val conversationItems = buildConversationItems(messages)
    val totalItems = conversationItems.size + pendingFiles.size + (if (otherPartyTyping) 1 else 0)

    val lastMessageId = messages.lastOrNull()?.id
    LaunchedEffect(lastMessageId, pendingFiles.size, otherPartyTyping) {
        if (totalItems > 0) listState.animateScrollToItem(totalItems - 1)
    }

    LaunchedEffect(listState, hasMoreHistory, isLoadingMoreHistory) {
        snapshotFlow { listState.firstVisibleItemIndex }.collect { index ->
            if (hasMoreHistory && !isLoadingMoreHistory && index <= 5 && listState.canScrollForward) {
                onLoadMoreHistory()
            }
        }
    }

    // Fires on every keystroke (LaunchedEffect restarts each time inputText changes, cancelling
    // the previous delay); stop is sent 1s after the last keystroke, or immediately once cleared
    // (send / manual clear).
    LaunchedEffect(inputText) {
        if (inputText.isNotBlank()) {
            onTyping(true)
            delay(1_000L)
            onTyping(false)
        } else {
            onTyping(false)
        }
    }

    /**
     * Reads a picked file and hands it to the ViewModel. The read happens on [Dispatchers.IO],
     * never on the composition's Main-dispatched scope, and the size is checked first so an
     * oversized file is never read at all.
     */
    fun sendPickedFile(file: PlatformFile, fallbackName: String) {
        scope.launch {
            val name = file.name.ifBlank { fallbackName }
            val mime = mimeFromName(name)
            val size = withContext(Dispatchers.IO) { runCatching { file.size() }.getOrNull() }
            if (size == null) {
                onSendFileFailed(name, mime)
                return@launch
            }
            if (size > Constants.MAX_CHAT_FILE_BYTES) {
                onFileTooLarge(name, mime, size)
                return@launch
            }
            val preview = if (mime.startsWith("image/") && size <= Constants.MAX_INLINE_PREVIEW_BYTES) {
                withContext(Dispatchers.IO) { runCatching { file.readBytes() }.getOrNull() }
            } else {
                null
            }
            onSendFile(name, mime, size, preview) { file.source() }
        }
    }

    val cameraLauncher = rememberCameraPickerLauncher { file ->
        file?.let {
            val name = it.name.takeIf { n -> n.isNotBlank() && n.contains('.') } ?: "photo.jpg"
            sendPickedFile(it, name)
        }
    }
    val galleryLauncher = rememberFilePickerLauncher(mode = FileKitMode.Single, type = FileKitType.ImageAndVideo) { file ->
        file?.let { sendPickedFile(it, "media") }
    }
    val fileLauncher = rememberFilePickerLauncher(mode = FileKitMode.Single, type = FileKitType.File()) { file ->
        file?.let { sendPickedFile(it, "file") }
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
                        ) { showInfoSheet = true },
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        PatientAvatar(name = counterpartName, picture = counterpartPicture, size = 36)
                        Spacer(Modifier.width(10.dp))
                        Column {
                            Text(
                                text = "Dr. $counterpartName",
                                style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis,
                            )
                            if (isConnected) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(4.dp),
                                ) {
                                    val presenceLabel = when {
                                        otherPartyTyping -> "typing…"
                                        otherPartyOnline -> "Online"
                                        otherPartyLastSeenAt != null -> "Last seen ${formatLastSeen(otherPartyLastSeenAt)}"
                                        else -> "Offline"
                                    }
                                    val presenceColor = if (otherPartyOnline || otherPartyTyping) Tertiary40 else MaterialTheme.colorScheme.onSurfaceVariant
                                    Icon(
                                        imageVector = Icons.Filled.Circle,
                                        contentDescription = null,
                                        tint = if (otherPartyOnline) Tertiary40 else MaterialTheme.colorScheme.onSurfaceVariant,
                                        modifier = Modifier.size(7.dp),
                                    )
                                    Text(
                                        text = presenceLabel,
                                        style = MaterialTheme.typography.labelSmall,
                                        color = presenceColor,
                                        maxLines = 1,
                                        overflow = TextOverflow.Ellipsis,
                                    )
                                }
                            }
                        }
                    }
                },
                actions = {
                    if (isConnected) {
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
                .padding(top = paddingValues.calculateTopPadding()),
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
                            text = "Say hello to start the conversation!",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                        )
                    }
                }
                else -> {
                    Box(modifier = Modifier.weight(1f)) {
                        LazyColumn(
                            state = listState,
                            modifier = Modifier.fillMaxSize(),
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
                                        is ConversationItem.DateDivider -> "divider_${item.dateKey}"
                                        is ConversationItem.MessageItem -> item.message.id
                                    }
                                },
                            ) { item ->
                                when (item) {
                                    is ConversationItem.DateDivider -> DateDividerRow(label = item.label)
                                    is ConversationItem.MessageItem -> {
                                        val fromMe = item.message.senderId == currentUserId
                                        MessageBubble(
                                            message = item.message,
                                            fromMe = fromMe,
                                            onFileClick = { viewerFile = it },
                                        )
                                    }
                                }
                            }
                            items(pendingFiles, key = { "p_${it.localId}" }) { pending ->
                                PendingFileBubble(pending = pending, onDismiss = { onDismissPendingFile(pending.localId) })
                            }
                            if (otherPartyTyping) {
                                item(key = "typing_indicator") { TypingIndicatorBubble() }
                            }
                        }
                        ScrollToBottomButton(
                            visible = listState.canScrollForward,
                            onClick = { scope.launch { if (totalItems > 0) listState.animateScrollToItem(totalItems - 1) } },
                            modifier = Modifier.align(Alignment.BottomEnd).padding(16.dp),
                        )
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
                enabled = isConnected,
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

    if (showInfoSheet) {
        DoctorInfoSheet(
            counterpartName = counterpartName,
            counterpartPicture = counterpartPicture,
            onDismiss = { showInfoSheet = false },
            onViewProfile = {
                showInfoSheet = false
                onViewProfile()
            },
        )
    }
}

private sealed class ConversationItem {
    data class DateDivider(val label: String, val dateKey: String) : ConversationItem()
    data class MessageItem(val message: DoctorChatMessage) : ConversationItem()
}

// This is a permanent, ongoing conversation (not scoped to any one visit) — insert a WhatsApp-style
// day divider whenever the calendar date changes.
private fun buildConversationItems(messages: List<DoctorChatMessage>): List<ConversationItem> {
    val items = mutableListOf<ConversationItem>()
    var lastDateKey: String? = null
    for (message in messages) {
        val dateKey = dateKeyOf(message.createdAt)
        if (dateKey != lastDateKey) {
            items += ConversationItem.DateDivider(dateDividerLabel(message.createdAt), dateKey)
            lastDateKey = dateKey
        }
        items += ConversationItem.MessageItem(message)
    }
    return items
}

private fun dateKeyOf(iso: String): String = try {
    Instant.parse(iso).toLocalDateTime(TimeZone.currentSystemDefault()).date.toString()
} catch (_: Exception) { "unknown" }

private fun dateDividerLabel(iso: String): String = try {
    val zone = TimeZone.currentSystemDefault()
    val date = Instant.parse(iso).toLocalDateTime(zone).date
    val today = Clock.System.now().toLocalDateTime(zone).date
    val yesterday = today.minus(1, DateTimeUnit.DAY)
    val month = date.month.name.take(3).lowercase().replaceFirstChar { it.uppercase() }
    when (date) {
        today -> "Today"
        yesterday -> "Yesterday"
        else -> "$month ${date.dayOfMonth}, ${date.year}"
    }
} catch (_: Exception) { "" }

@Composable
private fun DateDividerRow(label: String, modifier: Modifier = Modifier) {
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

@Composable
private fun TypingIndicatorBubble(modifier: Modifier = Modifier) {
    Box(
        modifier = modifier.fillMaxWidth().padding(vertical = 2.dp),
        contentAlignment = Alignment.CenterStart,
    ) {
        Box(
            modifier = Modifier
                .clip(RoundedCornerShape(topStart = 18.dp, topEnd = 18.dp, bottomStart = 4.dp, bottomEnd = 18.dp))
                .background(MaterialTheme.colorScheme.surfaceVariant)
                .padding(horizontal = 16.dp, vertical = 14.dp),
        ) {
            TypingDots()
        }
    }
}

@Composable
private fun TypingDots(modifier: Modifier = Modifier) {
    val transition = rememberInfiniteTransition(label = "typing")
    Row(modifier = modifier, horizontalArrangement = Arrangement.spacedBy(4.dp)) {
        repeat(3) { index ->
            val alpha by transition.animateFloat(
                initialValue = 0.3f,
                targetValue = 1f,
                animationSpec = infiniteRepeatable(
                    animation = tween(durationMillis = 600, delayMillis = index * 150),
                    repeatMode = RepeatMode.Reverse,
                ),
                label = "dot$index",
            )
            Box(modifier = Modifier.size(7.dp).clip(CircleShape).background(MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = alpha)))
        }
    }
}

@Composable
private fun ScrollToBottomButton(visible: Boolean, onClick: () -> Unit, modifier: Modifier = Modifier) {
    AnimatedVisibility(
        visible = visible,
        modifier = modifier,
        enter = fadeIn() + scaleIn(),
        exit = fadeOut() + scaleOut(),
    ) {
        Box(
            modifier = Modifier
                .size(40.dp)
                .clip(CircleShape)
                .background(MaterialTheme.colorScheme.surface.copy(alpha = 0.85f))
                .clickable(
                    indication = null,
                    interactionSource = remember { MutableInteractionSource() },
                    onClick = onClick,
                ),
            contentAlignment = Alignment.Center,
        ) {
            Icon(
                imageVector = Icons.Filled.KeyboardArrowDown,
                contentDescription = "Scroll to bottom",
                tint = Primary40,
            )
        }
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
    file: DoctorChatFileAttachment,
    onDismiss: () -> Unit,
) {
    val uriHandler = LocalUriHandler.current
    val fileName = file.fileName
    val kind = attachmentKind(fileName, file.contentType)
    val isImage = kind == AttachmentKind.PHOTO
    val isVideo = kind == AttachmentKind.VIDEO
    val label = attachmentLabel(fileName, file.contentType)
    val isPdfFile = isPdf(fileName, file.contentType)

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true),
        dragHandle = null,
        containerColor = if (isImage || isVideo || isPdfFile) Color.Black else MaterialTheme.colorScheme.surface,
        // Media fills the screen edge to edge; documents keep the inset sheet.
        modifier = if (isImage || isVideo || isPdfFile) Modifier.fillMaxSize() else Modifier.fillMaxHeight(0.94f).statusBarsPadding(),
    ) {
        if (isVideo) {
            // Plays in-app, same as photos open in-app, rather than handing off to the OS.
            val playerHost = remember(file.url) { MediaPlayerHost(mediaUrl = file.url, autoPlay = true, isLooping = false) }
            Box(modifier = Modifier.fillMaxSize()) {
                VideoPlayerComposable(
                    modifier = Modifier.fillMaxSize(),
                    playerHost = playerHost,
                )
                Row(
                    modifier = Modifier
                        .align(Alignment.TopCenter)
                        .fillMaxWidth()
                        .background(Color.Black.copy(alpha = 0.5f))
                        .statusBarsPadding()
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
        } else if (isPdfFile) {
            Box(modifier = Modifier.fillMaxSize()) {
                PdfViewer(url = file.url, modifier = Modifier.fillMaxSize())
                Row(
                    modifier = Modifier
                        .align(Alignment.TopCenter)
                        .fillMaxWidth()
                        .background(Color.Black.copy(alpha = 0.5f))
                        .statusBarsPadding()
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
        } else if (isImage) {
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
                        .statusBarsPadding()
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
                            .background(if (isPdfFile) Color(0xFFFFEBEE) else MaterialTheme.colorScheme.surfaceVariant),
                        contentAlignment = Alignment.Center,
                    ) {
                        Icon(
                            imageVector = attachmentIcon(kind, fileName, file.contentType),
                            contentDescription = null,
                            tint = if (isPdfFile) Color(0xFFE53935) else Primary40,
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
private fun MessageBubble(
    message: DoctorChatMessage,
    fromMe: Boolean,
    onFileClick: (DoctorChatFileAttachment) -> Unit,
    modifier: Modifier = Modifier,
) {
    val isFile = message.messageType.uppercase() == "FILE"
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
                    text = "Dr. ${message.senderName}",
                    style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.SemiBold),
                    color = Tertiary40,
                    modifier = Modifier.padding(start = 4.dp, bottom = 2.dp),
                )
            }
            if (isFile) {
                FileBubble(message = message, fromMe = fromMe, onFileClick = onFileClick)
                Text(
                    text = formatTime(message.createdAt),
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.padding(top = 2.dp, start = 4.dp, end = 4.dp),
                )
            } else {
                TextBubble(text = message.message ?: "", fromMe = fromMe, time = formatTime(message.createdAt))
            }
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
            Text(text = text, style = MaterialTheme.typography.bodyMedium.copy(fontFamily = FontFamily.Default), color = textColor)
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
    message: DoctorChatMessage,
    fromMe: Boolean,
    onFileClick: (DoctorChatFileAttachment) -> Unit,
) {
    val file = message.files.firstOrNull() ?: return
    val fileName = file.fileName.ifBlank { message.message ?: "File" }
    val kind = attachmentKind(fileName, file.contentType)
    val isImage = kind == AttachmentKind.PHOTO
    val label = attachmentLabel(fileName, file.contentType)
    val isPdfFile = isPdf(fileName, file.contentType)
    val isVideoBubble = kind == AttachmentKind.VIDEO

    val shape = RoundedCornerShape(
        topStart = 18.dp, topEnd = 18.dp,
        bottomStart = if (fromMe) 18.dp else 4.dp,
        bottomEnd = if (fromMe) 4.dp else 18.dp,
    )

    Box(
        modifier = Modifier
            .widthIn(max = 260.dp)
            .clip(shape)
            .then(
                // Video plays in place, so it owns its own gestures rather than the whole
                // bubble opening the full-screen viewer on any tap.
                if (isVideoBubble) Modifier else Modifier.clickable(
                    indication = null,
                    interactionSource = remember { MutableInteractionSource() },
                ) { onFileClick(file) },
            ),
    ) {
        if (isVideoBubble) {
            InlineVideoBubble(
                url = file.url,
                timestamp = formatTime(message.createdAt),
                onExpand = { onFileClick(file) },
            )
        } else if (isImage) {
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
                        .background(if (fromMe) Color.White.copy(alpha = 0.15f) else if (isPdfFile) Color(0xFFFFEBEE) else MaterialTheme.colorScheme.surface),
                    contentAlignment = Alignment.Center,
                ) {
                    Icon(
                        imageVector = attachmentIcon(kind, fileName, file.contentType),
                        contentDescription = null,
                        tint = if (isPdfFile) Color(0xFFE53935) else if (fromMe) Color.White else Primary40,
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

@Composable
private fun PendingFileBubble(pending: PendingFile, onDismiss: () -> Unit = {}) {
    val kind = attachmentKind(pending.fileName, pending.contentType)
    // Only show the inline preview when we actually kept the bytes (small images).
    val isImage = kind == AttachmentKind.PHOTO && pending.previewBytes != null
    val label = attachmentLabel(pending.fileName, pending.contentType)
    val shape = RoundedCornerShape(topStart = 18.dp, topEnd = 18.dp, bottomStart = 18.dp, bottomEnd = 4.dp)

    Box(
        modifier = Modifier.fillMaxWidth().padding(vertical = 2.dp),
        contentAlignment = Alignment.CenterEnd,
    ) {
        if (isImage) {
            Box(Modifier.widthIn(max = 260.dp).clip(shape)) {
                AsyncImage(
                    model = pending.previewBytes,
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
                        UploadProgressRing(progress = pending.progress)
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
                        imageVector = attachmentIcon(kind, pending.fileName, pending.contentType),
                        contentDescription = null,
                        tint = if (pending.failed) MaterialTheme.colorScheme.onErrorContainer else Color.White,
                        modifier = Modifier.size(28.dp),
                    )
                }
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = label,
                        style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.SemiBold),
                        color = if (pending.failed) MaterialTheme.colorScheme.onErrorContainer else Color.White,
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis,
                    )
                    Text(
                        text = when {
                            pending.errorText != null -> pending.errorText
                            pending.failed -> "Failed to send"
                            else -> formatBytes(pending.totalBytes)
                        },
                        style = MaterialTheme.typography.labelSmall,
                        color = if (pending.failed)
                            MaterialTheme.colorScheme.onErrorContainer.copy(alpha = 0.7f)
                        else
                            Color.White.copy(alpha = 0.7f),
                    )
                    pending.detailText?.let { detail ->
                        Text(
                            text = detail,
                            style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.SemiBold),
                            color = MaterialTheme.colorScheme.onErrorContainer,
                        )
                    }
                }
                if (pending.failed) {
                    IconButton(onClick = onDismiss, modifier = Modifier.size(28.dp)) {
                        Icon(
                            imageVector = Icons.Filled.Close,
                            contentDescription = "Dismiss",
                            tint = MaterialTheme.colorScheme.onErrorContainer,
                            modifier = Modifier.size(18.dp),
                        )
                    }
                }
                if (!pending.failed) {
                    UploadProgressRing(progress = pending.progress)
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

            Box(
                modifier = Modifier
                    .weight(1f)
                    .clip(ShapePill)
                    .background(MaterialTheme.colorScheme.surfaceVariant)
                    .padding(horizontal = 16.dp, vertical = 12.dp),
            ) {
                if (value.isEmpty()) {
                    Text(
                        text = if (enabled) "Type a message…" else "Connecting…",
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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun DoctorInfoSheet(
    counterpartName: String,
    counterpartPicture: String?,
    onDismiss: () -> Unit,
    onViewProfile: () -> Unit,
) {
    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true),
        containerColor = MaterialTheme.colorScheme.background,
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp)
                .windowInsetsPadding(WindowInsets.navigationBars)
                .padding(bottom = 24.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable(
                        indication = null,
                        interactionSource = remember { MutableInteractionSource() },
                        onClick = onViewProfile,
                    ),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Box(
                    modifier = Modifier.size(48.dp).clip(CircleShape).background(Secondary90),
                    contentAlignment = Alignment.Center,
                ) {
                    if (counterpartPicture != null) {
                        AsyncImage(model = counterpartPicture, contentDescription = counterpartName, contentScale = ContentScale.Crop, modifier = Modifier.fillMaxSize().clip(CircleShape))
                    } else {
                        Icon(imageVector = Icons.Filled.Person, contentDescription = null, tint = Secondary40, modifier = Modifier.size(28.dp))
                    }
                }
                Spacer(Modifier.width(12.dp))
                Column(modifier = Modifier.weight(1f)) {
                    Text("Dr. $counterpartName", style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.SemiBold))
                    Text("Verified doctor on SmartRound Clinic", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
                Icon(imageVector = Icons.AutoMirrored.Filled.KeyboardArrowRight, contentDescription = "View profile", tint = MaterialTheme.colorScheme.onSurfaceVariant)
            }
            HorizontalDivider(thickness = 0.5.dp, color = MaterialTheme.colorScheme.outlineVariant)
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = MaterialTheme.shapes.medium,
                colors = CardDefaults.cardColors(containerColor = Neutral90),
                elevation = CardDefaults.cardElevation(0.dp),
            ) {
                Text(
                    text = "This is a private conversation between you and Dr. $counterpartName.",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.padding(14.dp),
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
    val dateTime = Instant.parse(iso).toLocalDateTime(TimeZone.currentSystemDefault())
    val hour = dateTime.hour
    val ampm = if (hour < 12) "AM" else "PM"
    val h = if (hour % 12 == 0) 12 else hour % 12
    "$h:${dateTime.minute.toString().padStart(2, '0')} $ampm"
} catch (_: Exception) { iso }

private fun formatLastSeen(iso: String): String = try {
    val zone = TimeZone.currentSystemDefault()
    val dateTime = Instant.parse(iso).toLocalDateTime(zone)
    val today = Clock.System.now().toLocalDateTime(zone).date
    val hour = dateTime.hour
    val ampm = if (hour < 12) "AM" else "PM"
    val h = if (hour % 12 == 0) 12 else hour % 12
    val time = "$h:${dateTime.minute.toString().padStart(2, '0')} $ampm"
    if (dateTime.date == today) "today at $time"
    else "${dateTime.date.month.name.take(3).lowercase().replaceFirstChar { it.uppercase() }} ${dateTime.date.dayOfMonth} at $time"
} catch (_: Exception) { "recently" }

private fun formatFileSize(bytes: Long): String = when {
    bytes >= 1_048_576 -> { val t = bytes * 10L / 1_048_576L; "${t / 10}.${t % 10} MB" }
    bytes >= 1024 -> "${bytes / 1024} KB"
    else -> "$bytes B"
}

/**
 * Video that plays inside the chat bubble, WhatsApp-style, and only goes full screen if the
 * user asks for it.
 *
 * The player is created lazily on first play: a thread can hold many videos, and instantiating
 * a platform player for each one as it scrolls into view would be far too heavy.
 */
@Composable
private fun InlineVideoBubble(
    url: String,
    timestamp: String,
    onExpand: () -> Unit,
) {
    var isPlaying by remember(url) { mutableStateOf(false) }

    Box(
        modifier = Modifier.size(240.dp, 200.dp).background(Color.Black),
        contentAlignment = Alignment.Center,
    ) {
        if (isPlaying) {
            val playerHost = remember(url) {
                MediaPlayerHost(mediaUrl = url, autoPlay = true, isLooping = false)
            }
            VideoPlayerComposable(
                modifier = Modifier.fillMaxSize(),
                playerHost = playerHost,
                playerConfig = VideoPlayerConfig(
                    isFastForwardBackwardEnabled = false,
                    isScreenResizeEnabled = false,
                    isSpeedControlEnabled = false,
                    isFullScreenEnabled = false,
                ),
            )
        } else {
            // A real frame from the video, decoded on-device, rather than a generic icon.
            VideoPreviewComposable(
                url = url,
                frameCount = 1,
                contentScale = ContentScale.Crop,
                loadingIndicatorColor = Color.White,
            )
            // Scrim so the play button stays legible over a bright frame.
            Box(Modifier.fillMaxSize().background(Color.Black.copy(alpha = 0.2f)))
            Box(
                modifier = Modifier
                    .size(56.dp)
                    .clip(CircleShape)
                    .background(Color.Black.copy(alpha = 0.45f))
                    .clickable(
                        indication = null,
                        interactionSource = remember { MutableInteractionSource() },
                    ) { isPlaying = true },
                contentAlignment = Alignment.Center,
            ) {
                Icon(
                    imageVector = Icons.Filled.PlayArrow,
                    contentDescription = "Play video",
                    tint = Color.White,
                    modifier = Modifier.size(34.dp),
                )
            }
        }

        // Expand to the full-screen viewer, which is where scrubbing and download live.
        IconButton(
            onClick = onExpand,
            modifier = Modifier.align(Alignment.TopEnd).padding(4.dp),
        ) {
            Box(
                modifier = Modifier.size(28.dp).clip(CircleShape).background(Color.Black.copy(alpha = 0.45f)),
                contentAlignment = Alignment.Center,
            ) {
                Icon(
                    imageVector = Icons.Filled.Fullscreen,
                    contentDescription = "Full screen",
                    tint = Color.White,
                    modifier = Modifier.size(18.dp),
                )
            }
        }

        if (!isPlaying) {
            Box(
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .padding(6.dp)
                    .clip(RoundedCornerShape(10.dp))
                    .background(Color.Black.copy(alpha = 0.45f))
                    .padding(horizontal = 6.dp, vertical = 2.dp),
            ) {
                Text(text = timestamp, style = MaterialTheme.typography.labelSmall, color = Color.White)
            }
        }
    }
}
