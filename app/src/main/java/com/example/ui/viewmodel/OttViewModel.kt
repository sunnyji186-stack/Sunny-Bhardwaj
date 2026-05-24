package com.example.ui.viewmodel

import android.app.Application
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.local.OttDatabase
import com.example.data.local.UserStatsEntity
import com.example.data.model.MediaDataProvider
import com.example.data.model.MediaItem
import com.example.data.model.OttPlatform
import com.example.data.repository.MediaHistoryItem
import com.example.data.repository.MediaRepository
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class OttViewModel(application: Application) : AndroidViewModel(application) {

    private val database = OttDatabase.getInstance(application)
    private val repository = MediaRepository(database.dao)

    var selectedPlatform by mutableStateOf(OttPlatform.ALL)
        private set

    var searchQuery by mutableStateOf("")
        private set

    // Detailed peek sheet media selection
    var selectedDetailsMedia by mutableStateOf<MediaItem?>(null)
        private set

    // Media player active simulation state
    var activePlayingMedia by mutableStateOf<MediaItem?>(null)
        private set

    var isPlayerPlaying by mutableStateOf(false)
        private set

    var playerProgress by mutableStateOf(0.0f)
        private set

    var playerMuted by mutableStateOf(false)
        private set

    // Ad overlay stream mechanics
    var isShowingAdOverlay by mutableStateOf(false)
        private set

    var adCountdownSeconds by mutableStateOf(5)
        private set

    var canSkipAd by mutableStateOf(false)
        private set

    private var adTimerJob: Job? = null
    private var playbackJob: Job? = null

    // Sponsor details for current ad
    val sponsors = listOf(
        "EcoRide Vehicles (Electric Mobility)",
        "QuickCola Energy (Stay Refreshed)",
        "SkyCap Insurance (Safeguard Your Hopes)",
        "ZetaPay Crypto-Shield (Safe Payments)",
        "HarvestBites Organic Bars (Green Energy)"
    )
    var currentSponsor by mutableStateOf(sponsors[0])
        private set

    // Lists with Room persistence
    val watchlist: StateFlow<List<MediaItem>> = repository.getWatchlist()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    val watchHistory: StateFlow<List<MediaHistoryItem>> = repository.getWatchHistory()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    val userStats: StateFlow<UserStatsEntity> = repository.getUserStatsFlow()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = UserStatsEntity()
        )

    // Exposed Catalog matching active filters
    fun getFilteredCatalog(): List<MediaItem> {
        return MediaDataProvider.items.filter { item ->
            val matchesPlatform = (selectedPlatform == OttPlatform.ALL || item.platform == selectedPlatform)
            val matchesQuery = item.title.contains(searchQuery, ignoreCase = true) ||
                    item.category.contains(searchQuery, ignoreCase = true)
            matchesPlatform && matchesQuery
        }
    }

    // Setters
    fun updatePlatform(platform: OttPlatform) {
        selectedPlatform = platform
    }

    fun updateSearchQuery(query: String) {
        searchQuery = query
    }

    fun showDetails(media: MediaItem?) {
        selectedDetailsMedia = media
    }

    // Watchlist Interaction
    fun toggleWatchlist(media: MediaItem) {
        viewModelScope.launch {
            val contains = watchlist.value.any { it.id == media.id }
            repository.toggleWatchlist(media.id, contains)
        }
    }

    // Playback and Advertisement Engine
    fun launchMediaPlayer(media: MediaItem) {
        viewModelScope.launch {
            selectedDetailsMedia = null // Close sheet if open
            activePlayingMedia = media
            isPlayerPlaying = false
            playerProgress = 0.0f
            
            // Randomize sponsor for the ad
            currentSponsor = sponsors.random()
            
            // Check if user has ad-free credits
            val stats = userStats.value
            if (stats.adFreeCreditsMinutes > 0) {
                // Skip the ad! Spend 1 min credit
                repository.spendAdFreeCredits(1)
                isShowingAdOverlay = false
                startMediaPlayback(media)
            } else {
                // Play standard ad
                isShowingAdOverlay = true
                adCountdownSeconds = 5
                canSkipAd = false
                startAdTimer(media)
            }
        }
    }

    private fun startAdTimer(media: MediaItem) {
        adTimerJob?.cancel()
        adTimerJob = viewModelScope.launch {
            while (adCountdownSeconds > 0) {
                delay(1000)
                adCountdownSeconds--
            }
            canSkipAd = true
            // Support auto-advance or skip prompt
            delay(1000)
            skipAdAndStartPlay()
        }
    }

    fun skipAdAndStartPlay() {
        adTimerJob?.cancel()
        isShowingAdOverlay = false
        val media = activePlayingMedia
        if (media == null) {
            // Voluntary sponsor booster ad has finished or skipped. Reward is saved by ad timer job.
            return
        }
        
        viewModelScope.launch {
            repository.incrementAdsWatched(media.platform)
            startMediaPlayback(media)
        }
    }

    private fun launchPlaybackLoop(media: MediaItem) {
        playbackJob?.cancel()
        playbackJob = viewModelScope.launch {
            var lastSaveTime = System.currentTimeMillis()
            while (isPlayerPlaying && playerProgress < 1.0f) {
                delay(100)
                playerProgress += 0.005f // Incremental playback
                
                val now = System.currentTimeMillis()
                if (now - lastSaveTime >= 3000) { // Throttle save to database every 3 seconds
                    lastSaveTime = now
                    repository.saveToHistory(media.id, playerProgress)
                }
            }
            if (playerProgress >= 1.0f) {
                playerProgress = 1.0f
                isPlayerPlaying = false
                repository.saveToHistory(media.id, 1.0f)
            }
        }
    }

    private fun startMediaPlayback(media: MediaItem) {
        isPlayerPlaying = true
        
        // Find existing history progress to resume at
        val existing = watchHistory.value.find { it.mediaItem.id == media.id }
        playerProgress = existing?.progress ?: 0.0f

        launchPlaybackLoop(media)
    }

    fun togglePlayPause() {
        isPlayerPlaying = !isPlayerPlaying
        val media = activePlayingMedia ?: return
        
        if (isPlayerPlaying) {
            launchPlaybackLoop(media)
        } else {
            playbackJob?.cancel()
            viewModelScope.launch {
                repository.saveToHistory(media.id, playerProgress)
            }
        }
    }

    fun seekPlayback(targetProgress: Float) {
        playerProgress = targetProgress.coerceIn(0.0f, 1.0f)
        val media = activePlayingMedia ?: return
        viewModelScope.launch {
            repository.saveToHistory(media.id, playerProgress)
        }
    }

    fun toggleMute() {
        playerMuted = !playerMuted
    }

    fun closeMediaPlayer() {
        playbackJob?.cancel()
        adTimerJob?.cancel()
        
        val media = activePlayingMedia
        val progress = playerProgress
        if (media != null) {
            viewModelScope.launch {
                repository.saveToHistory(media.id, progress)
            }
        }
        
        activePlayingMedia = null
        isPlayerPlaying = false
        isShowingAdOverlay = false
    }

    fun clearWatchHistoryItem(mediaId: String) {
        viewModelScope.launch {
            repository.deleteFromHistory(mediaId)
        }
    }

    fun watchQuickSponsorAd() {
        if (isShowingAdOverlay) return
        
        adTimerJob?.cancel()
        playbackJob?.cancel()
        
        isShowingAdOverlay = true
        currentSponsor = "Global Partner Brand Network"
        adCountdownSeconds = 3
        canSkipAd = false
        
        adTimerJob = viewModelScope.launch {
            while (adCountdownSeconds > 0) {
                delay(1000)
                adCountdownSeconds--
            }
            canSkipAd = true
            delay(800)
            isShowingAdOverlay = false
            // Earn 30 credits!
            val currentStats = userStats.value
            val bonusSavings = 3.99 // Simulated bonus savings value
            database.dao.updateUserStats(
                currentStats.copy(
                    adsWatchedCount = currentStats.adsWatchedCount + 1,
                    savingsAccumulated = currentStats.savingsAccumulated + bonusSavings,
                    adFreeCreditsMinutes = currentStats.adFreeCreditsMinutes + 30
                )
            )
        }
    }
}
