package ke.co.smartroundclinic.doctor.presentation.main.profile

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import ke.co.smartroundclinic.doctor.common.Resource
import ke.co.smartroundclinic.doctor.core.snackbar.SnackbarController
import ke.co.smartroundclinic.doctor.data.remote.dto.request.AddPaymentDetailsReq
import ke.co.smartroundclinic.doctor.data.remote.dto.request.UpdatePaymentDetailsReq
import ke.co.smartroundclinic.doctor.domain.model.Bank
import ke.co.smartroundclinic.doctor.domain.model.BankBranch
import ke.co.smartroundclinic.doctor.domain.model.PaymentDetails
import ke.co.smartroundclinic.doctor.domain.repository.PaymentDetailsLocalRepository
import ke.co.smartroundclinic.doctor.domain.usecase.bank.AddPaymentDetailsUseCase
import ke.co.smartroundclinic.doctor.domain.usecase.bank.GetLocalBanksUseCase
import ke.co.smartroundclinic.doctor.domain.usecase.bank.GetPaymentDetailsUseCase
import ke.co.smartroundclinic.doctor.domain.usecase.bank.UpdatePaymentDetailsUseCase
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class PaymentDetailsViewModel(
    private val getPaymentDetailsUseCase: GetPaymentDetailsUseCase,
    private val updatePaymentDetailsUseCase: UpdatePaymentDetailsUseCase,
    private val addPaymentDetailsUseCase: AddPaymentDetailsUseCase,
    private val getLocalBanksUseCase: GetLocalBanksUseCase,
    private val localRepository: PaymentDetailsLocalRepository,
    private val snackbarController: SnackbarController,
) : ViewModel() {

    val paymentDetails: StateFlow<PaymentDetails?> = localRepository.observePaymentDetails()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), null)

    var banks by mutableStateOf<List<Bank>>(emptyList())
        private set

    var isRefreshing by mutableStateOf(false)
        private set

    var isSaving by mutableStateOf(false)
        private set

    init {
        refreshPaymentDetails()
        loadBanks()
    }

    fun refreshPaymentDetails() {
        viewModelScope.launch {
            isRefreshing = true
            getPaymentDetailsUseCase()
            loadBanks(forceRefresh = true)
            isRefreshing = false
        }
    }

    private fun loadBanks(forceRefresh: Boolean = false) {
        viewModelScope.launch {
            val result = getLocalBanksUseCase(forceRefresh)
            if (result is Resource.Success) banks = result.data ?: emptyList()
        }
    }

    fun savePaymentDetails(
        accountName: String,
        accountNumber: String,
        bank: Bank,
        branch: BankBranch,
    ) {
        viewModelScope.launch {
            isSaving = true
            val existing = paymentDetails.value
            val result = if (existing != null) {
                updatePaymentDetailsUseCase(
                    UpdatePaymentDetailsReq(
                        accountName = accountName,
                        accountNumber = accountNumber,
                        bankName = bank.bankName,
                        bankCode = bank.bankCode,
                        branchName = branch.branchName,
                        branchCode = branch.branchCode,
                    )
                )
            } else {
                val addResult = addPaymentDetailsUseCase(
                    AddPaymentDetailsReq(
                        accountName = accountName,
                        accountNumber = accountNumber,
                        bankName = bank.bankName,
                        bankCode = bank.bankCode,
                        branchName = branch.branchName,
                        branchCode = branch.branchCode,
                    )
                )
                when (addResult) {
                    is Resource.Success -> {
                        snackbarController.show(addResult.message ?: "Banking details saved")
                        refreshPaymentDetails()
                    }
                    is Resource.Error -> snackbarController.show(addResult.message ?: "Failed to save banking details")
                    else -> Unit
                }
                isSaving = false
                return@launch
            }
            isSaving = false
            when (result) {
                is Resource.Success -> {
                    snackbarController.show(result.message ?: "Banking details saved")
                    refreshPaymentDetails()
                }
                is Resource.Error -> snackbarController.show(result.message ?: "Failed to save banking details")
                else -> Unit
            }
        }
    }
}
