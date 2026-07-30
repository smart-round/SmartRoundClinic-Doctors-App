package ke.co.smartroundclinic.doctor.domain.repository

import ke.co.smartroundclinic.doctor.common.Resource
import ke.co.smartroundclinic.doctor.domain.model.PaymentSummary
import ke.co.smartroundclinic.doctor.domain.model.WalletTransaction
import ke.co.smartroundclinic.doctor.domain.model.Withdrawal
import ke.co.smartroundclinic.doctor.domain.model.WithdrawalBalance
import ke.co.smartroundclinic.doctor.domain.model.WithdrawResult

interface WalletRepository {
    suspend fun getWalletTransactions(page: Int = 1): Resource<List<WalletTransaction>>
    suspend fun getPaymentSummary(): Resource<PaymentSummary>
    suspend fun getWithdrawalBalance(): Resource<WithdrawalBalance>
    suspend fun withdraw(idNumber: String, amount: Double): Resource<WithdrawResult>
    suspend fun getWithdrawalHistory(page: Int = 1): Resource<List<Withdrawal>>
    suspend fun getWithdrawalById(id: String): Resource<Withdrawal>
}
