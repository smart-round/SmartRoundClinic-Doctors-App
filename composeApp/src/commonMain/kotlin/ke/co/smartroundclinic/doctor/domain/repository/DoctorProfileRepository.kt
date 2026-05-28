package ke.co.smartroundclinic.doctor.domain.repository

import ke.co.smartroundclinic.doctor.common.Resource
import ke.co.smartroundclinic.doctor.domain.model.DoctorProfile

interface DoctorProfileRepository {
    suspend fun getProfile(): Resource<DoctorProfile>
    suspend fun createProfile(): Resource<DoctorProfile>
    suspend fun updateProfile(
        title: String?,
        bio: String?,
        kmpdcRegNumber: String?,
        yearsOfExperience: Int?,
        languages: List<String>?,
        facilityName: String?,
    ): Resource<DoctorProfile>
}
