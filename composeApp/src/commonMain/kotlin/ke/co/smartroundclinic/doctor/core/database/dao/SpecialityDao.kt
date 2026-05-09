package ke.co.smartroundclinic.doctor.core.database.dao

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Upsert
import ke.co.smartroundclinic.doctor.core.database.entity.SpecialityEntity

@Dao
interface SpecialityDao {
    @Query("SELECT * FROM specialities")
    suspend fun getAll(): List<SpecialityEntity>

    @Upsert
    suspend fun upsertAll(specialities: List<SpecialityEntity>)

    @Query("DELETE FROM specialities")
    suspend fun deleteAll()
}
