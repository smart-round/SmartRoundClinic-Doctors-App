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
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.outlined.Image
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.blur
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil3.compose.AsyncImage
import ke.co.smartroundclinic.doctor.core.platform.rememberShareText
import ke.co.smartroundclinic.doctor.domain.model.Article
import ke.co.smartroundclinic.doctor.domain.model.ArticleState
import ke.co.smartroundclinic.doctor.presentation.main.articles.readMinutes
import ke.co.smartroundclinic.doctor.presentation.theme.Neutral20
import ke.co.smartroundclinic.doctor.presentation.theme.Neutral40
import ke.co.smartroundclinic.doctor.presentation.theme.Primary40
import ke.co.smartroundclinic.doctor.presentation.theme.ShapePill
import kotlinx.datetime.Instant
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import org.jetbrains.compose.resources.painterResource
import smartroundclinic.composeapp.generated.resources.Res
import smartroundclinic.composeapp.generated.resources.delete_article
import smartroundclinic.composeapp.generated.resources.edit_article

/** Destructive glyph in the byline row — pure red in the Figma spec, not the muted error token. */
private val DeleteRed = Color(0xFFFF0000)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun ArticleDetailScreen(
    article: Article,
    categoryName: String,
    isOwn: Boolean,
    onBack: () -> Unit,
    onEdit: () -> Unit,
    onPublish: () -> Unit,
    onUnpublish: () -> Unit,
    onDelete: () -> Unit,
    onNotificationsClick: () -> Unit = {},
    modifier: Modifier = Modifier,
) {
    val shareText = rememberShareText()
    var showDeleteConfirm by remember { mutableStateOf(false) }

    val minutes = remember(article.content) { readMinutes(article.content) }
    val formattedDate = remember(article.datePosted, article.createdAt) {
        formatLongDate(article.datePosted ?: article.createdAt)
    }

    Scaffold(
        modifier = modifier,
        containerColor = Color.White,
        topBar = {
            // The app's standard sub-screen bar, same as every other detail screen.
            TopAppBar(
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(imageVector = Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
                title = {
                    Text(
                        text = "Article",
                        style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
                    )
                },
                actions = {
                    IconButton(onClick = { shareText("${article.title}\n\n${article.summary}") }) {
                        Icon(
                            imageVector = Icons.Filled.Share,
                            contentDescription = "Share article",
                            tint = Neutral20,
                            modifier = Modifier.size(20.dp),
                        )
                    }
                    if (isOwn) {
                        IconButton(onClick = onEdit) {
                            Icon(
                                painter = painterResource(Res.drawable.edit_article),
                                contentDescription = "Edit article",
                                tint = Color.Unspecified,
                                modifier = Modifier.size(20.dp),
                            )
                        }
                        IconButton(onClick = { showDeleteConfirm = true }) {
                            Icon(
                                painter = painterResource(Res.drawable.delete_article),
                                contentDescription = "Delete article",
                                tint = Color.Unspecified,
                                modifier = Modifier.size(20.dp),
                            )
                        }
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.White),
            )
        },
        contentWindowInsets = WindowInsets(0),
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.White)
                .padding(paddingValues)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = ArticlesGutter),
        ) {
            Spacer(Modifier.height(24.dp))

            if (categoryName.isNotBlank()) {
                Text(
                    text = categoryName,
                    style = MaterialTheme.typography.bodyMedium.copy(fontSize = 13.sp, letterSpacing = 0.sp),
                    color = Neutral40,
                )
                Spacer(Modifier.height(8.dp))
            }

            Text(
                text = article.title,
                style = MaterialTheme.typography.titleMedium.copy(
                    fontWeight = FontWeight.Bold,
                    fontSize = 18.sp,
                    lineHeight = 24.sp,
                    letterSpacing = 0.sp,
                ),
                color = Neutral20,
            )

            Spacer(Modifier.height(10.dp))

            // Share, edit and delete live in the top bar's action slots, so the byline is text only.
            Text(
                text = buildAnnotatedString {
                    if (formattedDate.isNotBlank()) append("$formattedDate by ")
                    val author = article.authorName
                    if (!author.isNullOrBlank()) {
                        withStyle(SpanStyle(color = Primary40)) { append(author) }
                    }
                    append(" · $minutes min read")
                },
                style = MaterialTheme.typography.bodyMedium.copy(fontSize = 12.sp, letterSpacing = 0.sp),
                color = Neutral20,
                modifier = Modifier.fillMaxWidth(),
            )

            Spacer(Modifier.height(16.dp))

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(147.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(MaterialTheme.colorScheme.surfaceVariant),
                contentAlignment = Alignment.Center,
            ) {
                if (article.thumbnailUrl != null) {
                    // Blurred cover fill hides the letterboxing when the source photo's aspect
                    // ratio doesn't match this banner, instead of stretching it to fit.
                    AsyncImage(
                        model = article.thumbnailUrl,
                        contentDescription = null,
                        contentScale = ContentScale.Crop,
                        modifier = Modifier.fillMaxSize().blur(16.dp),
                    )
                    AsyncImage(
                        model = article.thumbnailUrl,
                        contentDescription = null,
                        contentScale = ContentScale.Crop,
                        modifier = Modifier.fillMaxHeight().aspectRatio(1f),
                    )
                } else {
                    Icon(
                        imageVector = Icons.Outlined.Image,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.size(40.dp),
                    )
                }
            }

            Spacer(Modifier.height(24.dp))

            HtmlText(
                html = article.content,
                textAlign = TextAlign.Justify,
                modifier = Modifier.fillMaxWidth(),
            )

            // Edit and delete live in the top bar; moving an article between draft and live still
            // needs its own control under the body.
            if (isOwn && article.state != ArticleState.SUSPENDED) {
                Spacer(Modifier.height(28.dp))
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    when (article.state) {
                        ArticleState.DRAFT -> ArticleActionButton(
                            label = "Publish",
                            containerColor = Primary40,
                            onClick = onPublish,
                        )
                        ArticleState.LIVE -> ArticleActionButton(
                            label = "Unpublish",
                            containerColor = Color.Transparent,
                            contentColor = Primary40,
                            shape = RoundedCornerShape(8.dp),
                            borderColor = Primary40,
                            onClick = onUnpublish,
                        )
                        else -> Unit
                    }
                }
            }

            Spacer(Modifier.height(32.dp))
        }
    }

    if (showDeleteConfirm) {
        AlertDialog(
            onDismissRequest = { showDeleteConfirm = false },
            containerColor = MaterialTheme.colorScheme.background,
            title = { Text("Delete article?") },
            text = { Text("\"${article.title}\" will be removed permanently.") },
            confirmButton = {
                TextButton(onClick = { showDeleteConfirm = false; onDelete() }) {
                    Text("Delete", color = DeleteRed)
                }
            },
            dismissButton = {
                TextButton(onClick = { showDeleteConfirm = false }) { Text("Cancel") }
            },
        )
    }
}

@Composable
private fun ArticleActionButton(
    label: String,
    containerColor: Color,
    contentColor: Color = Color.White,
    shape: Shape = ShapePill,
    borderColor: Color? = null,
    onClick: () -> Unit,
) {
    Box(
        modifier = Modifier
            .clip(shape)
            .background(containerColor)
            .then(
                if (borderColor != null) Modifier.border(1.dp, borderColor, shape) else Modifier,
            )
            .clickable(
                indication = null,
                interactionSource = remember { MutableInteractionSource() },
                onClick = onClick,
            )
            .padding(horizontal = 20.dp, vertical = 8.dp),
        contentAlignment = Alignment.Center,
    ) {
        Text(text = label, style = MaterialTheme.typography.labelMedium, color = contentColor)
    }
}

/** "20 Nov, 2025" — the long form the detail byline uses. */
private fun formatLongDate(isoDate: String): String = try {
    val dateTime = Instant.parse(isoDate).toLocalDateTime(TimeZone.currentSystemDefault())
    val month = dateTime.month.name.lowercase().replaceFirstChar { it.uppercase() }.take(3)
    "${dateTime.dayOfMonth} $month, ${dateTime.year}"
} catch (_: Exception) {
    ""
}
