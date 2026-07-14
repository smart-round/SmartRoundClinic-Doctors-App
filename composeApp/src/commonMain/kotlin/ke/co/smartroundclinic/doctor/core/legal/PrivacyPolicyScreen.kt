package ke.co.smartroundclinic.doctor.core.legal

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import ke.co.smartroundclinic.doctor.common.Constants.PRIVACY_POLICY_URL

@Composable
fun PrivacyPolicyScreen(
    onBack: () -> Unit,
    modifier: Modifier = Modifier,
) {
    WebPageScreen(title = "Privacy Policy", url = PRIVACY_POLICY_URL, onBack = onBack, modifier = modifier)
}
