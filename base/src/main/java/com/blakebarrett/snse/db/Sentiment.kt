package com.blakebarrett.snse.db

import android.text.format.DateUtils
import androidx.annotation.NonNull
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
public data class Sentiment (
    @PrimaryKey
    @NonNull
    val timestamp: Long,
    val feeling: String, val intensity: Int, val color: String, val water: Boolean, val elaborate: String) {

    fun prettyDate(): String {
        val epoch = timestamp * 1000
        return DateUtils.formatSameDayTime(
            epoch,
            System.currentTimeMillis(),
            java.text.DateFormat.SHORT,
            java.text.DateFormat.SHORT
        ).toString()
    }
}
