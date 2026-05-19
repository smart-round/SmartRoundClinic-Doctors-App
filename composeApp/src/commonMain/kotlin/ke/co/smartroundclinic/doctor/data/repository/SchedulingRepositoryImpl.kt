package ke.co.smartroundclinic.doctor.data.repository

import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.delete
import io.ktor.client.request.get
import io.ktor.client.request.parameter
import io.ktor.client.request.post
import io.ktor.client.request.put
import io.ktor.client.request.setBody
import ke.co.smartroundclinic.doctor.common.Resource
import ke.co.smartroundclinic.doctor.data.remote.dto.request.UpdateAvailabilityDayReq
import ke.co.smartroundclinic.doctor.data.remote.dto.request.UpsertAvailabilityReq
import ke.co.smartroundclinic.doctor.data.remote.dto.response.DeactivateScheduleRes
import ke.co.smartroundclinic.doctor.data.remote.dto.response.GetScheduleRes
import ke.co.smartroundclinic.doctor.data.remote.dto.response.UpsertAvailabilityRes
import ke.co.smartroundclinic.doctor.domain.repository.SchedulingRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.withContext

class SchedulingRepositoryImpl(private val client: HttpClient) : SchedulingRepository {

    override suspend fun getSchedule(): Resource<GetScheduleRes> = withContext(Dispatchers.IO) {
        try {
            Resource.Success(client.get("scheduling/availability/schedule").body<GetScheduleRes>())
        } catch (e: Exception) {
            Resource.Error(e.message ?: "Failed to load schedule")
        }
    }

    override suspend fun upsertAvailability(req: UpsertAvailabilityReq): Resource<UpsertAvailabilityRes> = withContext(Dispatchers.IO) {
        try {
            Resource.Success(client.post("scheduling/availability") { setBody(req) }.body<UpsertAvailabilityRes>())
        } catch (e: Exception) {
            Resource.Error(e.message ?: "Failed to save availability")
        }
    }

    override suspend fun updateAvailabilityDay(day: Int, isActive: Boolean): Resource<UpsertAvailabilityRes> = withContext(Dispatchers.IO) {
        try {
            Resource.Success(
                client.put("scheduling/availability") {
                    parameter("day", day)
                    setBody(UpdateAvailabilityDayReq(isActive))
                }.body<UpsertAvailabilityRes>()
            )
        } catch (e: Exception) {
            Resource.Error(e.message ?: "Failed to update day availability")
        }
    }

    override suspend fun deactivateSchedule(day: Int): Resource<DeactivateScheduleRes> = withContext(Dispatchers.IO) {
        try {
            val response = client.delete("scheduling/availability") {
                parameter(key = "day", value = day)
            }.body<DeactivateScheduleRes>()
            Resource.Success(data = response, message = response.message)
        } catch (e: Exception) {
            Resource.Error(e.message ?: "Failed to deactivate schedule")
        }
    }
}
