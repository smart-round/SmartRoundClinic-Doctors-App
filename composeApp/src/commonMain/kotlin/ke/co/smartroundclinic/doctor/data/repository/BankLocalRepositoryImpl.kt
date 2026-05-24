package ke.co.smartroundclinic.doctor.data.repository

import ke.co.smartroundclinic.doctor.core.database.dao.BankDao
import ke.co.smartroundclinic.doctor.core.database.entity.toDomain
import ke.co.smartroundclinic.doctor.core.database.entity.toEntity
import ke.co.smartroundclinic.doctor.domain.model.Bank
import ke.co.smartroundclinic.doctor.domain.repository.BankLocalRepository

class BankLocalRepositoryImpl(private val dao: BankDao) : BankLocalRepository {

    override suspend fun getBanks(): List<Bank> = dao.getAll().map { it.toDomain() }

    override suspend fun searchBanks(query: String): List<Bank> = dao.search(query).map { it.toDomain() }

    override suspend fun saveBanks(banks: List<Bank>) = dao.upsertAll(banks.map { it.toEntity() })

    override suspend fun hasBanks(): Boolean = dao.getAll().isNotEmpty()

    override suspend fun clearAll() = dao.deleteAll()
}
