package ke.co.smartroundclinic.doctor.domain.usecase.scheduling

import ke.co.smartroundclinic.doctor.common.Resource
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
            val data = result.data ?: emptyList()
            local.upsertAll(data)
        }
        return result
    }
}
