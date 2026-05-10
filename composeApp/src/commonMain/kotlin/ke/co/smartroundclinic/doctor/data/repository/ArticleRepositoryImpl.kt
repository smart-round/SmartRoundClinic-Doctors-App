package ke.co.smartroundclinic.doctor.data.repository

import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.delete
import io.ktor.client.request.forms.MultiPartFormDataContent
import io.ktor.client.request.forms.formData
import io.ktor.client.request.get
import io.ktor.client.request.parameter
import io.ktor.client.request.patch
import io.ktor.client.request.post
import io.ktor.client.request.put
import io.ktor.client.request.setBody
import io.ktor.http.Headers
import io.ktor.http.HttpHeaders
import ke.co.smartroundclinic.doctor.common.Resource
import ke.co.smartroundclinic.doctor.data.remote.dto.response.ArticleListRes
import ke.co.smartroundclinic.doctor.data.remote.dto.response.ArticleRes
import ke.co.smartroundclinic.doctor.data.remote.dto.response.CategoryListRes
import ke.co.smartroundclinic.doctor.data.remote.dto.response.toDomain
import ke.co.smartroundclinic.doctor.domain.model.Article
import ke.co.smartroundclinic.doctor.domain.model.ArticleCategory
import ke.co.smartroundclinic.doctor.domain.repository.ArticleRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.withContext

class ArticleRepositoryImpl(private val client: HttpClient) : ArticleRepository {

    override suspend fun getCategories(): Resource<List<ArticleCategory>> = withContext(Dispatchers.IO) {
        try {
            val res = client.get("article/categories/all") {
                parameter("size", 100)
            }.body<CategoryListRes>()
            Resource.Success(res.data.items.filter { it.isActive }.map { it.toDomain() })
        } catch (e: Exception) {
            Resource.Error(e.message ?: "Failed to load categories")
        }
    }

    override suspend fun getMyArticles(): Resource<List<Article>> = withContext(Dispatchers.IO) {
        try {
            val res = client.get("article/my") {
                parameter("size", 100)
            }.body<ArticleListRes>()
            Resource.Success(res.data.items.map { it.toDomain() })
        } catch (e: Exception) {
            Resource.Error(e.message ?: "Failed to load articles")
        }
    }

    override suspend fun getLiveArticles(): Resource<List<Article>> = withContext(Dispatchers.IO) {
        try {
            val res = client.get("article/live") {
                parameter("size", 100)
            }.body<ArticleListRes>()
            Resource.Success(res.data.items.map { it.toDomain() })
        } catch (e: Exception) {
            Resource.Error(e.message ?: "Failed to load live articles")
        }
    }

    override suspend fun createArticle(
        title: String,
        content: String,
        summary: String,
        categoryId: String,
        thumbnailBytes: ByteArray?,
        thumbnailFilename: String?,
    ): Resource<Article> = withContext(Dispatchers.IO) {
        try {
            val res = client.post("article") {
                setBody(MultiPartFormDataContent(formData {
                    append("title", title)
                    append("content", content)
                    append("summary", summary)
                    append("categoryId", categoryId)
                    if (thumbnailBytes != null && thumbnailFilename != null) {
                        append(
                            key = "thumbnail",
                            value = thumbnailBytes,
                            headers = Headers.build {
                                append(HttpHeaders.ContentDisposition, "form-data; name=\"thumbnail\"; filename=\"$thumbnailFilename\"")
                                append(HttpHeaders.ContentType, mimeTypeFromFilename(thumbnailFilename))
                            },
                        )
                    }
                }))
            }.body<ArticleRes>()
            Resource.Success(res.data.toDomain())
        } catch (e: Exception) {
            Resource.Error(e.message ?: "Failed to create article")
        }
    }

    override suspend fun updateArticle(
        id: String,
        title: String?,
        content: String?,
        summary: String?,
        categoryId: String?,
        thumbnailBytes: ByteArray?,
        thumbnailFilename: String?,
    ): Resource<Article> = withContext(Dispatchers.IO) {
        try {
            val res = client.put("article") {
                parameter("id", id)
                setBody(MultiPartFormDataContent(formData {
                    title?.let { append("title", it) }
                    content?.let { append("content", it) }
                    summary?.let { append("summary", it) }
                    categoryId?.let { append("categoryId", it) }
                    if (thumbnailBytes != null && thumbnailFilename != null) {
                        append(
                            key = "thumbnail",
                            value = thumbnailBytes,
                            headers = Headers.build {
                                append(HttpHeaders.ContentDisposition, "form-data; name=\"thumbnail\"; filename=\"$thumbnailFilename\"")
                                append(HttpHeaders.ContentType, mimeTypeFromFilename(thumbnailFilename))
                            },
                        )
                    }
                }))
            }.body<ArticleRes>()
            Resource.Success(res.data.toDomain())
        } catch (e: Exception) {
            Resource.Error(e.message ?: "Failed to update article")
        }
    }

    override suspend fun publishArticle(id: String): Resource<Article> = withContext(Dispatchers.IO) {
        try {
            val res = client.patch("article/my/publish") {
                parameter("id", id)
            }.body<ArticleRes>()
            Resource.Success(res.data.toDomain())
        } catch (e: Exception) {
            Resource.Error(e.message ?: "Failed to publish article")
        }
    }

    override suspend fun unpublishArticle(id: String): Resource<Article> = withContext(Dispatchers.IO) {
        try {
            val res = client.patch("article/my/unpublish") {
                parameter("id", id)
            }.body<ArticleRes>()
            Resource.Success(res.data.toDomain())
        } catch (e: Exception) {
            Resource.Error(e.message ?: "Failed to unpublish article")
        }
    }

    override suspend fun deleteArticle(id: String): Resource<Article> = withContext(Dispatchers.IO) {
        try {
            val res = client.delete("article") {
                parameter("id", id)
            }.body<ArticleRes>()
            Resource.Success(res.data.toDomain())
        } catch (e: Exception) {
            Resource.Error(e.message ?: "Failed to delete article")
        }
    }
}

private fun mimeTypeFromFilename(name: String): String = when (name.substringAfterLast('.', "").lowercase()) {
    "pdf" -> "application/pdf"
    "jpg", "jpeg" -> "image/jpeg"
    "png" -> "image/png"
    "webp" -> "image/webp"
    "docx" -> "application/vnd.openxmlformats-officedocument.wordprocessingml.document"
    else -> "application/octet-stream"
}
