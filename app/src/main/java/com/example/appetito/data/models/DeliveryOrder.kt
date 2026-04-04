package com.example.appetito.data.models

data class DeliveryOrder(
    val createdAt: String,
    val customer: Customer,
    val estimatedEarning: Double,
    val items: List<DeliveryOrderItem>,
    val orderId: String,
    val restaurant: Restaurant,
    val status: String,
    val totalAmount: Double,
    val updatedAt: String
)