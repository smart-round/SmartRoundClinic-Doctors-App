package ke.co.smartroundclinic.doctor.domain.usecase.auth

import ke.co.smartroundclinic.doctor.common.Resource
import ke.co.smartroundclinic.doctor.data.remote.dto.response.DeleteProfilePictureRes
import ke.co.smartroundclinic.doctor.domain.repository.AuthRepository

class DeleteProfilePictureUseCase(private val repository: AuthRepository) {
    suspend operator fun invoke(): Resource<DeleteProfilePictureRes> =
        repository.deleteProfilePicture()
}
