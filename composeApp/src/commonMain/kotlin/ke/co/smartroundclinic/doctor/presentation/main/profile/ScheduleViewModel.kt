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
import ke.co.smartroundclinic.doctor.domain.model.DoctorAvailability
import ke.co.smartroundclinic.doctor.domain.model.ScheduleBreakBlock
import ke.co.smartroundclinic.doctor.domain.repository.ScheduleLocalRepository
import ke.co.smartroundclinic.doctor.domain.usecase.scheduling.GetScheduleUseCase
import ke.co.smartroundclinic.doctor.domain.usecase.scheduling.UpdateAvailabilityUseCase
import ke.co.smartroundclinic.doctor.domain.usecase.scheduling.UpsertAvailabilityUseCase
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class ScheduleViewModel(
    private val getScheduleUseCase: GetScheduleUseCase,
    private val upsertAvailabilityUseCase: UpsertAvailabilityUseCase,
    private val updateAvailabilityUseCase: UpdateAvailabilityUseCase,
    private val scheduleLocalRepository: ScheduleLocalRepository,
    private val snackbarController: SnackbarController,
) : ViewModel() {

    val schedule: StateFlow<List<DoctorAvailability>> = scheduleLocalRepository.observeSchedule()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), emptyList())

    var isLoading by mutableStateOf(false)
        private set

    var isSaving by mutableStateOf(false)
        private set

    init {
        refresh()
    }

    fun refresh() {
        viewModelScope.launch {
            isLoading = true
            getScheduleUseCase()
            isLoading = false
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

            // Deactivate days that were previously configured but are now unchecked
            val daysToDeactivate = schedule.value
                .filter { it.isActive && it.dayOfWeek !in enabledDays }
                .map { it.dayOfWeek }
            for (day in daysToDeactivate) {
                val result = updateAvailabilityUseCase(day, false)
                if (result is Resource.Error) {
                    snackbarController.show(result.message ?: "Failed to update schedule")
                    allOk = false
                    break
                }
            }

            // Upsert all enabled days
            if (allOk) {
                for (day in enabledDays) {
                    val result = upsertAvailabilityUseCase(
                        UpsertAvailabilityReq(
                            dayOfWeek = day,
                            windowStart = windowStart,
                            windowEnd = windowEnd,
                            slotDuration = slotDuration,
                            breakBlocks = breakBlocks.map { BreakBlockReq(it.start, it.end) },
                            isActive = true,
                        )
                    )
                    if (result is Resource.Error) {
                        snackbarController.show(result.message ?: "Failed to save schedule")
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
