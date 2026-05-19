package ke.co.smartroundclinic.doctor.data.remote.dto.response

import ke.co.smartroundclinic.doctor.domain.model.ComplianceStatus
import kotlinx.serialization.Serializable

@Serializable
data class GetComplianceStatusRes(
    val `data`: ComplianceData,
    val httpStatusCode: Int, // 200
    val message: String, // Success
    val status: Boolean // true
)

@Serializable
data class ComplianceData(
    val createdAt: String, // 2026-05-16T17:19:07.418018403Z
    val doctorId: String, // 6a08a70a8751f92f85e39bae
    val id: String, // 6a08a70b8751f92f85e39bb6
    val isApproved: Boolean, // false
    val status: String, // PENDING, REJECTED, APPROVED
    val failedApprovalReason: String? = null
)

fun ComplianceData.toDomain() = ComplianceStatus(
    isApproved = isApproved,
    status = status,
    failedApprovalReason = failedApprovalReason,
)