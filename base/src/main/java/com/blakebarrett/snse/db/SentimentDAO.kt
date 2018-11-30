package com.blakebarrett.snse.db

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query

@Dao
public interface SentimentDAO {
    @Query("SELECT * FROM Sentiment")
    fun getAll(): List<Sentiment>

    @Query("SELECT * FROM Sentiment WHERE timestamp LIKE :timestamp LIMIT 1")
    fun findByTimestamp(timestamp: Long): Sentiment

    @Insert
    fun insert(vararg sentiment: Sentiment)

    @Delete
    fun delete(sentiment: Sentiment)
}
