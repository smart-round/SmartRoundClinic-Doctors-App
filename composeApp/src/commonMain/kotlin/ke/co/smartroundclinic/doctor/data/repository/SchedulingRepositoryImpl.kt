package ke.co.smartroundclinic.doctor.data.repository

import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.get
import io.ktor.client.request.parameter
import io.ktor.client.request.patch
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import ke.co.smartroundclinic.doctor.common.Resource
import ke.co.smartroundclinic.doctor.data.remote.dto.request.CancelAppointmentReq
import ke.co.smartroundclinic.doctor.data.remote.dto.request.UpsertAvailabilityReq
import ke.co.smartroundclinic.doctor.data.remote.dto.response.AppointmentActionRes
import ke.co.smartroundclinic.doctor.data.remote.dto.response.GetAppointmentsRes
import ke.co.smartroundclinic.doctor.data.remote.dto.response.GetScheduleRes
import ke.co.smartroundclinic.doctor.data.remote.dto.response.UpsertAvailabilityRes
import ke.co.smartroundclinic.doctor.data.remote.dto.response.toDomain
import ke.co.smartroundclinic.doctor.domain.model.Appointment
import ke.co.smartroundclinic.doctor.domain.model.DoctorAvailability
import ke.co.smartroundclinic.doctor.domain.repository.SchedulingRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.withContext

class SchedulingRepositoryImpl(private val client: HttpClient) : SchedulingRepository {

    override suspend fun getSchedule(): Resource<List<DoctorAvailability>> = withContext(Dispatchers.IO) {
        try {
            val response = client.get("scheduling/availability/schedule").body<GetScheduleRes>()
            Resource.Success(response.data.map { it.toDomain() })
        } catch (e: Exception) {
            Resource.Error(e.message ?: "Failed to load schedule")
        }
    }

    override suspend fun upsertAvailability(req: UpsertAvailabilityReq): Resource<DoctorAvailability> = withContext(Dispatchers.IO) {
        try {
            val response = client.post("scheduling/availability") {
                setBody(req)
            }.body<UpsertAvailabilityRes>()
            Resource.Success(response.data.toDomain())
        } catch (e: Exception) {
            Resource.Error(e.message ?: "Failed to save availability")
        }
    }

    override suspend fun getAppointments(filter: String?): Resource<List<Appointment>> = withContext(Dispatchers.IO) {
        try {
            val response = client.get("scheduling/appointments/doctor/all") {
                filter?.let { parameter("filter", it) }
            }.body<GetAppointmentsRes>()
            Resource.Success(response.data.map { it.toDomain() })
        } catch (e: Exception) {
            Resource.Error(e.message ?: "Failed to load appointments")
        }
    }

    override suspend fun confirmAppointment(id: String): Resource<Appointment> = withContext(Dispatchers.IO) {
        try {
            val response = client.patch("scheduling/appointments/confirm") {
                parameter("id", id)
            }.body<AppointmentActionRes>()
            Resource.Success(response.data.toDomain())
        } catch (e: Exception) {
            Resource.Error(e.message ?: "Failed to confirm appointment")
        }
    }

    override suspend fun completeAppointment(id: String): Resource<Appointment> = withContext(Dispatchers.IO) {
        try {
            val response = client.patch("scheduling/appointments/complete") {
                parameter("id", id)
            }.body<AppointmentActionRes>()
            Resource.Success(response.data.toDomain())
        } catch (e: Exception) {
            Resource.Error(e.message ?: "Failed to complete appointment")
        }
    }

    override suspend fun noShowAppointment(id: String): Resource<Appointment> = withContext(Dispatchers.IO) {
        try {
            val response = client.patch("scheduling/appointments/no-show") {
                parameter("id", id)
            }.body<AppointmentActionRes>()
            Resource.Success(response.data.toDomain())
        } catch (e: Exception) {
            Resource.Error(e.message ?: "Failed to mark no-show")
        }
    }

    override suspend fun cancelAppointment(id: String, reason: String?): Resource<Appointment> = withContext(Dispatchers.IO) {
        try {
            val response = client.patch("scheduling/appointments/cancel") {
                parameter("id", id)
                setBody(CancelAppointmentReq(reason))
            }.body<AppointmentActionRes>()
            Resource.Success(response.data.toDomain())
        } catch (e: Exception) {
            Resource.Error(e.message ?: "Failed to cancel appointment")
        }
    }
}
