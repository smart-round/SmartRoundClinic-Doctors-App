package ke.co.smartroundclinic.doctor.presentation.main.profile

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import ke.co.smartroundclinic.doctor.common.Resource
import ke.co.smartroundclinic.doctor.core.snackbar.SnackbarController
import ke.co.smartroundclinic.doctor.data.remote.dto.request.Gender
import ke.co.smartroundclinic.doctor.data.remote.dto.request.UpdatePersonalInformationReq
import ke.co.smartroundclinic.doctor.data.remote.dto.response.UploadProfilePictureRes
import ke.co.smartroundclinic.doctor.domain.model.User
import ke.co.smartroundclinic.doctor.domain.repository.DoctorSpecializationLocalRepository
import ke.co.smartroundclinic.doctor.domain.repository.UserLocalRepository
import ke.co.smartroundclinic.doctor.domain.usecase.auth.DeleteProfilePictureUseCase
import ke.co.smartroundclinic.doctor.domain.usecase.auth.GetUserUseCase
import ke.co.smartroundclinic.doctor.domain.usecase.auth.SignOutUseCase
import ke.co.smartroundclinic.doctor.domain.usecase.auth.UpdatePersonalInfoUseCase
import ke.co.smartroundclinic.doctor.domain.usecase.auth.UploadProfilePictureUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class PersonalInfoViewModel(
    private val uploadProfilePictureUseCase: UploadProfilePictureUseCase,
    private val deleteProfilePictureUseCase: DeleteProfilePictureUseCase,
    private val updatePersonalInfoUseCase: UpdatePersonalInfoUseCase,
    private val getUserUseCase: GetUserUseCase,
    private val userLocalRepository: UserLocalRepository,
    private val doctorSpecializationLocalRepository: DoctorSpecializationLocalRepository,
    private val signOutUseCase: SignOutUseCase,
    private val snackbarController: SnackbarController,
) : ViewModel() {

    val user: StateFlow<User?> = userLocalRepository.observeUser()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), null)

    val specializationTitle: StateFlow<String> = doctorSpecializationLocalRepository.observeSpecialization()
        .map { it?.title ?: "" }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), "")

    private val _uploadState = MutableStateFlow<Resource<UploadProfilePictureRes>?>(null)
    val uploadState: StateFlow<Resource<UploadProfilePictureRes>?> = _uploadState

    var isSaving by mutableStateOf(false)
        private set

    var isDeleting by mutableStateOf(false)
        private set

    init {
        refreshUser()
    }

    fun refreshUser() {
        viewModelScope.launch { getUserUseCase() }
    }

    fun uploadProfilePicture(bytes: ByteArray) {
        viewModelScope.launch {
            _uploadState.value = Resource.Loading()
            val result = uploadProfilePictureUseCase(bytes)
            _uploadState.value = result
            when (result) {
                is Resource.Success -> refreshUser()
                is Resource.Error -> snackbarController.show(result.message ?: "Failed to upload profile picture")
                else -> Unit
            }
        }
    }

    fun deleteProfilePicture(onSuccess: () -> Unit) {
        viewModelScope.launch {
            isDeleting = true
            val result = deleteProfilePictureUseCase()
            isDeleting = false
            when (result) {
                is Resource.Success -> {
                    snackbarController.show(result.message ?: "Profile picture removed")
                    refreshUser()
                    onSuccess()
                }
                is Resource.Error -> snackbarController.show(result.message ?: "Failed to delete profile picture")
                else -> Unit
            }
        }
    }

    fun signOut(onComplete: () -> Unit) {
        viewModelScope.launch {
            signOutUseCase()
            onComplete()
        }
    }

    fun updatePersonalInfo(
        fullName: String,
        email: String,
        phoneNumber: String,
        dateOfBirth: String?,
        gender: Gender?,
    ) {
        viewModelScope.launch {
            isSaving = true
            val result = updatePersonalInfoUseCase(
                UpdatePersonalInformationReq(
                    fullName = fullName.takeIf { it.isNotBlank() },
                    email = email.takeIf { it.isNotBlank() },
                    phoneNumber = phoneNumber,
                    dateOfBirth = dateOfBirth?.takeIf { it.isNotBlank() },
                    gender = gender,
                )
            )
            isSaving = false
            when (result) {
                is Resource.Success -> {
                    snackbarController.show(result.message ?: "Personal information updated")
                    refreshUser()
                }
                is Resource.Error -> snackbarController.show(result.message ?: "Failed to update personal information")
                else -> Unit
            }
        }
    }
}
