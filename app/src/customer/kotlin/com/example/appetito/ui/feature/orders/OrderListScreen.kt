package com.example.appetito.ui.feature.orders

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import coil3.compose.AsyncImage
import com.example.appetito.R
import com.example.appetito.data.models.CartItem
import com.example.appetito.data.models.FoodItem
import com.example.appetito.data.models.Order
import com.example.appetito.data.models.Restaurant
import com.example.appetito.ui.navigation.OrderDetails
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import kotlin.toString

val PrimaryOrange = Color(0xFFFE724C)
val TextGray = Color(0xFF9796A1)
val BgLight = Color(0xFFF9F9FB)

@Composable
fun OrderListScreen(navController: NavController, viewModel: OrderListViewModel = hiltViewModel()) {
    Column(
        modifier = Modifier.fillMaxSize().background(Color.White)
    ) {
        val uiState = viewModel.state.collectAsStateWithLifecycle()

        LaunchedEffect(key1 = true) {
            viewModel.event.collectLatest {
                when (it) {
                    is OrderListViewModel.OrderListEvent.NavigateToOrderDetailScreen -> {
                        navController.navigate(OrderDetails(it.order.id))
                    }
                    OrderListViewModel.OrderListEvent.NavigateBack -> {
                        navController.popBackStack()
                    }
                }
            }
        }
        
        OrderHeader(onBack = { viewModel.navigateBack() })
        
        when (uiState.value) {
            is OrderListViewModel.OrderListState.Loading -> {
                Column(
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier.fillMaxSize()
                ) {
                    CircularProgressIndicator(color = PrimaryOrange)
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(text = "Loading Orders...", color = TextGray)
                }
            }

            is OrderListViewModel.OrderListState.OrderList -> {
                val list = (uiState.value as OrderListViewModel.OrderListState.OrderList).orderList
                
                val listOfTabs = listOf("Upcoming", "History")
                val coroutineScope = rememberCoroutineScope()
                val pagerState = rememberPagerState(pageCount = { listOfTabs.size }, initialPage = 0)
                
                // Custom Tab Row
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 24.dp, vertical = 24.dp)
                        .height(56.dp)
                        .border(1.dp, Color(0xFFF1F2F6), RoundedCornerShape(30.dp))
                        .background(Color.White, RoundedCornerShape(30.dp))
                        .padding(4.dp)
                ) {
                    Row(modifier = Modifier.fillMaxSize()) {
                        listOfTabs.forEachIndexed { index, title ->
                            val isSelected = pagerState.currentPage == index
                            Box(
                                modifier = Modifier
                                    .weight(1f)
                                    .fillMaxSize()
                                    .background(
                                        color = if (isSelected) PrimaryOrange else Color.Transparent,
                                        shape = RoundedCornerShape(30.dp)
                                    )
                                    .clickable {
                                        coroutineScope.launch {
                                            pagerState.animateScrollToPage(index)
                                        }
                                    },
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = title,
                                    style = TextStyle(
                                        fontSize = 14.sp,
                                        fontWeight = FontWeight.SemiBold,
                                        color = if (isSelected) Color.White else PrimaryOrange
                                    )
                                )
                            }
                        }
                    }
                }

                HorizontalPager(state = pagerState, modifier = Modifier.weight(1f)) { page ->
                    when (page) {
                        0 -> {
                            val upcomingList = list.filter { it.status != "Delivered" } 
                            UpcomingOrdersList(upcomingList, onClick = { viewModel.navigateToDetails(it) })
                        }
                        1 -> {
                            val historyList = list.filter { it.status == "Delivered" }
                            HistoryOrdersList(historyList, onClick = { viewModel.navigateToDetails(it) })
                        }
                    }
                }
            }

            is OrderListViewModel.OrderListState.Error -> {
                Column(
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier.fillMaxSize()
                ) {
                    Text(text = (uiState.value as OrderListViewModel.OrderListState.Error).message, color = Color.Red)
                    Spacer(modifier = Modifier.height(16.dp))
                    Button(onClick = { viewModel.getOrders() }, colors = ButtonDefaults.buttonColors(containerColor = PrimaryOrange)) {
                        Text(text = "Retry", color = Color.White)
                    }
                }
            }
        }
    }
}

@Composable
fun OrderHeader(onBack: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 16.dp, start = 24.dp, end = 24.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(44.dp)
                .shadow(16.dp, shape = RoundedCornerShape(12.dp))
                .background(Color.White, RoundedCornerShape(12.dp))
                .clickable { onBack() },
            contentAlignment = Alignment.Center
        ) {
            Image(painterResource(id = R.drawable.ic_back), contentDescription = "Back")
        }

        Text(text = "My Orders", style = TextStyle(fontSize = 18.sp, fontWeight = FontWeight.Bold, color = Color(0xFF323643)))

    }
}

@Composable
fun UpcomingOrdersList(list: List<Order>, onClick: (Order) -> Unit) {
    LazyColumn(
        contentPadding = PaddingValues(horizontal = 24.dp, vertical = 8.dp),
        verticalArrangement = Arrangement.spacedBy(20.dp)
    ) {
        items(list) { order ->
            UpcomingOrderItemCard(order = order, onClick = { onClick(order) })
        }
    }
}

@Composable
fun HistoryOrdersList(list: List<Order>, onClick: (Order) -> Unit) {
    Column {
        Text(
            text = "Lasted Orders", 
            style = TextStyle(fontSize = 18.sp, fontWeight = FontWeight.Bold, color = Color(0xFF323643)),
            modifier = Modifier.padding(horizontal = 24.dp, vertical = 8.dp)
        )
        LazyColumn(
            contentPadding = PaddingValues(horizontal = 24.dp, vertical = 8.dp),
            verticalArrangement = Arrangement.spacedBy(20.dp)
        ) {
            items(list) { order ->
                HistoryOrderItemCard(order = order, onClick = { onClick(order) }, isFirst = list.indexOf(order) == 0)
            }
        }
    }
}

@Composable
fun UpcomingOrderItemCard(order: Order, onClick: () -> Unit) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .shadow(elevation = 16.dp, shape = RoundedCornerShape(20.dp), spotColor = Color(0x22000000))
            .background(Color.White, RoundedCornerShape(20.dp))
            .clickable { onClick() }
            .padding(16.dp)
    ) {
        Column {
            OrderDetailsText(order = order)
            
            Spacer(modifier = Modifier.height(24.dp))
            
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                Column {
                    Text(text = "Estimated Arrival", style = TextStyle(fontSize = 12.sp, color = TextGray))
                    Spacer(modifier = Modifier.height(4.dp))
                    Row(verticalAlignment = Alignment.Bottom) {
                        Text(text = "25", style = TextStyle(fontSize = 32.sp, fontWeight = FontWeight.Bold, color = Color.Black))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(text = "min", style = TextStyle(fontSize = 14.sp, fontWeight = FontWeight.Medium, color = Color.Black), modifier = Modifier.padding(bottom = 6.dp))
                    }
                }
                
                Column(horizontalAlignment = Alignment.End) {
                    Text(text = "Now", style = TextStyle(fontSize = 12.sp, color = TextGray))
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(text = "Food on the way", style = TextStyle(fontSize = 14.sp, fontWeight = FontWeight.SemiBold, color = Color.Black))
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Button(
                    onClick = { /* cancel */ },
                    modifier = Modifier.weight(1f).height(44.dp).border(1.dp, Color(0xFFEEEEEE), RoundedCornerShape(22.dp)),
                    colors = ButtonDefaults.buttonColors(containerColor = Color.White)
                ) {
                    Text(text = "Cancel", style = TextStyle(fontSize = 14.sp, color = Color.Black, fontWeight = FontWeight.SemiBold))
                }
                Spacer(modifier = Modifier.width(16.dp))
                Button(
                    onClick = { onClick() },
                    modifier = Modifier.weight(1f).height(44.dp).shadow(8.dp, RoundedCornerShape(22.dp), spotColor = PrimaryOrange.copy(alpha=0.5f)),
                    colors = ButtonDefaults.buttonColors(containerColor = PrimaryOrange)
                ) {
                    Text(text = "Track Order", style = TextStyle(fontSize = 14.sp, color = Color.White, fontWeight = FontWeight.SemiBold))
                }
            }
        }
    }
}

@Composable
fun HistoryOrderItemCard(order: Order, onClick: () -> Unit, isFirst: Boolean = false) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .shadow(if(isFirst) 20.dp else 8.dp, shape = RoundedCornerShape(20.dp), spotColor = if(isFirst) Color.Blue.copy(alpha = 0.2f) else Color(0x11000000))
            .border(width = if(isFirst) 2.dp else 0.dp, color = if(isFirst) Color(0xFF00A2FF) else Color.Transparent, shape = RoundedCornerShape(20.dp))
            .background(Color.White, RoundedCornerShape(20.dp))
            .padding(16.dp)
    ) {
        Column {
            OrderDetailsText(order = order)
            
            Spacer(modifier = Modifier.height(20.dp))

            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Button(
                    onClick = { /* rate */ },
                    modifier = Modifier.weight(1f).height(40.dp).border(1.dp, Color(0xFFEEEEEE), RoundedCornerShape(20.dp)),
                    colors = ButtonDefaults.buttonColors(containerColor = Color.White)
                ) {
                    Text(text = "Rate", style = TextStyle(fontSize = 14.sp, color = Color.Black, fontWeight = FontWeight.Medium))
                }
                Spacer(modifier = Modifier.width(16.dp))
                Button(
                    onClick = { onClick() },
                    modifier = Modifier.weight(1f).height(40.dp).shadow(8.dp, RoundedCornerShape(20.dp), spotColor = PrimaryOrange.copy(alpha=0.5f)),
                    colors = ButtonDefaults.buttonColors(containerColor = PrimaryOrange)
                ) {
                    Text(text = "Re-Order", style = TextStyle(fontSize = 14.sp, color = Color.White, fontWeight = FontWeight.Medium))
                }
            }
        }
    }
}

@Composable
fun OrderDetailsText(order: Order) {
    Column {
        Row(
            modifier = Modifier.fillMaxWidth()
        ) {

            AsyncImage(
                model = order.restaurant.imageUrl,
                contentDescription = null,
                modifier = Modifier
                    .size(64.dp)
                    .clip(RoundedCornerShape(12.dp)),
                contentScale = androidx.compose.ui.layout.ContentScale.Crop
            )
            Spacer(modifier = Modifier.size(8.dp))
            Column {
                Text(
                    text = order.id,
                    textAlign = androidx.compose.ui.text.style.TextAlign.End,
                    modifier = Modifier.fillMaxWidth(),
                    color = MaterialTheme.colorScheme.primary,
                    style = MaterialTheme.typography.bodyMedium,
                    maxLines = 1
                )
                Text(text = "${order.items.size.toString()} items", color = Color.Gray)
                Text(
                    text = order.restaurant.name,
                    style = MaterialTheme.typography.titleMedium,
                    color = Color.Black
                )
            }
        }
        Text(text = "Status", color = Color.Gray)
        Text(text = order.status, color = Color.Black)
        Spacer(modifier = Modifier.size(16.dp))
    }
}

