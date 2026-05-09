package ke.co.smartroundclinic.doctor.presentation.signup

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import ke.co.smartroundclinic.doctor.common.Resource
import ke.co.smartroundclinic.doctor.domain.model.Speciality
import ke.co.smartroundclinic.doctor.domain.usecase.speciality.GetSpecialitiesUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class SpecializationComplianceViewModel(
    private val getSpecialitiesUseCase: GetSpecialitiesUseCase
) : ViewModel() {

    private val _specialities = MutableStateFlow<List<Speciality>>(emptyList())
    val specialities = _specialities.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading = _isLoading.asStateFlow()

    private val _error = MutableStateFlow<String?>(null)
    val error = _error.asStateFlow()

    init {
        loadSpecialities()
    }

    private fun loadSpecialities() {
        viewModelScope.launch {
            _isLoading.value = true
            _error.value = null
            when (val result = getSpecialitiesUseCase()) {
                is Resource.Success -> _specialities.value = result.data.orEmpty()
                is Resource.Error -> _error.value = result.message
                is Resource.Loading -> Unit
            }
            _isLoading.value = false
        }
    }
}
