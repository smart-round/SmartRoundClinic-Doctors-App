package ke.co.smartroundclinic.doctor.data.repository

import io.github.aakira.napier.Napier
import io.ktor.client.plugins.timeout
import io.ktor.client.request.put
import io.ktor.http.ContentType
import io.ktor.http.content.OutgoingContent
import io.ktor.http.contentType
import io.ktor.http.isSuccess
import io.ktor.utils.io.ByteWriteChannel
import io.ktor.utils.io.writeFully
import kotlinx.datetime.Clock
import kotlinx.io.RawSource
import kotlinx.io.Buffer
import kotlinx.io.readByteArray
import ke.co.smartroundclinic.doctor.common.Constants
import ke.co.smartroundclinic.doctor.data.remote.dto.response.DoctorChatPresignUploadReq
import ke.co.smartroundclinic.doctor.data.remote.dto.response.DoctorChatPresignUploadResponse
import ke.co.smartroundclinic.doctor.data.remote.dto.response.DoctorChatCompleteUploadReq
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.forms.MultiPartFormDataContent
import io.ktor.client.request.forms.formData
import io.ktor.client.request.get
import io.ktor.client.request.parameter
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.http.Headers
import io.ktor.http.HttpHeaders
import ke.co.smartroundclinic.doctor.common.Resource
import ke.co.smartroundclinic.doctor.data.remote.dto.request.DoctorCallActionReq
import ke.co.smartroundclinic.doctor.data.remote.dto.request.InitiateDoctorChatReq
import ke.co.smartroundclinic.doctor.data.remote.dto.request.InviteToDoctorCallReq
import ke.co.smartroundclinic.doctor.data.remote.dto.response.DoctorCallActionRes
import ke.co.smartroundclinic.doctor.data.remote.dto.response.DoctorChatFileUploadResponse
import ke.co.smartroundclinic.doctor.data.remote.dto.response.DoctorChatMessageData
import ke.co.smartroundclinic.doctor.data.remote.dto.response.DoctorChatMessagesResponse
import ke.co.smartroundclinic.doctor.data.remote.dto.response.DoctorChatThreadResponse
import ke.co.smartroundclinic.doctor.data.remote.dto.response.DoctorChatThreadsResponse
import ke.co.smartroundclinic.doctor.data.remote.dto.response.InviteToDoctorCallResponse
import ke.co.smartroundclinic.doctor.data.remote.dto.response.JoinDoctorCallResponse
import ke.co.smartroundclinic.doctor.data.remote.dto.response.toDomain
import ke.co.smartroundclinic.doctor.domain.model.CallJoinInfo
import ke.co.smartroundclinic.doctor.domain.model.DoctorCallInvite
import ke.co.smartroundclinic.doctor.domain.model.DoctorChatHistoryPage
import ke.co.smartroundclinic.doctor.domain.model.DoctorChatMessage
import ke.co.smartroundclinic.doctor.domain.model.DoctorChatThread
import ke.co.smartroundclinic.doctor.domain.repository.DoctorChatRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.withContext

class DoctorChatRepositoryImpl(
    private val client: HttpClient,
    /** Auth-free client for pre-signed storage PUTs — see CoreModule.STORAGE_HTTP_CLIENT. */
    private val storageClient: HttpClient,
) : DoctorChatRepository {

    override suspend fun initiateChat(otherDoctorId: String): Resource<DoctorChatThread> = withContext(Dispatchers.IO) {
        try {
            val res = client.post("doctor-chat/threads") { setBody(InitiateDoctorChatReq(otherDoctorId)) }.body<DoctorChatThreadResponse>()
            if (res.status && res.data != null) Resource.Success(res.data.toDomain(), res.message) else Resource.Error(res.message)
        } catch (e: Exception) {
            Resource.Error(e.message ?: "Failed to start chat")
        }
    }

    override suspend fun listThreads(): Resource<List<DoctorChatThread>> = withContext(Dispatchers.IO) {
        try {
            val res = client.get("doctor-chat/threads").body<DoctorChatThreadsResponse>()
            if (res.status) Resource.Success(res.data?.map { it.toDomain() } ?: emptyList(), res.message) else Resource.Error(res.message)
        } catch (e: Exception) {
            Resource.Error(e.message ?: "Failed to load conversations")
        }
    }

    override suspend fun getHistory(threadId: String, before: String?, size: Int): Resource<DoctorChatHistoryPage> = withContext(Dispatchers.IO) {
        try {
            val res = client.get("doctor-chat/threads/$threadId/messages") {
                if (before != null) parameter("before", before)
                parameter("size", size)
            }.body<DoctorChatMessagesResponse>()
            if (res.status) {
                val page = DoctorChatHistoryPage(
                    items = res.data?.items?.map(DoctorChatMessageData::toDomain) ?: emptyList(),
                    nextCursor = res.data?.nextCursor,
                )
                Resource.Success(page, res.message)
            } else Resource.Error(res.message)
        } catch (e: Exception) {
            Resource.Error(e.message ?: "Failed to load conversation")
        }
    }

    override suspend fun uploadFile(
        threadId: String,
        fileName: String,
        contentType: String,
        sizeBytes: Long,
        openSource: () -> RawSource,
        onProgress: (sent: Long, total: Long) -> Unit,
    ): Resource<DoctorChatMessage> = withContext(Dispatchers.IO) {
        val startedAt = Clock.System.now().toEpochMilliseconds()
        fun elapsed() = Clock.System.now().toEpochMilliseconds() - startedAt
        Napier.i(tag = "SRC-UPLOAD", message = "doctor-chat start name=$fileName type=$contentType bytes=$sizeBytes")
        try {
            val presign = client.post("doctor-chat/threads/$threadId/files/presign") {
                setBody(DoctorChatPresignUploadReq(fileName = fileName, contentType = contentType, sizeBytes = sizeBytes))
            }.body<DoctorChatPresignUploadResponse>()
            val target = presign.data
            if (!presign.status || target == null) {
                Napier.e(tag = "SRC-UPLOAD", message = "presign refused after ${elapsed()}ms: ${presign.message}")
                return@withContext Resource.Error(presign.message)
            }

            val putResponse = storageClient.put(target.uploadUrl) {
                timeout {
                    requestTimeoutMillis = Constants.UPLOAD_REQUEST_TIMEOUT_MS
                    socketTimeoutMillis = Constants.UPLOAD_SOCKET_TIMEOUT_MS
                }
                contentType(ContentType.parse(target.contentType))
                setBody(
                    object : OutgoingContent.WriteChannelContent() {
                        override val contentType = ContentType.parse(target.contentType)
                        override val contentLength = sizeBytes
                        override suspend fun writeTo(channel: ByteWriteChannel) {
                            // Reads through RawSource/Buffer rather than the ByteArray overload of
                            // readAtMostTo: on Kotlin/Native that name also matches an internal
                            // CPointer overload, which fails to compile for iOS.
                            val buffer = Buffer()
                            openSource().use { source ->
                                var sent = 0L
                                var lastReportedPercent = -1
                                while (true) {
                                    val read = source.readAtMostTo(buffer, Constants.UPLOAD_CHUNK_BYTES.toLong())
                                    if (read <= 0L) break
                                    val bytes = buffer.readByteArray()
                                    channel.writeFully(bytes)
                                    sent += bytes.size
                                    val percent = if (sizeBytes > 0) ((sent * 100) / sizeBytes).toInt() else 0
                                    if (percent != lastReportedPercent) {
                                        if (percent / 10 != lastReportedPercent / 10) {
                                            Napier.i(tag = "SRC-UPLOAD", message = "$percent% ($sent/$sizeBytes) at ${elapsed()}ms")
                                        }
                                        lastReportedPercent = percent
                                        onProgress(sent, sizeBytes)
                                    }
                                }
                            }
                        }
                    },
                )
            }
            if (!putResponse.status.isSuccess()) {
                Napier.e(tag = "SRC-UPLOAD", message = "storage PUT failed after ${elapsed()}ms http=${putResponse.status}")
                return@withContext Resource.Error("Failed to upload file (${putResponse.status.value})")
            }

            val res = client.post("doctor-chat/threads/$threadId/files/complete") {
                setBody(
                    DoctorChatCompleteUploadReq(
                        messageId = target.messageId,
                        key = target.key,
                        fileName = fileName,
                        contentType = target.contentType,
                        sizeBytes = sizeBytes,
                    ),
                )
            }.body<DoctorChatFileUploadResponse>()
            Napier.i(tag = "SRC-UPLOAD", message = "doctor-chat completed in ${elapsed()}ms status=${res.status}")
            if (res.status && res.data != null) Resource.Success(res.data.toDomain(), res.message) else Resource.Error(res.message)
        } catch (e: Exception) {
            Napier.e(tag = "SRC-UPLOAD", message = "failed after ${elapsed()}ms ${e::class.simpleName}: ${e.message}", throwable = e)
            Resource.Error(e.message ?: "Failed to upload file")
        }
    }

    override suspend fun joinCall(threadId: String): Resource<CallJoinInfo> = withContext(Dispatchers.IO) {
        try {
            val res = client.post("doctor-chat/threads/$threadId/call/join").body<JoinDoctorCallResponse>()
            if (res.status && res.data != null) Resource.Success(res.data.toDomain(), res.message) else Resource.Error(res.message)
        } catch (e: Exception) {
            Resource.Error(e.message ?: "Failed to join call")
        }
    }

    override suspend fun inviteToCall(threadId: String, isVideo: Boolean): Resource<DoctorCallInvite> = withContext(Dispatchers.IO) {
        try {
            val res = client.post("doctor-chat/threads/$threadId/call/invite") { setBody(InviteToDoctorCallReq(isVideo)) }.body<InviteToDoctorCallResponse>()
            if (res.status && res.data != null) Resource.Success(res.data.toDomain(), res.message) else Resource.Error(res.message)
        } catch (e: Exception) {
            Resource.Error(e.message ?: "Failed to start call")
        }
    }

    override suspend fun declineCall(threadId: String, callId: String): Resource<Unit> = withContext(Dispatchers.IO) {
        try {
            val res = client.post("doctor-chat/threads/$threadId/call/decline") { setBody(DoctorCallActionReq(callId)) }.body<DoctorCallActionRes>()
            if (res.status) Resource.Success(Unit, res.message) else Resource.Error(res.message)
        } catch (e: Exception) {
            Resource.Error(e.message ?: "Failed to decline call")
        }
    }

    override suspend fun cancelCall(threadId: String, callId: String): Resource<Unit> = withContext(Dispatchers.IO) {
        try {
            val res = client.post("doctor-chat/threads/$threadId/call/cancel") { setBody(DoctorCallActionReq(callId)) }.body<DoctorCallActionRes>()
            if (res.status) Resource.Success(Unit, res.message) else Resource.Error(res.message)
        } catch (e: Exception) {
            Resource.Error(e.message ?: "Failed to cancel call")
        }
    }

    override suspend fun endCall(threadId: String): Resource<Unit> = withContext(Dispatchers.IO) {
        try {
            client.post("doctor-chat/threads/$threadId/call/end")
            Resource.Success(Unit)
        } catch (e: Exception) {
            Resource.Error(e.message ?: "Failed to end call")
        }
    }
}
