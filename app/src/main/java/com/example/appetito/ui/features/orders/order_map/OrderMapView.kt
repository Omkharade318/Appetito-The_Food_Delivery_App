package com.example.appetito.ui.features.orders.order_map

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import androidx.annotation.DrawableRes
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.appetito.ui.features.orders.OrderDetailsBaseViewModel
import com.example.appetito.R
import com.example.appetito.data.models.Order
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.model.BitmapDescriptor
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.LatLngBounds
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.Marker
import com.google.maps.android.compose.Polyline
import com.google.maps.android.compose.rememberCameraPositionState
import com.google.maps.android.compose.rememberMarkerState

@Composable
fun OrderTrackerMapView(
    modifier: Modifier,
    viewModel: OrderDetailsBaseViewModel,
    order: Order,
    initialLocation: LatLng? = null
) {
    val context = LocalContext.current
    val messages = viewModel.locationUpdate.collectAsStateWithLifecycle(null)
    val cameraPositionState = rememberCameraPositionState()

    // Initialize camera to initialLocation or destination if messages are null
    LaunchedEffect(key1 = order.address) {
        if (messages.value == null) {
            val startLocation = initialLocation ?: order.address.latitude?.let { lat ->
                order.address.longitude?.let { lng ->
                    LatLng(lat, lng)
                }
            }
            startLocation?.let {
                cameraPositionState.position = CameraPosition.fromLatLngZoom(it, 15f)
            }
        }
    }

    // Move camera to show both rider and destination
    LaunchedEffect(key1 = messages.value) {
        messages.value?.let {
            val riderMarker = LatLng(it.currentLocation.latitude, it.currentLocation.longitude)
            val destMarker = LatLng(it.finalDestination.latitude, it.finalDestination.longitude)
            
            val bounds = LatLngBounds.builder()
                .include(riderMarker)
                .include(destMarker)
                .build()
            
            cameraPositionState.animate(
                update = CameraUpdateFactory.newLatLngBounds(bounds, 100),
                durationMs = 1000
            )
        }
    }

    Box(modifier = modifier.fillMaxSize()) {
        GoogleMap(
            modifier = Modifier.fillMaxSize(),
            cameraPositionState = cameraPositionState
        ) {
            // Always show Customer marker if available
            order.address.latitude?.let { lat ->
                order.address.longitude?.let { lng ->
                    Marker(
                        state = rememberMarkerState(position = LatLng(lat, lng)),
                        title = "Customer",
                        snippet = order.address.addressLine1
                    )
                }
            }

            messages.value?.let {
                val riderMarker = LatLng(it.currentLocation.latitude, it.currentLocation.longitude)
                Marker(
                    state = rememberMarkerState(position = riderMarker),
                    title = "Rider",
                    snippet = "Delivery Partner",
                    icon = bitmapDescriptorFromVector(
                        context = context,
                        vectorResId = R.drawable.ic_delivery
                    )
                )

                Polyline(
                    points = it.polyline,
                    color = MaterialTheme.colorScheme.primary,
                    width = 10f,
                )
            }
        }

        // ETA Overlay
        messages.value?.let {
            Surface(
                modifier = Modifier
                    .align(Alignment.TopCenter)
                    .padding(16.dp)
                    .clip(RoundedCornerShape(12.dp)),
                color = Color.White,
                shadowElevation = 8.dp
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        val phaseText = if (it.deliveryPhase == "TO_RESTAURANT") {
                            "Heading to restaurant"
                        } else {
                            "Heading to you"
                        }
                        Text(
                            text = phaseText,
                            style = MaterialTheme.typography.labelMedium,
                            color = Color.Gray
                        )
                        Text(
                            text = "Arriving in ${it.estimatedTime} mins",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = Color.Black
                        )
                    }
                }
            }
        }
    }
}


fun bitmapDescriptorFromVector(context: Context, @DrawableRes vectorResId: Int): BitmapDescriptor {
    val vectorDrawable = ContextCompat.getDrawable(context, vectorResId)
    val bitmap = Bitmap.createBitmap(
        vectorDrawable!!.intrinsicWidth,
        vectorDrawable.intrinsicHeight,
        Bitmap.Config.ARGB_8888
    )
    val canvas = Canvas(bitmap)
    vectorDrawable.setBounds(0, 0, canvas.width, canvas.height)
    vectorDrawable.draw(canvas)
    return BitmapDescriptorFactory.fromBitmap(bitmap)
}

