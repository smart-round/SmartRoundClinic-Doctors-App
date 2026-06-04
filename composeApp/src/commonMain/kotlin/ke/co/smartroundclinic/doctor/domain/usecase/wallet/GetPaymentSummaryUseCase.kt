package ke.co.smartroundclinic.doctor.domain.usecase.wallet

import ke.co.smartroundclinic.doctor.common.Resource
import ke.co.smartroundclinic.doctor.domain.model.PaymentSummary
import ke.co.smartroundclinic.doctor.domain.repository.WalletRepository

class GetPaymentSummaryUseCase(private val repository: WalletRepository) {
    suspend operator fun invoke(): Resource<PaymentSummary> = repository.getPaymentSummary()
}
