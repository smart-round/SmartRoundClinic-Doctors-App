package ke.co.smartroundclinic.doctor.domain.repository

import ke.co.smartroundclinic.doctor.common.Resource
import ke.co.smartroundclinic.doctor.domain.model.Bank

interface BankRepository {
    suspend fun getLocalBanks(): Resource<List<Bank>>
}
