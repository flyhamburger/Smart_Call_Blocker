package com.example.smartcallblocker.data.model

data class AppSettings(
    val blockAllInternational: Boolean = false,
    val blockPrivateCallers: Boolean = false,
    val notificationPreference: NotificationPreference = NotificationPreference.ALL
)
