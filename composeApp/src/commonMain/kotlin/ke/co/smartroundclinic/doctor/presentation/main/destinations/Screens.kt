package ke.co.smartroundclinic.doctor.presentation.main.destinations

import androidx.navigation3.runtime.NavKey
import kotlinx.serialization.Serializable

@Serializable
data object MainRoot : NavKey

@Serializable
data object Home : NavKey

@Serializable
data object Bookings : NavKey

@Serializable
data object Articles : NavKey

@Serializable
data object Wallet : NavKey

@Serializable
data object Chat : NavKey
