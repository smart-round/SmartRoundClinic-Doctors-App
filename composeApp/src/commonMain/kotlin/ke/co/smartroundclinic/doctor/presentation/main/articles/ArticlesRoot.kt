package ke.co.smartroundclinic.doctor.presentation.main.articles

import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.retain.retain
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.navigation3.runtime.NavKey
import androidx.navigation3.runtime.entryProvider
import androidx.navigation3.ui.NavDisplay
import ke.co.smartroundclinic.doctor.domain.model.Article
import ke.co.smartroundclinic.doctor.domain.model.ArticleState
import ke.co.smartroundclinic.doctor.presentation.main.articles.destinations.ArticleDetail
import ke.co.smartroundclinic.doctor.presentation.main.articles.ui.ArticlesTab
import ke.co.smartroundclinic.doctor.presentation.main.articles.destinations.ArticleList
import ke.co.smartroundclinic.doctor.presentation.main.articles.destinations.WriteArticle
import ke.co.smartroundclinic.doctor.presentation.main.articles.ui.ArticleDetailScreen
import ke.co.smartroundclinic.doctor.presentation.main.articles.ui.ArticleListScreen
import ke.co.smartroundclinic.doctor.presentation.main.articles.ui.WriteArticleScreen
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun ArticlesRoot(
    modifier: Modifier = Modifier,
    onAtRootChanged: (Boolean) -> Unit = {},
    onProfileClick: () -> Unit = {},
    onNotificationsClick: () -> Unit = {},
) {
    val viewModel = koinViewModel<ArticlesViewModel>()
    val backStack = retain { mutableStateListOf<NavKey>(ArticleList) }
    val isAtRoot = backStack.size == 1
    var selectedTab by remember { mutableStateOf(ArticlesTab.MY_ARTICLES) }

    SideEffect { onAtRootChanged(isAtRoot) }

    val myArticles by viewModel.myArticles.collectAsState()
    val liveArticles by viewModel.liveArticles.collectAsState()
    val categories by viewModel.categories.collectAsState()

    NavDisplay(
        modifier = modifier,
        backStack = backStack,
        onBack = { backStack.removeLastOrNull() },
        entryProvider = entryProvider {
            entry<ArticleList> {
                ArticleListScreen(
                    myArticles = myArticles,
                    liveArticles = liveArticles,
                    isLoadingMine = viewModel.isLoadingMine,
                    isLoadingLive = viewModel.isLoadingLive,
                    hasLoadedMine = viewModel.hasLoadedMine,
                    hasLoadedLive = viewModel.hasLoadedLive,
                    selectedTab = selectedTab,
                    categories = categories,
                    onTabSelected = { tab ->
                        selectedTab = tab
                        if (tab == ArticlesTab.MY_ARTICLES) viewModel.refreshMyArticles()
                        else viewModel.refreshLiveArticles()
                    },
                    onWriteArticle = { viewModel.clearThumbnail(); backStack.add(WriteArticle()) },
                    onEditArticle = { article -> viewModel.clearThumbnail(); backStack.add(WriteArticle(articleId = article.id)) },
                    onArticleClick = { article -> backStack.add(ArticleDetail(article.id)) },
                    onPublish = { article -> viewModel.publishArticle(article.id) },
                    onUnpublish = { article -> viewModel.unpublishArticle(article.id) },
                    onDelete = { article -> viewModel.deleteArticle(article.id) },
                    onRefresh = {
                        if (selectedTab == ArticlesTab.MY_ARTICLES) viewModel.refreshMyArticles(forceRefresh = true)
                        else viewModel.refreshLiveArticles(forceRefresh = true)
                    },
                    onProfileClick = onProfileClick,
                    onNotificationsClick = onNotificationsClick,
                )
            }
            entry<WriteArticle> { dest ->
                val article: Article? = if (dest.articleId != null) {
                    myArticles.find { it.id == dest.articleId }
                } else null

                WriteArticleScreen(
                    article = article,
                    categories = categories,
                    thumbnailBytes = viewModel.thumbnailBytes,
                    isSaving = viewModel.isSaving,
                    onBack = { backStack.removeLastOrNull() },
                    onThumbnailPicked = { bytes, filename -> viewModel.setThumbnail(bytes, filename) },
                    onRefreshCategories = { viewModel.refreshCategories() },
                    onSaveDraft = { title, content, summary, categoryId ->
                        viewModel.createArticle(title, content, summary, categoryId) {
                            backStack.removeLastOrNull()
                        }
                    },
                    onPublish = { title, content, summary, categoryId ->
                        if (dest.articleId != null) {
                            viewModel.updateArticle(dest.articleId, title, content, summary, categoryId) {
                                val updated = myArticles.find { it.id == dest.articleId }
                                if (updated?.state == ArticleState.DRAFT) {
                                    viewModel.publishArticle(dest.articleId)
                                }
                                backStack.removeLastOrNull()
                            }
                        } else {
                            viewModel.createArticle(title, content, summary, categoryId) { created ->
                                viewModel.publishArticle(created.id)
                                backStack.removeLastOrNull()
                            }
                        }
                    },
                )
            }
            entry<ArticleDetail> { dest ->
                val article = myArticles.find { it.id == dest.articleId }
                    ?: liveArticles.find { it.id == dest.articleId }

                if (article != null) {
                    val isOwn = myArticles.any { it.id == dest.articleId }
                    ArticleDetailScreen(
                        article = article,
                        isOwn = isOwn,
                        onBack = { backStack.removeLastOrNull() },
                        onEdit = { viewModel.clearThumbnail(); backStack.add(WriteArticle(articleId = article.id)) },
                        onPublish = { viewModel.publishArticle(article.id) },
                        onUnpublish = { viewModel.unpublishArticle(article.id) },
                        onDelete = {
                            viewModel.deleteArticle(article.id) {
                                backStack.removeLastOrNull()
                            }
                        },
                    )
                }
            }
        },
    )
}
