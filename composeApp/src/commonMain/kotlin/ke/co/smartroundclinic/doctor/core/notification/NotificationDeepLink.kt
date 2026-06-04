package ke.co.smartroundclinic.doctor.core.notification

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

object NotificationDeepLink {
    private val _pending = MutableStateFlow(false)
    val pending = _pending.asStateFlow()

    fun signal() { _pending.value = true }
    fun consume() { _pending.value = false }
}
