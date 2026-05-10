package ke.co.smartroundclinic.doctor.presentation.main.articles.destinations

import androidx.navigation3.runtime.NavKey
import kotlinx.serialization.Serializable

@Serializable
data object ArticleList : NavKey

@Serializable
data object WriteArticle : NavKey

@Serializable
data class ArticleDetail(val articleId: Int) : NavKey
