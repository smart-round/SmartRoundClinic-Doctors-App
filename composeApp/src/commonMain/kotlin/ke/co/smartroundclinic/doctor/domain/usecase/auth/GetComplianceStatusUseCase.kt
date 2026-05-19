package ke.co.smartroundclinic.doctor.domain.usecase.auth

import ke.co.smartroundclinic.doctor.common.Resource
import ke.co.smartroundclinic.doctor.data.remote.dto.response.toDomain
import ke.co.smartroundclinic.doctor.domain.model.ComplianceStatus
import ke.co.smartroundclinic.doctor.domain.repository.AuthRepository

class GetComplianceStatusUseCase(private val remote: AuthRepository) {
    suspend operator fun invoke(): Resource<ComplianceStatus> {
        val result = remote.getComplianceStatus()
        if (result is Resource.Success) {
            val status = result.data?.data?.toDomain() ?: return Resource.Error("No compliance data")
            return Resource.Success(status)
        }
        return Resource.Error(result.message ?: "Failed to fetch compliance status")
    }
}
