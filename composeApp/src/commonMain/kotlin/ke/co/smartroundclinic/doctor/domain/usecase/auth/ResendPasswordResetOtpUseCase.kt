package ke.co.smartroundclinic.doctor.domain.usecase.auth

import ke.co.smartroundclinic.doctor.domain.repository.AuthRepository

class ResendPasswordResetOtpUseCase(private val repository: AuthRepository) {
    suspend operator fun invoke(email: String) = repository.resendPasswordResetOtp(email)
}
