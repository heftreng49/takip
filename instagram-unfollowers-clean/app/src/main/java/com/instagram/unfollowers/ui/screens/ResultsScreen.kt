package com.instagram.unfollowers.ui.screens

import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.instagram.unfollowers.data.model.AnalysisResult
import com.instagram.unfollowers.data.model.InstagramUser
import com.instagram.unfollowers.ui.components.SearchBar
import com.instagram.unfollowers.ui.components.StatCard
import com.instagram.unfollowers.ui.components.UserCard
import com.instagram.unfollowers.ui.theme.*
import com.instagram.unfollowers.viewmodel.MainViewModel

private val tabs = listOf(
    Triple("Takip Etmeyenler", Icons.Default.PersonOff, InstagramPink),
    Triple("Takip Etmediklerim", Icons.Default.PersonAdd, InstagramPurple),
    Triple("Takip Ettiklerim", Icons.Default.People, GradientStart),
    Triple("Takipçilerim", Icons.Default.Favorite, InstagramOrange)
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ResultsScreen(
    result: AnalysisResult,
    viewModel: MainViewModel
) {
    val selectedTab by viewModel.selectedTab.collectAsState()
    val searchQuery by viewModel.searchQuery.collectAsState()

    val currentList = remember(selectedTab, result) {
        when (selectedTab) {
            0 -> result.unfollowers
            1 -> result.notFollowingBack
            2 -> result.following
            3 -> result.followers
            else -> emptyList()
        }
    }

    val filteredList = remember(currentList, searchQuery) {
        viewModel.filteredList(currentList)
    }

    val currentColor = tabs[selectedTab].third

    Column(modifier = Modifier.fillMaxSize()) {

        // Header summary
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    Brush.linearGradient(
                        colors = listOf(
                            currentColor.copy(alpha = 0.2f),
                            Color.Transparent
                        )
                    )
                )
                .padding(horizontal = 20.dp, vertical = 16.dp)
        ) {
            Column {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Analiz Sonucu",
                        style = MaterialTheme.typography.headlineMedium,
                        color = MaterialTheme.colorScheme.onBackground
                    )
                    IconButton(onClick = { viewModel.reset() }) {
                        Icon(
                            Icons.Default.Refresh,
                            contentDescription = "Sıfırla",
                            tint = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    StatCard(
                        label = "Takip Etmiyor",
                        value = result.unfollowers.size,
                        color = InstagramPink
                    )
                    StatCard(
                        label = "Takip Edilenler",
                        value = result.following.size,
                        color = GradientStart
                    )
                    StatCard(
                        label = "Takipçi",
                        value = result.followers.size,
                        color = InstagramOrange
                    )
                }
            }
        }

        // Scrollable Tabs
        ScrollableTabRow(
            selectedTabIndex = selectedTab,
            containerColor = MaterialTheme.colorScheme.background,
            contentColor = currentColor,
            edgePadding = 12.dp,
            divider = {
                HorizontalDivider(color = MaterialTheme.colorScheme.outline)
            }
        ) {
            tabs.forEachIndexed { index, (label, icon, color) ->
                Tab(
                    selected = selectedTab == index,
                    onClick = { viewModel.setSelectedTab(index) },
                    text = {
                        Text(
                            text = label,
                            fontWeight = if (selectedTab == index) FontWeight.Bold else FontWeight.Normal,
                            fontSize = 13.sp
                        )
                    },
                    icon = {
                        Icon(icon, contentDescription = null, modifier = Modifier.size(16.dp))
                    },
                    selectedContentColor = color,
                    unselectedContentColor = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }

        // Search + List
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp)
        ) {
            Spacer(modifier = Modifier.height(12.dp))

            SearchBar(
                query = searchQuery,
                onQueryChange = { viewModel.setSearchQuery(it) },
                placeholder = "${tabs[selectedTab].first} içinde ara..."
            )

            Spacer(modifier = Modifier.height(8.dp))

            // Count chip
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "${filteredList.size} kullanıcı",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                if (selectedTab == 0) {
                    Surface(
                        shape = RoundedCornerShape(6.dp),
                        color = InstagramPink.copy(alpha = 0.12f)
                    ) {
                        Text(
                            text = "Takip geri dönmüyor",
                            color = InstagramPink,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Medium,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            if (filteredList.isEmpty()) {
                EmptyState(
                    message = if (searchQuery.isNotEmpty()) "Arama sonucu bulunamadı"
                    else "Bu listede kullanıcı yok"
                )
            } else {
                val listState = rememberLazyListState()
                LazyColumn(
                    state = listState,
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                    contentPadding = PaddingValues(bottom = 24.dp)
                ) {
                    items(
                        items = filteredList,
                        key = { it.username }
                    ) { user ->
                        AnimatedVisibility(
                            visible = true,
                            enter = fadeIn() + slideInVertically()
                        ) {
                            UserCard(
                                user = user,
                                accentColor = currentColor,
                                badgeLabel = when (selectedTab) {
                                    0 -> "TAKİP ETMİYOR"
                                    else -> null
                                }
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun EmptyState(message: String) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 48.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Icon(
                imageVector = Icons.Default.SearchOff,
                contentDescription = null,
                modifier = Modifier.size(56.dp),
                tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.4f)
            )
            Spacer(modifier = Modifier.height(12.dp))
            Text(
                text = message,
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}
