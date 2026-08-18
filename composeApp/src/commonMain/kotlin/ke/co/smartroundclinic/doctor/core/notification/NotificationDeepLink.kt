package ke.co.smartroundclinic.doctor.core.notification

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

sealed class NotificationEvent {
    data object ToNotifications : NotificationEvent()
    data class ToBookingDetail(val appointmentId: String) : NotificationEvent()
    data class ToConversation(val patientId: String, val patientName: String, val appointmentId: String) : NotificationEvent()
    data class ToCall(val patientId: String, val patientName: String, val appointmentId: String) : NotificationEvent()
    data class ToDoctorConversation(val threadId: String, val counterpartName: String) : NotificationEvent()
    data class ToDoctorCall(val threadId: String, val counterpartName: String, val isVideo: Boolean) : NotificationEvent()
    data object ToArticles : NotificationEvent()
    data class ToSupportTicket(val ticketId: String) : NotificationEvent()
}

object NotificationDeepLink {
    private val _pendingEvent = MutableStateFlow<NotificationEvent?>(null)
    val pendingEvent = _pendingEvent.asStateFlow()

    fun signal(event: NotificationEvent) { _pendingEvent.value = event }
    fun consume() { _pendingEvent.value = null }
}
