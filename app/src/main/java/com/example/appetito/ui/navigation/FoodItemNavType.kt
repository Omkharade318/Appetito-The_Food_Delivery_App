package com.example.appetito.ui.navigation

import android.os.Bundle
import androidx.navigation.NavType
import com.example.appetito.data.models.FoodItem
import kotlinx.serialization.json.Json
import java.net.URLDecoder
import java.net.URLEncoder

val foodItemNavType = object : NavType<FoodItem>(false){
    override fun get(
        bundle: Bundle,
        key: String
    ): FoodItem? {
        val value = bundle.getString(key)
        return value?.let { parseValue(it) }
    }

    override fun parseValue(value: String): FoodItem {
        val decodedValue = URLDecoder.decode(value, "UTF-8")
        return Json.decodeFromString(FoodItem.serializer(), decodedValue)
    }

    override fun serializeAsValue(value: FoodItem): String {
        val jsonString = Json.encodeToString(FoodItem.serializer(), value)
        return URLEncoder.encode(jsonString, "UTF-8")
    }

    override fun put(
        bundle: Bundle,
        key: String,
        value: FoodItem
    ) {
        bundle.putString(key, serializeAsValue(value))
    }

}