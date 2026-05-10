package ke.co.smartroundclinic.doctor.domain.usecase.speciality

import ke.co.smartroundclinic.doctor.common.Resource
import ke.co.smartroundclinic.doctor.data.remote.dto.request.UpdateSpecializationReq
import ke.co.smartroundclinic.doctor.data.remote.dto.response.UpdateSpecializationRes
import ke.co.smartroundclinic.doctor.domain.repository.SpecialityRepository

class UpdateDoctorSpecializationUseCase(private val repository: SpecialityRepository) {
    suspend operator fun invoke(id: String, specializationId: String): Resource<UpdateSpecializationRes> =
        repository.updateSpecialization(UpdateSpecializationReq(specializationId), id)
}
