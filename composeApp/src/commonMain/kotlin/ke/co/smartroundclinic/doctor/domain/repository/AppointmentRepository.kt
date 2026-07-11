package ke.co.smartroundclinic.doctor.domain.repository

import ke.co.smartroundclinic.doctor.common.Resource
import ke.co.smartroundclinic.doctor.data.remote.dto.response.GetAppointmentsRes
import ke.co.smartroundclinic.doctor.data.remote.dto.response.SuccessRes
import ke.co.smartroundclinic.doctor.domain.model.NextAppointment

interface AppointmentRepository {
    suspend fun getAppointments(filter: String?): Resource<GetAppointmentsRes>
    suspend fun confirmAppointment(id: String): Resource<SuccessRes>
    suspend fun completeAppointment(id: String): Resource<SuccessRes>
    suspend fun noShowAppointment(id: String): Resource<SuccessRes>
    suspend fun cancelAppointment(id: String, reason: String?): Resource<SuccessRes>
    suspend fun getNextAppointment(otherUserId: String): Resource<NextAppointment?>
}
