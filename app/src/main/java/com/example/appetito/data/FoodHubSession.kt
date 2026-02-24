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
}