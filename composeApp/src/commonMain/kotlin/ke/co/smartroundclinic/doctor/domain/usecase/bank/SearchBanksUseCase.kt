package ke.co.smartroundclinic.doctor.domain.usecase.bank

import ke.co.smartroundclinic.doctor.common.Resource
import ke.co.smartroundclinic.doctor.domain.model.Bank
import ke.co.smartroundclinic.doctor.domain.repository.BankLocalRepository
import ke.co.smartroundclinic.doctor.domain.repository.BankRepository

class SearchBanksUseCase(
    private val remote: BankRepository,
    private val local: BankLocalRepository
) {
    suspend operator fun invoke(query: String): Resource<List<Bank>> {
        if (!local.hasBanks()) {
            val result = remote.getLocalBanks()
            result.data?.let { local.saveBanks(it) }
                ?: return result
        }
        return Resource.Success(local.searchBanks(query))
    }
}
