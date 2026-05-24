package ke.co.smartroundclinic.doctor.domain.usecase.consultation

import ke.co.smartroundclinic.doctor.common.Resource
import ke.co.smartroundclinic.doctor.domain.model.ConsultationSession
import ke.co.smartroundclinic.doctor.domain.repository.ConsultationRepository

class StartConsultationUseCase(private val repository: ConsultationRepository) {
    suspend operator fun invoke(appointmentId: String): Resource<ConsultationSession> =
        repository.startOrGet(appointmentId)
}
