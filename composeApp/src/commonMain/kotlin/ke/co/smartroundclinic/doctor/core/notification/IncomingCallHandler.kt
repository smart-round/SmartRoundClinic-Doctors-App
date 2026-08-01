package ke.co.smartroundclinic.doctor.core.notification

/**
 * Platform reaction to a call-signaling push arriving while the app has no foreground UI open
 * to react to it directly (see IncomingCallState for the foreground/WebSocket path). Android
 * shows a full-screen incoming-call notification; iOS's reliable channel is native
 * CallKit/PushKit, which bypasses this Kotlin callback chain entirely for backgrounded/killed
 * apps — the iOS actual only covers the case where the app process happens to still be alive.
 *
 * The doctor-call methods below are the same shape as the patient-call ones (threadId in place
 * of doctorId/patientId) and are also called from ConsultationViewModel's doctor-chat counterpart
 * for the foreground WebSocket path — same as onCallInvite/onCallAnswered/etc. are the single
 * source of truth for patient calls regardless of channel, these are that for doctor calls.
 */
expect object IncomingCallHandler {
    fun onCallInvite(
        callId: String,
        callerId: String,
        callerName: String?,
        doctorId: String,
        patientId: String,
        isVideo: Boolean,
        ringTimeoutSeconds: Long,
    )
    fun onCallAnswered(callId: String)
    fun onCallDeclined(callId: String)
    fun onCallCancelled(callId: String)

    fun onDoctorCallInvite(
        callId: String,
        callerId: String,
        callerName: String?,
        threadId: String,
        isVideo: Boolean,
        ringTimeoutSeconds: Long,
    )
    fun onDoctorCallAnswered(callId: String)
    fun onDoctorCallDeclined(callId: String)
    fun onDoctorCallCancelled(callId: String)
}
