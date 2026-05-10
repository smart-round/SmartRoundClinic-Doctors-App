package ke.co.smartroundclinic.doctor.domain.usecase.auth

import ke.co.smartroundclinic.doctor.common.Resource
import ke.co.smartroundclinic.doctor.data.remote.dto.response.toDomain
import ke.co.smartroundclinic.doctor.domain.model.User
import ke.co.smartroundclinic.doctor.domain.repository.AuthRepository
import ke.co.smartroundclinic.doctor.domain.repository.UserLocalRepository

class GetUserUseCase(
    private val authRepository: AuthRepository,
    private val userLocalRepository: UserLocalRepository,
) {
    suspend operator fun invoke(): Resource<User> {
        val result = authRepository.getUser()
        return when (result) {
            is Resource.Success -> {
                val user = result.data?.data?.toDomain()
                user?.let { userLocalRepository.saveUser(it) }
                Resource.Success(data = user, message = result.message ?: "Success")
            }
            is Resource.Error -> Resource.Error(result.message ?: "Failed")
            is Resource.Loading -> Resource.Loading()
        }
    }
}
