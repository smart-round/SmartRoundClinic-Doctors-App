package ke.co.smartroundclinic.doctor.domain.model

data class DoctorPayment(
    val id: String,
    val appointmentId: String?,
    val amount: Double,
    val currency: String,
    val status: String,
    val paymentMethod: String?,
    val transactionRef: String?,
    val invoiceId: String?,
    val notes: String?,
    val commissionRate: Double,
    val platformFee: Double,
    val netEarnings: Double,
    val createdAt: String,
    val updatedAt: String?,
)
