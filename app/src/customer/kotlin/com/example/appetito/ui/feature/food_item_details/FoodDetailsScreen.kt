package com.example.appetito.ui.feature.food_item_details

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.AnimatedVisibilityScope
import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.animation.SharedTransitionScope
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.appetito.R
import androidx.navigation.NavController
import com.example.appetito.data.models.FoodItem
import com.example.appetito.ui.BasicDialog
import com.example.appetito.ui.feature.restaurant_details.RestaurantDetails
import com.example.appetito.ui.feature.restaurant_details.RestaurantDetailsHeader
import com.example.appetito.ui.navigation.Cart
import com.example.appetito.ui.theme.Primary
import kotlinx.coroutines.flow.collectLatest


@OptIn(ExperimentalSharedTransitionApi::class, ExperimentalMaterial3Api::class)
@Composable
fun SharedTransitionScope.FoodDetailsScreen(
    navController: NavController,
    foodItem: FoodItem,
    animatedVisibilityScope: AnimatedVisibilityScope,
    onItemAddedToCart: () -> Unit,
    viewModel: FoodDetailsViewModel = hiltViewModel()
) {

    val showSuccessDialog = remember {
        mutableStateOf(false)
    }
    val showErrorDialog = remember {
        mutableStateOf(false)
    }
    val count = viewModel.quantity.collectAsStateWithLifecycle()
    val uiState = viewModel.uiState.collectAsStateWithLifecycle()
    val isLoading = remember {
        mutableStateOf(false)
    }

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
    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = androidx.compose.ui.Alignment.CenterHorizontally
    ) {
        RestaurantDetailsHeader(imageUrl = foodItem.imageUrl,
            restaurantID = foodItem.id?:"",
            animatedVisibilityScope = animatedVisibilityScope,
            onBackButton = {
                navController.popBackStack()
            }) {}
        RestaurantDetails(
            title = foodItem.name,
            description = foodItem.description,
            restaurantID = foodItem.id?:"",
            animatedVisibilityScope = animatedVisibilityScope
        )
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Text(
                text = "$${foodItem.price}",
                color = MaterialTheme.colorScheme.primary,
                style = MaterialTheme.typography.headlineLarge
            )
            Spacer(modifier = Modifier.weight(1f))
            FoodItemCounter(onCounterIncrement = {
                viewModel.incrementQuantity()
            }, onCounterDecrement = {
                viewModel.decrementQuantity()
            }, count = count.value
            )
        }
        Spacer(modifier = Modifier.weight(1f))
        Button(
            onClick = {
                viewModel.addToCart(
                    restaurantId = foodItem.restaurantId, foodItemId = foodItem.id ?:""
                )
            }, enabled = !isLoading.value, modifier = Modifier.padding(8.dp)
        ) {
            Row(
                modifier = Modifier
                    .background(MaterialTheme.colorScheme.primary)
                    .padding(horizontal = 8.dp)
                    .clip(RoundedCornerShape(32.dp)),
                verticalAlignment = androidx.compose.ui.Alignment.CenterVertically
            ) {
                AnimatedVisibility(visible = !isLoading.value) {
                    Row(verticalAlignment = androidx.compose.ui.Alignment.CenterVertically) {
                        Image(
                            painter = painterResource(id = R.drawable.ic_cart),
                            contentDescription = null
                        )
                        Spacer(modifier = Modifier.size(8.dp))
                        Text(
                            text = "Add to Cart".uppercase(),
                            style = MaterialTheme.typography.bodyMedium
                        )
                    }

                }
                AnimatedVisibility(visible = isLoading.value) {
                    CircularProgressIndicator(modifier = Modifier.size(24.dp))
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
                Text(
                    text = "Item added to cart", style = MaterialTheme.typography.titleLarge
                )
                Spacer(modifier = Modifier.size(16.dp))
                Button(
                    onClick = {
                        showSuccessDialog.value = false
                        viewModel.goToCart()
                    }, modifier = Modifier
                        .padding(horizontal = 16.dp)
                        .fillMaxWidth()
                ) {
                    Text(text = "Go to Cart")
                }

                Button(
                    onClick = {
                        showSuccessDialog.value = false
                    }, modifier = Modifier
                        .padding(horizontal = 16.dp)
                        .fillMaxWidth()
                ) {
                    Text(text = "OK")
                }

            }
        }
    }
    if (showErrorDialog.value) {
        ModalBottomSheet(onDismissRequest = { showSuccessDialog.value = false }) {
            BasicDialog(
                title = "Error",
                description = (uiState.value as? FoodDetailsViewModel.FoodDetailsUiState.Error)?.message
                    ?: "Failed to add to cart"
            ) {
                showErrorDialog.value = false
            }
        }
    }

}


@Composable
fun FoodItemCounter(onCounterIncrement: () -> Unit, onCounterDecrement: () -> Unit, count: Int) {
    Row(verticalAlignment = androidx.compose.ui.Alignment.CenterVertically) {
        Image(painter = painterResource(id = R.drawable.ic_add),
            contentDescription = null,
            modifier = Modifier
                .clip(CircleShape)
                .clickable { onCounterIncrement.invoke() })
        Spacer(modifier = Modifier.size(8.dp))
        Text(text = "${count}", style = MaterialTheme.typography.titleMedium)
        Spacer(modifier = Modifier.size(8.dp))
        Image(painter = painterResource(id = R.drawable.ic_minus),
            contentDescription = null,
            modifier = Modifier
                .clip(CircleShape)
                .clickable { onCounterDecrement.invoke() })

    }
}


