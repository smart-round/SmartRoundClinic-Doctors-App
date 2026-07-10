package ke.co.smartroundclinic.doctor.domain.repository

import ke.co.smartroundclinic.doctor.common.Resource
import ke.co.smartroundclinic.doctor.domain.model.CallJoinInfo
import ke.co.smartroundclinic.doctor.domain.model.ConsultationMessage
import ke.co.smartroundclinic.doctor.domain.model.ConsultationSession
import ke.co.smartroundclinic.doctor.domain.model.ConversationThread
import ke.co.smartroundclinic.doctor.domain.model.MergedHistoryPage

interface ConsultationRepository {
    suspend fun startOrGet(appointmentId: String): Resource<ConsultationSession>
    suspend fun getMessages(sessionId: String, page: Int, size: Int): Resource<List<ConsultationMessage>>
    suspend fun endConsultation(sessionId: String): Resource<Unit>
    suspend fun endCall(sessionId: String): Resource<Unit>
    suspend fun uploadFile(sessionId: String, fileName: String, contentType: String, bytes: ByteArray): Resource<ConsultationMessage>
    suspend fun joinCall(sessionId: String): Resource<CallJoinInfo>

    /** One entry per doctor-patient pair the caller participates in — merges all of their consultations. */
    suspend fun listThreads(): Resource<List<ConversationThread>>

    /** Merged, cursor-paginated history across every consultation a (doctorId, patientId) pair has had. */
    suspend fun getMergedMessages(doctorId: String, patientId: String, before: String?, size: Int): Resource<MergedHistoryPage>

    /** Hides the thread from the caller's own list only ("delete for me") — reappears on a new message. */
    suspend fun deleteThread(doctorId: String, patientId: String): Resource<Unit>
}
