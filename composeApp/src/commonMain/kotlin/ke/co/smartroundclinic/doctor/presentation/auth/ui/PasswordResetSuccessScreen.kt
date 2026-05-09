package ke.co.smartroundclinic.doctor.presentation.auth.ui

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import ke.co.smartroundclinic.doctor.presentation.theme.SmartRoundTheme
import ke.co.smartroundclinic.doctor.presentation.theme.smartRoundColors
import org.jetbrains.compose.resources.painterResource
import androidx.compose.ui.tooling.preview.Preview
import smartroundclinic.composeapp.generated.resources.Res
import smartroundclinic.composeapp.generated.resources.background_pattern
import smartroundclinic.composeapp.generated.resources.password_reset_success

@Composable
fun PasswordResetSuccessScreen(
    onNavigateToLoginScreen: () -> Unit
) {
    var isVisible by remember { mutableStateOf(false) }
    
    // Initial drop-in animation
    val offsetY = remember { Animatable(-200f) }
    val imageScale = remember { Animatable(0.5f) }
    val rotation = remember { Animatable(0f) }

    // Subtle floating loop for after the drop
    val infiniteTransition = rememberInfiniteTransition(label = "floating")
    val floatingOffset by infiniteTransition.animateFloat(
        initialValue = -5f,
        targetValue = 5f,
        animationSpec = infiniteRepeatable(
            animation = tween(2000),
            repeatMode = RepeatMode.Reverse
        ),
        label = "floatingOffset"
    )

    LaunchedEffect(Unit) {
        isVisible = true
        // Step 1: Drop from top and scale up with a heavy bounce
        offsetY.animateTo(
            targetValue = 0f,
            animationSpec = spring(
                dampingRatio = 0.45f, // More bouncy
                stiffness = Spring.StiffnessLow
            )
        )
        // Step 2: Add a little "yay" wobble/rotation after landing
        rotation.animateTo(
            targetValue = 10f,
            animationSpec = spring(Spring.DampingRatioHighBouncy)
        )
        rotation.animateTo(
            targetValue = 0f,
            animationSpec = spring(Spring.DampingRatioMediumBouncy)
        )
    }

    LaunchedEffect(Unit) {
        imageScale.animateTo(
            targetValue = 1f,
            animationSpec = spring(
                dampingRatio = Spring.DampingRatioLowBouncy,
                stiffness = Spring.StiffnessVeryLow
            )
        )
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
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
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.Crop // Crop ensures full coverage edge-to-edge
        )

        AnimatedVisibility(
            visible = isVisible,
            enter = fadeIn(animationSpec = tween(1000)),
            modifier = Modifier.fillMaxSize()
        ) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(WindowInsets.safeDrawing.asPaddingValues()), // Protect content from system bars
                contentAlignment = Alignment.Center
            ) {
                Column(
                    modifier = Modifier
                        .wrapContentSize()
                        .padding(32.dp)
                        .background(shape = MaterialTheme.shapes.medium, color = MaterialTheme.colorScheme.onPrimary),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = androidx.compose.foundation.layout.Arrangement.Center
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(200.dp)
                            .padding(top = 16.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Image(
                            painter = painterResource(Res.drawable.password_reset_success),
                            contentDescription = "Success Illustration",
                            contentScale = ContentScale.Fit,
                            modifier = Modifier
                                .graphicsLayer {
                                    translationY = offsetY.value + floatingOffset
                                    scaleX = imageScale.value
                                    scaleY = imageScale.value
                                    rotationZ = rotation.value
                                }
                        )
                    }

                    Text(
                        text = "Password Changed!",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier
                            .padding(top = 16.dp, bottom = 16.dp)
                    )
                    Text(
                        text = "You have successfully created a new password. You can now sign in with your new credentials",
                        style = MaterialTheme.typography.bodyMedium,
                        maxLines = 3,
                        textAlign = TextAlign.Center,
                        modifier = Modifier
                            .fillMaxWidth(0.9f)
                            .padding(bottom = 16.dp)
                    )

                    Button(
                        colors = ButtonDefaults.buttonColors(
                            containerColor = MaterialTheme.smartRoundColors.statusPublished,
                            contentColor = MaterialTheme.colorScheme.onPrimary
                        ),
                        shape = MaterialTheme.shapes.medium,
                        onClick = onNavigateToLoginScreen,
                        modifier = Modifier.fillMaxWidth()
                            .padding(horizontal = 16.dp, vertical = 24.dp)
                    ) {
                        Text(
                            text = "Sign In",
                            style = MaterialTheme.typography.labelLarge
                        )
                    }
                }
            }
        }
    }
}

@Preview
@Composable
fun PasswordResetSuccessScreenPreview() {
    SmartRoundTheme {
        PasswordResetSuccessScreen {

        }
    }
}
