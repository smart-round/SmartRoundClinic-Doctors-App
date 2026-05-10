package ke.co.smartroundclinic.doctor.domain.usecase.articles

import ke.co.smartroundclinic.doctor.common.Resource
import ke.co.smartroundclinic.doctor.domain.repository.ArticleLocalRepository
import ke.co.smartroundclinic.doctor.domain.repository.ArticleRepository

class GetLiveArticlesUseCase(
    private val remote: ArticleRepository,
    private val local: ArticleLocalRepository,
) {
    suspend operator fun invoke(): Resource<Unit> {
        val result = remote.getLiveArticles()
        if (result is Resource.Success) {
            result.data?.let { local.upsertLiveArticles(it) }
            return Resource.Success(Unit)
        }
        return Resource.Error((result as Resource.Error).message ?: "Failed to load live articles")
    }
}
