@file:OptIn(kotlinx.cinterop.ExperimentalForeignApi::class)

package ke.co.smartroundclinic.doctor.presentation.main.chat.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import platform.UIKit.NSTextAlignmentCenter
import platform.UIKit.UIApplication
import platform.UIKit.UIColor
import platform.UIKit.UILabel
import platform.UIKit.UIScreen
import platform.UIKit.UIView
import platform.UIKit.UIViewController

/**
 * Kotlin↔Swift bridge for the RealtimeKitUI Swift Package.
 *
 * Swift code (`iosApp/iosApp/iOSApp.swift`) sets [factory] once at app startup;
 * the Compose [RealtimeMeetingView] asks the factory for a UIViewController
 * whenever a call is joined.
 *
 * Set it from Swift like:
 * ```swift
 * import RealtimeKitCore
 * import RealtimeKitUI
 * import ComposeApp
 *
 * RealtimeMeetingBridge.shared.factory = { authToken, enableVideo, onLeave in
 *     let rtkUI = RealtimeKitUI(
 *         meetingInfo: RtkMeetingInfo(
 *             authToken: authToken,
 *             enableAudio: true,
 *             enableVideo: enableVideo.boolValue
 *         )
 *     )
 *     return rtkUI.startMeeting { onLeave() }
 * }
 * ```
 */
object RealtimeMeetingBridge {
    var factory: ((authToken: String, enableVideo: Boolean, onLeave: () -> Unit) -> UIViewController)? = null
}

@Composable
actual fun RealtimeMeetingView(
    authToken: String,
    enableVideo: Boolean,
    onLeave: () -> Unit,
    modifier: Modifier,
) {
    val meetingVC = remember(authToken) {
        RealtimeMeetingBridge.factory?.invoke(authToken, enableVideo, onLeave)
            ?: makeStubViewController(authToken, enableVideo)
    }

    DisposableEffect(authToken) {
        var topVC = UIApplication.sharedApplication.keyWindow?.rootViewController
        while (topVC?.presentedViewController != null) {
            topVC = topVC?.presentedViewController
        }
        topVC?.presentViewController(meetingVC, animated = true, completion = null)

        onDispose {
            UIApplication.sharedApplication.keyWindow?.rootViewController
                ?.dismissViewControllerAnimated(true, completion = null)
        }
    }

    Box(modifier = modifier.fillMaxSize().background(Color.Black))
}

private fun makeStubViewController(authToken: String, enableVideo: Boolean): UIViewController {
    val vc = UIViewController()
    vc.view.backgroundColor = UIColor.blackColor
    val label = UILabel(frame = UIScreen.mainScreen.bounds).apply {
        text = "RealtimeKitUI bridge not set.\nSee iOSApp.swift\nvideo=$enableVideo\ntoken=${authToken.take(16)}…"
        numberOfLines = 0
        textColor = UIColor.whiteColor
        textAlignment = NSTextAlignmentCenter
    }
    vc.view.addSubview(label as UIView)
    return vc
}
