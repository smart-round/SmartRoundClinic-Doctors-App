package ke.co.smartroundclinic.doctor.domain.usecase.consultation

import ke.co.smartroundclinic.doctor.common.Resource
import ke.co.smartroundclinic.doctor.domain.model.ConsultationMessage
import ke.co.smartroundclinic.doctor.domain.repository.ConsultationRepository

class GetConsultationMessagesUseCase(private val repository: ConsultationRepository) {
    suspend operator fun invoke(sessionId: String, page: Int = 1, size: Int = 50): Resource<List<ConsultationMessage>> =
        repository.getMessages(sessionId, page, size)
}
