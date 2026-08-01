package ke.co.smartroundclinic.doctor.presentation.main.services.destinations

import androidx.navigation3.runtime.NavKey
import kotlinx.serialization.Serializable

@Serializable data object ServicesList : NavKey
@Serializable data class DoctorsByCategory(
    val categoryId: String,
    val categoryName: String,
    val categoryDescription: String = "",
    val categoryIconUrl: String? = null,
) : NavKey
@Serializable data class DoctorProfile(val doctorId: String) : NavKey
