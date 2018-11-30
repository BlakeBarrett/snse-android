package com.blakebarrett.snse.db

import androidx.annotation.NonNull
import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.*

@Entity
public data class Sentiment (
    @PrimaryKey
    @NonNull
    val timestamp: Long,
    val feeling: String, val intensity: Int, val color: String, val water: Boolean, val elaborate: String) {

    fun prettyDate(): String {
        return Date(timestamp * 1000).toString()
    }
}
