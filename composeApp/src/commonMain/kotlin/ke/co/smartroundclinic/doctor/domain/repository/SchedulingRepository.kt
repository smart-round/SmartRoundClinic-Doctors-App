package ke.co.smartroundclinic.doctor.domain.repository

import ke.co.smartroundclinic.doctor.common.Resource
import ke.co.smartroundclinic.doctor.data.remote.dto.request.CancelAppointmentReq
import ke.co.smartroundclinic.doctor.data.remote.dto.request.UpsertAvailabilityReq
import ke.co.smartroundclinic.doctor.domain.model.Appointment
import ke.co.smartroundclinic.doctor.domain.model.DoctorAvailability

interface SchedulingRepository {
    suspend fun getSchedule(): Resource<List<DoctorAvailability>>
    suspend fun upsertAvailability(req: UpsertAvailabilityReq): Resource<DoctorAvailability>
    suspend fun getAppointments(filter: String?): Resource<List<Appointment>>
    suspend fun confirmAppointment(id: String): Resource<Appointment>
    suspend fun completeAppointment(id: String): Resource<Appointment>
    suspend fun noShowAppointment(id: String): Resource<Appointment>
    suspend fun cancelAppointment(id: String, reason: String?): Resource<Appointment>
}
