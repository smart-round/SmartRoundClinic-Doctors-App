package ke.co.smartroundclinic.doctor.presentation.main

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.liftric.kvault.KVault
import ke.co.smartroundclinic.doctor.common.Constants.KEY_COMPLIANCE_IS_APPROVED
import ke.co.smartroundclinic.doctor.common.Constants.KEY_COMPLIANCE_STATUS
import ke.co.smartroundclinic.doctor.common.Resource
import ke.co.smartroundclinic.doctor.domain.model.ComplianceStatus
import ke.co.smartroundclinic.doctor.domain.usecase.auth.GetComplianceStatusUseCase
import ke.co.smartroundclinic.doctor.domain.usecase.auth.SignOutUseCase
import kotlinx.coroutines.launch

class ComplianceViewModel(
    private val getComplianceStatusUseCase: GetComplianceStatusUseCase,
    private val signOutUseCase: SignOutUseCase,
    private val secureStorage: KVault,
) : ViewModel() {

    var complianceStatus by mutableStateOf<ComplianceStatus?>(null)
        private set

    var isChecking by mutableStateOf(false)
        private set

    var isSigningOut by mutableStateOf(false)
        private set

    init {
        // If we know from a previous session that the account is not approved,
        // show the dialog immediately with a generic PENDING state so the user
        // isn't left with a blank screen while the API call completes.
        // The real status (PENDING vs REJECTED + rejection reason) is filled in
        // once checkStatus() returns.
        val cachedIsApproved = secureStorage.bool(KEY_COMPLIANCE_IS_APPROVED) ?: true
        if (!cachedIsApproved) {
            complianceStatus = ComplianceStatus(isApproved = false, status = "PENDING")
        }
        checkStatus()
    }

    fun checkStatus() {
        viewModelScope.launch {
            isChecking = true
            val result = getComplianceStatusUseCase()
            if (result is Resource.Success) {
                complianceStatus = result.data
                result.data?.let { status ->
                    secureStorage.set(KEY_COMPLIANCE_STATUS, status.status)
                    secureStorage.set(KEY_COMPLIANCE_IS_APPROVED, status.isApproved)
                }
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
