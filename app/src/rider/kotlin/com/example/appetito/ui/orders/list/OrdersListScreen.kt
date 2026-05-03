package com.example.appetito.ui.orders.list

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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import com.example.appetito.ui.features.notifications.ErrorScreen
import com.example.appetito.ui.features.notifications.LoadingScreen
import com.example.appetito.ui.navigation.OrderDetails
import com.example.appetito.ui.theme.Orange
import com.example.appetito.utils.StringUtils
import com.example.appetito.R

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun OrdersListScreen(
    navController: NavController,
    viewModel: OrdersListViewModel = hiltViewModel()
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Active Orders",
                        fontWeight = FontWeight.Bold,
                        color = Color.Black
                    )
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color.White
                )
            )
        },
        containerColor = Color(0xFFF5F5F8)
    ) { paddingValues ->
        Box(modifier = Modifier.padding(paddingValues)) {
            val state = viewModel.state.collectAsStateWithLifecycle()

            when (val currentState = state.value) {
                is OrdersListViewModel.OrdersListState.Loading -> {
                    LoadingScreen()
                }

                is OrdersListViewModel.OrdersListState.Empty -> {
                    Column(
                        modifier = Modifier.fillMaxSize(),
                        verticalArrangement = Arrangement.Center,
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Image(
                            painter = painterResource(R.drawable.ic_empty_orders),
                            contentDescription = "Empty Orders",
                            modifier = Modifier.size(80.dp).padding(bottom = 16.dp),
                        )

                        Text(
                            text = "No active orders",
                            style = MaterialTheme.typography.titleLarge,
                            color = Color.Gray,
                            fontWeight = FontWeight.SemiBold
                        )
                        Text(
                            text = "When you accept deliveries, they will appear here.",
                            style = MaterialTheme.typography.bodyMedium,
                            color = Color.Gray,
                            modifier = Modifier.padding(top = 8.dp, start = 32.dp, end = 32.dp),
                            textAlign = androidx.compose.ui.text.style.TextAlign.Center
                        )
                    }
                }

                is OrdersListViewModel.OrdersListState.Success -> {
                    val orders = currentState.orders
                    LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        contentPadding = androidx.compose.foundation.layout.PaddingValues(16.dp),
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        items(orders) { delivery ->
                            Card(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clickable {
                                        navController.navigate(OrderDetails(delivery.orderId))
                                    },
                                shape = RoundedCornerShape(16.dp),
                                colors = CardDefaults.cardColors(containerColor = Color.White),
                                elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
                            ) {
                                Column(
                                    modifier = Modifier.padding(16.dp)
                                ) {
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Text(
                                            text = "Order #${delivery.orderId.takeLast(6)}",
                                            style = MaterialTheme.typography.labelLarge,
                                            color = Color.Gray,
                                            fontWeight = FontWeight.Bold
                                        )
                                        Box(
                                            modifier = Modifier
                                                .clip(RoundedCornerShape(8.dp))
                                                .background(Color(0xFFE3F2FD))
                                                .padding(horizontal = 8.dp, vertical = 4.dp)
                                        ) {
                                            Text(
                                                text = delivery.status.uppercase(),
                                                style = MaterialTheme.typography.labelSmall,
                                                color = Color(0xFF1976D2),
                                                fontWeight = FontWeight.ExtraBold
                                            )
                                        }
                                    }

                                    Spacer(modifier = Modifier.height(16.dp))

                                    Row(verticalAlignment = Alignment.Top) {

                                        Image(
                                            painterResource(id = R.drawable.ic_store),
                                            contentDescription = "Restaurant",
                                            modifier = Modifier
                                                .padding(end = 8.dp)
                                                .size(32.dp)
                                        )
                                        Column {
                                            Text(
                                                text = "From",
                                                style = MaterialTheme.typography.labelSmall,
                                                color = Color.Gray
                                            )
                                            Text(
                                                text = delivery.restaurant.address,
                                                style = MaterialTheme.typography.bodyMedium,
                                                color = Color.Black
                                            )
                                        }
                                    }

                                    Spacer(modifier = Modifier.height(12.dp))

                                    Row(verticalAlignment = Alignment.Top) {
                                        Icon(
                                            imageVector = Icons.Default.LocationOn,
                                            contentDescription = "Customer",
                                            tint = Color(0xFF03A9F4),
                                            modifier = Modifier.padding(end = 8.dp)
                                        )
                                        Column {
                                            Text(
                                                text = "To",
                                                style = MaterialTheme.typography.labelSmall,
                                                color = Color.Gray
                                            )
                                            Text(
                                                text = delivery.customer.addressLine1,
                                                style = MaterialTheme.typography.bodyLarge,
                                                color = Color.Black,
                                                fontWeight = FontWeight.SemiBold
                                            )
                                        }
                                    }

                                    Spacer(modifier = Modifier.height(16.dp))
                                    
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.End
                                    ) {
                                        Text(
                                            text = StringUtils.formatCurrency(delivery.totalAmount),
                                            style = MaterialTheme.typography.titleMedium,
                                            color = Color(0xFF4CAF50), // Green for earnings
                                            fontWeight = FontWeight.ExtraBold
                                        )
                                    }
                                }
                            }
                        }
                    }
                }

                is OrdersListViewModel.OrdersListState.Error -> {
                    ErrorScreen(currentState.message) {
                        viewModel.getOrders()
                    }
                }
            }
        }
    }
}