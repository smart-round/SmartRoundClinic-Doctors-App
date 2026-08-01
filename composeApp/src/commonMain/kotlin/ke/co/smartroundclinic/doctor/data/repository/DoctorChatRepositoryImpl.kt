package ke.co.smartroundclinic.doctor.data.repository

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

class DoctorChatRepositoryImpl(private val client: HttpClient) : DoctorChatRepository {

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

    override suspend fun uploadFile(threadId: String, fileName: String, contentType: String, bytes: ByteArray): Resource<DoctorChatMessage> =
        withContext(Dispatchers.IO) {
            try {
                val res = client.post("doctor-chat/threads/$threadId/files") {
                    setBody(MultiPartFormDataContent(formData {
                        append(
                            key = "file",
                            value = bytes,
                            headers = Headers.build {
                                append(HttpHeaders.ContentDisposition, "form-data; name=\"file\"; filename=\"$fileName\"")
                                append(HttpHeaders.ContentType, contentType)
                            },
                        )
                    }))
                }.body<DoctorChatFileUploadResponse>()
                if (res.status && res.data != null) Resource.Success(res.data.toDomain(), res.message) else Resource.Error(res.message)
            } catch (e: Exception) {
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
