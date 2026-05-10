package ke.co.smartroundclinic.doctor.data.repository

import ke.co.smartroundclinic.doctor.core.database.dao.DoctorSpecializationDao
import ke.co.smartroundclinic.doctor.core.database.entity.toDomain
import ke.co.smartroundclinic.doctor.core.database.entity.toEntity
import ke.co.smartroundclinic.doctor.domain.model.DoctorSpecialization
import ke.co.smartroundclinic.doctor.domain.repository.DoctorSpecializationLocalRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class DoctorSpecializationLocalRepositoryImpl(
    private val dao: DoctorSpecializationDao,
) : DoctorSpecializationLocalRepository {

    override fun observeSpecialization(): Flow<DoctorSpecialization?> =
        dao.observeSpecialization().map { it?.toDomain() }

    override suspend fun saveSpecialization(specialization: DoctorSpecialization) =
        dao.upsertSpecialization(specialization.toEntity())

    override suspend fun clearSpecializations() = dao.clearSpecializations()
}
