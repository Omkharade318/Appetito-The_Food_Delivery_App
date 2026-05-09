package com.example.appetito.ui.feature.home


import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import com.example.appetito.data.models.AdAnalytics
import com.example.appetito.data.models.ClickDataPoint
import com.example.appetito.ui.features.notifications.ErrorScreen
import com.example.appetito.ui.features.notifications.LoadingScreen

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdDetailsScreen(
    navController: NavController,
    adId: String,
    viewModel: AdDetailsViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    LaunchedEffect(adId) {
        viewModel.loadAnalytics(adId)
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Advertisement Insights", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) { padding ->
        Box(modifier = Modifier.padding(padding).fillMaxSize().background(Color(0xFFF9F9FB))) {
            when (uiState) {
                is AdDetailsViewModel.AdDetailsState.Loading -> LoadingScreen()
                is AdDetailsViewModel.AdDetailsState.Success -> {
                    val analytics = (uiState as AdDetailsViewModel.AdDetailsState.Success).data
                    AdDetailsContent(analytics)
                }
                is AdDetailsViewModel.AdDetailsState.Error -> {
                    ErrorScreen(message = (uiState as AdDetailsViewModel.AdDetailsState.Error).message) {
                        viewModel.loadAnalytics(adId)
                    }
                }
            }
        }
    }
}

@Composable
fun AdDetailsContent(analytics: AdAnalytics) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp)
    ) {
        // Summary Cards
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            StatCard(
                title = "Total Clicks",
                value = analytics.totalClicks.toString(),
                modifier = Modifier.weight(1f),
                containerColor = Color(0xFFE3F2FD),
                contentColor = Color(0xFF1976D2)
            )
            StatCard(
                title = "Unique Users",
                value = analytics.uniqueUsers.toString(),
                modifier = Modifier.weight(1f),
                containerColor = Color(0xFFF3E5F5),
                contentColor = Color(0xFF7B1FA2)
            )
        }
        
        Spacer(modifier = Modifier.height(12.dp))
        
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            StatCard(
                title = "Revenue",
                value = "$${String.format("%.2f", analytics.totalRevenue)}",
                modifier = Modifier.weight(1f),
                containerColor = Color(0xFFE8F5E9),
                contentColor = Color(0xFF388E3C)
            )
            StatCard(
                title = "Profit",
                value = "$${String.format("%.2f", analytics.profit)}",
                modifier = Modifier.weight(1f),
                containerColor = if (analytics.profit >= 0) Color(0xFFFFF3E0) else Color(0xFFFFEBEE),
                contentColor = if (analytics.profit >= 0) Color(0xFFF57C00) else Color(0xFFD32F2F)
            )
        }

        Spacer(modifier = Modifier.height(24.dp))

        // Graphical Representation (Click Chart)
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text("Clicks over time", fontWeight = FontWeight.Bold, fontSize = 18.sp, color = Color(0xFFFE724C))
                }
                Spacer(modifier = Modifier.height(24.dp))
                ClickChart(analytics.clickData)
            }
        }

        Spacer(modifier = Modifier.height(24.dp))
        
        // Profitability Status
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(
                containerColor = if (analytics.profit > 0) Color(0xFFE8F5E9) else Color(0xFFFFEBEE)
            )
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    text = if (analytics.profit > 0) "Highly Profitable!" else "Needs Optimization",
                    fontWeight = FontWeight.Bold,
                    fontSize = 18.sp,
                    color = if (analytics.profit > 0) Color(0xFF2E7D32) else Color(0xFFC62828)
                )
                Text(
                    text = if (analytics.profit > 0) 
                        "This advertisement is generating positive ROI. Consider increasing the budget." 
                        else "The cost of this ad is higher than the revenue generated. Review your strategy.",
                    fontSize = 14.sp,
                    color = Color.Black.copy(alpha = 0.7f)
                )
            }
        }
        
        Spacer(modifier = Modifier.height(40.dp))
    }
}

@Composable
fun StatCard(title: String, value: String, modifier: Modifier, containerColor: Color, contentColor: Color) {
    Card(
        modifier = modifier,
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = containerColor)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(title, fontSize = 12.sp, fontWeight = FontWeight.Medium, color = contentColor.copy(alpha = 0.8f))
            Spacer(modifier = Modifier.height(4.dp))
            Text(value, fontSize = 20.sp, fontWeight = FontWeight.ExtraBold, color = contentColor)
        }
    }
}

@Composable
fun ClickChart(data: List<ClickDataPoint>) {
    if (data.isEmpty()) {
        Box(modifier = Modifier.fillMaxWidth().height(200.dp), contentAlignment = Alignment.Center) {
            Text("No data available")
        }
        return
    }

    val maxClicks = data.maxOfOrNull { it.count }?.coerceAtLeast(1) ?: 1
    
    Canvas(modifier = Modifier.fillMaxWidth().height(200.dp)) {
        val width = size.width
        val height = size.height
        val barWidth = width / (data.size * 2)
        val spacing = width / (data.size * 2)

        data.forEachIndexed { index, point ->
            val barHeight = (point.count.toFloat() / maxClicks) * height
            val x = index * (barWidth + spacing) + spacing / 2
            
            drawRoundRect(
                color = PrimaryOrange,
                topLeft = Offset(x, height - barHeight),
                size = Size(barWidth, barHeight),
                cornerRadius = androidx.compose.ui.geometry.CornerRadius(4.dp.toPx())
            )
            
            // Draw text for count above bar
            drawContext.canvas.nativeCanvas.drawText(
                point.count.toString(),
                x + barWidth / 2,
                height - barHeight - 10.dp.toPx(),
                android.graphics.Paint().apply {
                    color = android.graphics.Color.GRAY
                    textSize = 12.sp.toPx()
                    textAlign = android.graphics.Paint.Align.CENTER
                }
            )
            
            // Draw date label
            drawContext.canvas.nativeCanvas.drawText(
                point.date.takeLast(2), // Just day
                x + barWidth / 2,
                height + 15.dp.toPx(),
                android.graphics.Paint().apply {
                    color = android.graphics.Color.GRAY
                    textSize = 10.sp.toPx()
                    textAlign = android.graphics.Paint.Align.CENTER
                }
            )
        }
    }
}
