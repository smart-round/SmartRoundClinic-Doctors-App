package ke.co.smartroundclinic.doctor.presentation.main.services

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import ke.co.smartroundclinic.doctor.common.Resource
import ke.co.smartroundclinic.doctor.domain.model.Doctor
import ke.co.smartroundclinic.doctor.domain.model.Speciality
import ke.co.smartroundclinic.doctor.domain.usecase.directory.GetRecommendedDoctorsUseCase
import ke.co.smartroundclinic.doctor.domain.usecase.speciality.GetSpecialitiesUseCase
import kotlinx.coroutines.launch

/**
 * Backs the doctor-facing "browse other doctors" flow — the Services bottom-nav tab, the Chat
 * tab's Services sub-tab, and (later) the referral doctor picker. Deliberately much smaller than
 * the patient app's ServicesViewModel: no booking/payment/calendar state, just specialities and
 * the doctor-directory search, since this screen's only actions are "view profile" and "connect"/
 * "refer".
 */
class ServicesViewModel(
    private val getSpecialitiesUseCase: GetSpecialitiesUseCase,
    private val getRecommendedDoctorsUseCase: GetRecommendedDoctorsUseCase,
) : ViewModel() {

    private val doctorCache = mutableMapOf<String, Doctor>()

    fun cacheDoctor(doctor: Doctor) { doctorCache[doctor.id] = doctor }

    fun doctorById(id: String): Doctor? = specialityDoctors.find { it.id == id } ?: doctorCache[id]

    var specialities by mutableStateOf<List<Speciality>>(emptyList())
        private set

    var specialityDoctors by mutableStateOf<List<Doctor>>(emptyList())
        private set

    var isLoadingDoctors by mutableStateOf(false)
        private set

    var doctorsCurrentPage by mutableStateOf(1)
        private set

    var doctorsTotalPages by mutableStateOf(1)
        private set

    init {
        viewModelScope.launch {
            val result = getSpecialitiesUseCase()
            if (result is Resource.Success) specialities = result.data ?: emptyList()
        }
    }

    fun loadDoctorsBySpeciality(specializationId: String, excludeDoctorId: String? = null) {
        doctorsCurrentPage = 1
        doctorsTotalPages = 1
        loadDoctorsPage(specializationId, page = 1, excludeDoctorId = excludeDoctorId)
    }

    fun loadDoctorsPage(specializationId: String, page: Int, excludeDoctorId: String? = null) {
        viewModelScope.launch {
            isLoadingDoctors = true
            if (page == 1) specialityDoctors = emptyList()
            when (val result = getRecommendedDoctorsUseCase(specializationId, page, 20, excludeDoctorId)) {
                is Resource.Success -> {
                    val data = result.data ?: return@launch
                    specialityDoctors = data.doctors
                    doctorsCurrentPage = data.page
                    doctorsTotalPages = data.totalPages
                }
                else -> {}
            }
            isLoadingDoctors = false
        }
    }
}
