package ke.co.smartroundclinic.doctor.domain.usecase.rating

import ke.co.smartroundclinic.doctor.common.Resource
import ke.co.smartroundclinic.doctor.domain.model.Rating
import ke.co.smartroundclinic.doctor.domain.repository.PatientRatingRepository

class UpdatePatientRatingUseCase(private val repository: PatientRatingRepository) {
    suspend operator fun invoke(id: String, rating: Int, comment: String?): Resource<Rating> =
        repository.updateRating(id, rating, comment)
}
