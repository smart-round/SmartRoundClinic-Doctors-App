package ke.co.smartroundclinic.doctor.data.remote.dto.response

import ke.co.smartroundclinic.doctor.domain.model.PaymentSummary
import ke.co.smartroundclinic.doctor.domain.model.WalletTransaction
import ke.co.smartroundclinic.doctor.domain.model.Withdrawal
import ke.co.smartroundclinic.doctor.domain.model.WithdrawalBalance
import kotlinx.serialization.Serializable

// ── GET /doctor/payments — wallet transaction ledger, live from IntaSend ────────

@Serializable
data class GetWalletTransactionsRes(
    val httpStatusCode: Int,
    val status: Boolean,
    val message: String,
    val data: WalletTransactionsPageData? = null,
)

@Serializable
data class WalletTransactionsPageData(
    val items: List<WalletTransactionItemRes>,
    val total: Int = 0,
    val page: Int = 1,
    val hasMore: Boolean = false,
)

@Serializable
data class WalletTransactionItemRes(
    val transactionId: String,
    val invoice: String? = null,
    val currency: String,
    val value: Double,
    val runningBalance: Double,
    val narrative: String? = null,
    val transType: String,
    val status: String,
    val createdAt: String,
    val updatedAt: String? = null,
)

fun WalletTransactionItemRes.toDomain() = WalletTransaction(
    transactionId = transactionId,
    invoice = invoice,
    currency = currency,
    value = value,
    runningBalance = runningBalance,
    narrative = narrative,
    transType = transType,
    status = status,
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

// ── GET /doctor/payments/withdraw/history — live from IntaSend send-money ───────

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
    val total: Int = 0,
    val page: Int = 1,
    val hasMore: Boolean = false,
)

@Serializable
data class WithdrawalItemRes(
    val transactionId: String,
    val status: String,
    val statusCode: String? = null,
    val statusDescription: String? = null,
    val provider: String? = null,
    val bankCode: String? = null,
    val name: String? = null,
    val account: String? = null,
    val amount: String,
    val charge: String? = null,
    val narrative: String? = null,
    val currency: String,
    val createdAt: String,
    val updatedAt: String? = null,
)

fun WithdrawalItemRes.toDomain() = Withdrawal(
    transactionId = transactionId,
    status = status,
    statusCode = statusCode,
    statusDescription = statusDescription,
    provider = provider,
    bankCode = bankCode,
    name = name,
    account = account,
    amount = amount,
    charge = charge,
    narrative = narrative,
    currency = currency,
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
    val currentBalance: Double,
    val minimumWithdrawal: Double = 100.0,
)

fun WithdrawalBalanceData.toDomain() = WithdrawalBalance(
    totalNetEarnings = totalNetEarnings,
    totalWithdrawn = totalWithdrawn,
    totalPending = totalPending,
    totalCompleted = totalCompleted,
    availableBalance = availableBalance,
    currentBalance = currentBalance,
    minimumWithdrawal = minimumWithdrawal,
)
