package ke.co.smartroundclinic.doctor.presentation.auth

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mmk.kmpnotifier.notification.NotifierManager
import ke.co.smartroundclinic.doctor.common.Resource
import ke.co.smartroundclinic.doctor.core.snackbar.SnackbarController
import ke.co.smartroundclinic.doctor.domain.usecase.PreloadDashboardUseCase
import ke.co.smartroundclinic.doctor.domain.usecase.auth.GetUserUseCase
import ke.co.smartroundclinic.doctor.domain.usecase.auth.SignInUseCase
import ke.co.smartroundclinic.doctor.domain.usecase.auth.SignOutUseCase
import ke.co.smartroundclinic.doctor.domain.usecase.notification.RegisterDeviceTokenUseCase
import ke.co.smartroundclinic.doctor.notificationPlatform
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class SignInViewModel(
    private val signInUseCase: SignInUseCase,
    private val snackbarController: SnackbarController,
    private val preloadDashboardUseCase: PreloadDashboardUseCase,
    private val signOutUseCase: SignOutUseCase,
    private val getUserUseCase: GetUserUseCase,
    private val registerDeviceTokenUseCase: RegisterDeviceTokenUseCase,
) : ViewModel() {

    private val _isSigningIn = MutableStateFlow(false)
    val isSigningIn = _isSigningIn.asStateFlow()

    private val _isWrongApp = MutableStateFlow(false)
    val isWrongApp = _isWrongApp.asStateFlow()

    fun dismissWrongApp() { _isWrongApp.value = false }

    fun signIn(
        email: String,
        password: String,
        onSuccess: () -> Unit,
        onUnverified: (email: String) -> Unit,
    ) {
        viewModelScope.launch {
            _isSigningIn.value = true
            when (val result = signInUseCase(email, password)) {
                is Resource.Success -> {
                    if (result.data?.verificationStatus?.uppercase() == "UNVERIFIED") {
                        snackbarController.show("Account not verified. Please check your email for the OTP code.")
                        _isSigningIn.value = false
                        onUnverified(email)
                        return@launch
                    }
                    val userResult = getUserUseCase()
                    val role = (userResult as? Resource.Success)?.data?.role?.uppercase()
                    if (role != null && role != "DOCTOR") {
                        signOutUseCase()
                        _isSigningIn.value = false
                        _isWrongApp.value = true
                    } else {
                        launch { preloadDashboardUseCase() }
                        launch {
                            NotifierManager.getPushNotifier().getToken()?.let { token ->
                                registerDeviceTokenUseCase(token, notificationPlatform)
                            }
                        }
                        snackbarController.show(result.message ?: "Welcome back!")
                        _isSigningIn.value = false
                        onSuccess()
                    }
                }
                is Resource.Error -> {
                    snackbarController.show(isError = true,message = result.message ?: "Sign in failed")
                    _isSigningIn.value = false
                }
                is Resource.Loading -> Unit
            }
        }
    }
}
