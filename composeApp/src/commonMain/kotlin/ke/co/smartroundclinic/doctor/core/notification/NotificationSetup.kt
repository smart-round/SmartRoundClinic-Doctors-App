package ke.co.smartroundclinic.doctor.core.notification

import com.mmk.kmpnotifier.notification.NotifierManager
import io.github.aakira.napier.Napier
import ke.co.smartroundclinic.doctor.domain.repository.AppointmentLocalRepository
import ke.co.smartroundclinic.doctor.domain.usecase.notification.RegisterDeviceTokenUseCase
import ke.co.smartroundclinic.doctor.notificationPlatform
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

private const val TAG = "NotificationSetup"

fun setupNotificationListener() {
    val scope = CoroutineScope(Dispatchers.IO)
    val component = object : KoinComponent {
        val registerDeviceToken: RegisterDeviceTokenUseCase by inject()
        val appointmentLocalRepository: AppointmentLocalRepository by inject()
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
            val patientId = data["patientId"]?.toString()
            val patientName = (data["patientName"] ?: data["senderName"])?.toString() ?: "Patient"

            Napier.d(tag = TAG, message = "Notification tapped — event=$event appointmentId=$appointmentId consultationId=$consultationId ticketId=$ticketId")

            suspend fun toConversation(appointmentId: String): NotificationEvent {
                // The push payload doesn't always carry patientId (only "New Chat Message" does today) —
                // fall back to the locally-cached appointment so every chat-bound event can still deep-link.
                val resolvedPatientId = patientId
                    ?: component.appointmentLocalRepository.observeAppointments().first()
                        .firstOrNull { it.id == appointmentId }?.patientId
                return if (!resolvedPatientId.isNullOrBlank()) NotificationEvent.ToConversation(resolvedPatientId, patientName, appointmentId)
                else NotificationEvent.ToNotifications
            }

            scope.launch {
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
                        if (!appointmentId.isNullOrBlank()) toConversation(appointmentId)
                        else NotificationEvent.ToNotifications
                    }
                    "New Chat Message" -> when {
                        !appointmentId.isNullOrBlank() -> toConversation(appointmentId)
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
