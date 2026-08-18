package ke.co.smartroundclinic.doctor.core.notification

import ke.co.smartroundclinic.doctor.domain.model.IncomingDoctorCall
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

/**
 * App-wide observable ringing state for doctor-to-doctor calls — mirrors [IncomingCallState],
 * kept as a separate singleton (rather than reusing IncomingCallState with a nullable threadId)
 * so a doctor-to-doctor invite arriving can never collide with or clear a genuine patient-call
 * ring in progress, and vice versa.
 */
object IncomingDoctorCallState {
    private val _current = MutableStateFlow<IncomingDoctorCall?>(null)
    val current: StateFlow<IncomingDoctorCall?> = _current

    fun ring(call: IncomingDoctorCall) {
        _current.value = call
    }

    /** No-ops if a newer call has already replaced this one — avoids racing a stale clear. */
    fun clear(callId: String) {
        if (_current.value?.callId == callId) _current.value = null
    }

    fun clearAll() {
        _current.value = null
    }
}
