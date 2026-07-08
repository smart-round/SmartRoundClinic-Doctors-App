package ke.co.smartroundclinic.doctor.domain.model

data class SupportTicket(
    val id: String,
    val ticketNumber: String,
    val issueCategoryId: String,
    val categoryName: String,
    val title: String,
    val description: String,
    val status: String,
    val createdAt: String,
    val updatedAt: String?,
)

data class IssueCategory(
    val id: String,
    val name: String,
    val description: String?,
    val isActive: Boolean = true,
)

data class SupportChatMessage(
    val id: String,
    val ticketId: String,
    val senderId: String,
    val senderName: String,
    val messageType: String,
    val message: String?,
    val files: List<SupportChatFile>,
    val createdAt: String,
)

data class SupportChatFile(
    val fileName: String,
    val url: String,
    val contentType: String,
)
