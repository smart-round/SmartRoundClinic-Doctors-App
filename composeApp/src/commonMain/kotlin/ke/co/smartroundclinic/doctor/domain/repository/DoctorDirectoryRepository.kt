package ke.co.smartroundclinic.doctor.domain.repository

import ke.co.smartroundclinic.doctor.common.Resource
import ke.co.smartroundclinic.doctor.data.remote.dto.response.directory.GetDoctorByIdRes
import ke.co.smartroundclinic.doctor.data.remote.dto.response.directory.RecommendedDoctorsPageData

/** Browses other doctors on the platform — used by the referral picker and the Services/chat doctor search. */
interface DoctorDirectoryRepository {
    suspend fun getRecommendedDoctors(
        specializationId: String?,
        page: Int,
        size: Int,
        excludeDoctorId: String?,
    ): Resource<RecommendedDoctorsPageData>

    suspend fun getDoctorById(doctorId: String): Resource<GetDoctorByIdRes>
}
