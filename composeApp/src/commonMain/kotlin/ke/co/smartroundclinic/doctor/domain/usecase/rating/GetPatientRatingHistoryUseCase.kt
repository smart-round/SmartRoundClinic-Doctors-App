package ke.co.smartroundclinic.doctor.domain.usecase.rating

import ke.co.smartroundclinic.doctor.common.Resource
import ke.co.smartroundclinic.doctor.domain.model.RatingPage
import ke.co.smartroundclinic.doctor.domain.repository.PatientRatingRepository

class GetPatientRatingHistoryUseCase(private val repository: PatientRatingRepository) {
    suspend operator fun invoke(patientId: String, page: Int = 1, size: Int = 20): Resource<RatingPage> =
        repository.getRatingsPage(patientId, page, size)
}
