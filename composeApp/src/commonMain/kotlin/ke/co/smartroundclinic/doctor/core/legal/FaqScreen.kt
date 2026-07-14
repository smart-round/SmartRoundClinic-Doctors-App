package ke.co.smartroundclinic.doctor.core.legal

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import ke.co.smartroundclinic.doctor.common.Constants.FAQ_URL

@Composable
fun FaqScreen(
    onBack: () -> Unit,
    modifier: Modifier = Modifier,
) {
    WebPageScreen(title = "FAQs", url = FAQ_URL, onBack = onBack, modifier = modifier)
}
