package ke.co.smartroundclinic.doctor.data.remote.dto.response

import ke.co.smartroundclinic.doctor.domain.model.CallJoinInfo
import ke.co.smartroundclinic.doctor.domain.model.DoctorCallInvite
import ke.co.smartroundclinic.doctor.domain.model.DoctorChatFileAttachment
import ke.co.smartroundclinic.doctor.domain.model.DoctorChatMessage
import ke.co.smartroundclinic.doctor.domain.model.DoctorChatThread
import ke.co.smartroundclinic.doctor.domain.model.ThreadPreviewKind
import kotlinx.serialization.Serializable

@Serializable
data class DoctorChatMessageData(
    val id: String,
    val threadId: String,
    val senderId: String,
    val senderName: String,
    val messageType: String,
    val message: String? = null,
    val files: List<DoctorChatFileData> = emptyList(),
    val createdAt: String,
)

@Serializable
data class DoctorChatFileData(
    val fileName: String,
    val url: String,
    val contentType: String,
    val sizeBytes: Long,
)

fun DoctorChatMessageData.toDomain() = DoctorChatMessage(
    id = id, threadId = threadId, senderId = senderId, senderName = senderName,
    messageType = messageType, message = message, files = files.map { it.toDomain() }, createdAt = createdAt,
)

fun DoctorChatFileData.toDomain() = DoctorChatFileAttachment(fileName, url, contentType, sizeBytes)

/** Outgoing WS text-frame envelope — same shape as consultation's, minus the fields this module doesn't use. */
@Serializable
data class DoctorChatWsOutgoing(val type: String, val message: String? = null, val isTyping: Boolean? = null)

@Serializable
data class DoctorChatWsEventPeek(val type: String? = null)

@Serializable
data class DoctorTypingEventData(
    val type: String = "TYPING",
    val senderId: String,
    val isTyping: Boolean,
)

@Serializable
data class DoctorPresenceEventData(
    val type: String = "PRESENCE",
    val userId: String,
    val isOnline: Boolean,
    val lastSeenAt: String? = null,
)

@Serializable
data class DoctorCallInviteEventData(
    val type: String = "CALL_INVITE",
    val callId: String,
    val callerId: String,
    val callerName: String? = null,
    val callerPicture: String? = null,
    val isVideo: Boolean,
    val ringTimeoutSeconds: Long,
)

@Serializable
data class DoctorCallAnsweredEventData(val type: String = "CALL_ANSWERED", val callId: String)

@Serializable
data class DoctorCallDeclinedEventData(val type: String = "CALL_DECLINED", val callId: String)

@Serializable
data class DoctorCallCancelledEventData(val type: String = "CALL_CANCELLED", val callId: String)

@Serializable
data class DoctorChatFileUploadResponse(
    val data: DoctorChatMessageData? = null,
    val httpStatusCode: Int,
    val message: String,
    val status: Boolean,
)

@Serializable
data class JoinDoctorCallData(
    val meetingId: String,
    val participantId: String,
    val authToken: String,
    val presetName: String,
)

@Serializable
data class JoinDoctorCallResponse(
    val data: JoinDoctorCallData? = null,
    val httpStatusCode: Int,
    val message: String,
    val status: Boolean,
)

fun JoinDoctorCallData.toDomain() = CallJoinInfo(meetingId, participantId, authToken, presetName)

@Serializable
data class InviteToDoctorCallData(val callId: String, val ringTimeoutSeconds: Long)

@Serializable
data class InviteToDoctorCallResponse(
    val data: InviteToDoctorCallData? = null,
    val httpStatusCode: Int,
    val message: String,
    val status: Boolean,
)

fun InviteToDoctorCallData.toDomain() = DoctorCallInvite(callId, ringTimeoutSeconds)

@Serializable
data class DoctorChatThreadData(
    val threadId: String,
    val counterpartId: String,
    val counterpartName: String,
    val counterpartPicture: String? = null,
    val lastMessagePreview: String? = null,
    val lastMessageAt: String? = null,
    val isOnline: Boolean = false,
    val lastSeenAt: String? = null,
    val lastMessageKind: String? = null,
)

@Serializable
data class DoctorChatThreadResponse(
    val data: DoctorChatThreadData? = null,
    val httpStatusCode: Int,
    val message: String,
    val status: Boolean,
)

@Serializable
data class DoctorChatThreadsResponse(
    val data: List<DoctorChatThreadData>? = null,
    val httpStatusCode: Int,
    val message: String,
    val status: Boolean,
)

@Serializable
data class DoctorCallActionRes(
    val httpStatusCode: Int,
    val message: String,
    val status: Boolean,
)

fun DoctorChatThreadData.toDomain() = DoctorChatThread(
    threadId = threadId, counterpartId = counterpartId, counterpartName = counterpartName,
    counterpartPicture = counterpartPicture, lastMessagePreview = lastMessagePreview, lastMessageAt = lastMessageAt,
    isOnline = isOnline, lastSeenAt = lastSeenAt,
    lastMessageKind = when (lastMessageKind?.uppercase()) {
        "PHOTO" -> ThreadPreviewKind.PHOTO
        "VIDEO" -> ThreadPreviewKind.VIDEO
    "VIDEO" -> ThreadPreviewKind.VIDEO
        "FILE" -> ThreadPreviewKind.FILE
        "PRESCRIPTION" -> ThreadPreviewKind.PRESCRIPTION
        else -> ThreadPreviewKind.TEXT
    },
)

@Serializable
data class DoctorChatMessagesData(
    val items: List<DoctorChatMessageData> = emptyList(),
    val nextCursor: String? = null,
)

@Serializable
data class DoctorChatMessagesResponse(
    val data: DoctorChatMessagesData? = null,
    val httpStatusCode: Int,
    val message: String,
    val status: Boolean,
)

// ─── Pre-signed direct-to-storage upload ────────────────────────────────────

@Serializable
data class DoctorChatPresignUploadReq(
    val fileName: String,
    val contentType: String,
    val sizeBytes: Long,
)

@Serializable
data class DoctorChatPresignUploadData(
    val messageId: String,
    val key: String,
    val uploadUrl: String,
    val contentType: String,
)

@Serializable
data class DoctorChatPresignUploadResponse(
    val data: DoctorChatPresignUploadData? = null,
    val httpStatusCode: Int,
    val message: String,
    val status: Boolean,
)

@Serializable
data class DoctorChatCompleteUploadReq(
    val messageId: String,
    val key: String,
    val fileName: String,
    val contentType: String,
    val sizeBytes: Long,
)
