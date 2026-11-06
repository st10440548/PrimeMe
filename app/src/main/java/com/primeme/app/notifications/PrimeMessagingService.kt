package com.primeme.app.notifications

import android.util.Log
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage

/** Receives Firebase Cloud Messaging notifications. Backend token registration is pending. */
class PrimeMessagingService : FirebaseMessagingService() {
    override fun onNewToken(token: String) {
        super.onNewToken(token)
        // Do not log or store tokens until authenticated per-user backend registration exists.
        Log.i("PrimeFCM", "Messaging registration token refreshed")
    }

    override fun onMessageReceived(message: RemoteMessage) {
        super.onMessageReceived(message)
        // Only show notification payloads; do not trust remote data to trigger API writes.
        val notification = message.notification ?: return
        val title = notification.title?.take(100) ?: "PrimeX"
        val body = notification.body?.take(250) ?: return
        GoalNotifications.show(applicationContext, title, body)
    }
}
