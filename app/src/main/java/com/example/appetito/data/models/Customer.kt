package com.example.appetito.data.models

data class Customer(
    val addressLine1: String,
    val addressLine2: String,
    val city: String,
    val latitude: Double,
    val longitude: Double,
    val state: String,
    val zipCode: String
)