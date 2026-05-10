package ke.co.smartroundclinic.doctor.domain.usecase.articles

import ke.co.smartroundclinic.doctor.common.Resource
import ke.co.smartroundclinic.doctor.domain.model.Article
import ke.co.smartroundclinic.doctor.domain.repository.ArticleLocalRepository
import ke.co.smartroundclinic.doctor.domain.repository.ArticleRepository

class UpdateArticleUseCase(
    private val remote: ArticleRepository,
    private val local: ArticleLocalRepository,
) {
    suspend operator fun invoke(
        id: String,
        title: String?,
        content: String?,
        summary: String?,
        categoryId: String?,
        thumbnailBytes: ByteArray?,
        thumbnailFilename: String?,
    ): Resource<Article> {
        val result = remote.updateArticle(id, title, content, summary, categoryId, thumbnailBytes, thumbnailFilename)
        if (result is Resource.Success) result.data?.let { local.upsertArticle(it, isOwn = true) }
        return result
    }
}
