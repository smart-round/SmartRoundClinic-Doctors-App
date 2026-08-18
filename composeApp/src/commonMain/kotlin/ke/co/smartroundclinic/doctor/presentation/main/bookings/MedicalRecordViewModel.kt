package ke.co.smartroundclinic.doctor.presentation.main.bookings

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import ke.co.smartroundclinic.doctor.common.Resource
import ke.co.smartroundclinic.doctor.core.snackbar.SnackbarController
import ke.co.smartroundclinic.doctor.data.remote.dto.request.PrescriptionItemReq
import ke.co.smartroundclinic.doctor.data.remote.dto.request.SaveMedicalRecordReq
import ke.co.smartroundclinic.doctor.domain.model.MedicalRecord
import ke.co.smartroundclinic.doctor.domain.model.PatientBio
import ke.co.smartroundclinic.doctor.domain.usecase.medicalrecord.GetMedicalRecordUseCase
import ke.co.smartroundclinic.doctor.domain.usecase.medicalrecord.GetPatientBioUseCase
import ke.co.smartroundclinic.doctor.domain.usecase.medicalrecord.GetPatientMedicalHistoryUseCase
import ke.co.smartroundclinic.doctor.domain.usecase.medicalrecord.SaveMedicalRecordUseCase
import kotlinx.coroutines.launch

class MedicalRecordViewModel(
    private val saveUseCase: SaveMedicalRecordUseCase,
    private val getUseCase: GetMedicalRecordUseCase,
    private val historyUseCase: GetPatientMedicalHistoryUseCase,
    private val getBioUseCase: GetPatientBioUseCase,
    private val snackbarController: SnackbarController,
) : ViewModel() {

    var record by mutableStateOf<MedicalRecord?>(null)
        private set
    var isSaving by mutableStateOf(false)
        private set
    var isLoading by mutableStateOf(false)
        private set
    var saveSuccess by mutableStateOf(false)
        private set

    val patientHistory = mutableStateListOf<MedicalRecord>()
    var patientBio by mutableStateOf<PatientBio?>(null)
        private set

    var diagnosis by mutableStateOf("")
    var summary by mutableStateOf("")

    // No longer editable — referring a patient now collects its own medical reason on the Refer
    // Patient screen. Still round-tripped so that editing a record written before that change
    // preserves its existing note instead of silently clearing it.
    var referralNote by mutableStateOf("")

    var additionalNotes by mutableStateOf("")
    val prescription = mutableStateListOf<PrescriptionItemReq>()
    val labRequests = mutableStateListOf<String>()

    fun loadRecord(appointmentId: String) {
        viewModelScope.launch {
            isLoading = true
            record = null
            diagnosis = ""
            summary = ""
            referralNote = ""
            additionalNotes = ""
            prescription.clear()
            labRequests.clear()
            when (val result = getUseCase(appointmentId)) {
                is Resource.Success -> {
                    result.data?.let { rec ->
                        record = rec
                        diagnosis = rec.diagnosis ?: ""
                        summary = rec.summary ?: ""
                        referralNote = rec.referralNote ?: ""
                        additionalNotes = rec.additionalNotes ?: ""
                        prescription.addAll(rec.prescription.map {
                            PrescriptionItemReq(it.drug, it.dosage, it.frequency, it.duration, it.instructions)
                        })
                        labRequests.addAll(rec.labRequests)
                    }
                }
                else -> {}
            }
            isLoading = false
        }
    }

    fun loadPatientHistory(patientId: String) {
        viewModelScope.launch {
            when (val result = historyUseCase(patientId)) {
                is Resource.Success -> {
                    patientHistory.clear()
                    patientHistory.addAll(result.data ?: emptyList())
                }
                else -> {}
            }
        }
    }

    fun loadPatientBio(patientId: String) {
        viewModelScope.launch {
            when (val result = getBioUseCase(patientId)) {
                is Resource.Success -> patientBio = result.data
                else -> {}
            }
        }
    }

    fun addPrescriptionItem() {
        prescription.add(PrescriptionItemReq(drug = "", dosage = "", frequency = "", duration = ""))
    }

    fun removePrescriptionItem(index: Int) {
        if (index in prescription.indices) prescription.removeAt(index)
    }

    fun updatePrescriptionItem(index: Int, item: PrescriptionItemReq) {
        if (index in prescription.indices) prescription[index] = item
    }

    fun addLabRequest() {
        labRequests.add("")
    }

    fun removeLabRequest(index: Int) {
        if (index in labRequests.indices) labRequests.removeAt(index)
    }

    fun updateLabRequest(index: Int, value: String) {
        if (index in labRequests.indices) labRequests[index] = value
    }

    fun save(appointmentId: String, consultationId: String?, patientId: String) {
        if (isSaving) return
        viewModelScope.launch {
            isSaving = true
            val req = SaveMedicalRecordReq(
                appointmentId = appointmentId,
                consultationId = consultationId,
                patientId = patientId,
                diagnosis = diagnosis.ifBlank { null },
                prescription = prescription.toList(),
                summary = summary.ifBlank { null },
                referralNote = referralNote.ifBlank { null },
                labRequests = labRequests.filter { it.isNotBlank() },
                additionalNotes = additionalNotes.ifBlank { null },
            )
            when (val result = saveUseCase(req)) {
                is Resource.Success -> {
                    record = result.data
                    saveSuccess = true
                    snackbarController.show("Medical record saved")
                }
                is Resource.Error -> snackbarController.show(result.message ?: "Failed to save", isError = true)
                else -> {}
            }
            isSaving = false
        }
    }

    fun resetSaveSuccess() {
        saveSuccess = false
    }
}
