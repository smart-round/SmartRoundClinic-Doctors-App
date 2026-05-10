package ke.co.smartroundclinic.doctor.core.database.dao

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Upsert
import ke.co.smartroundclinic.doctor.core.database.entity.AppointmentEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface AppointmentDao {
    @Query("SELECT * FROM appointments ORDER BY date DESC, slotStart ASC")
    fun observeAppointments(): Flow<List<AppointmentEntity>>

    @Query("SELECT * FROM appointments ORDER BY date DESC, slotStart ASC")
    suspend fun getAppointments(): List<AppointmentEntity>

    @Upsert
    suspend fun upsertAppointments(appointments: List<AppointmentEntity>)

    @Upsert
    suspend fun upsertAppointment(appointment: AppointmentEntity)

    @Query("DELETE FROM appointments")
    suspend fun clearAppointments()
}
