package ke.co.smartroundclinic.doctor.presentation.main.profile.destinations

import androidx.navigation3.runtime.NavKey
import kotlinx.serialization.Serializable

@Serializable data object ProfileList : NavKey
@Serializable data object PersonalInfo : NavKey
@Serializable data object DoctorProfile : NavKey
@Serializable data object BankingDetails : NavKey
@Serializable data object ResetPassword : NavKey
@Serializable data object VerifyEmailSecurity : NavKey
@Serializable data object CreateNewPasswordSecurity : NavKey
@Serializable data object PasswordChanged : NavKey
@Serializable data object Support : NavKey
@Serializable data object ContactSupport : NavKey
@Serializable data object Faqs : NavKey
@Serializable data object TermsAndConditions : NavKey
@Serializable data object ScheduleManagement : NavKey
@Serializable data class Specialization(val isEnabled: Boolean) : NavKey
@Serializable data object LicenceManagement : NavKey
@Serializable data class LicenceViewer(val url: String, val name: String) : NavKey
