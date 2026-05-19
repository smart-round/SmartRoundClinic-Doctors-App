package ke.co.smartroundclinic.doctor.domain.usecase.licence

import ke.co.smartroundclinic.doctor.common.Resource
import ke.co.smartroundclinic.doctor.data.remote.dto.response.SuccessRes
import ke.co.smartroundclinic.doctor.domain.repository.LicenceRepository

class DeleteLicenceUseCase(private val repository: LicenceRepository) {
    suspend operator fun invoke(id: String): Resource<SuccessRes> = repository.deleteMyLicence(id)
}
