package ke.co.smartroundclinic.doctor.domain.usecase.licence

import ke.co.smartroundclinic.doctor.common.Resource
import ke.co.smartroundclinic.doctor.data.remote.dto.response.toDomain
import ke.co.smartroundclinic.doctor.domain.model.Licence
import ke.co.smartroundclinic.doctor.domain.repository.LicenceRepository

class GetAllLicencesUseCase(private val repository: LicenceRepository) {
    suspend operator fun invoke(): Resource<List<Licence>> {
        val result = repository.getAllMyLicences()
        if (result is Resource.Success) {
            return Resource.Success(result.data?.data?.map { it.toDomain() } ?: emptyList())
        }
        return Resource.Error(result.message ?: "Failed to load licences")
    }
}
