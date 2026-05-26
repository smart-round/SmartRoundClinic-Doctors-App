package ke.co.smartroundclinic.doctor.presentation.main.home

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.retain.retain
import androidx.compose.ui.Modifier
import androidx.navigation3.runtime.NavKey
import androidx.navigation3.runtime.entryProvider
import androidx.navigation3.ui.NavDisplay
import ke.co.smartroundclinic.doctor.presentation.main.home.destinations.HomeList
import ke.co.smartroundclinic.doctor.presentation.main.home.ui.HomeScreen
import ke.co.smartroundclinic.doctor.presentation.auth.ForgotPasswordViewModel
import ke.co.smartroundclinic.doctor.presentation.main.profile.PersonalInfoViewModel
import ke.co.smartroundclinic.doctor.presentation.main.profile.destinations.BankingDetails
import ke.co.smartroundclinic.doctor.presentation.main.profile.destinations.CreateNewPasswordSecurity
import ke.co.smartroundclinic.doctor.presentation.main.profile.destinations.PasswordChanged
import ke.co.smartroundclinic.doctor.presentation.main.profile.destinations.PersonalInfo
import ke.co.smartroundclinic.doctor.presentation.main.profile.destinations.ProfileList
import ke.co.smartroundclinic.doctor.presentation.main.profile.destinations.ResetPassword
import ke.co.smartroundclinic.doctor.presentation.main.profile.destinations.ScheduleManagement
import ke.co.smartroundclinic.doctor.presentation.main.profile.destinations.LicenceManagement
import ke.co.smartroundclinic.doctor.presentation.main.profile.destinations.LicenceViewer
import ke.co.smartroundclinic.doctor.presentation.main.profile.destinations.Specialization
import ke.co.smartroundclinic.doctor.presentation.main.profile.destinations.Support
import ke.co.smartroundclinic.doctor.presentation.main.profile.destinations.VerifyEmailSecurity
import ke.co.smartroundclinic.doctor.presentation.main.profile.ui.CreateNewPasswordSecurityScreen
import ke.co.smartroundclinic.doctor.presentation.main.profile.ui.PasswordChangedScreen
import ke.co.smartroundclinic.doctor.presentation.main.profile.ui.PaymentDetailsScreen
import ke.co.smartroundclinic.doctor.presentation.main.profile.ui.PersonalInfoScreen
import ke.co.smartroundclinic.doctor.presentation.main.profile.ui.ProfileListScreen
import ke.co.smartroundclinic.doctor.presentation.main.profile.ui.ResetPasswordScreen
import ke.co.smartroundclinic.doctor.presentation.main.profile.ui.ScheduleManagementScreen
import ke.co.smartroundclinic.doctor.presentation.main.profile.ui.LicenceScreen
import ke.co.smartroundclinic.doctor.presentation.main.profile.ui.LicenceViewerScreen
import ke.co.smartroundclinic.doctor.presentation.main.profile.ui.SpecializationScreen
import ke.co.smartroundclinic.doctor.presentation.main.profile.ui.SupportScreen
import ke.co.smartroundclinic.doctor.presentation.main.profile.ui.VerifyEmailSecurityScreen
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun HomeRoot(
    modifier: Modifier = Modifier,
    onAtRootChanged: (Boolean) -> Unit = {},
    onSignOut: () -> Unit = {},
    onSeeAllAppointments: () -> Unit = {},
    onSeeAllConsultations: () -> Unit = {},
    onOpenConsultation: (appointmentId: String, patientName: String) -> Unit = { _, _ -> },
    pendingDestinations: List<NavKey> = emptyList(),
    onPendingNavigated: () -> Unit = {},
) {
    val backStack = retain { mutableStateListOf<NavKey>(HomeList) }
    val isAtRoot = backStack.size == 1
    val viewModel: PersonalInfoViewModel = koinViewModel()
    val forgotPasswordViewModel: ForgotPasswordViewModel = koinViewModel()

    SideEffect { onAtRootChanged(isAtRoot) }

    LaunchedEffect(pendingDestinations) {
        if (pendingDestinations.isNotEmpty()) {
            pendingDestinations.forEach { backStack.add(it) }
            onPendingNavigated()
        }
    }

    NavDisplay(
        modifier = modifier,
        backStack = backStack,
        onBack = { backStack.removeLastOrNull() },
        entryProvider = entryProvider {
            entry<HomeList> {
                HomeScreen(
                    onProfileClick = { backStack.add(ProfileList) },
                    onSeeAllAppointments = onSeeAllAppointments,
                    onSeeAllConsultations = onSeeAllConsultations,
                    onOpenConsultation = onOpenConsultation,
                    onSetUpCalendar = { backStack.add(ScheduleManagement) },
                )
            }
            entry<ProfileList> {
                ProfileListScreen(
                    onBack = { backStack.removeLastOrNull() },
                    onPersonalInfo = { backStack.add(PersonalInfo) },
                    onBankingDetails = { backStack.add(BankingDetails) },
                    onSecuritySettings = { backStack.add(ResetPassword) },
                    onSupport = { backStack.add(Support) },
                    onScheduleManagement = { backStack.add(ScheduleManagement) },
                    onSpecialization = { backStack.add(Specialization(it)) },
                    onLicences = { backStack.add(LicenceManagement) },
                    onSignOut = onSignOut,
                    viewModel = viewModel,
                )
            }
            entry<PersonalInfo> {
                PersonalInfoScreen(onBack = { backStack.removeLastOrNull() })
            }
            entry<BankingDetails> {
                PaymentDetailsScreen(onBack = { backStack.removeLastOrNull() })
            }
            entry<ResetPassword> {
                ResetPasswordScreen(
                    onProceed = { backStack.add(VerifyEmailSecurity) },
                    onBack = { backStack.removeLastOrNull() },
                    viewModel = forgotPasswordViewModel,
                )
            }
            entry<VerifyEmailSecurity> {
                VerifyEmailSecurityScreen(
                    onContinue = { backStack.add(CreateNewPasswordSecurity) },
                    onBack = { backStack.removeLastOrNull() },
                    viewModel = forgotPasswordViewModel,
                )
            }
            entry<CreateNewPasswordSecurity> {
                CreateNewPasswordSecurityScreen(
                    onSuccess = {
                        backStack.removeAll { it is ResetPassword || it is VerifyEmailSecurity || it is CreateNewPasswordSecurity }
                        backStack.add(PasswordChanged)
                    },
                    onBack = { backStack.removeLastOrNull() },
                    viewModel = forgotPasswordViewModel,
                )
            }
            entry<PasswordChanged> {
                PasswordChangedScreen(
                    onOk = { backStack.removeAll { it is PasswordChanged } },
                )
            }
            entry<Support> {
                SupportScreen(onBack = { backStack.removeLastOrNull() })
            }
            entry<ScheduleManagement> {
                ScheduleManagementScreen(onBack = { backStack.removeLastOrNull() })
            }
            entry<Specialization> {
                SpecializationScreen(isEnabled = it.isEnabled, onBack = { backStack.removeLastOrNull() })
            }
            entry<LicenceManagement> {
                LicenceScreen(
                    onBack = { backStack.removeLastOrNull() },
                    onViewLicence = { url, name -> backStack.add(LicenceViewer(url, name)) },
                )
            }
            entry<LicenceViewer> {
                LicenceViewerScreen(
                    url = it.url,
                    name = it.name,
                    onBack = { backStack.removeLastOrNull() },
                )
            }
        },
    )
}
