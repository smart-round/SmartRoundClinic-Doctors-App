import SwiftUI
import ComposeApp
import FirebaseCore
import FirebaseMessaging
import UserNotifications
import RealtimeKit
import RTKWebRTC

class AppDelegate: NSObject, UIApplicationDelegate {

    func application(
        _ application: UIApplication,
        didFinishLaunchingWithOptions launchOptions: [UIApplication.LaunchOptionsKey: Any]? = nil
    ) -> Bool {
        // Do NOT set UNUserNotificationCenter.current().delegate here.
        // NotifierManager.initialize() sets itself as the UNUserNotificationCenterDelegate
        // so it can fire onNotificationClicked and show foreground banners.
        // Overriding that delegate here would break kmpnotifier's handling.
        NSLog("SRC-AUDIO: CANARY-20260730 app didFinishLaunchingWithOptions, backgroundStatus=\(application.backgroundRefreshStatus.rawValue)")
        application.registerForRemoteNotifications()
        CallKitManager.shared.start()
        return true
    }

    // Forward APNS device token to Firebase so it can exchange it for an FCM token.
    // kmpnotifier's internal MessagingDelegate picks this up and fires onNewToken.
    func application(
        _ application: UIApplication,
        didRegisterForRemoteNotificationsWithDeviceToken deviceToken: Foundation.Data
    ) {
        Messaging.messaging().apnsToken = deviceToken
    }

    // Required when FirebaseAppDelegateProxyEnabled = NO — pass the raw remote
    // notification to Firebase so it can update FCM state (ack, analytics, etc.)
    func application(
        _ application: UIApplication,
        didReceiveRemoteNotification userInfo: [AnyHashable: Any],
        fetchCompletionHandler completionHandler: @escaping (UIBackgroundFetchResult) -> Void
    ) {
        Messaging.messaging().appDidReceiveMessage(userInfo)
        completionHandler(.newData)
    }
}

@main
struct iOSApp: App {
    @UIApplicationDelegateAdaptor(AppDelegate.self) var appDelegate

    init() {
        FirebaseApp.configure()
        NotifierManager.shared.initialize(
            configuration: NotificationPlatformConfigurationIos(
                showPushNotification: true,
                askNotificationPermissionOnStart: true,
                notificationSoundName: nil
            )
        )
        MainViewControllerKt.doInitKoin()
        wireRtkCallBridge()
        wireCallKitBridge()
    }

    var body: some Scene {
        WindowGroup {
            ContentView()
        }
    }

    private func wireRtkCallBridge() {
        RtkCallBridge.shared.factory = { authToken, enableAudio, enableVideo, listener in
            RtkCallSessionImpl(
                authToken: authToken,
                enableAudio: enableAudio.boolValue,
                enableVideo: enableVideo.boolValue,
                listener: listener
            )
        }
    }

    private func wireCallKitBridge() {
        CallKitBridge.shared.onIncomingCall = { callId, callerName, isVideo in
            CallKitManager.shared.reportIncomingCall(callId: callId, callerName: callerName, isVideo: isVideo.boolValue)
        }
        CallKitBridge.shared.onIncomingDoctorCall = { callId, callerName, isVideo, threadId in
            CallKitManager.shared.reportIncomingCall(callId: callId, callerName: callerName, isVideo: isVideo.boolValue, threadId: threadId)
        }
        CallKitBridge.shared.onEndCall = { callId in
            CallKitManager.shared.endCall(callId: callId)
        }
        CallKitBridge.shared.onEndActiveCall = {
            CallKitManager.shared.endActiveCall()
        }
    }
}

/// Swift-side handle over one live RealtimeKit `RealtimeKitClient`, driven by
/// [ke.co.smartroundclinic.doctor.presentation.main.chat.call.RtkCallController] via the
/// `IosCallSession`/`IosCallSessionListener` Kotlin interfaces. Mirrors the Android actual's
/// init→auto-joinRoom chain and event forwarding, since Kotlin has no direct visibility into
/// RealtimeKit on iOS (it's linked only into this app target via SPM, not cinterop'd into the
/// shared Kotlin framework).
private final class RtkCallSessionImpl: NSObject, RtkMeetingRoomEventListener, RtkSelfEventListener, RtkParticipantsEventListener, IosCallSession {
    private let client: RealtimeKitClient
    private let listener: IosCallSessionListener
    // Stashed so toggleAudio() can re-arm RTCAudioSession with the right mode (see toggleAudio()
    // below) — the mic/speaker button doubles as a manual "retry audio" affordance for the case
    // where automatic activation on CallKit answer silently failed to start the AURemoteIO unit.
    private let isVideoCall: Bool

    init(authToken: String, enableAudio: Bool, enableVideo: Bool, listener: IosCallSessionListener) {
        client = RealtimeKitiOSClientBuilder().build()
        self.listener = listener
        self.isVideoCall = enableVideo
        super.init()

        // Report outgoing patient-doctor calls to CallKit too (previously only incoming calls
        // did) — this gets them the system's native "return to call" background pill, and hands
        // RTCAudioSession activation to CallKitManager's didActivate/didDeactivate instead of
        // driving it manually here, so both directions go through the same audio-routing path.
        // Incoming calls are answered via CXAnswerCallAction first, so CallKitManager.shared's
        // didActivate (CallKitManager.swift) already owns that job for those by the time this
        // runs — skip reporting here to avoid double-reporting the same call.
        NSLog("SRC-AUDIO: RtkCallSessionImpl.init enableAudio=\(enableAudio) isCallKitCallActive=\(CallKitManager.shared.isCallKitCallActive)")
        if enableAudio && !CallKitManager.shared.isCallKitCallActive {
            CallKitManager.shared.reportOutgoingCall(isVideo: enableVideo, calleeName: nil)
        }

        client.addMeetingRoomEventListener(meetingRoomEventListener: self)
        client.addSelfEventListener(selfEventListener: self)
        client.addParticipantsEventListener(participantsEventListener: self)

        let meetingInfo = RtkMeetingInfo(authToken: authToken, enableAudio: enableAudio, enableVideo: enableVideo)
        NSLog("SRC-AUDIO: calling client.doInit now")
        client.doInit(meetingInfo: meetingInfo, onSuccess: {}, onFailure: { [weak self] error in
            self?.listener.onFailed(message: error.message)
        })
    }

    // MARK: - IosCallSession (called from Kotlin)

    func leaveRoom() {
        client.leaveRoom(onSuccess: {}, onFailure: { [weak self] _ in self?.listener.onEnded() })
    }

    func toggleAudio() {
        if client.localUser.audioEnabled {
            client.localUser.disableAudio(onResult: { _ in })
        } else {
            client.localUser.enableAudio(onResult: { _ in })
        }
        // Manual recovery path: if the CallKit-triggered activation on answer silently failed to
        // start the AURemoteIO unit (both directions end up silent, no error anywhere — see
        // CallKitManager.swift's configureAndActivateWebRTCAudioSession), re-running it here gives
        // the user a way to retry by tapping the mic button, instead of being stuck for the call's
        // whole duration with no in-app affordance to recover.
        NSLog("SRC-AUDIO: toggleAudio tapped, forcing re-arm")
        configureAndActivateWebRTCAudioSession(isVideo: isVideoCall)
    }

    func toggleVideo() {
        if client.localUser.videoEnabled {
            client.localUser.disableVideo(onResult: { _ in })
        } else {
            client.localUser.enableVideo(onResult: { _ in })
        }
    }

    func switchCamera() {
        client.localUser.switchCamera()
    }

    func dispose() {
        client.removeMeetingRoomEventListener(meetingRoomEventListener: self)
        client.removeSelfEventListener(selfEventListener: self)
        client.removeParticipantsEventListener(participantsEventListener: self)
        client.release(onSuccess: {}, onFailure: { _ in })
        // RTCAudioSession activation for both call directions now goes through CallKit's
        // didActivate/didDeactivate (see init() above) — no manual deactivate needed here.
        CallPictureInPictureManager.shared.stop()
    }

    // RealtimeKit hands back the remote participant's video view late (often not yet at
    // room-join — see PlatformVideoView.ios.kt's own note on this), so this is called
    // opportunistically from several event points below; CallPictureInPictureManager.start()
    // no-ops once already armed.
    private func armPictureInPictureIfPossible() {
        guard isVideoCall else { return }
        guard let videoView = client.participants.joined.first?.getVideoView() else {
            NSLog("SRC-PIP: armPictureInPictureIfPossible — no remote video view yet")
            return
        }
        CallPictureInPictureManager.shared.start(remoteVideoView: videoView)
    }

    func localVideoView() -> UIView? {
        client.localUser.getSelfPreview()
    }

    func remoteVideoView() -> UIView? {
        client.participants.joined.first?.getVideoView()
    }

    // MARK: - RtkConnectionEventListener (required by RtkMeetingRoomEventListener)

    func onMediaConnectionUpdate(update: MediaConnectionUpdate) {}
    func onSocketConnectionUpdate(newState: SocketConnectionState) {}

    // MARK: - RtkMeetingRoomEventListener

    func onMeetingInitStarted() {}

    func onMeetingInitCompleted(meeting: RealtimeKitClient) {
        meeting.joinRoom(onSuccess: {}, onFailure: { [weak self] error in
            self?.listener.onFailed(message: error.message)
        })
    }

    func onMeetingInitFailed(error: MeetingError) {
        listener.onFailed(message: error.message)
    }

    func onMeetingRoomJoinStarted() {}

    func onMeetingRoomJoinCompleted(meeting: RealtimeKitClient) {
        NSLog("SRC-AUDIO: onMeetingRoomJoinCompleted localAudioEnabled=\(meeting.localUser.audioEnabled) rtcAudioSessionEnabled=\(RTKRTCAudioSession.sharedInstance().isAudioEnabled)")
        // No-op if this call was never reported as outgoing (i.e. it's the answered/incoming
        // side, already tracked via CallKitManager's own answer flow).
        CallKitManager.shared.reportOutgoingCallConnected()
        listener.onConnected()
        listener.onAudioUpdate(enabled: meeting.localUser.audioEnabled)
        listener.onVideoUpdate(enabled: meeting.localUser.videoEnabled)
        if let participant = meeting.participants.joined.first {
            listener.onRemoteParticipantUpdate(
                name: participant.name,
                audioEnabled: participant.audioEnabled,
                videoEnabled: participant.videoEnabled
            )
        }
        armPictureInPictureIfPossible()
    }

    func onMeetingRoomJoinFailed(error: MeetingError) {
        listener.onFailed(message: error.message)
    }

    func onMeetingRoomLeaveStarted() {}

    func onMeetingRoomLeaveCompleted() {
        listener.onEnded()
    }

    func onMeetingEnded() {
        listener.onEnded()
    }

    func onActiveTabUpdate(meeting: RealtimeKitClient, activeTab: ActiveTab) {}

    // MARK: - RtkSelfEventListener

    func onMeetingRoomJoinedWithoutCameraPermission() {}
    func onMeetingRoomJoinedWithoutMicPermission() {}

    func onAudioUpdate(isEnabled: Bool) {
        listener.onAudioUpdate(enabled: isEnabled)
    }

    func onVideoUpdate(isEnabled: Bool) {
        listener.onVideoUpdate(enabled: isEnabled)
    }

    func onScreenShareUpdate(isEnabled: Bool) {}
    func onPinned() {}
    func onUnpinned() {}
    func onAudioDevicesUpdated(devices: [AudioDevice]) {}
    func onAudioDeviceChanged(audioDevice: AudioDevice) {}
    func onVideoDeviceChanged(videoDevice: VideoDevice) {}
    func onWaitListStatusUpdate(waitListStatus: WaitListStatus) {}
    func onUpdate(participant: RtkSelfParticipant) {}

    func onRemovedFromMeeting() {
        listener.onEnded()
    }

    func onScreenShareStartFailed(reason: String) {}
    func onPermissionsUpdated(permission: SelfPermissions) {}

    // MARK: - RtkParticipantsEventListener

    func onParticipantJoin(participant: RtkRemoteParticipant) {
        listener.onRemoteParticipantUpdate(
            name: participant.name,
            audioEnabled: participant.audioEnabled,
            videoEnabled: participant.videoEnabled
        )
        armPictureInPictureIfPossible()
    }

    func onParticipantLeave(participant: RtkRemoteParticipant) {
        listener.onRemoteParticipantUpdate(name: nil, audioEnabled: false, videoEnabled: false)
    }

    func onAudioUpdate(participant: RtkRemoteParticipant, isEnabled: Bool) {
        listener.onRemoteParticipantUpdate(name: participant.name, audioEnabled: isEnabled, videoEnabled: participant.videoEnabled)
    }

    func onVideoUpdate(participant: RtkRemoteParticipant, isEnabled: Bool) {
        listener.onRemoteParticipantUpdate(name: participant.name, audioEnabled: participant.audioEnabled, videoEnabled: isEnabled)
        armPictureInPictureIfPossible()
    }

    func onScreenShareUpdate(participant: RtkRemoteParticipant, isEnabled: Bool) {}
    func onParticipantPinned(participant: RtkRemoteParticipant) {}
    func onParticipantUnpinned(participant: RtkRemoteParticipant) {}
    func onActiveParticipantsChanged(active: [RtkRemoteParticipant]) {}
    func onActiveSpeakerChanged(participant: RtkRemoteParticipant?) {}
    func onAllParticipantsUpdated(allParticipants: [RtkParticipant]) {}
    func onNewBroadcastMessage(type: String, payload: [String: Any]) {}
    func onUpdate(participants: RtkParticipants) {}
}
