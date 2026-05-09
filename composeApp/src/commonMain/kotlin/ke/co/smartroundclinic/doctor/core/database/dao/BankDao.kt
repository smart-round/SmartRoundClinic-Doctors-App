package ke.co.smartroundclinic.doctor.core.database.dao

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Upsert
import ke.co.smartroundclinic.doctor.core.database.entity.BankEntity

@Dao
interface BankDao {
    @Query("SELECT * FROM banks")
    suspend fun getAll(): List<BankEntity>

    @Query("SELECT * FROM banks WHERE bankName LIKE '%' || :query || '%' OR bankCode LIKE '%' || :query || '%'")
    suspend fun search(query: String): List<BankEntity>

    @Upsert
    suspend fun upsertAll(banks: List<BankEntity>)

    @Query("DELETE FROM banks")
    suspend fun deleteAll()
}
