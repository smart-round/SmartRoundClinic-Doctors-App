package ke.co.smartroundclinic.doctor.presentation.main.bookings

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import ke.co.smartroundclinic.doctor.common.Resource
import ke.co.smartroundclinic.doctor.core.snackbar.SnackbarController
import ke.co.smartroundclinic.doctor.domain.usecase.referral.CreateReferralUseCase
import kotlinx.coroutines.launch

class ReferralViewModel(
    private val createReferralUseCase: CreateReferralUseCase,
    private val snackbarController: SnackbarController,
) : ViewModel() {

    var isSubmitting by mutableStateOf(false)
        private set

    fun createReferral(appointmentId: String, receivingDoctorId: String, reason: String, onSuccess: () -> Unit) {
        if (isSubmitting) return
        viewModelScope.launch {
            isSubmitting = true
            when (val result = createReferralUseCase(appointmentId, receivingDoctorId, reason)) {
                is Resource.Success -> {
                    snackbarController.show("Referral sent successfully")
                    onSuccess()
                }
                is Resource.Error -> snackbarController.show(result.message ?: "Failed to send referral", isError = true)
                else -> {}
            }
            isSubmitting = false
        }
    }
}
