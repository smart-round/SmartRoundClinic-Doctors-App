package ke.co.smartroundclinic.doctor

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import ke.co.smartroundclinic.doctor.core.snackbar.SnackbarController
import ke.co.smartroundclinic.doctor.presentation.navigation.NavigationRoot
import ke.co.smartroundclinic.doctor.presentation.theme.SmartRoundTheme
import org.koin.compose.koinInject

@Composable
fun App() {
    SmartRoundTheme {
        val snackbarHostState = remember { SnackbarHostState() }
        val snackbarController: SnackbarController = koinInject()

        LaunchedEffect(snackbarController) {
            snackbarController.messages.collect { message ->
                snackbarHostState.showSnackbar(message)
            }
        }

        Box(modifier = Modifier.fillMaxSize()) {
            NavigationRoot()
            SnackbarHost(
                hostState = snackbarHostState,
                modifier = Modifier.align(Alignment.BottomCenter)
                    .systemBarsPadding(),
            )
        }
    }
}