package ke.co.smartroundclinic.doctor.data.repository

import ke.co.smartroundclinic.doctor.core.database.dao.SpecialityDao
import ke.co.smartroundclinic.doctor.core.database.entity.toDomain
import ke.co.smartroundclinic.doctor.core.database.entity.toEntity
import ke.co.smartroundclinic.doctor.domain.model.Speciality
import ke.co.smartroundclinic.doctor.domain.repository.SpecialityLocalRepository

class SpecialityLocalRepositoryImpl(private val dao: SpecialityDao) : SpecialityLocalRepository {

    override suspend fun getSpecialities(): List<Speciality> = dao.getAll().map { it.toDomain() }

    override suspend fun saveSpecialities(specialities: List<Speciality>) =
        dao.upsertAll(specialities.map { it.toEntity() })

    override suspend fun hasSpecialities(): Boolean = dao.getAll().isNotEmpty()

    override suspend fun clearAll() = dao.deleteAll()
}
