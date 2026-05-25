import SwiftUI
import ComposeApp
import RealtimeKit
import RealtimeKitUI

/// Retained for the lifetime of an active meeting.
/// rtkUI owns the delegate relationship with RtkSetupViewController; if it
/// is released before the user taps "Join", the meeting room VC is never shown.
private var activeMeeting: RealtimeKitUI?

@main
struct iOSApp: App {
    init() {
        MainViewControllerKt.doInitKoin()
        wireRealtimeMeetingBridge()
    }

    var body: some Scene {
        WindowGroup {
            ContentView()
        }
    }

    private func wireRealtimeMeetingBridge() {
        RealtimeMeetingBridge.shared.factory = { authToken, enableVideo, onLeave in
            let rtkUI = RealtimeKitUI(
                meetingInfo: RtkMeetingInfo(
                    authToken: authToken,
                    enableAudio: true,
                    enableVideo: enableVideo.boolValue
                )
            )
            activeMeeting = rtkUI

            weak var setupVC: UIViewController?
            let vc = rtkUI.startMeeting {
                activeMeeting = nil
                if let presenter = setupVC?.presentingViewController {
                    presenter.dismiss(animated: true) { onLeave() }
                } else {
                    onLeave()
                }
            }
            setupVC = vc
            vc.modalPresentationStyle = .fullScreen
            vc.overrideUserInterfaceStyle = .dark
            return vc
        }
    }
}
