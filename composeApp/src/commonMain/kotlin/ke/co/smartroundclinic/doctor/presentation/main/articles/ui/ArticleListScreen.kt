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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.Article
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.outlined.Image
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import ke.co.smartroundclinic.doctor.presentation.main.articles.ArticleStatus
import ke.co.smartroundclinic.doctor.presentation.main.articles.ArticleUi
import ke.co.smartroundclinic.doctor.presentation.common.composables.PrimaryButton
import ke.co.smartroundclinic.doctor.presentation.theme.CardBackground
import ke.co.smartroundclinic.doctor.presentation.theme.Error40
import ke.co.smartroundclinic.doctor.presentation.theme.Error90
import ke.co.smartroundclinic.doctor.presentation.theme.Primary40
import ke.co.smartroundclinic.doctor.presentation.theme.Primary90
import ke.co.smartroundclinic.doctor.presentation.theme.ShapeBadge
import ke.co.smartroundclinic.doctor.presentation.theme.ShapeCard
import ke.co.smartroundclinic.doctor.presentation.theme.ShapePill
import ke.co.smartroundclinic.doctor.presentation.theme.StatusPublished
import ke.co.smartroundclinic.doctor.presentation.theme.StatusSuspended

private enum class ArticlesTab(val label: String) {
    MY_ARTICLES("My Articles"),
    OTHER_ARTICLES("Other Articles"),
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun ArticleListScreen(
    myArticles: List<ArticleUi>,
    otherArticles: List<ArticleUi>,
    onWriteArticle: () -> Unit,
    onArticleClick: (ArticleUi) -> Unit,
    modifier: Modifier = Modifier,
) {
    var selectedTab by remember { mutableStateOf(ArticlesTab.MY_ARTICLES) }
    val articles = if (selectedTab == ArticlesTab.MY_ARTICLES) myArticles else otherArticles

    Scaffold(
        modifier = modifier,
        topBar = {
            TopAppBar(title = { Text("Articles", style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)) })
        },
        contentWindowInsets = WindowInsets(0),
    ) { paddingValues ->
        Column(modifier = Modifier.fillMaxSize().padding(paddingValues)) {
            ArticleTabRow(selectedTab = selectedTab, onTabSelected = { selectedTab = it })

            if (articles.isEmpty()) {
                EmptyArticlesView(onWriteArticle = onWriteArticle, modifier = Modifier.weight(1f))
            } else {
                LazyColumn(
                    modifier = Modifier.weight(1f),
                    contentPadding = PaddingValues(horizontal = 16.dp, vertical = 12.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp),
                ) {
                    items(articles, key = { it.id }) { article ->
                        ArticleCard(
                            article = article,
                            showDelete = selectedTab == ArticlesTab.MY_ARTICLES,
                            onClick = { onArticleClick(article) },
                            onDelete = {},
                        )
                    }
                }
            }

            if (selectedTab == ArticlesTab.MY_ARTICLES) {
                HorizontalDivider(thickness = 0.5.dp, color = MaterialTheme.colorScheme.outlineVariant)
                Box(modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 12.dp)) {
                    PrimaryButton(onClick = onWriteArticle) {
                        Text(text = "Write Article", style = MaterialTheme.typography.labelLarge, color = Color.White, modifier = Modifier.padding(vertical = 12.dp))
                    }
                }
            }
        }
    }
}

@Composable
private fun ArticleTabRow(selectedTab: ArticlesTab, onTabSelected: (ArticlesTab) -> Unit, modifier: Modifier = Modifier) {
    Row(
        modifier = modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 12.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        ArticlesTab.entries.forEach { tab ->
            val isSelected = selectedTab == tab
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier
                    .weight(1f)
                    .clip(ShapePill)
                    .background(if (isSelected) Primary40 else MaterialTheme.colorScheme.surfaceVariant)
                    .clickable(indication = null, interactionSource = remember { MutableInteractionSource() }) { onTabSelected(tab) }
                    .padding(vertical = 10.dp),
            ) {
                Text(text = tab.label, style = MaterialTheme.typography.labelMedium, color = if (isSelected) Color.White else MaterialTheme.colorScheme.onSurfaceVariant)
            }
        }
    }
}

@Composable
private fun EmptyArticlesView(onWriteArticle: () -> Unit, modifier: Modifier = Modifier) {
    Column(
        modifier = modifier.fillMaxWidth().padding(horizontal = 32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
    ) {
        Box(modifier = Modifier.size(80.dp).clip(CircleShape).background(Primary90), contentAlignment = Alignment.Center) {
            Icon(imageVector = Icons.AutoMirrored.Outlined.Article, contentDescription = null, tint = Primary40, modifier = Modifier.size(40.dp))
        }
        Spacer(Modifier.height(16.dp))
        Text(text = "No Articles Yet", style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold))
        Spacer(Modifier.height(8.dp))
        Text(text = "Create your first article to get started", style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant, textAlign = TextAlign.Center)
        Spacer(Modifier.height(24.dp))
        PrimaryButton(onClick = onWriteArticle) {
            Text(text = "Write Article", style = MaterialTheme.typography.labelLarge, color = Color.White, modifier = Modifier.padding(vertical = 12.dp))
        }
    }
}

@Composable
private fun ArticleCard(article: ArticleUi, showDelete: Boolean, onClick: () -> Unit, onDelete: () -> Unit, modifier: Modifier = Modifier) {
    Card(onClick = onClick, modifier = modifier.fillMaxWidth(), shape = ShapeCard, colors = CardDefaults.cardColors(containerColor = CardBackground), elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)) {
        Row(modifier = Modifier.padding(12.dp), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            Column(modifier = Modifier.weight(1f)) {
                ArticleStatusBadge(status = article.status)
                Spacer(Modifier.height(6.dp))
                Text(text = article.title, style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.SemiBold), maxLines = 2, overflow = TextOverflow.Ellipsis)
                Spacer(Modifier.height(4.dp))
                Text(text = article.snippet, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant, maxLines = 2, overflow = TextOverflow.Ellipsis)
            }
            Column(horizontalAlignment = Alignment.End, verticalArrangement = Arrangement.SpaceBetween, modifier = Modifier.height(88.dp)) {
                Box(modifier = Modifier.size(72.dp).clip(ShapeCard).background(MaterialTheme.colorScheme.surfaceVariant), contentAlignment = Alignment.Center) {
                    Icon(imageVector = Icons.Outlined.Image, contentDescription = null, tint = MaterialTheme.colorScheme.onSurfaceVariant, modifier = Modifier.size(28.dp))
                }
                if (showDelete) {
                    Box(
                        modifier = Modifier
                            .size(24.dp)
                            .clip(CircleShape)
                            .background(Error40)
                            .clickable(indication = null, interactionSource = remember { MutableInteractionSource() }, onClick = onDelete),
                        contentAlignment = Alignment.Center,
                    ) {
                        Icon(imageVector = Icons.Filled.Delete, contentDescription = "Delete article", tint = Color.White, modifier = Modifier.size(14.dp))
                    }
                }
            }
        }
    }
}

@Composable
internal fun ArticleStatusBadge(status: ArticleStatus, modifier: Modifier = Modifier) {
    val (label, bgColor, textColor) = when (status) {
        ArticleStatus.PENDING -> Triple("Pending", Primary90, Primary40)
        ArticleStatus.PUBLISHED -> Triple("Published", Color(0xFFE6F4E5), StatusPublished)
        ArticleStatus.SUSPENDED -> Triple("Suspended", Error90, StatusSuspended)
    }
    Box(modifier = modifier.clip(ShapeBadge).background(bgColor).padding(horizontal = 8.dp, vertical = 2.dp)) {
        Text(text = label, style = MaterialTheme.typography.labelSmall, color = textColor)
    }
}
