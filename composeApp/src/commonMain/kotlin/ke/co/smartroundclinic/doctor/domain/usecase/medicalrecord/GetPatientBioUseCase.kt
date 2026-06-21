package ke.co.smartroundclinic.doctor.domain.usecase.medicalrecord

import ke.co.smartroundclinic.doctor.common.Resource
import ke.co.smartroundclinic.doctor.data.remote.dto.response.toDomain
import ke.co.smartroundclinic.doctor.domain.model.PatientBio
import ke.co.smartroundclinic.doctor.domain.repository.PatientBioRepository

class GetPatientBioUseCase(private val repository: PatientBioRepository) {
    suspend operator fun invoke(patientId: String): Resource<PatientBio?> =
        when (val result = repository.getPatientBio(patientId)) {
            is Resource.Success -> Resource.Success(data = result.data?.data?.toDomain())
            is Resource.Error -> Resource.Error(result.message ?: "Unknown error")
            is Resource.Loading -> Resource.Loading()
        }
}
