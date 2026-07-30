package ke.co.smartroundclinic.doctor.domain.model

data class WithdrawResult(
    val trackingId: String?,
    val status: String?,
    val insufficientBalance: InsufficientBalance?,
)

data class InsufficientBalance(
    val requestedAmount: Double,
    val feeEstimate: Double,
    val totalRequired: Double,
    val availableBalance: Double,
)
