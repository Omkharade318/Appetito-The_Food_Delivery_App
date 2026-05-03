package com.example.appetito.data.models

data class PaymentIntentResponse(
    val amount: Int,
    val currency: String,
    val customerId: String,
    val ephemeralKeySecret: String,
    val paymentIntentClientSecret: String,
    val paymentIntentId: String,
    val publishableKey: String,
    val status: String
)