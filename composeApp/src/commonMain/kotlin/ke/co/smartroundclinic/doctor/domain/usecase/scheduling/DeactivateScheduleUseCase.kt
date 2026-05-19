package ke.co.smartroundclinic.doctor.domain.usecase.scheduling

import ke.co.smartroundclinic.doctor.common.Resource
import ke.co.smartroundclinic.doctor.data.remote.dto.response.toDomain
import ke.co.smartroundclinic.doctor.domain.repository.ScheduleLocalRepository
import ke.co.smartroundclinic.doctor.domain.repository.SchedulingRepository

class DeactivateScheduleUseCase(
    private val remote: SchedulingRepository,
    private val local: ScheduleLocalRepository,
) {
    suspend operator fun invoke(day: Int): Resource<Unit> {
        val result = remote.deactivateSchedule(day)
        if (result is Resource.Success) {
            result.data?.data?.toDomain()?.let { local.upsertEntry(it) }
        }
        return when (result) {
            is Resource.Success -> Resource.Success(Unit, result.message ?: "Success")
            is Resource.Error -> Resource.Error(result.message ?: "Failed to deactivate schedule")
            is Resource.Loading -> Resource.Loading()
        }
    }
}
