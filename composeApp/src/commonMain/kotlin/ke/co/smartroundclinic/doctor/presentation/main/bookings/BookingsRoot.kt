package ke.co.smartroundclinic.doctor.presentation.main.bookings

import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.retain.retain
import androidx.compose.ui.Modifier
import androidx.navigation3.runtime.NavKey
import androidx.navigation3.runtime.entryProvider
import androidx.navigation3.ui.NavDisplay
import ke.co.smartroundclinic.doctor.presentation.main.bookings.destinations.BookingDetail
import ke.co.smartroundclinic.doctor.presentation.main.bookings.destinations.BookingList
import ke.co.smartroundclinic.doctor.presentation.main.bookings.ui.AppointmentDetailScreen
import ke.co.smartroundclinic.doctor.presentation.main.bookings.ui.BookingListScreen

internal enum class BookingStatus { UPCOMING, COMPLETED, CANCELLED }

internal data class BookingUi(
    val id: Int,
    val dateTime: String,
    val patientName: String,
    val fee: String,
    val status: BookingStatus,
    val existingRating: Int = 0,
    val existingReview: String = "",
)

private val allBookings = listOf(
    BookingUi(1, "Wed, June 25 · 8:00 - 8:20 AM", "Mercy Wambui", "KES 700", BookingStatus.UPCOMING),
    BookingUi(2, "Wed, June 25 · 9:00 - 9:20 AM", "Mercy Wambui", "KES 700", BookingStatus.UPCOMING),
    BookingUi(3, "Wed, June 25 · 10:00 - 10:20 AM", "Mercy Wambui", "KES 700", BookingStatus.UPCOMING),
    BookingUi(4, "Mon, June 20 · 8:00 - 8:20 AM", "Mercy Wambui", "KES 700", BookingStatus.COMPLETED, existingRating = 4, existingReview = "Great patient, very cooperative and followed all instructions carefully."),
    BookingUi(5, "Mon, June 20 · 9:00 - 9:20 AM", "Mercy Wambui", "KES 700", BookingStatus.COMPLETED),
    BookingUi(6, "Fri, June 18 · 8:00 - 8:20 AM", "Mercy Wambui", "KES 700", BookingStatus.CANCELLED),
)

@Composable
fun BookingsRoot(
    modifier: Modifier = Modifier,
    onAtRootChanged: (Boolean) -> Unit = {},
) {
    val backStack = retain { mutableStateListOf<NavKey>(BookingList) }
    val isAtRoot = backStack.size == 1

    SideEffect { onAtRootChanged(isAtRoot) }

    NavDisplay(
        modifier = modifier,
        backStack = backStack,
        onBack = { backStack.removeLastOrNull() },
        entryProvider = entryProvider {
            entry<BookingList> {
                BookingListScreen(
                    bookings = allBookings,
                    onBookingClick = { booking -> backStack.add(BookingDetail(booking.id)) },
                )
            }
            entry<BookingDetail> { dest ->
                val booking = allBookings.first { it.id == dest.bookingId }
                AppointmentDetailScreen(
                    booking = booking,
                    onBack = { backStack.removeLastOrNull() },
                )
            }
        },
    )
}
