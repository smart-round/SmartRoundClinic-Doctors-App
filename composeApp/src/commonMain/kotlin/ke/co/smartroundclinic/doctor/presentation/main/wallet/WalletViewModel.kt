package ke.co.smartroundclinic.doctor.presentation.main.wallet

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import ke.co.smartroundclinic.doctor.common.Resource
import ke.co.smartroundclinic.doctor.core.snackbar.SnackbarController
import ke.co.smartroundclinic.doctor.domain.model.PaymentSummary
import ke.co.smartroundclinic.doctor.domain.model.WalletTransaction
import ke.co.smartroundclinic.doctor.domain.model.Withdrawal
import ke.co.smartroundclinic.doctor.domain.model.WithdrawalBalance
import ke.co.smartroundclinic.doctor.domain.usecase.wallet.GetWalletTransactionsUseCase
import ke.co.smartroundclinic.doctor.domain.usecase.wallet.GetPaymentSummaryUseCase
import ke.co.smartroundclinic.doctor.domain.usecase.wallet.GetWithdrawalBalanceUseCase
import ke.co.smartroundclinic.doctor.domain.usecase.wallet.GetWithdrawalByIdUseCase
import ke.co.smartroundclinic.doctor.domain.usecase.wallet.GetWithdrawalHistoryUseCase
import ke.co.smartroundclinic.doctor.domain.usecase.wallet.WithdrawUseCase
import kotlinx.coroutines.launch

class WalletViewModel(
    private val getWalletTransactionsUseCase: GetWalletTransactionsUseCase,
    private val getSummaryUseCase: GetPaymentSummaryUseCase,
    private val getBalanceUseCase: GetWithdrawalBalanceUseCase,
    private val withdrawUseCase: WithdrawUseCase,
    private val getWithdrawalHistoryUseCase: GetWithdrawalHistoryUseCase,
    private val getWithdrawalByIdUseCase: GetWithdrawalByIdUseCase,
    private val snackbarController: SnackbarController,
) : ViewModel() {

    var summary by mutableStateOf<PaymentSummary?>(null)
        private set

    var balance by mutableStateOf<WithdrawalBalance?>(null)
        private set

    var transactions by mutableStateOf<List<WalletTransaction>>(emptyList())
        private set

    var withdrawals by mutableStateOf<List<Withdrawal>>(emptyList())
        private set

    var isLoading by mutableStateOf(false)
        private set

    var isWithdrawing by mutableStateOf(false)
        private set

    var withdrawalDetail by mutableStateOf<Withdrawal?>(null)
        private set

    var isLoadingDetail by mutableStateOf(false)
        private set

    init {
        load()
    }

    fun load() {
        viewModelScope.launch {
            isLoading = true
            val summaryResult = getSummaryUseCase()
            val balanceResult = getBalanceUseCase()
            val transactionsResult = getWalletTransactionsUseCase()
            val withdrawalsResult = getWithdrawalHistoryUseCase()
            isLoading = false

            if (summaryResult is Resource.Success) summary = summaryResult.data
            if (balanceResult is Resource.Success) balance = balanceResult.data
            if (transactionsResult is Resource.Success) transactions = transactionsResult.data ?: emptyList()
            if (withdrawalsResult is Resource.Success) withdrawals = withdrawalsResult.data ?: emptyList()

            // Only show an error if every single endpoint failed — individual failures
            // (e.g. no transactions yet for summary) are shown as empty state, not snackbars.
            val allFailed = listOf(summaryResult, balanceResult, transactionsResult, withdrawalsResult)
                .all { it is Resource.Error }
            if (allFailed) {
                snackbarController.show("Failed to load wallet data", isError = true)
            }
        }
    }

    fun loadWithdrawalDetail(id: String) {
        viewModelScope.launch {
            isLoadingDetail = true
            val result = getWithdrawalByIdUseCase(id)
            isLoadingDetail = false
            when (result) {
                is Resource.Success -> withdrawalDetail = result.data
                is Resource.Error -> snackbarController.show(result.message ?: "Failed to load withdrawal", isError = true)
                else -> Unit
            }
        }
    }

    fun clearWithdrawalDetail() { withdrawalDetail = null }

    fun withdraw(idNumber: String, amount: Double, onSuccess: () -> Unit) {
        viewModelScope.launch {
            isWithdrawing = true
            val result = withdrawUseCase(idNumber, amount)
            isWithdrawing = false
            when (result) {
                is Resource.Success -> {
                    snackbarController.show(result.message ?: "Withdrawal initiated successfully")
                    load()
                    onSuccess()
                }
                is Resource.Error -> snackbarController.show(result.message ?: "Withdrawal failed", isError = true)
                else -> Unit
            }
        }
    }
}
