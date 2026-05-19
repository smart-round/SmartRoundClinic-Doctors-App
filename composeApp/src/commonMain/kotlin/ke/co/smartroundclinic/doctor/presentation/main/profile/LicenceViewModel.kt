package ke.co.smartroundclinic.doctor.presentation.main.profile

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import ke.co.smartroundclinic.doctor.common.Resource
import ke.co.smartroundclinic.doctor.core.snackbar.SnackbarController
import ke.co.smartroundclinic.doctor.domain.model.Licence
import ke.co.smartroundclinic.doctor.domain.usecase.licence.DeleteLicenceUseCase
import ke.co.smartroundclinic.doctor.domain.usecase.licence.GetAllLicencesUseCase
import ke.co.smartroundclinic.doctor.domain.usecase.licence.UploadLicenceUseCase
import kotlinx.coroutines.launch

class LicenceViewModel(
    private val getAllLicencesUseCase: GetAllLicencesUseCase,
    private val uploadLicenceUseCase: UploadLicenceUseCase,
    private val deleteLicenceUseCase: DeleteLicenceUseCase,
    private val snackbarController: SnackbarController,
) : ViewModel() {

    var licences by mutableStateOf<List<Licence>>(emptyList())
        private set

    var isRefreshing by mutableStateOf(false)
        private set

    var isUploading by mutableStateOf(false)
        private set

    var deletingId by mutableStateOf<String?>(null)
        private set

    init {
        loadLicences()
    }

    fun loadLicences() {
        viewModelScope.launch {
            isRefreshing = true
            val result = getAllLicencesUseCase()
            isRefreshing = false
            when (result) {
                is Resource.Success -> licences = result.data ?: emptyList()
                is Resource.Error -> snackbarController.show(result.message ?: "Failed to load licences", isError = true)
                else -> Unit
            }
        }
    }

    fun uploadLicence(name: String, fileName: String, bytes: ByteArray, onSuccess: () -> Unit) {
        viewModelScope.launch {
            isUploading = true
            val result = uploadLicenceUseCase(name, fileName, bytes)
            isUploading = false
            when (result) {
                is Resource.Success -> {
                    snackbarController.show(result.message ?: "Licence uploaded successfully")
                    loadLicences()
                    onSuccess()
                }
                is Resource.Error -> snackbarController.show(result.message ?: "Failed to upload licence", isError = true)
                else -> Unit
            }
        }
    }

    fun deleteLicence(id: String) {
        viewModelScope.launch {
            deletingId = id
            val result = deleteLicenceUseCase(id)
            deletingId = null
            when (result) {
                is Resource.Success -> {
                    snackbarController.show(result.data?.message ?: "Licence deleted")
                    licences = licences.filter { it.id != id }
                }
                is Resource.Error -> snackbarController.show(result.message ?: "Failed to delete licence", isError = true)
                else -> Unit
            }
        }
    }
}
