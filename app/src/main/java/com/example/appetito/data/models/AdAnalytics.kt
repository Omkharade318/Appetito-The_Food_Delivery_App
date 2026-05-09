package com.example.appetito.data.models

data class AdAnalytics(
    val adId: String,
    val totalClicks: Int,
    val uniqueUsers: Int,
    val totalRevenue: Double,
    val cost: Double,
    val profit: Double,
    val clickData: List<ClickDataPoint>
)

data class ClickDataPoint(
    val date: String,
    val count: Int
)
