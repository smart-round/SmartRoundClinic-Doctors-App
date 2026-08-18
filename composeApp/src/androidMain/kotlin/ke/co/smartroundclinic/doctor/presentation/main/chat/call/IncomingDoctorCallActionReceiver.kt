package ke.co.smartroundclinic.doctor.presentation.main.chat.call

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import androidx.core.app.NotificationManagerCompat
import ke.co.smartroundclinic.doctor.core.notification.CallActionDispatcher
import ke.co.smartroundclinic.doctor.core.notification.IncomingDoctorCallState

private const val DOCTOR_CALL_NOTIFICATION_ID = 9002

/** Doctor-to-doctor near-duplicate of [IncomingCallActionReceiver] — see its doc comment. */
class IncomingDoctorCallActionReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        val callId = intent.getStringExtra(EXTRA_CALL_ID) ?: return
        val threadId = intent.getStringExtra(EXTRA_THREAD_ID) ?: return

        NotificationManagerCompat.from(context).cancel(DOCTOR_CALL_NOTIFICATION_ID)
        IncomingDoctorCallState.clear(callId)
        CallActionDispatcher.declineDoctorCall(threadId, callId)
    }

    companion object {
        const val ACTION_DECLINE = "ke.co.smartroundclinic.doctor.action.DECLINE_DOCTOR_CALL"
        const val EXTRA_CALL_ID = "callId"
        const val EXTRA_THREAD_ID = "threadId"
    }
}
