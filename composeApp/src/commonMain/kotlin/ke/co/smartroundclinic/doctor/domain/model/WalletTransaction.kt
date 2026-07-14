package ke.co.smartroundclinic.doctor.domain.model

data class WalletTransaction(
    val transactionId: String,
    val invoice: String?,
    val currency: String,
    val value: Double,
    val runningBalance: Double,
    val narrative: String?,
    val transType: String,
    val status: String,
    val createdAt: String,
    val updatedAt: String?,
)
