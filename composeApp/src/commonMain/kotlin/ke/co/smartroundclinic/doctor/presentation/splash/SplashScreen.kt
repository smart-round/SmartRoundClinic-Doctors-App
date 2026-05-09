package ke.co.smartroundclinic.doctor.presentation.splash


import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.ImageShader
import androidx.compose.ui.layout.ContentScale
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import ke.co.smartroundclinic.doctor.common.Constants
import ke.co.smartroundclinic.doctor.domain.usecase.datastore.ObserveKeyUseCase
import ke.co.smartroundclinic.doctor.presentation.theme.SmartRoundTheme
import ke.co.smartroundclinic.doctor.presentation.theme.smartRoundColors
import kotlinx.coroutines.delay
import org.jetbrains.compose.resources.painterResource
import org.koin.compose.koinInject
import smartroundclinic.composeapp.generated.resources.Res
import smartroundclinic.composeapp.generated.resources.background_pattern
import smartroundclinic.composeapp.generated.resources.splashscreen_logo
import kotlin.time.Duration.Companion.seconds


@Composable
fun SplashScreen(
    onNavigateToNext: () -> Unit = {},
    onSkipOnBoarding: () -> Unit = {}
) {

    val observe = koinInject<ObserveKeyUseCase>()
    val onBoardingStatus = observe(Constants.ONBOARDING_KEY).collectAsStateWithLifecycle(null)

    LaunchedEffect(Unit) {
        delay(1.seconds.inWholeMilliseconds)
        if (onBoardingStatus.value?.toBooleanStrictOrNull() == true) onSkipOnBoarding()
            else onNavigateToNext()
    }

    Box(
        contentAlignment = Alignment.Center,
        modifier = Modifier.fillMaxSize()
            .background(
                brush = Brush.verticalGradient(
                    colors = listOf(
                        MaterialTheme.smartRoundColors.gradientStart,
                        MaterialTheme.smartRoundColors.gradientEnd,
                    )
                )
            )

    ) {
        Image(
            painter = painterResource(Res.drawable.background_pattern),
            contentDescription = "Background pattern",
            modifier = Modifier.fillMaxSize()
        )

        Image(
            painter = painterResource(Res.drawable.splashscreen_logo),
            contentDescription = "Splash screen logo",
            contentScale = ContentScale.Crop
        )

    }

}