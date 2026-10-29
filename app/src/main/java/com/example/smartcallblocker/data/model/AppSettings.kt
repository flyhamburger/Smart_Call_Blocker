package com.example.smartcallblocker.data.model

data class AppSettings(
    val blockAllInternational: Boolean = false,
    val blockPrivateCallers: Boolean = false,
    val notificationPreference: NotificationPreference = NotificationPreference.ALL,
    val blockAllCallsMode: BlockAllCallsMode = BlockAllCallsMode.DISABLED,
    val blockAllCallsStartTime: String = "22:00",
    val blockAllCallsEndTime: String = "07:00"
)
