package ke.co.smartroundclinic.doctor.domain.usecase.consultation

import ke.co.smartroundclinic.doctor.common.Resource
import ke.co.smartroundclinic.doctor.domain.repository.ConsultationRepository

class DeclineCallUseCase(private val repository: ConsultationRepository) {
    suspend operator fun invoke(otherUserId: String, callId: String): Resource<Unit> =
        repository.declineCall(otherUserId, callId)
}
