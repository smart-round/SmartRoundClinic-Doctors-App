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
import smartroundclinic.composeapp.generated.resources.female_young_doctor
import smartroundclinic.composeapp.generated.resources.male_young_doctor
import smartroundclinic.composeapp.generated.resources.old_male_doctor
import smartroundclinic.composeapp.generated.resources.onboarding_screen_bg

@Composable
fun ThirdOnboardingScreen(
    onNavigateToNext:() -> Unit,
    onSkipToNext:() -> Unit
){
    Box(
        modifier = Modifier.fillMaxSize()
            .background(color = MaterialTheme.colorScheme.background)
    ){
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
                    text = "Smarter Income Made Simple",
                    style = MaterialTheme.typography.headlineLarge,
                    maxLines = 2,
                    textAlign = TextAlign.Center,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 16.dp)
                )
                Text(
                    text = "Track earnings effortlessly, receive timely payouts, and gain full visibility into your revenue.",
                    maxLines = 3,
                    textAlign = TextAlign.Center,
                    style = MaterialTheme.typography.bodySmall,
                    modifier = Modifier
                        .fillMaxWidth()
                )
            }
            Image(
                painter = painterResource(Res.drawable.old_male_doctor),
                contentDescription = "old male doctor as background",
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
                        text = "Get Started",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onPrimary
                    )
                }
            }
        }

    }
}