package ke.co.smartroundclinic.doctor.domain.usecase.auth

import ke.co.smartroundclinic.doctor.common.Resource
import ke.co.smartroundclinic.doctor.data.remote.dto.request.UpdatePersonalInformationReq
import ke.co.smartroundclinic.doctor.data.remote.dto.response.UpdatePersonalInformationRes
import ke.co.smartroundclinic.doctor.domain.repository.AuthRepository

class UpdatePersonalInfoUseCase(private val repository: AuthRepository) {
    suspend operator fun invoke(body: UpdatePersonalInformationReq): Resource<UpdatePersonalInformationRes> =
        repository.updatePersonalInfo(body)
}
