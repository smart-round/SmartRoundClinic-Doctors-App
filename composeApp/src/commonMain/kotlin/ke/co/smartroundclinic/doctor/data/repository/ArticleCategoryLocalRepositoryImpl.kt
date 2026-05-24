package ke.co.smartroundclinic.doctor.data.repository

import ke.co.smartroundclinic.doctor.core.database.dao.ArticleCategoryDao
import ke.co.smartroundclinic.doctor.core.database.entity.toDomain
import ke.co.smartroundclinic.doctor.core.database.entity.toEntity
import ke.co.smartroundclinic.doctor.domain.model.ArticleCategory
import ke.co.smartroundclinic.doctor.domain.repository.ArticleCategoryLocalRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class ArticleCategoryLocalRepositoryImpl(private val dao: ArticleCategoryDao) : ArticleCategoryLocalRepository {
    override fun observeActiveCategories(): Flow<List<ArticleCategory>> =
        dao.observeActiveCategories().map { list -> list.map { it.toDomain() } }

    override suspend fun getActiveCategories(): List<ArticleCategory> =
        dao.getActiveCategories().map { it.toDomain() }

    override suspend fun upsertCategories(categories: List<ArticleCategory>) =
        dao.upsertCategories(categories.map { it.toEntity() })

    override suspend fun clearAll() = dao.deleteAll()
}
