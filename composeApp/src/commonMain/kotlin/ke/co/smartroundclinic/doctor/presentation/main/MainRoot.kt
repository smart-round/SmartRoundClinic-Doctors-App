package ke.co.smartroundclinic.doctor.presentation.main

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Article
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.Chat
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.outlined.Article
import androidx.compose.material.icons.outlined.CalendarMonth
import androidx.compose.material.icons.outlined.ChatBubbleOutline
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.retain.retain
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import androidx.navigation3.runtime.NavKey
import androidx.navigation3.runtime.entryProvider
import androidx.navigation3.ui.NavDisplay
import ke.co.smartroundclinic.doctor.presentation.main.destinations.Articles
import ke.co.smartroundclinic.doctor.presentation.main.destinations.Bookings
import ke.co.smartroundclinic.doctor.presentation.main.destinations.Chat
import ke.co.smartroundclinic.doctor.presentation.main.destinations.Home
import ke.co.smartroundclinic.doctor.presentation.main.ui.ArticlesScreen
import ke.co.smartroundclinic.doctor.presentation.main.ui.BookingsScreen
import ke.co.smartroundclinic.doctor.presentation.main.ui.ChatScreen
import ke.co.smartroundclinic.doctor.presentation.main.ui.HomeScreen

private data class BottomTab(
    val destination: NavKey,
    val label: String,
    val selectedIcon: ImageVector,
    val unselectedIcon: ImageVector,
)

private val tabs = listOf(
    BottomTab(Home, "Home", Icons.Filled.Home, Icons.Outlined.Home),
    BottomTab(Bookings, "Bookings", Icons.Filled.CalendarMonth, Icons.Outlined.CalendarMonth),
    BottomTab(Articles, "Articles", Icons.Filled.Article, Icons.Outlined.Article),
    BottomTab(Chat, "Chat", Icons.Filled.Chat, Icons.Outlined.ChatBubbleOutline),
)

@Composable
fun MainRoot(modifier: Modifier = Modifier) {
    val backStack = retain { mutableStateListOf<NavKey>(Home) }
    val currentTab = backStack.lastOrNull() ?: Home

    fun selectTab(dest: NavKey) {
        if (currentTab != dest) {
            backStack.clear()
            backStack.add(dest)
        }
    }

    Scaffold(
        modifier = modifier.fillMaxSize(),
        bottomBar = {
            BottomNavBar(
                currentTab = currentTab,
                onTabSelected = ::selectTab,
            )
        },
    ) { paddingValues ->
        NavDisplay(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues),
            backStack = backStack,
            onBack = {
                if (currentTab != Home) {
                    backStack.clear()
                    backStack.add(Home)
                }
            },
            entryProvider = entryProvider {
                entry<Home> { HomeScreen() }
                entry<Bookings> { BookingsScreen() }
                entry<Articles> { ArticlesScreen() }
                entry<Chat> { ChatScreen() }
            },
        )
    }
}

@Composable
private fun BottomNavBar(
    currentTab: NavKey,
    onTabSelected: (NavKey) -> Unit,
) {
    Column {
        HorizontalDivider(thickness = 0.5.dp, color = MaterialTheme.colorScheme.outlineVariant)
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(MaterialTheme.colorScheme.surface)
                .navigationBarsPadding(),
        ) {
            tabs.forEach { tab ->
                val isSelected = currentTab::class == tab.destination::class
                BottomNavItem(
                    tab = tab,
                    isSelected = isSelected,
                    onSelected = { onTabSelected(tab.destination) },
                    modifier = Modifier.weight(1f),
                )
            }
        }
    }
}

@Composable
private fun BottomNavItem(
    tab: BottomTab,
    isSelected: Boolean,
    onSelected: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val iconColor by animateColorAsState(
        targetValue = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant,
        animationSpec = tween(200),
        label = "tabIconColor",
    )
    val labelColor by animateColorAsState(
        targetValue = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant,
        animationSpec = tween(200),
        label = "tabLabelColor",
    )

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = modifier
            .clickable(
                indication = null,
                interactionSource = remember { MutableInteractionSource() },
                onClick = onSelected,
            )
            .padding(bottom = 10.dp),
    ) {
        // Active indicator bar at the very top
        Box(
            modifier = Modifier
                .width(32.dp)
                .height(3.dp)
                .clip(RoundedCornerShape(bottomStart = 4.dp, bottomEnd = 4.dp))
                .background(
                    if (isSelected) MaterialTheme.colorScheme.primary
                    else MaterialTheme.colorScheme.surface
                ),
        )

        Spacer(Modifier.height(6.dp))

        Icon(
            imageVector = if (isSelected) tab.selectedIcon else tab.unselectedIcon,
            contentDescription = tab.label,
            tint = iconColor,
            modifier = Modifier.size(24.dp),
        )

        Spacer(Modifier.height(2.dp))

        Text(
            text = tab.label,
            style = MaterialTheme.typography.labelSmall,
            color = labelColor,
        )
    }
}
