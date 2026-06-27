package ke.co.smartroundclinic.doctor.presentation.signup.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.relocation.BringIntoViewRequester
import androidx.compose.foundation.relocation.bringIntoViewRequester
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuAnchorType
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.MenuDefaults
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
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
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.OffsetMapping
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.TransformedText
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage
import io.github.vinceglb.filekit.dialogs.FileKitType
import io.github.vinceglb.filekit.dialogs.compose.rememberCameraPickerLauncher
import io.github.vinceglb.filekit.dialogs.compose.rememberFilePickerLauncher
import io.github.vinceglb.filekit.readBytes
import ke.co.smartroundclinic.doctor.core.media.PhotoPickerBottomSheet
import ke.co.smartroundclinic.doctor.presentation.common.composables.PrimaryButton
import ke.co.smartroundclinic.doctor.presentation.signup.PersonalInfoData
import ke.co.smartroundclinic.doctor.presentation.signup.SignUpFilesViewModel
import ke.co.smartroundclinic.doctor.presentation.theme.ShapeInput
import kotlinx.coroutines.launch
import ke.co.smartroundclinic.doctor.core.platform.todayDay
import ke.co.smartroundclinic.doctor.core.platform.todayMonth
import ke.co.smartroundclinic.doctor.core.platform.todayYear
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import org.jetbrains.compose.resources.ExperimentalResourceApi
import smartroundclinic.composeapp.generated.resources.Res



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
    val phoneFocus = remember { FocusRequester() }
    val kraPinFocus = remember { FocusRequester() }
    val passwordFocus = remember { FocusRequester() }

    // UI-only transient state — fine to reset on back-nav
    var passwordVisible by remember { mutableStateOf(false) }
    var genderExpanded by remember { mutableStateOf(false) }
    var showPhotoPicker by remember { mutableStateOf(false) }
    var showCountryPicker by remember { mutableStateOf(false) }
    var countryQuery by remember { mutableStateOf("") }

    val photoSheetState = rememberModalBottomSheetState()
    val countrySheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    val scope = rememberCoroutineScope()
    val passwordBivr = remember { BringIntoViewRequester() }
    val allCountries = rememberCountryCodes()

    val genderOptions = listOf("Male", "Female", "Other")

    val filteredCountries = remember(countryQuery, allCountries) {
        if (countryQuery.isEmpty()) allCountries
        else allCountries.filter {
            it.name.contains(countryQuery, ignoreCase = true) ||
                it.dialCode.contains(countryQuery)
        }
    }

    val dobError = when {
        formViewModel.dob.isEmpty() -> null
        formViewModel.dob.length < 8 -> "Enter a complete date (DD/MM/YYYY)"
        !formViewModel.dob.isValidDate() -> "Invalid date"
        !formViewModel.dob.isOldEnough() -> "Must be at least 18 years old"
        else -> null
    }
    val emailError = if (formViewModel.email.isNotBlank() && !formViewModel.email.isValidEmail()) "Enter a valid email" else null
    val phoneNumberError = if (formViewModel.phoneNumber.isNotBlank()) {
        val digits = formViewModel.phoneNumber.filter { it.isDigit() }
        if (digits.length < 6 || digits.length > 12) "Enter a valid phone number" else null
    } else null

    val isFormValid = formViewModel.fullName.isNotBlank() &&
        formViewModel.gender.isNotBlank() &&
        formViewModel.dob.length == 8 && formViewModel.dob.isValidDate() && formViewModel.dob.isOldEnough() &&
        formViewModel.email.isValidEmail() &&
        formViewModel.phoneNumber.isNotBlank() &&
        phoneNumberError == null &&
        formViewModel.kraPin.isNotBlank() &&
        formViewModel.password.length >= 8

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

    if (showCountryPicker) {
        val currentCountry = remember(allCountries, formViewModel.countryDialCode, formViewModel.countryName) {
            allCountries.firstOrNull { it.dialCode == formViewModel.countryDialCode && it.name == formViewModel.countryName }
                ?: allCountries.firstOrNull { it.dialCode == formViewModel.countryDialCode }
                ?: defaultCountry
        }
        CountryCodeBottomSheet(
            sheetState = countrySheetState,
            query = countryQuery,
            onQueryChange = { countryQuery = it },
            countries = filteredCountries,
            selectedCountry = currentCountry,
            initialScrollIndex = allCountries.indexOf(currentCountry).coerceAtLeast(0),
            onSelect = { country ->
                formViewModel.countryDialCode = country.dialCode
                formViewModel.countryFlag = country.flag
                formViewModel.countryName = country.name
                countryQuery = ""
                scope.launch {
                    countrySheetState.hide()
                    showCountryPicker = false
                }
            },
            onDismiss = {
                showCountryPicker = false
                countryQuery = ""
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
                    .border(1.dp, MaterialTheme.colorScheme.outline, CircleShape)
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
            text = "Upload Photo",
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

        Row(
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            modifier = Modifier.fillMaxWidth(),
        ) {
            ExposedDropdownMenuBox(
                expanded = genderExpanded,
                onExpandedChange = { genderExpanded = it },
                modifier = Modifier.weight(1f),
            ) {
                OutlinedTextField(
                    value = formViewModel.gender,
                    onValueChange = {},
                    readOnly = true,
                    label = { Text("Gender", style = MaterialTheme.typography.bodySmall) },
                    trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(genderExpanded) },
                    shape = ShapeInput,
                    modifier = Modifier
                        .fillMaxWidth()
                        .menuAnchor(ExposedDropdownMenuAnchorType.PrimaryNotEditable),
                )
                ExposedDropdownMenu(
                    expanded = genderExpanded,
                    onDismissRequest = { genderExpanded = false },
                    containerColor = MaterialTheme.colorScheme.background,
                ) {
                    genderOptions.forEach { option ->
                        DropdownMenuItem(
                            colors = MenuDefaults.itemColors(textColor = MaterialTheme.colorScheme.onBackground),
                            text = { Text(option, style = MaterialTheme.typography.bodySmall) },
                            onClick = { formViewModel.gender = option; genderExpanded = false },
                        )
                    }
                }
            }

            SignUpField(
                value = formViewModel.dob,
                onValueChange = { formViewModel.dob = it.filter { c -> c.isDigit() }.take(8) },
                label = "DOB",
                placeholder = "DD/MM/YYYY",
                keyboardType = KeyboardType.Number,
                imeAction = ImeAction.Next,
                visualTransformation = DobVisualTransformation,
                errorMessage = dobError,
                modifier = Modifier.weight(1f),
                onNext = { emailFocus.requestFocus() },
            )
        }

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
            onNext = { phoneFocus.requestFocus() },
        )

        Spacer(Modifier.height(12.dp))

        // Phone number row: country code trigger + number input
        Row(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalAlignment = Alignment.Top,
            modifier = Modifier.fillMaxWidth(),
        ) {
            // Country code button — transparent overlay over OutlinedTextField to open bottom sheet
            Box(modifier = Modifier.width(120.dp)) {
                OutlinedTextField(
                    value = "${formViewModel.countryFlag} ${formViewModel.countryDialCode}",
                    onValueChange = {},
                    readOnly = true,
                    singleLine = true,
                    label = { Text("Code", style = MaterialTheme.typography.bodySmall) },
                    shape = ShapeInput,
                    trailingIcon = {
                        Icon(
                            imageVector = Icons.Default.ArrowDropDown,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.size(18.dp),
                        )
                    },
                    textStyle = MaterialTheme.typography.bodySmall,
                    modifier = Modifier.fillMaxWidth(),
                )
                // Invisible tap overlay — prevents keyboard, opens sheet instead
                Box(
                    modifier = Modifier
                        .matchParentSize()
                        .clickable(
                            interactionSource = remember { MutableInteractionSource() },
                            indication = null,
                        ) { showCountryPicker = true },
                )
            }

            SignUpField(
                value = formViewModel.phoneNumber,
                onValueChange = { formViewModel.phoneNumber = it },
                label = "Phone Number",
                placeholder = "7XX XXX XXX",
                keyboardType = KeyboardType.Phone,
                imeAction = ImeAction.Next,
                errorMessage = phoneNumberError,
                modifier = Modifier
                    .weight(1f)
                    .focusRequester(phoneFocus),
                onNext = { kraPinFocus.requestFocus() },
            )
        }

        Spacer(Modifier.height(12.dp))

        SignUpField(
            value = formViewModel.kraPin,
            onValueChange = { formViewModel.kraPin = it },
            label = "KRA PIN",
            placeholder = "Enter your KRA PIN",
            imeAction = ImeAction.Next,
            modifier = Modifier.focusRequester(kraPinFocus),
            onNext = { passwordFocus.requestFocus() },
        )

        Spacer(Modifier.height(12.dp))

        Column(modifier = Modifier.focusRequester(passwordFocus).bringIntoViewRequester(passwordBivr)) {
            OutlinedTextField(
                value = formViewModel.password,
                onValueChange = { formViewModel.password = it },
                label = { Text("Password", style = MaterialTheme.typography.bodySmall) },
                visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                isError = formViewModel.password.isNotBlank() && formViewModel.password.length < 8,
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
            if (formViewModel.password.isNotBlank() && formViewModel.password.length < 8) {
                Text(
                    text = "Minimum 8 characters",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.error,
                    modifier = Modifier.padding(start = 4.dp, top = 2.dp),
                )
            }
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
                        gender = formViewModel.gender,
                        email = formViewModel.email,
                        phoneNumber = "${formViewModel.countryDialCode}${formViewModel.phoneNumber}",
                        kraPin = formViewModel.kraPin,
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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun CountryCodeBottomSheet(
    sheetState: androidx.compose.material3.SheetState,
    query: String,
    onQueryChange: (String) -> Unit,
    countries: List<CountryCode>,
    selectedCountry: CountryCode,
    initialScrollIndex: Int,
    onSelect: (CountryCode) -> Unit,
    onDismiss: () -> Unit,
) {
    val listState = rememberLazyListState()

    LaunchedEffect(Unit) {
        if (initialScrollIndex > 0) {
            listState.scrollToItem(maxOf(0, initialScrollIndex - 2))
        }
    }

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        containerColor = MaterialTheme.colorScheme.background,
        contentColor = MaterialTheme.colorScheme.onBackground,
        dragHandle = {
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 12.dp, bottom = 4.dp),
            ) {
                Box(
                    modifier = Modifier
                        .width(40.dp)
                        .height(4.dp)
                        .clip(MaterialTheme.shapes.small)
                        .background(MaterialTheme.colorScheme.onBackground.copy(alpha = 0.2f)),
                )
            }
        },
    ) {
        Column {
            Text(
                text = "Select Country Code",
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onBackground,
                modifier = Modifier.padding(horizontal = 20.dp, vertical = 8.dp),
            )

            OutlinedTextField(
                value = query,
                onValueChange = onQueryChange,
                placeholder = { Text("Search country or code", style = MaterialTheme.typography.bodySmall) },
                leadingIcon = {
                    Icon(
                        imageVector = Icons.Default.Search,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                },
                singleLine = true,
                shape = ShapeInput,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 4.dp),
            )

            Spacer(Modifier.height(8.dp))

            LazyColumn(state = listState) {
                items(countries, key = { "${it.dialCode}_${it.name}" }) { country ->
                    val isSelected = country == selectedCountry
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { onSelect(country) }
                            .background(
                                if (isSelected) MaterialTheme.colorScheme.primary.copy(alpha = 0.08f)
                                else Color.Transparent,
                            )
                            .padding(horizontal = 20.dp, vertical = 14.dp),
                    ) {
                        Text(
                            text = country.flag,
                            style = MaterialTheme.typography.titleMedium,
                            modifier = Modifier.width(36.dp),
                        )
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = country.name,
                                style = MaterialTheme.typography.bodyMedium,
                                color = if (isSelected) MaterialTheme.colorScheme.primary
                                else MaterialTheme.colorScheme.onBackground,
                            )
                            Text(
                                text = country.dialCode,
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                            )
                        }
                        if (isSelected) {
                            Icon(
                                imageVector = Icons.Default.Check,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.primary,
                                modifier = Modifier.size(20.dp),
                            )
                        }
                    }
                    HorizontalDivider(
                        color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.4f),
                        thickness = 0.5.dp,
                    )
                }
            }
        }
    }
}

private fun String.isValidEmail(): Boolean =
    contains("@") && substringAfter("@").contains(".") && length > 5

private fun String.isValidDate(): Boolean {
    if (length != 8) return false
    val day = substring(0, 2).toIntOrNull() ?: return false
    val month = substring(2, 4).toIntOrNull() ?: return false
    val year = substring(4, 8).toIntOrNull() ?: return false
    if (month < 1 || month > 12) return false
    if (year < 1900 || year > todayYear()) return false
    val daysInMonth = when (month) {
        2 -> if (year % 4 == 0 && (year % 100 != 0 || year % 400 == 0)) 29 else 28
        4, 6, 9, 11 -> 30
        else -> 31
    }
    return day in 1..daysInMonth
}

private fun String.isOldEnough(): Boolean {
    if (!isValidDate()) return false
    val day = substring(0, 2).toInt()
    val month = substring(2, 4).toInt()
    val year = substring(4, 8).toInt()
    val age = todayYear() - year -
        if (todayMonth() < month || (todayMonth() == month && todayDay() < day)) 1 else 0
    return age >= 18
}

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

// Stores raw digits (max 8), displays as DD/MM/YYYY.
private object DobVisualTransformation : VisualTransformation {
    override fun filter(text: AnnotatedString): TransformedText {
        val digits = text.text.take(8)
        val formatted = buildString {
            digits.forEachIndexed { index, c ->
                if (index == 2 || index == 4) append('/')
                append(c)
            }
        }
        val offsetMapping = object : OffsetMapping {
            override fun originalToTransformed(offset: Int): Int {
                var result = offset
                if (offset >= 2 && digits.length > 2) result++
                if (offset >= 4 && digits.length > 4) result++
                return result
            }
            override fun transformedToOriginal(offset: Int): Int {
                var result = offset
                if (offset > 2) result--
                if (offset > 5) result--
                return result.coerceAtMost(digits.length)
            }
        }
        return TransformedText(AnnotatedString(formatted), offsetMapping)
    }
}



@Serializable
private data class CountryCode(val name: String, val dialCode: String, val flag: String)

@OptIn(ExperimentalResourceApi::class)
@Composable
private fun rememberCountryCodes(): List<CountryCode> {
    var list by remember { mutableStateOf(emptyList<CountryCode>()) }
    LaunchedEffect(Unit) {
        val bytes = Res.readBytes("files/country_codes.json")
        list = Json.decodeFromString(bytes.decodeToString())
    }
    return list
}


private val defaultCountry = CountryCode("Kenya", "+254", "🇰🇪")