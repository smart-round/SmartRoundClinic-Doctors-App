package ke.co.smartroundclinic.doctor.domain.repository

import ke.co.smartroundclinic.doctor.domain.model.User
import kotlinx.coroutines.flow.Flow

interface UserLocalRepository {
    fun observeUser(): Flow<User?>
    suspend fun saveUser(user: User)
    suspend fun clearUser()
}
