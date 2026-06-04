package ke.co.smartroundclinic.doctor.domain.repository

import ke.co.smartroundclinic.doctor.common.Resource
import ke.co.smartroundclinic.doctor.domain.model.DoctorPayment
import ke.co.smartroundclinic.doctor.domain.model.PaymentSummary
import ke.co.smartroundclinic.doctor.domain.model.Withdrawal
import ke.co.smartroundclinic.doctor.domain.model.WithdrawalBalance

interface WalletRepository {
    suspend fun getPayments(page: Int = 1, size: Int = 20): Resource<List<DoctorPayment>>
    suspend fun getPaymentSummary(): Resource<PaymentSummary>
    suspend fun getWithdrawalBalance(): Resource<WithdrawalBalance>
    suspend fun withdraw(idNumber: String, amount: Double): Resource<Unit>
    suspend fun getWithdrawalHistory(page: Int = 1, size: Int = 20): Resource<List<Withdrawal>>
    suspend fun getWithdrawalById(id: String): Resource<Withdrawal>
}
