package ke.co.smartroundclinic.doctor.presentation.signup

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.retain.retain
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.navigation3.runtime.NavKey
import androidx.navigation3.runtime.entryProvider
import androidx.navigation3.ui.NavDisplay
import ke.co.smartroundclinic.doctor.presentation.signup.destinations.AccountVerification
import ke.co.smartroundclinic.doctor.presentation.signup.destinations.ApplicationUnderReview
import ke.co.smartroundclinic.doctor.presentation.signup.destinations.BankDetails
import ke.co.smartroundclinic.doctor.presentation.signup.destinations.SignUp
import ke.co.smartroundclinic.doctor.presentation.signup.destinations.SpecializationAndCompliance
import ke.co.smartroundclinic.doctor.presentation.signup.ui.AccountVerificationScreen
import ke.co.smartroundclinic.doctor.presentation.signup.ui.ApplicationUnderReviewScreen
import ke.co.smartroundclinic.doctor.presentation.signup.ui.BankDetailsScreen
import ke.co.smartroundclinic.doctor.presentation.signup.ui.SignUpScreen
import ke.co.smartroundclinic.doctor.presentation.signup.ui.SpecializationAndComplianceScreen
import ke.co.smartroundclinic.doctor.presentation.theme.smartRoundColors
import org.jetbrains.compose.resources.painterResource
import org.koin.compose.viewmodel.koinViewModel
import smartroundclinic.composeapp.generated.resources.Res
import smartroundclinic.composeapp.generated.resources.top_appbar_logo

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SignUpRoot(
    onSignIn: () -> Unit = {},
    modifier: Modifier = Modifier,
) {
    val filesViewModel: SignUpFilesViewModel = koinViewModel()
    val backStack = retain { mutableStateListOf<NavKey>(SignUp) }
    val steps = listOf(
        StepItem("1", "Personal Info"),
        StepItem("2", "Specialization & Compliance"),
        StepItem("3", "Bank Details"),
    )

    val currentDestination = backStack.lastOrNull()
    val currentStep = when (currentDestination) {
        is SpecializationAndCompliance -> 1
        is BankDetails -> 2
        else -> 0
    }
    val showProgress = currentDestination !is AccountVerification &&
            currentDestination !is ApplicationUnderReview

    fun navigateTo(destination: NavKey) = backStack.add(destination)
    fun navigateBack() = backStack.removeLastOrNull()

    Scaffold(
        modifier = modifier.fillMaxSize(),
        topBar = { SignUpTopBar() }
    ) { paddingValues ->
        Column(
            modifier = Modifier.fillMaxSize().padding(paddingValues)
        ) {
            SignUpHeader()
            if (showProgress) {
                SignUpProgress(steps = steps, currentStep = currentStep)
            }
            SignUpNavHost(
                backStack = backStack,
                filesViewModel = filesViewModel,
                onNavigateTo = ::navigateTo,
                onBack = ::navigateBack,
                onSignIn = onSignIn,
                modifier = modifier,
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun SignUpTopBar() {
    TopAppBar(
        modifier = Modifier.wrapContentHeight(),
        colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.Transparent),
        title = {
            Column(
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.fillMaxWidth()
            ) {
                Image(
                    painter = painterResource(Res.drawable.top_appbar_logo),
                    contentScale = ContentScale.Crop,
                    contentDescription = "Top App bar logo",
                )
            }
        }
    )
}

@Composable
private fun SignUpHeader() {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .wrapContentHeight()
            .background(
                brush = Brush.horizontalGradient(
                    listOf(
                        MaterialTheme.smartRoundColors.topAppBarGradientStart,
                        MaterialTheme.smartRoundColors.topAppBarGradientEnd,
                    )
                )
            )
    ) {
        Column(
            horizontalAlignment = Alignment.Start,
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 8.dp)
        ) {
            Text(
                text = "Get Started",
                color = MaterialTheme.colorScheme.onPrimary,
                style = MaterialTheme.typography.titleMedium,
                modifier = Modifier.padding(bottom = 4.dp)
            )
            Text(
                text = "Sign up to join our network",
                color = MaterialTheme.colorScheme.onPrimary,
                style = MaterialTheme.typography.bodySmall,
            )
        }
    }
}

@Composable
private fun SignUpNavHost(
    backStack: MutableList<NavKey>,
    filesViewModel: SignUpFilesViewModel,
    onNavigateTo: (NavKey) -> Unit,
    onBack: () -> Unit,
    onSignIn: () -> Unit,
    modifier: Modifier = Modifier,
) {
    NavDisplay(
        modifier = modifier.fillMaxSize()
            .background(color = MaterialTheme.colorScheme.background),
        backStack = backStack,
        onBack = onBack,
        entryProvider = entryProvider {
            entry<SignUp> {
                SignUpScreen(
                    filesViewModel = filesViewModel,
                    onNext = { personalInfo -> onNavigateTo(SpecializationAndCompliance(personalInfo)) },
                    onSignIn = onSignIn,
                )
            }
            entry<SpecializationAndCompliance> { dest ->
                SpecializationAndComplianceScreen(
                    filesViewModel = filesViewModel,
                    onNext = { specialization -> onNavigateTo(BankDetails(dest.personalInfo, specialization)) },
                    onBack = onBack,
                )
            }
            entry<BankDetails> { dest ->
                BankDetailsScreen(
                    filesViewModel = filesViewModel,
                    personalInfo = dest.personalInfo,
                    specializationData = dest.specialization,
                    onNext = { onNavigateTo(AccountVerification(dest.personalInfo.email)) },
                    onBack = onBack,
                )
            }
            entry<AccountVerification> { dest ->
                AccountVerificationScreen(
                    email = dest.email,
                    onVerify = onSignIn,
                    onBack = onBack,
                    onResendCode = {},
                )
            }
            entry<ApplicationUnderReview> {
                ApplicationUnderReviewScreen()
            }
        }
    )
}

private data class StepItem(val id: String, val label: String)

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
private fun SignUpProgress(
    steps: List<StepItem>,
    currentStep: Int,
    modifier: Modifier = Modifier,
    activeColor: Color = MaterialTheme.colorScheme.primary,
    inactiveColor: Color = MaterialTheme.colorScheme.outline,
    completedColor: Color = MaterialTheme.colorScheme.primary,
    labelActiveColor: Color = MaterialTheme.colorScheme.primary,
    labelInactiveColor: Color = MaterialTheme.colorScheme.outline,
    backgroundColor: Color = Color.Transparent,
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .background(backgroundColor)
            .padding(horizontal = 16.dp, vertical = 8.dp),
        verticalAlignment = Alignment.Top,
    ) {
        steps.forEachIndexed { index, step ->
            val isCompleted = index < currentStep
            val isActive = index == currentStep

            val circleColor by animateColorAsState(
                targetValue = when {
                    isCompleted -> completedColor
                    isActive -> activeColor
                    else -> inactiveColor
                },
                animationSpec = tween(300),
                label = "circleColor",
            )
            val circleBg by animateColorAsState(
                targetValue = if (isCompleted) completedColor else Color.Transparent,
                animationSpec = tween(300),
                label = "circleBg",
            )
            val textColor by animateColorAsState(
                targetValue = when {
                    isCompleted -> MaterialTheme.colorScheme.onPrimary
                    isActive -> activeColor
                    else -> inactiveColor
                },
                animationSpec = tween(300),
                label = "textColor",
            )
            val labelColor by animateColorAsState(
                targetValue = if (isActive) labelActiveColor else labelInactiveColor,
                animationSpec = tween(300),
                label = "labelColor",
            )

            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.widthIn(100.dp),
            ) {
                Box(
                    contentAlignment = Alignment.Center,
                    modifier = Modifier
                        .size(24.dp)
                        .clip(CircleShape)
                        .background(circleBg)
                        .border(width = 1.dp, color = circleColor, shape = CircleShape),
                ) {
                    Text(
                        text = if (isCompleted) "✓" else step.id,
                        color = textColor,
                        style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.SemiBold),
                    )
                }
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = step.label,
                    color = labelColor,
                    style = MaterialTheme.typography.bodySmallEmphasized.copy(
                        fontWeight = if (isActive) FontWeight.SemiBold else FontWeight.Normal,
                        textAlign = TextAlign.Center,
                    ),
                    //modifier = Modifier.widthIn(max = 72.dp),
                )
            }

            if (index < steps.lastIndex) {
                val lineProgress by animateFloatAsState(
                    targetValue = if (isCompleted) 1f else 0f,
                    animationSpec = spring(stiffness = 200f),
                    label = "lineProgress",
                )
                Spacer(
                    modifier = Modifier
                        .weight(1f)
                        .padding(top = 17.dp)
                        .height(2.dp)
                        .drawBehind {
                            drawLine(
                                color = inactiveColor,
                                start = Offset(0f, size.height / 2),
                                end = Offset(size.width, size.height / 2),
                                strokeWidth = 2.dp.toPx(),
                                cap = StrokeCap.Round,
                            )
                            if (lineProgress > 0f) {
                                drawLine(
                                    color = completedColor,
                                    start = Offset(0f, size.height / 2),
                                    end = Offset(size.width * lineProgress, size.height / 2),
                                    strokeWidth = 2.dp.toPx(),
                                    cap = StrokeCap.Round,
                                )
                            }
                        }
                )
            }
        }
    }
}
