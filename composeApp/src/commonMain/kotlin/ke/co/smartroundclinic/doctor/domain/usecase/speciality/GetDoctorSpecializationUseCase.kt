package ke.co.smartroundclinic.doctor.domain.usecase.speciality

import ke.co.smartroundclinic.doctor.common.Resource
import ke.co.smartroundclinic.doctor.data.remote.dto.response.toDomain
import ke.co.smartroundclinic.doctor.domain.model.DoctorSpecialization
import ke.co.smartroundclinic.doctor.domain.repository.DoctorSpecializationLocalRepository
import ke.co.smartroundclinic.doctor.domain.repository.SpecialityRepository

class GetDoctorSpecializationUseCase(
    private val repository: SpecialityRepository,
    private val localRepository: DoctorSpecializationLocalRepository,
) {
    suspend operator fun invoke(): Resource<DoctorSpecialization?> {
        return when (val result = repository.getSpecialization()) {
            is Resource.Success -> {
                val first = result.data?.data?.firstOrNull()?.toDomain()
                if (first != null) {
                    localRepository.saveSpecialization(first)
                } else {
                    localRepository.clearSpecializations()
                }
                Resource.Success(first, result.message ?: "Success")
            }
            is Resource.Error -> Resource.Error(result.message ?: "Failed to fetch specialization")
            is Resource.Loading -> Resource.Loading()
        }
    }
}
