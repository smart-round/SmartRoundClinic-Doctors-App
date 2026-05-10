package ke.co.smartroundclinic.doctor.presentation.main.articles.destinations

import androidx.navigation3.runtime.NavKey
import kotlinx.serialization.Serializable

@Serializable
data object ArticleList : NavKey

@Serializable
data class WriteArticle(val articleId: String? = null) : NavKey

@Serializable
data class ArticleDetail(val articleId: String) : NavKey
