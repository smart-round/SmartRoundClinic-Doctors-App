package ke.co.smartroundclinic.doctor.core.legal

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import ke.co.smartroundclinic.doctor.common.Constants.TERMS_CONDITIONS_URL

@Composable
fun TermsAndConditionsScreen(
    onBack: () -> Unit,
    modifier: Modifier = Modifier,
) {
    WebPageScreen(title = "Terms and Conditions", url = TERMS_CONDITIONS_URL, onBack = onBack, modifier = modifier)
}
