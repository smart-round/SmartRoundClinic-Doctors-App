package ke.co.smartroundclinic.doctor.domain.usecase.referral

import ke.co.smartroundclinic.doctor.common.Resource
import ke.co.smartroundclinic.doctor.data.remote.dto.response.toDomain
import ke.co.smartroundclinic.doctor.domain.model.ReferralEligibility
import ke.co.smartroundclinic.doctor.domain.repository.ReferralRepository

class CheckReferralEligibilityUseCase(private val repository: ReferralRepository) {
    suspend operator fun invoke(appointmentId: String): Resource<ReferralEligibility> {
        val result = repository.checkEligibility(appointmentId)
        val data = result.data ?: return Resource.Error(result.message ?: "Failed to check referral eligibility")
        return Resource.Success(data.toDomain())
    }
}
