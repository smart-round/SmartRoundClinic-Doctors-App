package ke.co.smartroundclinic.doctor.domain.usecase.notification

import ke.co.smartroundclinic.doctor.common.Resource
import ke.co.smartroundclinic.doctor.domain.repository.NotificationRepository

class RegisterDeviceTokenUseCase(private val repository: NotificationRepository) {
    suspend operator fun invoke(token: String, platform: String): Resource<Unit> =
        repository.registerDeviceToken(token, platform)
}
