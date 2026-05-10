package ke.co.smartroundclinic.doctor.data.repository

import ke.co.smartroundclinic.doctor.core.database.dao.DoctorAvailabilityDao
import ke.co.smartroundclinic.doctor.core.database.entity.toDomain
import ke.co.smartroundclinic.doctor.core.database.entity.toEntity
import ke.co.smartroundclinic.doctor.domain.model.DoctorAvailability
import ke.co.smartroundclinic.doctor.domain.repository.ScheduleLocalRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class ScheduleLocalRepositoryImpl(private val dao: DoctorAvailabilityDao) : ScheduleLocalRepository {
    override fun observeSchedule(): Flow<List<DoctorAvailability>> =
        dao.observeSchedule().map { list -> list.map { it.toDomain() } }

    override suspend fun upsertEntry(entry: DoctorAvailability) =
        dao.upsertEntry(entry.toEntity())

    override suspend fun upsertAll(entries: List<DoctorAvailability>) =
        dao.upsertEntries(entries.map { it.toEntity() })

    override suspend fun clearSchedule() = dao.clearSchedule()
}
