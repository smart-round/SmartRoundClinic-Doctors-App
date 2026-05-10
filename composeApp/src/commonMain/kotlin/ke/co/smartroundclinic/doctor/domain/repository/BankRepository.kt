package ke.co.smartroundclinic.doctor.domain.repository

import ke.co.smartroundclinic.doctor.common.Resource
import ke.co.smartroundclinic.doctor.data.remote.dto.request.AddPaymentDetailsReq
import ke.co.smartroundclinic.doctor.data.remote.dto.request.UpdatePaymentDetailsReq
import ke.co.smartroundclinic.doctor.data.remote.dto.response.GetPaymentDetailsRes
import ke.co.smartroundclinic.doctor.data.remote.dto.response.SuccessRes
import ke.co.smartroundclinic.doctor.data.remote.dto.response.UpdatePaymentDetailsRes
import ke.co.smartroundclinic.doctor.domain.model.Bank
import smartroundclinic.composeapp.generated.resources.Res

interface BankRepository {
    suspend fun getLocalBanks(): Resource<List<Bank>>
    suspend fun getPaymentDetails(): Resource<GetPaymentDetailsRes>
    suspend fun updatePaymentDetails(body: UpdatePaymentDetailsReq): Resource<UpdatePaymentDetailsRes>
    suspend fun addPaymentDetails(body: AddPaymentDetailsReq): Resource<SuccessRes>
    suspend fun deletePaymentDetails(): Resource<SuccessRes>
}
