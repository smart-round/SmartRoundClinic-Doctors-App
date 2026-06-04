package ke.co.smartroundclinic.doctor.presentation.main.bookings

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
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
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun BookingsRoot(
    modifier: Modifier = Modifier,
    onAtRootChanged: (Boolean) -> Unit = {},
) {
    val backStack = retain { mutableStateListOf<NavKey>(BookingList) }
    val isAtRoot = backStack.size == 1
    val viewModel: BookingsViewModel = koinViewModel()
    val appointments by viewModel.appointments.collectAsState()

    SideEffect { onAtRootChanged(isAtRoot) }

    LaunchedEffect(Unit) { viewModel.loadAppointments() }

    NavDisplay(
        modifier = modifier,
        backStack = backStack,
        onBack = { backStack.removeLastOrNull() },
        entryProvider = entryProvider {
            entry<BookingList> {
                BookingListScreen(
                    appointments = appointments,
                    isLoading = viewModel.isLoading,
                    onRefresh = { viewModel.loadAppointments() },
                    onBookingClick = { appointment -> backStack.add(BookingDetail(appointment.id)) },
                )
            }
            entry<BookingDetail> { dest ->
                val appointment = appointments.find { it.id == dest.bookingId }
                if (appointment != null) {
                    AppointmentDetailScreen(
                        appointment = appointment,
                        isActioning = viewModel.isActioning,
                        onBack = { backStack.removeLastOrNull() },
                        onConfirm = { viewModel.confirmAppointment(appointment.id) },
                        onComplete = { viewModel.completeAppointment(appointment.id) },
                        onNoShow = { viewModel.noShowAppointment(appointment.id) },
                        onCancel = { reason -> viewModel.cancelAppointment(appointment.id, reason) },
                    )
                }
            }
        },
    )
}
