package ke.co.smartroundclinic.doctor.domain.usecase.wallet

import ke.co.smartroundclinic.doctor.common.Resource
import ke.co.smartroundclinic.doctor.domain.model.Withdrawal
import ke.co.smartroundclinic.doctor.domain.repository.WalletRepository

class GetWithdrawalByIdUseCase(private val repository: WalletRepository) {
    suspend operator fun invoke(id: String): Resource<Withdrawal> = repository.getWithdrawalById(id)
}
