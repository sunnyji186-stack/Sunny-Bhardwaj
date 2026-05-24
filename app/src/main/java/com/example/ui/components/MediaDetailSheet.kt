package com.example.ui.components

import androidx.compose.animation.*
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.FavoriteBorder
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.MediaItem
import com.example.ui.theme.*
import com.example.ui.viewmodel.OttViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MediaDetailSheet(
    viewModel: OttViewModel,
    modifier: Modifier = Modifier
) {
    val media = viewModel.selectedDetailsMedia ?: return
    val watchlist by viewModel.watchlist.collectAsState()
    val isFavorited = watchlist.any { it.id == media.id }

    androidx.compose.ui.window.Dialog(
        onDismissRequest = { viewModel.showDetails(null) },
        properties = androidx.compose.ui.window.DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Surface(
            shape = RoundedCornerShape(24.dp),
            color = CinemaSurface,
            border = BorderStroke(1.dp, Color(0xFF2C2C38)),
            modifier = modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
            ) {
                // Header Gradient Artwork representing backdrop image
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(180.dp)
                        .background(
                            Brush.verticalGradient(
                                colors = media.gradientColors
                            )
                        )
                ) {
                    // Close shortcut
                    IconButton(
                        onClick = { viewModel.showDetails(null) },
                        modifier = Modifier
                            .align(Alignment.TopEnd)
                            .padding(12.dp)
                            .background(Color.Black.copy(alpha = 0.5f), CircleShape)
                    ) {
                        Icon(Icons.Default.Close, contentDescription = "Close detailed sheet", tint = Color.White)
                    }

                    // Platform Watermark badge
                    Row(
                        modifier = Modifier
                            .align(Alignment.BottomStart)
                            .padding(16.dp)
                            .background(media.platform.primaryColor, RoundedCornerShape(6.dp))
                            .padding(horizontal = 10.dp, vertical = 4.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = media.platform.displayName.uppercase(),
                            color = media.platform.onColor,
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Black,
                            letterSpacing = 0.5.sp
                        )
                    }
                }

                // Metadata Details Content Column
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(20.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = media.title,
                            fontSize = 22.sp,
                            fontWeight = FontWeight.Black,
                            color = Color.White,
                            modifier = Modifier
                                .weight(1f)
                                .testTag("details_media_title")
                        )

                        Surface(
                            shape = RoundedCornerShape(4.dp),
                            color = Color.White.copy(alpha = 0.1f),
                            modifier = Modifier.padding(start = 8.dp)
                        ) {
                            Text(
                                text = media.releaseYear,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = CinemaAccent,
                                modifier = Modifier.padding(6.dp)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    // Movie tags category and stream specifics
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = media.category,
                            color = CinemaAccent,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Box(modifier = Modifier.size(4.dp).background(CinemaTextSecondary, CircleShape))
                        Text(
                            text = media.duration,
                            color = CinemaTextSecondary,
                            fontSize = 12.sp
                        )
                        Box(modifier = Modifier.size(4.dp).background(CinemaTextSecondary, CircleShape))
                        Text(
                            text = media.rating,
                            color = Color.White,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    // Description text
                    Text(
                        text = media.description,
                        color = CinemaTextSecondary,
                        fontSize = 13.sp,
                        lineHeight = 20.sp,
                        textAlign = TextAlign.Start
                    )

                    Spacer(modifier = Modifier.height(24.dp))

                    // Actions bottom row (Watchlist heart toggle + Play Neon button)
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        // Watchlist Heart/Love bookmark
                        IconButton(
                            onClick = { viewModel.toggleWatchlist(media) },
                            modifier = Modifier
                                .size(50.dp)
                                .background(Color.White.copy(alpha = 0.08f), CircleShape)
                                .testTag("details_watchlist_toggle")
                        ) {
                            Icon(
                                imageVector = if (isFavorited) Icons.Default.Favorite else Icons.Filled.FavoriteBorder,
                                contentDescription = "Add to watchlist",
                                tint = if (isFavorited) Color.Red else Color.White
                            )
                        }

                        // Stream command player launcher button
                        Button(
                            onClick = { viewModel.launchMediaPlayer(media) },
                            colors = ButtonDefaults.buttonColors(containerColor = CinemaAccent),
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier
                                .weight(1f)
                                .height(50.dp)
                                .testTag("details_play_stream_button")
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.Center
                            ) {
                                Icon(
                                    Icons.Default.PlayArrow,
                                    contentDescription = null,
                                    tint = Color.Black
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                    Text(
                                        text = "WATCH FREE",
                                        color = Color.Black,
                                        fontSize = 13.sp,
                                        fontWeight = FontWeight.Black
                                    )
                                    Text(
                                        text = "Ad-Supported Stream",
                                        color = Color.Black.copy(alpha = 0.6f),
                                        fontSize = 8.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
