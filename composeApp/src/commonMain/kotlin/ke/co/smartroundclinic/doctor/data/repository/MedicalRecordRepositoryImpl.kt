package ke.co.smartroundclinic.doctor.data.repository

import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.get
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import ke.co.smartroundclinic.doctor.common.Resource
import ke.co.smartroundclinic.doctor.data.remote.dto.request.SaveMedicalRecordReq
import ke.co.smartroundclinic.doctor.data.remote.dto.response.MedicalHistoryResponse
import ke.co.smartroundclinic.doctor.data.remote.dto.response.MedicalRecordResponse
import ke.co.smartroundclinic.doctor.data.remote.dto.response.toDomain
import ke.co.smartroundclinic.doctor.domain.model.MedicalRecord
import ke.co.smartroundclinic.doctor.domain.repository.MedicalRecordRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.withContext

class MedicalRecordRepositoryImpl(private val client: HttpClient) : MedicalRecordRepository {

    override suspend fun save(req: SaveMedicalRecordReq): Resource<MedicalRecord?> =
        withContext(Dispatchers.IO) {
            try {
                val response = client.post("medical-records") {
                    setBody(req)
                }.body<MedicalRecordResponse>()
                if (response.status) {
                    Resource.Success(response.data?.toDomain(), response.message)
                } else {
                    Resource.Error(response.message)
                }
            } catch (e: Exception) {
                Resource.Error(e.message ?: "Failed to save medical record")
            }
        }

    override suspend fun getByAppointmentId(appointmentId: String): Resource<MedicalRecord?> =
        withContext(Dispatchers.IO) {
            try {
                val response = client.get("medical-records/appointment/$appointmentId").body<MedicalRecordResponse>()
                if (response.status) {
                    Resource.Success(response.data?.toDomain(), response.message)
                } else {
                    Resource.Error(response.message)
                }
            } catch (e: Exception) {
                Resource.Error(e.message ?: "Failed to load medical record")
            }
        }

    override suspend fun getPatientHistory(patientId: String): Resource<List<MedicalRecord>> =
        withContext(Dispatchers.IO) {
            try {
                val response = client.get("medical-records/patient/$patientId").body<MedicalHistoryResponse>()
                if (response.status) {
                    Resource.Success(response.data?.map { it.toDomain() } ?: emptyList(), response.message)
                } else {
                    Resource.Error(response.message)
                }
            } catch (e: Exception) {
                Resource.Error(e.message ?: "Failed to load medical history")
            }
        }
}
