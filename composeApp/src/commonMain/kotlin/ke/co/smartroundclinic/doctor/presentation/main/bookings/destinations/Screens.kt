package ke.co.smartroundclinic.doctor.presentation.main.bookings.destinations

import androidx.navigation3.runtime.NavKey
import kotlinx.serialization.Serializable

@Serializable
data object BookingList : NavKey

@Serializable
data class BookingDetail(val bookingId: String) : NavKey
