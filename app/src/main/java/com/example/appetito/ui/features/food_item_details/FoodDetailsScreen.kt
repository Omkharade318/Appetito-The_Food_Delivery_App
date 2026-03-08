package com.example.appetito.ui.features.food_item_details

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.AnimatedVisibilityScope
import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.animation.SharedTransitionScope
import androidx.compose.foundation.Image
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
import androidx.compose.material3.BasicAlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.appetito.R
import androidx.navigation.NavController
import com.example.appetito.data.models.FoodItem
import com.example.appetito.ui.features.restaurant_details.RestaurantDetailHeader
import com.example.appetito.ui.features.restaurant_details.RestaurantDetails
import com.example.appetito.ui.navigation.Cart
import kotlinx.coroutines.flow.collectLatest

@OptIn(ExperimentalSharedTransitionApi::class, ExperimentalMaterial3Api::class)
@Composable
fun SharedTransitionScope.FoodDetailsScreen(
    navController: NavController,
    foodItem: FoodItem,
    animatedVisibilityScope: AnimatedVisibilityScope,
    viewModel: FoodDetailsViewModel = hiltViewModel()
) {
    val showSuccessDialog  = remember {
        mutableStateOf(false)
    }

    val showErrorDialog = remember {
        mutableStateOf(false)
    }

    val count = viewModel.quantity.collectAsStateWithLifecycle()
    val  uiState = viewModel.uiState.collectAsStateWithLifecycle()

    val isLoading =  remember {
        mutableStateOf(false)
    }

    when(uiState.value){
        is FoodDetailsViewModel.FoodDetailsUiState.Loading -> {
            isLoading.value = true
        }

        else -> {
            isLoading.value = false
        }
    }

    LaunchedEffect(Unit) {
        viewModel.event.collectLatest {

            when(it){
                is FoodDetailsViewModel.FoodDetailsEvent.onAddToCart -> {
                   showSuccessDialog.value = true
                }

                is FoodDetailsViewModel.FoodDetailsEvent.showErrorDialog -> {
                    showErrorDialog.value = true
                }

                is FoodDetailsViewModel.FoodDetailsEvent.goToCart -> {
                    navController.navigate(Cart)
                }
            }
        }
    }

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
        }

        // Bottom Fixed Button
        Button(
            onClick = {
                viewModel.addToCart(
                    restaurantId = foodItem.restaurantId,
                    foodItemId = foodItem.id
                )
            },
            enabled = !isLoading.value,
            shape = RoundedCornerShape(32.dp),
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .fillMaxWidth()
                .padding(16.dp)
                .height(56.dp)
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {

                AnimatedVisibility(visible = !isLoading.value,){
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

                AnimatedVisibility(visible = isLoading.value) {
                    CircularProgressIndicator(modifier = Modifier.size(24.dp))
                }
            }
        }
    }

    if(showSuccessDialog.value){
        ModalBottomSheet(
            onDismissRequest = {showSuccessDialog.value = false}
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp)
            ) {
                Text(
                    text = "Item added to cart",
                    style = MaterialTheme.typography.titleLarge
                )

                Spacer(modifier = Modifier.size(16.dp))

                Button(
                    onClick = {
                        showSuccessDialog.value = false
                        viewModel.goToCart();
                    },
                    modifier = Modifier
                        .padding(horizontal = 16.dp)
                        .fillMaxWidth()
                ) {
                    Text(text = "Go to Cart")
                }

                Button(
                    onClick = {
                        showSuccessDialog.value = false
                    },
                    modifier = Modifier
                        .padding(horizontal = 16.dp)
                        .fillMaxWidth()
                ) {
                    Text(text = "Ok")
                }

            }
        }
    }

    if (showErrorDialog.value) {
        ModalBottomSheet(
            onDismissRequest = { showErrorDialog.value = false }
        ) {
            BasicDialog(
                title = "Error",
                description = (uiState.value as? FoodDetailsViewModel.FoodDetailsUiState.Error)
                    ?.message ?: "Failed to add to cart",
                confirmText = "OK",
                onConfirm = {
                    showErrorDialog.value = false
                }
            )
        }
    }

}

@Composable
fun BasicDialog(
    title: String,
    description: String,
    confirmText: String = "OK",
    dismissText: String? = null,
    onConfirm: () -> Unit,
    onDismiss: (() -> Unit)? = null
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(20.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        // Title
        Text(
            text = title,
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.SemiBold
        )

        // Description
        Text(
            text = description,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )

        Spacer(modifier = Modifier.height(8.dp))

        // Buttons
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.End
        ) {
            if (dismissText != null && onDismiss != null) {
                TextButton(onClick = onDismiss) {
                    Text(dismissText)
                }
                Spacer(modifier = Modifier.width(8.dp))
            }

            Button(onClick = onConfirm) {
                Text(confirmText)
            }
        }
    }
}


@Composable
fun FoodItemCounter(
    onCounterIncrement: () -> Unit,
    onCounterDecrement: () -> Unit,
    count: Int
){
    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.Center) {

        Image(
            modifier = Modifier
                .clip(CircleShape)
                .size(36.dp)
                .clickable { onCounterDecrement.invoke() },
            painter = painterResource(id = R.drawable.ic_minus),
            contentDescription = null
        )

        Spacer(modifier = Modifier.size(8.dp))

        Text(
            text = "$count",
            style = MaterialTheme.typography.titleMedium
        )

        Spacer(modifier = Modifier.size(8.dp))

        Image(
            modifier = Modifier
                .clip(CircleShape)
                .size(36.dp)
                .clickable { onCounterIncrement.invoke() },
            painter = painterResource(id = R.drawable.ic_add),
            contentDescription = null
        )
    }
}