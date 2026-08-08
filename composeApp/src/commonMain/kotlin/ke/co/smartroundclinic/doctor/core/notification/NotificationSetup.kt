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

        // Data-only messages (call invite/answer/decline/cancel) — fires on receipt, including
        // while backgrounded, unlike onNotificationClicked which only fires on tap.
        override fun onPayloadData(data: Map<String, *>) {
            val event = data["event"]?.toString() ?: return
            val callId = data["callId"]?.toString() ?: return
            Napier.d(tag = TAG, message = "Call signal received: event=$event callId=$callId")
            when (event) {
                "Incoming Video Call" -> {
                    val doctorId = data["doctorId"]?.toString() ?: return
                    val patientId = data["patientId"]?.toString() ?: return
                    IncomingCallHandler.onCallInvite(
                        callId = callId,
                        callerId = data["callerId"]?.toString() ?: return,
                        callerName = data["callerName"]?.toString(),
                        callerPicture = data["callerPicture"]?.toString()?.takeIf { it.isNotBlank() },
                        doctorId = doctorId,
                        patientId = patientId,
                        isVideo = data["isVideo"]?.toString()?.toBooleanStrictOrNull() ?: true,
                        ringTimeoutSeconds = data["ringTimeoutSeconds"]?.toString()?.toLongOrNull() ?: 45L,
                    )
                }
                "Call Answered" -> IncomingCallHandler.onCallAnswered(callId)
                "Call Declined" -> IncomingCallHandler.onCallDeclined(callId)
                "Call Cancelled" -> IncomingCallHandler.onCallCancelled(callId)
                "Incoming Doctor Call" -> {
                    val threadId = data["threadId"]?.toString() ?: return
                    IncomingCallHandler.onDoctorCallInvite(
                        callId = callId,
                        callerId = data["callerId"]?.toString() ?: return,
                        callerName = data["callerName"]?.toString(),
                        callerPicture = data["callerPicture"]?.toString()?.takeIf { it.isNotBlank() },
                        threadId = threadId,
                        isVideo = data["isVideo"]?.toString()?.toBooleanStrictOrNull() ?: true,
                        ringTimeoutSeconds = data["ringTimeoutSeconds"]?.toString()?.toLongOrNull() ?: 45L,
                    )
                }
                "Doctor Call Answered" -> IncomingCallHandler.onDoctorCallAnswered(callId)
                "Doctor Call Declined" -> IncomingCallHandler.onDoctorCallDeclined(callId)
                "Doctor Call Cancelled" -> IncomingCallHandler.onDoctorCallCancelled(callId)
                else -> Napier.w(tag = TAG, message = "onPayloadData: unrecognized event '$event' — no call-signal handler for it")
            }
        }

        override fun onNotificationClicked(data: Map<String, Any?>) {
            val event = data["event"]?.toString()
            val appointmentId = data["appointmentId"]?.toString()
            val ticketId = data["ticketId"]?.toString()
            val threadId = data["threadId"]?.toString()
            val patientId = data["patientId"]?.toString()
            val patientName = (data["patientName"] ?: data["senderName"])?.toString() ?: "Patient"
            // Doctor-chat threadId-shaped events carry no display name in metadata (only the push
            // title does, e.g. "New message from Dr. X") — fall back to a generic label rather than
            // mislabeling the other doctor as "Patient".
            val counterpartName = (data["patientName"] ?: data["callerName"] ?: data["senderName"])?.toString() ?: "Doctor"

            Napier.d(tag = TAG, message = "Notification tapped — event=$event appointmentId=$appointmentId threadId=$threadId patientId=$patientId ticketId=$ticketId")

            // Call/chat lifecycle events (Patient Joined the Call, Call Ended, New Chat Message for a a
            // consultation thread) never carry appointmentId on the backend — they carry patientId
            // directly. Resolving via a locally-cached appointment lookup is only a fallback for the
            // rare case patientId itself is missing; it must never be the gate that blocks routing.
            suspend fun resolvedPatientId(appointmentId: String?): String? = patientId
                ?: appointmentId?.let { id ->
                    component.appointmentLocalRepository.observeAppointments().first().firstOrNull { it.id == id }?.patientId
                }

            suspend fun toConversation(appointmentId: String?): NotificationEvent {
                val resolved = resolvedPatientId(appointmentId)
                return if (!resolved.isNullOrBlank()) NotificationEvent.ToConversation(resolved, patientName, appointmentId.orEmpty())
                else NotificationEvent.ToNotifications
            }

            suspend fun toCall(appointmentId: String?): NotificationEvent {
                val resolved = resolvedPatientId(appointmentId)
                return if (!resolved.isNullOrBlank()) NotificationEvent.ToCall(resolved, patientName, appointmentId.orEmpty())
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
                    "Patient Joined the Call" -> toCall(appointmentId)
                    // "Doctor Joined the Call" is only ever sent to a doctor from the doctor-chat
                    // module (threadId-shaped) — the consultation module's identically-named event
                    // only ever targets the PATIENT side.
                    "Doctor Joined the Call" -> {
                        if (!threadId.isNullOrBlank()) NotificationEvent.ToDoctorConversation(threadId, counterpartName)
                        else NotificationEvent.ToNotifications
                    }
                    // "Call Ended" is reused by both consultation (patientId-shaped) and doctor-chat
                    // (threadId-shaped) — disambiguate by which key is actually present rather than
                    // assuming a shape from the event name alone.
                    "Consultation Ended",
                    "Call Ended" -> {
                        if (!threadId.isNullOrBlank()) NotificationEvent.ToDoctorConversation(threadId, counterpartName)
                        else toConversation(appointmentId)
                    }
                    // Same ambiguity as above: doctor-chat (threadId), consultation (patientId), or
                    // support (ticketId) — check each payload shape rather than gating on appointmentId,
                    // which none of the three chat surfaces ever actually send.
                    "New Chat Message" -> when {
                        !threadId.isNullOrBlank() -> NotificationEvent.ToDoctorConversation(threadId, counterpartName)
                        patientId != null || !appointmentId.isNullOrBlank() -> toConversation(appointmentId)
                        !ticketId.isNullOrBlank() -> NotificationEvent.ToSupportTicket(ticketId)
                        else -> NotificationEvent.ToNotifications
                    }
                    "Support ticket status updated" -> {
                        if (!ticketId.isNullOrBlank()) NotificationEvent.ToSupportTicket(ticketId)
                        else NotificationEvent.ToNotifications
                    }
                    "Article Suspended",
                    "Article Deleted" -> NotificationEvent.ToArticles
                    // Referral status changes have no dedicated "my sent referrals" screen in this app
                    // yet — route to Notifications deliberately (not a bug) rather than fabricating a
                    // destination that doesn't exist.
                    "Referral Accepted",
                    "Referral Declined" -> NotificationEvent.ToNotifications
                    null -> NotificationEvent.ToNotifications
                    else -> {
                        Napier.w(tag = TAG, message = "onNotificationClicked: unrecognized event '$event' — falling back to Notifications")
                        NotificationEvent.ToNotifications
                    }
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
