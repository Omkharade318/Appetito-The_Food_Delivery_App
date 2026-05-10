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
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.outlined.AccessTime
import androidx.compose.material.icons.outlined.DirectionsBike
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import coil3.compose.AsyncImage
import com.example.appetito.R
import com.example.appetito.data.models.FoodItem
import com.example.appetito.ui.features.common.FoodItemView
import com.example.appetito.ui.gridItems
import com.example.appetito.ui.navigation.FoodDetails
import ir.kaaveh.sdpcompose.sdp

val PrimaryOrange = Color(0xFFFE724C)
val TextGray = Color(0xFF9796A1)
val BgLight = Color(0xFFF9F9FB)

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

    Box(modifier = Modifier.fillMaxSize().background(BgLight)) {
        LazyColumn(modifier = Modifier.fillMaxSize()) {

            // 1. Header Image
            item {
                RestaurantDetailsHeader(
                    imageUrl = imageUrl,
                    restaurantID = restaurantID,
                    animatedVisibilityScope = animatedVisibilityScope,
                    onBackButton = { navController.popBackStack() },
                    onFavoriteButton = { }
                )
            }

            // 2. Overlapping Details Card
            item {
                RestaurantDetails(
                    title = name,
                    description = "Lorem ipsum dolor sit amet, consectetur adipiscing elit. Sed ut purus eget sapien fermentum aliquam. Nullam nec nunc nec libero fermentum aliquam.",
                    animatedVisibilityScope = animatedVisibilityScope,
                    restaurantID = restaurantID
                )
            }

            // 3. Menu Content
            when (uiState.value) {
                is RestaurantViewModel.RestaurantEvent.Loading -> {
                    item {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            modifier = Modifier.fillMaxWidth().padding(top = 40.dp),
                            verticalArrangement = Arrangement.Center
                        ) {
                            CircularProgressIndicator(color = PrimaryOrange)
                            Spacer(modifier = Modifier.height(12.dp))
                            Text(text = "Loading Menu...", style = TextStyle(color = TextGray))
                        }
                    }
                }

                is RestaurantViewModel.RestaurantEvent.Success -> {
                    val foodItems = (uiState.value as RestaurantViewModel.RestaurantEvent.Success).foodItems
                    if (foodItems.isNotEmpty()) {
                        item {
                            Text(
                                text = "Popular Menu",
                                style = TextStyle(fontSize = 20.sp, fontWeight = FontWeight.Bold, color = Color(0xFF323643)),
                                modifier = Modifier.padding(horizontal = 24.dp, vertical = 8.dp)
                            )
                        }

                        gridItems(foodItems, 2) { foodItem ->
                            Box(modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp)) {
                                FoodItemView(footItem = foodItem, animatedVisibilityScope) {
                                    navController.navigate(FoodDetails(foodItem))
                                }
                            }
                        }

                        item { Spacer(modifier = Modifier.height(32.dp)) }
                    } else {
                        item {
                            Box(modifier = Modifier.fillMaxWidth().padding(top = 40.dp), contentAlignment = Alignment.Center) {
                                Text(text = "No items available", style = TextStyle(fontSize = 16.sp, color = TextGray))
                            }
                        }
                    }
                }

                is RestaurantViewModel.RestaurantEvent.Error -> {
                    item {
                        Box(modifier = Modifier.fillMaxWidth().padding(top = 40.dp), contentAlignment = Alignment.Center) {
                            Text(text = "Failed to load menu", style = TextStyle(color = Color.Red))
                        }
                    }
                }

                RestaurantViewModel.RestaurantEvent.Nothing -> {}
            }
        }
    }
}

@OptIn(ExperimentalSharedTransitionApi::class)
@Composable
fun SharedTransitionScope.RestaurantDetails(
    title: String,
    description: String,
    restaurantID: String,
    animatedVisibilityScope: AnimatedVisibilityScope,
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .offset(y = (-40).dp) // This pulls the card UP to overlap the image
            .padding(horizontal = 24.dp)
            .shadow(elevation = 12.dp, shape = RoundedCornerShape(24.dp), spotColor = Color(0x1A000000))
            .background(Color.White, RoundedCornerShape(24.dp))
            .padding(24.dp)
    ) {
        Column {
            Text(
                text = title,
                style = TextStyle(fontSize = 26.sp, fontWeight = FontWeight.Bold, color = Color(0xFF323643)),
                modifier = Modifier.sharedElement(
                    state = rememberSharedContentState(key = "title/${restaurantID}"),
                    animatedVisibilityScope
                )
            )

            Spacer(modifier = Modifier.height(12.dp))

            // Rating Row
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = Icons.Filled.Star,
                    contentDescription = null,
                    modifier = Modifier.size(18.dp),
                    tint = Color(0xFFFFC529)
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text(text = "4.5", style = TextStyle(fontSize = 14.sp, fontWeight = FontWeight.Bold, color = Color(0xFF323643)))
                Spacer(modifier = Modifier.width(4.dp))
                Text(text = "(30+)", style = TextStyle(fontSize = 12.sp, color = TextGray))
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "See Reviews",
                    style = TextStyle(fontSize = 12.sp, color = PrimaryOrange, textDecoration = TextDecoration.Underline, fontWeight = FontWeight.Medium),
                    modifier = Modifier.clickable { /* Handle click */ }
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Delivery Info Row
            Row(verticalAlignment = Alignment.CenterVertically) {
                InfoPill(icon = Icons.Outlined.DirectionsBike, text = "Free Delivery")
                Spacer(modifier = Modifier.width(16.dp))
                InfoPill(icon = Icons.Outlined.AccessTime, text = "10-15 mins")
            }

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = description,
                style = TextStyle(fontSize = 14.sp, color = TextGray, lineHeight = 22.sp)
            )
        }
    }
}

@Composable
fun InfoPill(icon: ImageVector, text: String) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Icon(imageVector = icon, contentDescription = null, tint = PrimaryOrange, modifier = Modifier.size(18.dp))
        Spacer(modifier = Modifier.width(6.dp))
        Text(text = text, style = TextStyle(fontSize = 13.sp, color = TextGray))
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
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(280.dp) // Made taller to accommodate the overlap
    ) {
        AsyncImage(
            model = imageUrl,
            contentDescription = null,
            modifier = Modifier
                .fillMaxSize()
                .sharedElement(
                    state = rememberSharedContentState(key = "image/${restaurantID}"),
                    animatedVisibilityScope
                ),
            contentScale = ContentScale.Crop
        )

        // App Bar Overlay
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 40.dp, start = 24.dp, end = 24.dp),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            // Back Button
            Box(
                modifier = Modifier
                    .size(44.dp)
                    .shadow(12.dp, shape = RoundedCornerShape(12.dp), spotColor = Color(0x33000000))
                    .background(Color.White, RoundedCornerShape(12.dp))
                    .clickable { onBackButton() },
                contentAlignment = Alignment.Center
            ) {
                Image(painter = painterResource(id = R.drawable.ic_back), contentDescription = "Back")
            }

            // Favorite Button
            Box(
                modifier = Modifier
                    .size(44.dp)
                    .shadow(12.dp, shape = CircleShape, spotColor = PrimaryOrange.copy(alpha = 0.5f))
                    .background(Color.White, CircleShape)
                    .clickable { onFavoriteButton() },
                contentAlignment = Alignment.Center
            ) {
                Image(painter = painterResource(id = R.drawable.ic_favourite), contentDescription = "Favorite")
            }
        }
    }
}