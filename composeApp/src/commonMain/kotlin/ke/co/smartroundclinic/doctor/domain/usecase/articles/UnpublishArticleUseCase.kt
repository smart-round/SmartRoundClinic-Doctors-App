package ke.co.smartroundclinic.doctor.domain.usecase.articles

import ke.co.smartroundclinic.doctor.common.Resource
import ke.co.smartroundclinic.doctor.domain.model.Article
import ke.co.smartroundclinic.doctor.domain.repository.ArticleLocalRepository
import ke.co.smartroundclinic.doctor.domain.repository.ArticleRepository

class UnpublishArticleUseCase(
    private val remote: ArticleRepository,
    private val local: ArticleLocalRepository,
) {
    suspend operator fun invoke(id: String): Resource<Article> {
        val result = remote.unpublishArticle(id)
        if (result is Resource.Success) result.data?.let { local.upsertArticle(it, isOwn = true) }
        return result
    }
}
