package ke.co.smartroundclinic.doctor.presentation.main.articles.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.MenuAnchorType
import androidx.compose.material3.OutlinedTextField
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
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage
import ke.co.smartroundclinic.doctor.domain.model.Article
import ke.co.smartroundclinic.doctor.domain.model.ArticleCategory
import ke.co.smartroundclinic.doctor.presentation.common.composables.PrimaryButton
import ke.co.smartroundclinic.doctor.presentation.theme.Primary40
import ke.co.smartroundclinic.doctor.presentation.theme.ShapeCard
import ke.co.smartroundclinic.doctor.presentation.theme.ShapeInput

@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun WriteArticleScreen(
    article: Article?,
    categories: List<ArticleCategory>,
    thumbnailBytes: ByteArray?,
    isSaving: Boolean,
    onBack: () -> Unit,
    onPickThumbnail: () -> Unit,
    onSaveDraft: (title: String, content: String, summary: String, categoryId: String) -> Unit,
    onPublish: (title: String, content: String, summary: String, categoryId: String) -> Unit,
    modifier: Modifier = Modifier,
) {
    var title by remember { mutableStateOf(article?.title ?: "") }
    var summary by remember { mutableStateOf(article?.summary ?: "") }
    var content by remember { mutableStateOf(article?.content ?: "") }
    var selectedCategoryId by remember { mutableStateOf(article?.categoryId ?: "") }
    var categoryExpanded by remember { mutableStateOf(false) }

    val selectedCategoryName = categories.find { it.id == selectedCategoryId }?.name ?: ""
    val isEditing = article != null
    val isFormValid = title.isNotBlank() && summary.isNotBlank() && content.isNotBlank() && selectedCategoryId.isNotBlank()

    LaunchedEffect(article) {
        if (article != null) {
            title = article.title
            summary = article.summary
            content = article.content
            selectedCategoryId = article.categoryId
        }
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
                title = { Text(if (isEditing) "Edit Article" else "Write Article") },
            )
        },
        contentWindowInsets = WindowInsets(0),
    ) { paddingValues ->
        Column(modifier = Modifier.fillMaxSize().padding(paddingValues)) {
            Column(
                modifier = Modifier
                    .weight(1f)
                    .verticalScroll(rememberScrollState())
                    .padding(horizontal = 16.dp, vertical = 16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp),
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(180.dp)
                        .clip(ShapeCard)
                        .background(MaterialTheme.colorScheme.surfaceVariant)
                        .border(1.dp, MaterialTheme.colorScheme.outline, ShapeCard)
                        .clickable(indication = null, interactionSource = remember { MutableInteractionSource() }, onClick = onPickThumbnail),
                    contentAlignment = Alignment.Center,
                ) {
                    when {
                        thumbnailBytes != null -> AsyncImage(
                            model = thumbnailBytes,
                            contentDescription = null,
                            contentScale = ContentScale.Crop,
                            modifier = Modifier.fillMaxSize().clip(ShapeCard),
                        )
                        article?.thumbnailUrl != null -> AsyncImage(
                            model = article.thumbnailUrl,
                            contentDescription = null,
                            contentScale = ContentScale.Crop,
                            modifier = Modifier.fillMaxSize().clip(ShapeCard),
                        )
                        else -> Column(horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.spacedBy(8.dp)) {
                            Icon(imageVector = Icons.Filled.CameraAlt, contentDescription = null, tint = MaterialTheme.colorScheme.onSurfaceVariant, modifier = Modifier.size(40.dp))
                            Text(text = "Tap to add cover image (optional)", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        }
                    }
                }

                OutlinedTextField(
                    value = title,
                    onValueChange = { title = it },
                    label = { Text("Article title *") },
                    shape = ShapeInput,
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true,
                )

                OutlinedTextField(
                    value = summary,
                    onValueChange = { summary = it },
                    label = { Text("Short summary *") },
                    shape = ShapeInput,
                    modifier = Modifier.fillMaxWidth(),
                    maxLines = 3,
                    minLines = 2,
                )

                ExposedDropdownMenuBox(
                    expanded = categoryExpanded,
                    onExpandedChange = { categoryExpanded = it },
                ) {
                    OutlinedTextField(
                        value = selectedCategoryName,
                        onValueChange = {},
                        readOnly = true,
                        label = { Text("Category *") },
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = categoryExpanded) },
                        shape = ShapeInput,
                        modifier = Modifier.fillMaxWidth().menuAnchor(MenuAnchorType.PrimaryNotEditable),
                    )
                    ExposedDropdownMenu(expanded = categoryExpanded, onDismissRequest = { categoryExpanded = false }) {
                        categories.forEach { cat ->
                            DropdownMenuItem(
                                text = { Text(cat.name) },
                                onClick = { selectedCategoryId = cat.id; categoryExpanded = false },
                                trailingIcon = if (selectedCategoryId == cat.id) {
                                    { Icon(imageVector = Icons.Filled.Check, contentDescription = null, tint = Primary40, modifier = Modifier.size(18.dp)) }
                                } else null,
                            )
                        }
                    }
                }

                OutlinedTextField(
                    value = content,
                    onValueChange = { content = it },
                    label = { Text("Article body *") },
                    shape = ShapeInput,
                    modifier = Modifier.fillMaxWidth().height(260.dp),
                    maxLines = Int.MAX_VALUE,
                )

                Spacer(Modifier.height(4.dp))
            }

            HorizontalDivider(thickness = 0.5.dp, color = MaterialTheme.colorScheme.outlineVariant)
            Column(
                modifier = Modifier.fillMaxWidth().navigationBarsPadding().padding(horizontal = 16.dp, vertical = 12.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                PrimaryButton(
                    onClick = { onPublish(title, content, summary, selectedCategoryId) },
                    enabled = isFormValid && !isSaving,
                ) {
                    Text(
                        text = if (isSaving) "Saving..." else if (isEditing) "Save & Publish" else "Publish",
                        style = MaterialTheme.typography.labelLarge,
                        color = Color.White,
                        modifier = Modifier.padding(vertical = 12.dp),
                    )
                }
                if (!isEditing) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(ke.co.smartroundclinic.doctor.presentation.theme.ShapeButton)
                            .background(if (isFormValid && !isSaving) MaterialTheme.colorScheme.surfaceVariant else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f))
                            .clickable(
                                enabled = isFormValid && !isSaving,
                                indication = null,
                                interactionSource = remember { MutableInteractionSource() },
                                onClick = { onSaveDraft(title, content, summary, selectedCategoryId) },
                            ),
                        contentAlignment = Alignment.Center,
                    ) {
                        Text(
                            text = "Save as Draft",
                            style = MaterialTheme.typography.labelLarge,
                            color = MaterialTheme.colorScheme.onSurface,
                            modifier = Modifier.padding(vertical = 14.dp),
                        )
                    }
                }
            }
        }
    }
}
