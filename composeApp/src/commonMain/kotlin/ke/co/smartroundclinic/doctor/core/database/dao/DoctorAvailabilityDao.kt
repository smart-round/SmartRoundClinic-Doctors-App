package ke.co.smartroundclinic.doctor.core.database.dao

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Upsert
import ke.co.smartroundclinic.doctor.core.database.entity.DoctorAvailabilityEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface DoctorAvailabilityDao {
    @Query("SELECT * FROM doctor_availability ORDER BY dayOfWeek")
    fun observeSchedule(): Flow<List<DoctorAvailabilityEntity>>

    @Query("SELECT * FROM doctor_availability ORDER BY dayOfWeek")
    suspend fun getSchedule(): List<DoctorAvailabilityEntity>

    @Upsert
    suspend fun upsertEntries(entries: List<DoctorAvailabilityEntity>)

    @Upsert
    suspend fun upsertEntry(entry: DoctorAvailabilityEntity)

    @Query("DELETE FROM doctor_availability")
    suspend fun clearSchedule()
}
