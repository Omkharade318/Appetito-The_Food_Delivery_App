package com.example.appetito.ui.feature.food_item_details

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.AnimatedVisibilityScope
import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.animation.SharedTransitionScope
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import coil3.compose.AsyncImage
import com.example.appetito.R
import com.example.appetito.data.models.FoodItem
import com.example.appetito.ui.BasicDialog
import com.example.appetito.ui.feature.restaurant_details.RestaurantDetails
import com.example.appetito.ui.navigation.Cart
import kotlinx.coroutines.flow.collectLatest

val PrimaryOrange = Color(0xFFFE724C)
val TextGray = Color(0xFF9796A1)

@OptIn(ExperimentalSharedTransitionApi::class, ExperimentalMaterial3Api::class)
@Composable
fun SharedTransitionScope.FoodDetailsScreen(
    navController: NavController,
    foodItem: FoodItem,
    animatedVisibilityScope: AnimatedVisibilityScope,
    onItemAddedToCart: () -> Unit,
    viewModel: FoodDetailsViewModel = hiltViewModel()
) {

    val showSuccessDialog = remember { mutableStateOf(false) }
    val showErrorDialog = remember { mutableStateOf(false) }
    val count = viewModel.quantity.collectAsStateWithLifecycle()
    val uiState = viewModel.uiState.collectAsStateWithLifecycle()
    val isLoading = remember { mutableStateOf(false) }

    when (uiState.value) {
        FoodDetailsViewModel.FoodDetailsUiState.Loading -> {
            isLoading.value = true
        }
        else -> {
            isLoading.value = false
        }
    }
    LaunchedEffect(Unit) {
        viewModel.event.collectLatest {
            when (it) {
                is FoodDetailsViewModel.FoodDetailsEvent.onAddToCart -> {
                    showSuccessDialog.value = true
                    onItemAddedToCart()
                }
                is FoodDetailsViewModel.FoodDetailsEvent.showErrorDialog -> {
                    showErrorDialog.value = true
                }
                is FoodDetailsViewModel.FoodDetailsEvent.goToCart -> {
                    navController.navigate(Cart)
                }
                else -> {}
            }
        }
    }

    Box(modifier = Modifier.fillMaxSize().background(Color.White)) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
        ) {
            // Header Image Box
            Box(modifier = Modifier
                .fillMaxWidth()
                .height(300.dp)) {
                AsyncImage(
                    model = foodItem.imageUrl,
                    contentDescription = null,
                    modifier = Modifier
                        .fillMaxSize()
                        .clip(RoundedCornerShape(bottomStart = 32.dp, bottomEnd = 32.dp)),
                    contentScale = ContentScale.Crop
                )

                // Back Button
                Box(
                    modifier = Modifier
                        .padding(top = 40.dp, start = 20.dp)
                        .size(44.dp)
                        .shadow(16.dp, shape = RoundedCornerShape(12.dp))
                        .background(Color.White, RoundedCornerShape(12.dp))
                        .clickable { navController.popBackStack() },
                    contentAlignment = Alignment.Center
                ) {
                    Image(painterResource(id = R.drawable.ic_back), contentDescription = "Back")
                }

                // Fav Button
                Box(
                    modifier = Modifier
                        .padding(top = 40.dp, end = 20.dp)
                        .align(Alignment.TopEnd)
                        .size(44.dp)
                        .shadow(16.dp, shape = CircleShape, spotColor = PrimaryOrange.copy(alpha = 0.5f)),
                    contentAlignment = Alignment.Center
                ) {
                    Image(painterResource(id = R.drawable.ic_favourite), contentDescription = "Fav")
                }
            }

            // Details Content
            Column(modifier = Modifier.padding(24.dp)) {
                Text(
                    text = foodItem.name,
                    style = TextStyle(fontSize = 28.sp, fontWeight = FontWeight.Bold, color = Color(0xFF323643))
                )
                
                Spacer(modifier = Modifier.height(12.dp))

                // Rating line
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Image(
                        imageVector = Icons.Filled.Star,
                        contentDescription = null,
                        modifier = Modifier.size(16.dp),
                        colorFilter = ColorFilter.tint(Color(0xFFFFC529))
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(text = "4.5", style = TextStyle(fontSize = 14.sp, fontWeight = FontWeight.Bold))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(text = "(30+)", style = TextStyle(fontSize = 12.sp, color = TextGray))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "See Review", 
                        style = TextStyle(fontSize = 12.sp, color = PrimaryOrange, textDecoration = TextDecoration.Underline, fontWeight = FontWeight.Medium)
                    )
                }
                
                Spacer(modifier = Modifier.height(24.dp))

                // Price & Counter
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.Top) {
                        Text(
                            text = "$",
                            style = TextStyle(fontSize = 16.sp, fontWeight = FontWeight.Bold, color = PrimaryOrange),
                            modifier = Modifier.padding(top = 4.dp)
                        )

                        // Calculate the total price based on the current count
                        val totalPrice = foodItem.price * count.value

                        // Format to 2 decimal places (optional, but recommended for currency)
                        Text(
                            text = String.format("%.2f", totalPrice),
                            style = TextStyle(fontSize = 32.sp, fontWeight = FontWeight.Bold, color = PrimaryOrange)
                        )
                    }

                    FoodItemCounter(
                        onCounterIncrement = {
                            viewModel.incrementQuantity()
                        },
                        onCounterDecrement = {
                            viewModel.decrementQuantity()
                        },
                        count = count.value
                    )
                }

                Spacer(modifier = Modifier.height(24.dp))
                
                Text(
                    text = foodItem.description,
                    style = TextStyle(fontSize = 14.sp, color = TextGray, lineHeight = 22.sp)
                )

                Spacer(modifier = Modifier.height(100.dp)) // padding for bottom button
            }
        }

        // Floating Bottom Button
        Box(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .fillMaxWidth()
                .padding(24.dp)
        ) {
            Button(
                onClick = {
                    viewModel.addToCart(restaurantId = foodItem.restaurantId, foodItemId = foodItem.id ?: "")
                },
                enabled = !isLoading.value,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(60.dp)
                    .shadow(elevation = 16.dp, shape = RoundedCornerShape(30.dp), spotColor = PrimaryOrange.copy(alpha=0.5f)),
                colors = ButtonDefaults.buttonColors(containerColor = PrimaryOrange),
                shape = RoundedCornerShape(30.dp)
            ) {
                if (!isLoading.value) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(36.dp)
                                .background(Color.White, CircleShape),
                            contentAlignment = Alignment.Center
                        ) {
                            Image(painterResource(id = R.drawable.ic_cart), contentDescription = null)
                        }
                        Spacer(modifier = Modifier.width(12.dp))
                        Text(text = "ADD TO CART", style = TextStyle(fontSize = 15.sp, fontWeight = FontWeight.Bold, color = Color.White))
                    }
                } else {
                    CircularProgressIndicator(modifier = Modifier.size(24.dp), color = Color.White)
                }
            }
        }
    }

    if (showSuccessDialog.value) {
        ModalBottomSheet(onDismissRequest = { showSuccessDialog.value = false }) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
            ) {
                Text(text = "Item added to cart", style = MaterialTheme.typography.titleLarge)
                Spacer(modifier = Modifier.size(16.dp))
                Button(
                    onClick = {
                        showSuccessDialog.value = false
                        viewModel.goToCart()
                    }, modifier = Modifier.padding(horizontal = 16.dp).fillMaxWidth(),
                    colors = ButtonDefaults.buttonColors(containerColor = PrimaryOrange)
                ) {
                    Text(text = "Go to Cart", color = Color.White)
                }
                Spacer(modifier = Modifier.size(8.dp))
                Button(
                    onClick = { showSuccessDialog.value = false }, 
                    modifier = Modifier.padding(horizontal = 16.dp).fillMaxWidth(),
                    colors = ButtonDefaults.buttonColors(containerColor = Color.LightGray)
                ) {
                    Text(text = "Continue Shopping", color = Color.Black)
                }
            }
        }
    }

    if (showErrorDialog.value) {
        ModalBottomSheet(onDismissRequest = { showSuccessDialog.value = false }) {
            BasicDialog(
                title = "Error",
                description = (uiState.value as? FoodDetailsViewModel.FoodDetailsUiState.Error)?.message ?: "Failed to add to cart"
            ) {
                showErrorDialog.value = false
            }
        }
    }
}

@Composable
fun FoodItemCounter(onCounterIncrement: () -> Unit, onCounterDecrement: () -> Unit, count: Int) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Box(
            modifier = Modifier
                .size(34.dp)
                .border(width = 1.dp, color = PrimaryOrange, shape = CircleShape)
                .background(Color.White, CircleShape)
                .clickable { onCounterDecrement.invoke() },
            contentAlignment = Alignment.Center
        ) {
            Image(painterResource(id = R.drawable.ic_minus), contentDescription = "Minus")
        }
        
        Spacer(modifier = Modifier.width(16.dp))
        
        Text(
            text = String.format("%02d", count), 
            style = TextStyle(fontSize = 16.sp, fontWeight = FontWeight.Bold, color = Color(0xFF323643))
        )
        
        Spacer(modifier = Modifier.width(16.dp))
        
        Box(
            modifier = Modifier
                .size(34.dp)
                .background(PrimaryOrange, CircleShape)
                .clickable { onCounterIncrement.invoke() },
            contentAlignment = Alignment.Center
        ) {
            Image(painterResource(id = R.drawable.ic_add), contentDescription = "Plus")
        }
    }
}
