package com.example.smartcallblocker.notification

import android.Manifest
import android.annotation.SuppressLint
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.content.ContextCompat
import com.example.smartcallblocker.R
import com.example.smartcallblocker.data.model.NotificationPreference
import java.util.concurrent.atomic.AtomicInteger

object NotificationHelper {

    const val CHANNEL_ID = "blocked_calls_channel"
    const val CHANNEL_NAME = "Blocked Calls Channel"

    private val notificationId = AtomicInteger(0)

    fun createNotificationChannel(context: Context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                CHANNEL_ID,
                CHANNEL_NAME,
                NotificationManager.IMPORTANCE_HIGH,
            ).apply {
                description = "Notifications for blocked incoming calls"
            }
            val notificationManager =
                context.getSystemService(Context.NOTIFICATION_SERVICE) as? NotificationManager
            notificationManager?.createNotificationChannel(channel)
        }
    }

    fun shouldPostNotification(
        preference: NotificationPreference,
        blockReason: String
    ): Boolean {
        return when (preference) {
            NotificationPreference.ALL -> true
            NotificationPreference.INTERNATIONAL_ONLY -> {
                (blockReason == "All International Calls (+)") || blockReason.startsWith("Country Code")
            }
            NotificationPreference.PATTERNS_ONLY -> {
                blockReason.startsWith("Prefix") || blockReason.startsWith("Suffix")
            }
            NotificationPreference.NONE -> false
        }
    }

    @SuppressLint("MissingPermission")
    fun postBlockedCallNotification(
        context: Context,
        phoneNumber: String,
        blockReason: String,
        preference: NotificationPreference
    ) {
        if (!shouldPostNotification(preference, blockReason)) {
            return
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(
                    context,
                    Manifest.permission.POST_NOTIFICATIONS
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                return
            }
        }

        createNotificationChannel(context)

        val notificationManager =
            context.getSystemService(Context.NOTIFICATION_SERVICE) as? NotificationManager
                ?: return

        val notification = NotificationCompat.Builder(context, CHANNEL_ID)
            .setSmallIcon(R.mipmap.ic_launcher)
            .setContentTitle("Call Blocked")
            .setContentText("$phoneNumber - $blockReason")
            .setStyle(
                NotificationCompat.BigTextStyle()
                    .bigText("Blocked call from $phoneNumber\nReason: $blockReason")
            )
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setDefaults(NotificationCompat.DEFAULT_ALL)
            .setAutoCancel(true)
            .build()

        val id = notificationId.incrementAndGet()
        notificationManager.notify(id, notification)
    }
}
