package com.example.appetito.ui.feature.home

import androidx.compose.animation.AnimatedVisibilityScope
import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.animation.SharedTransitionScope
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Alignment.Companion.CenterHorizontally
import androidx.compose.ui.Alignment.Companion.TopStart
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import coil3.compose.AsyncImage
import kotlinx.coroutines.flow.collectLatest
import com.example.appetito.R
import com.example.appetito.data.models.Category
import com.example.appetito.data.models.Restaurant
import com.example.appetito.ui.navigation.RestaurantDetails

val PrimaryOrange = Color(0xFFFE724C)
val TextGray = Color(0xFF9796A1)
val BgLight = Color(0xFFF9F9FB) // Using this for the main background!

@OptIn(ExperimentalSharedTransitionApi::class)
@Composable
fun SharedTransitionScope.HomeScreen(
    navController: NavController,
    animatedVisibilityScope: AnimatedVisibilityScope,
    viewModel: HomeViewModel = hiltViewModel(),
) {

    LaunchedEffect(Unit) {
        viewModel.navigationEvent.collectLatest {
            when (it) {
                is HomeViewModel.HomeScreenNavigationEvents.NavigateToDetail -> {
                    navController.navigate(RestaurantDetails(it.id, it.name, it.imageUrl))
                }
                is HomeViewModel.HomeScreenNavigationEvents.NavigateToAdDetail -> {
                    // For now, let's just navigate to restaurant details if it's an ad for a restaurant
                    // Or we could have a specific ad detail screen.
                    // The user said "click on them to know more about them".
                    // I'll just show an alert or navigate to restaurant details for now if we don't have an AdDetails screen.
                    // Actually, I'll navigate to RestaurantDetails.
                    navController.navigate(RestaurantDetails(it.ad.restaurantId, it.ad.restaurantName, it.ad.imageUrl))
                }
                else -> {}
            }
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(BgLight) // Applying the lighter background for contrast
            .verticalScroll(rememberScrollState())
    ) {
        HomeHeader()
        
        val ads = viewModel.ads
        if (ads.isNotEmpty()) {
            AdBanner(ads = ads, onAdClick = { viewModel.onAdSelected(it) })
        }

        val uiState = viewModel.uiState.collectAsState()
        when (uiState.value) {
            is HomeViewModel.HomeScreenState.Loading -> {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text(text = "Loading", color = PrimaryOrange)
                }
            }

            is HomeViewModel.HomeScreenState.Empty -> {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text(text = "No items found", color = TextGray)
                }
            }

            is HomeViewModel.HomeScreenState.Success -> {
                val categories = viewModel.categories
                var selectedCategory by remember { mutableStateOf<Category?>(categories.firstOrNull()) }

                CategoriesList(
                    categories = categories,
                    selectedCategory = selectedCategory,
                    onCategorySelected = { selectedCategory = it }
                )

                RestaurantList(
                    title = "Featured Restaurants",
                    restaurants = viewModel.restaurants,
                    animatedVisibilityScope = animatedVisibilityScope,
                    onRestaurantSelected = {
                        viewModel.onRestaurantSelected(it)
                    }
                )

                RestaurantList(
                    title = "Popular Items",
                    restaurants = viewModel.restaurants.reversed(),
                    animatedVisibilityScope = animatedVisibilityScope,
                    onRestaurantSelected = {
                        viewModel.onRestaurantSelected(it)
                    }
                )
                Spacer(modifier = Modifier.height(32.dp))
            }
        }
    }
}

@Composable
fun HomeHeader() {
    Column(modifier = Modifier.padding(top = 16.dp, start = 24.dp, end = 24.dp)) {

        Spacer(modifier = Modifier.height(24.dp))

        Text(
            text = "What would you like\nto order?",
            style = TextStyle(
                fontSize = 32.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF323643),
                lineHeight = 40.sp // Slightly increased line height
            )
        )

        Spacer(modifier = Modifier.height(24.dp))

        // Refined Search Bar
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(54.dp)
                // Replaced hard border with a soft shadow
                .shadow(elevation = 8.dp, shape = RoundedCornerShape(16.dp), spotColor = Color(0x1A000000))
                .background(Color.White, RoundedCornerShape(16.dp))
                .padding(horizontal = 16.dp),
            contentAlignment = Alignment.CenterStart
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(painterResource(id = R.drawable.ic_search), contentDescription = "Search", tint = Color(0xFF767F9D), modifier = Modifier.size(20.dp))
                Spacer(modifier = Modifier.width(12.dp))
                Text("Find for food or restaurant...", color = Color(0xFF9AA0B4), fontSize = 14.sp)
            }
        }

        Spacer(modifier = Modifier.height(24.dp))
    }
}

@Composable
fun CategoriesList(categories: List<Category>, selectedCategory: Category?, onCategorySelected: (Category) -> Unit) {
    LazyRow(
        contentPadding = PaddingValues(horizontal = 24.dp),
        horizontalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        items(categories) {
            CategoryItem(
                category = it,
                isSelected = it == selectedCategory,
                onCategorySelected = onCategorySelected
            )
        }
    }
}

@OptIn(ExperimentalSharedTransitionApi::class)
@Composable
fun SharedTransitionScope.RestaurantList(
    title: String,
    restaurants: List<Restaurant>,
    animatedVisibilityScope: AnimatedVisibilityScope,
    onRestaurantSelected: (Restaurant) -> Unit
) {
    Column {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp, vertical = 16.dp)
                .padding(top = 8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = title,
                style = TextStyle(fontSize = 20.sp, fontWeight = FontWeight.Bold, color = Color(0xFF323643))
            )
            Spacer(modifier = Modifier.weight(1f))
            TextButton(onClick = { /*TODO*/ }) {
                Text(text = "View All >", style = TextStyle(fontSize = 13.sp, color = PrimaryOrange, fontWeight = FontWeight.Medium))
            }
        }
        LazyRow(
            contentPadding = PaddingValues(horizontal = 24.dp),
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            items(restaurants) {
                RestaurantItem(it, animatedVisibilityScope, onRestaurantSelected)
            }
        }
    }
}

@OptIn(ExperimentalSharedTransitionApi::class)
@Composable
fun SharedTransitionScope.RestaurantItem(
    restaurant: Restaurant,
    animatedVisibilityScope: AnimatedVisibilityScope,
    onRestaurantSelected: (Restaurant) -> Unit
) {
    Box(
        modifier = Modifier
            .width(260.dp)
            // Softened the shadow significantly for a cleaner look
            .shadow(elevation = 12.dp, shape = RoundedCornerShape(18.dp), spotColor = Color(0x1A000000))
            .background(Color.White, RoundedCornerShape(18.dp))
            .clickable { onRestaurantSelected(restaurant) }
            .clip(RoundedCornerShape(18.dp))
    ) {
        Column(modifier = Modifier.fillMaxWidth()) {
            Box(modifier = Modifier
                .fillMaxWidth()
                .height(140.dp)) { // Slightly taller image
                AsyncImage(
                    model = restaurant.imageUrl,
                    contentDescription = null,
                    modifier = Modifier
                        .fillMaxSize()
                        .sharedElement(
                            state = rememberSharedContentState(key = "image/${restaurant.id}"),
                            animatedVisibilityScope = animatedVisibilityScope
                        ),
                    contentScale = ContentScale.Crop
                )
                Row(
                    modifier = Modifier
                        .align(TopStart)
                        .padding(12.dp)
                        .shadow(4.dp, RoundedCornerShape(32.dp))
                        .background(Color.White, RoundedCornerShape(32.dp))
                        .padding(horizontal = 10.dp, vertical = 6.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center
                ) {
                    Text(text = "4.5", style = TextStyle(fontSize = 12.sp, fontWeight = FontWeight.Bold))
                    Spacer(modifier = Modifier.width(4.dp))
                    Image(
                        imageVector = Icons.Filled.Star,
                        contentDescription = null,
                        modifier = Modifier.size(12.dp),
                        colorFilter = ColorFilter.tint(Color(0xFFFFC529))
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(text = "(25+)", style = TextStyle(fontSize = 10.sp, color = TextGray))
                }

                // Heart Fav Button
                Box(modifier = Modifier
                    .align(Alignment.TopEnd)
                    .padding(12.dp)
                    .size(32.dp) // slightly larger touch target
                    .background(Color(0x33000000), CircleShape), // added subtle dark background so it pops on light food images
                    contentAlignment = Alignment.Center
                ) {
                    Image(painterResource(id = R.drawable.ic_favourite), contentDescription = "Fav")
                }
            }

            Column(modifier = Modifier
                .background(Color.White)
                .padding(16.dp)) { // Uniform padding

                Text(
                    text = restaurant.name,
                    // Increased title size
                    style = TextStyle(fontSize = 18.sp, fontWeight = FontWeight.Bold, color = Color(0xFF323643)),
                    modifier = Modifier.sharedElement(
                        state = rememberSharedContentState(key = "title/${restaurant.id}"),
                        animatedVisibilityScope = animatedVisibilityScope
                    )
                )

                Spacer(modifier = Modifier.height(10.dp))

                Row(verticalAlignment = Alignment.CenterVertically) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Image(painter = painterResource(id = R.drawable.ic_delivery), contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(text = "Free delivery", style = TextStyle(fontSize = 13.sp, color = TextGray))
                    }
                    Spacer(modifier = Modifier.width(16.dp))
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Image(painter = painterResource(id = R.drawable.ic_time), contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(text = "10-15 mins", style = TextStyle(fontSize = 13.sp, color = TextGray))
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Tags row
                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    TagItem("BURGER")
                    TagItem("CHICKEN")
                    TagItem("FAST FOOD")
                }
            }
        }
    }
}

@Composable
fun TagItem(text: String) {
    Box(
        modifier = Modifier
            .background(Color(0xFFF1F2F6), RoundedCornerShape(6.dp))
            .padding(horizontal = 8.dp, vertical = 6.dp)
    ) {
        Text(text = text, style = TextStyle(fontSize = 10.sp, color = Color(0xFF8A8E9B), fontWeight = FontWeight.SemiBold))
    }
}

@Composable
fun CategoryItem(category: Category, isSelected: Boolean, onCategorySelected: (Category) -> Unit) {
    val bgColor = if(isSelected) PrimaryOrange else Color.White
    val textColor = if(isSelected) Color.White else Color(0xFF67666D)
    val shadowElevation = if(isSelected) 12.dp else 4.dp

    Column(
        modifier = Modifier
            .width(70.dp) // slightly wider
            .height(115.dp) // slightly taller
            .shadow(
                elevation = shadowElevation,
                shape = RoundedCornerShape(35.dp),
                spotColor = if(isSelected) PrimaryOrange.copy(alpha = 0.5f) else Color(0x1A000000)
            )
            .background(color = bgColor, shape = RoundedCornerShape(35.dp))
            .clip(RoundedCornerShape(35.dp))
            .clickable { onCategorySelected(category) }
            .padding(vertical = 10.dp),
        horizontalAlignment = CenterHorizontally,
        verticalArrangement = Arrangement.SpaceBetween
    ) {
        Box(
            modifier = Modifier
                .size(52.dp)
                .background(Color.White, CircleShape)
                .padding(8.dp),
            contentAlignment = Alignment.Center
        ){
            AsyncImage(
                model = category.imageUrl,
                contentDescription = null,
                modifier = Modifier.fillMaxSize(),
                contentScale = ContentScale.Inside
            )
        }
        Text(
            text = category.name,
            style = TextStyle(fontSize = 12.sp, color = textColor, fontWeight = FontWeight.SemiBold),
            textAlign = TextAlign.Center
        )
        Spacer(modifier = Modifier.height(2.dp))
    }
}