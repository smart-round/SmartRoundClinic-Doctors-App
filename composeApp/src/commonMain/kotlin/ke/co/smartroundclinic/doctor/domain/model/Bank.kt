package ke.co.smartroundclinic.doctor.domain.model

data class Bank(
    val id: String,
    val bankCode: String,
    val bankName: String,
    val branches: List<BankBranch>
)

data class BankBranch(
    val branchCode: String,
    val branchName: String
)
