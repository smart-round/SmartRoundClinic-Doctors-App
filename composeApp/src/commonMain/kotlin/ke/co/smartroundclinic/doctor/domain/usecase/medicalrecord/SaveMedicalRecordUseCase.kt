package ke.co.smartroundclinic.doctor.domain.usecase.medicalrecord

import ke.co.smartroundclinic.doctor.common.Resource
import ke.co.smartroundclinic.doctor.data.remote.dto.request.SaveMedicalRecordReq
import ke.co.smartroundclinic.doctor.domain.model.MedicalRecord
import ke.co.smartroundclinic.doctor.domain.repository.MedicalRecordRepository

class SaveMedicalRecordUseCase(private val repository: MedicalRecordRepository) {
    suspend operator fun invoke(req: SaveMedicalRecordReq): Resource<MedicalRecord?> =
        repository.save(req)
}
