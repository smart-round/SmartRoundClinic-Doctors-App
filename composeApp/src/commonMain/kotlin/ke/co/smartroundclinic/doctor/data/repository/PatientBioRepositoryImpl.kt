package ke.co.smartroundclinic.doctor.data.repository

import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.get
import ke.co.smartroundclinic.doctor.common.Resource
import ke.co.smartroundclinic.doctor.data.remote.dto.response.PatientBioResponse
import ke.co.smartroundclinic.doctor.domain.repository.PatientBioRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.withContext

class PatientBioRepositoryImpl(private val client: HttpClient) : PatientBioRepository {
    override suspend fun getPatientBio(patientId: String): Resource<PatientBioResponse> =
        withContext(Dispatchers.IO) {
            try {
                val response = client.get("patient/personal-information?id=$patientId")
                    .body<PatientBioResponse>()
                if (response.status) Resource.Success(data = response, message = response.message)
                else Resource.Error(message = response.message)
            } catch (e: Exception) {
                Resource.Error(e.message ?: "Failed to load patient info")
            }
        }
}
