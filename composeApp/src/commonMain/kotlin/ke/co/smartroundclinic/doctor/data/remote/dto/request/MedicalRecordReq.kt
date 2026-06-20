package ke.co.smartroundclinic.doctor.data.remote.dto.request

import kotlinx.serialization.Serializable

@Serializable
data class PrescriptionItemReq(
    val drug: String,
    val dosage: String,
    val frequency: String,
    val duration: String,
    val instructions: String? = null,
)

@Serializable
data class SaveMedicalRecordReq(
    val appointmentId: String,
    val consultationId: String? = null,
    val patientId: String,
    val diagnosis: String? = null,
    val prescription: List<PrescriptionItemReq> = emptyList(),
    val summary: String? = null,
    val referralNote: String? = null,
    val labRequests: List<String> = emptyList(),
    val additionalNotes: String? = null,
)
