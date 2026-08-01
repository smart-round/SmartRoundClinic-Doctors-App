package ke.co.smartroundclinic.doctor.data.remote.dto.response

import ke.co.smartroundclinic.doctor.domain.model.Referral
import ke.co.smartroundclinic.doctor.domain.model.ReferralEligibility
import kotlinx.serialization.Serializable

@Serializable
data class ReferralRes(
    val data: ReferralData? = null,
    val httpStatusCode: Int = 200,
    val message: String = "",
    val status: Boolean = false,
)

@Serializable
data class ReferralData(
    val id: String,
    val sourceAppointmentId: String,
    val patientId: String,
    val patientName: String? = null,
    val receivingDoctorId: String,
    val receivingDoctorName: String? = null,
    val reason: String,
    val status: String,
    val createdAt: String,
)

@Serializable
data class ReferralEligibilityRes(
    val data: ReferralEligibilityData? = null,
    val httpStatusCode: Int = 200,
    val message: String = "",
    val status: Boolean = false,
)

@Serializable
data class ReferralEligibilityData(
    val eligible: Boolean = false,
    val reasons: List<String> = emptyList(),
)

fun ReferralData.toDomain() = Referral(
    id = id,
    sourceAppointmentId = sourceAppointmentId,
    patientId = patientId,
    patientName = patientName,
    receivingDoctorId = receivingDoctorId,
    receivingDoctorName = receivingDoctorName,
    reason = reason,
    status = status,
    createdAt = createdAt,
)

fun ReferralEligibilityData.toDomain() = ReferralEligibility(eligible = eligible, reasons = reasons)
