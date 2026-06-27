package ke.co.smartroundclinic.doctor.presentation.signup

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import ke.co.smartroundclinic.doctor.common.Resource
import ke.co.smartroundclinic.doctor.core.snackbar.SnackbarController
import ke.co.smartroundclinic.doctor.domain.model.Bank
import ke.co.smartroundclinic.doctor.domain.model.DoctorSignUpData
import ke.co.smartroundclinic.doctor.domain.usecase.auth.SignUpUseCase
import ke.co.smartroundclinic.doctor.domain.usecase.bank.GetLocalBanksUseCase
import ke.co.smartroundclinic.doctor.presentation.signup.BioData
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class BankDetailsViewModel(
    private val getLocalBanksUseCase: GetLocalBanksUseCase,
    private val signUpUseCase: SignUpUseCase,
    private val snackbarController: SnackbarController,
) : ViewModel() {

    private val _banks = MutableStateFlow<List<Bank>>(emptyList())
    val banks = _banks.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading = _isLoading.asStateFlow()

    private val _error = MutableStateFlow<String?>(null)
    val error = _error.asStateFlow()

    private val _isSubmitting = MutableStateFlow(false)
    val isSubmitting = _isSubmitting.asStateFlow()

    init {
        loadBanks()
    }

    private fun loadBanks() {
        viewModelScope.launch {
            _isLoading.value = true
            _error.value = null
            when (val result = getLocalBanksUseCase()) {
                is Resource.Success -> _banks.value = result.data.orEmpty()
                is Resource.Error -> _error.value = result.message
                is Resource.Loading -> Unit
            }
            _isLoading.value = false
        }
    }

    fun signUp(
        personalInfo: PersonalInfoData,
        specialization: SpecializationData,
        bioData: BioData,
        licenseFileBytes: ByteArray,
        licenseFileName: String,
        licenseFileMimeType: String,
        profilePictureBytes: ByteArray?,
        bankName: String,
        bankCode: String,
        branchCode: String,
        branchName: String,
        accountName: String,
        accountNumber: String,
        onSuccess: () -> Unit,
    ) {
        viewModelScope.launch {
            _isSubmitting.value = true
            val result = signUpUseCase(
                DoctorSignUpData(
                    fullName = personalInfo.fullName,
                    email = personalInfo.email,
                    password = personalInfo.password,
                    gender = personalInfo.gender,
                    phoneNumber = personalInfo.phoneNumber,
                    kraPin = personalInfo.kraPin,
                    specializationId = specialization.specializationId,
                    licenceName = specialization.specializationName,
                    licenceFileName = licenseFileName,
                    licenceFileMimeType = licenseFileMimeType,
                    licenceFile = licenseFileBytes,
                    bankName = bankName,
                    bankCode = bankCode,
                    branchCode = branchCode,
                    branchName = branchName,
                    accountName = accountName,
                    accountNumber = accountNumber,
                    profilePicture = profilePictureBytes,
                    kmpdcRegNumber = bioData.kmpdcRegNumber.takeIf { it.isNotBlank() },
                    title = bioData.title.takeIf { it.isNotBlank() },
                    bio = bioData.bio.takeIf { it.isNotBlank() },
                    yearsOfExperience = bioData.yearsOfExperience.toIntOrNull(),
                    languages = bioData.languages.split(",").map { it.trim() }.filter { it.isNotBlank() },
                    facilityName = bioData.facilityName.takeIf { it.isNotBlank() },
                )
            )
            when (result) {
                is Resource.Success -> {
                    snackbarController.show(result.message ?: "Registration submitted! Please verify your email.")
                    onSuccess()
                }
                is Resource.Error -> {
                    result.message?.let { snackbarController.show(it) }
                }
                is Resource.Loading -> Unit
            }
            _isSubmitting.value = false
        }
    }
}
