package ke.co.smartroundclinic.doctor.presentation.main.articles.ui

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
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.Article
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.outlined.Image
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage
import ke.co.smartroundclinic.doctor.domain.model.Article
import ke.co.smartroundclinic.doctor.domain.model.ArticleCategory
import ke.co.smartroundclinic.doctor.domain.model.ArticleState
import ke.co.smartroundclinic.doctor.presentation.common.composables.DashboardHeader
import ke.co.smartroundclinic.doctor.presentation.common.composables.PrimaryButton
import ke.co.smartroundclinic.doctor.presentation.main.profile.PersonalInfoViewModel
import ke.co.smartroundclinic.doctor.presentation.theme.CardBackground
import ke.co.smartroundclinic.doctor.presentation.theme.Error40
import ke.co.smartroundclinic.doctor.presentation.theme.Error90
import ke.co.smartroundclinic.doctor.presentation.theme.GradientEnd
import ke.co.smartroundclinic.doctor.presentation.theme.GradientStart
import ke.co.smartroundclinic.doctor.presentation.theme.Primary40
import ke.co.smartroundclinic.doctor.presentation.theme.Primary90
import ke.co.smartroundclinic.doctor.presentation.theme.ShapeBadge
import ke.co.smartroundclinic.doctor.presentation.theme.ShapeCard
import ke.co.smartroundclinic.doctor.presentation.theme.ShapePill
import ke.co.smartroundclinic.doctor.presentation.theme.StatusPublished
import ke.co.smartroundclinic.doctor.presentation.theme.StatusSuspended
import kotlinx.datetime.Instant
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import org.koin.compose.viewmodel.koinViewModel

internal enum class ArticlesTab(val label: String) {
    MY_ARTICLES("My Articles"),
    OTHER_ARTICLES("Other Articles"),
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun ArticleListScreen(
    myArticles: List<Article>,
    liveArticles: List<Article>,
    isLoadingMine: Boolean,
    isLoadingLive: Boolean,
    hasLoadedMine: Boolean,
    hasLoadedLive: Boolean,
    selectedTab: ArticlesTab,
    categories: List<ArticleCategory>,
    onTabSelected: (ArticlesTab) -> Unit,
    onWriteArticle: () -> Unit,
    onEditArticle: (Article) -> Unit,
    onArticleClick: (Article) -> Unit,
    onPublish: (Article) -> Unit,
    onUnpublish: (Article) -> Unit,
    onDelete: (Article) -> Unit,
    onRefresh: () -> Unit,
    onProfileClick: () -> Unit = {},
    onNotificationsClick: () -> Unit = {},
    modifier: Modifier = Modifier,
    profileViewModel: PersonalInfoViewModel = koinViewModel(),
) {
    val isMyTab = selectedTab == ArticlesTab.MY_ARTICLES
    var selectedCategoryId by remember { mutableStateOf<String?>(null) }

    val user by profileViewModel.user.collectAsState()
    val doctorName = user?.fullName?.trim() ?: ""

    LaunchedEffect(selectedTab) { if (isMyTab) selectedCategoryId = null }

    val articles = if (isMyTab) myArticles else liveArticles
    val filteredArticles = if (!isMyTab && selectedCategoryId != null) {
        articles.filter { it.categoryId == selectedCategoryId }
    } else articles
    val isLoading = if (isMyTab) isLoadingMine else isLoadingLive
    val hasLoaded = if (isMyTab) hasLoadedMine else hasLoadedLive

    Scaffold(
        modifier = modifier,
        topBar = {
            DashboardHeader(
                title = "Articles",
                onProfileClick = onProfileClick,
                onNotificationsClick = onNotificationsClick,
            )
        },
        floatingActionButton = {
            if (isMyTab) {
                FloatingActionButton(
                    onClick = onWriteArticle,
                    containerColor = Primary40,
                    shape = CircleShape,
                ) {
                    Icon(
                        imageVector = Icons.Filled.Edit,
                        contentDescription = "Write Article",
                        tint = Color.White,
                    )
                }
            }
        },
        contentWindowInsets = WindowInsets(0),
    ) { paddingValues ->
        Column(modifier = Modifier.fillMaxSize().padding(paddingValues)) {

            SegmentedTabRow(selectedTab = selectedTab, onTabSelected = onTabSelected)

            if (!isMyTab && categories.isNotEmpty()) {
                CategoryFilterRow(
                    categories = categories,
                    selectedCategoryId = selectedCategoryId,
                    onCategorySelected = { selectedCategoryId = it },
                )
            }

            PullToRefreshBox(
                isRefreshing = isLoading,
                onRefresh = onRefresh,
                modifier = Modifier.weight(1f),
            ) {
                when {
                    !hasLoaded && filteredArticles.isEmpty() -> {
                        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                            CircularProgressIndicator(color = Primary40)
                        }
                    }

                    hasLoaded && filteredArticles.isEmpty() -> {
                        EmptyArticlesView(
                            isMyTab = isMyTab,
                            onWriteArticle = onWriteArticle,
                            modifier = Modifier.fillMaxSize(),
                        )
                    }

                    else -> {
                        LazyColumn(
                            modifier = Modifier.fillMaxSize(),
                            contentPadding = PaddingValues(
                                start = 16.dp,
                                end = 16.dp,
                                top = 12.dp,
                                bottom = 88.dp,
                            ),
                            verticalArrangement = Arrangement.spacedBy(12.dp),
                        ) {
                            items(filteredArticles, key = { it.id }) { article ->
                                ArticleCard(
                                    article = article,
                                    isOwn = isMyTab,
                                    authorName = if (isMyTab) doctorName else "",
                                    onClick = { onArticleClick(article) },
                                    onEdit = { onEditArticle(article) },
                                    onPublish = { onPublish(article) },
                                    onUnpublish = { onUnpublish(article) },
                                    onDelete = { onDelete(article) },
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun SegmentedTabRow(
    selectedTab: ArticlesTab,
    onTabSelected: (ArticlesTab) -> Unit,
    modifier: Modifier = Modifier,
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 12.dp)
            .shadow(elevation = 4.dp, shape = ShapePill)
            .clip(ShapePill)
            .background(color = MaterialTheme.colorScheme.surfaceVariant)
            .padding(4.dp),
    ) {
        Row {
            ArticlesTab.entries.forEach { tab ->
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
                        color = if (isSelected) MaterialTheme.colorScheme.onSurface else MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                }
            }
        }
    }
}

@Composable
private fun CategoryFilterRow(
    categories: List<ArticleCategory>,
    selectedCategoryId: String?,
    onCategorySelected: (String?) -> Unit,
    modifier: Modifier = Modifier,
) {
    LazyRow(
        modifier = modifier.fillMaxWidth(),
        contentPadding = PaddingValues(horizontal = 16.dp, vertical = 4.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        item {
            FilterChip(label = "All", isSelected = selectedCategoryId == null, onClick = { onCategorySelected(null) })
        }
        items(categories, key = { it.id }) { cat ->
            FilterChip(label = cat.name, isSelected = selectedCategoryId == cat.id, onClick = { onCategorySelected(cat.id) })
        }
    }
    Spacer(Modifier.height(4.dp))
}

@Composable
private fun FilterChip(label: String, isSelected: Boolean, onClick: () -> Unit) {
    val gradient = Brush.horizontalGradient(listOf(GradientStart, GradientEnd))
    Box(
        contentAlignment = Alignment.Center,
        modifier = Modifier
            .clip(ShapePill)
            .then(
                if (isSelected) Modifier.background(gradient)
                else Modifier.background(MaterialTheme.colorScheme.surfaceVariant)
            )
            .clickable(indication = null, interactionSource = remember { MutableInteractionSource() }, onClick = onClick)
            .padding(horizontal = 16.dp, vertical = 6.dp),
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.labelSmall,
            color = if (isSelected) Color.White else MaterialTheme.colorScheme.onSurfaceVariant,
        )
    }
}

@Composable
private fun EmptyArticlesView(
    isMyTab: Boolean,
    onWriteArticle: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier.fillMaxWidth().padding(horizontal = 32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
    ) {
        Box(
            modifier = Modifier.size(80.dp).clip(CircleShape).background(Primary90),
            contentAlignment = Alignment.Center,
        ) {
            Icon(
                imageVector = Icons.AutoMirrored.Outlined.Article,
                contentDescription = null,
                tint = Primary40,
                modifier = Modifier.size(40.dp),
            )
        }
        Spacer(Modifier.height(16.dp))
        Text(
            text = if (isMyTab) "No Articles Yet" else "No Published Articles",
            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
        )
        Spacer(Modifier.height(8.dp))
        Text(
            text = if (isMyTab) "Create your first article and share your expertise with patients." else "No articles from other doctors yet.",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            textAlign = TextAlign.Center,
        )
        if (isMyTab) {
            Spacer(Modifier.height(24.dp))
            PrimaryButton(onClick = onWriteArticle) {
                Text(
                    text = "Write Article",
                    style = MaterialTheme.typography.labelLarge,
                    color = Color.White,
                    modifier = Modifier.padding(vertical = 12.dp, horizontal = 16.dp),
                )
            }
        }
    }
}

@Composable
private fun ArticleCard(
    article: Article,
    isOwn: Boolean,
    authorName: String,
    onClick: () -> Unit,
    onEdit: () -> Unit,
    onPublish: () -> Unit,
    onUnpublish: () -> Unit,
    onDelete: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val readMinutes = remember(article.content) {
        val words = article.content
            .replace(Regex("<[^>]+>"), "")
            .trim()
            .split(Regex("\\s+"))
            .count { it.isNotBlank() }
        maxOf(1, words / 200)
    }

    val formattedDate = remember(article.datePosted, article.createdAt) {
        val dateStr = article.datePosted ?: article.createdAt
        try {
            val dt = Instant.parse(dateStr).toLocalDateTime(TimeZone.currentSystemDefault())
            val month = dt.month.name.lowercase().replaceFirstChar { it.uppercase() }.take(3)
            "${dt.dayOfMonth} $month"
        } catch (_: Exception) {
            ""
        }
    }

    Card(
        onClick = onClick,
        modifier = modifier.fillMaxWidth(),
        shape = ShapeCard,
        colors = CardDefaults.cardColors(containerColor = CardBackground),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp, pressedElevation = 8.dp),
    ) {
        Column(modifier = Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {

            Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = buildString {
                        if (authorName.isNotBlank()) append("$authorName · ")
                        append("$readMinutes min read")
                        if (formattedDate.isNotBlank()) append(" · $formattedDate")
                    },
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier.weight(1f),
                )
                if (isOwn) {
                    Spacer(Modifier.width(8.dp))
                    ArticleStateBadge(state = article.state)
                }
            }

            Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                Column(
                    modifier = Modifier.weight(1f),
                    verticalArrangement = Arrangement.spacedBy(4.dp),
                ) {
                    Text(
                        text = article.title,
                        style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold),
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis,
                    )
                    Text(
                        text = article.summary,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis,
                    )
                }
                Box(
                    modifier = Modifier
                        .size(80.dp)
                        .clip(ShapeCard)
                        .background(MaterialTheme.colorScheme.surfaceVariant),
                    contentAlignment = Alignment.Center,
                ) {
                    if (article.thumbnailUrl != null) {
                        AsyncImage(
                            model = article.thumbnailUrl,
                            contentDescription = null,
                            contentScale = ContentScale.Crop,
                            modifier = Modifier.fillMaxSize(),
                        )
                    } else {
                        Icon(
                            imageVector = Icons.Outlined.Image,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.size(28.dp),
                        )
                    }
                }
            }

            if (isOwn && article.state != ArticleState.SUSPENDED) {
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    when (article.state) {
                        ArticleState.DRAFT -> ActionChip(label = "Publish", color = Primary40, onClick = onPublish)
                        ArticleState.LIVE -> ActionChip(label = "Unpublish", color = MaterialTheme.colorScheme.onSurfaceVariant, onClick = onUnpublish)
                        else -> Unit
                    }
                    ActionChip(
                        label = "Edit",
                        color = Primary40,
                        icon = {
                            Icon(
                                imageVector = Icons.Filled.Edit,
                                contentDescription = null,
                                tint = Primary40,
                                modifier = Modifier.size(12.dp),
                            )
                        },
                        onClick = onEdit,
                    )
                    ActionChip(
                        label = "Delete",
                        color = Error40,
                        icon = {
                            Icon(
                                imageVector = Icons.Filled.Delete,
                                contentDescription = null,
                                tint = Error40,
                                modifier = Modifier.size(12.dp),
                            )
                        },
                        onClick = onDelete,
                    )
                }
            }
        }
    }
}

@Composable
private fun ActionChip(
    label: String,
    color: Color,
    icon: (@Composable () -> Unit)? = null,
    onClick: () -> Unit,
) {
    Row(
        modifier = Modifier
            .clip(ShapePill)
            .background(color.copy(alpha = 0.1f))
            .clickable(indication = null, interactionSource = remember { MutableInteractionSource() }, onClick = onClick)
            .padding(horizontal = 10.dp, vertical = 4.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(4.dp),
    ) {
        icon?.invoke()
        Text(text = label, style = MaterialTheme.typography.labelSmall, color = color)
    }
}

@Composable
internal fun ArticleStateBadge(state: ArticleState, modifier: Modifier = Modifier) {
    val (label, bgColor, textColor) = when (state) {
        ArticleState.DRAFT -> Triple("Draft", MaterialTheme.colorScheme.surfaceVariant, MaterialTheme.colorScheme.onSurfaceVariant)
        ArticleState.LIVE -> Triple("Published", Color(0xFFE6F4E5), StatusPublished)
        ArticleState.SUSPENDED -> Triple("Suspended", Error90, StatusSuspended)
        ArticleState.DELETED -> Triple("Deleted", MaterialTheme.colorScheme.errorContainer, MaterialTheme.colorScheme.error)
    }
    Box(
        modifier = modifier
            .clip(ShapeBadge)
            .background(bgColor)
            .padding(horizontal = 8.dp, vertical = 2.dp),
    ) {
        Text(text = label, style = MaterialTheme.typography.labelSmall, color = textColor)
    }
}
