package ke.co.smartroundclinic.doctor.domain.model

data class DoctorChatMessage(
    val id: String,
    val threadId: String,
    val senderId: String,
    val senderName: String,
    val messageType: String,
    val message: String?,
    val files: List<DoctorChatFileAttachment>,
    val createdAt: String,
)

data class DoctorChatFileAttachment(
    val fileName: String,
    val url: String,
    val contentType: String,
    val sizeBytes: Long,
)

data class DoctorChatHistoryPage(
    val items: List<DoctorChatMessage>,
    val nextCursor: String?,
)

data class DoctorCallInvite(val callId: String, val ringTimeoutSeconds: Long)
