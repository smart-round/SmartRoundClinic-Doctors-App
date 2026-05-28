package ke.co.smartroundclinic.doctor.presentation.main.profile.ui

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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.ArrowBack
import androidx.compose.material.icons.outlined.Add
import androidx.compose.material.icons.outlined.Close
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import ke.co.smartroundclinic.doctor.presentation.common.composables.PrimaryButton
import ke.co.smartroundclinic.doctor.presentation.main.profile.PersonalInfoViewModel
import ke.co.smartroundclinic.doctor.presentation.theme.ShapeInput
import org.koin.compose.viewmodel.koinViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun ProfileScreen(
    onBack: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: PersonalInfoViewModel = koinViewModel(),
) {
    var title by remember { mutableStateOf("") }
    var bio by remember { mutableStateOf("") }
    var kmpdcRegNumber by remember { mutableStateOf("") }
    var yearsOfExperience by remember { mutableStateOf("") }
    var facilityName by remember { mutableStateOf("") }
    val languages = remember { mutableStateListOf<String>() }
    var languageInput by remember { mutableStateOf("") }

    val isSaving = viewModel.isSavingProfile

    // Pre-fill once the profile loads
    LaunchedEffect(viewModel.profileBio, viewModel.profileTitle) {
        if (title.isBlank()) title = viewModel.profileTitle ?: ""
        if (bio.isBlank()) bio = viewModel.profileBio ?: ""
        if (kmpdcRegNumber.isBlank()) kmpdcRegNumber = viewModel.profileKmpdcRegNumber ?: ""
        if (yearsOfExperience.isBlank()) yearsOfExperience = viewModel.profileYearsOfExperience?.toString() ?: ""
        if (facilityName.isBlank()) facilityName = viewModel.profileFacilityName ?: ""
        if (languages.isEmpty()) {
            languages.clear()
            languages.addAll(viewModel.profileLanguages)
        }
    }

    Scaffold(
        modifier = modifier,
        contentWindowInsets = WindowInsets(0),
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Profile",
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.SemiBold),
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Outlined.ArrowBack,
                            contentDescription = "Back",
                        )
                    }
                },
            )
        },
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 20.dp, vertical = 16.dp)
                .navigationBarsPadding(),
        ) {
            SectionLabel("Professional Details")
            Spacer(Modifier.height(12.dp))

            OutlinedTextField(
                value = title,
                onValueChange = { title = it },
                label = { Text("Title", style = MaterialTheme.typography.bodySmall) },
                placeholder = { Text("e.g. Dr.", style = MaterialTheme.typography.bodySmall) },
                shape = ShapeInput,
                singleLine = true,
                modifier = Modifier.fillMaxWidth(),
            )
            Spacer(Modifier.height(12.dp))

            OutlinedTextField(
                value = kmpdcRegNumber,
                onValueChange = { kmpdcRegNumber = it },
                label = { Text("KMPDC Reg. Number", style = MaterialTheme.typography.bodySmall) },
                placeholder = { Text("e.g. KMPDC/2015/12345", style = MaterialTheme.typography.bodySmall) },
                shape = ShapeInput,
                singleLine = true,
                modifier = Modifier.fillMaxWidth(),
            )
            Spacer(Modifier.height(12.dp))

            OutlinedTextField(
                value = yearsOfExperience,
                onValueChange = { if (it.all(Char::isDigit) && it.length <= 2) yearsOfExperience = it },
                label = { Text("Years of Experience", style = MaterialTheme.typography.bodySmall) },
                placeholder = { Text("e.g. 8", style = MaterialTheme.typography.bodySmall) },
                shape = ShapeInput,
                singleLine = true,
                modifier = Modifier.fillMaxWidth(),
            )
            Spacer(Modifier.height(12.dp))

            OutlinedTextField(
                value = facilityName,
                onValueChange = { facilityName = it },
                label = { Text("Facility Name", style = MaterialTheme.typography.bodySmall) },
                placeholder = { Text("e.g. Nairobi Medical Centre", style = MaterialTheme.typography.bodySmall) },
                shape = ShapeInput,
                singleLine = true,
                modifier = Modifier.fillMaxWidth(),
            )

            Spacer(Modifier.height(20.dp))
            SectionLabel("Languages Spoken")
            Spacer(Modifier.height(12.dp))

            Row(verticalAlignment = Alignment.CenterVertically) {
                OutlinedTextField(
                    value = languageInput,
                    onValueChange = { languageInput = it },
                    label = { Text("Add language", style = MaterialTheme.typography.bodySmall) },
                    shape = ShapeInput,
                    singleLine = true,
                    modifier = Modifier.weight(1f),
                )
                Spacer(Modifier.width(8.dp))
                IconButton(
                    onClick = {
                        val lang = languageInput.trim()
                        if (lang.isNotBlank() && !languages.contains(lang)) {
                            languages.add(lang)
                        }
                        languageInput = ""
                    },
                ) {
                    Icon(
                        imageVector = Icons.Outlined.Add,
                        contentDescription = "Add language",
                        tint = MaterialTheme.colorScheme.primary,
                    )
                }
            }

            if (languages.isNotEmpty()) {
                Spacer(Modifier.height(8.dp))
                androidx.compose.foundation.layout.FlowRow(horizontalArrangement = androidx.compose.foundation.layout.Arrangement.spacedBy(8.dp)) {
                    languages.forEach { lang ->
                        FilterChip(
                            selected = true,
                            onClick = { languages.remove(lang) },
                            label = { Text(lang, style = MaterialTheme.typography.labelMedium) },
                            trailingIcon = {
                                Icon(
                                    imageVector = Icons.Outlined.Close,
                                    contentDescription = "Remove $lang",
                                    modifier = Modifier.size(14.dp),
                                )
                            },
                            shape = RoundedCornerShape(20.dp),
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = MaterialTheme.colorScheme.primaryContainer,
                                selectedLabelColor = MaterialTheme.colorScheme.onPrimaryContainer,
                                selectedTrailingIconColor = MaterialTheme.colorScheme.onPrimaryContainer,
                            ),
                        )
                    }
                }
            }

            Spacer(Modifier.height(20.dp))
            SectionLabel("Bio")
            Spacer(Modifier.height(12.dp))

            OutlinedTextField(
                value = bio,
                onValueChange = { if (it.length <= 500) bio = it },
                label = { Text("Bio", style = MaterialTheme.typography.bodySmall) },
                placeholder = {
                    Text(
                        text = "Tell patients about your experience, specialties, and approach to care…",
                        style = MaterialTheme.typography.bodySmall,
                    )
                },
                shape = ShapeInput,
                minLines = 5,
                maxLines = 10,
                modifier = Modifier.fillMaxWidth(),
                supportingText = {
                    Text(
                        text = "${bio.length}/500",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.fillMaxWidth(),
                        textAlign = TextAlign.End,
                    )
                },
            )

            Spacer(Modifier.height(24.dp))
            PrimaryButton(
                onClick = {
                    viewModel.updateDoctorProfile(
                        title = title,
                        bio = bio,
                        kmpdcRegNumber = kmpdcRegNumber,
                        yearsOfExperience = yearsOfExperience,
                        languages = languages.toList(),
                        facilityName = facilityName,
                    )
                },
                enabled = !isSaving,
            ) {
                if (isSaving) {
                    CircularProgressIndicator(
                        color = Color.White,
                        modifier = Modifier.size(20.dp),
                        strokeWidth = 2.dp,
                    )
                } else {
                    Text(
                        text = "Save Profile",
                        style = MaterialTheme.typography.labelLarge,
                        color = Color.White,
                        modifier = Modifier.padding(vertical = 14.dp),
                    )
                }
            }
            Spacer(Modifier.height(8.dp))
        }
    }
}

@Composable
private fun SectionLabel(text: String) {
    Text(
        text = text,
        style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.SemiBold),
        color = MaterialTheme.colorScheme.onSurface,
    )
}
