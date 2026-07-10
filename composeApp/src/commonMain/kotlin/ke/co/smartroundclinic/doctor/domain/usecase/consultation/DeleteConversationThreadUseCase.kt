package ke.co.smartroundclinic.doctor.domain.usecase.consultation

import ke.co.smartroundclinic.doctor.common.Resource
import ke.co.smartroundclinic.doctor.domain.repository.ConsultationRepository

class DeleteConversationThreadUseCase(private val repository: ConsultationRepository) {
    suspend operator fun invoke(doctorId: String, patientId: String): Resource<Unit> =
        repository.deleteThread(doctorId, patientId)
}
