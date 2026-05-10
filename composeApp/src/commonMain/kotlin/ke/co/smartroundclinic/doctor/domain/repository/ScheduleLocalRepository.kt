package ke.co.smartroundclinic.doctor.domain.repository

import ke.co.smartroundclinic.doctor.domain.model.DoctorAvailability
import kotlinx.coroutines.flow.Flow

interface ScheduleLocalRepository {
    fun observeSchedule(): Flow<List<DoctorAvailability>>
    suspend fun upsertEntry(entry: DoctorAvailability)
    suspend fun upsertAll(entries: List<DoctorAvailability>)
    suspend fun clearSchedule()
}
