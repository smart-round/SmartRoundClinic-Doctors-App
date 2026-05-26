package ke.co.smartroundclinic.doctor.domain.repository

import ke.co.smartroundclinic.doctor.common.Resource

interface NotificationRepository {
    suspend fun registerDeviceToken(token: String, platform: String): Resource<Unit>
}
