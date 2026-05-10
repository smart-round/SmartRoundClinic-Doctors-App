package ke.co.smartroundclinic.doctor.presentation.main.profile.ui

import androidx.compose.foundation.Image
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
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.outlined.ArrowBack
import androidx.compose.material.icons.automirrored.outlined.ExitToApp
import androidx.compose.material.icons.outlined.CalendarMonth
import androidx.compose.material.icons.outlined.ChevronRight
import androidx.compose.material.icons.outlined.FiberNew
import androidx.compose.material.icons.automirrored.outlined.HelpOutline
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.outlined.Lock
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage
import ke.co.smartroundclinic.doctor.presentation.main.profile.PersonalInfoViewModel
import ke.co.smartroundclinic.doctor.presentation.main.profile.destinations.ProfileList
import ke.co.smartroundclinic.doctor.presentation.theme.Error40
import ke.co.smartroundclinic.doctor.presentation.theme.GradientEnd
import ke.co.smartroundclinic.doctor.presentation.theme.GradientStart
import ke.co.smartroundclinic.doctor.presentation.theme.Primary40
import ke.co.smartroundclinic.doctor.presentation.theme.Primary90
import ke.co.smartroundclinic.doctor.presentation.theme.SmartRoundTheme
import ke.co.smartroundclinic.doctor.presentation.theme.smartRoundColors
import org.koin.compose.viewmodel.koinViewModel

@Composable
internal fun ProfileListScreen(
    onBack: () -> Unit  = {},
    onPersonalInfo: () -> Unit  = {},
    onBankingDetails: () -> Unit = {},
    onSecuritySettings: () -> Unit = {},
    onSupport: () -> Unit  = {},
    onScheduleManagement: () -> Unit  = {},
    onSpecialization: () -> Unit = {},
    onSignOut: () -> Unit = {},
    viewModel: PersonalInfoViewModel = koinViewModel(),
    modifier: Modifier = Modifier,
) {
    val user by viewModel.user.collectAsState()
    Scaffold(
        modifier = modifier
            .systemBarsPadding(),
        contentWindowInsets = WindowInsets(0),
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(horizontal = 16.dp, vertical = 16.dp)
                .verticalScroll(rememberScrollState()),
        ) {
            ProfileHeader(
                email = user?.email ?: "",
                fullName = user?.fullName ?: "",
                profilePicture = user?.profilePicture,
                onBack = onBack
            )

            Spacer(Modifier.height(8.dp))

            ProfileSection {
                ProfileMenuItem(
                    icon = Icons.Outlined.Person,
                    label = "Personal Information",
                    onClick = onPersonalInfo,
                )
                HorizontalDivider(modifier = Modifier.padding(start = 56.dp), thickness = 0.5.dp, color = MaterialTheme.colorScheme.outlineVariant)
                ProfileMenuItem(
                    icon = Icons.Outlined.Lock,
                    label = "Specialization",
                    onClick = onSpecialization,
                )
                HorizontalDivider(modifier = Modifier.padding(start = 56.dp), thickness = 0.5.dp, color = MaterialTheme.colorScheme.outlineVariant)
                ProfileMenuItem(
                    icon = Icons.Outlined.Lock,
                    label = "Banking Details",
                    onClick = onBankingDetails,
                )
                HorizontalDivider(modifier = Modifier.padding(start = 56.dp), thickness = 0.5.dp, color = MaterialTheme.colorScheme.outlineVariant)
                ProfileMenuItem(
                    icon = Icons.Outlined.CalendarMonth,
                    label = "Schedule Management",
                    onClick = onScheduleManagement,
                )
                HorizontalDivider(modifier = Modifier.padding(start = 56.dp), thickness = 0.5.dp, color = MaterialTheme.colorScheme.outlineVariant)
                ProfileMenuItem(
                    icon = Icons.Outlined.Lock,
                    label = "Update Password",
                    onClick = onSecuritySettings,
                )
            }


            Spacer(Modifier.height(12.dp))

            ProfileSectionHeader(title = "Help")

            ProfileSection {
                ProfileMenuItem(
                    icon = Icons.AutoMirrored.Outlined.HelpOutline,
                    label = "Support",
                    onClick = onSupport,
                )
                HorizontalDivider(modifier = Modifier.padding(start = 56.dp), thickness = 0.5.dp, color = MaterialTheme.colorScheme.outlineVariant)
                ProfileMenuItem(
                    icon = Icons.Outlined.FiberNew,
                    label = "What's New",
                    onClick = {},
                )
            }

            Spacer(Modifier.height(12.dp))

            ProfileSection {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable(
                            indication = null,
                            interactionSource = remember { MutableInteractionSource() },
                            onClick = { viewModel.signOut(onSignOut) },
                        )
                        .padding(horizontal = 16.dp, vertical = 14.dp),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Outlined.ExitToApp,
                        contentDescription = null,
                        tint = Error40,
                        modifier = Modifier.size(22.dp),
                    )
                    Spacer(Modifier.width(16.dp))
                    Text(
                        text = "Sign Out",
                        style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Medium),
                        color = Error40,
                        modifier = Modifier.weight(1f),
                    )
                }
            }

            Spacer(Modifier.height(24.dp))
        }
    }
}

@Composable
private fun ProfileHeader(
    email: String,
    fullName: String,
    profilePicture: String? = null,
    onBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = Modifier.fillMaxWidth()
            .padding(bottom = 16.dp)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Start,
            modifier = Modifier.padding(bottom = 16.dp)
        ) {
            IconButton(
                onClick = onBack
            ){
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = "Go Back"
                )
            }
            Spacer(modifier = Modifier.width(16.dp))
            Text(
                text = "Profile",
                style = MaterialTheme.typography.titleMedium
            )

        }
        Box(
            modifier = Modifier
                .wrapContentSize()
                .background(
                    shape = MaterialTheme.shapes.medium,
                    brush = Brush.verticalGradient(
                        listOf(MaterialTheme.smartRoundColors.gradientStart,
                            MaterialTheme.smartRoundColors.gradientEnd
                        )
                    )
                )
        ){
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Start,
                modifier = Modifier.fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical =16.dp )
                    .align(Alignment.Center)
            ) {
                Box(
                    modifier = Modifier
                        .size(80.dp)
                        .clip(CircleShape)
                        .background(MaterialTheme.colorScheme.surface),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Person,
                        contentDescription = null,
                        tint = Color.Gray,
                        modifier = Modifier.size(40.dp)
                    )
                    AsyncImage(
                        model = profilePicture,
                        contentScale = ContentScale.Crop,
                        contentDescription = "Profile Picture",
                        modifier = Modifier.fillMaxSize()
                    )
                }
                Column(
                    verticalArrangement = Arrangement.SpaceBetween,
                    modifier = Modifier.fillMaxWidth()
                        .padding(start = 16.dp)
                ) {
                    Text(
                        text = "Dr $fullName",
                        style = MaterialTheme.typography.titleLarge,
                        color = MaterialTheme.colorScheme.onPrimary,
                        modifier = Modifier.padding(bottom = 8.dp)

                    )
                    Text(
                        text = email,
                        style = MaterialTheme.typography.titleSmall,
                        color = MaterialTheme.colorScheme.onPrimary,
                        modifier = Modifier.padding(bottom = 8.dp)
                    )
                }
            }
        }
    }
}

@Composable
private fun ProfileSection(
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit,
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .background(shape = MaterialTheme.shapes.small, color = MaterialTheme.colorScheme.surface),
    ) {
        content()
    }
}

@Composable
private fun ProfileSectionHeader(title: String, modifier: Modifier = Modifier) {
    Text(
        text = title,
        style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.SemiBold),
        color = MaterialTheme.colorScheme.onSurfaceVariant,
        modifier = modifier.padding(horizontal = 16.dp, vertical = 6.dp),
    )
}

@Composable
private fun ProfileMenuItem(
    icon: ImageVector,
    label: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .clickable(
                indication = null,
                interactionSource = remember { MutableInteractionSource() },
                onClick = onClick,
            )
            .padding(horizontal = 16.dp, vertical = 14.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Box(
            modifier = Modifier
                .size(36.dp)
                .clip(CircleShape)
                .background(Primary90),
            contentAlignment = Alignment.Center,
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = Primary40,
                modifier = Modifier.size(20.dp),
            )
        }
        Spacer(Modifier.width(16.dp))
        Text(
            text = label,
            style = MaterialTheme.typography.bodyMedium,
            modifier = Modifier.weight(1f),
        )
        Icon(
            imageVector = Icons.Outlined.ChevronRight,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.size(20.dp),
        )
    }
}


@Preview
@Composable
private fun ProfileListPreview(){
    SmartRoundTheme {
        ProfileListScreen()
    }
}