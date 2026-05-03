package com.example.appetito.ui.home

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
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
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import com.example.appetito.ui.features.notifications.ErrorScreen
import com.example.appetito.ui.features.notifications.LoadingScreen
import com.example.appetito.ui.theme.Orange
import com.example.appetito.utils.StringUtils
import com.example.appetito.R

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DeliveriesScreen(
    navController: NavController,
    homeViewModel: DeliveriesViewModel = hiltViewModel()
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "New Deliveries",
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
        val uiState = homeViewModel.deliveriesState.collectAsStateWithLifecycle()

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            when (val state = uiState.value) {
                is DeliveriesViewModel.DeliveriesState.Loading -> {
                    LoadingScreen()
                }

                is DeliveriesViewModel.DeliveriesState.Success -> {
                    if (state.deliveries.isEmpty()) {
                        Column(
                            modifier = Modifier.fillMaxSize(),
                            verticalArrangement = Arrangement.Center,
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Image(
                                painterResource(id = R.drawable.ic_store),
                                contentDescription = "No Deliveries",
                                modifier = Modifier.padding(bottom = 16.dp),
                            )

                            Text(
                                text = "No new deliveries right now",
                                style = MaterialTheme.typography.titleMedium,
                                color = Color.Gray,
                                modifier = Modifier.padding(16.dp)
                            )
                        }
                    } else {
                        LazyColumn(
                            modifier = Modifier.fillMaxSize(),
                            contentPadding = androidx.compose.foundation.layout.PaddingValues(16.dp),
                            verticalArrangement = Arrangement.spacedBy(16.dp)
                        ) {
                            items(state.deliveries) { delivery ->
                                Card(
                                    modifier = Modifier.fillMaxWidth(),
                                    shape = RoundedCornerShape(16.dp),
                                    colors = CardDefaults.cardColors(containerColor = Color.White),
                                    elevation = CardDefaults.cardElevation(defaultElevation = 6.dp)
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
                                            Text(
                                                text = StringUtils.formatCurrency(delivery.orderAmount),
                                                style = MaterialTheme.typography.titleLarge,
                                                color = Color(0xFF4CAF50), // Green for earnings
                                                fontWeight = FontWeight.ExtraBold
                                            )
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
                                                    text = "Pick up at",
                                                    style = MaterialTheme.typography.labelSmall,
                                                    color = Color.Gray
                                                )
                                                Text(
                                                    text = delivery.restaurantAddress,
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
                                                    text = "Deliver to",
                                                    style = MaterialTheme.typography.labelSmall,
                                                    color = Color.Gray
                                                )
                                                Text(
                                                    text = delivery.customerAddress,
                                                    style = MaterialTheme.typography.bodyLarge,
                                                    color = Color.Black,
                                                    fontWeight = FontWeight.SemiBold
                                                )
                                            }
                                        }

                                        Spacer(modifier = Modifier.height(16.dp))
                                        
                                        Row(
                                            verticalAlignment = Alignment.CenterVertically
                                        ) {
                                            Image(
                                                painterResource(R.drawable.ic_car),
                                                contentDescription = "Distance",
                                                modifier = Modifier
                                                    .padding(end = 8.dp)
                                                    .size(32.dp)
                                            )

                                            Text(
                                                text = "8.64 km total distance",
                                                style = MaterialTheme.typography.bodyMedium,
                                                color = Color.DarkGray
                                            )
                                        }

                                        Spacer(modifier = Modifier.height(20.dp))

                                        Row(
                                            modifier = Modifier.fillMaxWidth(),
                                            horizontalArrangement = Arrangement.spacedBy(12.dp)
                                        ) {
                                            OutlinedButton(
                                                onClick = { homeViewModel.deliveryRejected(delivery) },
                                                modifier = Modifier.weight(1f),
                                                shape = RoundedCornerShape(12.dp)
                                            ) {
                                                Text(text = "Decline", color = Color.DarkGray)
                                            }

                                            Button(
                                                onClick = { homeViewModel.deliveryAccepted(delivery) },
                                                modifier = Modifier.weight(1f),
                                                shape = RoundedCornerShape(12.dp),
                                                colors = ButtonDefaults.buttonColors(containerColor = Orange)
                                            ) {
                                                Text(text = "Accept", color = Color.White)
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }

                is DeliveriesViewModel.DeliveriesState.Error -> {
                    ErrorScreen(message = state.message) {
                        homeViewModel.getDeliveries()
                    }
                }
            }
        }
    }
}