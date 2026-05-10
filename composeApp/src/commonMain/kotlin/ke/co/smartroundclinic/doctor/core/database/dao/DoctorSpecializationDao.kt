package ke.co.smartroundclinic.doctor.core.database.dao

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Upsert
import ke.co.smartroundclinic.doctor.core.database.entity.DoctorSpecializationEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface DoctorSpecializationDao {
    @Query("SELECT * FROM doctor_specializations LIMIT 1")
    fun observeSpecialization(): Flow<DoctorSpecializationEntity?>

    @Upsert
    suspend fun upsertSpecialization(entity: DoctorSpecializationEntity)

    @Query("DELETE FROM doctor_specializations")
    suspend fun clearSpecializations()
}
