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
data class ReferralListRes(
    val data: List<ReferralData>? = null,
    val httpStatusCode: Int = 200,
    val message: String = "",
    val status: Boolean = false,
)

@Serializable
data class ReferralData(
    val id: String,
    val sourceAppointmentId: String,
    val referringDoctorId: String = "",
    val referringDoctorName: String? = null,
    val referringDoctorPicture: String? = null,
    val patientId: String,
    val patientName: String? = null,
    val patientProfilePicture: String? = null,
    val receivingDoctorId: String,
    val receivingDoctorName: String? = null,
    val receivingDoctorPicture: String? = null,
    val reason: String,
    val status: String,
    val resultingAppointmentId: String? = null,
    val createdAt: String,
    val respondedAt: String? = null,
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
    referringDoctorId = referringDoctorId,
    referringDoctorName = referringDoctorName,
    referringDoctorPicture = referringDoctorPicture,
    patientId = patientId,
    patientName = patientName,
    patientProfilePicture = patientProfilePicture,
    receivingDoctorId = receivingDoctorId,
    receivingDoctorName = receivingDoctorName,
    receivingDoctorPicture = receivingDoctorPicture,
    reason = reason,
    status = status,
    resultingAppointmentId = resultingAppointmentId,
    createdAt = createdAt,
    respondedAt = respondedAt,
)

fun ReferralEligibilityData.toDomain() = ReferralEligibility(eligible = eligible, reasons = reasons)
