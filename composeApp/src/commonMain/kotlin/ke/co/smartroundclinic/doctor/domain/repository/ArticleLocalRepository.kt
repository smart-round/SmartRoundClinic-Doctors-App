package ke.co.smartroundclinic.doctor.domain.repository

import ke.co.smartroundclinic.doctor.domain.model.Article
import kotlinx.coroutines.flow.Flow

interface ArticleLocalRepository {
    fun observeMyArticles(): Flow<List<Article>>
    fun observeLiveArticles(): Flow<List<Article>>
    suspend fun upsertMyArticles(articles: List<Article>)
    suspend fun upsertLiveArticles(articles: List<Article>)
    suspend fun upsertArticle(article: Article, isOwn: Boolean)
    suspend fun removeArticle(id: String)
}
