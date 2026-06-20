package ke.co.smartroundclinic.doctor.domain.usecase.medicalrecord

import ke.co.smartroundclinic.doctor.common.Resource
import ke.co.smartroundclinic.doctor.domain.model.MedicalRecord
import ke.co.smartroundclinic.doctor.domain.repository.MedicalRecordRepository

class GetMedicalRecordUseCase(private val repository: MedicalRecordRepository) {
    suspend operator fun invoke(appointmentId: String): Resource<MedicalRecord?> =
        repository.getByAppointmentId(appointmentId)
}
