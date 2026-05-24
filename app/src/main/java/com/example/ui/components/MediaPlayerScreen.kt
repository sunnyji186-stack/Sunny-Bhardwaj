package com.example.ui.components

import java.util.Locale
import androidx.compose.animation.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.MediaItem
import com.example.ui.theme.*
import com.example.ui.viewmodel.OttViewModel
import kotlin.math.cos
import kotlin.math.sin

@Composable
fun MediaPlayerScreen(
    viewModel: OttViewModel,
    modifier: Modifier = Modifier
) {
    val media = viewModel.activePlayingMedia ?: return
    val isShowingAd = viewModel.isShowingAdOverlay

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(Color.Black)
            .statusBarsPadding()
            .navigationBarsPadding()
    ) {
        if (isShowingAd) {
            AdOverlayLayout(
                sponsor = viewModel.currentSponsor,
                countdown = viewModel.adCountdownSeconds,
                canSkip = viewModel.canSkipAd,
                onSkip = { viewModel.skipAdAndStartPlay() }
            )
        } else {
            PlayerControlLayout(
                media = media,
                isPlaying = viewModel.isPlayerPlaying,
                progress = viewModel.playerProgress,
                isMuted = viewModel.playerMuted,
                onPlayPauseToggle = { viewModel.togglePlayPause() },
                onSeek = { viewModel.seekPlayback(it) },
                onMuteToggle = { viewModel.toggleMute() },
                onClose = { viewModel.closeMediaPlayer() }
            )
        }
    }
}

@Composable
fun AdOverlayLayout(
    sponsor: String,
    countdown: Int,
    canSkip: Boolean,
    onSkip: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    colors = listOf(Color(0xFF16161C), Color(0xFF030A16))
                )
            )
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.SpaceBetween
    ) {
        // Ad Header Info
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .background(CinemaAccent, RoundedCornerShape(4.dp))
                        .padding(horizontal = 8.dp, vertical = 4.dp)
                ) {
                    Text(
                        text = "SPONSOR AD",
                        color = Color.Black,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Black
                    )
                }
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "Allows 100% Free Streaming",
                    color = CinemaTextSecondary,
                    fontSize = 11.sp
                )
            }

            Text(
                text = "Ad 1 of 1",
                color = CinemaTextSecondary,
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold
            )
        }

        // Sponsor Poster Board Area
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 32.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(100.dp)
                    .background(CinemaAccent.copy(alpha = 0.15f), CircleShape)
                    .clip(CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.Star,
                    contentDescription = null,
                    tint = CinemaAccent,
                    modifier = Modifier.size(54.dp)
                )
            }
            Spacer(modifier = Modifier.height(24.dp))
            Text(
                text = sponsor,
                color = Color.White,
                fontSize = 22.sp,
                fontWeight = FontWeight.Black,
                textAlign = TextAlign.Center
            )
            Spacer(modifier = Modifier.height(12.dp))
            Text(
                text = "Ad-supported viewing lets you stream full Netflix, Prime, Hotstar & Jio TV highlights with NO subscription fees. Support our sponsors and keep it free!",
                color = CinemaTextSecondary,
                fontSize = 13.sp,
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(horizontal = 16.dp)
            )
        }

        // Bottom skip & Countdown Controls
        Column(
            modifier = Modifier.fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            if (canSkip) {
                Button(
                    onClick = onSkip,
                    colors = ButtonDefaults.buttonColors(containerColor = CinemaAccent),
                    shape = RoundedCornerShape(12.dp),
                    contentPadding = PaddingValues(horizontal = 32.dp, vertical = 16.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("skip_ad_button")
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Center
                    ) {
                        Text(
                            text = "Skip Ad & Start Free Stream",
                            color = Color.Black,
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Icon(
                            Icons.Default.PlayArrow,
                            contentDescription = null,
                            tint = Color.Black
                        )
                    }
                }
            } else {
                Surface(
                    color = Color.White.copy(alpha = 0.08f),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier.padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Center
                    ) {
                        CircularProgressIndicator(
                            color = CinemaAccent,
                            strokeWidth = 3.dp,
                            modifier = Modifier.size(24.dp)
                        )
                        Spacer(modifier = Modifier.width(16.dp))
                        Text(
                            text = "Stream starting in $countdown...",
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                    }
                }
            }
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = "No Credit Card Needed • Powered by Ads",
                fontSize = 10.sp,
                fontWeight = FontWeight.Medium,
                color = CinemaTextSecondary,
                letterSpacing = 1.sp
            )
        }
    }
}

@Composable
fun PlayerControlLayout(
    media: MediaItem,
    isPlaying: Boolean,
    progress: Float,
    isMuted: Boolean,
    onPlayPauseToggle: () -> Unit,
    onSeek: (Float) -> Unit,
    onMuteToggle: () -> Unit,
    onClose: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black)
            .padding(16.dp),
        verticalArrangement = Arrangement.SpaceBetween
    ) {
        // Player header bar
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(
                onClick = onClose,
                modifier = Modifier
                    .background(Color.White.copy(alpha = 0.1f), CircleShape)
                    .testTag("close_player_button")
            ) {
                Icon(Icons.Default.Close, contentDescription = "Close stream player", tint = Color.White)
            }

            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(
                    text = media.title,
                    color = Color.White,
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp
                )
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(6.dp)
                            .background(CinemaAccent, CircleShape)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "STREAM FROM ${media.platform.displayName.uppercase()}",
                        color = CinemaAccent,
                        fontWeight = FontWeight.Bold,
                        fontSize = 10.sp,
                        letterSpacing = 0.5.sp
                    )
                }
            }

            IconButton(
                onClick = onMuteToggle,
                modifier = Modifier.background(Color.White.copy(alpha = 0.1f), CircleShape)
            ) {
                Icon(
                    imageVector = if (isMuted) Icons.Default.VolumeOff else Icons.Default.VolumeUp,
                    contentDescription = "Toggle Mute",
                    tint = Color.White
                )
            }
        }

        // Custom Graphics Visualizer representing flowing/oscillating cinematic waves!
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
                .padding(vertical = 16.dp)
                .background(CinemaDark, RoundedCornerShape(16.dp))
                .clip(RoundedCornerShape(16.dp))
                .border(1.dp, Color(0xFF202028), RoundedCornerShape(16.dp)),
            contentAlignment = Alignment.Center
        ) {
            CinematicVisualizer(progress = progress, isPlaying = isPlaying)

            if (!isPlaying) {
                IconButton(
                    onClick = onPlayPauseToggle,
                    modifier = Modifier
                        .size(72.dp)
                        .background(Color.Black.copy(alpha = 0.6f), CircleShape)
                        .border(1.dp, CinemaAccent, CircleShape)
                ) {
                    Icon(
                        Icons.Default.PlayArrow,
                        contentDescription = "Resume Stream Play",
                        tint = CinemaAccent,
                        modifier = Modifier.size(40.dp)
                    )
                }
            }
        }

        // Player Controls Area
        Column(
            modifier = Modifier.fillMaxWidth()
        ) {
            // Slider / Scrubber track
            Slider(
                value = progress,
                onValueChange = onSeek,
                valueRange = 0.0f..1.0f,
                colors = SliderDefaults.colors(
                    thumbColor = CinemaAccent,
                    activeTrackColor = CinemaAccent,
                    inactiveTrackColor = Color(0xFF2E2E3A)
                ),
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("player_scrubber")
            )

            // Timeline text indicators
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                val totalSeconds = 7200L // Simulated 2 hours
                val currentSeconds = (progress * totalSeconds).toLong()
                
                Text(
                    text = formatTimeline(currentSeconds),
                    color = CinemaTextSecondary,
                    fontSize = 11.sp
                )
                Text(
                    text = formatTimeline(totalSeconds),
                    color = CinemaTextSecondary,
                    fontSize = 11.sp
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Playback controls row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(
                    onClick = { onSeek((progress - 0.05f).coerceAtLeast(0.0f)) },
                    modifier = Modifier.size(54.dp)
                ) {
                    Icon(Icons.Default.Refresh, contentDescription = "Rewind 10s", tint = Color.White, modifier = Modifier.size(28.dp))
                }

                Spacer(modifier = Modifier.width(24.dp))

                IconButton(
                    onClick = onPlayPauseToggle,
                    modifier = Modifier
                        .size(68.dp)
                        .background(CinemaAccent, CircleShape)
                        .testTag("player_play_pause")
                ) {
                    Icon(
                        imageVector = if (isPlaying) Icons.Default.Pause else Icons.Default.PlayArrow,
                        contentDescription = "Play input",
                        tint = Color.Black,
                        modifier = Modifier.size(32.dp)
                    )
                }

                Spacer(modifier = Modifier.width(24.dp))

                IconButton(
                    onClick = { onSeek((progress + 0.05f).coerceAtMost(1.0f)) },
                    modifier = Modifier.size(54.dp)
                ) {
                    Icon(
                        Icons.Default.PlayArrow, // Stand in for fast forward
                        contentDescription = "Forward 10s & Resume",
                        tint = Color.White,
                        modifier = Modifier.size(28.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))
            Text(
                text = "Ad-Supported Open stream • Free viewing sponsored by sponsor networks.",
                fontSize = 9.sp,
                textAlign = TextAlign.Center,
                color = CinemaTextSecondary,
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}

@Composable
fun CinematicVisualizer(progress: Float, isPlaying: Boolean) {
    Canvas(modifier = Modifier.fillMaxSize()) {
        val width = size.width
        val height = size.height
        val center = Offset(width / 2f, height / 2f)

        // Draw deep cinematic space feel
        drawRect(
            color = Color(0xFF060608),
            size = size
        )

        // Render animated orbital ring structure reacting to progress
        val radiusMultiplier = 140.dp.toPx()
        val numPoints = 80
        val rotationOffset = progress * 360f

        // Draw atmospheric grid mesh representation
        for (i in 1..4) {
            val ringRadius = radiusMultiplier * (i * 0.4f)
            drawCircle(
                color = Color(0xFF00FFCC).copy(alpha = 0.03f * (5 - i)),
                radius = ringRadius,
                center = center,
                style = Stroke(width = 1.dp.toPx())
            )
        }

        // Draw cosmic wave coordinates
        val points = mutableListOf<Offset>()
        for (i in 0 until numPoints) {
            val theta = (i.toFloat() / numPoints) * 2 * Math.PI
            // Dynamic oscillation offset simulating streaming signal
            val angleDeg = Math.toDegrees(theta) + rotationOffset
            val wave = (sin(Math.toRadians(angleDeg * 5)) * 12.dp.toPx()).toFloat()
            
            val r = radiusMultiplier + wave
            val x = (center.x + r * cos(theta)).toFloat()
            val y = (center.y + r * sin(theta)).toFloat()
            points.add(Offset(x, y))
        }

        // Draw custom connecting paths
        for (i in 0 until points.size) {
            val p1 = points[i]
            val p2 = points[(i + 1) % points.size]
            drawLine(
                color = CinemaAccent.copy(alpha = if (isPlaying) 0.8f else 0.3f),
                start = p1,
                end = p2,
                strokeWidth = 2.dp.toPx()
            )
        }

        // Concentric neon target pulses
        drawCircle(
            color = CinemaAccent.copy(alpha = 0.1f * progress),
            radius = 30.dp.toPx(),
            center = center
        )
    }
}

fun formatTimeline(secondsPassed: Long): String {
    val hrs = secondsPassed / 3600
    val mins = (secondsPassed % 3600) / 60
    val secs = secondsPassed % 60
    return if (hrs > 0) {
        String.format(Locale.getDefault(), "%02d:%02d:%02d", hrs, mins, secs)
    } else {
        String.format(Locale.getDefault(), "%02d:%02d", mins, secs)
    }
}
