package com.example.ui.screens

import androidx.compose.animation.*
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.MediaItem
import com.example.data.model.OttPlatform
import com.example.data.repository.MediaHistoryItem
import com.example.ui.theme.*
import com.example.ui.viewmodel.OttViewModel
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    viewModel: OttViewModel,
    modifier: Modifier = Modifier
) {
    val selectedPlatform = viewModel.selectedPlatform
    val searchQuery = viewModel.searchQuery
    val watchlist by viewModel.watchlist.collectAsState()
    val watchHistory by viewModel.watchHistory.collectAsState()
    val userStats by viewModel.userStats.collectAsState()

    val filteredMedia = viewModel.getFilteredCatalog()

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(CinemaDark)
            .statusBarsPadding()
    ) {
        // App Header Section
        HeaderSection(
            savings = userStats.savingsAccumulated,
            searchQuery = searchQuery,
            onQueryChange = { viewModel.updateSearchQuery(it) }
        )

        // Ad Savings Tracker Card and Quick-Ad Booster
        StatsTrackerSection(
            adsCount = userStats.adsWatchedCount,
            savings = userStats.savingsAccumulated,
            adCredits = userStats.adFreeCreditsMinutes,
            onWatchAdClick = { viewModel.watchQuickSponsorAd() }
        )

        // OTT Platform Filters Layout (Interactive Chips)
        PlatformFilters(
            selected = selectedPlatform,
            onSelect = { viewModel.updatePlatform(it) }
        )

        // Primary Content Canvas
        Box(
            modifier = Modifier
                .weight(1f)
        ) {
            if (filteredMedia.isEmpty()) {
                EmptyStateSection(searchQuery)
            } else {
                ScrollableCatalogContent(
                    filteredMedia = filteredMedia,
                    watchHistory = watchHistory,
                    watchlist = watchlist,
                    selectedPlatform = selectedPlatform,
                    onItemClick = { viewModel.showDetails(it) }
                )
            }
        }
    }
}

@Composable
fun HeaderSection(
    savings: Double,
    searchQuery: String,
    onQueryChange: (String) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(
                    text = "All in One",
                    fontSize = 28.sp,
                    fontWeight = FontWeight.Black,
                    color = Color.White,
                    letterSpacing = (-0.5).sp,
                    modifier = Modifier.testTag("app_logo_title")
                )
                Text(
                    text = "Unified FREE Stream Console",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Medium,
                    color = CinemaAccent,
                    letterSpacing = 1.sp
                )
            }

            // Zero subscription fees badge
            Surface(
                color = CinemaAccent.copy(alpha = 0.15f),
                border = BorderStroke(1.dp, CinemaAccent),
                shape = RoundedCornerShape(100.dp)
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(6.dp)
                            .background(CinemaAccent, CircleShape)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "100% FREE",
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        color = CinemaAccent
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        // Custom Search Bar
        OutlinedTextField(
            value = searchQuery,
            onValueChange = onQueryChange,
            placeholder = { Text("Search show, movies, category...", color = CinemaTextSecondary) },
            leadingIcon = { Icon(Icons.Default.Search, contentDescription = "Search icon", tint = CinemaAccent) },
            trailingIcon = {
                if (searchQuery.isNotEmpty()) {
                    IconButton(onClick = { onQueryChange("") }) {
                        Icon(Icons.Default.Clear, contentDescription = "Clear", tint = CinemaTextSecondary)
                    }
                }
            },
            singleLine = true,
            colors = OutlinedTextFieldDefaults.colors(
                focusedContainerColor = CinemaCard,
                unfocusedContainerColor = CinemaCard,
                focusedBorderColor = CinemaAccent,
                unfocusedBorderColor = Color.Transparent,
                focusedTextColor = Color.White,
                unfocusedTextColor = Color.White
            ),
            shape = RoundedCornerShape(12.dp),
            modifier = Modifier
                .fillMaxWidth()
                .testTag("search_bar")
        )
    }
}

@Composable
fun StatsTrackerSection(
    adsCount: Int,
    savings: Double,
    adCredits: Int,
    onWatchAdClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 6.dp)
            .testTag("stats_tracker_card"),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = CinemaSurface),
        border = BorderStroke(1.dp, Color(0xFF25252D))
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column(modifier = Modifier.weight(1.3f)) {
                Text(
                    text = "AD-SUPPORTED SAVINGS INDEX",
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Bold,
                    color = CinemaAccent,
                    letterSpacing = 1.sp
                )
                Spacer(modifier = Modifier.height(4.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = String.format(Locale.getDefault(), "$%.2f", savings),
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Black,
                        color = Color.White
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "SAVED THIS WEEK",
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Normal,
                        color = CinemaTextSecondary
                    )
                }
                Spacer(modifier = Modifier.height(6.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        Icons.Default.Star,
                        contentDescription = "Ad credits",
                        modifier = Modifier.size(14.dp),
                        tint = CinemaTextSecondary
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = "$adCredits min Ad-Free Credit",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Medium,
                        color = CinemaTextSecondary
                    )
                }
            }

            // Quick Sponsoring Ad Booster button to extend seamless watch
            Button(
                onClick = onWatchAdClick,
                colors = ButtonDefaults.buttonColors(containerColor = CinemaAccent.copy(alpha = 0.15f)),
                border = BorderStroke(1.dp, CinemaAccent),
                contentPadding = PaddingValues(horizontal = 12.dp, vertical = 8.dp),
                shape = RoundedCornerShape(10.dp),
                modifier = Modifier
                    .weight(0.9f)
                    .testTag("boost_ads_button")
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Icon(
                        Icons.Default.PlayArrow,
                        contentDescription = "Boost credits icon",
                        tint = CinemaAccent,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = "+30m Ad-Free",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = CinemaAccent
                    )
                }
            }
        }
    }
}

@Composable
fun PlatformFilters(
    selected: OttPlatform,
    onSelect: (OttPlatform) -> Unit
) {
    LazyRow(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 12.dp, horizontal = 16.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        items(OttPlatform.values()) { platform ->
            val isSelected = selected == platform
            val itemBg = if (isSelected) platform.primaryColor else CinemaCard
            val itemText = if (isSelected) platform.onColor else CinemaTextSecondary
            val strokeColor = if (isSelected) Color.Transparent else Color(0xFF2C2C35)

            Surface(
                modifier = Modifier
                    .clip(RoundedCornerShape(100.dp))
                    .clickable { onSelect(platform) }
                    .testTag("filter_chip_${platform.name.lowercase()}"),
                color = itemBg,
                shape = RoundedCornerShape(100.dp),
                border = BorderStroke(1.dp, strokeColor)
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 14.dp, vertical = 8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    if (platform != OttPlatform.ALL) {
                        Box(
                            modifier = Modifier
                                .size(18.dp)
                                .background(platform.primaryColor, CircleShape)
                                .border(1.dp, Color.White.copy(alpha = 0.4f), CircleShape),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = platform.logoText.take(1),
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Black,
                                color = platform.onColor
                            )
                        }
                        Spacer(modifier = Modifier.width(6.dp))
                    }
                    Text(
                        text = platform.displayName,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = itemText
                    )
                }
            }
        }
    }
}

@Composable
fun ScrollableCatalogContent(
    filteredMedia: List<MediaItem>,
    watchHistory: List<MediaHistoryItem>,
    watchlist: List<MediaItem>,
    selectedPlatform: OttPlatform,
    onItemClick: (MediaItem) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
    ) {
        // Continue Watching Row (Room-backed!)
        if (watchHistory.isNotEmpty()) {
            Text(
                text = "Continue Watching",
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White,
                modifier = Modifier.padding(start = 16.dp, top = 8.dp, bottom = 8.dp)
            )
            LazyRow(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 16.dp),
                contentPadding = PaddingValues(horizontal = 16.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(watchHistory) { historyItem ->
                    ContinueWatchingCard(
                        historyItem = historyItem,
                        onClick = { onItemClick(historyItem.mediaItem) }
                    )
                }
            }
        }

        // Custom Advertisement Sponsor Banner Inline ad
        SponsorBannerAd()

        // Selected Platform Title / Spotlight Row
        val headingText = if (selectedPlatform == OttPlatform.ALL) "Spotlight Streams" else "${selectedPlatform.displayName} Exclusives"
        Text(
            text = headingText,
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold,
            color = Color.White,
            modifier = Modifier.padding(start = 16.dp, top = 8.dp, bottom = 8.dp)
        )

        LazyRow(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 16.dp),
            contentPadding = PaddingValues(horizontal = 16.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            items(filteredMedia.filter { it.isTrending }) { media ->
                MediaPosterCard(media = media, isTrending = true, onClick = { onItemClick(media) })
            }
        }

        // Dynamic watchlist shelf
        if (watchlist.isNotEmpty()) {
            Text(
                text = "My Unified Watchlist",
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White,
                modifier = Modifier.padding(start = 16.dp, top = 8.dp, bottom = 8.dp)
            )
            LazyRow(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 16.dp),
                contentPadding = PaddingValues(horizontal = 16.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(watchlist) { media ->
                    MediaPosterCard(media = media, onClick = { onItemClick(media) })
                }
            }
        }

        // Expanded Explorer Grid Block
        Text(
            text = "Browse Free Catalog",
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold,
            color = Color.White,
            modifier = Modifier.padding(start = 16.dp, top = 12.dp, bottom = 12.dp)
        )

        // We show all filtered media here nicely
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(start = 16.dp, end = 16.dp, bottom = 80.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            filteredMedia.chunked(2).forEach { rowItems ->
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    rowItems.forEach { media ->
                        Box(modifier = Modifier.weight(1f)) {
                            MediaPosterCard(
                                media = media,
                                isGridItem = true,
                                onClick = { onItemClick(media) }
                            )
                        }
                    }
                    if (rowItems.size == 1) {
                        Spacer(modifier = Modifier.weight(1f))
                    }
                }
            }
        }
    }
}

@Composable
fun MediaPosterCard(
    media: MediaItem,
    isTrending: Boolean = false,
    isGridItem: Boolean = false,
    onClick: () -> Unit
) {
    val heightDp = if (isGridItem) 170.dp else 220.dp
    val baseModifier = if (isGridItem) Modifier.fillMaxWidth() else Modifier.width(150.dp)

    Card(
        modifier = baseModifier
            .height(heightDp)
            .clickable { onClick() }
            .testTag("media_card_${media.id}"),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = CinemaCard),
        border = BorderStroke(1.dp, Color(0xFF2A2A32))
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.verticalGradient(
                        colors = media.gradientColors,
                        startY = 0f,
                        endY = Float.POSITIVE_INFINITY
                    )
                )
        ) {
            // OTT watermark indicator
            Box(
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .padding(8.dp)
                    .background(media.platform.primaryColor, RoundedCornerShape(4.dp))
                    .padding(horizontal = 6.dp, vertical = 2.dp)
            ) {
                Text(
                    text = media.platform.logoText,
                    color = media.platform.onColor,
                    fontSize = 8.sp,
                    fontWeight = FontWeight.Black
                )
            }

            // Central visual art drawing representing theater feel
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(top = 30.dp, bottom = 45.dp),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.PlayArrow,
                    contentDescription = null,
                    tint = Color.White.copy(alpha = 0.35f),
                    modifier = Modifier.size(48.dp)
                )
            }

            // Bottom title banner overlay
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(
                        Brush.verticalGradient(
                            colors = listOf(Color.Transparent, Color.Black.copy(alpha = 0.9f)),
                            startY = 0f
                        )
                    )
                    .align(Alignment.BottomCenter)
                    .padding(10.dp)
            ) {
                Column {
                    Text(
                        text = media.title,
                        color = Color.White,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = media.category,
                            color = CinemaTextSecondary,
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Normal
                        )
                        Text(
                            text = media.rating,
                            color = CinemaAccent,
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }

            // Trending indicator label
            if (isTrending) {
                Box(
                    modifier = Modifier
                        .align(Alignment.TopStart)
                        .padding(8.dp)
                        .background(Color.White, RoundedCornerShape(100.dp))
                        .padding(horizontal = 6.dp, vertical = 2.dp)
                ) {
                    Text(
                        text = "🔥 LIVE",
                        color = Color.Black,
                        fontSize = 8.sp,
                        fontWeight = FontWeight.Black
                    )
                }
            }
        }
    }
}

@Composable
fun ContinueWatchingCard(
    historyItem: MediaHistoryItem,
    onClick: () -> Unit
) {
    val media = historyItem.mediaItem
    Card(
        modifier = Modifier
            .width(220.dp)
            .height(130.dp)
            .clickable { onClick() }
            .testTag("history_card_${media.id}"),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = CinemaCard),
        border = BorderStroke(1.dp, Color(0xFF2C2C35))
    ) {
        Column(
            modifier = Modifier.fillMaxSize()
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
                    .background(
                        Brush.horizontalGradient(
                            colors = media.gradientColors
                        )
                    )
            ) {
                Box(
                    modifier = Modifier
                        .align(Alignment.TopStart)
                        .padding(8.dp)
                        .background(media.platform.primaryColor, RoundedCornerShape(4.dp))
                        .padding(horizontal = 6.dp, vertical = 2.dp)
                ) {
                    Text(
                        text = media.platform.logoText,
                        color = media.platform.onColor,
                        fontSize = 8.sp,
                        fontWeight = FontWeight.Bold
                    )
                }

                // Mini play circle
                Box(
                    modifier = Modifier
                        .align(Alignment.Center)
                        .size(32.dp)
                        .background(Color.Black.copy(alpha = 0.6f), CircleShape)
                        .border(1.dp, Color.White.copy(alpha = 0.5f), CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        Icons.Default.PlayArrow,
                        contentDescription = "Resume play",
                        tint = Color.White,
                        modifier = Modifier.size(16.dp)
                    )
                }
            }

            // Live progress bar tracking (Visual of ad-supported continued watch)
            LinearProgressIndicator(
                progress = historyItem.progress,
                modifier = Modifier.fillMaxWidth(),
                color = CinemaAccent,
                trackColor = Color(0xFF24242A)
            )

            // Info row
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 10.dp, vertical = 8.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = media.title,
                        color = Color.White,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                    Text(
                        text = "${(historyItem.progress * 100).toInt()}% completed",
                        color = CinemaTextSecondary,
                        fontSize = 9.sp
                    )
                }
            }
        }
    }
}

@Composable
fun SponsorBannerAd() {
    val sponsorSlogans = listOf(
        "Chola Cola: Sip the absolute refresh stream. Zero Sugar!",
        "ZapPay: The world's fastest ad-supported blockchain checkout.",
        "Aura Wearables: Track sleep quality with high-definition deep bio metrics.",
        "EcoSip Flasks: Double-insulated vacuum flask that powers green initiatives."
    )
    val randomSlogan = remember { sponsorSlogans.random() }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 12.dp)
            .testTag("inline_sponsor_ad"),
        shape = RoundedCornerShape(10.dp),
        colors = CardDefaults.cardColors(containerColor = CinemaAccent.copy(alpha = 0.05f)),
        border = BorderStroke(1.dp, CinemaAccent.copy(alpha = 0.3f))
    ) {
        Row(
            modifier = Modifier.padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .background(CinemaAccent, RoundedCornerShape(4.dp))
                    .padding(horizontal = 6.dp, vertical = 3.dp)
            ) {
                Text(
                    text = "SPONSORED",
                    color = Color.Black,
                    fontSize = 8.sp,
                    fontWeight = FontWeight.Black,
                    letterSpacing = 0.5.sp
                )
            }
            Spacer(modifier = Modifier.width(10.dp))
            Text(
                text = randomSlogan,
                color = CinemaTextPrimary,
                fontSize = 11.sp,
                fontWeight = FontWeight.Medium,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier.weight(1f)
            )
            Icon(
                Icons.Default.Info,
                contentDescription = "Ad Info",
                tint = CinemaAccent.copy(alpha = 0.6f),
                modifier = Modifier.size(16.dp)
            )
        }
    }
}

@Composable
fun EmptyStateSection(query: String) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Icon(
            imageVector = Icons.Default.Warning,
            contentDescription = "No streams loaded",
            tint = CinemaTextSecondary,
            modifier = Modifier.size(64.dp)
        )
        Spacer(modifier = Modifier.height(16.dp))
        Text(
            text = "No Streams Found",
            color = Color.White,
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold
        )
        Spacer(modifier = Modifier.height(6.dp))
        Text(
            text = "There are no shows matching '$query' on the filtered OTT platform.",
            color = CinemaTextSecondary,
            fontSize = 13.sp,
            textAlign = androidx.compose.ui.text.style.TextAlign.Center
        )
    }
}
