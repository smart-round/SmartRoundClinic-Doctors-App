package ke.co.smartroundclinic.doctor.data.repository

import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.get
import io.ktor.client.request.post
import io.ktor.client.request.put
import io.ktor.client.request.setBody
import ke.co.smartroundclinic.doctor.common.Resource
import ke.co.smartroundclinic.doctor.data.remote.dto.request.UpdateDoctorProfileReq
import ke.co.smartroundclinic.doctor.data.remote.dto.response.DoctorProfileRes
import ke.co.smartroundclinic.doctor.data.remote.dto.response.toDomain
import ke.co.smartroundclinic.doctor.domain.model.DoctorProfile
import ke.co.smartroundclinic.doctor.domain.repository.DoctorProfileRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.withContext

class DoctorProfileRepositoryImpl(
    private val client: HttpClient,
) : DoctorProfileRepository {

    override suspend fun getProfile(): Resource<DoctorProfile> = withContext(Dispatchers.IO) {
        try {
            val res = client.get("doctor/profile/me").body<DoctorProfileRes>()
            Resource.Success(res.data.toDomain(), res.message)
        } catch (e: Exception) {
            Resource.Error(e.message ?: "Failed to load profile")
        }
    }

    override suspend fun createProfile(): Resource<DoctorProfile> = withContext(Dispatchers.IO) {
        try {
            val res = client.post("doctor/profile").body<DoctorProfileRes>()
            Resource.Success(res.data.toDomain(), res.message)
        } catch (e: Exception) {
            Resource.Error(e.message ?: "Failed to create profile")
        }
    }

    override suspend fun updateProfile(
        title: String?,
        bio: String?,
        kmpdcRegNumber: String?,
        yearsOfExperience: Int?,
        languages: List<String>?,
        facilityName: String?,
    ): Resource<DoctorProfile> = withContext(Dispatchers.IO) {
        try {
            val res = client.put("doctor/profile") {
                setBody(UpdateDoctorProfileReq(
                    title = title,
                    bio = bio,
                    kmpdcRegNumber = kmpdcRegNumber,
                    yearsOfExperience = yearsOfExperience,
                    languages = languages,
                    facilityName = facilityName,
                ))
            }.body<DoctorProfileRes>()
            Resource.Success(res.data.toDomain(), res.message)
        } catch (e: Exception) {
            Resource.Error(e.message ?: "Failed to update profile")
        }
    }
}
