package ke.co.smartroundclinic.doctor.presentation.signup.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.relocation.BringIntoViewRequester
import androidx.compose.foundation.relocation.bringIntoViewRequester
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage
import io.github.vinceglb.filekit.dialogs.FileKitType
import io.github.vinceglb.filekit.dialogs.compose.rememberCameraPickerLauncher
import io.github.vinceglb.filekit.dialogs.compose.rememberFilePickerLauncher
import io.github.vinceglb.filekit.readBytes
import ke.co.smartroundclinic.doctor.core.media.PhotoPickerBottomSheet
import ke.co.smartroundclinic.doctor.common.isValidPassword
import ke.co.smartroundclinic.doctor.presentation.common.composables.PasswordRequirements
import ke.co.smartroundclinic.doctor.presentation.common.composables.PrimaryButton
import ke.co.smartroundclinic.doctor.presentation.signup.PersonalInfoData
import ke.co.smartroundclinic.doctor.presentation.signup.SignUpFilesViewModel
import ke.co.smartroundclinic.doctor.presentation.theme.ShapeInput
import kotlinx.coroutines.launch



@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SignUpScreen(
    filesViewModel: SignUpFilesViewModel,
    formViewModel: ke.co.smartroundclinic.doctor.presentation.signup.SignUpFormViewModel,
    onNext: (PersonalInfoData) -> Unit,
    onSignIn: () -> Unit,
) {
    val fullNameFocus = remember { FocusRequester() }
    val emailFocus = remember { FocusRequester() }
    val passwordFocus = remember { FocusRequester() }

    // UI-only transient state — fine to reset on back-nav
    var passwordVisible by remember { mutableStateOf(false) }
    var showPhotoPicker by remember { mutableStateOf(false) }

    val photoSheetState = rememberModalBottomSheetState()
    val scope = rememberCoroutineScope()
    val passwordBivr = remember { BringIntoViewRequester() }

    val emailError = if (formViewModel.email.isNotBlank() && !formViewModel.email.isValidEmail()) "Enter a valid email" else null

    val isFormValid = formViewModel.fullName.isNotBlank() &&
        formViewModel.email.isValidEmail() &&
        formViewModel.password.isValidPassword() &&
        filesViewModel.profilePictureBytes != null

    val galleryLauncher = rememberFilePickerLauncher(type = FileKitType.Image) { file ->
        scope.launch { filesViewModel.profilePictureBytes = file?.readBytes() }
    }
    val cameraLauncher = rememberCameraPickerLauncher { file ->
        scope.launch { filesViewModel.profilePictureBytes = file?.readBytes() }
    }

    if (showPhotoPicker) {
        PhotoPickerBottomSheet(
            sheetState = photoSheetState,
            onDismiss = { showPhotoPicker = false },
            onTakePhoto = {
                scope.launch {
                    photoSheetState.hide()
                    showPhotoPicker = false
                    cameraLauncher.launch()
                }
            },
            onChooseFromGallery = {
                scope.launch {
                    photoSheetState.hide()
                    showPhotoPicker = false
                    galleryLauncher.launch()
                }
            },
        )
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .imePadding(),
    ) {
    Column(
        modifier = Modifier
            .weight(1f)
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 16.dp, vertical = 16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Box(
            contentAlignment = Alignment.BottomEnd,
            modifier = Modifier.size(88.dp),
        ) {
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier
                    .size(80.dp)
                    .clip(CircleShape)
                    .background(MaterialTheme.colorScheme.surfaceVariant)
                    .border(
                        1.dp,
                        if (filesViewModel.profilePictureBytes == null) MaterialTheme.colorScheme.error
                        else MaterialTheme.colorScheme.outline,
                        CircleShape,
                    )
                    .clickable { showPhotoPicker = true },
            ) {
                if (filesViewModel.profilePictureBytes != null) {
                    AsyncImage(
                        model = filesViewModel.profilePictureBytes,
                        contentDescription = "Profile photo",
                        contentScale = ContentScale.Crop,
                        modifier = Modifier.fillMaxSize(),
                    )
                } else {
                    Icon(
                        painter = rememberVectorPainter(Icons.Default.Person),
                        contentDescription = "Upload photo",
                        tint = Color.White,
                        modifier = Modifier.fillMaxSize(),
                    )
                }
            }
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier
                    .size(26.dp)
                    .clip(CircleShape)
                    .background(MaterialTheme.colorScheme.primary)
                    .border(2.dp, MaterialTheme.colorScheme.background, CircleShape),
            ) {
                Icon(
                    imageVector = Icons.Default.CameraAlt,
                    contentDescription = "Change photo",
                    tint = MaterialTheme.colorScheme.onPrimary,
                    modifier = Modifier.size(14.dp),
                )
            }
        }

        Spacer(Modifier.height(4.dp))
        Text(
            text = "Upload Photo *",
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )

        Spacer(Modifier.height(16.dp))

        SignUpField(
            value = formViewModel.fullName,
            onValueChange = { formViewModel.fullName = it },
            label = "Full Name",
            placeholder = "Enter your full name",
            imeAction = ImeAction.Next,
            modifier = Modifier.focusRequester(fullNameFocus),
            onNext = { emailFocus.requestFocus() },
        )

        Spacer(Modifier.height(12.dp))

        SignUpField(
            value = formViewModel.email,
            onValueChange = { formViewModel.email = it },
            label = "Email",
            placeholder = "Enter your email",
            keyboardType = KeyboardType.Email,
            imeAction = ImeAction.Next,
            errorMessage = emailError,
            modifier = Modifier.focusRequester(emailFocus),
            onNext = { passwordFocus.requestFocus() },
        )

        Spacer(Modifier.height(12.dp))

        Column(modifier = Modifier.focusRequester(passwordFocus).bringIntoViewRequester(passwordBivr)) {
            OutlinedTextField(
                value = formViewModel.password,
                onValueChange = { formViewModel.password = it },
                label = { Text("Password", style = MaterialTheme.typography.bodySmall) },
                visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                isError = formViewModel.password.isNotBlank() && !formViewModel.password.isValidPassword(),
                trailingIcon = {
                    TextButton(onClick = { passwordVisible = !passwordVisible }) {
                        Text(
                            text = if (passwordVisible) "Hide" else "Show",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.primary,
                        )
                    }
                },
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Password,
                    imeAction = ImeAction.Done,
                ),
                keyboardActions = KeyboardActions(onDone = {}),
                shape = ShapeInput,
                singleLine = true,
                modifier = Modifier
                    .fillMaxWidth()
                    .onFocusChanged { if (it.isFocused) scope.launch { passwordBivr.bringIntoView() } },
            )
            PasswordRequirements(
                password = formViewModel.password,
                visible = formViewModel.password.isNotBlank(),
            )
        }

        Spacer(Modifier.height(8.dp))
    } // end scrollable form Column

    // Fixed footer — always visible above the keyboard
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(MaterialTheme.colorScheme.background)
            .padding(start = 16.dp, end = 16.dp, top = 12.dp, bottom = 16.dp),
    ) {
        PrimaryButton(
            onClick = {
                onNext(
                    PersonalInfoData(
                        fullName = formViewModel.fullName,
                        email = formViewModel.email,
                        password = formViewModel.password,
                    )
                )
            },
            enabled = isFormValid,
        ) {
            Text(
                text = "Next",
                style = MaterialTheme.typography.labelLarge,
                color = MaterialTheme.colorScheme.onPrimary,
                modifier = Modifier.padding(vertical = 14.dp),
            )
        }

        Spacer(Modifier.height(8.dp))

        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center,
            modifier = Modifier.fillMaxWidth(),
        ) {
            Text(
                text = "Already have an account? ",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onBackground,
            )
            TextButton(onClick = onSignIn, contentPadding = PaddingValues(0.dp)) {
                Text(
                    text = "Sign In",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.primary,
                )
            }
        }
    }
    } // end outer imePadding Column
}

private fun String.isValidEmail(): Boolean =
    contains("@") && substringAfter("@").contains(".") && length > 5

@Composable
internal fun SignUpField(
    value: String,
    onValueChange: (String) -> Unit,
    label: String,
    placeholder: String,
    modifier: Modifier = Modifier,
    keyboardType: KeyboardType = KeyboardType.Text,
    imeAction: ImeAction = ImeAction.Next,
    onNext: (() -> Unit)? = null,
    visualTransformation: VisualTransformation = VisualTransformation.None,
    errorMessage: String? = null,
) {
    val bringIntoViewRequester = remember { BringIntoViewRequester() }
    val scope = rememberCoroutineScope()

    Column(modifier = modifier.bringIntoViewRequester(bringIntoViewRequester)) {
        OutlinedTextField(
            value = value,
            onValueChange = onValueChange,
            label = { Text(label, style = MaterialTheme.typography.bodySmall) },
            placeholder = { Text(placeholder, style = MaterialTheme.typography.bodySmall) },
            isError = errorMessage != null,
            keyboardOptions = KeyboardOptions(keyboardType = keyboardType, imeAction = imeAction),
            keyboardActions = KeyboardActions(
                onNext = { onNext?.invoke() },
                onDone = { onNext?.invoke() },
            ),
            visualTransformation = visualTransformation,
            shape = ShapeInput,
            singleLine = true,
            modifier = Modifier
                .fillMaxWidth()
                .onFocusChanged { if (it.isFocused) scope.launch { bringIntoViewRequester.bringIntoView() } },
        )
        if (errorMessage != null) {
            Text(
                text = errorMessage,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.error,
                modifier = Modifier.padding(start = 4.dp, top = 2.dp),
            )
        }
    }
}