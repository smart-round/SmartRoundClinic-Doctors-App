package ke.co.smartroundclinic.doctor.data.repository

import ke.co.smartroundclinic.doctor.core.database.dao.ArticleDao
import ke.co.smartroundclinic.doctor.core.database.entity.toDomain
import ke.co.smartroundclinic.doctor.core.database.entity.toEntity
import ke.co.smartroundclinic.doctor.domain.model.Article
import ke.co.smartroundclinic.doctor.domain.repository.ArticleLocalRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class ArticleLocalRepositoryImpl(private val dao: ArticleDao) : ArticleLocalRepository {
    override fun observeMyArticles(): Flow<List<Article>> =
        dao.observeMyArticles().map { list -> list.map { it.toDomain() } }

    override fun observeLiveArticles(): Flow<List<Article>> =
        dao.observeLiveArticles().map { list -> list.map { it.toDomain() } }

    override suspend fun upsertMyArticles(articles: List<Article>) {
        if (articles.isNotEmpty()) dao.upsertArticles(articles.map { it.toEntity(isOwn = true) })
    }

    override suspend fun upsertLiveArticles(articles: List<Article>) {
        if (articles.isNotEmpty()) dao.upsertArticles(articles.map { it.toEntity(isOwn = false) })
    }

    override suspend fun upsertArticle(article: Article, isOwn: Boolean) =
        dao.upsertArticle(article.toEntity(isOwn))

    override suspend fun removeArticle(id: String) = dao.deleteById(id)

    override suspend fun clearMyArticles() = dao.clearMyArticles()

    override suspend fun clearLiveArticles() = dao.clearLiveArticles()

    override suspend fun clearAll() = dao.clearAll()
}
