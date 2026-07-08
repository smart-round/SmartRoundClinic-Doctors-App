package ke.co.smartroundclinic.doctor.domain.repository

import ke.co.smartroundclinic.doctor.common.Resource
import ke.co.smartroundclinic.doctor.domain.model.Rating
import ke.co.smartroundclinic.doctor.domain.model.RatingPage

interface PatientRatingRepository {
    suspend fun submitRating(appointmentId: String, patientId: String, rating: Int, comment: String?): Resource<Rating>
    suspend fun updateRating(id: String, rating: Int, comment: String?): Resource<Rating>
    suspend fun deleteRating(id: String): Resource<Unit>
    suspend fun getRatings(patientId: String, page: Int = 1, size: Int = 100): Resource<List<Rating>>
    suspend fun getRatingsPage(patientId: String, page: Int = 1, size: Int = 20): Resource<RatingPage>
}
