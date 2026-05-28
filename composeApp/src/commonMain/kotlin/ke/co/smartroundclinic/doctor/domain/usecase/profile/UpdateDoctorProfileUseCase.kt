package ke.co.smartroundclinic.doctor.domain.usecase.profile

import ke.co.smartroundclinic.doctor.common.Resource
import ke.co.smartroundclinic.doctor.domain.model.DoctorProfile
import ke.co.smartroundclinic.doctor.domain.repository.DoctorProfileRepository

class UpdateDoctorProfileUseCase(private val repository: DoctorProfileRepository) {
    suspend operator fun invoke(
        title: String?,
        bio: String?,
        kmpdcRegNumber: String?,
        yearsOfExperience: Int?,
        languages: List<String>?,
        facilityName: String?,
    ): Resource<DoctorProfile> = repository.updateProfile(
        title = title,
        bio = bio,
        kmpdcRegNumber = kmpdcRegNumber,
        yearsOfExperience = yearsOfExperience,
        languages = languages,
        facilityName = facilityName,
    )
}
