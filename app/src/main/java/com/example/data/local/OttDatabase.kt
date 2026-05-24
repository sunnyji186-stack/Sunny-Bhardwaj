package com.example.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(
    entities = [WatchlistItemEntity::class, WatchHistoryEntity::class, UserStatsEntity::class],
    version = 1,
    exportSchema = false
)
abstract class OttDatabase : RoomDatabase() {
    abstract val dao: OttDao

    companion object {
        @Volatile
        private var INSTANCE: OttDatabase? = null

        fun getInstance(context: Context): OttDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    OttDatabase::class.java,
                    "all_in_one_ott.db"
                ).build()
                INSTANCE = instance
                instance
            }
        }
    }
}
