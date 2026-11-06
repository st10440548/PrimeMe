package com.primeme.app.notifications

import android.Manifest
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat

/** Immediate local notifications after a successful goal API operation. */
object GoalNotifications {
    private const val CHANNEL_ID = "goal_updates"

    fun show(context: Context, title: String, message: String) {
        val preferences = context.getSharedPreferences("primeme_settings", Context.MODE_PRIVATE)
        if (!preferences.getBoolean("reminders_enabled", true)) return

        val manager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        manager.createNotificationChannel(
            NotificationChannel(CHANNEL_ID, "Goal updates", NotificationManager.IMPORTANCE_DEFAULT)
        )

        if (Build.VERSION.SDK_INT >= 33 &&
            context.checkSelfPermission(Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED
        ) return

        val notification = NotificationCompat.Builder(context, CHANNEL_ID)
            .setSmallIcon(com.primeme.app.R.mipmap.ic_launcher)
            .setContentTitle(title)
            .setContentText(message)
            .setAutoCancel(true)
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .build()

        NotificationManagerCompat.from(context).notify(
            (System.currentTimeMillis() % Int.MAX_VALUE).toInt(), notification
        )
    }
}
