package ke.co.smartroundclinic.doctor.domain.model

data class PaymentSummary(
    val totalGross: Double,
    val totalPlatformFees: Double,
    val totalNetEarnings: Double,
    val completedCount: Int,
    val pendingCount: Int,
    val totalTransactions: Int,
    val totalWithdrawn: Double,
    val totalPendingWithdrawals: Double,
    val availableBalance: Double,
)
