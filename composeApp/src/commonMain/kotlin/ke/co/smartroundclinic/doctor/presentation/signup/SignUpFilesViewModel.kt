package ke.co.smartroundclinic.doctor.presentation.signup

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel

// Holds binary file data in memory only — never touches SavedState / Bundle.
// Scoped to SignUpRoot so it lives exactly as long as the signup flow.
class SignUpFilesViewModel : ViewModel() {
    var profilePictureBytes by mutableStateOf<ByteArray?>(null)
    var licenseFileName by mutableStateOf("")
    var licenseFileBytes by mutableStateOf<ByteArray?>(null)
}
