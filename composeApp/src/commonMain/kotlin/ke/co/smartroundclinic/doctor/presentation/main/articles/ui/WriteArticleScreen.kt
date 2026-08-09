package ke.co.smartroundclinic.doctor.presentation.main.articles.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.CreateNewFolder
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuAnchorType
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.MenuDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil3.compose.AsyncImage
import io.github.vinceglb.filekit.dialogs.FileKitType
import io.github.vinceglb.filekit.dialogs.compose.rememberCameraPickerLauncher
import io.github.vinceglb.filekit.dialogs.compose.rememberFilePickerLauncher
import io.github.vinceglb.filekit.readBytes
import ke.co.smartroundclinic.doctor.core.media.PhotoPickerBottomSheet
import ke.co.smartroundclinic.doctor.domain.model.Article
import ke.co.smartroundclinic.doctor.domain.model.ArticleCategory
import ke.co.smartroundclinic.doctor.presentation.common.composables.PrimaryButton
import ke.co.smartroundclinic.doctor.presentation.main.articles.htmlToPlainText
import ke.co.smartroundclinic.doctor.presentation.main.articles.plainTextToHtml
import ke.co.smartroundclinic.doctor.presentation.theme.Neutral20
import ke.co.smartroundclinic.doctor.presentation.theme.Neutral60
import ke.co.smartroundclinic.doctor.presentation.theme.Primary40
import kotlinx.coroutines.launch

private val FieldShape = RoundedCornerShape(6.dp)

/** Half the label's line box — the border is inset by this so the label rides its centre line. */
private val LabelOverlap = 8.dp

/** Single-line fields (Category, Title) — taller than the text alone needs, for an easier target. */
private val FieldHeight = 72.dp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun WriteArticleScreen(
    article: Article?,
    categories: List<ArticleCategory>,
    thumbnailBytes: ByteArray?,
    isSaving: Boolean,
    onBack: () -> Unit,
    onThumbnailPicked: (bytes: ByteArray, filename: String) -> Unit,
    onRefreshCategories: () -> Unit,
    onPublish: (title: String, content: String, categoryId: String) -> Unit,
    onNotificationsClick: () -> Unit = {},
    onSearchClick: () -> Unit = {},
    modifier: Modifier = Modifier,
) {
    var title by remember { mutableStateOf(article?.title ?: "") }
    var body by remember { mutableStateOf(article?.content?.let(::htmlToPlainText) ?: "") }
    var selectedCategoryId by remember { mutableStateOf(article?.categoryId ?: "") }
    var categoryExpanded by remember { mutableStateOf(false) }
    var showPhotoPicker by remember { mutableStateOf(false) }

    val photoPickerSheetState = rememberModalBottomSheetState()
    val scope = rememberCoroutineScope()

    val selectedCategoryName = categories.find { it.id == selectedCategoryId }?.name ?: ""
    val isFormValid = title.isNotBlank() && body.isNotBlank() && selectedCategoryId.isNotBlank()

    val galleryLauncher = rememberFilePickerLauncher(type = FileKitType.Image) { file ->
        scope.launch { file?.readBytes()?.let { bytes -> onThumbnailPicked(bytes, "thumbnail.jpg") } }
    }

    val cameraLauncher = rememberCameraPickerLauncher { file ->
        scope.launch { file?.readBytes()?.let { bytes -> onThumbnailPicked(bytes, "thumbnail.jpg") } }
    }

    LaunchedEffect(article) {
        if (article != null) {
            title = article.title
            body = htmlToPlainText(article.content)
            selectedCategoryId = article.categoryId
        }
    }

    if (showPhotoPicker) {
        PhotoPickerBottomSheet(
            sheetState = photoPickerSheetState,
            onDismiss = { showPhotoPicker = false },
            onTakePhoto = {
                scope.launch {
                    photoPickerSheetState.hide()
                    showPhotoPicker = false
                    cameraLauncher.launch()
                }
            },
            onChooseFromGallery = {
                scope.launch {
                    photoPickerSheetState.hide()
                    showPhotoPicker = false
                    galleryLauncher.launch()
                }
            },
        )
    }

    Scaffold(
        modifier = modifier,
        containerColor = Color.White,
        topBar = {
            ArticlesHeader(
                onBack = onBack,
                onNotificationsClick = onNotificationsClick,
                onSearchClick = onSearchClick,
            )
        },
        contentWindowInsets = WindowInsets(0),
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.White)
                .padding(paddingValues)
                .imePadding(),
        ) {
            Column(
                modifier = Modifier
                    .weight(1f)
                    .verticalScroll(rememberScrollState())
                    .padding(horizontal = 16.dp),
                verticalArrangement = Arrangement.spacedBy(18.dp),
            ) {
                Spacer(Modifier.height(4.dp))

                ImageUploadBox(
                    thumbnailBytes = thumbnailBytes,
                    existingThumbnailUrl = article?.thumbnailUrl,
                    onChooseFile = { showPhotoPicker = true },
                )

                ExposedDropdownMenuBox(
                    expanded = categoryExpanded,
                    onExpandedChange = { expanded ->
                        categoryExpanded = expanded
                        if (expanded) onRefreshCategories()
                    },
                ) {
                    NotchedField(
                        label = "Category",
                        contentAlignment = Alignment.CenterStart,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(FieldHeight)
                            .menuAnchor(ExposedDropdownMenuAnchorType.PrimaryNotEditable),
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = selectedCategoryName.ifBlank { "Select a category" },
                                style = fieldTextStyle(),
                                color = if (selectedCategoryName.isBlank()) Neutral60 else Neutral20,
                                modifier = Modifier.weight(1f),
                            )
                            Icon(
                                imageVector = Icons.Filled.KeyboardArrowDown,
                                contentDescription = null,
                                tint = Neutral60,
                                modifier = Modifier.size(22.dp),
                            )
                        }
                    }
                    ExposedDropdownMenu(
                        expanded = categoryExpanded,
                        onDismissRequest = { categoryExpanded = false },
                        containerColor = MaterialTheme.colorScheme.background,
                    ) {
                        categories.forEach { category ->
                            DropdownMenuItem(
                                colors = MenuDefaults.itemColors(textColor = MaterialTheme.colorScheme.onBackground),
                                text = { Text(category.name) },
                                onClick = { selectedCategoryId = category.id; categoryExpanded = false },
                                trailingIcon = if (selectedCategoryId == category.id) {
                                    {
                                        Icon(
                                            imageVector = Icons.Filled.Check,
                                            contentDescription = null,
                                            tint = Primary40,
                                            modifier = Modifier.size(18.dp),
                                        )
                                    }
                                } else null,
                            )
                        }
                    }
                }

                NotchedField(
                    label = "Title",
                    contentAlignment = Alignment.CenterStart,
                    modifier = Modifier.fillMaxWidth().height(FieldHeight),
                ) {
                    FieldTextInput(
                        value = title,
                        onValueChange = { title = it },
                        placeholder = "How Sunlight, and Covid-19 Interact In 2026",
                        singleLine = true,
                    )
                }

                NotchedField(
                    label = "Article",
                    modifier = Modifier.fillMaxWidth().height(207.dp),
                ) {
                    FieldTextInput(
                        value = body,
                        onValueChange = { body = it },
                        placeholder = "Write your article here…",
                        singleLine = false,
                        modifier = Modifier.fillMaxSize(),
                    )
                }

                Spacer(Modifier.height(4.dp))
            }

            PrimaryButton(
                onClick = { onPublish(title, plainTextToHtml(body), selectedCategoryId) },
                enabled = isFormValid && !isSaving,
                shape = RoundedCornerShape(14.5.dp),
                modifier = Modifier
                    .padding(horizontal = 16.dp, vertical = 12.dp)
                    .height(57.dp),
            ) {
                Text(
                    text = if (isSaving) "Saving…" else "Publish Article",
                    style = MaterialTheme.typography.labelLarge.copy(fontSize = 16.sp),
                    color = Color.White,
                )
            }
        }
    }
}

/**
 * Outlined container whose label always sits on the top border, centred on the stroke, regardless
 * of focus or content — Material's own floating label only rides the border once the field is
 * focused or filled.
 */
@Composable
private fun NotchedField(
    label: String,
    modifier: Modifier = Modifier,
    contentAlignment: Alignment = Alignment.TopStart,
    content: @Composable () -> Unit,
) {
    Box(modifier) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(top = LabelOverlap)
                .border(1.dp, Primary40, FieldShape)
                .padding(horizontal = 14.dp, vertical = 12.dp),
            contentAlignment = contentAlignment,
        ) {
            content()
        }

        Text(
            text = label,
            style = MaterialTheme.typography.bodyMedium.copy(fontSize = 13.sp, letterSpacing = 0.sp),
            color = Neutral20,
            maxLines = 1,
            modifier = Modifier
                .padding(start = 12.dp)
                .background(Color.White)
                .padding(horizontal = 4.dp),
        )
    }
}

@Composable
private fun FieldTextInput(
    value: String,
    onValueChange: (String) -> Unit,
    placeholder: String,
    singleLine: Boolean,
    modifier: Modifier = Modifier,
) {
    Box(modifier) {
        BasicTextField(
            value = value,
            onValueChange = onValueChange,
            singleLine = singleLine,
            textStyle = fieldTextStyle().copy(color = Neutral20),
            cursorBrush = SolidColor(Primary40),
            modifier = Modifier.fillMaxWidth().then(if (singleLine) Modifier else Modifier.fillMaxHeight()),
        )
        if (value.isEmpty()) {
            Text(text = placeholder, style = fieldTextStyle(), color = Neutral60)
        }
    }
}

@Composable
private fun fieldTextStyle() =
    MaterialTheme.typography.bodyMedium.copy(fontSize = 14.sp, letterSpacing = 0.sp)

/**
 * Dashed drop-zone from the amended spec. Once an image is chosen it is shown in place of the
 * prompt, but the "Choose File" button stays so it can be swapped.
 */
@Composable
private fun ImageUploadBox(
    thumbnailBytes: ByteArray?,
    existingThumbnailUrl: String?,
    onChooseFile: () -> Unit,
) {
    val borderColor = Neutral20.copy(alpha = 0.35f)
    val hasImage = thumbnailBytes != null || existingThumbnailUrl != null

    Box {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .height(175.dp)
                .padding(top = LabelOverlap)
                .drawBehind {
                    drawRoundRect(
                        color = borderColor,
                        size = Size(size.width, size.height),
                        cornerRadius = CornerRadius(6.dp.toPx(), 6.dp.toPx()),
                        style = Stroke(
                            width = 1.dp.toPx(),
                            pathEffect = PathEffect.dashPathEffect(
                                floatArrayOf(6.dp.toPx(), 6.dp.toPx()),
                            ),
                        ),
                    )
                }
                .padding(12.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
        ) {
            if (hasImage) {
                AsyncImage(
                    model = thumbnailBytes ?: existingThumbnailUrl,
                    contentDescription = null,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(100.dp)
                        .clip(RoundedCornerShape(8.dp)),
                )
                Spacer(Modifier.height(12.dp))
            } else {
                Box(
                    modifier = Modifier
                        .size(56.dp)
                        .clip(CircleShape)
                        .background(Primary40.copy(alpha = 0.2f)),
                    contentAlignment = Alignment.Center,
                ) {
                    Icon(
                        imageVector = Icons.Filled.CreateNewFolder,
                        contentDescription = null,
                        tint = Neutral20,
                        modifier = Modifier.size(26.dp),
                    )
                }

                Spacer(Modifier.height(14.dp))

                Text(
                    text = "Upload Your Article Image",
                    style = MaterialTheme.typography.bodyMedium.copy(fontSize = 15.sp, letterSpacing = 0.sp),
                    color = Neutral20,
                )
                Spacer(Modifier.height(2.dp))
                Text(
                    text = "JPEG/PNG (Max 5MB)",
                    style = MaterialTheme.typography.bodySmall.copy(fontSize = 11.sp, letterSpacing = 0.sp),
                    color = Neutral60,
                )

                Spacer(Modifier.height(14.dp))
            }

            Box(
                modifier = Modifier
                    .height(23.dp)
                    .clip(RoundedCornerShape(4.5.dp))
                    .border(1.dp, Primary40, RoundedCornerShape(4.5.dp))
                    .clickable(
                        indication = null,
                        interactionSource = remember { MutableInteractionSource() },
                        onClick = onChooseFile,
                    )
                    .padding(horizontal = 18.dp),
                contentAlignment = Alignment.Center,
            ) {
                Text(
                    text = "Choose File",
                    style = MaterialTheme.typography.bodySmall.copy(fontSize = 12.sp, letterSpacing = 0.sp),
                    color = Primary40,
                )
            }
        }

        Text(
            text = "Image",
            style = MaterialTheme.typography.bodyMedium.copy(fontSize = 13.sp, letterSpacing = 0.sp),
            color = Neutral20,
            modifier = Modifier
                .padding(start = 12.dp)
                .background(Color.White)
                .padding(horizontal = 4.dp),
        )
    }
}
