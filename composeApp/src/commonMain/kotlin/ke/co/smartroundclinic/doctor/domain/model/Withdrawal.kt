package ke.co.smartroundclinic.doctor.domain.model

data class Withdrawal(
    val id: String,
    val amount: Double,
    val currency: String,
    val trackingId: String,
    val status: String,
    val provider: String,
    val platformCommission: Double,
    val createdAt: String,
    val updatedAt: String?,
)
