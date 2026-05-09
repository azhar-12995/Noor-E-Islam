package com.azhar.noor_e_islam.data.remote.firebase

import android.app.NotificationManager
import android.content.Context
import com.azhar.noor_e_islam.R
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import dagger.hilt.android.AndroidEntryPoint
import timber.log.Timber

/** Receives push notifications and posts them via NotificationManager. */
@AndroidEntryPoint
class FcmService : FirebaseMessagingService() {

    override fun onNewToken(token: String) {
        Timber.d("FCM new token: $token")
        // TODO: persist token to Firestore/users collection.
    }

    override fun onMessageReceived(message: RemoteMessage) {
        val title = message.notification?.title ?: message.data["title"] ?: getString(R.string.app_name)
        val body  = message.notification?.body  ?: message.data["body"]  ?: ""
        showNotification(title, body)
    }

    private fun showNotification(title: String, body: String) {
        val channelId = getString(R.string.default_notification_channel_id)
        val notif = androidx.core.app.NotificationCompat.Builder(this, channelId)
            .setSmallIcon(R.mipmap.ic_launcher)
            .setContentTitle(title)
            .setContentText(body)
            .setStyle(androidx.core.app.NotificationCompat.BigTextStyle().bigText(body))
            .setAutoCancel(true)
            .build()
        val nm = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        nm.notify((System.currentTimeMillis() % Int.MAX_VALUE).toInt(), notif)
    }
}

