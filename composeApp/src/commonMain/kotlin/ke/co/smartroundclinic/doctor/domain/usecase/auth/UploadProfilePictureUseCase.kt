package ke.co.smartroundclinic.doctor.domain.usecase.auth

import ke.co.smartroundclinic.doctor.common.Resource
import ke.co.smartroundclinic.doctor.data.remote.dto.response.UploadProfilePictureRes
import ke.co.smartroundclinic.doctor.domain.repository.AuthRepository

class UploadProfilePictureUseCase(private val repository: AuthRepository) {
    suspend operator fun invoke(profilePicture: ByteArray): Resource<UploadProfilePictureRes> =
        repository.uploadProfilePicture(profilePicture)
}
