package com.example.appetito.ui.orders.details

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import com.codewithfk.foodhub.utils.OrdersUtils
import com.example.appetito.data.models.Order
import com.example.appetito.ui.features.orders.order_map.OrderTrackerMapView
import com.example.appetito.ui.features.notifications.ErrorScreen
import com.example.appetito.ui.features.notifications.LoadingScreen
import com.example.appetito.ui.theme.Orange
import com.google.android.gms.maps.model.LatLng
import kotlinx.coroutines.flow.collectLatest

@OptIn(ExperimentalLayoutApi::class, ExperimentalMaterial3Api::class)
@Composable
fun OrderDetailsScreen(
    orderId: String,
    navController: NavController,
    viewModel: OrderDetailsViewModel = hiltViewModel()
) {
    LaunchedEffect(key1 = orderId) {
        viewModel.getOrderDetails(orderId)
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

                else -> {

                }
            }
        }
    }

    val uiState = viewModel.uiState.collectAsStateWithLifecycle()

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Order Details",
                        fontWeight = FontWeight.Bold,
                        color = Color.Black
                    )
                },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(imageVector = Icons.Default.ArrowBack, contentDescription = "Back", tint = Color.Black)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color.White
                )
            )
        },
        containerColor = Color(0xFFF5F5F8)
    ) { paddingValues ->

        BoxContentWrapper(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues),
            uiState = uiState.value,
            orderId = orderId,
            viewModel = viewModel,
            navController = navController
        )
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun BoxContentWrapper(
    modifier: Modifier,
    uiState: OrderDetailsViewModel.OrderDetailsUiState,
    orderId: String,
    viewModel: OrderDetailsViewModel,
    navController: NavController
) {
    Column(modifier = modifier) {
        when (uiState) {
            is OrderDetailsViewModel.OrderDetailsUiState.Loading -> {
                LoadingScreen()
            }

            is OrderDetailsViewModel.OrderDetailsUiState.Error -> {
                ErrorScreen(message = "Something went wrong") {
                    viewModel.getOrderDetails(orderId)
                }
            }

            is OrderDetailsViewModel.OrderDetailsUiState.Success -> {
                val order = uiState.order
                
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .verticalScroll(rememberScrollState())
                        .padding(16.dp)
                ) {
                    // Header Order ID
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp),
                        colors = CardDefaults.cardColors(containerColor = Color.White)
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Text(
                                text = "Order ID",
                                style = MaterialTheme.typography.labelMedium,
                                color = Color.Gray
                            )
                            Text(
                                text = order.id,
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.SemiBold,
                                color = Color.Black
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    Text(
                        text = "Items",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = Color.DarkGray,
                        modifier = Modifier.padding(bottom = 8.dp)
                    )

                    // Order Items
                    order.items.forEach { item ->
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(bottom = 8.dp),
                            shape = RoundedCornerShape(12.dp),
                            colors = CardDefaults.cardColors(containerColor = Color.White),
                            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                        ) {
                            Row(
                                modifier = Modifier
                                    .padding(16.dp)
                                    .fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = item.menuItemName ?: "Unknown Item",
                                    style = MaterialTheme.typography.bodyLarge,
                                    fontWeight = FontWeight.Medium,
                                    color = Color.Black
                                )
                                Surface(
                                    shape = RoundedCornerShape(8.dp),
                                    color = Color(0xFFEEEEEE)
                                ) {
                                    Text(
                                        text = "x${item.quantity}",
                                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                                        style = MaterialTheme.typography.labelLarge,
                                        fontWeight = FontWeight.Bold,
                                        color = Color.DarkGray
                                    )
                                }
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(24.dp))

                    if (order.status == OrdersUtils.OrderStatus.DELIVERED.name) {
                        Column(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Icon(
                                imageVector = Icons.Default.CheckCircle,
                                contentDescription = "Delivered",
                                tint = Color(0xFF4CAF50),
                                modifier = Modifier.padding(bottom = 8.dp)
                            )
                            Text(
                                text = "Order Delivered Successfully",
                                style = MaterialTheme.typography.titleLarge,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF4CAF50)
                            )
                        }
                    } else {
                        Text(
                            text = "Update Status",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = Color.DarkGray,
                            modifier = Modifier.padding(bottom = 12.dp)
                        )
                        FlowRow(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            viewModel.listOfStatus.forEach { statusText ->
                                val isSelected = order.status == statusText
                                if (isSelected) {
                                    Button(
                                        onClick = { },
                                        enabled = false,
                                        colors = ButtonDefaults.buttonColors(
                                            disabledContainerColor = Orange,
                                            disabledContentColor = Color.White
                                        ),
                                        shape = RoundedCornerShape(20.dp)
                                    ) {
                                        Text(text = statusText)
                                    }
                                } else {
                                    OutlinedButton(
                                        onClick = { viewModel.updateOrderStatus(orderId, statusText) },
                                        shape = RoundedCornerShape(20.dp)
                                    ) {
                                        Text(text = statusText, color = Color.DarkGray)
                                    }
                                }
                            }
                        }
                    }
                }
            }

            is OrderDetailsViewModel.OrderDetailsUiState.OrderDelivery -> {
                val order = uiState.order
                Column(
                    modifier = Modifier.fillMaxSize()
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(Color.White)
                            .padding(16.dp)
                    ) {
                        Text(
                            text = "Tracking Order Route",
                            style = MaterialTheme.typography.labelLarge,
                            color = Color.Gray
                        )
                        Text(
                            text = order.id,
                            style = MaterialTheme.typography.bodyMedium,
                            color = Color.Black,
                            fontWeight = FontWeight.SemiBold
                        )
                    }

                    BoxContentMap(
                        modifier = Modifier
                            .weight(1f)
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp)),
                        viewModel = viewModel,
                        order = order,
                        initialLocation = viewModel.riderLocation.collectAsStateWithLifecycle().value
                    )
                    
                    Surface(
                        modifier = Modifier.fillMaxWidth(),
                        color = Color.White,
                        shadowElevation = 16.dp
                    ) {
                        Button(
                            onClick = {
                                viewModel.updateOrderStatus(
                                    orderId,
                                    OrdersUtils.OrderStatus.DELIVERED.name
                                )
                            },
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp)
                                .height(56.dp),
                            shape = RoundedCornerShape(12.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = Orange)
                        ) {
                            Text(
                                text = "Mark as Delivered",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                color = Color.White
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun BoxContentMap(
    modifier: Modifier,
    viewModel: OrderDetailsViewModel,
    order: Order,
    initialLocation: LatLng? = null
    ) {
    OrderTrackerMapView(
        modifier = modifier,
        viewModel = viewModel,
        order = order,
        initialLocation = initialLocation
    )
}

