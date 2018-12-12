package com.blakebarrett.snse.utils

import android.app.IntentService
import android.content.Intent

/**
 * An [IntentService] subclass for handling asynchronous task requests in
 * a service on a separate handler thread.
 * helper methods.
 */
class NotificationService : IntentService("NotificationService") {

    override fun onHandleIntent(intent: Intent?) {
        NotificationUtils.handleIntent(this.applicationContext, intent)
    }
}
