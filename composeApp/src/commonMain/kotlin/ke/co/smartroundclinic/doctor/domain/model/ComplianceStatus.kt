package ke.co.smartroundclinic.doctor.domain.model

data class ComplianceStatus(
    val isApproved: Boolean,
    val status: String,
    val failedApprovalReason: String? = null,
)
