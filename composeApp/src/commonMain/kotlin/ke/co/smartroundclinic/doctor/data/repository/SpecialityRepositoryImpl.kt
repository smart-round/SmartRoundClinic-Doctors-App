package ke.co.smartroundclinic.doctor.data.repository

import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.get
import io.ktor.client.request.parameter
import io.ktor.client.request.post
import io.ktor.client.request.put
import io.ktor.client.request.setBody
import ke.co.smartroundclinic.doctor.common.Resource
import ke.co.smartroundclinic.doctor.data.remote.dto.request.AddSpecializationReq
import ke.co.smartroundclinic.doctor.data.remote.dto.request.UpdateSpecializationReq
import ke.co.smartroundclinic.doctor.data.remote.dto.response.AddSpecializationRes
import ke.co.smartroundclinic.doctor.data.remote.dto.response.DeleteSpecializationRes
import ke.co.smartroundclinic.doctor.data.remote.dto.response.GetSpecializationRes
import ke.co.smartroundclinic.doctor.data.remote.dto.response.UpdateSpecializationRes
import ke.co.smartroundclinic.doctor.data.remote.dto.response.specialities.GetSpecialitiesRes
import ke.co.smartroundclinic.doctor.data.remote.dto.response.specialities.toModel
import ke.co.smartroundclinic.doctor.domain.model.Speciality
import ke.co.smartroundclinic.doctor.domain.repository.SpecialityRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.withContext

class SpecialityRepositoryImpl(private val client: HttpClient) : SpecialityRepository {

    override suspend fun getAllSpecialities(): Resource<List<Speciality>> = withContext(Dispatchers.IO) {
        try {
            Resource.Success(client.get("public/specialities").body<GetSpecialitiesRes>().toModel())
        } catch (e: Exception) {
            Resource.Error(e.message ?: "An unknown error occurred")
        }
    }

    override suspend fun getSpecialization(): Resource<GetSpecializationRes>  = withContext(
        Dispatchers.IO){
        try {
            val response = client.get("/doctor/specializations").body<GetSpecializationRes>()
            Resource.Success(response, response.message)
        } catch (e: Exception) {
            Resource.Error(e.message ?: "An unknown error occurred")
        }
    }

    override suspend fun addSpecialization(body: AddSpecializationReq): Resource<AddSpecializationRes>  = withContext(
        Dispatchers.IO){
        try {
            val response = client.post("/doctor/specializations"){
                setBody(body)
            }.body<AddSpecializationRes>()
            Resource.Success(response, response.message)
        } catch (e: Exception) {
            Resource.Error(e.message ?: "An unknown error occurred")
        }
    }

    override suspend fun updateSpecialization(body: UpdateSpecializationReq, id: String): Resource<UpdateSpecializationRes>  = withContext(
        Dispatchers.IO){
        try {
            val response = client.put("/doctor/specializations"){
                parameter(key = "id", id)
                setBody(body)
            }.body<UpdateSpecializationRes>()
            Resource.Success(response, response.message)
        } catch (e: Exception) {
            Resource.Error(e.message ?: "An unknown error occurred")
        }
    }

    override suspend fun deleteSpecialization(id: String): Resource<DeleteSpecializationRes> = withContext(
        Dispatchers.IO) {
        try {
            val response = client.get("/doctor/specializations"){
                parameter(key = "id", id)
            }.body<DeleteSpecializationRes>()
            Resource.Success(response, response.message)
        } catch (e: Exception) {
            Resource.Error(e.message ?: "An unknown error occurred")
        }
    }
}
