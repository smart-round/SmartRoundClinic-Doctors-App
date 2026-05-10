package ke.co.smartroundclinic.doctor.domain.model

data class PaymentDetails(
    val id: String,
    val doctorId: String,
    val accountName: String,
    val accountNumber: String,
    val bankCode: String,
    val bankName: String,
    val branchCode: String,
    val branchName: String,
    val createdAt: String,
)
