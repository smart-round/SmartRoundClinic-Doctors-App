package ke.co.smartroundclinic.doctor.domain.usecase.wallet

import ke.co.smartroundclinic.doctor.common.Resource
import ke.co.smartroundclinic.doctor.domain.model.WithdrawResult
import ke.co.smartroundclinic.doctor.domain.repository.WalletRepository

class WithdrawUseCase(private val repository: WalletRepository) {
    suspend operator fun invoke(idNumber: String, amount: Double): Resource<WithdrawResult> =
        repository.withdraw(idNumber, amount)
}
