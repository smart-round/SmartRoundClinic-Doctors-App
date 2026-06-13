package ke.co.smartroundclinic.doctor.domain.usecase.articles

import ke.co.smartroundclinic.doctor.common.Resource
import ke.co.smartroundclinic.doctor.domain.repository.ArticleLocalRepository
import ke.co.smartroundclinic.doctor.domain.repository.ArticleRepository

class GetMyArticlesUseCase(
    private val remote: ArticleRepository,
    private val local: ArticleLocalRepository,
) {
    suspend operator fun invoke(forceRefresh: Boolean = false): Resource<Unit> {
        if (forceRefresh) local.clearMyArticles()
        val result = remote.getMyArticles()
        if (result is Resource.Success) {
            result.data?.let { local.upsertMyArticles(it) }
            return Resource.Success(Unit)
        }
        return Resource.Error((result as Resource.Error).message ?: "Failed to load articles")
    }
}
