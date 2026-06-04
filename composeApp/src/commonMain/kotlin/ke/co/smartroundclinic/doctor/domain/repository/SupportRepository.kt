package ke.co.smartroundclinic.doctor.domain.repository

import ke.co.smartroundclinic.doctor.common.Resource
import ke.co.smartroundclinic.doctor.domain.model.IssueCategory
import ke.co.smartroundclinic.doctor.domain.model.SupportChatMessage
import ke.co.smartroundclinic.doctor.domain.model.SupportTicket

interface SupportRepository {
    suspend fun getCategories(): Resource<List<IssueCategory>>
    suspend fun createTicket(
        categoryId: String,
        title: String,
        description: String,
        complainantName: String,
        complainantEmail: String,
    ): Resource<SupportTicket>
    suspend fun getMyTickets(page: Int = 1, size: Int = 20): Resource<List<SupportTicket>>
    suspend fun getChatHistory(ticketId: String): Resource<List<SupportChatMessage>>
    suspend fun uploadChatFile(ticketId: String, bytes: ByteArray, fileName: String, contentType: String): Resource<SupportChatMessage>
}
