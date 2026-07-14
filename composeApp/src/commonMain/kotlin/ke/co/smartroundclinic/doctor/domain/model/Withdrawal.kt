package ke.co.smartroundclinic.doctor.domain.model

data class Withdrawal(
    val transactionId: String,
    val status: String,
    val statusCode: String?,
    val statusDescription: String?,
    val provider: String?,
    val bankCode: String?,
    val name: String?,
    val account: String?,
    val amount: String,
    val charge: String?,
    val narrative: String?,
    val currency: String,
    val createdAt: String,
    val updatedAt: String?,
)
