package ke.co.smartroundclinic.doctor.domain.repository

import ke.co.smartroundclinic.doctor.domain.model.ArticleCategory
import kotlinx.coroutines.flow.Flow

interface ArticleCategoryLocalRepository {
    fun observeActiveCategories(): Flow<List<ArticleCategory>>
    suspend fun getActiveCategories(): List<ArticleCategory>
    suspend fun upsertCategories(categories: List<ArticleCategory>)
    suspend fun clearAll()
}
