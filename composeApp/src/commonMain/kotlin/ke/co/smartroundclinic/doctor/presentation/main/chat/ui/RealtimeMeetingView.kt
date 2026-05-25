package ke.co.smartroundclinic.doctor.presentation.main.chat.ui

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier

@Composable
expect fun RealtimeMeetingView(
    authToken: String,
    enableVideo: Boolean,
    onLeave: () -> Unit,
    modifier: Modifier = Modifier,
)
