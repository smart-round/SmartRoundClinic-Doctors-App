package ke.co.smartroundclinic.doctor.domain.usecase.support

import ke.co.smartroundclinic.doctor.common.Resource
import ke.co.smartroundclinic.doctor.domain.model.SupportChatMessage
import ke.co.smartroundclinic.doctor.domain.repository.SupportRepository

class GetSupportChatHistoryUseCase(private val repository: SupportRepository) {
    suspend operator fun invoke(ticketId: String): Resource<List<SupportChatMessage>> =
        repository.getChatHistory(ticketId)
}
