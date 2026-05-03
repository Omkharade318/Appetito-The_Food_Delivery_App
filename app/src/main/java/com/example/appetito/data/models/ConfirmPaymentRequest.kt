package com.example.appetito.data.models

data class ConfirmPaymentRequest(
    val paymentIntentId: String,
    val addressId: String
)