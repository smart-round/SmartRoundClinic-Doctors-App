package ke.co.smartroundclinic.doctor.domain.usecase.referral

import ke.co.smartroundclinic.doctor.common.Resource
import ke.co.smartroundclinic.doctor.data.remote.dto.response.toDomain
import ke.co.smartroundclinic.doctor.domain.model.Referral
import ke.co.smartroundclinic.doctor.domain.repository.ReferralRepository

class GetSentReferralsUseCase(private val repository: ReferralRepository) {
    suspend operator fun invoke(): Resource<List<Referral>> =
        when (val result = repository.getMyReferrals()) {
            is Resource.Success -> Resource.Success(result.data?.map { it.toDomain() } ?: emptyList())
            is Resource.Error -> Resource.Error(result.message ?: "Failed to load sent referrals")
            is Resource.Loading -> Resource.Loading()
        }
}
