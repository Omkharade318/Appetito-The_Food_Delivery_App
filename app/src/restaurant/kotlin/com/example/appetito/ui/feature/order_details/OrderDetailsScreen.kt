package com.example.appetito.ui.feature.order_details

import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import com.example.appetito.ui.features.notifications.ErrorScreen
import com.example.appetito.ui.features.notifications.LoadingScreen
import kotlinx.coroutines.flow.collectLatest
import com.example.appetito.R

val PrimaryOrange = Color(0xFFFE724C)
val TextGray = Color(0xFF9796A1)
val BgLight = Color(0xFFF9F9FB)

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun OrderDetailsScreen(
    orderID: String,
    navController: NavController,
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
                is OrderDetailsViewModel.OrderDetailsEvent.ShowPopUp -> {
                    Toast.makeText(navController.context, it.msg, Toast.LENGTH_SHORT).show()
                }
                else -> {}
            }
        }
    }

    val uiState = viewModel.uiState.collectAsStateWithLifecycle()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(BgLight)
    ) {
        when (uiState.value) {
            is OrderDetailsViewModel.OrderDetailsUiState.Loading -> {
                LoadingScreen()
            }

            is OrderDetailsViewModel.OrderDetailsUiState.Error -> {
                ErrorScreen(message = "Something went wrong") {
                    viewModel.getOrderDetails(orderID)
                }
            }

            is OrderDetailsViewModel.OrderDetailsUiState.Success -> {
                val order = (uiState.value as OrderDetailsViewModel.OrderDetailsUiState.Success).order
                
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .verticalScroll(rememberScrollState())
                ) {

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ){
                        IconButton(
                            onClick = { navController.popBackStack() },
                            modifier = Modifier
                                .padding(start = 24.dp, top = 16.dp)
                                .shadow(elevation = 8.dp, shape = CircleShape)
                                .background(Color.White, CircleShape)
                                .size(48.dp)
                        ) {
                            Image(
                                painter = painterResource(id = R.drawable.ic_back),
                                contentDescription = "Back",
                            )
                        }

                        Text(
                            text = "Order Details",
                            fontWeight = FontWeight.Bold,
                            fontSize = 28.sp,
                            color = Color(0xFF323643),
                            modifier = Modifier.padding(start = 24.dp, top = 24.dp, bottom = 16.dp)
                        )
                    }

                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 24.dp)
                            .shadow(elevation = 8.dp, shape = RoundedCornerShape(16.dp), spotColor = Color(0x1A000000))
                            .background(Color.White, RoundedCornerShape(16.dp))
                            .padding(16.dp)
                    ) {
                        Column {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = "Order #${order.id.takeLast(6)}",
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 18.sp,
                                    color = Color(0xFF323643)
                                )
                                val statusColor = when(order.status.lowercase()) {
                                    "completed", "delivered" -> Color(0xFF4CAF50)
                                    "pending" -> Color(0xFFFF9800)
                                    "cancelled" -> Color(0xFFF44336)
                                    else -> PrimaryOrange
                                }
                                Box(
                                    modifier = Modifier
                                        .background(statusColor.copy(alpha = 0.1f), RoundedCornerShape(20.dp))
                                        .padding(horizontal = 10.dp, vertical = 4.dp)
                                ) {
                                    Text(
                                        text = order.status,
                                        fontSize = 12.sp,
                                        fontWeight = FontWeight.SemiBold,
                                        color = statusColor
                                    )
                                }
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(24.dp))
                    
                    Text(
                        text = "Items",
                        fontWeight = FontWeight.Bold,
                        fontSize = 20.sp,
                        color = Color(0xFF323643),
                        modifier = Modifier.padding(horizontal = 24.dp, vertical = 8.dp)
                    )

                    Column(
                        modifier = Modifier.padding(horizontal = 24.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        order.items.forEach { item ->
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .shadow(elevation = 4.dp, shape = RoundedCornerShape(12.dp), spotColor = Color(0x1A000000))
                                    .background(Color.White, RoundedCornerShape(12.dp))
                                    .padding(16.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(32.dp)
                                        .background(PrimaryOrange.copy(alpha = 0.1f), CircleShape),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(
                                        text = "${item.quantity}x",
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 14.sp,
                                        color = PrimaryOrange
                                    )
                                }
                                Spacer(modifier = Modifier.width(16.dp))
                                Text(
                                    text = item.menuItemName ?: "Unknown Item",
                                    fontWeight = FontWeight.Medium,
                                    fontSize = 16.sp,
                                    color = Color(0xFF323643)
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(24.dp))
                    
                    Text(
                        text = "Update Status",
                        fontWeight = FontWeight.Bold,
                        fontSize = 20.sp,
                        color = Color(0xFF323643),
                        modifier = Modifier.padding(horizontal = 24.dp, vertical = 8.dp)
                    )

                    FlowRow(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 24.dp)
                            .padding(bottom = 32.dp),
                        horizontalArrangement = Arrangement.spacedBy(12.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        viewModel.listOfStatus.forEach { status ->
                            val isCurrentStatus = order.status == status
                            Button(
                                onClick = { viewModel.updateOrderStatus(orderID, status) },
                                enabled = !isCurrentStatus,
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = if(isCurrentStatus) TextGray.copy(alpha = 0.2f) else PrimaryOrange,
                                    disabledContainerColor = TextGray.copy(alpha = 0.2f),
                                    disabledContentColor = TextGray
                                ),
                                shape = RoundedCornerShape(24.dp)
                            ) {
                                Text(
                                    text = status,
                                    fontWeight = FontWeight.SemiBold,
                                    fontSize = 14.sp
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}