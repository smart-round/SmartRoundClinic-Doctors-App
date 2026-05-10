package ke.co.smartroundclinic.doctor.domain.repository

import ke.co.smartroundclinic.doctor.common.Resource
import ke.co.smartroundclinic.doctor.domain.model.Article
import ke.co.smartroundclinic.doctor.domain.model.ArticleCategory

interface ArticleRepository {
    suspend fun getCategories(): Resource<List<ArticleCategory>>
    suspend fun getMyArticles(): Resource<List<Article>>
    suspend fun getLiveArticles(): Resource<List<Article>>
    suspend fun createArticle(
        title: String,
        content: String,
        summary: String,
        categoryId: String,
        thumbnailBytes: ByteArray?,
        thumbnailFilename: String?,
    ): Resource<Article>
    suspend fun updateArticle(
        id: String,
        title: String?,
        content: String?,
        summary: String?,
        categoryId: String?,
        thumbnailBytes: ByteArray?,
        thumbnailFilename: String?,
    ): Resource<Article>
    suspend fun publishArticle(id: String): Resource<Article>
    suspend fun unpublishArticle(id: String): Resource<Article>
    suspend fun deleteArticle(id: String): Resource<Article>
}
