package ke.co.smartroundclinic.doctor.domain.usecase.scheduling

import ke.co.smartroundclinic.doctor.common.Resource
import ke.co.smartroundclinic.doctor.domain.model.Appointment
import ke.co.smartroundclinic.doctor.domain.repository.AppointmentLocalRepository
import ke.co.smartroundclinic.doctor.domain.repository.SchedulingRepository

class CancelAppointmentUseCase(
    private val remote: SchedulingRepository,
    private val local: AppointmentLocalRepository,
) {
    suspend operator fun invoke(id: String, reason: String? = null): Resource<Appointment> {
        val result = remote.cancelAppointment(id, reason)
        if (result is Resource.Success) result.data?.let { local.upsertAppointment(it) }
        return result
    }
}
