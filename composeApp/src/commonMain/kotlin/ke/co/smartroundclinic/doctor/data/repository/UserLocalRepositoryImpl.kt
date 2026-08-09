package ke.co.smartroundclinic.doctor.data.repository

import ke.co.smartroundclinic.doctor.core.database.dao.UserDao
import ke.co.smartroundclinic.doctor.core.database.entity.toDomain
import ke.co.smartroundclinic.doctor.core.database.entity.toEntity
import ke.co.smartroundclinic.doctor.domain.model.User
import ke.co.smartroundclinic.doctor.domain.repository.UserLocalRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class UserLocalRepositoryImpl(private val dao: UserDao) : UserLocalRepository {

    override fun observeUser(): Flow<User?> = dao.observeUser().map { it?.toDomain() }

    override suspend fun saveUser(user: User) = dao.replaceUser(user.toEntity())

    override suspend fun clearUser() = dao.clearUser()
}
