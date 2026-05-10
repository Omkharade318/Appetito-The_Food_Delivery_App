package com.example.appetito.ui.features.common

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
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
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
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil3.compose.AsyncImage
import com.example.appetito.data.models.FoodItem
import com.example.appetito.R

// Shared app colors
val PrimaryOrange = Color(0xFFFE724C)
val TextGray = Color(0xFF9796A1)

@OptIn(ExperimentalSharedTransitionApi::class)
@Composable
fun SharedTransitionScope.FoodItemView(
    footItem: FoodItem,
    animatedVisibilityScope: AnimatedVisibilityScope,
    onClick: (FoodItem) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth() // Let the grid determine the exact width
            .shadow(
                elevation = 12.dp,
                shape = RoundedCornerShape(16.dp),
                spotColor = Color(0x1A000000) // Soft, modern 10% black shadow
            )
            .background(Color.White, RoundedCornerShape(16.dp))
            .clickable { onClick.invoke(footItem) }
            .clip(RoundedCornerShape(16.dp))
    ) {
        // --- Image Section ---
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(140.dp)
        ) {
            AsyncImage(
                model = footItem.imageUrl,
                contentDescription = null,
                modifier = Modifier
                    .fillMaxSize()
                    .sharedElement(
                        state = rememberSharedContentState(key = "image/${footItem.id}"),
                        animatedVisibilityScope
                    ),
                contentScale = ContentScale.Crop,
            )

            // Price Badge (Top Left)
            Box(
                modifier = Modifier
                    .padding(8.dp)
                    .background(Color.White, RoundedCornerShape(12.dp))
                    .padding(horizontal = 8.dp, vertical = 4.dp)
                    .align(Alignment.TopStart)
            ) {
                Text(
                    text = "$${footItem.price}",
                    style = TextStyle(fontSize = 12.sp, fontWeight = FontWeight.Bold, color = PrimaryOrange)
                )
            }

            // Favorite Icon (Top Right)
            Box(
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .padding(8.dp)
                    .size(28.dp)
                    .background(Color(0x33000000), CircleShape), // Translucent dark bg so it pops on light food
                contentAlignment = Alignment.Center
            ) {
                Image(
                    painter = painterResource(id = R.drawable.ic_favourite),
                    contentDescription = "Favorite",
                    modifier = Modifier.size(32.dp)
                )
            }

            // Rating Badge (Bottom Left)
            Row(
                modifier = Modifier
                    .align(Alignment.BottomStart)
                    .padding(8.dp)
                    .shadow(4.dp, RoundedCornerShape(32.dp))
                    .background(Color.White, RoundedCornerShape(32.dp))
                    .padding(horizontal = 8.dp, vertical = 4.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(text = "4.5", style = TextStyle(fontSize = 11.sp, fontWeight = FontWeight.Bold, color = Color(0xFF323643)))
                Spacer(modifier = Modifier.width(4.dp))
                Icon(
                    imageVector = Icons.Filled.Star,
                    contentDescription = null,
                    modifier = Modifier.size(12.dp),
                    tint = Color(0xFFFFC529)
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text(text = "(21)", style = TextStyle(fontSize = 10.sp, color = TextGray))
            }
        }

        // --- Text Details Section ---
        Column(
            modifier = Modifier
                .padding(12.dp)
                .fillMaxWidth()
        ) {
            Text(
                text = footItem.name,
                style = TextStyle(fontSize = 15.sp, fontWeight = FontWeight.Bold, color = Color(0xFF323643)),
                maxLines = 1,
                overflow = TextOverflow.Ellipsis, // Ensures long text truncates neatly
                modifier = Modifier.sharedElement(
                    state = rememberSharedContentState(key = "title/${footItem.id}"),
                    animatedVisibilityScope
                )
            )

            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text = footItem.description,
                style = TextStyle(fontSize = 12.sp, color = TextGray),
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }
    }
}