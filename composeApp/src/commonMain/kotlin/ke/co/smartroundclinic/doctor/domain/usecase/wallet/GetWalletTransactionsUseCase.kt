package ke.co.smartroundclinic.doctor.domain.usecase.wallet

import ke.co.smartroundclinic.doctor.common.Resource
import ke.co.smartroundclinic.doctor.domain.model.WalletTransaction
import ke.co.smartroundclinic.doctor.domain.repository.WalletRepository

class GetWalletTransactionsUseCase(private val repository: WalletRepository) {
    suspend operator fun invoke(page: Int = 1): Resource<List<WalletTransaction>> =
        repository.getWalletTransactions(page)
}
