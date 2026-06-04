package ke.co.smartroundclinic.doctor.domain.usecase.wallet

import ke.co.smartroundclinic.doctor.common.Resource
import ke.co.smartroundclinic.doctor.domain.model.WithdrawalBalance
import ke.co.smartroundclinic.doctor.domain.repository.WalletRepository

class GetWithdrawalBalanceUseCase(private val repository: WalletRepository) {
    suspend operator fun invoke(): Resource<WithdrawalBalance> = repository.getWithdrawalBalance()
}
