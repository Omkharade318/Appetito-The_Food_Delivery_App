@file:OptIn(ExperimentalSharedTransitionApi::class)

package com.example.appetito.ui.features.restaurant_details

import androidx.compose.animation.AnimatedVisibilityScope
import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.animation.SharedTransitionScope
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
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
import com.example.appetito.ui.gridItems
import ir.kaaveh.sdpcompose.sdp

@Composable
fun SharedTransitionScope.RestaurantDetailsScreen(
    navController: NavController,
    name : String,
    imageUrl: String,
    restaurantId: String,
    animatedVisibilityScope: AnimatedVisibilityScope,
    viewModel: RestaurantViewModel = hiltViewModel()
){

    LaunchedEffect(restaurantId) {
        viewModel.getFoodItem(restaurantId)
    }
    
    val uiState = viewModel.uiState.collectAsState()

    LazyColumn(
        modifier = Modifier.fillMaxSize()
    ) {
        item {
            RestaurantDetailHeader(
                imageUrl = imageUrl,
                restaurantId = restaurantId,
                animatedVisibilityScope = animatedVisibilityScope,
                onBackButton = {navController.popBackStack()},
                onFavouriteButton = {}
            )

        }

        item {
            RestaurantDetails(
                title = name,
                description = "Lorem ipsum dolor sit amet",
                restaurantId = restaurantId,
                animatedVisibilityScope = animatedVisibilityScope
            )
        }

        when(uiState.value){

            is RestaurantViewModel.RestaurantEvent.Loading -> {
                item {
                   Column(
                       modifier = Modifier.fillMaxSize(),
                       horizontalAlignment = Alignment.CenterHorizontally,
                       verticalArrangement = Arrangement.Center
                   ) {
                       CircularProgressIndicator()
                       Text(text = "Loading...")
                   }
                }
            }

            is RestaurantViewModel.RestaurantEvent.Success -> {
                val foodItems = (uiState.value as RestaurantViewModel.RestaurantEvent.Success).foodItems

                if(foodItems.isNotEmpty()){
                    gridItems(foodItems, 2){ foodItem ->
                        FoodItemView(foodItem = foodItem)
                    }
                } else{
                    item {
                        Text(text = "No Food Items Found")
                    }
                }

            }

            is RestaurantViewModel.RestaurantEvent.Error -> {
                item {

                    Text(text = "Error")
                }
            }

            RestaurantViewModel.RestaurantEvent.Nothing -> {

            }

        }


    }

}

@Composable
fun SharedTransitionScope.RestaurantDetails(
    title: String,
    description: String,
    restaurantId: String,
    animatedVisibilityScope: AnimatedVisibilityScope
){

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Text(
            text = title,
            style = MaterialTheme.typography.titleLarge,
            modifier = Modifier
                .sharedElement(
                    state = rememberSharedContentState(key = "title/$restaurantId"),
                    animatedVisibilityScope = animatedVisibilityScope
                )
        )

        Spacer(modifier = Modifier.size(8.dp))

        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(
                imageVector = Icons.Filled.Star,
                contentDescription = null,
                modifier = Modifier.size(26.dp),
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

            TextButton(onClick = {}) {
                Text(
                    text = "View All Reviews",
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


@Composable
fun SharedTransitionScope.RestaurantDetailHeader(
    imageUrl: String,
    restaurantId: String,
    animatedVisibilityScope: AnimatedVisibilityScope,
    onBackButton:()-> Unit,
    onFavouriteButton:() -> Unit
){

    Box(modifier = Modifier.fillMaxWidth()){
        AsyncImage(
            model = imageUrl,
            contentDescription = null,
            modifier = Modifier
                .fillMaxWidth()
                .height(200.dp)
                .sharedElement(
                    state = rememberSharedContentState(key = "image/$restaurantId"),
                    animatedVisibilityScope = animatedVisibilityScope
                )
                .clip(RoundedCornerShape(bottomStart = 16.dp, bottomEnd = 16.dp)),
            contentScale = ContentScale.Crop
        )

        IconButton(
            onClick = onBackButton,
            modifier = Modifier
                .padding(16.dp)
                .size(48.sdp)
                .align(Alignment.TopStart)
        ) {
            Image(
                painter = painterResource(id = R.drawable.ic_back),
                contentDescription = null,
                modifier = Modifier.size(48.sdp)
            )
        }

        IconButton(
            onClick = onFavouriteButton,
            modifier = Modifier
                .padding(16.dp)
                .size(48.sdp)
                .align(Alignment.TopEnd)
        ) {
            Image(
                painter = painterResource(id = R.drawable.ic_favourite),
                contentDescription = null,
                modifier = Modifier.size(48.sdp)
            )
        }
    }

}

@Composable
fun FoodItemView(foodItem: FoodItem){

    Column(
        modifier = Modifier
            .padding(8.dp)
            .width(162.dp)
            .height(216.dp)
            .shadow(
                elevation = 16.dp,
                shape = RoundedCornerShape(16.dp),
                ambientColor = Color.Gray.copy(alpha = 0.8f),
                spotColor = Color.Gray.copy(alpha = 0.8f)
            )
            .background(Color.White)
            .clip(RoundedCornerShape(16.dp))
    ) {

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(147.dp)
        ){
            AsyncImage(
                model = foodItem.imageUrl, contentDescription = null,
                modifier = Modifier
                    .fillMaxSize()
                    .clip(RoundedCornerShape(16.dp)),
                contentScale = ContentScale.Crop
            )

            Text(
                text = "$${foodItem.price}",
                style = MaterialTheme.typography.bodySmall,
                modifier = Modifier
                    .padding(8.dp)
                    .clip(RoundedCornerShape(8.dp))
                    .background(Color.White)
                    .padding(horizontal = 16.dp)
                    .align(Alignment.TopStart)
            )


            Image(
                painter = painterResource(id = R.drawable.ic_favourite),
                contentDescription = null,
                modifier = Modifier
                    .size(32.sdp)
                    .clip(CircleShape)
                    .align(Alignment.TopEnd)
            )


            Row(
                modifier = Modifier
                    .align(Alignment.BottomStart)
                    .clip(RoundedCornerShape(16.dp))
                    .background(Color.White)
                    .padding(horizontal = 8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "4.5",
                    style = MaterialTheme.typography.titleSmall,
                )

                Spacer(modifier = Modifier.size(8.dp))

                Icon(
                    imageVector = Icons.Filled.Star,
                    contentDescription = null,
                    modifier = Modifier.size(14.dp)
                )

                Spacer(modifier = Modifier.size(8.dp))

                Text(
                    text = "(21)",
                    style = MaterialTheme.typography.bodySmall,
                    color = Color.Gray
                )
            }


        }

        Column(
            modifier = Modifier
                .padding( 8.dp)
                .fillMaxWidth()
        ) {
            Text(
                text = foodItem.name,
                style = MaterialTheme.typography.bodyMedium,
                maxLines = 1
            )

            Text(
                text = foodItem.description,
                style = MaterialTheme.typography.bodyMedium,
                color = Color.Gray,
                maxLines = 1
            )
        }
    }
}