package ke.co.smartroundclinic.doctor.presentation.auth

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import ke.co.smartroundclinic.doctor.common.Resource
import ke.co.smartroundclinic.doctor.core.snackbar.SnackbarController
import ke.co.smartroundclinic.doctor.domain.usecase.auth.SignInUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class SignInViewModel(
    private val signInUseCase: SignInUseCase,
    private val snackbarController: SnackbarController,
) : ViewModel() {

    private val _isSigningIn = MutableStateFlow(false)
    val isSigningIn = _isSigningIn.asStateFlow()

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
                        onUnverified(email)
                    } else {
                        snackbarController.show(result.message ?: "Welcome back!")
                        onSuccess()
                    }
                }
                is Resource.Error -> snackbarController.show(result.message ?: "Sign in failed")
                is Resource.Loading -> Unit
            }
            _isSigningIn.value = false
        }
    }
}
