package ke.co.smartroundclinic.doctor.core.pdf

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.multiplatform.webview.web.LoadingState
import com.multiplatform.webview.web.WebView
import com.multiplatform.webview.web.rememberWebViewState

@Composable
fun PdfViewer(url: String, modifier: Modifier = Modifier) {
    val viewerUrl = buildPdfViewerUrl(url)
    val state = rememberWebViewState(viewerUrl)

    // JavaScript must be enabled for the Google Docs viewer to render on Android
    state.webSettings.isJavaScriptEnabled = true

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(Color.White),
    ) {
        WebView(
            state = state,
            modifier = Modifier.fillMaxSize(),
        )

        when (val loadingState = state.loadingState) {
            is LoadingState.Initializing ->
                CircularProgressIndicator(
                    modifier = Modifier.size(40.dp).align(Alignment.Center),
                )

            is LoadingState.Loading ->
                LinearProgressIndicator(
                    progress = { loadingState.progress },
                    modifier = Modifier.fillMaxWidth().align(Alignment.TopCenter),
                )

            is LoadingState.Finished -> Unit
        }
    }
}
