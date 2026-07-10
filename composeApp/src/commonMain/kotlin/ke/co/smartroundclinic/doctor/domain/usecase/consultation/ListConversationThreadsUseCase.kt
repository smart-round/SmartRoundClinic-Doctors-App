package ke.co.smartroundclinic.doctor.domain.usecase.consultation

import ke.co.smartroundclinic.doctor.common.Resource
import ke.co.smartroundclinic.doctor.domain.model.ConversationThread
import ke.co.smartroundclinic.doctor.domain.repository.ConsultationRepository

class ListConversationThreadsUseCase(private val repository: ConsultationRepository) {
    suspend operator fun invoke(): Resource<List<ConversationThread>> = repository.listThreads()
}
