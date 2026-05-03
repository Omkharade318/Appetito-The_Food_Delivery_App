package com.example.appetito.ui.feature.order_list

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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ScrollableTabRow
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRowDefaults
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import com.example.appetito.data.models.Order
import com.example.appetito.ui.features.notifications.ErrorScreen
import com.example.appetito.ui.features.notifications.LoadingScreen
import com.example.appetito.ui.navigation.OrderDetails
import kotlinx.coroutines.launch

val PrimaryOrange = Color(0xFFFE724C)
val TextGray = Color(0xFF9796A1)
val BgLight = Color(0xFFF9F9FB)

@Composable
fun OrderListScreen(
    navController: NavController, viewModel: OrdersListViewModel = hiltViewModel()
) {
    val listOfItems = viewModel.getOrderTypes()
    
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(BgLight)
    ) {
        Text(
            text = "Orders",
            fontWeight = FontWeight.Bold,
            fontSize = 28.sp,
            color = Color(0xFF323643),
            modifier = Modifier.padding(start = 24.dp, top = 24.dp, bottom = 8.dp)
        )
        
        val pagerState = rememberPagerState(pageCount = { listOfItems.size })
        val coroutineScope = rememberCoroutineScope()
        
        LaunchedEffect(key1 = pagerState.currentPage) {
            viewModel.getOrdersByType(listOfItems[pagerState.currentPage])
        }
        
        ScrollableTabRow(
            selectedTabIndex = pagerState.currentPage,
            modifier = Modifier.fillMaxWidth(),
            containerColor = BgLight,
            contentColor = PrimaryOrange,
            indicator = { tabPositions ->
                TabRowDefaults.SecondaryIndicator(
                    modifier = Modifier.tabIndicatorOffset(tabPositions[pagerState.currentPage]),
                    color = PrimaryOrange
                )
            },
            divider = { }
        ) {
            listOfItems.forEachIndexed { index, item ->
                val isSelected = pagerState.currentPage == index
                Tab(
                    selected = isSelected,
                    onClick = {
                        coroutineScope.launch {
                            pagerState.animateScrollToPage(index)
                        }
                    },
                    text = {
                        Text(
                            text = item,
                            fontSize = 15.sp,
                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                            color = if (isSelected) PrimaryOrange else TextGray
                        )
                    }
                )
            }
        }
        
        HorizontalPager(state = pagerState) { page ->
            Column(modifier = Modifier.fillMaxSize()) {
                val uiState = viewModel.uiState.collectAsStateWithLifecycle()
                when (uiState.value) {
                    is OrdersListViewModel.OrdersScreenState.Loading -> {
                        LoadingScreen()
                    }

                    is OrdersListViewModel.OrdersScreenState.Success -> {
                        val orders =
                            (uiState.value as OrdersListViewModel.OrdersScreenState.Success).data
                        LazyColumn(
                            contentPadding = PaddingValues(top = 16.dp, bottom = 80.dp),
                            verticalArrangement = Arrangement.spacedBy(16.dp)
                        ) {
                            items(orders) { order ->
                                OrderListItem(order = order) {
                                    navController.navigate(OrderDetails(order.id))
                                }
                            }
                        }
                    }

                    is OrdersListViewModel.OrdersScreenState.Failed -> {
                        ErrorScreen(message = "Failed to load data") {
                            viewModel.getOrdersByType(listOfItems[pagerState.currentPage])
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun OrderListItem(order: Order, onOrderClicked: () -> Unit) {
    val statusColor = when(order.status.lowercase()) {
        "completed", "delivered" -> Color(0xFF4CAF50)
        "pending" -> Color(0xFFFF9800)
        "cancelled" -> Color(0xFFF44336)
        else -> PrimaryOrange
    }

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 24.dp)
            .shadow(elevation = 8.dp, shape = RoundedCornerShape(16.dp), spotColor = Color(0x1A000000))
            .background(Color.White, RoundedCornerShape(16.dp))
            .clip(RoundedCornerShape(16.dp))
            .clickable { onOrderClicked() }
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Order #${order.id.takeLast(6)}",
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp,
                    color = Color(0xFF323643)
                )
                
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
            
            Spacer(modifier = Modifier.height(12.dp))
            
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = "Address:",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Medium,
                    color = TextGray
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = order.address.addressLine1,
                    fontSize = 14.sp,
                    color = Color(0xFF323643),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }
        }
    }
}