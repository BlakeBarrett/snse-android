package com.blakebarrett.snse.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(entities = arrayOf(Sentiment::class), version = 1)
abstract class AppDatabase : RoomDatabase() {

    abstract fun sentimentDao(): SentimentDAO

    companion object {

        @Volatile
        private var instance: AppDatabase? = null

        fun getInstance(context: Context): AppDatabase {
            if (instance == null) {
                instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "Sentiments.db"
                )
                    .allowMainThreadQueries()
                    .build()
            }
            return instance!!
        }
    }
}
