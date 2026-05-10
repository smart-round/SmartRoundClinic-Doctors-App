package ke.co.smartroundclinic.doctor.domain.usecase.bank

import ke.co.smartroundclinic.doctor.common.Resource
import ke.co.smartroundclinic.doctor.data.remote.dto.request.AddPaymentDetailsReq
import ke.co.smartroundclinic.doctor.data.remote.dto.response.SuccessRes
import ke.co.smartroundclinic.doctor.domain.repository.BankRepository

class AddPaymentDetailsUseCase(private val bankRepository: BankRepository) {
    suspend operator fun invoke(body: AddPaymentDetailsReq): Resource<SuccessRes> =
        bankRepository.addPaymentDetails(body)
}
