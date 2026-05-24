package com.example.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "watchlist")
data class WatchlistItemEntity(
    @PrimaryKey val mediaId: String,
    val addedAt: Long = System.currentTimeMillis()
)

@Entity(tableName = "watch_history")
data class WatchHistoryEntity(
    @PrimaryKey val mediaId: String,
    val progress: Float,
    val lastWatchedAt: Long = System.currentTimeMillis()
)

@Entity(tableName = "user_stats")
data class UserStatsEntity(
    @PrimaryKey val id: Int = 1,
    val adsWatchedCount: Int = 0,
    val savingsAccumulated: Double = 0.0,
    val adFreeCreditsMinutes: Int = 0
)
