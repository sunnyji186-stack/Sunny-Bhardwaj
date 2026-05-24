package com.example.data.repository

import com.example.data.local.OttDao
import com.example.data.local.UserStatsEntity
import com.example.data.local.WatchHistoryEntity
import com.example.data.local.WatchlistItemEntity
import com.example.data.model.MediaDataProvider
import com.example.data.model.MediaItem
import com.example.data.model.OttPlatform
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

data class MediaHistoryItem(
    val mediaItem: MediaItem,
    val progress: Float,
    val lastWatchedAt: Long
)

class MediaRepository(private val dao: OttDao) {

    // Fetch static models
    fun getMediaItems(): List<MediaItem> = MediaDataProvider.items

    fun getMediaItemById(id: String): MediaItem? =
        MediaDataProvider.items.find { it.id == id }

    // Reactive Watchlist flow mapped to domain MediaItem
    fun getWatchlist(): Flow<List<MediaItem>> {
        return dao.getWatchlist().map { entities ->
            entities.mapNotNull { entity ->
                getMediaItemById(entity.mediaId)
            }
        }
    }

    suspend fun toggleWatchlist(mediaId: String, isAlreadyIn: Boolean) {
        if (isAlreadyIn) {
            dao.deleteFromWatchlist(mediaId)
        } else {
            dao.insertWatchlist(WatchlistItemEntity(mediaId = mediaId))
        }
    }

    fun isMediaInWatchlist(mediaId: String): Flow<Boolean> =
        dao.isInWatchlist(mediaId)

    // Reactive Watch History flow
    fun getWatchHistory(): Flow<List<MediaHistoryItem>> {
        return dao.getWatchHistory().map { entities ->
            entities.mapNotNull { entity ->
                val media = getMediaItemById(entity.mediaId)
                if (media != null) {
                    MediaHistoryItem(
                        mediaItem = media,
                        progress = entity.progress,
                        lastWatchedAt = entity.lastWatchedAt
                    )
                } else null
            }
        }
    }

    suspend fun saveToHistory(mediaId: String, progress: Float) {
        dao.insertWatchHistory(
            WatchHistoryEntity(
                mediaId = mediaId,
                progress = progress,
                lastWatchedAt = System.currentTimeMillis()
            )
        )
    }

    suspend fun deleteFromHistory(mediaId: String) {
        dao.deleteFromHistory(mediaId)
    }

    // User Ads Statistics & Simulated Monthly Savings
    fun getUserStatsFlow(): Flow<UserStatsEntity> {
        return dao.getUserStatsFlow().map { entity ->
            entity ?: UserStatsEntity(id = 1, adsWatchedCount = 0, savingsAccumulated = 0.0, adFreeCreditsMinutes = 0)
        }
    }

    suspend fun incrementAdsWatched(platform: OttPlatform) {
        val current = dao.getUserStatsDirect() ?: UserStatsEntity(id = 1)
        val addedSavings = platform.subscriptionFeeSavedMonthly / 30.0 // Daily/Session proportion simulated savings!
        val newCredits = current.adFreeCreditsMinutes + 5 // Add 5 minutes of premium "ad-free streaming tracker"
        
        val updated = current.copy(
            adsWatchedCount = current.adsWatchedCount + 1,
            savingsAccumulated = current.savingsAccumulated + addedSavings,
            adFreeCreditsMinutes = newCredits
        )
        dao.updateUserStats(updated)
    }

    suspend fun spendAdFreeCredits(minutes: Int) {
        val current = dao.getUserStatsDirect() ?: return
        val newCredits = (current.adFreeCreditsMinutes - minutes).coerceAtLeast(0)
        dao.updateUserStats(current.copy(adFreeCreditsMinutes = newCredits))
    }
}
