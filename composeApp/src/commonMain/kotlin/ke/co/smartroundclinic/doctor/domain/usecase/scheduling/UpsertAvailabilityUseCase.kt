package ke.co.smartroundclinic.doctor.domain.usecase.scheduling

import ke.co.smartroundclinic.doctor.common.Resource
import ke.co.smartroundclinic.doctor.data.remote.dto.request.UpsertAvailabilityReq
import ke.co.smartroundclinic.doctor.data.remote.dto.response.toDomain
import ke.co.smartroundclinic.doctor.domain.model.DoctorAvailability
import ke.co.smartroundclinic.doctor.domain.repository.ScheduleLocalRepository
import ke.co.smartroundclinic.doctor.domain.repository.SchedulingRepository

class UpsertAvailabilityUseCase(
    private val remote: SchedulingRepository,
    private val local: ScheduleLocalRepository,
) {
    suspend operator fun invoke(req: UpsertAvailabilityReq): Resource<DoctorAvailability> {
        val result = remote.upsertAvailability(req)
        if (result is Resource.Success) {
            val entry = result.data?.data?.toDomain() ?: return Resource.Error("No data returned")
            local.upsertEntry(entry)
            return Resource.Success(entry)
        }
        return Resource.Error(result.message ?: "Failed to save availability")
    }
}
