package ke.co.smartroundclinic.doctor.domain.model

data class WithdrawalBalance(
    val totalNetEarnings: Double,
    val totalWithdrawn: Double,
    val totalPending: Double,
    val totalCompleted: Double,
    val availableBalance: Double,
    val minimumWithdrawal: Double,
)
