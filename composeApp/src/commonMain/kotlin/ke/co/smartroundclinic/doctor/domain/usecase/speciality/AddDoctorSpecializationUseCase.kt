package ke.co.smartroundclinic.doctor.domain.usecase.speciality

import ke.co.smartroundclinic.doctor.common.Resource
import ke.co.smartroundclinic.doctor.data.remote.dto.request.AddSpecializationReq
import ke.co.smartroundclinic.doctor.data.remote.dto.response.AddSpecializationRes
import ke.co.smartroundclinic.doctor.domain.repository.SpecialityRepository

class AddDoctorSpecializationUseCase(private val repository: SpecialityRepository) {
    suspend operator fun invoke(specializationId: String): Resource<AddSpecializationRes> =
        repository.addSpecialization(AddSpecializationReq(specializationId))
}
