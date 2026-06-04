package ke.co.smartroundclinic.doctor.data.remote.dto.response

import ke.co.smartroundclinic.doctor.domain.model.DoctorPayment
import ke.co.smartroundclinic.doctor.domain.model.PaymentSummary
import ke.co.smartroundclinic.doctor.domain.model.WithdrawalBalance
import kotlinx.serialization.Serializable

// ── GET /doctor/payments ──────────────────────────────────────────────────────

@Serializable
data class GetDoctorPaymentsRes(
    val httpStatusCode: Int,
    val status: Boolean,
    val message: String,
    val data: DoctorPaymentsPageData? = null,
)

@Serializable
data class DoctorPaymentsPageData(
    val items: List<DoctorPaymentItemRes>,
    val total: Long = 0,
    val page: Int = 1,
    val size: Int = 20,
    val pages: Long = 0,
)

@Serializable
data class DoctorPaymentItemRes(
    val id: String,
    val appointmentId: String? = null,
    val patientId: String,
    val doctorId: String,
    val amount: Double,
    val currency: String,
    val status: String,
    val paymentMethod: String? = null,
    val transactionRef: String? = null,
    val invoiceId: String? = null,
    val notes: String? = null,
    val commissionRate: Double = 0.0,
    val platformFee: Double,
    val netEarnings: Double,
    val createdAt: String,
    val updatedAt: String? = null,
)

fun DoctorPaymentItemRes.toDomain() = DoctorPayment(
    id = id,
    appointmentId = appointmentId,
    amount = amount,
    currency = currency,
    status = status,
    paymentMethod = paymentMethod,
    transactionRef = transactionRef,
    invoiceId = invoiceId,
    notes = notes,
    commissionRate = commissionRate,
    platformFee = platformFee,
    netEarnings = netEarnings,
    createdAt = createdAt,
    updatedAt = updatedAt,
)

// ── GET /doctor/payments/summary ──────────────────────────────────────────────

@Serializable
data class GetPaymentSummaryRes(
    val httpStatusCode: Int,
    val status: Boolean,
    val message: String,
    val data: PaymentSummaryData? = null,
)

@Serializable
data class PaymentSummaryData(
    val totalGross: Double,
    val totalPlatformFees: Double,
    val totalNetEarnings: Double,
    val totalPendingPayments: Double = 0.0,
    val completedCount: Int,
    val pendingCount: Int,
    val totalTransactions: Int,
    val totalWithdrawn: Double,
    val totalPendingWithdrawals: Double,
    val totalCompletedWithdrawals: Double = 0.0,
    val availableBalance: Double,
)

fun PaymentSummaryData.toDomain() = PaymentSummary(
    totalGross = totalGross,
    totalPlatformFees = totalPlatformFees,
    totalNetEarnings = totalNetEarnings,
    completedCount = completedCount,
    pendingCount = pendingCount,
    totalTransactions = totalTransactions,
    totalWithdrawn = totalWithdrawn,
    totalPendingWithdrawals = totalPendingWithdrawals,
    availableBalance = availableBalance,
)

// ── GET /doctor/payments/withdraw/history ────────────────────────────────────

@Serializable
data class GetWithdrawalHistoryRes(
    val httpStatusCode: Int,
    val status: Boolean,
    val message: String,
    val data: WithdrawalHistoryPageData? = null,
)

@Serializable
data class WithdrawalHistoryPageData(
    val items: List<WithdrawalItemRes>,
    val total: Long = 0,
    val page: Int = 1,
    val size: Int = 20,
    val pages: Long = 0,
)

@Serializable
data class WithdrawalItemRes(
    val id: String,
    val doctorId: String,
    val amount: Double,
    val currency: String,
    val trackingId: String,
    val status: String,
    val provider: String,
    val platformCommission: Double,
    val createdAt: String,
    val updatedAt: String? = null,
)

fun WithdrawalItemRes.toDomain() = ke.co.smartroundclinic.doctor.domain.model.Withdrawal(
    id = id,
    amount = amount,
    currency = currency,
    trackingId = trackingId,
    status = status,
    provider = provider,
    platformCommission = platformCommission,
    createdAt = createdAt,
    updatedAt = updatedAt,
)

// ── GET /doctor/payments/withdraw/history/{id} ───────────────────────────────

@Serializable
data class GetWithdrawalByIdRes(
    val httpStatusCode: Int,
    val status: Boolean,
    val message: String,
    val data: WithdrawalItemRes? = null,
)

// ── GET /doctor/payments/withdraw/balance ─────────────────────────────────────

@Serializable
data class GetWithdrawalBalanceRes(
    val httpStatusCode: Int,
    val status: Boolean,
    val message: String,
    val data: WithdrawalBalanceData? = null,
)

@Serializable
data class WithdrawalBalanceData(
    val totalNetEarnings: Double,
    val totalWithdrawn: Double,
    val totalPending: Double,
    val totalCompleted: Double,
    val availableBalance: Double,
    val minimumWithdrawal: Double = 100.0,
)

fun WithdrawalBalanceData.toDomain() = WithdrawalBalance(
    totalNetEarnings = totalNetEarnings,
    totalWithdrawn = totalWithdrawn,
    totalPending = totalPending,
    totalCompleted = totalCompleted,
    availableBalance = availableBalance,
    minimumWithdrawal = minimumWithdrawal,
)
