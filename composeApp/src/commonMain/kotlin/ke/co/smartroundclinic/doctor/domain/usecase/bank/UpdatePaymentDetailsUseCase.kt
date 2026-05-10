package ke.co.smartroundclinic.doctor.domain.usecase.bank

import ke.co.smartroundclinic.doctor.common.Resource
import ke.co.smartroundclinic.doctor.data.remote.dto.request.UpdatePaymentDetailsReq
import ke.co.smartroundclinic.doctor.data.remote.dto.response.toDomain
import ke.co.smartroundclinic.doctor.domain.model.PaymentDetails
import ke.co.smartroundclinic.doctor.domain.repository.BankRepository
import ke.co.smartroundclinic.doctor.domain.repository.PaymentDetailsLocalRepository

class UpdatePaymentDetailsUseCase(
    private val bankRepository: BankRepository,
    private val localRepository: PaymentDetailsLocalRepository,
) {
    suspend operator fun invoke(body: UpdatePaymentDetailsReq): Resource<PaymentDetails> {
        val result = bankRepository.updatePaymentDetails(body)
        return when (result) {
            is Resource.Success -> {
                val details = result.data?.data?.toDomain()
                details?.let { localRepository.savePaymentDetails(it) }
                Resource.Success(data = details, message = result.message ?: "Success")
            }
            is Resource.Error -> Resource.Error(result.message ?: "Failed")
            is Resource.Loading -> Resource.Loading()
        }
    }
}
