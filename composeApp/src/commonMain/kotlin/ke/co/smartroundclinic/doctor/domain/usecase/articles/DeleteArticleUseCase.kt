package ke.co.smartroundclinic.doctor.domain.usecase.articles

import ke.co.smartroundclinic.doctor.common.Resource
import ke.co.smartroundclinic.doctor.domain.repository.ArticleLocalRepository
import ke.co.smartroundclinic.doctor.domain.repository.ArticleRepository

class DeleteArticleUseCase(
    private val remote: ArticleRepository,
    private val local: ArticleLocalRepository,
) {
    suspend operator fun invoke(id: String): Resource<Unit> {
        val result = remote.deleteArticle(id)
        if (result is Resource.Success) local.removeArticle(id)
        return if (result is Resource.Success) Resource.Success(Unit)
        else Resource.Error((result as Resource.Error).message ?: "Failed to delete article")
    }
}
