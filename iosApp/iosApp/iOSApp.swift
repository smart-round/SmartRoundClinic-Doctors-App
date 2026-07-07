import SwiftUI
import ComposeApp
import FirebaseCore
import FirebaseMessaging
import UserNotifications
import RealtimeKit

class AppDelegate: NSObject, UIApplicationDelegate, UNUserNotificationCenterDelegate {

    func application(
        _ application: UIApplication,
        didFinishLaunchingWithOptions launchOptions: [UIApplication.LaunchOptionsKey: Any]? = nil
    ) -> Bool {
        UNUserNotificationCenter.current().delegate = self
        application.registerForRemoteNotifications()
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

    // Show banner + sound when a notification arrives while the app is in the foreground
    func userNotificationCenter(
        _ center: UNUserNotificationCenter,
        willPresent notification: UNNotification,
        withCompletionHandler completionHandler: @escaping (UNNotificationPresentationOptions) -> Void
    ) {
        completionHandler([.banner, .sound, .badge])
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

    init(authToken: String, enableAudio: Bool, enableVideo: Bool, listener: IosCallSessionListener) {
        client = RealtimeKitiOSClientBuilder().build()
        self.listener = listener
        super.init()

        client.addMeetingRoomEventListener(meetingRoomEventListener: self)
        client.addSelfEventListener(selfEventListener: self)
        client.addParticipantsEventListener(participantsEventListener: self)

        let meetingInfo = RtkMeetingInfo(authToken: authToken, enableAudio: enableAudio, enableVideo: enableVideo)
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
    }

    func onParticipantLeave(participant: RtkRemoteParticipant) {
        listener.onRemoteParticipantUpdate(name: nil, audioEnabled: false, videoEnabled: false)
    }

    func onAudioUpdate(participant: RtkRemoteParticipant, isEnabled: Bool) {
        listener.onRemoteParticipantUpdate(name: participant.name, audioEnabled: isEnabled, videoEnabled: participant.videoEnabled)
    }

    func onVideoUpdate(participant: RtkRemoteParticipant, isEnabled: Bool) {
        listener.onRemoteParticipantUpdate(name: participant.name, audioEnabled: participant.audioEnabled, videoEnabled: isEnabled)
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
