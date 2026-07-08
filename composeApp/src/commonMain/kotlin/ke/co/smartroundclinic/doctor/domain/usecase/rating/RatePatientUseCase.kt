package ke.co.smartroundclinic.doctor.domain.usecase.rating

import ke.co.smartroundclinic.doctor.common.Resource
import ke.co.smartroundclinic.doctor.domain.model.Rating
import ke.co.smartroundclinic.doctor.domain.repository.PatientRatingRepository

class RatePatientUseCase(private val repository: PatientRatingRepository) {
    suspend operator fun invoke(appointmentId: String, patientId: String, rating: Int, comment: String?): Resource<Rating> =
        repository.submitRating(appointmentId, patientId, rating, comment)
}
