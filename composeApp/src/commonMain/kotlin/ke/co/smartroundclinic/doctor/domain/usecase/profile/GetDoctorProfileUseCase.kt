package ke.co.smartroundclinic.doctor.domain.usecase.profile

import ke.co.smartroundclinic.doctor.common.Resource
import ke.co.smartroundclinic.doctor.domain.model.DoctorProfile
import ke.co.smartroundclinic.doctor.domain.repository.DoctorProfileRepository

class GetDoctorProfileUseCase(private val repository: DoctorProfileRepository) {
    suspend operator fun invoke(): Resource<DoctorProfile> {
        val result = repository.getProfile()
        // Profile may not exist for new/old accounts — create it on first fetch
        if (result is Resource.Error) {
            return repository.createProfile()
        }
        return result
    }
}
