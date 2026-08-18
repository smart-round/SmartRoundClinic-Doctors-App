package ke.co.smartroundclinic.doctor.presentation.main.services

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.retain.retain
import androidx.compose.ui.Modifier
import androidx.navigation3.runtime.NavKey
import androidx.navigation3.runtime.entryProvider
import androidx.navigation3.ui.NavDisplay
import ke.co.smartroundclinic.doctor.domain.model.Doctor
import ke.co.smartroundclinic.doctor.presentation.main.services.destinations.DoctorProfile
import ke.co.smartroundclinic.doctor.presentation.main.services.destinations.DoctorsByCategory
import ke.co.smartroundclinic.doctor.presentation.main.services.destinations.ServicesList
import ke.co.smartroundclinic.doctor.presentation.main.services.ui.DoctorProfileScreen
import ke.co.smartroundclinic.doctor.presentation.main.services.ui.DoctorsByCategoryScreen
import ke.co.smartroundclinic.doctor.presentation.main.services.ui.ServiceCategoriesScreen
import org.koin.compose.viewmodel.koinViewModel

/**
 * Doctor-facing "browse other doctors" flow — reused as-is by both the Services bottom-nav tab
 * and (embedded, with its own local backstack) the Chat tab's Services sub-tab and the referral
 * doctor picker, per [onPrimaryAction]/[primaryActionLabel] and [headerTitle].
 */
@Composable
fun ServicesRoot(
    selfDoctorId: String?,
    onPrimaryAction: (Doctor) -> Unit,
    modifier: Modifier = Modifier,
    onAtRootChanged: (Boolean) -> Unit = {},
    onProfileClick: () -> Unit = {},
    onNotificationsClick: () -> Unit = {},
    headerTitle: String = "Find a Doctor",
    primaryActionLabel: String = "Connect",
) {
    val backStack = retain { mutableStateListOf<NavKey>(ServicesList) }
    val isAtRoot = backStack.size == 1
    SideEffect { onAtRootChanged(isAtRoot) }

    val vm: ServicesViewModel = koinViewModel()

    NavDisplay(
        modifier = modifier,
        backStack = backStack,
        onBack = { backStack.removeLastOrNull() },
        entryProvider = entryProvider {

            entry<ServicesList> {
                ServiceCategoriesScreen(
                    specialities = vm.specialities,
                    headerTitle = headerTitle,
                    onProfileClick = onProfileClick,
                    onNotificationsClick = onNotificationsClick,
                    onSpecialityClick = { speciality ->
                        backStack.add(
                            DoctorsByCategory(
                                categoryId = speciality.id,
                                categoryName = speciality.title,
                                categoryDescription = speciality.description,
                                categoryIconUrl = speciality.iconUrl,
                            ),
                        )
                    },
                )
            }

            entry<DoctorsByCategory> { dest ->
                LaunchedEffect(dest.categoryId) {
                    vm.loadDoctorsBySpeciality(dest.categoryId, selfDoctorId)
                }
                DoctorsByCategoryScreen(
                    categoryName = dest.categoryName,
                    categoryDescription = dest.categoryDescription,
                    categoryIconUrl = dest.categoryIconUrl,
                    doctors = vm.specialityDoctors,
                    isLoading = vm.isLoadingDoctors,
                    currentPage = vm.doctorsCurrentPage,
                    totalPages = vm.doctorsTotalPages,
                    onLoadPage = { vm.loadDoctorsPage(dest.categoryId, it, selfDoctorId) },
                    onDoctorClick = { doctor ->
                        vm.cacheDoctor(doctor)
                        backStack.add(DoctorProfile(doctor.id))
                    },
                    onPrimaryAction = { doctor ->
                        vm.cacheDoctor(doctor)
                        onPrimaryAction(doctor)
                    },
                    onBack = { backStack.removeLastOrNull() },
                )
            }

            entry<DoctorProfile> { dest ->
                val doctor = vm.doctorById(dest.doctorId)
                if (doctor != null) {
                    DoctorProfileScreen(
                        doctor = doctor,
                        primaryActionLabel = primaryActionLabel,
                        onPrimaryAction = { onPrimaryAction(doctor) },
                        onBack = { backStack.removeLastOrNull() },
                    )
                }
            }
        },
    )
}
