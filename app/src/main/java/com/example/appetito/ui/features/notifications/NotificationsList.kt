package com.example.appetito.ui.features.notifications

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Notifications
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import com.example.appetito.R
import com.example.appetito.data.models.Notification
import com.example.appetito.ui.navigation.OrderDetails
import kotlinx.coroutines.flow.collectLatest

// Shared app colors
val PrimaryOrange = Color(0xFFFE724C)
val TextGray = Color(0xFF9796A1)
val BgLight = Color(0xFFF9F9FB)

@Composable
fun NotificationsList(navController: NavController, viewModel: NotificationsViewModel) {

    val state = viewModel.state.collectAsStateWithLifecycle()

    LaunchedEffect(key1 = true) {
        viewModel.event.collectLatest {
            when (it) {
                is NotificationsViewModel.NotificationsEvent.NavigateToOrderDetail -> {
                    navController.navigate(OrderDetails(it.orderID))
                }
            }
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(BgLight) // Using off-white background for depth
    ) {
        // --- HEADER ---
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 16.dp, start = 24.dp, end = 24.dp, bottom = 8.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(44.dp)
                    .shadow(12.dp, shape = RoundedCornerShape(12.dp), spotColor = Color(0x1A000000))
                    .background(Color.White, RoundedCornerShape(12.dp))
                    .clickable { navController.popBackStack() },
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    painter = painterResource(id = R.drawable.ic_back),
                    contentDescription = "Back",
                    tint = Color.Unspecified
                )
            }

            Text(
                text = "Notifications",
                style = TextStyle(fontSize = 18.sp, fontWeight = FontWeight.Bold, color = Color(0xFF323643))
            )

            Spacer(modifier = Modifier.size(44.dp)) // To keep title centered
        }

        // --- CONTENT ---
        when (state.value) {
            is NotificationsViewModel.NotificationsState.Loading -> {
                LoadingScreen()
            }

            is NotificationsViewModel.NotificationsState.Success -> {
                val notifications = (state.value as NotificationsViewModel.NotificationsState.Success).data

                if (notifications.isEmpty()) {
                    EmptyNotificationsScreen()
                } else {
                    LazyColumn(
                        contentPadding = PaddingValues(horizontal = 24.dp, vertical = 16.dp),
                        verticalArrangement = Arrangement.spacedBy(16.dp) // Space between cards
                    ) {
                        items(notifications, key = { it.id }) { notification ->
                            NotificationItem(notification) {
                                viewModel.readNotification(notification)
                            }
                        }
                    }
                }
            }

            is NotificationsViewModel.NotificationsState.Error -> {
                val message = (state.value as NotificationsViewModel.NotificationsState.Error).message
                ErrorScreen(message = message) {
                    viewModel.getNotifications()
                }
            }
        }
    }
}

@Composable
fun NotificationItem(notification: Notification, onRead: () -> Unit) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .shadow(
                elevation = if (notification.isRead) 4.dp else 12.dp,
                shape = RoundedCornerShape(16.dp),
                spotColor = Color(0x1A000000)
            )
            .background(Color.White, RoundedCornerShape(16.dp))
            .clip(RoundedCornerShape(16.dp))
            .clickable { onRead() }
            .padding(16.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Leading Icon Box
            Box(
                modifier = Modifier
                    .size(50.dp)
                    .background(
                        color = if (notification.isRead) Color(0xFFF1F2F6) else PrimaryOrange.copy(alpha = 0.15f),
                        shape = CircleShape
                    ),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Outlined.Notifications,
                    contentDescription = "Notification",
                    tint = if (notification.isRead) Color(0xFFAAAAAA) else PrimaryOrange,
                    modifier = Modifier.size(24.dp)
                )
            }

            Spacer(modifier = Modifier.width(16.dp))

            // Text Content
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = notification.title,
                    style = TextStyle(
                        fontSize = 16.sp,
                        fontWeight = if (notification.isRead) FontWeight.Medium else FontWeight.Bold,
                        color = if (notification.isRead) Color(0xFF67666D) else Color(0xFF323643)
                    )
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = notification.message,
                    style = TextStyle(
                        fontSize = 13.sp,
                        color = TextGray,
                        lineHeight = 18.sp
                    ),
                    maxLines = 2
                )
            }

            // Unread Dot Indicator
            if (!notification.isRead) {
                Spacer(modifier = Modifier.width(12.dp))
                Box(
                    modifier = Modifier
                        .size(10.dp)
                        .background(PrimaryOrange, CircleShape)
                )
            }
        }
    }
}

@Composable
fun EmptyNotificationsScreen() {
    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Icon(
            imageVector = Icons.Outlined.Notifications,
            contentDescription = null,
            modifier = Modifier.size(80.dp),
            tint = Color(0xFFEAEAEC)
        )
        Spacer(modifier = Modifier.height(16.dp))
        Text(
            text = "No notifications yet",
            style = TextStyle(fontSize = 18.sp, fontWeight = FontWeight.Bold, color = Color(0xFF323643))
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = "When you get updates about your orders,\nthey'll show up here.",
            style = TextStyle(fontSize = 14.sp, color = TextGray),
            textAlign = TextAlign.Center
        )
    }
}

@Composable
fun LoadingScreen() {
    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        CircularProgressIndicator(color = PrimaryOrange)
    }
}

@Composable
fun ErrorScreen(message: String, onRetry: () -> Unit) {
    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(message, color = Color.Red, textAlign = TextAlign.Center, modifier = Modifier.padding(horizontal = 24.dp))
        Spacer(modifier = Modifier.height(16.dp))
        Button(
            onClick = { onRetry() },
            colors = ButtonDefaults.buttonColors(containerColor = PrimaryOrange),
            shape = RoundedCornerShape(30.dp)
        ) {
            Text(text = "Retry", color = Color.White)
        }
    }
}