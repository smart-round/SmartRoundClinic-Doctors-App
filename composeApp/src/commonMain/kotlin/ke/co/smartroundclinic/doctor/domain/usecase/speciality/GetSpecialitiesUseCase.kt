package ke.co.smartroundclinic.doctor.domain.usecase.speciality

import ke.co.smartroundclinic.doctor.common.Resource
import ke.co.smartroundclinic.doctor.domain.model.Speciality
import ke.co.smartroundclinic.doctor.domain.repository.SpecialityLocalRepository
import ke.co.smartroundclinic.doctor.domain.repository.SpecialityRepository

class GetSpecialitiesUseCase(
    private val remote: SpecialityRepository,
    private val local: SpecialityLocalRepository
) {
    suspend operator fun invoke(): Resource<List<Speciality>> {
        val cached = local.getSpecialities()
        if (cached.isNotEmpty()) return Resource.Success(cached)
        val result = remote.getAllSpecialities()
        result.data?.let { local.saveSpecialities(it) }
        return result
    }
}
