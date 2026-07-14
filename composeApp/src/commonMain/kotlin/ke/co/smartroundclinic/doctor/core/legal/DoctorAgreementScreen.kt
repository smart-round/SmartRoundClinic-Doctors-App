package ke.co.smartroundclinic.doctor.core.legal

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import ke.co.smartroundclinic.doctor.common.Constants.DOCTORS_AGREEMENT_URL

@Composable
fun DoctorAgreementScreen(
    onBack: () -> Unit,
    modifier: Modifier = Modifier,
) {
    WebPageScreen(title = "Doctor Agreement", url = DOCTORS_AGREEMENT_URL, onBack = onBack, modifier = modifier)
}
