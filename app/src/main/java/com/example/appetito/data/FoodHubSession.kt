package com.example.appetito.data

import android.content.Context
import android.content.SharedPreferences

class FoodHubSession(val context: Context) {

    val sharedPrefs: SharedPreferences =
        context.getSharedPreferences("foodhub", Context.MODE_PRIVATE)

    fun storeToken(token: String){
        sharedPrefs.edit().putString("token", token).apply()
    }

    fun getToken() : String? {
        sharedPrefs.getString("token", null)?.let {
            return it
        }
        return null
    }

    fun storeRestaurantId(restaurantId: String) {
        sharedPrefs.edit().putString("restaurantId", restaurantId).apply()
    }

    fun getRestaurantId(): String? {
        sharedPrefs.getString("restaurantId", null)?.let {
            return it
        }
        return null
    }

    fun storeAdId(adId: String) {
        sharedPrefs.edit().putString("adId", adId).apply()
    }

    fun getAdId(): String? {
        return sharedPrefs.getString("adId", null)
    }

    fun clearAdId() {
        sharedPrefs.edit().remove("adId").apply()
    }
}