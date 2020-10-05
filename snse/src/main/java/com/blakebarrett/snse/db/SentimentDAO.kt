package com.blakebarrett.snse.db

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query

@Dao
interface SentimentDAO {
    @Query("SELECT * FROM Sentiment ORDER BY timestamp DESC")
    fun getAll(): List<Sentiment>

    @Query("SELECT * FROM Sentiment WHERE timestamp LIKE :timestamp LIMIT 1")
    fun findByTimestamp(timestamp: Long): Sentiment

    @Insert
    fun insert(vararg sentiment: Sentiment)

    @Delete
    fun delete(sentiment: Sentiment)
}
