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
import ke.co.smartroundclinic.doctor.core.snackbar.SnackbarController
import ke.co.smartroundclinic.doctor.domain.model.ComplianceStatus
import ke.co.smartroundclinic.doctor.domain.usecase.auth.ConfirmComplianceCorrectionUseCase
import ke.co.smartroundclinic.doctor.domain.usecase.auth.GetComplianceStatusUseCase
import ke.co.smartroundclinic.doctor.domain.usecase.auth.SignOutUseCase
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

private const val MAX_CHECK_ATTEMPTS = 4
private const val RETRY_DELAY_MS = 1500L

class ComplianceViewModel(
    private val getComplianceStatusUseCase: GetComplianceStatusUseCase,
    private val signOutUseCase: SignOutUseCase,
    private val secureStorage: KVault,
    private val confirmComplianceCorrectionUseCase: ConfirmComplianceCorrectionUseCase,
    private val snackbarController: SnackbarController,
) : ViewModel() {

    var complianceStatus by mutableStateOf<ComplianceStatus?>(null)
        private set

    var isChecking by mutableStateOf(false)
        private set

    var isSigningOut by mutableStateOf(false)
        private set

    var isConfirmingCorrection by mutableStateOf(false)
        private set

    init {
        // Stay unresolved (null, dialog hidden) until checkStatus() confirms the real status —
        // an already-approved doctor must never see a "pending" flash on every sign-in.
        checkStatus()
    }

    fun checkStatus() {
        viewModelScope.launch {
            isChecking = true
            var result = getComplianceStatusUseCase()
            var attempts = 1
            while (result !is Resource.Success && attempts < MAX_CHECK_ATTEMPTS) {
                delay(RETRY_DELAY_MS)
                result = getComplianceStatusUseCase()
                attempts++
            }
            if (result is Resource.Success) {
                complianceStatus = result.data
                result.data?.let { status ->
                    secureStorage.set(KEY_COMPLIANCE_STATUS, status.status)
                    secureStorage.set(KEY_COMPLIANCE_IS_APPROVED, status.isApproved)
                }
            } else if (complianceStatus == null && secureStorage.bool(KEY_COMPLIANCE_IS_APPROVED) != true) {
                // Fail closed only when we've never confirmed approval before (e.g. right after
                // signup, before the compliance record has materialized on the backend, or the
                // backend is unreachable) — never override an already-confirmed approved status
                // just because a later periodic re-check failed transiently.
                complianceStatus = ComplianceStatus(isApproved = false, status = "PENDING")
            }
            isChecking = false
        }
    }

    fun confirmCorrection() {
        viewModelScope.launch {
            isConfirmingCorrection = true
            when (val result = confirmComplianceCorrectionUseCase()) {
                is Resource.Success -> {
                    snackbarController.show("Corrections submitted for review")
                    checkStatus()
                }
                is Resource.Error -> snackbarController.show(result.message ?: "Failed to submit corrections", isError = true)
                else -> Unit
            }
            isConfirmingCorrection = false
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
