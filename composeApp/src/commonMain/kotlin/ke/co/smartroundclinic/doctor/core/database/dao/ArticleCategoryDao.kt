package ke.co.smartroundclinic.doctor.core.database.dao

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Upsert
import ke.co.smartroundclinic.doctor.core.database.entity.ArticleCategoryEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface ArticleCategoryDao {
    @Query("SELECT * FROM article_categories WHERE isActive = 1 ORDER BY name ASC")
    fun observeActiveCategories(): Flow<List<ArticleCategoryEntity>>

    @Query("SELECT * FROM article_categories WHERE isActive = 1 ORDER BY name ASC")
    suspend fun getActiveCategories(): List<ArticleCategoryEntity>

    @Upsert
    suspend fun upsertCategories(categories: List<ArticleCategoryEntity>)

    /**
     * The category list is a mirror of the backend's, not an accumulation. Upserting alone kept
     * rows from every environment the app had ever pointed at, and picking one of those stale
     * categories made `POST /article` fail with a 404 — the server rejects a categoryId it has no
     * record of. Replacing wholesale keeps the picker to categories that actually exist.
     *
     * A refresh that comes back empty is treated as "nothing to mirror" and leaves the cache alone,
     * so a failed or empty fetch can never blank out the picker.
     */
    @Transaction
    suspend fun replaceCategories(categories: List<ArticleCategoryEntity>) {
        if (categories.isEmpty()) return
        deleteAll()
        upsertCategories(categories)
    }

    @Query("DELETE FROM article_categories")
    suspend fun deleteAll()
}
