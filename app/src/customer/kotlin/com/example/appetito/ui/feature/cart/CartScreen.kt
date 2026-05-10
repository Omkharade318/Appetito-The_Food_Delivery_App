package com.example.appetito.ui.feature.cart

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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import coil3.compose.AsyncImage
import com.example.appetito.R
import com.example.appetito.data.models.Address
import com.example.appetito.data.models.CartItem
import com.example.appetito.data.models.CheckOutDetails
import com.example.appetito.ui.BasicDialog
import com.example.appetito.ui.feature.food_item_details.FoodItemCounter
import com.example.appetito.ui.navigation.AddressList
import com.example.appetito.ui.navigation.OrderSuccess
import com.example.appetito.utils.StringUtils
import com.stripe.android.PaymentConfiguration
import com.stripe.android.paymentsheet.PaymentSheet
import com.stripe.android.paymentsheet.PaymentSheetResult
import com.stripe.android.paymentsheet.rememberPaymentSheet
import kotlinx.coroutines.flow.collectLatest

val PrimaryOrange = Color(0xFFFE724C)
val TextGray = Color(0xFF9796A1)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CartScreen(navController: NavController, viewModel: CartViewModel){

    val uiState = viewModel.uiState.collectAsState()
    val showErrorDialog = remember { mutableStateOf(false) }

    val address = navController.currentBackStackEntry?.savedStateHandle?.getStateFlow<Address?>("address", null)?.collectAsStateWithLifecycle()

    LaunchedEffect(key1 = address?.value) {
        address?.value?.let {
            viewModel.onAddressSelected(it)
        }
    }

    val paymentSheet = rememberPaymentSheet(paymentResultCallback = {
        if (it is PaymentSheetResult.Completed) {
            viewModel.onPaymentSuccess()
        } else {
            viewModel.onPaymentFailed()
        }
    })

    LaunchedEffect(key1 = true) {
        viewModel.event.collectLatest {
            when(it){
                is CartViewModel.CartEvent.onItemRemoveError,
                is CartViewModel.CartEvent.onQuantityUpdateError,
                is CartViewModel.CartEvent.showErrorDialog -> {
                    showErrorDialog.value = true
                }
                is CartViewModel.CartEvent.onAddressClicked -> {
                    navController.navigate(AddressList)
                }
                is CartViewModel.CartEvent.OrderSuccess -> {
                    navController.navigate(OrderSuccess(it.orderId!!))
                }
                is CartViewModel.CartEvent.OnInitiatePayment -> {
                    PaymentConfiguration.init(navController.context, it.data.publishableKey)
                    val customer = PaymentSheet.CustomerConfiguration(
                        it.data.customerId,
                        it.data.ephemeralKeySecret
                    )
                    val paymentSheetConfig = PaymentSheet.Configuration(
                        merchantDisplayName = "FoodHub",
                        customer = customer,
                        allowsDelayedPaymentMethods = false,
                    )

                    paymentSheet.presentWithPaymentIntent(
                        it.data.paymentIntentClientSecret,
                        paymentSheetConfig
                    )
                }
                else -> {}
            }
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
            .padding(horizontal = 24.dp)
            .padding(top = 16.dp)
    ) {

        CartHeaderView(onBack = {navController.popBackStack()})

        Spacer(modifier = Modifier.size(24.dp))

        when(uiState.value){
            is CartViewModel.CartUiState.Loading ->{
                Column(
                    modifier = Modifier.fillMaxSize(),
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    CircularProgressIndicator(color = PrimaryOrange)
                    Spacer(modifier = Modifier.size(16.dp))
                    Text(text = "Loading Cart...", style = TextStyle(color = TextGray))
                }
            }

            is CartViewModel.CartUiState.Success -> {
                val data = (uiState.value as CartViewModel.CartUiState.Success).data
                val selectedAddress = viewModel.selectedAddress.collectAsStateWithLifecycle()

                if(data.items.isNotEmpty()){
                    LazyColumn(modifier = Modifier.weight(1f)) {
                        items(data.items){ it ->
                            CartItemView(
                                cartItem = it,
                                onIncrement = { cartItem, _ -> viewModel.incrementQuantity(cartItem) },
                                onDecrement = { cartItem, _ -> viewModel.decrementQuantity(cartItem) },
                                onRemove = { viewModel.removeItem(it) }
                            )
                        }

                        item {
                            Spacer(modifier = Modifier.height(24.dp))
                            PromoCodeView()
                            Spacer(modifier = Modifier.height(24.dp))
                            CheckoutDetailsView(checkoutDetails = data.checkoutDetails)
                            Spacer(modifier = Modifier.height(16.dp))
                            AddressCard(selectedAddress.value) { viewModel.onAddressClicked() }
                            Spacer(modifier = Modifier.height(24.dp))
                        }
                    }
                    
                    Box(modifier = Modifier.padding(bottom = 24.dp, top = 8.dp)) {
                        Button(
                            onClick = { viewModel.checkout() },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(60.dp)
                                .shadow(elevation = 16.dp, shape = RoundedCornerShape(30.dp), spotColor = PrimaryOrange.copy(alpha=0.5f)),
                            enabled = selectedAddress.value != null,
                            colors = ButtonDefaults.buttonColors(
                                containerColor = PrimaryOrange,
                                disabledContainerColor = Color(0xFFFFD4C8)
                            ),
                            shape = RoundedCornerShape(30.dp)
                        ) {
                            Text(text = "CHECKOUT", style = TextStyle(fontSize = 15.sp, fontWeight = FontWeight.Bold, color = Color.White))
                        }
                    }
                }
                else {
                    Column(
                        modifier = Modifier.fillMaxSize(),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        Image(painter = painterResource(id = R.drawable.ic_cart), contentDescription = null)
                        Spacer(modifier = Modifier.height(16.dp))
                        Text(text = "Your Cart is Empty", style = TextStyle(fontSize = 18.sp, color = TextGray))
                    }
                }
            }

            is CartViewModel.CartUiState.Error -> {
                Column(
                    modifier = Modifier.fillMaxSize(),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    val message = (uiState.value as CartViewModel.CartUiState.Error).message
                    Text(text = message, style = TextStyle(color = Color.Red))
                    Spacer(modifier = Modifier.height(16.dp))
                    Button(onClick = { /* retry */ }, colors = ButtonDefaults.buttonColors(containerColor = PrimaryOrange)) {
                        Text(text = "Retry", color = Color.White)
                    }
                }
            }

            CartViewModel.CartUiState.Nothing -> {}
        }
    }

    if(showErrorDialog.value){
        ModalBottomSheet(onDismissRequest = { showErrorDialog.value = false }) {
            BasicDialog(title = viewModel.errorTitle, description = viewModel.errorMessage) {
                showErrorDialog.value = false
            }
        }
    }
}

@Composable
fun PromoCodeView() {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(56.dp)
            .border(width = 1.dp, color = Color(0xFFEEEEEE), shape = RoundedCornerShape(30.dp))
            .background(Color.White, RoundedCornerShape(30.dp))
            .padding(start = 24.dp, end = 6.dp),
        contentAlignment = Alignment.CenterStart
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(text = "Promo Code", color = Color(0xFFC4C4C4), fontSize = 14.sp)
            
            Button(
                onClick = { /* Apply */ },
                modifier = Modifier
                    .height(44.dp)
                    .width(100.dp),
                shape = RoundedCornerShape(30.dp),
                colors = ButtonDefaults.buttonColors(containerColor = PrimaryOrange),
                contentPadding = androidx.compose.foundation.layout.PaddingValues(0.dp)
            ) {
                Text(text = "Apply", color = Color.White, fontSize = 14.sp)
            }
        }
    }
}

@Composable
fun AddressCard(address: Address?, onAddressClicked: () -> Unit){
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .shadow(elevation = 8.dp, shape = RoundedCornerShape(16.dp), spotColor = Color(0x22000000))
            .background(Color.White, RoundedCornerShape(16.dp))
            .clickable { onAddressClicked.invoke() }
            .padding(16.dp)
    ) {
        if(address != null){
            Column {
                Text(text = "Delivery Address", style = TextStyle(color = TextGray, fontSize = 12.sp))
                Spacer(modifier = Modifier.height(4.dp))
                Text(text = address.addressLine1, style = TextStyle(fontSize = 16.sp, fontWeight = FontWeight.SemiBold, color = Color.Black))
                Text(
                    text = "${address.city}, ${address.state} ${address.country}",
                    style = TextStyle(fontSize = 14.sp, color = TextGray)
                )
            }
        }
        else{
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(text = "Select Delivery Address", style = TextStyle(fontSize = 16.sp, fontWeight = FontWeight.SemiBold, color = PrimaryOrange))
            }
        }
    }
}

@Composable
fun CheckoutDetailsView(checkoutDetails: CheckOutDetails) {
    Column {
        CheckoutRowItem("Subtotal", checkoutDetails.subTotal, "USD")
        CheckoutRowItem("Tax and Fees", checkoutDetails.subTotal * 0.1, "USD") // Mocking tax fee logic purely visually
        CheckoutRowItem("Delivery", 1.0, "USD") // Mocking logic to closely follow image
        Spacer(modifier = Modifier.height(8.dp))
        CheckoutRowItem("Total", checkoutDetails.totalAmount, "USD", titleBold = true, valueSize = 20.sp, itemsHint = "(2 items)")
    }
}

@Composable
fun CheckoutRowItem(
    title: String, 
    value: Double, 
    currency: String, 
    titleBold: Boolean = false, 
    valueSize: androidx.compose.ui.unit.TextUnit = 16.sp,
    itemsHint: String? = null
){
    Column {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(verticalAlignment = Alignment.Bottom) {
                Text(
                    text = title, 
                    style = TextStyle(
                        fontSize = 16.sp, 
                        fontWeight = if(titleBold) FontWeight.Bold else FontWeight.Medium, 
                        color = Color(0xFF323643)
                    )
                )
                if (itemsHint != null) {
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(text = itemsHint, style = TextStyle(fontSize = 14.sp, color = Color(0xFFB3B3B3)))
                }
            }
            
            Spacer(modifier = Modifier.weight(1f))
            
            Row(verticalAlignment = Alignment.Bottom) {
                Text(
                    text = StringUtils.formatCurrency(value), 
                    style = TextStyle(fontSize = valueSize, fontWeight = FontWeight.Bold, color = Color.Black)
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text(text = currency, style = TextStyle(fontSize = 12.sp, color = Color(0xFFB3B3B3)))
            }
        }
        Divider(color = Color(0xFFF1F2F6), thickness = 1.dp)
    }
}

@Composable
fun CartItemView(
        cartItem: CartItem,
        onIncrement: (CartItem, Int) -> Unit,
        onDecrement: (CartItem, Int) -> Unit,
        onRemove: (CartItem) -> Unit
    ) {

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 12.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ){

        AsyncImage(
            model = cartItem.menuItemId.imageUrl,
            contentDescription = null,
            modifier = Modifier
                .size(80.dp)
                .shadow(elevation = 16.dp, shape = RoundedCornerShape(16.dp), spotColor = Color(0x33000000))
                .clip(RoundedCornerShape(16.dp)),
            contentScale = ContentScale.Crop
        )

        Spacer(modifier = Modifier.width(16.dp))

        Column(modifier = Modifier.weight(1f)) {
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Text(
                    text = cartItem.menuItemId.name,
                    style = TextStyle(fontSize = 16.sp, fontWeight = FontWeight.SemiBold, color = Color.Black),
                    maxLines = 1
                )
                Box(
                    modifier = Modifier.clickable { onRemove.invoke(cartItem) }
                ) {
                    Icon(
                        imageVector = Icons.Filled.Close,
                        contentDescription = "Remove",
                        tint = PrimaryOrange,
                        modifier = Modifier.size(18.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(2.dp))
            
            Text(
                text = cartItem.menuItemId.description, 
                maxLines = 1, 
                style = TextStyle(fontSize = 13.sp, color = TextGray)
            )

            cartItem.selectedCustomizations?.forEach { customization ->
                Text(
                    text = "+ ${customization.optionName} ($${String.format("%.2f", customization.price)})",
                    style = TextStyle(fontSize = 12.sp, color = TextGray)
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.SpaceBetween, modifier = Modifier.fillMaxWidth()) {

                // 1. Calculate the total price for this specific row
                val basePrice = cartItem.menuItemId.price
                val customizationsPrice = cartItem.selectedCustomizations?.sumOf { it.price } ?: 0.0
                val totalItemPrice = (basePrice + customizationsPrice) * cartItem.quantity

                // 2. Display the calculated total, formatted to 2 decimal places
                Text(
                    text = "$${String.format("%.2f", totalItemPrice)}",
                    style = TextStyle(fontSize = 16.sp, fontWeight = FontWeight.Bold, color = PrimaryOrange)
                )

                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(28.dp)
                            .border(width = 1.dp, color = PrimaryOrange, shape = CircleShape)
                            .background(Color.White, CircleShape)
                            .clickable { onDecrement.invoke(cartItem, cartItem.quantity) },
                        contentAlignment = Alignment.Center
                    ) {
                        Image(painterResource(id = R.drawable.ic_minus), contentDescription = "Minus")
                    }

                    Spacer(modifier = Modifier.width(12.dp))

                    Text(
                        text = String.format("%02d", cartItem.quantity),
                        style = TextStyle(fontSize = 14.sp, fontWeight = FontWeight.SemiBold, color = Color(0xFF323643))
                    )

                    Spacer(modifier = Modifier.width(12.dp))

                    Box(
                        modifier = Modifier
                            .size(28.dp)
                            .background(PrimaryOrange, CircleShape)
                            .clickable { onIncrement.invoke(cartItem, cartItem.quantity) },
                        contentAlignment = Alignment.Center
                    ) {
                        Image(painterResource(id = R.drawable.ic_add), contentDescription = "Plus")
                    }
                }
            }
        }
    }
}

@Composable
fun CartHeaderView(onBack: () -> Unit){
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(44.dp)
                .shadow(16.dp, shape = RoundedCornerShape(12.dp))
                .background(Color.White, RoundedCornerShape(12.dp))
                .clickable { onBack() },
            contentAlignment = Alignment.Center
        ) {
            Image(painterResource(id = R.drawable.ic_back), contentDescription = "Back")
        }

        Text(text = "Cart", style = TextStyle(fontSize = 18.sp, fontWeight = FontWeight.Bold, color = Color(0xFF323643)))

        Spacer(modifier = Modifier.size(44.dp)) // To keep title centered
    }
}