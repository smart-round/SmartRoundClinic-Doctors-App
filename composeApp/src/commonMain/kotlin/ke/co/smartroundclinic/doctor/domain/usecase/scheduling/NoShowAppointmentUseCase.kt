package ke.co.smartroundclinic.doctor.domain.usecase.scheduling

import ke.co.smartroundclinic.doctor.common.Resource
import ke.co.smartroundclinic.doctor.domain.model.Appointment
import ke.co.smartroundclinic.doctor.domain.repository.AppointmentLocalRepository
import ke.co.smartroundclinic.doctor.domain.repository.SchedulingRepository

class NoShowAppointmentUseCase(
    private val remote: SchedulingRepository,
    private val local: AppointmentLocalRepository,
) {
    suspend operator fun invoke(id: String): Resource<Appointment> {
        val result = remote.noShowAppointment(id)
        if (result is Resource.Success) result.data?.let { local.upsertAppointment(it) }
        return result
    }
}
