package ke.co.smartroundclinic.doctor.presentation.main.articles

import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.retain.retain
import androidx.compose.ui.Modifier
import androidx.navigation3.runtime.NavKey
import androidx.navigation3.runtime.entryProvider
import androidx.navigation3.ui.NavDisplay
import ke.co.smartroundclinic.doctor.presentation.main.articles.destinations.ArticleDetail
import ke.co.smartroundclinic.doctor.presentation.main.articles.destinations.ArticleList
import ke.co.smartroundclinic.doctor.presentation.main.articles.destinations.WriteArticle
import ke.co.smartroundclinic.doctor.presentation.main.articles.ui.ArticleDetailScreen
import ke.co.smartroundclinic.doctor.presentation.main.articles.ui.ArticleListScreen
import ke.co.smartroundclinic.doctor.presentation.main.articles.ui.WriteArticleScreen

internal enum class ArticleStatus { PENDING, PUBLISHED, SUSPENDED }

internal data class ArticleUi(
    val id: Int,
    val title: String,
    val snippet: String,
    val body: String,
    val status: ArticleStatus,
)

private val sampleBody = """
Research shows that exposure to sunlight plays a key role in modulating immune response, with important implications for respiratory viral infections including Covid-19.

Vitamin D — synthesised by the skin on exposure to UV-B radiation — has been shown to enhance the innate immune system, reduce inflammatory cytokine production, and improve mucosal defences in the respiratory tract.

Several observational studies conducted during the 2020–2024 pandemic period found a consistent inverse relationship between population-level serum 25-hydroxyvitamin D concentrations and Covid-19 mortality rates across more than 40 countries.

While randomised controlled trial data remain limited, the mechanistic plausibility and the low risk profile of safe sun exposure and moderate supplementation make this an area worth continued clinical attention.
""".trimIndent()

private val myArticles = listOf(
    ArticleUi(1, "How Sunlight and Covid-19 Interact In 2026", "Research shows that exposure to sunlight plays a key role in modulating immune response, with implications for viral infections.", sampleBody, ArticleStatus.PENDING),
    ArticleUi(2, "How Sunlight and Covid-19 Interact In 2026", "Research shows that exposure to sunlight plays a key role in modulating immune response, with implications for viral infections.", sampleBody, ArticleStatus.PUBLISHED),
    ArticleUi(3, "How Sunlight and Covid-19 Interact In 2026", "Research shows that exposure to sunlight plays a key role in modulating immune response, with implications for viral infections.", sampleBody, ArticleStatus.PENDING),
    ArticleUi(4, "How Sunlight and Covid-19 Interact In 2026", "Research shows that exposure to sunlight plays a key role in modulating immune response, with implications for viral infections.", sampleBody, ArticleStatus.SUSPENDED),
)

private val otherArticles = listOf(
    ArticleUi(5, "How Sunlight and Covid-19 Interact In 2026", "Research shows that exposure to sunlight plays a key role in modulating immune response, with implications for viral infections.", sampleBody, ArticleStatus.PUBLISHED),
    ArticleUi(6, "How Sunlight and Covid-19 Interact In 2026", "Research shows that exposure to sunlight plays a key role in modulating immune response, with implications for viral infections.", sampleBody, ArticleStatus.PUBLISHED),
)

private val allArticles = myArticles + otherArticles

@Composable
fun ArticlesRoot(
    modifier: Modifier = Modifier,
    onAtRootChanged: (Boolean) -> Unit = {},
) {
    val backStack = retain { mutableStateListOf<NavKey>(ArticleList) }
    val isAtRoot = backStack.size == 1

    SideEffect { onAtRootChanged(isAtRoot) }

    NavDisplay(
        modifier = modifier,
        backStack = backStack,
        onBack = { backStack.removeLastOrNull() },
        entryProvider = entryProvider {
            entry<ArticleList> {
                ArticleListScreen(
                    myArticles = myArticles,
                    otherArticles = otherArticles,
                    onWriteArticle = { backStack.add(WriteArticle) },
                    onArticleClick = { article -> backStack.add(ArticleDetail(article.id)) },
                )
            }
            entry<WriteArticle> {
                WriteArticleScreen(
                    onBack = { backStack.removeLastOrNull() },
                    onPublish = { backStack.removeLastOrNull() },
                )
            }
            entry<ArticleDetail> { dest ->
                val article = allArticles.first { it.id == dest.articleId }
                ArticleDetailScreen(
                    article = article,
                    onBack = { backStack.removeLastOrNull() },
                )
            }
        },
    )
}
