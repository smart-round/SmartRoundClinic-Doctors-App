package ke.co.smartroundclinic.doctor.core.notification

import com.mmk.kmpnotifier.notification.NotifierManager
import io.github.aakira.napier.Napier
import ke.co.smartroundclinic.doctor.domain.usecase.notification.RegisterDeviceTokenUseCase
import ke.co.smartroundclinic.doctor.notificationPlatform
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.launch
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

private const val TAG = "NotificationSetup"

fun setupNotificationListener() {
    val scope = CoroutineScope(Dispatchers.IO)
    val component = object : KoinComponent {
        val registerDeviceToken: RegisterDeviceTokenUseCase by inject()
    }

    NotifierManager.addListener(object : NotifierManager.Listener {
        override fun onNewToken(token: String) {
            Napier.d(tag = TAG, message = "FCM token refreshed: $token")
            scope.launch {
                val result = component.registerDeviceToken(token, notificationPlatform)
                Napier.d(tag = TAG, message = "Token registration result (onNewToken): $result")
            }
        }

        override fun onNotificationClicked(data: Map<String, Any?>) {
            val event = data["event"]?.toString()
            val appointmentId = data["appointmentId"]?.toString()
            val consultationId = data["consultationId"]?.toString()
            val ticketId = data["ticketId"]?.toString()
            val patientName = (data["patientName"] ?: data["senderName"])?.toString() ?: "Patient"

            Napier.d(tag = TAG, message = "Notification tapped — event=$event appointmentId=$appointmentId consultationId=$consultationId ticketId=$ticketId")

            val notifEvent: NotificationEvent = when (event) {
                "New Appointment Request",
                "Appointment Confirmed",
                "Appointment Cancelled",
                "Appointment Completed",
                "Missed Appointment" -> {
                    if (!appointmentId.isNullOrBlank()) NotificationEvent.ToBookingDetail(appointmentId)
                    else NotificationEvent.ToNotifications
                }
                "Patient is Ready",
                "Patient Joined the Call",
                "Consultation Ended",
                "Call Ended" -> {
                    if (!appointmentId.isNullOrBlank()) NotificationEvent.ToConversation(appointmentId, patientName)
                    else NotificationEvent.ToNotifications
                }
                "New Chat Message" -> when {
                    !appointmentId.isNullOrBlank() -> NotificationEvent.ToConversation(appointmentId, patientName)
                    !ticketId.isNullOrBlank() -> NotificationEvent.ToSupportTicket(ticketId)
                    else -> NotificationEvent.ToNotifications
                }
                "Support ticket status updated" -> {
                    if (!ticketId.isNullOrBlank()) NotificationEvent.ToSupportTicket(ticketId)
                    else NotificationEvent.ToNotifications
                }
                "Article Suspended",
                "Article Deleted" -> NotificationEvent.ToArticles
                else -> NotificationEvent.ToNotifications
            }

            NotificationDeepLink.signal(notifEvent)
        }
    })

    scope.launch {
        val token = NotifierManager.getPushNotifier().getToken()
        Napier.d(tag = TAG, message = "Current FCM token on startup: $token")
        if (token != null) {
            val result = component.registerDeviceToken(token, notificationPlatform)
            Napier.d(tag = TAG, message = "Token registration result (startup): $result")
        } else {
            Napier.w(tag = TAG, message = "FCM token is null on startup — Firebase may not be initialized yet")
        }
    }
}
