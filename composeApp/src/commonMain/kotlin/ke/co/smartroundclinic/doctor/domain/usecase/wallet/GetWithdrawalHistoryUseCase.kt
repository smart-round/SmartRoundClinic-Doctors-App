package ke.co.smartroundclinic.doctor.domain.usecase.wallet

import ke.co.smartroundclinic.doctor.common.Resource
import ke.co.smartroundclinic.doctor.domain.model.Withdrawal
import ke.co.smartroundclinic.doctor.domain.repository.WalletRepository

class GetWithdrawalHistoryUseCase(private val repository: WalletRepository) {
    suspend operator fun invoke(page: Int = 1): Resource<List<Withdrawal>> =
        repository.getWithdrawalHistory(page)
}
