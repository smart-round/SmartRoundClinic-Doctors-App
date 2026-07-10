package ke.co.smartroundclinic.doctor.domain.usecase.consultation

import ke.co.smartroundclinic.doctor.common.Resource
import ke.co.smartroundclinic.doctor.domain.model.ConsultationMessage
import ke.co.smartroundclinic.doctor.domain.repository.ConsultationRepository

class GetMergedConsultationHistoryUseCase(private val repository: ConsultationRepository) {
    suspend operator fun invoke(
        doctorId: String,
        patientId: String,
        before: String? = null,
        size: Int = 50,
    ): Resource<Pair<List<ConsultationMessage>, String?>> = repository.getMergedMessages(doctorId, patientId, before, size)
}
