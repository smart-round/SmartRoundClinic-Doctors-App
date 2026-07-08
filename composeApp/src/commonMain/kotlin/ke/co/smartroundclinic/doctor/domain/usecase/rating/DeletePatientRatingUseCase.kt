package ke.co.smartroundclinic.doctor.domain.usecase.rating

import ke.co.smartroundclinic.doctor.common.Resource
import ke.co.smartroundclinic.doctor.domain.repository.PatientRatingRepository

class DeletePatientRatingUseCase(private val repository: PatientRatingRepository) {
    suspend operator fun invoke(id: String): Resource<Unit> = repository.deleteRating(id)
}
