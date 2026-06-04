package ke.co.smartroundclinic.doctor.domain.usecase.notification

import ke.co.smartroundclinic.doctor.common.Resource
import ke.co.smartroundclinic.doctor.domain.model.Notification
import ke.co.smartroundclinic.doctor.domain.repository.NotificationRepository

class GetMyNotificationsUseCase(private val repository: NotificationRepository) {
    suspend operator fun invoke(): Resource<List<Notification>> =
        repository.getMyNotifications()
}
