package ke.co.smartroundclinic.doctor.presentation.onboarding

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import ke.co.smartroundclinic.doctor.presentation.common.composables.PrimaryButton
import org.jetbrains.compose.resources.painterResource
import smartroundclinic.composeapp.generated.resources.Res
import smartroundclinic.composeapp.generated.resources.male_young_doctor
import smartroundclinic.composeapp.generated.resources.onboarding_screen_bg

@Composable
fun FirstOnboardingScreen(
    onNavigateToNext: () -> Unit,
    onSkipToNext: () -> Unit
) {
    Box(
        modifier = Modifier.fillMaxSize()
            .background(color = MaterialTheme.colorScheme.background)
    ) {
        Image(
            painter = painterResource(Res.drawable.onboarding_screen_bg),
            contentDescription = "onboarding screen background",
            contentScale = ContentScale.FillWidth,
            modifier = Modifier
                .fillMaxWidth()
                .align(Alignment.Center)
        )
        Column(
            modifier = Modifier.align(Alignment.Center)
                .padding(horizontal = 32.dp)
        ) {
            Column(modifier = Modifier.padding(vertical = 16.dp)) {
                Text(
                    text = "Expand Your Practice Digitally",
                    style = MaterialTheme.typography.headlineLarge,
                    maxLines = 2,
                    textAlign = TextAlign.Center,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 16.dp)
                )
                Text(
                    text = "Reach more patients beyond your location and grow your consultation opportunities seamlessly.",
                    maxLines = 3,
                    textAlign = TextAlign.Center,
                    style = MaterialTheme.typography.bodySmall,
                    modifier = Modifier
                        .fillMaxWidth()
                )
            }
            Image(
                painter = painterResource(Res.drawable.male_young_doctor),
                contentDescription = "young male doctor as background",
                contentScale = ContentScale.FillWidth,
                modifier = Modifier
                    .fillMaxWidth()

            )
            Column{
                PrimaryButton(
                    onClick = onNavigateToNext,
                    shape = MaterialTheme.shapes.medium,
                    modifier = Modifier
                        .fillMaxWidth()
                ) {
                    Text(
                        text = "Next",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onPrimary
                    )
                }

                TextButton(
                    onClick = onSkipToNext,
                    colors = ButtonDefaults.textButtonColors(
                        containerColor = Color.Transparent,
                        contentColor = MaterialTheme.colorScheme.onBackground
                    ),
                    modifier = Modifier
                        .fillMaxWidth()
                ) {
                    Text(
                        text = "Skip",
                        style = MaterialTheme.typography.bodySmall
                    )
                }

            }
        }
    }
}