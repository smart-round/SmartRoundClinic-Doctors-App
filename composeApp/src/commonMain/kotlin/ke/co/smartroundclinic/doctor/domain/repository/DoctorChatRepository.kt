package ke.co.smartroundclinic.doctor.domain.repository

import ke.co.smartroundclinic.doctor.common.Resource
import ke.co.smartroundclinic.doctor.domain.model.CallJoinInfo
import ke.co.smartroundclinic.doctor.domain.model.DoctorCallInvite
import ke.co.smartroundclinic.doctor.domain.model.DoctorChatHistoryPage
import ke.co.smartroundclinic.doctor.domain.model.DoctorChatMessage
import ke.co.smartroundclinic.doctor.domain.model.DoctorChatThread

interface DoctorChatRepository {
    suspend fun initiateChat(otherDoctorId: String): Resource<DoctorChatThread>
    suspend fun listThreads(): Resource<List<DoctorChatThread>>
    suspend fun getHistory(threadId: String, before: String?, size: Int): Resource<DoctorChatHistoryPage>
    suspend fun uploadFile(threadId: String, fileName: String, contentType: String, bytes: ByteArray): Resource<DoctorChatMessage>

    suspend fun joinCall(threadId: String): Resource<CallJoinInfo>
    suspend fun inviteToCall(threadId: String, isVideo: Boolean): Resource<DoctorCallInvite>
    suspend fun declineCall(threadId: String, callId: String): Resource<Unit>
    suspend fun cancelCall(threadId: String, callId: String): Resource<Unit>
    suspend fun endCall(threadId: String): Resource<Unit>
}
