package com.example.appetito

import android.app.Application
import com.example.appetito.notification.FoodHubNotificationManager
import dagger.hilt.android.HiltAndroidApp
import javax.inject.Inject

@HiltAndroidApp
class AppetitoApp : Application(){

    @Inject
    lateinit var foodHubNotificationManager: FoodHubNotificationManager
    override fun onCreate() {
        super.onCreate()
        foodHubNotificationManager.createChannels()
        foodHubNotificationManager.getAndStoreToken()
    }
}