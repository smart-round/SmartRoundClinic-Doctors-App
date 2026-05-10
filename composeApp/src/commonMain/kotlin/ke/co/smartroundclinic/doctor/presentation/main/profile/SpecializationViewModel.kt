package ke.co.smartroundclinic.doctor.presentation.main.profile

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import ke.co.smartroundclinic.doctor.common.Resource
import ke.co.smartroundclinic.doctor.core.snackbar.SnackbarController
import ke.co.smartroundclinic.doctor.domain.model.DoctorSpecialization
import ke.co.smartroundclinic.doctor.domain.model.Speciality
import ke.co.smartroundclinic.doctor.domain.repository.DoctorSpecializationLocalRepository
import ke.co.smartroundclinic.doctor.domain.usecase.speciality.AddDoctorSpecializationUseCase
import ke.co.smartroundclinic.doctor.domain.usecase.speciality.GetDoctorSpecializationUseCase
import ke.co.smartroundclinic.doctor.domain.usecase.speciality.GetSpecialitiesUseCase
import ke.co.smartroundclinic.doctor.domain.usecase.speciality.UpdateDoctorSpecializationUseCase
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class SpecializationViewModel(
    private val getDoctorSpecializationUseCase: GetDoctorSpecializationUseCase,
    private val addDoctorSpecializationUseCase: AddDoctorSpecializationUseCase,
    private val updateDoctorSpecializationUseCase: UpdateDoctorSpecializationUseCase,
    private val getSpecialitiesUseCase: GetSpecialitiesUseCase,
    private val localRepository: DoctorSpecializationLocalRepository,
    private val snackbarController: SnackbarController,
) : ViewModel() {

    val specialization: StateFlow<DoctorSpecialization?> = localRepository.observeSpecialization()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), null)

    var catalog by mutableStateOf<List<Speciality>>(emptyList())
        private set

    var isSaving by mutableStateOf(false)
        private set

    init {
        refreshSpecialization()
        loadCatalog()
    }

    fun refreshSpecialization() {
        viewModelScope.launch { getDoctorSpecializationUseCase() }
    }

    private fun loadCatalog() {
        viewModelScope.launch {
            val result = getSpecialitiesUseCase()
            if (result is Resource.Success) catalog = result.data ?: emptyList()
        }
    }

    fun saveSpecialization(specializationId: String) {
        viewModelScope.launch {
            isSaving = true
            val existing = specialization.value
            val result = if (existing != null) {
                updateDoctorSpecializationUseCase(existing.id, specializationId)
            } else {
                val addResult = addDoctorSpecializationUseCase(specializationId)
                when (addResult) {
                    is Resource.Success -> {
                        snackbarController.show(addResult.message ?: "Specialization saved")
                        refreshSpecialization()
                    }
                    is Resource.Error -> snackbarController.show(addResult.message ?: "Failed to save specialization")
                    else -> Unit
                }
                isSaving = false
                return@launch
            }
            isSaving = false
            when (result) {
                is Resource.Success -> {
                    snackbarController.show(result.message ?: "Specialization saved")
                    refreshSpecialization()
                }
                is Resource.Error -> snackbarController.show(result.message ?: "Failed to save specialization")
                else -> Unit
            }
        }
    }
}
