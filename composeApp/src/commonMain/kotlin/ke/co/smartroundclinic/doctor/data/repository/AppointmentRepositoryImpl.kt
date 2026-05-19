package ke.co.smartroundclinic.doctor.data.repository

import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.get
import io.ktor.client.request.parameter
import io.ktor.client.request.patch
import io.ktor.client.request.setBody
import ke.co.smartroundclinic.doctor.common.Resource
import ke.co.smartroundclinic.doctor.data.remote.dto.request.CancelAppointmentReq
import ke.co.smartroundclinic.doctor.data.remote.dto.response.GetAppointmentsRes
import ke.co.smartroundclinic.doctor.data.remote.dto.response.SuccessRes
import ke.co.smartroundclinic.doctor.domain.repository.AppointmentRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.withContext

class AppointmentRepositoryImpl(private val client: HttpClient) : AppointmentRepository {

    override suspend fun getAppointments(filter: String?): Resource<GetAppointmentsRes> = withContext(Dispatchers.IO) {
        try {
            Resource.Success(
                client.get("scheduling/appointments/doctor/all") {
                    filter?.let { parameter("filter", it) }
                }.body<GetAppointmentsRes>()
            )
        } catch (e: Exception) {
            Resource.Error(e.message ?: "Failed to load appointments")
        }
    }

    override suspend fun confirmAppointment(id: String): Resource<SuccessRes> = withContext(Dispatchers.IO) {
        try {
            val res = client.patch("scheduling/appointments/confirm") { parameter("id", id) }.body<SuccessRes>()
            Resource.Success(res, res.message)
        } catch (e: Exception) {
            Resource.Error(e.message ?: "Failed to confirm appointment")
        }
    }

    override suspend fun completeAppointment(id: String): Resource<SuccessRes> = withContext(Dispatchers.IO) {
        try {
            val res = client.patch("scheduling/appointments/complete") { parameter("id", id) }.body<SuccessRes>()
            Resource.Success(res, res.message)
        } catch (e: Exception) {
            Resource.Error(e.message ?: "Failed to complete appointment")
        }
    }

    override suspend fun noShowAppointment(id: String): Resource<SuccessRes> = withContext(Dispatchers.IO) {
        try {
            val res = client.patch("scheduling/appointments/no-show") { parameter("id", id) }.body<SuccessRes>()
            Resource.Success(res, res.message)
        } catch (e: Exception) {
            Resource.Error(e.message ?: "Failed to mark no-show")
        }
    }

    override suspend fun cancelAppointment(id: String, reason: String?): Resource<SuccessRes> = withContext(Dispatchers.IO) {
        try {
            val res = client.patch("scheduling/appointments/cancel") {
                parameter("id", id)
                setBody(CancelAppointmentReq(reason))
            }.body<SuccessRes>()
            Resource.Success(res, res.message)
        } catch (e: Exception) {
            Resource.Error(e.message ?: "Failed to cancel appointment")
        }
    }
}
