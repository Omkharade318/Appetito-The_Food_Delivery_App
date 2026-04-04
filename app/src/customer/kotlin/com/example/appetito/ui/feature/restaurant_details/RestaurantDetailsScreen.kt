package com.example.appetito.ui.feature.restaurant_details

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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import coil3.compose.AsyncImage
import com.example.appetito.R
import com.example.appetito.data.models.FoodItem
import com.example.appetito.ui.features.common.FoodItemView
import com.example.appetito.ui.gridItems
import com.example.appetito.ui.navigation.FoodDetails
import ir.kaaveh.sdpcompose.sdp

@OptIn(ExperimentalSharedTransitionApi::class)
@Composable
fun SharedTransitionScope.RestaurantDetailsScreen(
    navController: NavController,
    name: String,
    imageUrl: String,
    restaurantID: String,
    animatedVisibilityScope: AnimatedVisibilityScope,
    viewModel: RestaurantViewModel = hiltViewModel()
) {
    LaunchedEffect(restaurantID) {
        viewModel.getFoodItem(restaurantID)
    }
    val uiState = viewModel.uiState.collectAsState()
    LazyColumn(modifier = Modifier.fillMaxSize()) {
        item {
            RestaurantDetailsHeader(imageUrl = imageUrl,
                restaurantID = restaurantID,
                animatedVisibilityScope = animatedVisibilityScope,
                onBackButton = { navController.popBackStack() },
                onFavoriteButton = { })
        }
        item {
            RestaurantDetails(
                title = name,
                description = "Lorem ipsum dolor sit amet, consectetur adipiscing elit. Sed ut purus eget sapien fermentum aliquam. Nullam nec nunc nec libero fermentum aliquam. Nullam nec nunc nec libero fermentum aliquam.",
                animatedVisibilityScope = animatedVisibilityScope,
                restaurantID = restaurantID
            )
        }
        when (uiState.value) {
            is RestaurantViewModel.RestaurantEvent.Loading -> {
                item {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier.fillMaxWidth(),
                        verticalArrangement = Arrangement.Center
                    ) {
                        CircularProgressIndicator()
                        Text(text = "Loading")
                    }
                }
            }

            is RestaurantViewModel.RestaurantEvent.Success -> {
                val foodItems =
                    (uiState.value as RestaurantViewModel.RestaurantEvent.Success).foodItems
                if (foodItems.isNotEmpty()) {
                    gridItems(foodItems, 2) { foodItem ->
                        FoodItemView(footItem = foodItem, animatedVisibilityScope) {
                            navController.navigate(
                                FoodDetails(foodItem)
                            )
                        }
                    }
                } else {
                    item {
                        Text(text = "No Food Items")
                    }
                }

            }

            is RestaurantViewModel.RestaurantEvent.Error -> {
                item {
                    Text(text = "Error")
                }
            }

            RestaurantViewModel.RestaurantEvent.Nothing -> {}
        }
    }
}

@OptIn(ExperimentalSharedTransitionApi::class)
@Composable
fun SharedTransitionScope.RestaurantDetails(
    title: String, description: String, restaurantID: String,
    animatedVisibilityScope: AnimatedVisibilityScope,
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
    ) {
        Text(
            text = title,
            style = MaterialTheme.typography.titleLarge,
            modifier = Modifier.sharedElement(
                state = rememberSharedContentState(key = "title/${restaurantID}"),
                animatedVisibilityScope
            )
        )
        Spacer(modifier = Modifier.size(8.dp))
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(
                imageVector = Icons.Filled.Star,
                contentDescription = null,
                modifier = Modifier.size(24.dp),
                tint = MaterialTheme.colorScheme.primary
            )
            Spacer(modifier = Modifier.size(8.dp))
            Text(
                text = "4.5",
                style = MaterialTheme.typography.bodyMedium,
                modifier = Modifier.align(Alignment.CenterVertically)
            )
            Spacer(modifier = Modifier.size(8.dp))
            Text(
                text = "(30+)",
                style = MaterialTheme.typography.bodyMedium,
                modifier = Modifier.align(Alignment.CenterVertically)
            )
            Spacer(modifier = Modifier.size(8.dp))
            TextButton(onClick = { /*TODO*/ }) {
                Text(
                    text = " View All Reviews",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.primary
                )
            }

        }
        Spacer(modifier = Modifier.size(8.dp))
        Text(
            text = description,
            style = MaterialTheme.typography.bodyMedium,
            modifier = Modifier.padding(top = 8.dp)
        )
    }
}

@OptIn(ExperimentalSharedTransitionApi::class)
@Composable
fun SharedTransitionScope.RestaurantDetailsHeader(
    imageUrl: String,
    restaurantID: String,
    animatedVisibilityScope: AnimatedVisibilityScope,
    onBackButton: () -> Unit,
    onFavoriteButton: () -> Unit
) {
    Box(modifier = Modifier.fillMaxWidth()) {
        AsyncImage(
            model = imageUrl, contentDescription = null, modifier = Modifier
                .fillMaxWidth()
                .height(200.dp)
                .sharedElement(
                    state = rememberSharedContentState(key = "image/${restaurantID}"),
                    animatedVisibilityScope
                )
                .clip(
                    RoundedCornerShape(bottomStart = 16.dp, bottomEnd = 16.dp)
                ), contentScale = ContentScale.Crop
        )
        IconButton(
            onClick = onBackButton,
            modifier = Modifier
                .padding(16.dp)
                .size(48.dp)
                .align(Alignment.TopStart)
        ) {
            Image(painter = painterResource(id = R.drawable.ic_back), contentDescription = null)
        }
        IconButton(
            onClick = onFavoriteButton,
            modifier = Modifier
                .padding(16.dp)
                .size(48.dp)
                .align(Alignment.TopEnd)
        ) {
            Image(
                painter = painterResource(id = R.drawable.ic_favourite), contentDescription = null
            )
        }
    }
}