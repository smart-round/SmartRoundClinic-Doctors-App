package ke.co.smartroundclinic.doctor.domain.usecase.scheduling

import ke.co.smartroundclinic.doctor.common.Resource
import ke.co.smartroundclinic.doctor.data.remote.dto.response.toDomain
import ke.co.smartroundclinic.doctor.domain.model.DoctorAvailability
import ke.co.smartroundclinic.doctor.domain.repository.ScheduleLocalRepository
import ke.co.smartroundclinic.doctor.domain.repository.SchedulingRepository

class GetScheduleUseCase(
    private val remote: SchedulingRepository,
    private val local: ScheduleLocalRepository,
) {
    suspend operator fun invoke(): Resource<List<DoctorAvailability>> {
        val result = remote.getSchedule()
        if (result is Resource.Success) {
            val entries = result.data?.data?.map { it.toDomain() } ?: emptyList()
            if (entries.isNotEmpty()) local.upsertAll(entries)
            return Resource.Success(entries)
        }
        return Resource.Error(result.message ?: "Failed to load schedule")
    }
}
