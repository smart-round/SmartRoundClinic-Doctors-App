package ke.co.smartroundclinic.doctor.presentation.main.profile

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import ke.co.smartroundclinic.doctor.common.Resource
import ke.co.smartroundclinic.doctor.core.snackbar.SnackbarController
import ke.co.smartroundclinic.doctor.data.remote.dto.request.BreakBlockReq
import ke.co.smartroundclinic.doctor.data.remote.dto.request.UpsertAvailabilityReq
import ke.co.smartroundclinic.doctor.domain.model.Appointment
import ke.co.smartroundclinic.doctor.domain.model.DoctorAvailability
import ke.co.smartroundclinic.doctor.domain.model.ScheduleBreakBlock
import ke.co.smartroundclinic.doctor.domain.repository.AppointmentLocalRepository
import ke.co.smartroundclinic.doctor.domain.repository.ScheduleLocalRepository
import ke.co.smartroundclinic.doctor.domain.usecase.scheduling.DeactivateScheduleUseCase
import ke.co.smartroundclinic.doctor.domain.usecase.scheduling.GetAppointmentsUseCase
import ke.co.smartroundclinic.doctor.domain.usecase.scheduling.GetScheduleUseCase
import ke.co.smartroundclinic.doctor.domain.usecase.scheduling.UpsertAvailabilityUseCase
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class ScheduleViewModel(
    private val getScheduleUseCase: GetScheduleUseCase,
    private val getAppointmentsUseCase: GetAppointmentsUseCase,
    private val upsertAvailabilityUseCase: UpsertAvailabilityUseCase,
    private val deactivateScheduleUseCase: DeactivateScheduleUseCase,
    private val scheduleLocalRepository: ScheduleLocalRepository,
    private val appointmentLocalRepository: AppointmentLocalRepository,
    private val snackbarController: SnackbarController,
) : ViewModel() {

    val schedule: StateFlow<List<DoctorAvailability>> = scheduleLocalRepository.observeSchedule()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), emptyList())

    val appointments: StateFlow<List<Appointment>> = appointmentLocalRepository.observeAppointments()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), emptyList())

    var isLoading by mutableStateOf(false)
        private set

    var isSaving by mutableStateOf(false)
        private set

    var isDeactivating by mutableStateOf(false)
        private set

    init {
        refresh()
    }

    fun refresh() {
        viewModelScope.launch {
            isLoading = true
            getScheduleUseCase()
            getAppointmentsUseCase()
            isLoading = false
        }
    }

    fun deactivateSchedule(day: Int, onSuccess: () -> Unit = {}) {
        viewModelScope.launch {
            isDeactivating = true
            val result = deactivateScheduleUseCase(day)
            isDeactivating = false
            if (result is Resource.Success) {
                snackbarController.show("Schedule deactivated")
                onSuccess()
            } else if (result is Resource.Error) {
                snackbarController.show(result.message ?: "Failed to deactivate schedule", isError = true)
            }
        }
    }

    fun saveSchedule(
        enabledDays: Set<Int>,
        windowStart: String,
        windowEnd: String,
        slotDuration: Int,
        breakBlocks: List<ScheduleBreakBlock> = emptyList(),
        onSuccess: () -> Unit,
    ) {
        viewModelScope.launch {
            isSaving = true
            var allOk = true
            val mappedBreaks = breakBlocks.map { BreakBlockReq(it.start, it.end) }

            // Previously-configured days that are now unchecked → POST with isActive=false
            // (same upsert endpoint — updates the full entry)
            val daysToDeactivate = schedule.value
                .filter { it.dayOfWeek !in enabledDays }
                .map { it.dayOfWeek }
            for (day in daysToDeactivate) {
                val existing = schedule.value.first { it.dayOfWeek == day }
                val result = upsertAvailabilityUseCase(
                    UpsertAvailabilityReq(
                        dayOfWeek = day,
                        windowStart = existing.windowStart,
                        windowEnd = existing.windowEnd,
                        slotDuration = existing.slotDuration,
                        breakBlocks = existing.breakBlocks.map { BreakBlockReq(it.start, it.end) },
                        isActive = false,
                    )
                )
                if (result is Resource.Error) {
                    snackbarController.show(result.message ?: "Failed to update schedule", isError = true)
                    allOk = false
                    break
                }
            }

            // Enabled days → POST with isActive=true and current form values
            if (allOk) {
                for (day in enabledDays) {
                    val result = upsertAvailabilityUseCase(
                        UpsertAvailabilityReq(
                            dayOfWeek = day,
                            windowStart = windowStart,
                            windowEnd = windowEnd,
                            slotDuration = slotDuration,
                            breakBlocks = mappedBreaks,
                            isActive = true,
                        )
                    )
                    if (result is Resource.Error) {
                        snackbarController.show(result.message ?: "Failed to save schedule", isError = true)
                        allOk = false
                        break
                    }
                }
            }

            isSaving = false
            if (allOk) {
                snackbarController.show("Schedule saved")
                onSuccess()
            }
        }
    }
}
