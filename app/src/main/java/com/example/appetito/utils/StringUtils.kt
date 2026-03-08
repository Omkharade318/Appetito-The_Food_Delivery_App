package com.example.appetito.utils

object StringUtils {

    fun formatCurrency(value: Double): String{

        val currencyFormatter = java.text.NumberFormat.getCurrencyInstance()
        currencyFormatter.currency = java.util.Currency.getInstance("USD")

        return currencyFormatter.format(value)
    }
}