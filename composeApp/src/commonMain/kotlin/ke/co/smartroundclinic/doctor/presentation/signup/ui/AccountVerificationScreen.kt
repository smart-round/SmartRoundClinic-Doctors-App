package ke.co.smartroundclinic.doctor.presentation.signup.ui

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
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import ke.co.smartroundclinic.doctor.presentation.common.composables.PrimaryButton
import ke.co.smartroundclinic.doctor.presentation.signup.AccountVerificationViewModel
import ke.co.smartroundclinic.doctor.presentation.theme.ShapeInput
import ke.co.smartroundclinic.doctor.presentation.theme.SmartRoundTheme
import ke.co.smartroundclinic.doctor.presentation.theme.smartRoundColors
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun AccountVerificationScreen(
    email: String,
    onVerify: () -> Unit,
    onBack: () -> Unit,
    onResendCode: () -> Unit,
    onGoToLogin: (() -> Unit)? = null,
    viewModel: AccountVerificationViewModel = koinViewModel(),
) {
    var otp by remember { mutableStateOf("") }
    val otpLength = 4
    val isVerifying by viewModel.isVerifying.collectAsStateWithLifecycle()
    val isResending by viewModel.isResending.collectAsStateWithLifecycle()
    val resendCooldown by viewModel.resendCooldown.collectAsStateWithLifecycle()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState()),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        // Gradient header
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
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 16.dp),
            ) {
                Text(
                    text = "Verify Email",
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.onPrimary,
                    modifier = Modifier.padding(bottom = 4.dp),
                )
                Text(
                    text = "A verification code has been sent to your email. Check your inbox and enter the code below.",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onPrimary,
                )
            }
        }

        Spacer(Modifier.height(32.dp))

        Column(
            modifier = Modifier.padding(horizontal = 16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {

        BasicTextField(
            value = otp,
            onValueChange = { if (it.length <= otpLength && it.all { c -> c.isDigit() }) otp = it },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.NumberPassword),
            decorationBox = {
                Row(
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    repeat(otpLength) { index ->
                        val char = otp.getOrNull(index)?.toString() ?: ""
                        val isCurrent = index == otp.length
                        Box(
                            contentAlignment = Alignment.Center,
                            modifier = Modifier
                                .weight(1f)
                                .size(64.dp)
                                .border(
                                    width = if (isCurrent) 2.dp else 1.dp,
                                    color = if (isCurrent || char.isNotEmpty())
                                        MaterialTheme.colorScheme.primary
                                    else
                                        MaterialTheme.colorScheme.outline,
                                    shape = ShapeInput,
                                ),
                        ) {
                            Text(
                                text = char,
                                style = MaterialTheme.typography.headlineMedium,
                                color = MaterialTheme.colorScheme.onBackground,
                                textAlign = TextAlign.Center,
                            )
                        }
                    }
                }
            }
        )

        Spacer(Modifier.height(20.dp))

        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center,
        ) {
            Text(
                text = "Didn't receive a code? ",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onBackground,
            )
            val canResend = resendCooldown == 0 && !isResending
            TextButton(
                onClick = { if (canResend) viewModel.resend(email) },
                enabled = canResend,
                contentPadding = androidx.compose.foundation.layout.PaddingValues(0.dp),
            ) {
                Text(
                    text = when {
                        isResending -> "Resending..."
                        resendCooldown > 0 -> "Resend in ${resendCooldown}s"
                        else -> "Resend Code"
                    },
                    style = MaterialTheme.typography.bodySmall,
                    color = if (canResend) MaterialTheme.colorScheme.primary
                            else MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }
        }

        Spacer(Modifier.height(32.dp))

        PrimaryButton(
            onClick = { viewModel.verify(email, otp, onVerify) },
            enabled = otp.length == otpLength && !isVerifying,
        ) {
            if (isVerifying) {
                Box(contentAlignment = Alignment.Center, modifier = Modifier.padding(vertical = 14.dp)) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(20.dp),
                        strokeWidth = 2.dp,
                        color = MaterialTheme.colorScheme.onPrimary,
                    )
                }
            } else {
                Text(
                    text = "Continue",
                    style = MaterialTheme.typography.labelLarge,
                    color = MaterialTheme.colorScheme.onPrimary,
                    modifier = Modifier.padding(vertical = 14.dp),
                )
            }
        }

        Spacer(Modifier.height(4.dp))

        TextButton(
            onClick = onBack,
            modifier = Modifier.fillMaxWidth(),
            colors = ButtonDefaults.textButtonColors(contentColor = MaterialTheme.colorScheme.onBackground),
        ) {
            Text(text = "Back", style = MaterialTheme.typography.bodyMedium)
        }

        if (onGoToLogin != null) {
            TextButton(
                onClick = onGoToLogin,
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.textButtonColors(contentColor = MaterialTheme.colorScheme.primary),
            ) {
                Text(text = "Back to Login", style = MaterialTheme.typography.bodyMedium)
            }
        }

        } // end inner padding Column
    } // end outer scroll Column
}

@Preview
@Composable
private fun AccountVerificationScreenPreview() {
    SmartRoundTheme {
        val otp = "12"
        val otpLength = 4
        Column(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
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
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 16.dp),
                ) {
                    Text(
                        text = "Verify Email",
                        style = MaterialTheme.typography.titleMedium,
                        color = MaterialTheme.colorScheme.onPrimary,
                        modifier = Modifier.padding(bottom = 4.dp),
                    )
                    Text(
                        text = "A verification code has been sent to your email. Check your inbox and enter the code below.",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onPrimary,
                    )
                }
            }

            Spacer(Modifier.height(32.dp))

            Column(
                modifier = Modifier.padding(horizontal = 16.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {

            Row(
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                repeat(otpLength) { index ->
                    val char = otp.getOrNull(index)?.toString() ?: ""
                    val isCurrent = index == otp.length
                    Box(
                        contentAlignment = Alignment.Center,
                        modifier = Modifier
                            .weight(1f)
                            .size(64.dp)
                            .border(
                                width = if (isCurrent) 2.dp else 1.dp,
                                color = if (isCurrent || char.isNotEmpty())
                                    MaterialTheme.colorScheme.primary
                                else
                                    MaterialTheme.colorScheme.outline,
                                shape = ShapeInput,
                            ),
                    ) {
                        Text(
                            text = char,
                            style = MaterialTheme.typography.headlineMedium,
                            color = MaterialTheme.colorScheme.onBackground,
                            textAlign = TextAlign.Center,
                        )
                    }
                }
            }

            Spacer(Modifier.height(20.dp))

            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center,
            ) {
                Text(
                    text = "Didn't receive a code? ",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onBackground,
                )
                TextButton(
                    onClick = {},
                    contentPadding = androidx.compose.foundation.layout.PaddingValues(0.dp),
                ) {
                    Text(
                        text = "Resend Code",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.primary,
                    )
                }
            }

            Spacer(Modifier.height(32.dp))

            PrimaryButton(onClick = {}, enabled = false) {
                Text(
                    text = "Continue",
                    style = MaterialTheme.typography.labelLarge,
                    color = MaterialTheme.colorScheme.onPrimary,
                    modifier = Modifier.padding(vertical = 14.dp),
                )
            }

            Spacer(Modifier.height(4.dp))

            TextButton(
                onClick = {},
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.textButtonColors(contentColor = MaterialTheme.colorScheme.onBackground),
            ) {
                Text(text = "Back", style = MaterialTheme.typography.bodyMedium)
            }

            } // end inner Column
        } // end outer Column
    }
}
