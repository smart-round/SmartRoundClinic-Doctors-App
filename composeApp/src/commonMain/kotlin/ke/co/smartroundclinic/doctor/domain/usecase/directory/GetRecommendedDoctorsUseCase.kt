package ke.co.smartroundclinic.doctor.domain.usecase.directory

import ke.co.smartroundclinic.doctor.common.Resource
import ke.co.smartroundclinic.doctor.data.remote.dto.response.directory.toDomain
import ke.co.smartroundclinic.doctor.domain.model.Doctor
import ke.co.smartroundclinic.doctor.domain.repository.DoctorDirectoryRepository

data class DoctorDirectoryPage(val doctors: List<Doctor>, val page: Int, val totalPages: Int)

class GetRecommendedDoctorsUseCase(private val repository: DoctorDirectoryRepository) {
    suspend operator fun invoke(
        specializationId: String?,
        page: Int,
        size: Int,
        excludeDoctorId: String?,
    ): Resource<DoctorDirectoryPage> {
        val result = repository.getRecommendedDoctors(specializationId, page, size, excludeDoctorId)
        val data = result.data ?: return Resource.Error(result.message ?: "Failed to fetch doctors")
        val safeSize = if (data.size > 0) data.size else size.coerceAtLeast(1)
        val totalPages = ((data.total + safeSize - 1) / safeSize).coerceAtLeast(1)
        return Resource.Success(
            DoctorDirectoryPage(
                doctors = data.items.map { it.toDomain() },
                page = data.page,
                totalPages = totalPages.toInt(),
            ),
        )
    }
}
