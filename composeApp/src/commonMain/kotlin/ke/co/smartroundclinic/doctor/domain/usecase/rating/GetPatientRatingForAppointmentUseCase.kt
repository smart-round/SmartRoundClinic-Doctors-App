package ke.co.smartroundclinic.doctor.domain.usecase.rating

import ke.co.smartroundclinic.doctor.common.Resource
import ke.co.smartroundclinic.doctor.domain.model.Rating
import ke.co.smartroundclinic.doctor.domain.repository.PatientRatingRepository

class GetPatientRatingForAppointmentUseCase(private val repository: PatientRatingRepository) {
    suspend operator fun invoke(patientId: String, appointmentId: String): Resource<Rating?> =
        when (val result = repository.getRatings(patientId)) {
            is Resource.Success -> Resource.Success(result.data?.find { it.appointmentId == appointmentId })
            is Resource.Error -> Resource.Error(result.message ?: "Failed to load rating")
            else -> Resource.Success(null)
        }
}
