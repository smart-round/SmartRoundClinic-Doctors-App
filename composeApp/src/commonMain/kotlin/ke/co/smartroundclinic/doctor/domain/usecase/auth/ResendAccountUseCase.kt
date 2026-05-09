package ke.co.smartroundclinic.doctor.domain.usecase.auth

import ke.co.smartroundclinic.doctor.common.Resource
import ke.co.smartroundclinic.doctor.data.remote.dto.response.SuccessRes
import ke.co.smartroundclinic.doctor.domain.repository.AuthRepository

class ResendAccountUseCase(private val repository: AuthRepository) {
    suspend operator fun invoke(email: String): Resource<SuccessRes> =
        repository.resendAccount(email)
}
