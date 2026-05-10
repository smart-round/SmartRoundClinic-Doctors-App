package ke.co.smartroundclinic.doctor.domain.usecase.scheduling

import ke.co.smartroundclinic.doctor.common.Resource
import ke.co.smartroundclinic.doctor.domain.model.DoctorAvailability
import ke.co.smartroundclinic.doctor.domain.repository.ScheduleLocalRepository
import ke.co.smartroundclinic.doctor.domain.repository.SchedulingRepository

class UpdateAvailabilityUseCase(
    private val remote: SchedulingRepository,
    private val local: ScheduleLocalRepository,
) {
    suspend operator fun invoke(day: Int, isActive: Boolean): Resource<DoctorAvailability> {
        val result = remote.updateAvailabilityDay(day, isActive)
        if (result is Resource.Success) result.data?.let { local.upsertEntry(it) }
        return result
    }
}
