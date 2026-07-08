package ke.co.smartroundclinic.doctor.data.repository

import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.delete
import io.ktor.client.request.get
import io.ktor.client.request.parameter
import io.ktor.client.request.put
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import ke.co.smartroundclinic.doctor.common.Resource
import ke.co.smartroundclinic.doctor.data.remote.dto.request.SubmitPatientRatingReq
import ke.co.smartroundclinic.doctor.data.remote.dto.request.UpdateRatingReq
import ke.co.smartroundclinic.doctor.data.remote.dto.response.RatingListResponse
import ke.co.smartroundclinic.doctor.data.remote.dto.response.RatingResponse
import ke.co.smartroundclinic.doctor.data.remote.dto.response.toDomain
import ke.co.smartroundclinic.doctor.domain.model.Rating
import ke.co.smartroundclinic.doctor.domain.model.RatingPage
import ke.co.smartroundclinic.doctor.domain.repository.PatientRatingRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.withContext

class PatientRatingRepositoryImpl(private val client: HttpClient) : PatientRatingRepository {

    override suspend fun submitRating(
        appointmentId: String,
        patientId: String,
        rating: Int,
        comment: String?,
    ): Resource<Rating> = withContext(Dispatchers.IO) {
        try {
            val response = client.post("patient/ratings") {
                setBody(SubmitPatientRatingReq(appointmentId, patientId, rating, comment))
            }.body<RatingResponse>()
            if (response.status && response.data != null) {
                Resource.Success(response.data.toDomain(), response.message)
            } else {
                Resource.Error(response.message)
            }
        } catch (e: Exception) {
            Resource.Error(e.message ?: "Failed to submit rating")
        }
    }

    override suspend fun updateRating(id: String, rating: Int, comment: String?): Resource<Rating> =
        withContext(Dispatchers.IO) {
            try {
                val response = client.put("patient/ratings") {
                    parameter("id", id)
                    setBody(UpdateRatingReq(rating, comment))
                }.body<RatingResponse>()
                if (response.status && response.data != null) {
                    Resource.Success(response.data.toDomain(), response.message)
                } else {
                    Resource.Error(response.message)
                }
            } catch (e: Exception) {
                Resource.Error(e.message ?: "Failed to update rating")
            }
        }

    override suspend fun deleteRating(id: String): Resource<Unit> = withContext(Dispatchers.IO) {
        try {
            val response = client.delete("patient/ratings") {
                parameter("id", id)
            }.body<RatingResponse>()
            if (response.status) Resource.Success(Unit, response.message) else Resource.Error(response.message)
        } catch (e: Exception) {
            Resource.Error(e.message ?: "Failed to delete rating")
        }
    }

    override suspend fun getRatings(patientId: String, page: Int, size: Int): Resource<List<Rating>> =
        withContext(Dispatchers.IO) {
            try {
                val response = client.get("patient/ratings") {
                    parameter("patientId", patientId)
                    parameter("page", page)
                    parameter("size", size)
                }.body<RatingListResponse>()
                if (response.status) {
                    Resource.Success(response.data?.items?.map { it.toDomain() } ?: emptyList())
                } else {
                    Resource.Error(response.message)
                }
            } catch (e: Exception) {
                Resource.Error(e.message ?: "Failed to load ratings")
            }
        }

    override suspend fun getRatingsPage(patientId: String, page: Int, size: Int): Resource<RatingPage> =
        withContext(Dispatchers.IO) {
            try {
                val response = client.get("patient/ratings") {
                    parameter("patientId", patientId)
                    parameter("page", page)
                    parameter("size", size)
                }.body<RatingListResponse>()
                if (response.status) {
                    val data = response.data
                    Resource.Success(
                        RatingPage(
                            items = data?.items?.map { it.toDomain() } ?: emptyList(),
                            total = data?.total ?: 0,
                            page = data?.page ?: page,
                            size = data?.size ?: size,
                        ),
                    )
                } else {
                    Resource.Error(response.message)
                }
            } catch (e: Exception) {
                Resource.Error(e.message ?: "Failed to load ratings")
            }
        }
}
