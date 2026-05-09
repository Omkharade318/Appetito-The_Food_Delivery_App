package com.example.appetito.data.models

data class PaymentIntentRequest(
    val addressId: String,
    val adId: String? = null
)
