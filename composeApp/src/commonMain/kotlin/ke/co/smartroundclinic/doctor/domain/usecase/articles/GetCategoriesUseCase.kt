package ke.co.smartroundclinic.doctor.domain.usecase.articles

import ke.co.smartroundclinic.doctor.domain.repository.ArticleCategoryLocalRepository
import ke.co.smartroundclinic.doctor.domain.repository.ArticleRepository

class GetCategoriesUseCase(
    private val remote: ArticleRepository,
    private val local: ArticleCategoryLocalRepository,
) {
    suspend operator fun invoke() {
        if (local.getActiveCategories().isEmpty()) {
            val result = remote.getCategories()
            result.data?.let { local.upsertCategories(it) }
        }
    }
}
