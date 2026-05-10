package ke.co.smartroundclinic.doctor.data.repository

import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.delete
import io.ktor.client.request.get
import io.ktor.client.request.post
import io.ktor.client.request.put
import io.ktor.client.request.setBody
import ke.co.smartroundclinic.doctor.common.Resource
import ke.co.smartroundclinic.doctor.data.remote.dto.request.AddPaymentDetailsReq
import ke.co.smartroundclinic.doctor.data.remote.dto.request.UpdatePaymentDetailsReq
import ke.co.smartroundclinic.doctor.data.remote.dto.response.GetPaymentDetailsRes
import ke.co.smartroundclinic.doctor.data.remote.dto.response.SuccessRes
import ke.co.smartroundclinic.doctor.data.remote.dto.response.UpdatePaymentDetailsRes
import ke.co.smartroundclinic.doctor.data.remote.dto.response.localBanks.GetLocalBanksRes
import ke.co.smartroundclinic.doctor.data.remote.dto.response.localBanks.toModel
import ke.co.smartroundclinic.doctor.domain.model.Bank
import ke.co.smartroundclinic.doctor.domain.repository.BankRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.withContext

class BankRepositoryImpl(private val client: HttpClient) : BankRepository {

    override suspend fun getLocalBanks(): Resource<List<Bank>> = withContext(Dispatchers.IO) {
        try {
            Resource.Success(client.get("banks").body<GetLocalBanksRes>().toModel())
        } catch (e: Exception) {
            Resource.Error(e.message ?: "An unknown error occurred")
        }
    }

    override suspend fun getPaymentDetails(): Resource<GetPaymentDetailsRes> = withContext(
        Dispatchers.IO) {
        try {
            val response = client.get("doctor/payment-details").body<GetPaymentDetailsRes>()
            Resource.Success(data = response, message = response.message)
        } catch (e: Exception) {
            Resource.Error(e.message ?: "An unknown error occurred")
        }
    }

    override suspend fun updatePaymentDetails(body: UpdatePaymentDetailsReq): Resource<UpdatePaymentDetailsRes> = withContext(
        Dispatchers.IO) {
        try {
            val response = client.put("doctor/payment-details"){
                setBody(body)
            }.body<UpdatePaymentDetailsRes>()
            Resource.Success(data = response, message = response.message)
        } catch (e: Exception) {
            Resource.Error(e.message ?: "An unknown error occurred")
        }
    }

    override suspend fun addPaymentDetails(body: AddPaymentDetailsReq): Resource<SuccessRes> = withContext(Dispatchers.IO) {
        try {
            val response = client.post("doctor/payment-details"){
                setBody(body)
            }.body<SuccessRes>()
            Resource.Success(data = response, message = response.message)
        } catch (e: Exception) {
            Resource.Error(e.message ?: "An unknown error occurred")
        }
    }

    override suspend fun deletePaymentDetails(): Resource<SuccessRes> = withContext(Dispatchers.IO) {
        try {
            val response = client.delete("doctor/payment-details").body<SuccessRes>()
            Resource.Success(data = response, message = response.message)
        } catch (e: Exception) {
            Resource.Error(e.message ?: "An unknown error occurred")
        }
    }

}
