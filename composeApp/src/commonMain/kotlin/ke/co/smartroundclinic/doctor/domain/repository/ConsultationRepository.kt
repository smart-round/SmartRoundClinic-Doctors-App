package ke.co.smartroundclinic.doctor.domain.repository

import ke.co.smartroundclinic.doctor.common.Resource
import ke.co.smartroundclinic.doctor.domain.model.CallJoinInfo
import ke.co.smartroundclinic.doctor.domain.model.ConsultationMessage
import ke.co.smartroundclinic.doctor.domain.model.ConsultationSession

interface ConsultationRepository {
    suspend fun startOrGet(appointmentId: String): Resource<ConsultationSession>
    suspend fun getMessages(sessionId: String, page: Int, size: Int): Resource<List<ConsultationMessage>>
    suspend fun endConsultation(sessionId: String): Resource<Unit>
    suspend fun endCall(sessionId: String): Resource<Unit>
    suspend fun uploadFile(sessionId: String, fileName: String, contentType: String, bytes: ByteArray): Resource<ConsultationMessage>
    suspend fun joinCall(sessionId: String): Resource<CallJoinInfo>
}
