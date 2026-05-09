package ke.co.smartroundclinic.doctor.domain.repository

import ke.co.smartroundclinic.doctor.domain.model.Bank

interface BankLocalRepository {
    suspend fun getBanks(): List<Bank>
    suspend fun searchBanks(query: String): List<Bank>
    suspend fun saveBanks(banks: List<Bank>)
    suspend fun hasBanks(): Boolean
}
