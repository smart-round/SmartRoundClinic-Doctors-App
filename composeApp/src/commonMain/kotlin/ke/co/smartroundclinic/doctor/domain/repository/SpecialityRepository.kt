package ke.co.smartroundclinic.doctor.domain.repository

import ke.co.smartroundclinic.doctor.common.Resource
import ke.co.smartroundclinic.doctor.data.remote.dto.request.AddSpecializationReq
import ke.co.smartroundclinic.doctor.data.remote.dto.request.UpdateSpecializationReq
import ke.co.smartroundclinic.doctor.data.remote.dto.response.AddSpecializationRes
import ke.co.smartroundclinic.doctor.data.remote.dto.response.DeleteSpecializationRes
import ke.co.smartroundclinic.doctor.data.remote.dto.response.GetSpecializationRes
import ke.co.smartroundclinic.doctor.data.remote.dto.response.UpdateSpecializationData
import ke.co.smartroundclinic.doctor.data.remote.dto.response.UpdateSpecializationRes
import ke.co.smartroundclinic.doctor.domain.model.Speciality

interface SpecialityRepository {
    suspend fun getAllSpecialities(): Resource<List<Speciality>>
    suspend fun getSpecialization(): Resource<GetSpecializationRes>
    suspend fun addSpecialization(body: AddSpecializationReq): Resource<AddSpecializationRes>
    suspend fun updateSpecialization(body: UpdateSpecializationReq, id: String): Resource<UpdateSpecializationRes>
    suspend fun deleteSpecialization(id:String): Resource<DeleteSpecializationRes>
}
