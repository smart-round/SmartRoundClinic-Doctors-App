package ke.co.smartroundclinic.doctor.domain.usecase

import ke.co.smartroundclinic.doctor.domain.usecase.articles.GetCategoriesUseCase
import ke.co.smartroundclinic.doctor.domain.usecase.articles.GetMyArticlesUseCase
import ke.co.smartroundclinic.doctor.domain.usecase.auth.GetUserUseCase
import ke.co.smartroundclinic.doctor.domain.usecase.scheduling.GetAppointmentsUseCase
import ke.co.smartroundclinic.doctor.domain.usecase.scheduling.GetScheduleUseCase
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch

class PreloadDashboardUseCase(
    private val getUserUseCase: GetUserUseCase,
    private val getScheduleUseCase: GetScheduleUseCase,
    private val getAppointmentsUseCase: GetAppointmentsUseCase,
    private val getMyArticlesUseCase: GetMyArticlesUseCase,
    private val getCategoriesUseCase: GetCategoriesUseCase,
) {
    suspend operator fun invoke() = coroutineScope {
        launch { getUserUseCase() }
        launch { getScheduleUseCase() }
        launch { getAppointmentsUseCase() }
        launch { getMyArticlesUseCase() }
        launch { getCategoriesUseCase() }
    }
}
