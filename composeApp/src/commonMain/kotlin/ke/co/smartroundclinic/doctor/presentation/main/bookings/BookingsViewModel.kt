package ke.co.smartroundclinic.doctor.presentation.main.bookings

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import ke.co.smartroundclinic.doctor.common.Resource
import ke.co.smartroundclinic.doctor.core.snackbar.SnackbarController
import ke.co.smartroundclinic.doctor.domain.model.Appointment
import ke.co.smartroundclinic.doctor.domain.repository.AppointmentLocalRepository
import ke.co.smartroundclinic.doctor.domain.usecase.scheduling.CancelAppointmentUseCase
import ke.co.smartroundclinic.doctor.domain.usecase.scheduling.CompleteAppointmentUseCase
import ke.co.smartroundclinic.doctor.domain.usecase.scheduling.ConfirmAppointmentUseCase
import ke.co.smartroundclinic.doctor.domain.usecase.scheduling.GetAppointmentsUseCase
import ke.co.smartroundclinic.doctor.domain.usecase.scheduling.NoShowAppointmentUseCase
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch



class BookingsViewModel(
    private val getAppointmentsUseCase: GetAppointmentsUseCase,
    private val confirmAppointmentUseCase: ConfirmAppointmentUseCase,
    private val completeAppointmentUseCase: CompleteAppointmentUseCase,
    private val noShowAppointmentUseCase: NoShowAppointmentUseCase,
    private val cancelAppointmentUseCase: CancelAppointmentUseCase,
    private val appointmentLocalRepository: AppointmentLocalRepository,
    private val snackbarController: SnackbarController,
) : ViewModel() {

    val appointments: StateFlow<List<Appointment>> = appointmentLocalRepository.observeAppointments()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), emptyList())

    var isLoading by mutableStateOf(false)
        private set

    var isActioning by mutableStateOf(false)
        private set

    init {
        loadAppointments()
    }

    fun loadAppointments() {
        viewModelScope.launch {
            isLoading = true
            val result = getAppointmentsUseCase()
            isLoading = false
            when (result) {
                is Resource.Error -> snackbarController.show(result.message ?: "Failed to load appointments", isError = true)
                else -> Unit
            }
        }
    }

    fun confirmAppointment(id: String) {
        viewModelScope.launch {
            isActioning = true
            val result = confirmAppointmentUseCase(id)
            isActioning = false
            when (result) {
                is Resource.Success -> { snackbarController.show("Appointment confirmed"); loadAppointments() }
                is Resource.Error -> snackbarController.show(result.message ?: "Failed to confirm", isError = true)
                else -> Unit
            }
        }
    }

    fun completeAppointment(id: String) {
        viewModelScope.launch {
            isActioning = true
            val result = completeAppointmentUseCase(id)
            isActioning = false
            when (result) {
                is Resource.Success -> { snackbarController.show("Appointment marked complete"); loadAppointments() }
                is Resource.Error -> snackbarController.show(result.message ?: "Failed to complete", isError = true)
                else -> Unit
            }
        }
    }

    fun noShowAppointment(id: String) {
        viewModelScope.launch {
            isActioning = true
            val result = noShowAppointmentUseCase(id)
            isActioning = false
            when (result) {
                is Resource.Success -> { snackbarController.show("Marked as no-show"); loadAppointments() }
                is Resource.Error -> snackbarController.show(result.message ?: "Failed to mark no-show", isError = true)
                else -> Unit
            }
        }
    }

    fun cancelAppointment(id: String, reason: String? = null) {
        viewModelScope.launch {
            isActioning = true
            val result = cancelAppointmentUseCase(id, reason)
            isActioning = false
            when (result) {
                is Resource.Success -> { snackbarController.show("Appointment cancelled"); loadAppointments() }
                is Resource.Error -> snackbarController.show(result.message ?: "Failed to cancel", isError = true)
                else -> Unit
            }
        }
    }
}
