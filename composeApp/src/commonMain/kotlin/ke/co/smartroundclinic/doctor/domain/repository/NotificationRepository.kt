package ke.co.smartroundclinic.doctor.domain.repository

import ke.co.smartroundclinic.doctor.common.Resource
import ke.co.smartroundclinic.doctor.domain.model.Notification

interface NotificationRepository {
    suspend fun registerDeviceToken(token: String, platform: String): Resource<Unit>
    suspend fun getMyNotifications(page: Int = 1, size: Int = 50): Resource<List<Notification>>
    suspend fun markAsRead(id: String): Resource<Unit>
}
