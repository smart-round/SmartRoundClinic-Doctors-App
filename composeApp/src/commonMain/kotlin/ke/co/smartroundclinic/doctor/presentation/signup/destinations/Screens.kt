package ke.co.smartroundclinic.doctor.presentation.signup.destinations

import androidx.navigation3.runtime.NavKey
import ke.co.smartroundclinic.doctor.presentation.signup.PersonalInfoData
import ke.co.smartroundclinic.doctor.presentation.signup.SpecializationData
import kotlinx.serialization.Serializable

@Serializable
data object SignUpRoot : NavKey

@Serializable
data object SignUp : NavKey

@Serializable
data class SpecializationAndCompliance(
    val personalInfo: PersonalInfoData,
) : NavKey

@Serializable
data class BankDetails(
    val personalInfo: PersonalInfoData,
    val specialization: SpecializationData,
) : NavKey

@Serializable
data class AccountVerification(val email: String) : NavKey

@Serializable
data object ApplicationUnderReview : NavKey
