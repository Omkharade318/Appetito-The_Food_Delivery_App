package com.example.appetito.notification

import android.Manifest
import android.app.PendingIntent
import android.content.Intent
import androidx.annotation.RequiresPermission
import com.example.appetito.MainActivity
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class FoodHubMessagingService : FirebaseMessagingService() {

    @Inject
    lateinit var foodHubNotificationManager: FoodHubNotificationManager
    override fun onNewToken(token: String) {
        super.onNewToken(token)
        foodHubNotificationManager.updateToken(token)
    }

    @RequiresPermission(Manifest.permission.POST_NOTIFICATIONS)
    override fun onMessageReceived(message: RemoteMessage) {
        super.onMessageReceived(message)
        val intent = Intent(this, MainActivity::class.java)
        val title = message.notification?.title ?: ""
        val messageText = message.notification?.body ?: ""
        val data = message.data
        val type = data["type"] ?: "general"

        if (type == "order") {
            val orderID = data[ORDER_ID]
            intent.putExtra(ORDER_ID, orderID)
        }
        val pendingIntent = PendingIntent.getActivity(
            this,
            1,
            intent,
            PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
        )
        val notificationChannelType = when (type) {
            "order" -> FoodHubNotificationManager.NotificationChannelType.ORDER
            "general" -> FoodHubNotificationManager.NotificationChannelType.PROMOTION
            else -> FoodHubNotificationManager.NotificationChannelType.ACCOUNT
        }
        foodHubNotificationManager.showNotification(
            title, messageText, 13034, pendingIntent,
            notificationChannelType
        )
    }

    companion object {
        const val ORDER_ID = "orderId"
    }
}