package com.example.appetito.data.models

data class NotificationListResponse(
    val notifications: List<Notification>,
    val unreadCount: Int
)