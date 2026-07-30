package ke.co.smartroundclinic.doctor.data.repository

import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.get
import io.ktor.client.request.parameter
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import ke.co.smartroundclinic.doctor.common.Resource
import ke.co.smartroundclinic.doctor.data.remote.dto.request.WithdrawReq
import ke.co.smartroundclinic.doctor.data.remote.dto.response.GetWalletTransactionsRes
import ke.co.smartroundclinic.doctor.data.remote.dto.response.GetPaymentSummaryRes
import ke.co.smartroundclinic.doctor.data.remote.dto.response.GetWithdrawalBalanceRes
import ke.co.smartroundclinic.doctor.data.remote.dto.response.GetWithdrawalByIdRes
import ke.co.smartroundclinic.doctor.data.remote.dto.response.GetWithdrawalHistoryRes
import ke.co.smartroundclinic.doctor.data.remote.dto.response.WithdrawRes
import ke.co.smartroundclinic.doctor.data.remote.dto.response.toDomain
import ke.co.smartroundclinic.doctor.domain.model.PaymentSummary
import ke.co.smartroundclinic.doctor.domain.model.WalletTransaction
import ke.co.smartroundclinic.doctor.domain.model.Withdrawal
import ke.co.smartroundclinic.doctor.domain.model.WithdrawalBalance
import ke.co.smartroundclinic.doctor.domain.model.WithdrawResult
import ke.co.smartroundclinic.doctor.domain.repository.WalletRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.withContext

class WalletRepositoryImpl(private val client: HttpClient) : WalletRepository {

    override suspend fun getWalletTransactions(page: Int): Resource<List<WalletTransaction>> =
        withContext(Dispatchers.IO) {
            try {
                val res = client.get("doctor/payments") {
                    parameter("page", page)
                }.body<GetWalletTransactionsRes>()
                if (res.status) Resource.Success(res.data?.items?.map { it.toDomain() } ?: emptyList(), res.message)
                else Resource.Error(res.message)
            } catch (e: Exception) {
                Resource.Error(e.message ?: "Failed to load wallet transactions")
            }
        }

    override suspend fun getPaymentSummary(): Resource<PaymentSummary> =
        withContext(Dispatchers.IO) {
            try {
                val res = client.get("doctor/payments/summary").body<GetPaymentSummaryRes>()
                if (res.status && res.data != null) Resource.Success(res.data.toDomain(), res.message)
                else Resource.Error(res.message)
            } catch (e: Exception) {
                Resource.Error(e.message ?: "Failed to load summary")
            }
        }

    override suspend fun getWithdrawalBalance(): Resource<WithdrawalBalance> =
        withContext(Dispatchers.IO) {
            try {
                val res = client.get("doctor/payments/withdraw/balance").body<GetWithdrawalBalanceRes>()
                if (res.status && res.data != null) Resource.Success(res.data.toDomain(), res.message)
                else Resource.Error(res.message)
            } catch (e: Exception) {
                Resource.Error(e.message ?: "Failed to load balance")
            }
        }

    override suspend fun getWithdrawalHistory(page: Int): Resource<List<Withdrawal>> =
        withContext(Dispatchers.IO) {
            try {
                val res = client.get("doctor/payments/withdraw/history") {
                    parameter("page", page)
                }.body<GetWithdrawalHistoryRes>()
                if (res.status) Resource.Success(res.data?.items?.map { it.toDomain() } ?: emptyList(), res.message)
                else Resource.Error(res.message)
            } catch (e: Exception) {
                Resource.Error(e.message ?: "Failed to load withdrawal history")
            }
        }

    override suspend fun getWithdrawalById(id: String): Resource<Withdrawal> =
        withContext(Dispatchers.IO) {
            try {
                val res = client.get("doctor/payments/withdraw/history/$id").body<GetWithdrawalByIdRes>()
                if (res.status && res.data != null) Resource.Success(res.data.toDomain(), res.message)
                else Resource.Error(res.message)
            } catch (e: Exception) {
                Resource.Error(e.message ?: "Failed to load withdrawal")
            }
        }

    override suspend fun withdraw(idNumber: String, amount: Double): Resource<WithdrawResult> =
        withContext(Dispatchers.IO) {
            try {
                val res = client.post("doctor/payments/withdraw") {
                    setBody(WithdrawReq(idNumber = idNumber, amount = amount))
                }.body<WithdrawRes>()
                if (res.status) Resource.Success(res.data?.toDomain(), res.message)
                else Resource.Error(res.message, data = res.data?.toDomain())
            } catch (e: Exception) {
                Resource.Error(e.message ?: "Withdrawal failed")
            }
        }
}
