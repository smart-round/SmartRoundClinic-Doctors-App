package ke.co.smartroundclinic.doctor.presentation.main.profile.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.ArrowBack
import androidx.compose.material.icons.outlined.CheckCircle
import androidx.compose.material.icons.outlined.Close
import androidx.compose.material.icons.outlined.LocalHospital
import androidx.compose.material.icons.outlined.Lock
import androidx.compose.material.icons.outlined.Search
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import ke.co.smartroundclinic.doctor.domain.model.Speciality
import ke.co.smartroundclinic.doctor.presentation.common.composables.PrimaryButton
import ke.co.smartroundclinic.doctor.presentation.main.profile.SpecializationViewModel
import ke.co.smartroundclinic.doctor.presentation.theme.GradientEnd
import ke.co.smartroundclinic.doctor.presentation.theme.GradientStart
import ke.co.smartroundclinic.doctor.presentation.theme.ShapeInput
import ke.co.smartroundclinic.doctor.presentation.theme.Tertiary40
import ke.co.smartroundclinic.doctor.presentation.theme.Tertiary90
import org.koin.compose.viewmodel.koinViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun SpecializationScreen(
    onBack: () -> Unit,
    isEnabled: Boolean,
    modifier: Modifier = Modifier,
    viewModel: SpecializationViewModel = koinViewModel(),
) {
    val specialization by viewModel.specialization.collectAsState()
    val isAccountVerified by viewModel.isAccountVerified.collectAsState()
    val catalog = viewModel.catalog
    val isSaving = viewModel.isSaving
    val isRefreshing = viewModel.isRefreshing

    var selectedSpeciality by remember { mutableStateOf<Speciality?>(null) }
    var showPicker by remember { mutableStateOf(false) }
    val pickerSheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

    // Auto-fill from cached DB
    LaunchedEffect(specialization?.id) {
        specialization?.let { spec ->
            selectedSpeciality = catalog.firstOrNull { it.id == spec.specializationId }
        }
    }

    // Re-run fill when catalog finishes loading
    LaunchedEffect(catalog.size) {
        if (selectedSpeciality == null && specialization != null) {
            selectedSpeciality = catalog.firstOrNull { it.id == specialization!!.specializationId }
        }
    }

    if (showPicker && !isAccountVerified) {
        SpecialityPickerBottomSheet(
            sheetState = pickerSheetState,
            catalog = catalog,
            onDismiss = { showPicker = false },
            onSelected = { item ->
                selectedSpeciality = item
                showPicker = false
            },
        )
    }

    Scaffold(
        modifier = modifier,
        contentWindowInsets = WindowInsets(0),
    ) { paddingValues ->
        PullToRefreshBox(
            isRefreshing = isRefreshing,
            onRefresh = { viewModel.refreshSpecialization() },
            modifier = Modifier.fillMaxSize().padding(paddingValues),
        ) {
            Column(modifier = Modifier.fillMaxSize()) {
                SpecializationHeader(onBack = onBack)

                Column(
                    modifier = Modifier
                        .weight(1f)
                        .verticalScroll(rememberScrollState())
                        .padding(horizontal = 20.dp, vertical = 20.dp),
                ) {
                    // Verified banner
                    if (isAccountVerified) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(MaterialTheme.shapes.small)
                                .background(Tertiary90)
                                .padding(horizontal = 14.dp, vertical = 12.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(10.dp),
                        ) {
                            Icon(
                                imageVector = Icons.Outlined.CheckCircle,
                                contentDescription = null,
                                tint = Tertiary40,
                                modifier = Modifier.size(20.dp),
                            )
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = "Account Verified",
                                    style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.SemiBold),
                                    color = Tertiary40,
                                )
                                Text(
                                    text = "Your specialization is locked and cannot be changed after verification.",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = Tertiary40.copy(alpha = 0.8f),
                                )
                            }
                        }
                        Spacer(Modifier.height(20.dp))
                    }

                    Text(
                        text = "Your Specialization",
                        style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.SemiBold),
                        color = MaterialTheme.colorScheme.onSurface,
                    )
                    Spacer(Modifier.height(4.dp))
                    Text(
                        text = if (isAccountVerified)
                            "Your verified specialization is shown below."
                        else
                            "Select the medical specialization that best describes your expertise.",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                    Spacer(Modifier.height(16.dp))

                    // Picker field — only tappable when not verified
                    if (!isAccountVerified) {
                        OutlinedTextField(
                            value = selectedSpeciality?.title ?: "",
                            onValueChange = {},
                            readOnly = true,
                            label = { Text("Specialization", style = MaterialTheme.typography.bodySmall) },
                            trailingIcon = {
                                Icon(
                                    imageVector = Icons.Outlined.Search,
                                    contentDescription = "Select specialization",
                                    tint = MaterialTheme.colorScheme.onSurfaceVariant,
                                )
                            },
                            shape = ShapeInput,
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable(
                                    indication = null,
                                    interactionSource = remember { MutableInteractionSource() },
                                ) { showPicker = true },
                            enabled = false,
                        )
                        Spacer(Modifier.height(12.dp))
                    }

                    // Speciality detail card
                    if (selectedSpeciality != null) {
                        SpecialityCard(
                            speciality = selectedSpeciality!!,
                            isLocked = isAccountVerified,
                        )
                    } else if (isAccountVerified) {
                        // Verified but no specialization set
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(MaterialTheme.shapes.medium)
                                .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f))
                                .padding(24.dp),
                            contentAlignment = Alignment.Center,
                        ) {
                            Text(
                                text = "No specialization on record",
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                            )
                        }
                    }

                    // Save button — hidden when verified
                    if (!isAccountVerified) {
                        Spacer(Modifier.height(24.dp))
                        PrimaryButton(

                            onClick = {
                                val id = selectedSpeciality?.id ?: return@PrimaryButton
                                viewModel.saveSpecialization(id)
                            },
                            enabled = (selectedSpeciality != null && !isSaving) && isEnabled,
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

                    Spacer(Modifier.height(16.dp))
                }
            }
        }
    }
}

@Composable
private fun SpecialityCard(
    speciality: Speciality,
    isLocked: Boolean,
    modifier: Modifier = Modifier,
) {
    val accentColor = remember(speciality.color) {
        runCatching {
            val hex = speciality.color.trimStart('#')
            val r = hex.substring(0, 2).toInt(16) / 255f
            val g = hex.substring(2, 4).toInt(16) / 255f
            val b = hex.substring(4, 6).toInt(16) / 255f
            Color(r, g, b)
        }.getOrElse { Color(0xFF6750A4) }
    }

    Row(
        modifier = modifier
            .fillMaxWidth()
            .clip(MaterialTheme.shapes.medium)
            .background(accentColor.copy(alpha = 0.07f))
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(14.dp),
    ) {
        // Colored avatar with first letter
        Box(
            modifier = Modifier
                .size(52.dp)
                .clip(CircleShape)
                .background(accentColor.copy(alpha = 0.15f)),
            contentAlignment = Alignment.Center,
        ) {
            Text(
                text = speciality.title.first().uppercaseChar().toString(),
                style = MaterialTheme.typography.headlineSmall.copy(fontWeight = FontWeight.Bold),
                color = accentColor,
            )
        }

        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = speciality.title,
                style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.SemiBold),
                color = MaterialTheme.colorScheme.onSurface,
            )
            Spacer(Modifier.height(3.dp))
            Text(
                text = speciality.description,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                maxLines = 3,
                overflow = androidx.compose.ui.text.style.TextOverflow.Ellipsis,
            )
        }

        if (isLocked) {
            Icon(
                imageVector = Icons.Outlined.Lock,
                contentDescription = "Locked",
                tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f),
                modifier = Modifier.size(18.dp),
            )
        } else {
            // Color dot accent
            Box(
                modifier = Modifier
                    .size(10.dp)
                    .clip(CircleShape)
                    .background(accentColor),
            )
        }
    }
}

@Composable
private fun SpecializationHeader(onBack: () -> Unit, modifier: Modifier = Modifier) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .wrapContentHeight()
            .background(brush = Brush.horizontalGradient(colors = listOf(GradientStart, GradientEnd)))
            .statusBarsPadding(),
    ) {
        Box(modifier = Modifier.fillMaxWidth()) {
            IconButton(onClick = onBack) {
                Icon(
                    imageVector = Icons.AutoMirrored.Outlined.ArrowBack,
                    contentDescription = "Back",
                    tint = Color.White,
                )
            }
            Text(
                text = "Specialization",
                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.SemiBold),
                color = Color.White,
                modifier = Modifier
                    .align(Alignment.Center)
                    .padding(vertical = 16.dp),
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun SpecialityPickerBottomSheet(
    sheetState: androidx.compose.material3.SheetState,
    catalog: List<Speciality>,
    onDismiss: () -> Unit,
    onSelected: (Speciality) -> Unit,
) {
    var query by remember { mutableStateOf("") }
    val filtered = remember(query, catalog) {
        if (query.isBlank()) catalog
        else catalog.filter { it.title.contains(query, ignoreCase = true) || it.description.contains(query, ignoreCase = true) }
    }

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        containerColor = MaterialTheme.colorScheme.surface,
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .navigationBarsPadding(),
        ) {
            // Header
            Column(modifier = Modifier.padding(horizontal = 20.dp)) {
                Text(
                    text = "Select Specialization",
                    style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
                )
                Spacer(Modifier.height(2.dp))
                Text(
                    text = if (query.isBlank()) "${catalog.size} specializations available"
                           else "${filtered.size} result${if (filtered.size == 1) "" else "s"}",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
                Spacer(Modifier.height(16.dp))
                OutlinedTextField(
                    value = query,
                    onValueChange = { query = it },
                    placeholder = { Text("Search specializations…", style = MaterialTheme.typography.bodyMedium) },
                    leadingIcon = {
                        Icon(
                            Icons.Outlined.Search,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.onSurfaceVariant,
                        )
                    },
                    trailingIcon = if (query.isNotBlank()) {
                        {
                            IconButton(onClick = { query = "" }) {
                                Icon(
                                    Icons.Outlined.Close,
                                    contentDescription = "Clear",
                                    tint = MaterialTheme.colorScheme.onSurfaceVariant,
                                    modifier = Modifier.size(18.dp),
                                )
                            }
                        }
                    } else null,
                    shape = ShapeInput,
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth(),
                )
                Spacer(Modifier.height(12.dp))
            }

            HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant, thickness = 0.5.dp)

            if (filtered.isEmpty()) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 48.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                ) {
                    Icon(
                        imageVector = Icons.Outlined.LocalHospital,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.size(40.dp),
                    )
                    Text(
                        text = "No specializations found",
                        style = MaterialTheme.typography.titleSmall,
                        color = MaterialTheme.colorScheme.onSurface,
                    )
                    Text(
                        text = "Try a different search term",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                }
            } else {
                LazyColumn {
                    items(filtered, key = { it.id }) { item ->
                        val accentColor = remember(item.color) {
                            runCatching {
                                val hex = item.color.trimStart('#')
                                val r = hex.substring(0, 2).toInt(16) / 255f
                                val g = hex.substring(2, 4).toInt(16) / 255f
                                val b = hex.substring(4, 6).toInt(16) / 255f
                                Color(r, g, b)
                            }.getOrElse { Color.Gray }
                        }

                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable(
                                    indication = null,
                                    interactionSource = remember { MutableInteractionSource() },
                                    onClick = { onSelected(item) },
                                )
                                .padding(horizontal = 20.dp, vertical = 12.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(14.dp),
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(46.dp)
                                    .clip(CircleShape)
                                    .background(accentColor.copy(alpha = 0.12f)),
                                contentAlignment = Alignment.Center,
                            ) {
                                Text(
                                    text = item.title.first().uppercaseChar().toString(),
                                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                                    color = accentColor,
                                )
                            }
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = item.title,
                                    style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.SemiBold),
                                    color = MaterialTheme.colorScheme.onSurface,
                                )
                                Spacer(Modifier.height(2.dp))
                                Text(
                                    text = item.description,
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                                    maxLines = 2,
                                    overflow = androidx.compose.ui.text.style.TextOverflow.Ellipsis,
                                )
                            }
                            Box(
                                modifier = Modifier
                                    .size(8.dp)
                                    .clip(CircleShape)
                                    .background(accentColor),
                            )
                        }
                        HorizontalDivider(
                            color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f),
                            thickness = 0.5.dp,
                            modifier = Modifier.padding(start = 80.dp, end = 20.dp),
                        )
                    }
                }
            }
        }
    }
}
