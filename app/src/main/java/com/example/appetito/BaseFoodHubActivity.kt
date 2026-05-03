package com.example.appetito

import android.content.Intent
import androidx.activity.ComponentActivity
import androidx.activity.viewModels
import com.example.appetito.notification.FoodHubMessagingService
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
abstract class BaseFoodHubActivity : ComponentActivity(){

    val viewModel by viewModels<HomeViewModel>()

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        processIntent(intent, viewModel)
    }

    protected fun processIntent(intent: Intent, viewModel: HomeViewModel) {
        if (intent.hasExtra(FoodHubMessagingService.ORDER_ID)) {
            val orderID = intent.getStringExtra(FoodHubMessagingService.ORDER_ID)
            viewModel.navigateToOrderDetail(orderID!!)
            intent.removeExtra(FoodHubMessagingService.ORDER_ID)
        }
    }
}