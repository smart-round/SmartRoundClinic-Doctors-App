package ke.co.smartroundclinic.doctor.domain.usecase.scheduling

import ke.co.smartroundclinic.doctor.common.Resource
import ke.co.smartroundclinic.doctor.domain.model.Appointment
import ke.co.smartroundclinic.doctor.domain.repository.AppointmentLocalRepository
import ke.co.smartroundclinic.doctor.domain.repository.SchedulingRepository

class GetAppointmentsUseCase(
    private val remote: SchedulingRepository,
    private val local: AppointmentLocalRepository,
) {
    suspend operator fun invoke(filter: String? = null): Resource<List<Appointment>> {
        val result = remote.getAppointments(filter)
        if (result is Resource.Success) {
            val data = result.data ?: emptyList()
            if (filter == null) local.upsertAll(data) else data.forEach { local.upsertAppointment(it) }
        }
        return result
    }
}
