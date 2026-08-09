package ke.co.smartroundclinic.doctor.presentation.main.articles.ui

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
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.outlined.Image
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
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
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil3.compose.AsyncImage
import ke.co.smartroundclinic.doctor.domain.model.Article
import ke.co.smartroundclinic.doctor.domain.model.ArticleCategory
import ke.co.smartroundclinic.doctor.domain.model.ArticleState
import ke.co.smartroundclinic.doctor.presentation.common.composables.PrimaryButton
import ke.co.smartroundclinic.doctor.presentation.main.articles.readMinutes
import ke.co.smartroundclinic.doctor.presentation.main.profile.PersonalInfoViewModel
import ke.co.smartroundclinic.doctor.presentation.theme.GradientEnd
import ke.co.smartroundclinic.doctor.presentation.theme.GradientStart
import ke.co.smartroundclinic.doctor.presentation.theme.Neutral20
import ke.co.smartroundclinic.doctor.presentation.theme.Neutral60
import ke.co.smartroundclinic.doctor.presentation.theme.Primary40
import ke.co.smartroundclinic.doctor.presentation.theme.ShapePill
import ke.co.smartroundclinic.doctor.presentation.theme.StatusPublished
import ke.co.smartroundclinic.doctor.presentation.theme.StatusSuspended
import kotlinx.datetime.Instant
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import org.jetbrains.compose.resources.painterResource
import org.koin.compose.viewmodel.koinViewModel
import smartroundclinic.composeapp.generated.resources.Res
import smartroundclinic.composeapp.generated.resources.bottom_bar_cost_articles

// ── Figma geometry (414pt frame) ─────────────────────────────────────────────
private val CardShape = RoundedCornerShape(12.dp)
private val CardHeight = 129.dp
private val CardThumbWidth = 97.dp
private val TabRowHeight = 50.dp
private val ChipHeight = 21.dp

/** Card fill — #E84E1C21 from the spec (brand orange at 0x21 alpha). */
private val CardBackground = Color(0x21E84E1C)

internal enum class ArticlesTab(val label: String) {
    MY_ARTICLES("My Articles"),
    OTHER_ARTICLES("Other Articles"),
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun ArticleListScreen(
    myArticles: List<Article>,
    liveArticles: List<Article>,
    isLoadingMine: Boolean,
    isLoadingLive: Boolean,
    hasLoadedMine: Boolean,
    hasLoadedLive: Boolean,
    selectedTab: ArticlesTab,
    categories: List<ArticleCategory>,
    isSearching: Boolean,
    onSearchingChange: (Boolean) -> Unit,
    onTabSelected: (ArticlesTab) -> Unit,
    onWriteArticle: () -> Unit,
    onArticleClick: (Article) -> Unit,
    onRefresh: () -> Unit,
    onProfileClick: () -> Unit = {},
    onNotificationsClick: () -> Unit = {},
    modifier: Modifier = Modifier,
    profileViewModel: PersonalInfoViewModel = koinViewModel(),
) {
    val isMyTab = selectedTab == ArticlesTab.MY_ARTICLES
    var selectedCategoryId by remember { mutableStateOf<String?>(null) }
    var query by remember { mutableStateOf("") }

    LaunchedEffect(isSearching) { if (!isSearching) query = "" }

    val user by profileViewModel.user.collectAsState()
    val doctorName = user?.fullName?.trim() ?: ""

    LaunchedEffect(selectedTab) { if (isMyTab) selectedCategoryId = null }

    val articles = if (isMyTab) myArticles else liveArticles
    val filteredArticles = remember(articles, selectedCategoryId, isMyTab, query) {
        articles
            .filter { isMyTab || selectedCategoryId == null || it.categoryId == selectedCategoryId }
            .filter { article ->
                query.isBlank() ||
                    article.title.contains(query, ignoreCase = true) ||
                    article.summary.contains(query, ignoreCase = true) ||
                    (article.authorName?.contains(query, ignoreCase = true) == true)
            }
    }
    val isLoading = if (isMyTab) isLoadingMine else isLoadingLive
    val hasLoaded = if (isMyTab) hasLoadedMine else hasLoadedLive

    // Both tabs share one list, so without this a scrolled "My Articles" leaves "Other Articles"
    // opening halfway down its own list, with the first card clipped under the filter chips.
    val listState = rememberLazyListState()
    LaunchedEffect(selectedTab, selectedCategoryId) { listState.scrollToItem(0) }

    Scaffold(
        modifier = modifier,
        containerColor = Color.White,
        topBar = {
            Column(modifier = Modifier.background(Color.White)) {
                ArticlesHeader(
                    title = if (isSearching) null else "Articles",
                    onProfileClick = onProfileClick,
                    onNotificationsClick = onNotificationsClick,
                    onSearchClick = { onSearchingChange(!isSearching) },
                )

                if (isSearching) {
                    ArticleSearchField(
                        query = query,
                        onQueryChange = { query = it },
                        onClose = { onSearchingChange(false) },
                    )
                }

                Spacer(Modifier.height(8.dp))

                SegmentedTabRow(selectedTab = selectedTab, onTabSelected = onTabSelected)

                if (!isMyTab && categories.isNotEmpty()) {
                    Spacer(Modifier.height(4.dp))
                    CategoryFilterRow(
                        categories = categories,
                        selectedCategoryId = selectedCategoryId,
                        onCategorySelected = { selectedCategoryId = it },
                    )
                }
            }
        },
        floatingActionButton = {
            if (isMyTab) {
                FloatingActionButton(
                    onClick = onWriteArticle,
                    containerColor = Primary40,
                    shape = CircleShape,
                ) {
                    Icon(
                        imageVector = Icons.Filled.Edit,
                        contentDescription = "Write Article",
                        tint = Color.White,
                    )
                }
            }
        },
        contentWindowInsets = WindowInsets(0),
    ) { paddingValues ->
        PullToRefreshBox(
            isRefreshing = isLoading,
            onRefresh = onRefresh,
            modifier = Modifier.fillMaxSize().padding(paddingValues),
        ) {
            when {
                !hasLoaded && filteredArticles.isEmpty() -> {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        CircularProgressIndicator(color = Primary40)
                    }
                }

                hasLoaded && filteredArticles.isEmpty() -> {
                    EmptyArticlesView(
                        isMyTab = isMyTab,
                        isFiltered = query.isNotBlank() || selectedCategoryId != null,
                        onWriteArticle = onWriteArticle,
                        modifier = Modifier.fillMaxSize(),
                    )
                }

                else -> {
                    LazyColumn(
                        state = listState,
                        modifier = Modifier.fillMaxSize(),
                        contentPadding = PaddingValues(
                            start = ArticlesGutter,
                            end = ArticlesGutter,
                            top = 22.dp,
                            bottom = 88.dp,
                        ),
                        verticalArrangement = Arrangement.spacedBy(10.dp),
                    ) {
                        items(filteredArticles, key = { it.id }) { article ->
                            ArticleCard(
                                article = article,
                                isOwn = isMyTab,
                                authorName = article.authorName ?: (if (isMyTab) doctorName else ""),
                                onClick = { onArticleClick(article) },
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun ArticleSearchField(
    query: String,
    onQueryChange: (String) -> Unit,
    onClose: () -> Unit,
) {
    val focusRequester = remember { FocusRequester() }
    LaunchedEffect(Unit) { focusRequester.requestFocus() }

    OutlinedTextField(
        value = query,
        onValueChange = onQueryChange,
        placeholder = { Text("Search articles", style = MaterialTheme.typography.bodyMedium, color = Neutral60) },
        singleLine = true,
        shape = CardShape,
        keyboardOptions = KeyboardOptions(imeAction = ImeAction.Search),
        trailingIcon = {
            Icon(
                imageVector = Icons.Filled.Close,
                contentDescription = "Close search",
                tint = Neutral60,
                modifier = Modifier
                    .size(20.dp)
                    .clickable(
                        indication = null,
                        interactionSource = remember { MutableInteractionSource() },
                        onClick = onClose,
                    ),
            )
        },
        colors = OutlinedTextFieldDefaults.colors(
            focusedBorderColor = Primary40,
            unfocusedBorderColor = Primary40,
            focusedContainerColor = Color.White,
            unfocusedContainerColor = Color.White,
        ),
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = ArticlesGutter, vertical = 12.dp)
            .focusRequester(focusRequester),
    )
}

@Composable
private fun SegmentedTabRow(
    selectedTab: ArticlesTab,
    onTabSelected: (ArticlesTab) -> Unit,
    modifier: Modifier = Modifier,
) {
    // Same segmented control the Bookings tab uses, so the two screens read as one system.
    Box(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 12.dp)
            .shadow(elevation = 4.dp, shape = ShapePill)
            .clip(ShapePill)
            .background(MaterialTheme.colorScheme.surfaceVariant)
            .padding(4.dp),
    ) {
        Row {
            ArticlesTab.entries.forEach { tab ->
                val isSelected = selectedTab == tab
                Box(
                    contentAlignment = Alignment.Center,
                    modifier = Modifier
                        .weight(1f)
                        .clip(ShapePill)
                        .then(if (isSelected) Modifier.background(Color.White) else Modifier)
                        .clickable(
                            indication = null,
                            interactionSource = remember { MutableInteractionSource() },
                            onClick = { onTabSelected(tab) },
                        )
                        .padding(vertical = 10.dp),
                ) {
                    Text(
                        text = tab.label,
                        style = MaterialTheme.typography.labelMedium,
                        fontWeight = if (isSelected) FontWeight.SemiBold else FontWeight.Normal,
                        color = if (isSelected) MaterialTheme.colorScheme.onSurface else MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                }
            }
        }
    }
}

@Composable
private fun CategoryFilterRow(
    categories: List<ArticleCategory>,
    selectedCategoryId: String?,
    onCategorySelected: (String?) -> Unit,
    modifier: Modifier = Modifier,
) {
    LazyRow(
        modifier = modifier.fillMaxWidth(),
        contentPadding = PaddingValues(horizontal = ArticlesGutter),
        horizontalArrangement = Arrangement.spacedBy(4.dp),
    ) {
        item {
            CategoryChip(
                label = "All",
                isSelected = selectedCategoryId == null,
                onClick = { onCategorySelected(null) },
            )
        }
        items(categories, key = { it.id }) { category ->
            CategoryChip(
                label = category.name,
                isSelected = selectedCategoryId == category.id,
                onClick = { onCategorySelected(category.id) },
            )
        }
    }
}

@Composable
private fun CategoryChip(label: String, isSelected: Boolean, onClick: () -> Unit) {
    val gradient = Brush.verticalGradient(listOf(GradientStart, GradientEnd))
    Box(
        contentAlignment = Alignment.Center,
        modifier = Modifier
            .height(ChipHeight)
            .clip(CircleShape)
            .then(
                if (isSelected) {
                    Modifier.background(gradient)
                } else {
                    Modifier
                        .background(Color.White)
                        .border(1.dp, Neutral20.copy(alpha = 0.17f), CircleShape)
                },
            )
            .clickable(
                indication = null,
                interactionSource = remember { MutableInteractionSource() },
                onClick = onClick,
            )
            .padding(horizontal = 14.dp),
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.bodySmall.copy(fontSize = 10.sp, letterSpacing = 0.sp),
            color = if (isSelected) Color.White else Neutral20,
            maxLines = 1,
        )
    }
}

@Composable
private fun EmptyArticlesView(
    isMyTab: Boolean,
    isFiltered: Boolean,
    onWriteArticle: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
    ) {
        Box(
            modifier = Modifier
                .size(55.dp)
                .clip(RoundedCornerShape(4.dp))
                .background(Primary40.copy(alpha = 0.14f)),
            contentAlignment = Alignment.Center,
        ) {
            Icon(
                painter = painterResource(Res.drawable.bottom_bar_cost_articles),
                contentDescription = null,
                tint = Primary40,
                modifier = Modifier.size(24.dp),
            )
        }

        Spacer(Modifier.height(20.dp))

        Text(
            text = when {
                isFiltered -> "No Matching Articles"
                isMyTab -> "No Articles Yet"
                else -> "No Published Articles"
            },
            style = MaterialTheme.typography.titleMedium.copy(
                fontWeight = FontWeight.Bold,
                fontSize = 17.sp,
            ),
            color = Neutral20,
        )

        Spacer(Modifier.height(10.dp))

        Text(
            text = when {
                isFiltered -> "Try a different search or category"
                isMyTab -> "Create your first article and share your expertise with patients"
                else -> "No articles from other doctors yet"
            },
            style = MaterialTheme.typography.bodyMedium.copy(fontSize = 13.sp),
            color = Neutral60,
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(horizontal = 40.dp),
        )

        if (isMyTab && !isFiltered) {
            Spacer(Modifier.height(46.dp))
            PrimaryButton(
                onClick = onWriteArticle,
                shape = RoundedCornerShape(14.5.dp),
                modifier = Modifier.padding(horizontal = 48.dp).height(57.dp),
            ) {
                Text(
                    text = "Write Article",
                    style = MaterialTheme.typography.labelLarge.copy(fontSize = 16.sp),
                    color = Color.White,
                )
            }
        }
    }
}

@Composable
private fun ArticleCard(
    article: Article,
    isOwn: Boolean,
    authorName: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val minutes = remember(article.content) { readMinutes(article.content) }
    val formattedDate = remember(article.datePosted, article.createdAt) {
        formatDayMonth(article.datePosted ?: article.createdAt)
    }

    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(CardHeight)
            .clip(CardShape)
            .background(CardBackground)
            .clickable(
                indication = null,
                interactionSource = remember { MutableInteractionSource() },
                onClick = onClick,
            ),
    ) {
        Row(modifier = Modifier.fillMaxSize()) {
            Column(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxHeight()
                    .padding(start = 16.dp, end = 12.dp, top = 14.dp, bottom = 12.dp),
            ) {
                Text(
                    text = buildAnnotatedString {
                        if (authorName.isNotBlank()) {
                            withStyle(SpanStyle(color = Primary40, fontWeight = FontWeight.SemiBold, fontSize = 11.sp)) {
                                append(authorName)
                            }
                            append("  ")
                        }
                        withStyle(SpanStyle(color = Neutral60, fontSize = 9.sp)) {
                            append("· $minutes min read")
                            if (formattedDate.isNotBlank()) append(" · $formattedDate")
                        }
                    },
                    style = MaterialTheme.typography.bodySmall.copy(letterSpacing = 0.sp),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                )

                Spacer(Modifier.height(8.dp))

                Text(
                    text = article.title,
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontWeight = FontWeight.Bold,
                        fontSize = 15.sp,
                        lineHeight = 20.sp,
                        letterSpacing = 0.sp,
                    ),
                    color = Neutral20,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis,
                )

                Spacer(Modifier.height(8.dp))

                Text(
                    text = article.summary,
                    style = MaterialTheme.typography.bodySmall.copy(
                        fontSize = 10.sp,
                        lineHeight = 14.sp,
                        letterSpacing = 0.sp,
                    ),
                    color = Neutral60,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis,
                )
            }

            Box(
                modifier = Modifier
                    .padding(top = 8.dp, bottom = 7.dp, end = 5.dp)
                    .width(CardThumbWidth)
                    .fillMaxHeight()
                    .clip(CardShape)
                    .background(MaterialTheme.colorScheme.surfaceVariant),
            ) {
                if (article.thumbnailUrl != null) {
                    AsyncImage(
                        model = article.thumbnailUrl,
                        contentDescription = null,
                        contentScale = ContentScale.Crop,
                        modifier = Modifier.matchParentSize(),
                    )
                } else {
                    Icon(
                        imageVector = Icons.Outlined.Image,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.size(24.dp).align(Alignment.Center),
                    )
                }

                if (isOwn) {
                    ArticleStateBadge(
                        state = article.state,
                        modifier = Modifier.align(Alignment.TopEnd).padding(top = 6.dp, end = 5.dp),
                    )
                }
            }
        }
    }
}

/** Pill over the card thumbnail — 57×13 in Figma, green when live and red when suspended. */
@Composable
internal fun ArticleStateBadge(state: ArticleState, modifier: Modifier = Modifier) {
    val (label, background) = when (state) {
        ArticleState.DRAFT -> "Draft" to Neutral60
        ArticleState.LIVE -> "Published" to StatusPublished
        ArticleState.SUSPENDED -> "Suspended" to StatusSuspended
        ArticleState.DELETED -> "Deleted" to StatusSuspended
    }

    Box(
        modifier = modifier
            .height(13.dp)
            .clip(CircleShape)
            .background(background)
            .padding(horizontal = 8.dp),
        contentAlignment = Alignment.Center,
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.bodySmall.copy(fontSize = 8.sp, letterSpacing = 0.sp),
            color = Color.White,
            maxLines = 1,
        )
    }
}

/** "20 Nov" — the short form the cards use. */
internal fun formatDayMonth(isoDate: String): String = try {
    val dateTime = Instant.parse(isoDate).toLocalDateTime(TimeZone.currentSystemDefault())
    val month = dateTime.month.name.lowercase().replaceFirstChar { it.uppercase() }.take(3)
    "${dateTime.dayOfMonth} $month"
} catch (_: Exception) {
    ""
}
