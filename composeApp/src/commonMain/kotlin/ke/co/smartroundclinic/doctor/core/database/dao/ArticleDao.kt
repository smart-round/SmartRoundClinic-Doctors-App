package ke.co.smartroundclinic.doctor.core.database.dao

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Upsert
import ke.co.smartroundclinic.doctor.core.database.entity.ArticleEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface ArticleDao {
    @Query("SELECT * FROM articles WHERE isOwn = 1 AND state != 'DELETED' ORDER BY createdAt DESC")
    fun observeMyArticles(): Flow<List<ArticleEntity>>

    @Query("SELECT * FROM articles WHERE isOwn = 0 AND state = 'LIVE' ORDER BY datePosted DESC")
    fun observeLiveArticles(): Flow<List<ArticleEntity>>

    @Query("SELECT * FROM articles WHERE id = :id LIMIT 1")
    suspend fun getById(id: String): ArticleEntity?

    @Upsert
    suspend fun upsertArticles(articles: List<ArticleEntity>)

    @Upsert
    suspend fun upsertArticle(article: ArticleEntity)

    @Query("DELETE FROM articles WHERE isOwn = 1")
    suspend fun clearMyArticles()

    @Query("DELETE FROM articles WHERE isOwn = 0")
    suspend fun clearLiveArticles()

    @Query("DELETE FROM articles WHERE id = :id")
    suspend fun deleteById(id: String)
}
