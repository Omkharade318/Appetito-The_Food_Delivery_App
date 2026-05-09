package com.example.appetito.ui.feature.home

import android.graphics.Paint
import android.graphics.Typeface
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.rounded.AttachMoney
import androidx.compose.material.icons.rounded.CheckCircle
import androidx.compose.material.icons.rounded.People
import androidx.compose.material.icons.rounded.TouchApp
import androidx.compose.material.icons.rounded.TrendingDown
import androidx.compose.material.icons.rounded.TrendingUp
import androidx.compose.material.icons.rounded.Warning
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.graphics.vector.ImageVector
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

// Colors
val SurfaceWhite = Color(0xFFFFFFFF)

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
        contentWindowInsets = WindowInsets(0, 0, 0, 0),
        modifier = Modifier.padding(0.dp),
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        "Ad Insights",
                        fontWeight = FontWeight.ExtraBold,
                        fontSize = 22.sp
                    )
                },
                navigationIcon = {
                    IconButton(
                        onClick = { navController.popBackStack() },
                        modifier = Modifier
                            .padding(start = 8.dp)
                            .background(SurfaceWhite, CircleShape)
                    ) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = BgLight,
                    scrolledContainerColor = BgLight
                )
            )
        },
        containerColor = BgLight
    ) { padding ->
        Box(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize()
        ) {
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
            .padding(horizontal = 20.dp, vertical = 8.dp)
    ) {
        // First Row: Clicks & Users
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(16.dp)) {
            StatCard(
                title = "Total Clicks",
                value = analytics.totalClicks.toString(),
                icon = Icons.Rounded.TouchApp,
                modifier = Modifier.weight(1f),
                containerColor = Color(0xFFEAF4FF),
                contentColor = Color(0xFF0061D5)
            )
            StatCard(
                title = "Unique Users",
                value = analytics.uniqueUsers.toString(),
                icon = Icons.Rounded.People,
                modifier = Modifier.weight(1f),
                containerColor = Color(0xFFF4EAFF),
                contentColor = Color(0xFF6C22D6)
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Second Row: Revenue & Profit
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(16.dp)) {
            StatCard(
                title = "Revenue",
                value = "$${String.format("%.2f", analytics.totalRevenue)}",
                icon = Icons.Rounded.AttachMoney,
                modifier = Modifier.weight(1f),
                containerColor = Color(0xFFE8F8F0),
                contentColor = Color(0xFF1B8753)
            )
            val isProfitable = analytics.profit >= 0
            StatCard(
                title = "Profit",
                value = "$${String.format("%.2f", analytics.profit)}",
                icon = if (isProfitable) Icons.Rounded.TrendingUp else Icons.Rounded.TrendingDown,
                modifier = Modifier.weight(1f),
                containerColor = if (isProfitable) Color(0xFFFFF2E5) else Color(0xFFFFEBEE),
                contentColor = if (isProfitable) PrimaryOrange else Color(0xFFD32F2F)
            )
        }

        Spacer(modifier = Modifier.height(32.dp))

        // Graphical Representation (Click Chart)
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(24.dp),
            colors = CardDefaults.cardColors(containerColor = SurfaceWhite),
            elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
        ) {
            Column(modifier = Modifier.padding(24.dp)) {
                Text(
                    text = "Engagement Overview",
                    fontWeight = FontWeight.Bold,
                    fontSize = 18.sp,
                    color = Color(0xFF2D3142)
                )
                Text(
                    text = "Clicks over the last period",
                    fontSize = 13.sp,
                    color = Color(0xFF9C9EAD)
                )
                Spacer(modifier = Modifier.height(32.dp))
                ClickChart(analytics.clickData)
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        // Profitability Status
        val isProfitable = analytics.profit > 0
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(
                containerColor = if (isProfitable) Color(0xFFE8F8F0) else Color(0xFFFFEBEE)
            ),
            elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
        ) {
            Row(
                modifier = Modifier.padding(20.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(48.dp)
                        .background(
                            color = if (isProfitable) Color(0xFF1B8753).copy(alpha = 0.1f)
                            else Color(0xFFD32F2F).copy(alpha = 0.1f),
                            shape = CircleShape
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = if (isProfitable) Icons.Rounded.CheckCircle else Icons.Rounded.Warning,
                        contentDescription = null,
                        tint = if (isProfitable) Color(0xFF1B8753) else Color(0xFFD32F2F),
                        modifier = Modifier.size(28.dp)
                    )
                }

                Spacer(modifier = Modifier.width(16.dp))

                Column {
                    Text(
                        text = if (isProfitable) "Highly Profitable!" else "Needs Optimization",
                        fontWeight = FontWeight.Bold,
                        fontSize = 17.sp,
                        color = if (isProfitable) Color(0xFF13663E) else Color(0xFFB71C1C)
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = if (isProfitable)
                            "Generating positive ROI. Consider scaling your budget."
                        else "Cost is higher than revenue. Review your targeting strategy.",
                        fontSize = 13.sp,
                        lineHeight = 18.sp,
                        color = if (isProfitable) Color(0xFF1B8753).copy(alpha = 0.8f) else Color(0xFFD32F2F).copy(alpha = 0.8f)
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(40.dp))
    }
}

@Composable
fun StatCard(
    title: String,
    value: String,
    icon: ImageVector,
    modifier: Modifier,
    containerColor: Color,
    contentColor: Color
) {
    Card(
        modifier = modifier,
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = containerColor),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(36.dp)
                        .background(contentColor.copy(alpha = 0.15f), CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = icon,
                        contentDescription = null,
                        tint = contentColor,
                        modifier = Modifier.size(20.dp)
                    )
                }
            }
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = value,
                fontSize = 24.sp,
                fontWeight = FontWeight.ExtraBold,
                color = contentColor
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = title,
                fontSize = 13.sp,
                fontWeight = FontWeight.SemiBold,
                color = contentColor.copy(alpha = 0.7f)
            )
        }
    }
}

@Composable
fun ClickChart(data: List<ClickDataPoint>) {
    if (data.isEmpty()) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(220.dp),
            contentAlignment = Alignment.Center
        ) {
            Text("No data available", color = Color.Gray)
        }
        return
    }

    val maxClicks = data.maxOfOrNull { it.count }?.coerceAtLeast(1) ?: 1

    Canvas(
        modifier = Modifier
            .fillMaxWidth()
            .height(220.dp)
    ) {
        val width = size.width
        val height = size.height - 30.dp.toPx() // Leave room for text

        // 1. Draw horizontal background grid lines
        val gridLines = 4
        for (i in 0..gridLines) {
            val yPos = height - (height * (i.toFloat() / gridLines))
            drawLine(
                color = Color.LightGray.copy(alpha = 0.4f),
                start = Offset(0f, yPos),
                end = Offset(width, yPos),
                strokeWidth = 1.dp.toPx(),
                pathEffect = PathEffect.dashPathEffect(floatArrayOf(10f, 10f), 0f)
            )
        }

        // Calculate bar dimensions
        val barWidth = width / (data.size * 2f)
        val spacing = width / (data.size * 2f)

        // Gradient for bars
        val barBrush = Brush.verticalGradient(
            colors = listOf(PrimaryOrange, PrimaryOrange.copy(alpha = 0.5f))
        )

        data.forEachIndexed { index, point ->
            // Prevent 0 height bar from disappearing completely
            val normalizedHeight = (point.count.toFloat() / maxClicks).coerceAtLeast(0.05f)
            val barHeight = normalizedHeight * height
            val x = index * (barWidth + spacing) + spacing / 2

            // Draw gradient bar with rounded top corners
            drawRoundRect(
                brush = barBrush,
                topLeft = Offset(x, height - barHeight),
                size = Size(barWidth, barHeight),
                cornerRadius = CornerRadius(6.dp.toPx(), 6.dp.toPx())
            )

            // Text above bar (Count)
            drawContext.canvas.nativeCanvas.drawText(
                point.count.toString(),
                x + barWidth / 2,
                height - barHeight - 8.dp.toPx(),
                Paint().apply {
                    color = Color(0xFF2D3142).toArgb()
                    textSize = 11.sp.toPx()
                    textAlign = Paint.Align.CENTER
                    typeface = Typeface.DEFAULT_BOLD
                }
            )

            // Text below bar (Date)
            drawContext.canvas.nativeCanvas.drawText(
                point.date.takeLast(2), // Just day
                x + barWidth / 2,
                height + 20.dp.toPx(),
                Paint().apply {
                    color = Color(0xFF9C9EAD).toArgb()
                    textSize = 11.sp.toPx()
                    textAlign = Paint.Align.CENTER
                    typeface = Typeface.DEFAULT_BOLD
                }
            )
        }
    }
}