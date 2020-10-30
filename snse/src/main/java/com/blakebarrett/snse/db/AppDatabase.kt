package com.blakebarrett.snse.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(entities = [Sentiment::class], version = 1)
abstract class AppDatabase : RoomDatabase() {

    abstract fun sentimentDao(): SentimentDAO

    companion object {

        private val filename = "Sentiments.db"

        @Volatile
        private var instance: AppDatabase? = null

        fun getInstance(context: Context): AppDatabase {
            instance?.let {
                return it
            }
            Room.databaseBuilder(
                context.applicationContext,
                AppDatabase::class.java,
                filename
            )
                .allowMainThreadQueries()
                .build().apply {
                    instance = this
                    return this
                }
        }
    }
}
