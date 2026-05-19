package ke.co.smartroundclinic.doctor.domain.usecase.licence

import ke.co.smartroundclinic.doctor.common.Resource
import ke.co.smartroundclinic.doctor.data.remote.dto.response.toDomain
import ke.co.smartroundclinic.doctor.domain.model.Licence
import ke.co.smartroundclinic.doctor.domain.repository.LicenceRepository

class UploadLicenceUseCase(private val repository: LicenceRepository) {
    suspend operator fun invoke(licenceName: String, licenceFileName: String, bytes: ByteArray): Resource<Licence> {
        val result = repository.uploadLicence(licenceName, licenceFileName, bytes)
        if (result is Resource.Success) {
            return Resource.Success(
                data = result.data?.data?.toDomain(),
                message = result.data?.message ?: "Licence uploaded successfully",
            )
        }
        return Resource.Error(result.message ?: "Failed to upload licence")
    }
}
