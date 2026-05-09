package ke.co.smartroundclinic.doctor.core.snackbar

import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow

class SnackbarController {
    private val _messages = MutableSharedFlow<String>(extraBufferCapacity = 16)
    val messages = _messages.asSharedFlow()

    fun show(message: String) {
        _messages.tryEmit(message)
    }
}
