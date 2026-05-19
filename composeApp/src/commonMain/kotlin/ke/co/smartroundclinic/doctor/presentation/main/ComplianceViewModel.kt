package ke.co.smartroundclinic.doctor.presentation.main

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import ke.co.smartroundclinic.doctor.common.Resource
import ke.co.smartroundclinic.doctor.domain.model.ComplianceStatus
import ke.co.smartroundclinic.doctor.domain.usecase.auth.GetComplianceStatusUseCase
import ke.co.smartroundclinic.doctor.domain.usecase.auth.SignOutUseCase
import kotlinx.coroutines.launch

class ComplianceViewModel(
    private val getComplianceStatusUseCase: GetComplianceStatusUseCase,
    private val signOutUseCase: SignOutUseCase,
) : ViewModel() {

    var complianceStatus by mutableStateOf<ComplianceStatus?>(null)
        private set

    var isChecking by mutableStateOf(false)
        private set

    var isSigningOut by mutableStateOf(false)
        private set

    init {
        checkStatus()
    }

    fun checkStatus() {
        viewModelScope.launch {
            isChecking = true
            val result = getComplianceStatusUseCase()
            if (result is Resource.Success) {
                complianceStatus = result.data
            }
            isChecking = false
        }
    }

    fun signOut(onComplete: () -> Unit) {
        viewModelScope.launch {
            isSigningOut = true
            signOutUseCase()
            isSigningOut = false
            onComplete()
        }
    }
}
