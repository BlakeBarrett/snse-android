package com.blakebarrett.snse.ViewModels

import android.app.Application
import androidx.lifecycle.AndroidViewModel

public class SentimentViewModel(application: Application) : AndroidViewModel(application) {

//    private val sentiment: MutableLiveData<Sentiment> by lazy {
//        MutableLiveData().also {
//            loadSentiment()
//        }
//    }

//    fun getSentiment(timeStamp: Long): LiveData<Sentiment> {
//        return sentiment
//    }

    private fun loadSentiment() {

    }
}
