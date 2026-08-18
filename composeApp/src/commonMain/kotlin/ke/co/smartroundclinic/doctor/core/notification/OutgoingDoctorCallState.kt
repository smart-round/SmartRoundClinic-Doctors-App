package ke.co.smartroundclinic.doctor.core.notification

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

/** Observable state for a doctor-to-doctor call *this device* just placed — mirrors [OutgoingCallState],
 * kept separate so it can never be clobbered by (or clobber) a concurrent patient call's state. */
sealed class OutgoingDoctorCallStatus {
    data class Calling(val callId: String, val threadId: String, val calleeName: String?, val isVideo: Boolean) : OutgoingDoctorCallStatus()
    data class Answered(val callId: String) : OutgoingDoctorCallStatus()
    data class Declined(val callId: String) : OutgoingDoctorCallStatus()
}

object OutgoingDoctorCallState {
    private val _current = MutableStateFlow<OutgoingDoctorCallStatus?>(null)
    val current: StateFlow<OutgoingDoctorCallStatus?> = _current

    fun calling(callId: String, threadId: String, calleeName: String?, isVideo: Boolean) {
        _current.value = OutgoingDoctorCallStatus.Calling(callId, threadId, calleeName, isVideo)
    }

    /** No-ops if a newer/different call has already replaced this one. */
    fun answered(callId: String) {
        if (currentCallId() == callId) _current.value = OutgoingDoctorCallStatus.Answered(callId)
    }

    fun declined(callId: String) {
        if (currentCallId() == callId) _current.value = OutgoingDoctorCallStatus.Declined(callId)
    }

    fun clear() {
        _current.value = null
    }

    private fun currentCallId(): String? = when (val status = _current.value) {
        is OutgoingDoctorCallStatus.Calling -> status.callId
        is OutgoingDoctorCallStatus.Answered -> status.callId
        is OutgoingDoctorCallStatus.Declined -> status.callId
        null -> null
    }
}
