package ke.co.smartroundclinic.doctor.presentation.main.wallet

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.retain.retain
import androidx.compose.ui.Modifier
import androidx.navigation3.runtime.NavKey
import androidx.navigation3.runtime.entryProvider
import androidx.navigation3.ui.NavDisplay
import ke.co.smartroundclinic.doctor.presentation.main.wallet.destinations.WalletList
import ke.co.smartroundclinic.doctor.presentation.main.wallet.ui.WalletScreen
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun WalletRoot(
    modifier: Modifier = Modifier,
    onAtRootChanged: (Boolean) -> Unit = {},
    onProfileClick: () -> Unit = {},
    onNotificationsClick: () -> Unit = {},
) {
    val backStack = retain { mutableStateListOf<NavKey>(WalletList) }
    val isAtRoot = backStack.size == 1
    val viewModel: WalletViewModel = koinViewModel()

    SideEffect { onAtRootChanged(isAtRoot) }
    LaunchedEffect(Unit) { viewModel.load() }

    NavDisplay(
        modifier = modifier,
        backStack = backStack,
        onBack = { backStack.removeLastOrNull() },
        entryProvider = entryProvider {
            entry<WalletList> { WalletScreen(onProfileClick = onProfileClick, onNotificationsClick = onNotificationsClick) }
        },
    )
}
