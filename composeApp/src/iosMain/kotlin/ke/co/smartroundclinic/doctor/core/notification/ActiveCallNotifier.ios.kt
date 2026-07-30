package ke.co.smartroundclinic.doctor.core.notification

actual object ActiveCallNotifier {
    actual fun notifyCallEnded() {
        CallKitBridge.onEndActiveCall?.invoke()
    }
}
