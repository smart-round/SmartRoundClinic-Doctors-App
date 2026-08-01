package ke.co.smartroundclinic.doctor.data.repository

import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.get
import io.ktor.client.request.parameter
import ke.co.smartroundclinic.doctor.common.Resource
import ke.co.smartroundclinic.doctor.data.remote.dto.response.directory.GetRecommendedDoctorsRes
import ke.co.smartroundclinic.doctor.domain.repository.DoctorDirectoryRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.withContext

class DoctorDirectoryRepositoryImpl(private val client: HttpClient) : DoctorDirectoryRepository {

    override suspend fun getRecommendedDoctors(
        specializationId: String?,
        page: Int,
        size: Int,
        excludeDoctorId: String?,
    ) = withContext(Dispatchers.IO) {
        try {
            val response = client.get("doctor/recommendations") {
                specializationId?.let { parameter("specializationId", it) }
                excludeDoctorId?.let { parameter("excludeDoctorId", it) }
                parameter("page", page)
                parameter("size", size)
            }.body<GetRecommendedDoctorsRes>()
            Resource.Success(response.data, response.message)
        } catch (e: Exception) {
            Resource.Error(e.message ?: "An unknown error occurred")
        }
    }
}
