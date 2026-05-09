package ke.co.smartroundclinic.doctor.presentation.splash

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import ke.co.smartroundclinic.doctor.common.Constants
import ke.co.smartroundclinic.doctor.common.Resource
import ke.co.smartroundclinic.doctor.domain.usecase.auth.RefreshTokenUseCase
import ke.co.smartroundclinic.doctor.domain.usecase.datastore.ObserveKeyUseCase
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

private const val SPLASH_DELAY_MS = 1_500L

class SplashViewModel(
    private val observeKeyUseCase: ObserveKeyUseCase,
    private val refreshTokenUseCase: RefreshTokenUseCase,
) : ViewModel() {

    fun start(
        onOnboarding: () -> Unit,
        onSignIn: () -> Unit,
        onDashboard: () -> Unit,
    ) {
        viewModelScope.launch {
            kotlinx.coroutines.delay(SPLASH_DELAY_MS)

            val onboarded = observeKeyUseCase(Constants.ONBOARDING_KEY)
                .first()
                ?.toBooleanStrictOrNull() == true

            if (!onboarded) {
                onOnboarding()
                return@launch
            }

            when (refreshTokenUseCase()) {
                is Resource.Success -> onDashboard()
                else -> onSignIn()
            }
        }
    }
}
