package ke.co.smartroundclinic.doctor.presentation.main.bookings

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import ke.co.smartroundclinic.doctor.common.Resource
import ke.co.smartroundclinic.doctor.core.snackbar.SnackbarController
import ke.co.smartroundclinic.doctor.domain.model.Doctor
import ke.co.smartroundclinic.doctor.domain.model.Referral
import ke.co.smartroundclinic.doctor.domain.usecase.directory.GetRecommendedDoctorsUseCase
import ke.co.smartroundclinic.doctor.domain.usecase.referral.CreateReferralUseCase
import ke.co.smartroundclinic.doctor.domain.usecase.referral.GetReceivedReferralsUseCase
import ke.co.smartroundclinic.doctor.domain.usecase.referral.GetSentReferralsUseCase
import kotlinx.coroutines.launch

private const val DOCTORS_PAGE_SIZE = 20

class ReferralViewModel(
    private val createReferralUseCase: CreateReferralUseCase,
    private val getRecommendedDoctorsUseCase: GetRecommendedDoctorsUseCase,
    private val getReceivedReferralsUseCase: GetReceivedReferralsUseCase,
    private val getSentReferralsUseCase: GetSentReferralsUseCase,
    private val snackbarController: SnackbarController,
) : ViewModel() {

    var isSubmitting by mutableStateOf(false)
        private set

    // Received/sent referral lists — fetched directly from the referral collection so a doctor
    // can see who's been referred to them (and their own sent referrals) independent of whether
    // the patient has responded or booked yet, rather than inferring it from the appointments list.
    var receivedReferrals by mutableStateOf<List<Referral>>(emptyList())
        private set
    var sentReferrals by mutableStateOf<List<Referral>>(emptyList())
        private set
    var isLoadingReceivedReferrals by mutableStateOf(false)
        private set
    var isLoadingSentReferrals by mutableStateOf(false)
        private set

    fun loadReceivedReferrals() {
        viewModelScope.launch {
            isLoadingReceivedReferrals = true
            when (val result = getReceivedReferralsUseCase()) {
                is Resource.Success -> receivedReferrals = result.data ?: emptyList()
                is Resource.Error -> snackbarController.show(result.message ?: "Failed to load received referrals", isError = true)
                else -> {}
            }
            isLoadingReceivedReferrals = false
        }
    }

    fun loadSentReferrals() {
        viewModelScope.launch {
            isLoadingSentReferrals = true
            when (val result = getSentReferralsUseCase()) {
                is Resource.Success -> sentReferrals = result.data ?: emptyList()
                is Resource.Error -> snackbarController.show(result.message ?: "Failed to load sent referrals", isError = true)
                else -> {}
            }
            isLoadingSentReferrals = false
        }
    }

    fun createReferral(appointmentId: String, receivingDoctorId: String, onSuccess: () -> Unit) {
        if (isSubmitting) return
        viewModelScope.launch {
            isSubmitting = true
            when (val result = createReferralUseCase(appointmentId, receivingDoctorId)) {
                is Resource.Success -> {
                    snackbarController.show("Referral sent successfully")
                    loadSentReferrals()
                    onSuccess()
                }
                is Resource.Error -> snackbarController.show(result.message ?: "Failed to send referral", isError = true)
                else -> {}
            }
            isSubmitting = false
        }
    }

    // Referral doctor picker: a paginated, name-searchable directory of verified doctors, same
    // shape as DoctorChatViewModel's directory slice, scoped to this ViewModel/flow so the
    // referring doctor never appears in their own picker.
    var doctors by mutableStateOf<List<Doctor>>(emptyList())
        private set
    var isLoadingDoctors by mutableStateOf(false)
        private set
    var isLoadingMoreDoctors by mutableStateOf(false)
        private set
    var hasMoreDoctors by mutableStateOf(false)
        private set
    private var doctorsPage = 0
    private var doctorsTotalPages = 1
    private var excludeDoctorId: String? = null

    fun loadDoctors(excludeDoctorId: String?) {
        this.excludeDoctorId = excludeDoctorId
        if (doctors.isNotEmpty() || isLoadingDoctors) return
        viewModelScope.launch {
            isLoadingDoctors = true
            when (val result = getRecommendedDoctorsUseCase(null, 1, DOCTORS_PAGE_SIZE, excludeDoctorId)) {
                is Resource.Success -> {
                    val page = result.data
                    doctors = page?.doctors.orEmpty().filterNot { it.id == excludeDoctorId }
                    doctorsPage = page?.page ?: 1
                    doctorsTotalPages = page?.totalPages ?: 1
                    hasMoreDoctors = doctorsPage < doctorsTotalPages
                }
                is Resource.Error -> snackbarController.show(result.message ?: "Failed to load doctors", isError = true)
                else -> {}
            }
            isLoadingDoctors = false
        }
    }

    fun loadMoreDoctors() {
        if (!hasMoreDoctors || isLoadingMoreDoctors || isLoadingDoctors) return
        viewModelScope.launch {
            isLoadingMoreDoctors = true
            when (val result = getRecommendedDoctorsUseCase(null, doctorsPage + 1, DOCTORS_PAGE_SIZE, excludeDoctorId)) {
                is Resource.Success -> {
                    val page = result.data
                    val existingIds = doctors.map { it.id }.toSet()
                    doctors = doctors + page?.doctors.orEmpty().filterNot { it.id in existingIds || it.id == excludeDoctorId }
                    doctorsPage = page?.page ?: doctorsPage
                    doctorsTotalPages = page?.totalPages ?: doctorsTotalPages
                    hasMoreDoctors = doctorsPage < doctorsTotalPages
                }
                is Resource.Error -> snackbarController.show(result.message ?: "Failed to load more doctors", isError = true)
                else -> {}
            }
            isLoadingMoreDoctors = false
        }
    }
}
