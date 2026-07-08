package ke.co.smartroundclinic.doctor.presentation.main.bookings

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import ke.co.smartroundclinic.doctor.common.Resource
import ke.co.smartroundclinic.doctor.core.snackbar.SnackbarController
import ke.co.smartroundclinic.doctor.domain.model.Rating
import ke.co.smartroundclinic.doctor.domain.usecase.rating.DeletePatientRatingUseCase
import ke.co.smartroundclinic.doctor.domain.usecase.rating.GetPatientRatingForAppointmentUseCase
import ke.co.smartroundclinic.doctor.domain.usecase.rating.GetPatientRatingHistoryUseCase
import ke.co.smartroundclinic.doctor.domain.usecase.rating.RatePatientUseCase
import ke.co.smartroundclinic.doctor.domain.usecase.rating.UpdatePatientRatingUseCase
import kotlinx.coroutines.launch

private const val ALREADY_RATED_MESSAGE = "You have already rated this appointment"

class RatingViewModel(
    private val ratePatientUseCase: RatePatientUseCase,
    private val updatePatientRatingUseCase: UpdatePatientRatingUseCase,
    private val deletePatientRatingUseCase: DeletePatientRatingUseCase,
    private val getPatientRatingForAppointmentUseCase: GetPatientRatingForAppointmentUseCase,
    private val getPatientRatingHistoryUseCase: GetPatientRatingHistoryUseCase,
    private val snackbarController: SnackbarController,
) : ViewModel() {

    var myRatingOfPatient by mutableStateOf<Rating?>(null)
        private set
    var isLoadingRatings by mutableStateOf(false)
        private set
    var isSubmittingRating by mutableStateOf(false)
        private set

    var patientReviews by mutableStateOf<List<Rating>>(emptyList())
        private set
    var isLoadingPatientReviews by mutableStateOf(false)
        private set
    var isLoadingMorePatientReviews by mutableStateOf(false)
        private set
    var patientReviewsCurrentPage by mutableStateOf(1)
        private set
    var patientReviewsTotalPages by mutableStateOf(1)
        private set

    fun loadRatings(appointmentId: String, patientId: String) {
        viewModelScope.launch {
            isLoadingRatings = true
            myRatingOfPatient = null

            when (val result = getPatientRatingForAppointmentUseCase(patientId, appointmentId)) {
                is Resource.Success -> myRatingOfPatient = result.data
                else -> Unit
            }

            isLoadingRatings = false
        }
    }

    fun loadPatientReviews(patientId: String) {
        patientReviewsCurrentPage = 1
        patientReviewsTotalPages = 1
        patientReviews = emptyList()
        loadPatientReviewsPage(patientId, page = 1, append = false)
    }

    fun loadMorePatientReviews(patientId: String) {
        if (isLoadingMorePatientReviews || isLoadingPatientReviews || patientReviewsCurrentPage >= patientReviewsTotalPages) return
        loadPatientReviewsPage(patientId, page = patientReviewsCurrentPage + 1, append = true)
    }

    private fun loadPatientReviewsPage(patientId: String, page: Int, append: Boolean) {
        viewModelScope.launch {
            if (append) isLoadingMorePatientReviews = true else isLoadingPatientReviews = true
            when (val result = getPatientRatingHistoryUseCase(patientId, page)) {
                is Resource.Success -> {
                    val data = result.data
                    if (data != null) {
                        patientReviews = if (append) patientReviews + data.items else data.items
                        patientReviewsCurrentPage = data.page
                        val size = if (data.size > 0) data.size else 20
                        patientReviewsTotalPages = ((data.total + size - 1) / size).coerceAtLeast(1)
                    }
                }
                else -> Unit
            }
            if (append) isLoadingMorePatientReviews = false else isLoadingPatientReviews = false
        }
    }

    fun submitRating(appointmentId: String, patientId: String, rating: Int, comment: String?) {
        if (isSubmittingRating) return
        viewModelScope.launch {
            isSubmittingRating = true
            when (val result = ratePatientUseCase(appointmentId, patientId, rating, comment)) {
                is Resource.Success -> {
                    myRatingOfPatient = result.data
                    snackbarController.show("Rating submitted")
                }
                is Resource.Error -> {
                    if (result.message == ALREADY_RATED_MESSAGE) {
                        loadRatings(appointmentId, patientId)
                    }
                    snackbarController.show(result.message ?: "Failed to submit rating", isError = true)
                }
                else -> Unit
            }
            isSubmittingRating = false
        }
    }

    fun updateRating(rating: Int, comment: String?) {
        val existing = myRatingOfPatient ?: return
        if (isSubmittingRating) return
        viewModelScope.launch {
            isSubmittingRating = true
            when (val result = updatePatientRatingUseCase(existing.id, rating, comment)) {
                is Resource.Success -> {
                    myRatingOfPatient = result.data
                    snackbarController.show("Rating updated")
                }
                is Resource.Error -> snackbarController.show(result.message ?: "Failed to update rating", isError = true)
                else -> Unit
            }
            isSubmittingRating = false
        }
    }

    fun deleteRating() {
        val existing = myRatingOfPatient ?: return
        if (isSubmittingRating) return
        viewModelScope.launch {
            isSubmittingRating = true
            when (val result = deletePatientRatingUseCase(existing.id)) {
                is Resource.Success -> {
                    myRatingOfPatient = null
                    snackbarController.show("Rating deleted")
                }
                is Resource.Error -> snackbarController.show(result.message ?: "Failed to delete rating", isError = true)
                else -> Unit
            }
            isSubmittingRating = false
        }
    }
}
