package com.example.appetito.ui.features.food_item_details

import androidx.compose.animation.AnimatedVisibilityScope
import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.animation.SharedTransitionScope
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.example.appetito.R
import androidx.navigation.NavController
import com.example.appetito.data.models.FoodItem
import com.example.appetito.ui.features.restaurant_details.RestaurantDetailHeader
import com.example.appetito.ui.features.restaurant_details.RestaurantDetails

@OptIn(ExperimentalSharedTransitionApi::class)
@Composable
fun SharedTransitionScope.FoodDetailsScreen(
    navController: NavController,
    foodItem: FoodItem,
    animatedVisibilityScope: AnimatedVisibilityScope
) {
    var count by remember { mutableStateOf(1) }

    Box(modifier = Modifier.fillMaxSize()) {

        // Scrollable Content
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(bottom = 90.dp) // space for bottom button
        ) {

            RestaurantDetailHeader(
                imageUrl = foodItem.imageUrl,
                restaurantId = foodItem.id,
                animatedVisibilityScope = animatedVisibilityScope,
                onBackButton = { navController.popBackStack() },
                onFavouriteButton = {}
            )

            RestaurantDetails(
                title = foodItem.name,
                description = foodItem.description,
                restaurantId = foodItem.id,
                animatedVisibilityScope = animatedVisibilityScope
            )

            // Price + Counter
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center
            ) {

                Text(
                    text = "$${foodItem.price}",
                    color = MaterialTheme.colorScheme.primary,
                    style = MaterialTheme.typography.headlineLarge
                )

                Spacer(modifier = Modifier.weight(1f))

                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.Center) {

                    Image(
                        modifier = Modifier
                            .clip(CircleShape)
                            .size(36.dp)
                            .clickable { if (count > 1) count-- },
                        painter = painterResource(id = R.drawable.ic_minus),
                        contentDescription = null
                    )

                    Spacer(modifier = Modifier.size(8.dp))

                    Text(
                        text = count.toString(),
                        style = MaterialTheme.typography.titleMedium
                    )

                    Spacer(modifier = Modifier.size(8.dp))

                    Image(
                        modifier = Modifier
                            .clip(CircleShape)
                            .size(36.dp)
                            .clickable { count++ },
                        painter = painterResource(id = R.drawable.ic_add),
                        contentDescription = null
                    )
                }
            }
        }

        // Bottom Fixed Button
        Button(
            onClick = {},
            shape = RoundedCornerShape(32.dp),
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .fillMaxWidth()
                .padding(16.dp)
                .height(56.dp)
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Image(
                    painter = painterResource(id = R.drawable.ic_cart),
                    contentDescription = null
                )
                Spacer(modifier = Modifier.size(8.dp))
                Text(
                    text = "ADD TO CART",
                    style = MaterialTheme.typography.titleMedium
                )
            }
        }
    }
}