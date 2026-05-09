package ke.co.smartroundclinic.doctor.domain.usecase.bank

import ke.co.smartroundclinic.doctor.common.Resource
import ke.co.smartroundclinic.doctor.domain.model.Bank
import ke.co.smartroundclinic.doctor.domain.repository.BankLocalRepository
import ke.co.smartroundclinic.doctor.domain.repository.BankRepository

class GetLocalBanksUseCase(
    private val remote: BankRepository,
    private val local: BankLocalRepository
) {
    suspend operator fun invoke(): Resource<List<Bank>> {
        val cached = local.getBanks()
        if (cached.isNotEmpty()) return Resource.Success(cached)
        val result = remote.getLocalBanks()
        result.data?.let { local.saveBanks(it) }
        return result
    }
}
