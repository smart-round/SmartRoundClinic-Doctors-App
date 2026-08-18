package ke.co.smartroundclinic.doctor.core.notification

/**
 * Kotlin -> Swift bridge for reporting/ending calls via CallKit (same shape as
 * `RtkCallBridge` for RealtimeKit) — Swift sets these once at app startup, see
 * `iOSApp.swift`'s `wireCallKitBridge()`. If unset (e.g. VoIP push not configured yet),
 * [IncomingCallHandler] falls back to just updating [IncomingCallState].
 */
object CallKitBridge {
    var onIncomingCall: ((callId: String, callerName: String?, isVideo: Boolean) -> Unit)? = null
    var onEndCall: ((callId: String) -> Unit)? = null

    // Doctor-to-doctor equivalent of onIncomingCall — a separate callback (rather than a nullable
    // threadId param on the one above) so Swift can tell at the call site which kind it's
    // reporting, without needing its own internal discriminator just to pick a signature. Both
    // still funnel into the same single CXProvider (see CallKitManager.swift) — iOS only supports
    // one, unlike Android where doctor calls get their own Activity/notification id.
    var onIncomingDoctorCall: ((callId: String, callerName: String?, isVideo: Boolean, threadId: String) -> Unit)? = null

    /** Ends whichever call CallKit currently has active (answered), if any — see
     * CallKitManager.endActiveCall(). Invoked via [ActiveCallNotifier] once the in-app Call
     * screen ends, so CallKit's system call state (status bar indicator, Recents, Dynamic
     * Island) doesn't linger after our own UI has already moved on. */
    var onEndActiveCall: (() -> Unit)? = null
}
