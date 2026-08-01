package ke.co.smartroundclinic.doctor.presentation.main.bookings

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.retain.retain
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.navigation3.runtime.NavKey
import androidx.navigation3.runtime.entryProvider
import androidx.navigation3.ui.NavDisplay
import ke.co.smartroundclinic.doctor.presentation.main.bookings.destinations.BookingDetail
import ke.co.smartroundclinic.doctor.presentation.main.bookings.destinations.BookingList
import ke.co.smartroundclinic.doctor.presentation.main.bookings.destinations.MedicalRecordDetail
import ke.co.smartroundclinic.doctor.presentation.main.bookings.destinations.ReferralDoctorPicker
import ke.co.smartroundclinic.doctor.presentation.main.bookings.destinations.ReferralReason
import ke.co.smartroundclinic.doctor.presentation.main.bookings.ui.AppointmentDetailScreen
import ke.co.smartroundclinic.doctor.presentation.main.bookings.ui.BookingListScreen
import ke.co.smartroundclinic.doctor.presentation.main.bookings.ui.BookingTab
import ke.co.smartroundclinic.doctor.presentation.main.bookings.ui.BookingTopTab
import ke.co.smartroundclinic.doctor.presentation.main.bookings.ui.MedicalRecordScreen
import ke.co.smartroundclinic.doctor.presentation.main.bookings.ui.ReferralReasonScreen
import ke.co.smartroundclinic.doctor.presentation.main.profile.PersonalInfoViewModel
import ke.co.smartroundclinic.doctor.presentation.main.services.ServicesRoot
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun BookingsRoot(
    modifier: Modifier = Modifier,
    onAtRootChanged: (Boolean) -> Unit = {},
    pendingBookingId: String? = null,
    onPendingNavigated: () -> Unit = {},
    onProfileClick: () -> Unit = {},
    onNotificationsClick: () -> Unit = {},
) {
    val backStack = retain { mutableStateListOf<NavKey>(BookingList) }
    var selectedTopTab by retain { mutableStateOf(BookingTopTab.CONSULTATION) }
    var selectedTab by retain { mutableStateOf(BookingTab.UPCOMING) }
    val isAtRoot = backStack.size == 1
    val viewModel: BookingsViewModel = koinViewModel()
    val medicalRecordViewModel: MedicalRecordViewModel = koinViewModel()
    val ratingViewModel: RatingViewModel = koinViewModel()
    val referralViewModel: ReferralViewModel = koinViewModel()
    val personalInfoViewModel: PersonalInfoViewModel = koinViewModel()
    val appointments by viewModel.appointments.collectAsState()
    val currentUser by personalInfoViewModel.user.collectAsState()

    SideEffect { onAtRootChanged(isAtRoot) }

    LaunchedEffect(Unit) { viewModel.loadAppointments() }

    LaunchedEffect(pendingBookingId) {
        if (!pendingBookingId.isNullOrBlank()) {
            backStack.removeAll { it is BookingDetail }
            backStack.add(BookingDetail(pendingBookingId))
            onPendingNavigated()
        }
    }

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
                    selectedTopTab = selectedTopTab,
                    onTopTabSelected = { selectedTopTab = it },
                    selectedTab = selectedTab,
                    onTabSelected = { selectedTab = it },
                    onProfileClick = onProfileClick,
                    onNotificationsClick = onNotificationsClick,
                )
            }
            entry<BookingDetail> { dest ->
                val appointment = appointments.find { it.id == dest.bookingId }
                if (appointment != null) {
                    LaunchedEffect(appointment.id) {
                        medicalRecordViewModel.loadRecord(appointment.id)
                        medicalRecordViewModel.loadPatientBio(appointment.patientId)
                        medicalRecordViewModel.loadPatientHistory(appointment.patientId)
                        ratingViewModel.loadRatings(appointment.id, appointment.patientId)
                    }
                    AppointmentDetailScreen(
                        appointment = appointment,
                        isActioning = viewModel.isActioning,
                        medicalRecord = medicalRecordViewModel.record,
                        patientHistory = medicalRecordViewModel.patientHistory,
                        patientBio = medicalRecordViewModel.patientBio,
                        myRatingOfPatient = ratingViewModel.myRatingOfPatient,
                        isLoadingRatings = ratingViewModel.isLoadingRatings,
                        isSubmittingRating = ratingViewModel.isSubmittingRating,
                        onBack = { backStack.removeLastOrNull() },
                        onConfirm = { viewModel.confirmAppointment(appointment.id) },
                        onComplete = { viewModel.completeAppointment(appointment.id) },
                        onCancel = { reason -> viewModel.cancelAppointment(appointment.id, reason) },
                        onRefer = { backStack.add(ReferralReason(appointment.id)) },
                        onAddMedicalRecord = { backStack.add(MedicalRecordDetail(appointment.id, null, appointment.patientId)) },
                        onSubmitRating = { rating, comment ->
                            ratingViewModel.submitRating(appointment.id, appointment.patientId, rating, comment)
                        },
                        onUpdateRating = { rating, comment -> ratingViewModel.updateRating(rating, comment) },
                        onDeleteRating = { ratingViewModel.deleteRating() },
                        patientAverageRating = medicalRecordViewModel.patientBio?.averageRating ?: 0.0,
                        patientTotalReviews = medicalRecordViewModel.patientBio?.totalReviews ?: 0,
                        patientReviews = ratingViewModel.patientReviews,
                        isLoadingPatientReviews = ratingViewModel.isLoadingPatientReviews,
                        isLoadingMorePatientReviews = ratingViewModel.isLoadingMorePatientReviews,
                        hasMorePatientReviews = ratingViewModel.patientReviewsCurrentPage < ratingViewModel.patientReviewsTotalPages,
                        onOpenPatientReviews = { ratingViewModel.loadPatientReviews(appointment.patientId) },
                        onLoadMorePatientReviews = { ratingViewModel.loadMorePatientReviews(appointment.patientId) },
                    )
                }
            }
            entry<MedicalRecordDetail> { dest ->
                MedicalRecordScreen(
                    viewModel = medicalRecordViewModel,
                    appointmentId = dest.appointmentId,
                    consultationId = dest.consultationId,
                    patientId = dest.patientId,
                    onBack = { backStack.removeLastOrNull() },
                )
            }
            entry<ReferralReason> { dest ->
                ReferralReasonScreen(
                    onNext = { reason -> backStack.add(ReferralDoctorPicker(dest.appointmentId, reason)) },
                    onBack = { backStack.removeLastOrNull() },
                )
            }
            entry<ReferralDoctorPicker> { dest ->
                ServicesRoot(
                    selfDoctorId = currentUser?.id,
                    headerTitle = "Refer to a Doctor",
                    primaryActionLabel = "Refer to this doctor",
                    onProfileClick = {},
                    onNotificationsClick = {},
                    onPrimaryAction = { doctor ->
                        referralViewModel.createReferral(dest.appointmentId, doctor.id, dest.reason) {
                            backStack.removeAll { it is ReferralReason || it is ReferralDoctorPicker }
                            viewModel.loadAppointments()
                        }
                    },
                )
            }
        },
    )
}
