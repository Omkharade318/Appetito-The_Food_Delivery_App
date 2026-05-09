package com.example.appetito.ui.feature.order_details

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Divider
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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import com.example.appetito.R
import com.example.appetito.ui.feature.orders.OrderDetailsText
import com.example.appetito.ui.features.orders.order_map.OrderTrackerMapView
import com.example.appetito.utils.StringUtils
import kotlinx.coroutines.flow.collectLatest
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.Locale

val PrimaryOrange = Color(0xFFFE724C)
val TextGray = Color(0xFF9796A1)

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun OrderDetailsScreen(
    navController: NavController,
    orderID: String,
    viewModel: OrderDetailsViewModel = hiltViewModel()
) {
    LaunchedEffect(key1 = orderID) {
        viewModel.getOrderDetails(orderID)
    }

    LaunchedEffect(key1 = true) {
        viewModel.event.collectLatest {
            when (it) {
                is OrderDetailsViewModel.OrderDetailsEvent.NavigateBack -> {
                    navController.popBackStack()
                }
            }
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
    ) {
        // --- HEADER ---
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 16.dp, start = 24.dp, end = 24.dp, bottom = 16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(44.dp)
                    .shadow(16.dp, shape = RoundedCornerShape(12.dp))
                    .background(Color.White, RoundedCornerShape(12.dp))
                    .clickable { viewModel.navigateBack() },
                contentAlignment = Alignment.Center
            ) {
                Image(painterResource(id = R.drawable.ic_back), contentDescription = "Back")
            }

            Text(
                text = "Order Details",
                style = TextStyle(fontSize = 18.sp, fontWeight = FontWeight.Bold, color = Color(0xFF323643))
            )

            Spacer(modifier = Modifier.size(44.dp)) // To keep title centered
        }

        val uiState = viewModel.state.collectAsStateWithLifecycle()

        // --- CONTENT ---
        when (uiState.value) {
            is OrderDetailsViewModel.OrderDetailsState.Loading -> {
                Column(
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier.fillMaxSize()
                ) {
                    CircularProgressIndicator(color = PrimaryOrange)
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(text = "Loading Order...", color = TextGray)
                }
            }

            is OrderDetailsViewModel.OrderDetailsState.OrderDetails -> {
                val order = (uiState.value as OrderDetailsViewModel.OrderDetailsState.OrderDetails).order

                Column(modifier = Modifier.padding(horizontal = 24.dp)) {

                    // Render your existing top section (Pizza image, restaurant name, etc.)
                    OrderDetailsText(order)

                    Spacer(modifier = Modifier.height(24.dp))

                    // Clean Details Card
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .shadow(elevation = 8.dp, shape = RoundedCornerShape(16.dp), spotColor = Color(0x1A000000))
                            .background(Color.White, RoundedCornerShape(16.dp))
                            .padding(20.dp)
                    ) {
                        Column {
                            // Formatted Date
                            DetailRowItem(title = "Date", value = formatReadableDate(order.createdAt))

                            Spacer(modifier = Modifier.height(12.dp))
                            Divider(color = Color(0xFFF1F2F6), thickness = 1.dp)
                            Spacer(modifier = Modifier.height(12.dp))

                            // Formatted Status Row
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(text = "Status", style = TextStyle(fontSize = 14.sp, color = TextGray))
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Image(
                                        painter = painterResource(id = viewModel.getImage(order)),
                                        contentDescription = null,
                                        modifier = Modifier.size(20.dp)
                                    )
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text(
                                        text = formatReadableStatus(order.status),
                                        style = TextStyle(fontSize = 14.sp, fontWeight = FontWeight.SemiBold, color = Color(0xFF323643))
                                    )
                                }
                            }

                            Spacer(modifier = Modifier.height(12.dp))
                            Divider(color = Color(0xFFF1F2F6), thickness = 1.dp)
                            Spacer(modifier = Modifier.height(12.dp))

                            // Price
                            DetailRowItem(
                                title = "Total Amount",
                                value = StringUtils.formatCurrency(order.totalAmount),
                                isTotal = true
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(24.dp))

                    // Map View and Rider Info
                    if (order.status == OrdersUtils.OrderStatus.OUT_FOR_DELIVERY.name) {
                        Spacer(modifier = Modifier.height(24.dp))
                        
                        Text(
                            text = "Track Order",
                            style = TextStyle(fontSize = 16.sp, fontWeight = FontWeight.Bold, color = Color(0xFF323643))
                        )
                        
                        Spacer(modifier = Modifier.height(12.dp))
                        
                        OrderTrackerMapView(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(300.dp)
                                .clip(RoundedCornerShape(16.dp))
                                .shadow(4.dp),
                            viewModel = viewModel,
                            order = order
                        )
                    }
                }
            }

            is OrderDetailsViewModel.OrderDetailsState.Error -> {
                Column(
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier.fillMaxSize()
                ) {
                    Text(text = (uiState.value as OrderDetailsViewModel.OrderDetailsState.Error).message, color = Color.Red)
                    Spacer(modifier = Modifier.height(16.dp))
                    Button(
                        onClick = { viewModel.getOrderDetails(orderID) },
                        colors = ButtonDefaults.buttonColors(containerColor = PrimaryOrange)
                    ) {
                        Text(text = "Retry", color = Color.White)
                    }
                }
            }
        }
    }
}

// Reusable composable for the left-right receipt items
@Composable
fun DetailRowItem(title: String, value: String, isTotal: Boolean = false) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = title,
            style = TextStyle(
                fontSize = if (isTotal) 16.sp else 14.sp,
                fontWeight = if (isTotal) FontWeight.Bold else FontWeight.Normal,
                color = if (isTotal) Color(0xFF323643) else TextGray
            )
        )
        Text(
            text = value,
            style = TextStyle(
                fontSize = if (isTotal) 18.sp else 14.sp,
                fontWeight = if (isTotal) FontWeight.Bold else FontWeight.SemiBold,
                color = if (isTotal) PrimaryOrange else Color(0xFF323643)
            )
        )
    }
}

// Helper to turn "2026-04-13T15:48:39.883" into "April 13, 2026, 03:48 PM"
@RequiresApi(Build.VERSION_CODES.O)
fun formatReadableDate(isoString: String): String {
    return try {
        val parsedDate = LocalDateTime.parse(isoString)
        val formatter = DateTimeFormatter.ofPattern("MMM dd, yyyy, hh:mm a")
        parsedDate.format(formatter)
    } catch (e: Exception) {
        isoString // Fallback to raw string if parsing fails
    }
}

// Helper to turn "PENDING_ACCEPTANCE" into "Pending Acceptance"
fun formatReadableStatus(status: String): String {
    return status.replace("_", " ")
        .lowercase(Locale.getDefault())
        .split(" ")
        .joinToString(" ") { it.replaceFirstChar { char -> char.uppercase(Locale.getDefault()) } }
}