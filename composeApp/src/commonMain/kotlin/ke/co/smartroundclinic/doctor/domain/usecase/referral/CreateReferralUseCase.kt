package ke.co.smartroundclinic.doctor.domain.usecase.referral

import ke.co.smartroundclinic.doctor.common.Resource
import ke.co.smartroundclinic.doctor.data.remote.dto.request.CreateReferralReq
import ke.co.smartroundclinic.doctor.data.remote.dto.response.toDomain
import ke.co.smartroundclinic.doctor.domain.model.Referral
import ke.co.smartroundclinic.doctor.domain.repository.ReferralRepository

class CreateReferralUseCase(private val repository: ReferralRepository) {
    suspend operator fun invoke(appointmentId: String, receivingDoctorId: String): Resource<Referral> {
        val result = repository.createReferral(CreateReferralReq(appointmentId, receivingDoctorId))
        val data = result.data ?: return Resource.Error(result.message ?: "Failed to create referral")
        return Resource.Success(data.toDomain(), result.message ?: "Referral created")
    }
}
