package ke.co.smartroundclinic.doctor.domain.usecase.consultation

import ke.co.smartroundclinic.doctor.common.Resource
import ke.co.smartroundclinic.doctor.domain.model.CallJoinInfo
import ke.co.smartroundclinic.doctor.domain.repository.ConsultationRepository

class JoinConsultationCallUseCase(private val repository: ConsultationRepository) {
    suspend operator fun invoke(sessionId: String, callId: String): Resource<CallJoinInfo> =
        repository.joinCall(sessionId, callId)
}
