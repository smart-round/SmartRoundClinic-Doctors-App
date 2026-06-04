package ke.co.smartroundclinic.doctor.domain.usecase.wallet

import ke.co.smartroundclinic.doctor.common.Resource
import ke.co.smartroundclinic.doctor.domain.model.DoctorPayment
import ke.co.smartroundclinic.doctor.domain.repository.WalletRepository

class GetDoctorPaymentsUseCase(private val repository: WalletRepository) {
    suspend operator fun invoke(page: Int = 1, size: Int = 20): Resource<List<DoctorPayment>> =
        repository.getPayments(page, size)
}
