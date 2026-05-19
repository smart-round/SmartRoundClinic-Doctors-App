package ke.co.smartroundclinic.doctor.domain.repository

import ke.co.smartroundclinic.doctor.common.Resource
import ke.co.smartroundclinic.doctor.data.remote.dto.request.UpsertAvailabilityReq
import ke.co.smartroundclinic.doctor.data.remote.dto.response.DeactivateScheduleRes
import ke.co.smartroundclinic.doctor.data.remote.dto.response.GetScheduleRes
import ke.co.smartroundclinic.doctor.data.remote.dto.response.UpsertAvailabilityRes

interface SchedulingRepository {
    suspend fun getSchedule(): Resource<GetScheduleRes>
    suspend fun upsertAvailability(req: UpsertAvailabilityReq): Resource<UpsertAvailabilityRes>
    suspend fun updateAvailabilityDay(day: Int, isActive: Boolean): Resource<UpsertAvailabilityRes>
    suspend fun deactivateSchedule(day: Int): Resource<DeactivateScheduleRes>
}
