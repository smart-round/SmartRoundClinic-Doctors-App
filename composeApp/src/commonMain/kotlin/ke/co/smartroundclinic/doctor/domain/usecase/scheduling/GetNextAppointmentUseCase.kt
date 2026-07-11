package ke.co.smartroundclinic.doctor.domain.usecase.scheduling

import ke.co.smartroundclinic.doctor.common.Resource
import ke.co.smartroundclinic.doctor.domain.model.NextAppointment
import ke.co.smartroundclinic.doctor.domain.repository.AppointmentRepository

class GetNextAppointmentUseCase(private val remote: AppointmentRepository) {
    suspend operator fun invoke(otherUserId: String): Resource<NextAppointment?> = remote.getNextAppointment(otherUserId)
}
