package ke.co.smartroundclinic.doctor.domain.usecase.scheduling

import ke.co.smartroundclinic.doctor.common.Resource
import ke.co.smartroundclinic.doctor.data.remote.dto.response.toDomain
import ke.co.smartroundclinic.doctor.domain.model.Appointment
import ke.co.smartroundclinic.doctor.domain.repository.AppointmentLocalRepository
import ke.co.smartroundclinic.doctor.domain.repository.AppointmentRepository

class GetAppointmentsUseCase(
    private val remote: AppointmentRepository,
    private val local: AppointmentLocalRepository,
) {
    suspend operator fun invoke(filter: String? = null): Resource<List<Appointment>> {
        val result = remote.getAppointments(filter)
        if (result is Resource.Success) {
            val appointments = result.data?.data?.map { it.toDomain() } ?: emptyList()
            if (appointments.isNotEmpty()) {
                if (filter == null) local.upsertAll(appointments) else appointments.forEach { local.upsertAppointment(it) }
            }
            return Resource.Success(appointments)
        }
        return Resource.Error(result.message ?: "Failed to load appointments")
    }
}
