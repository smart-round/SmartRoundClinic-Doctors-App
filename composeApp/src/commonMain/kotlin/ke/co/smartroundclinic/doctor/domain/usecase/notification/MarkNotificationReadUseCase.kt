package ke.co.smartroundclinic.doctor.domain.usecase.notification

import ke.co.smartroundclinic.doctor.common.Resource
import ke.co.smartroundclinic.doctor.domain.repository.NotificationRepository

class MarkNotificationReadUseCase(private val repository: NotificationRepository) {
    suspend operator fun invoke(id: String): Resource<Unit> = repository.markAsRead(id)
}
