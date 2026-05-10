package com.example.appetito.ui.feature.food_item_details

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
import androidx.compose.foundation.layout.offset
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
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.RadioButton
import androidx.compose.material3.RadioButtonDefaults
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
import com.example.appetito.data.models.CustomizationGroup
import com.example.appetito.data.models.CustomizationOption
import com.example.appetito.data.models.FoodItem
import com.example.appetito.ui.BasicDialog
import com.example.appetito.ui.navigation.Cart
import kotlinx.coroutines.flow.collectLatest

val PrimaryOrange = Color(0xFFFE724C)
val TextGray = Color(0xFF9796A1)
val BgLight = Color(0xFFF9F9FB) // Added the off-white background

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
    val selectedCustomizations = viewModel.selectedCustomizations.collectAsStateWithLifecycle()
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

    Box(modifier = Modifier.fillMaxSize().background(BgLight)) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
        ) {
            // Header Image Box
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(320.dp) // Slightly taller to account for overlap
            ) {
                AsyncImage(
                    model = foodItem.imageUrl,
                    contentDescription = null,
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Crop
                )

                // Back Button
                Box(
                    modifier = Modifier
                        .padding(top = 40.dp, start = 20.dp)
                        .size(44.dp)
                        .shadow(12.dp, shape = RoundedCornerShape(12.dp), spotColor = Color(0x33000000))
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
                        .shadow(12.dp, shape = CircleShape, spotColor = PrimaryOrange.copy(alpha = 0.5f)),
                    contentAlignment = Alignment.Center
                ) {
                    Image(painterResource(id = R.drawable.ic_favourite), contentDescription = "Fav")
                }
            }

            // Foreground Content - Pulled up to overlap the image
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .offset(y = (-40).dp) // Overlaps the image beautifully
            ) {

                // Top Details Card
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .shadow(elevation = 8.dp, shape = RoundedCornerShape(topStart = 32.dp, topEnd = 32.dp, bottomStart = 24.dp, bottomEnd = 24.dp), spotColor = Color(0x1A000000))
                        .background(Color.White, RoundedCornerShape(topStart = 32.dp, topEnd = 32.dp, bottomStart = 24.dp, bottomEnd = 24.dp))
                        .padding(24.dp)
                ) {
                    Column {
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
                                text = "See Reviews",
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

                                val basePrice = foodItem.price
                                val customizationPrice = selectedCustomizations.value.sumOf { it.price }
                                val totalPrice = (basePrice + customizationPrice) * count.value

                                Text(
                                    text = String.format("%.2f", totalPrice),
                                    style = TextStyle(fontSize = 32.sp, fontWeight = FontWeight.Bold, color = PrimaryOrange)
                                )
                            }

                            FoodItemCounter(
                                onCounterIncrement = { viewModel.incrementQuantity() },
                                onCounterDecrement = { viewModel.decrementQuantity() },
                                count = count.value
                            )
                        }

                        Spacer(modifier = Modifier.height(24.dp))

                        Text(text = "Description", style = TextStyle(fontSize = 16.sp, fontWeight = FontWeight.Bold, color = Color(0xFF323643)))
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = foodItem.description,
                            style = TextStyle(fontSize = 14.sp, color = TextGray, lineHeight = 22.sp)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))

                // Customizations (If Any)
                if (!foodItem.customizations.isNullOrEmpty()) {
                    Column(modifier = Modifier.padding(horizontal = 24.dp)) {
                        foodItem.customizations.forEach { group ->
                            CustomizationGroupView(
                                group = group,
                                selectedOptions = selectedCustomizations.value.filter { it.groupId == group.id }.map { it.optionId },
                                onOptionToggle = { option -> viewModel.toggleCustomization(group, option) }
                            )
                            Spacer(modifier = Modifier.height(16.dp))
                        }
                    }
                }

                Spacer(modifier = Modifier.height(120.dp)) // padding for bottom bar and offset compensation
            }
        }

        // Clean Bottom Bar with Shadow
        Box(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .fillMaxWidth()
                .shadow(elevation = 16.dp, spotColor = Color(0x1A000000))
                .background(Color.White)
                .padding(horizontal = 24.dp, vertical = 16.dp)
        ) {
            Button(
                onClick = {
                    viewModel.addToCart(restaurantId = foodItem.restaurantId, foodItemId = foodItem.id ?: "")
                },
                enabled = !isLoading.value,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(60.dp),
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
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFF1F2F6))
                ) {
                    Text(text = "Continue Shopping", color = Color(0xFF323643))
                }
                Spacer(modifier = Modifier.size(24.dp))
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
                .size(36.dp)
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
                .size(36.dp)
                .background(PrimaryOrange, CircleShape)
                .clickable { onCounterIncrement.invoke() },
            contentAlignment = Alignment.Center
        ) {
            Image(painterResource(id = R.drawable.ic_add), contentDescription = "Plus")
        }
    }
}

@Composable
fun CustomizationGroupView(
    group: CustomizationGroup,
    selectedOptions: List<String>,
    onOptionToggle: (CustomizationOption) -> Unit
) {
    // Wrapped in a modern white card
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .shadow(elevation = 6.dp, shape = RoundedCornerShape(16.dp), spotColor = Color(0x1A000000))
            .background(Color.White, RoundedCornerShape(16.dp))
            .padding(16.dp)
    ) {
        Column(modifier = Modifier.fillMaxWidth()) {

            // Header Row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = group.name,
                    style = TextStyle(fontSize = 18.sp, fontWeight = FontWeight.Bold, color = Color(0xFF323643))
                )

                // Pill badge for requirement
                if (group.isRequired) {
                    Box(
                        modifier = Modifier
                            .background(Color(0xFFFFF2F2), RoundedCornerShape(6.dp))
                            .padding(horizontal = 8.dp, vertical = 4.dp)
                    ) {
                        Text(text = "Required", style = TextStyle(fontSize = 11.sp, fontWeight = FontWeight.SemiBold, color = Color(0xFFFF4B4B)))
                    }
                } else {
                    Text(text = "Optional", style = TextStyle(fontSize = 12.sp, color = TextGray))
                }
            }

            Spacer(modifier = Modifier.height(12.dp))
            Divider(color = Color(0xFFF1F2F6), thickness = 1.dp)
            Spacer(modifier = Modifier.height(8.dp))

            // Options List
            group.options.forEach { option ->
                val isSelected = selectedOptions.contains(option.id)
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(8.dp))
                        .clickable { onOptionToggle(option) }
                        .padding(vertical = 8.dp, horizontal = 4.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        if (group.maxSelections == 1) {
                            RadioButton(
                                selected = isSelected,
                                onClick = { onOptionToggle(option) },
                                colors = RadioButtonDefaults.colors(selectedColor = PrimaryOrange),
                                modifier = Modifier.size(24.dp)
                            )
                        } else {
                            Checkbox(
                                checked = isSelected,
                                onCheckedChange = { onOptionToggle(option) },
                                colors = CheckboxDefaults.colors(checkedColor = PrimaryOrange),
                                modifier = Modifier.size(24.dp)
                            )
                        }
                        Spacer(modifier = Modifier.width(12.dp))
                        Text(
                            text = option.name,
                            style = TextStyle(fontSize = 15.sp, fontWeight = if (isSelected) FontWeight.SemiBold else FontWeight.Normal, color = Color(0xFF323643))
                        )
                    }

                    if (option.price > 0) {
                        Text(
                            text = "+$${String.format("%.2f", option.price)}",
                            style = TextStyle(fontSize = 14.sp, fontWeight = FontWeight.Medium, color = if (isSelected) PrimaryOrange else TextGray)
                        )
                    }
                }
            }
        }
    }
}