package ke.co.smartroundclinic.doctor.domain.usecase.auth

import ke.co.smartroundclinic.doctor.data.remote.dto.request.UpdatePasswordReq
import ke.co.smartroundclinic.doctor.domain.repository.AuthRepository

class UpdatePasswordUseCase(private val repository: AuthRepository) {
    suspend operator fun invoke(email: String, newPassword: String, otpCode: String) =
        repository.updatePassword(UpdatePasswordReq(email, newPassword, otpCode))
}
