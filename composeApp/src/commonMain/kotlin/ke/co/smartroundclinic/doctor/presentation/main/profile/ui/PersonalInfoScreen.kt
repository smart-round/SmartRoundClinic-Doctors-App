package ke.co.smartroundclinic.doctor.presentation.main.profile.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.ArrowBack
import androidx.compose.material.icons.outlined.CameraAlt
import androidx.compose.material.icons.outlined.Delete
import androidx.compose.material.icons.outlined.LocalHospital
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuAnchorType
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.MenuDefaults
import androidx.compose.material3.MenuItemColors
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage
import io.github.vinceglb.filekit.dialogs.FileKitType
import io.github.vinceglb.filekit.dialogs.compose.rememberCameraPickerLauncher
import io.github.vinceglb.filekit.dialogs.compose.rememberFilePickerLauncher
import io.github.vinceglb.filekit.readBytes
import ke.co.smartroundclinic.doctor.common.Resource
import ke.co.smartroundclinic.doctor.core.media.PhotoPickerBottomSheet
import ke.co.smartroundclinic.doctor.data.remote.dto.request.Gender
import ke.co.smartroundclinic.doctor.presentation.common.composables.PrimaryButton
import ke.co.smartroundclinic.doctor.presentation.main.profile.PersonalInfoViewModel
import ke.co.smartroundclinic.doctor.presentation.theme.GradientEnd
import ke.co.smartroundclinic.doctor.presentation.theme.GradientStart
import ke.co.smartroundclinic.doctor.presentation.theme.Primary40
import ke.co.smartroundclinic.doctor.presentation.theme.ShapeInput
import kotlinx.coroutines.launch
import org.koin.compose.viewmodel.koinViewModel

private val genderOptions = listOf(
    Gender.MALE to "Male",
    Gender.FEMALE to "Female",
    Gender.NON_BINARY to "Non-binary",
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun PersonalInfoScreen(
    onBack: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: PersonalInfoViewModel = koinViewModel(),
) {
    val user by viewModel.user.collectAsState()
    val uploadState by viewModel.uploadState.collectAsState()
    val specializationTitle by viewModel.specializationTitle.collectAsState()
    val isUploading = uploadState is Resource.Loading
    val isSaving = viewModel.isSaving
    val isDeleting = viewModel.isDeleting

    var selectedBytes by remember { mutableStateOf<ByteArray?>(null) }

    // Two-level sheet flow: profile picture sheet → photo picker sheet
    var showProfilePictureSheet by remember { mutableStateOf(false) }
    var showPhotoPicker by remember { mutableStateOf(false) }
    val profilePictureSheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    val photoPickerSheetState = rememberModalBottomSheetState()
    val scope = rememberCoroutineScope()

    var fullName by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var phone by remember { mutableStateOf("") }
    var dateOfBirth by remember { mutableStateOf("") }
    var selectedGender by remember { mutableStateOf<Gender?>(null) }
    var genderExpanded by remember { mutableStateOf(false) }

    LaunchedEffect(user?.id) {
        user?.let {
            fullName = it.fullName
            email = it.email
            phone = it.phoneNumber ?: ""
            dateOfBirth = it.dateOfBirth ?: ""
            selectedGender = Gender.entries.firstOrNull { g -> g.name == it.gender }
        }
    }

    val profilePictureUrl = user?.profilePicture

    val galleryLauncher = rememberFilePickerLauncher(type = FileKitType.Image) { file ->
        scope.launch {
            file?.readBytes()?.let { bytes ->
                selectedBytes = bytes
                viewModel.uploadProfilePicture(bytes)
            }
        }
    }
    val cameraLauncher = rememberCameraPickerLauncher { file ->
        scope.launch {
            file?.readBytes()?.let { bytes ->
                selectedBytes = bytes
                viewModel.uploadProfilePicture(bytes)
            }
        }
    }

    // Profile picture management sheet
    if (showProfilePictureSheet) {
        ProfilePictureBottomSheet(
            sheetState = profilePictureSheetState,
            profilePictureBytes = selectedBytes,
            profilePictureUrl = profilePictureUrl,
            isUploading = isUploading,
            isDeleting = isDeleting,
            onDismiss = { showProfilePictureSheet = false },
            onChangePhoto = {
                scope.launch {
                    profilePictureSheetState.hide()
                    showProfilePictureSheet = false
                    showPhotoPicker = true
                }
            },
            onDeletePhoto = {
                viewModel.deleteProfilePicture {
                    scope.launch {
                        profilePictureSheetState.hide()
                        showProfilePictureSheet = false
                        selectedBytes = null
                    }
                }
            },
        )
    }

    // Camera / gallery picker sheet (second level)
    if (showPhotoPicker) {
        PhotoPickerBottomSheet(
            sheetState = photoPickerSheetState,
            onDismiss = { showPhotoPicker = false },
            onTakePhoto = {
                scope.launch {
                    photoPickerSheetState.hide()
                    showPhotoPicker = false
                    cameraLauncher.launch()
                }
            },
            onChooseFromGallery = {
                scope.launch {
                    photoPickerSheetState.hide()
                    showPhotoPicker = false
                    galleryLauncher.launch()
                }
            },
        )
    }

    Scaffold(
        modifier = modifier,
        contentWindowInsets = WindowInsets(0),
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues),
        ) {
            PersonalInfoHeader(
                onBack = onBack,
                onPhotoClick = { showProfilePictureSheet = true },
                profilePictureBytes = selectedBytes,
                profilePictureUrl = profilePictureUrl,
                isUploading = isUploading,
            )

            Column(
                modifier = Modifier
                    .weight(1f)
                    .verticalScroll(rememberScrollState())
                    .padding(horizontal = 20.dp, vertical = 20.dp),
            ) {
                Text(
                    text = "Personal Information",
                    style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.SemiBold),
                    color = MaterialTheme.colorScheme.onSurface,
                )
                Spacer(Modifier.height(12.dp))
                OutlinedTextField(
                    value = fullName,
                    onValueChange = { fullName = it },
                    label = { Text("Full Name", style = MaterialTheme.typography.bodySmall) },
                    shape = ShapeInput,
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth(),
                )
                Spacer(Modifier.height(12.dp))
                OutlinedTextField(
                    value = email,
                    onValueChange = { email = it },
                    label = { Text("Email Address", style = MaterialTheme.typography.bodySmall) },
                    shape = ShapeInput,
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth(),
                )
                Spacer(Modifier.height(12.dp))
                OutlinedTextField(
                    value = phone,
                    onValueChange = { phone = it },
                    label = { Text("Phone Number", style = MaterialTheme.typography.bodySmall) },
                    shape = ShapeInput,
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth(),
                )
                Spacer(Modifier.height(12.dp))
                OutlinedTextField(
                    value = dateOfBirth,
                    onValueChange = { dateOfBirth = it },
                    label = { Text("Date of Birth (DD/MM/YYYY)", style = MaterialTheme.typography.bodySmall) },
                    shape = ShapeInput,
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth(),
                )
                Spacer(Modifier.height(12.dp))
                ExposedDropdownMenuBox(
                    expanded = genderExpanded,
                    onExpandedChange = { genderExpanded = it },
                ) {
                    OutlinedTextField(
                        value = genderOptions.firstOrNull { it.first == selectedGender }?.second ?: "",
                        onValueChange = {},
                        readOnly = true,
                        label = { Text("Gender", style = MaterialTheme.typography.bodySmall) },
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = genderExpanded) },
                        shape = ShapeInput,
                        modifier = Modifier
                            .fillMaxWidth()
                            .menuAnchor(ExposedDropdownMenuAnchorType.PrimaryNotEditable),
                    )
                    ExposedDropdownMenu(
                        expanded = genderExpanded,
                        onDismissRequest = { genderExpanded = false },
                        containerColor =  MaterialTheme.colorScheme.background


                    ) {
                        genderOptions.forEach { (gender, label) ->
                            DropdownMenuItem(
                                text = { Text(label) },
                                colors = MenuDefaults.itemColors(MaterialTheme.colorScheme.onBackground),
                                onClick = {
                                    selectedGender = gender
                                    genderExpanded = false
                                },
                            )
                        }
                    }
                }
                Spacer(Modifier.height(20.dp))
                PrimaryButton(
                    onClick = {
                        viewModel.updatePersonalInfo(
                            fullName = fullName,
                            email = email,
                            phoneNumber = phone,
                            dateOfBirth = dateOfBirth.takeIf { it.isNotBlank() },
                            gender = selectedGender,
                        )
                    },
                    enabled = phone.isNotBlank() && !isSaving,
                ) {
                    if (isSaving) {
                        CircularProgressIndicator(
                            color = Color.White,
                            modifier = Modifier.size(20.dp),
                            strokeWidth = 2.dp,
                        )
                    } else {
                        Text(
                            text = "Save Changes",
                            style = MaterialTheme.typography.labelLarge,
                            color = Color.White,
                            modifier = Modifier.padding(vertical = 14.dp),
                        )
                    }
                }

            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun ProfilePictureBottomSheet(
    sheetState: androidx.compose.material3.SheetState,
    profilePictureBytes: ByteArray?,
    profilePictureUrl: String?,
    isUploading: Boolean,
    isDeleting: Boolean,
    onDismiss: () -> Unit,
    onChangePhoto: () -> Unit,
    onDeletePhoto: () -> Unit,
) {
    val hasPicture = profilePictureBytes != null || profilePictureUrl != null

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        containerColor = MaterialTheme.colorScheme.background,
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp)
                .navigationBarsPadding(),
        ) {
            Text(
                text = "Profile Picture",
                style = MaterialTheme.typography.titleMedium,
                modifier = Modifier.padding(bottom = 20.dp),
            )

            // Large preview
            Box(
                modifier = Modifier
                    .size(140.dp)
                    .clip(CircleShape)
                    .background(MaterialTheme.colorScheme.surfaceVariant),
                contentAlignment = Alignment.Center,
            ) {
                when {
                    isUploading -> CircularProgressIndicator(
                        modifier = Modifier.size(40.dp),
                        strokeWidth = 3.dp,
                    )
                    profilePictureBytes != null -> AsyncImage(
                        model = profilePictureBytes,
                        contentDescription = "Profile picture",
                        contentScale = ContentScale.Crop,
                        modifier = Modifier.fillMaxSize(),
                    )
                    profilePictureUrl != null -> AsyncImage(
                        model = profilePictureUrl,
                        contentDescription = "Profile picture",
                        contentScale = ContentScale.Crop,
                        modifier = Modifier.fillMaxSize(),
                    )
                    else -> Icon(
                        imageVector = Icons.Outlined.Person,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.size(72.dp),
                    )
                }
            }

            Spacer(Modifier.height(24.dp))
            HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant)

            ProfilePictureAction(
                icon = Icons.Outlined.CameraAlt,
                label = if (hasPicture) "Change Photo" else "Add Photo",
                onClick = onChangePhoto,
            )

            if (hasPicture) {
                HorizontalDivider(
                    color = MaterialTheme.colorScheme.outlineVariant,
                    modifier = Modifier.padding(horizontal = 4.dp),
                )
                if (isDeleting) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 18.dp),
                        contentAlignment = Alignment.Center,
                    ) {
                        CircularProgressIndicator(modifier = Modifier.size(24.dp), strokeWidth = 2.dp)
                    }
                } else {
                    ProfilePictureAction(
                        icon = Icons.Outlined.Delete,
                        label = "Delete Photo",
                        tint = MaterialTheme.colorScheme.error,
                        onClick = onDeletePhoto,
                    )
                }
            }

            Spacer(Modifier.height(8.dp))
        }
    }
}

@Composable
private fun ProfilePictureAction(
    icon: ImageVector,
    label: String,
    onClick: () -> Unit,
    tint: Color = MaterialTheme.colorScheme.onSurface,
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(vertical = 16.dp, horizontal = 4.dp),
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = tint,
            modifier = Modifier.size(22.dp),
        )
        Spacer(Modifier.width(16.dp))
        Text(
            text = label,
            style = MaterialTheme.typography.bodyLarge,
            color = tint,
        )
    }
}

@Composable
private fun SpecializationCard(title: String, modifier: Modifier = Modifier) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f))
            .padding(horizontal = 16.dp, vertical = 14.dp),
    ) {
        Box(
            modifier = Modifier
                .size(40.dp)
                .clip(CircleShape)
                .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.12f)),
            contentAlignment = Alignment.Center,
        ) {
            Icon(
                imageVector = Icons.Outlined.LocalHospital,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier.size(22.dp),
            )
        }
        Spacer(Modifier.width(12.dp))
        Column {
            Text(
                text = "Specialty",
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
            Text(
                text = title.ifBlank { "Not set" },
                style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Medium),
                color = MaterialTheme.colorScheme.onSurface,
            )
        }
    }
}

@Composable
private fun PersonalInfoHeader(
    onBack: () -> Unit,
    onPhotoClick: () -> Unit,
    profilePictureBytes: ByteArray?,
    profilePictureUrl: String?,
    isUploading: Boolean,
    modifier: Modifier = Modifier,
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .wrapContentHeight()
            .background(brush = Brush.horizontalGradient(colors = listOf(GradientStart, GradientEnd)))
            .statusBarsPadding(),
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.fillMaxWidth()) {
            Box(modifier = Modifier.fillMaxWidth()) {
                IconButton(onClick = onBack) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Outlined.ArrowBack,
                        contentDescription = "Back",
                        tint = Color.White,
                    )
                }
                Text(
                    text = "Personal Information",
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.SemiBold),
                    color = Color.White,
                    modifier = Modifier.align(Alignment.Center),
                )
            }
            Spacer(Modifier.height(12.dp))
            Box(contentAlignment = Alignment.BottomEnd) {
                Box(
                    modifier = Modifier
                        .size(72.dp)
                        .clip(CircleShape)
                        .background(Color.White.copy(alpha = 0.25f))
                        .clickable(onClick = onPhotoClick),
                    contentAlignment = Alignment.Center,
                ) {
                    when {
                        isUploading -> CircularProgressIndicator(
                            color = Color.White,
                            modifier = Modifier.size(32.dp),
                            strokeWidth = 2.dp,
                        )
                        profilePictureBytes != null -> AsyncImage(
                            model = profilePictureBytes,
                            contentDescription = "Profile picture",
                            contentScale = ContentScale.Crop,
                            modifier = Modifier.fillMaxSize().clip(CircleShape),
                        )
                        profilePictureUrl != null -> AsyncImage(
                            model = profilePictureUrl,
                            contentDescription = "Profile picture",
                            contentScale = ContentScale.Crop,
                            modifier = Modifier.fillMaxSize().clip(CircleShape),
                        )
                        else -> Icon(
                            imageVector = Icons.Outlined.Person,
                            contentDescription = null,
                            tint = Color.White,
                            modifier = Modifier.size(44.dp),
                        )
                    }
                }
                Box(
                    modifier = Modifier
                        .size(24.dp)
                        .clip(CircleShape)
                        .background(Primary40)
                        .clickable(onClick = onPhotoClick),
                    contentAlignment = Alignment.Center,
                ) {
                    Icon(
                        imageVector = Icons.Outlined.CameraAlt,
                        contentDescription = "Change photo",
                        tint = Color.White,
                        modifier = Modifier.size(14.dp),
                    )
                }
            }
            Spacer(Modifier.height(16.dp))
        }
    }
}
