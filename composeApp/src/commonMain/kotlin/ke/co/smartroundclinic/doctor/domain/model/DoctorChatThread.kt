package ke.co.smartroundclinic.doctor.domain.model

data class DoctorChatThread(
    val threadId: String,
    val counterpartId: String,
    val counterpartName: String,
    val counterpartPicture: String?,
    val lastMessagePreview: String?,
    val lastMessageAt: String?,
    val isOnline: Boolean = false,
    val lastSeenAt: String? = null,
)
