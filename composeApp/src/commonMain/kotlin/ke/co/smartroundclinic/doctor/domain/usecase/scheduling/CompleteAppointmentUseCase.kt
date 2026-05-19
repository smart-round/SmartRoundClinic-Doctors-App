package ke.co.smartroundclinic.doctor.domain.usecase.scheduling

import ke.co.smartroundclinic.doctor.common.Resource
import ke.co.smartroundclinic.doctor.data.remote.dto.response.SuccessRes
import ke.co.smartroundclinic.doctor.domain.repository.AppointmentRepository

class CompleteAppointmentUseCase(private val remote: AppointmentRepository) {
    suspend operator fun invoke(id: String): Resource<SuccessRes> = remote.completeAppointment(id)
}
