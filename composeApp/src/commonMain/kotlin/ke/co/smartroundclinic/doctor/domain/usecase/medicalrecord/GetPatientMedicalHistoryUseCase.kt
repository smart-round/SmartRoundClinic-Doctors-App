package ke.co.smartroundclinic.doctor.domain.usecase.medicalrecord

import ke.co.smartroundclinic.doctor.common.Resource
import ke.co.smartroundclinic.doctor.domain.model.MedicalRecord
import ke.co.smartroundclinic.doctor.domain.repository.MedicalRecordRepository

class GetPatientMedicalHistoryUseCase(private val repository: MedicalRecordRepository) {
    suspend operator fun invoke(patientId: String): Resource<List<MedicalRecord>> =
        repository.getPatientHistory(patientId)
}
