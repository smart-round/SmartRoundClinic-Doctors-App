package ke.co.smartroundclinic.doctor.data.repository

import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.get
import ke.co.smartroundclinic.doctor.common.Resource
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
}
