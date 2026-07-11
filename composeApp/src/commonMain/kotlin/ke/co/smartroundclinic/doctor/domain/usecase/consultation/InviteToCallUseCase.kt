package ke.co.smartroundclinic.doctor.domain.usecase.consultation

import ke.co.smartroundclinic.doctor.common.Resource
import ke.co.smartroundclinic.doctor.domain.model.CallInvite
import ke.co.smartroundclinic.doctor.domain.repository.ConsultationRepository

class InviteToCallUseCase(private val repository: ConsultationRepository) {
    suspend operator fun invoke(otherUserId: String, isVideo: Boolean = true): Resource<CallInvite> =
        repository.inviteToCall(otherUserId, isVideo)
}
