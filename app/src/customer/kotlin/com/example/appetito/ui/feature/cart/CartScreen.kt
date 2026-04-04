package com.example.appetito.ui.feature.cart

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
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.material3.VerticalDivider
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
import androidx.compose.ui.unit.dp
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

                    // Initiate payment

                    paymentSheet.presentWithPaymentIntent(
                        it.data.paymentIntentClientSecret,
                        paymentSheetConfig
                    )
                }

                else ->{

                }

            }
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp)
    ) {

        CartHeaderView(onBack = {navController.popBackStack()})

        Spacer(modifier = Modifier.size(16.dp))

        when(uiState.value){
            is CartViewModel.CartUiState.Loading ->{
                Spacer(modifier = Modifier.size(16.dp))

                Column(
                    modifier = Modifier.fillMaxSize(),
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Spacer(modifier = Modifier.size(16.dp))
                    CircularProgressIndicator()
                    Text(
                        text = "Loading",
                        style = MaterialTheme.typography.bodyMedium,
                        color = Color.Gray
                    )

                }
            }

            is CartViewModel.CartUiState.Success -> {
                val data = (uiState.value as CartViewModel.CartUiState.Success).data

                if(data.items.isNotEmpty()){
                    LazyColumn {
                        items(data.items){ it ->
                            CartItemView(
                                cartItem = it,
                                onIncrement = {
                                        cartItem,  _ ->
                                    viewModel.incrementQuantity(cartItem)
                                },
                                onDecrement = {
                                        cartItem, _ ->
                                    viewModel.decrementQuantity(cartItem)
                                },
                                onRemove = {
                                    viewModel.removeItem(it)
                                }
                            )
                        }

                        item {
                            CheckoutDetailsView(checkoutDetails = data.checkoutDetails)
                        }
                    }
                }
                else{
                    Column(
                        modifier = Modifier.fillMaxSize(),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        Icon(
                            painter = painterResource(id = R.drawable.ic_cart2),
                            contentDescription = null,
                            tint = Color.Gray
                        )
                        Text(
                            text = "No items in Cart",
                            style = MaterialTheme.typography.bodyMedium,
                            color = Color.Gray
                        )
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
                    Text(text = message, style = MaterialTheme.typography.bodyMedium)
                    Button(onClick = {}) {
                        Text(text = "Retry")
                    }
                }
            }

            CartViewModel.CartUiState.Nothing -> {}
        }

        val selectedAddress = viewModel.selectedAddress.collectAsStateWithLifecycle()
        Spacer(modifier = Modifier.weight(1f))

        if(uiState.value is CartViewModel.CartUiState.Success){
            AddressCard(
                selectedAddress.value
            ) {
                viewModel.onAddressClicked()
            }
            Button(
                onClick = {viewModel.checkout()},
                modifier = Modifier.fillMaxWidth(),
                enabled = selectedAddress.value != null
            ) {
                Text(text = "Checkout")
            }
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
fun AddressCard(address: Address?, onAddressClicked: () -> Unit){
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp)
            .shadow(8.dp)
            .clip(RoundedCornerShape(8.dp))
            .background(Color.White)
            .clickable {onAddressClicked.invoke()}
            .padding(16.dp)
    ) {
        if(address != null){
            Column {
                Text(text = address.addressLine1, style = MaterialTheme.typography.titleMedium)
                Spacer(modifier = Modifier.size(4.dp))
                Text(
                    text = "${address.city}, ${address.state} ${address.country}",
                    style = MaterialTheme.typography.bodyMedium,
                    color = Color.Gray
                )
            }
        }
        else{
            Text(text = "Select Address", style = MaterialTheme.typography.titleMedium)
        }
    }
}

@Composable
fun CheckoutDetailsView(checkoutDetails: CheckOutDetails) {
    Column {
        CheckoutRowItem("Sub Total", checkoutDetails.subTotal, "USD")
        CheckoutRowItem("Tax", checkoutDetails.subTotal, "USD")
        CheckoutRowItem("Delivery Fee", checkoutDetails.subTotal, "USD")
        CheckoutRowItem("Total", checkoutDetails.totalAmount, "USD")

    }
}

@Composable
fun CheckoutRowItem(title: String, value: Double, currency: String){
   Column {
       Row (
           modifier = Modifier
               .fillMaxWidth()
               .padding(vertical = 4.dp)
       ){
           Text(text = title, style = MaterialTheme.typography.titleMedium)
           Spacer(modifier = Modifier.weight(1f))
           Text(text = StringUtils.formatCurrency(value), style = MaterialTheme.typography.titleMedium)
           Text(text = currency, style = MaterialTheme.typography.titleMedium, color = Color.LightGray)
       }

       VerticalDivider()
   }
}

@Composable
fun CartItemView(
        cartItem: CartItem,
        onIncrement: (CartItem, Int) -> Unit,
        onDecrement: (CartItem, Int) -> Unit,
        onRemove: (CartItem) -> Unit
    ) {

    Row (
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ){

        AsyncImage(
            model = cartItem.menuItemId.imageUrl,
            contentDescription = null,
            modifier = Modifier
                .size(82.dp)
                .clip(RoundedCornerShape(12.dp)),
            contentScale = ContentScale.Crop
        )

        Spacer(modifier = Modifier.size(12.dp))

        Column(verticalArrangement = Arrangement.Center) {

            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = cartItem.menuItemId.name,
                    style = MaterialTheme.typography.titleMedium
                )

                Spacer(modifier = Modifier.weight(1f))

                IconButton(
                    onClick = {onRemove.invoke(cartItem)},
                    modifier = Modifier.size(24.dp)
                ) {
                    Icon(
                        imageVector = Icons.Filled.Close,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(18.dp)
                    )
                }
            }

            Text(text = cartItem.menuItemId.description, maxLines = 1, color = Color.Gray, style = MaterialTheme.typography.bodySmall)

            Spacer(modifier = Modifier.size(8.dp))

            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(text = "${cartItem.menuItemId.price}", style = MaterialTheme.typography.titleMedium, color = MaterialTheme.colorScheme.primary)
                Spacer(modifier = Modifier.weight(1f))

                FoodItemCounter(
                    onCounterDecrement = {onDecrement.invoke(cartItem, cartItem.quantity)},
                    onCounterIncrement = {onIncrement.invoke(cartItem, cartItem.quantity)},
                    count = cartItem.quantity
                )
            }

        }

    }
}

@Composable
fun CartHeaderView(onBack: () -> Unit){

    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        IconButton(onClick = onBack) {
            Image(painter = painterResource(id =  R.drawable.ic_back), contentDescription = null)
        }

        Text(text = "Cart", style = MaterialTheme.typography.titleMedium)

        Spacer(modifier = Modifier.size(8.dp))
    }

}