package com.example.appetito.ui.feature.menu.add

import android.widget.Toast
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
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Image
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import coil3.compose.AsyncImage
import com.example.appetito.R
import com.example.appetito.data.models.CustomizationGroup
import com.example.appetito.data.models.CustomizationOption
import com.example.appetito.ui.FoodHubTextField
import kotlinx.coroutines.flow.collectLatest

val PrimaryOrange = Color(0xFFFE724C)
val TextGray = Color(0xFF9796A1)
val BgLight = Color(0xFFF9F9FB)

@Composable
fun AddMenuItemScreen(
    navController: NavController,
    viewModel: AddMenuItemViewModel = hiltViewModel()
) {

    val name = viewModel.name.collectAsStateWithLifecycle()
    val description = viewModel.description.collectAsStateWithLifecycle()
    val price = viewModel.price.collectAsStateWithLifecycle()
    val customizationGroups = viewModel.customizationGroups.collectAsStateWithLifecycle()
    val uiState = viewModel.addMenuItemState.collectAsStateWithLifecycle()
    val imageUrlString = viewModel.imageUrlString.collectAsStateWithLifecycle()

    LaunchedEffect(key1 = true) {
        viewModel.addMenuItemEvent.collectLatest {
            when (it) {
                is AddMenuItemViewModel.AddMenuItemEvent.GoBack -> {
                    Toast.makeText(
                        navController.context, "Item added Successfully", Toast.LENGTH_SHORT
                    ).show()
                    navController.previousBackStackEntry?.savedStateHandle?.set("added", true)
                    navController.popBackStack()
                }
                is AddMenuItemViewModel.AddMenuItemEvent.ShowErrorMessage -> {
                    Toast.makeText(navController.context, it.message, Toast.LENGTH_SHORT).show()
                }
                else -> {}
            }
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(BgLight)
    ) {
        // --- HEADER ---
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 16.dp, start = 24.dp, end = 24.dp, bottom = 16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(44.dp)
                    .shadow(12.dp, shape = RoundedCornerShape(12.dp), spotColor = Color(0x1A000000))
                    .background(Color.White, RoundedCornerShape(12.dp))
                    .clickable { navController.popBackStack() },
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    painter = painterResource(id = R.drawable.ic_back), // Assuming you have this from previous screens
                    contentDescription = "Back",
                    tint = Color.Unspecified
                )
            }

            Text(
                text = "Add Menu Item",
                style = TextStyle(fontSize = 18.sp, fontWeight = FontWeight.Bold, color = Color(0xFF323643))
            )

            Spacer(modifier = Modifier.size(44.dp)) // To keep title centered
        }

        // --- SCROLLABLE CONTENT ---
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 24.dp)
        ) {

            // 1. Image Section
            SectionCard(title = "Item Image") {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(180.dp)
                        .clip(RoundedCornerShape(16.dp))
                        .background(Color(0xFFF1F2F6))
                        .border(
                            width = 2.dp,
                            color = PrimaryOrange.copy(alpha = 0.3f),
                            shape = RoundedCornerShape(16.dp)
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    if (imageUrlString.value.isBlank()) {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.Center
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(48.dp)
                                    .background(PrimaryOrange.copy(alpha = 0.1f), CircleShape),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Image,
                                    contentDescription = "Image Preview",
                                    tint = PrimaryOrange,
                                    modifier = Modifier.size(24.dp)
                                )
                            }
                            Spacer(modifier = Modifier.height(12.dp))
                            Text(
                                text = "Preview will appear here",
                                color = TextGray,
                                fontSize = 14.sp
                            )
                        }
                    } else {
                        AsyncImage(
                            model = imageUrlString.value,
                            contentDescription = "Food Image",
                            modifier = Modifier.fillMaxSize(),
                            contentScale = ContentScale.Crop
                        )
                    }
                }

                Spacer(modifier = Modifier.height(20.dp))

                FoodHubTextField(
                    value = imageUrlString.value,
                    onValueChange = { viewModel.onImageUrlStringChange(it) },
                    modifier = Modifier.fillMaxWidth(),
                    label = { Text(text = "Paste Image URL") }
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            // 2. Basic Details Section
            SectionCard(title = "Basic Details") {
                FoodHubTextField(
                    value = name.value,
                    onValueChange = { viewModel.onNameChange(it) },
                    modifier = Modifier.fillMaxWidth(),
                    label = { Text(text = "Item Name (e.g. Cheese Burger)") }
                )

                Spacer(modifier = Modifier.height(16.dp))

                FoodHubTextField(
                    value = description.value,
                    onValueChange = { viewModel.onDescriptionChange(it) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(120.dp), // Slightly taller for better UX
                    label = { Text(text = "Description") },
                    singleLine = false
                )

                Spacer(modifier = Modifier.height(16.dp))

                FoodHubTextField(
                    value = price.value,
                    onValueChange = { viewModel.onPriceChange(it) },
                    modifier = Modifier.fillMaxWidth(),
                    label = { Text(text = "Base Price ($)") }
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            // 3. Customizations Section
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Customizations",
                    style = TextStyle(fontSize = 18.sp, fontWeight = FontWeight.Bold, color = Color(0xFF323643))
                )
                Button(
                    onClick = { viewModel.addCustomizationGroup() },
                    colors = ButtonDefaults.buttonColors(containerColor = PrimaryOrange.copy(alpha = 0.1f)),
                    contentPadding = androidx.compose.foundation.layout.PaddingValues(horizontal = 16.dp, vertical = 8.dp),
                    shape = RoundedCornerShape(20.dp)
                ) {
                    Icon(imageVector = Icons.Default.Add, contentDescription = "Add Group", tint = PrimaryOrange, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(text = "Add Group", color = PrimaryOrange, fontWeight = FontWeight.SemiBold, fontSize = 13.sp)
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            if (customizationGroups.value.isEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(Color.White, RoundedCornerShape(16.dp))
                        .padding(32.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(text = "No customizations added yet.", color = TextGray, fontSize = 14.sp)
                }
            } else {
                customizationGroups.value.forEachIndexed { groupIndex, group ->
                    CustomizationGroupItem(
                        group = group,
                        onGroupChange = { viewModel.updateCustomizationGroup(groupIndex, it) },
                        onRemoveGroup = { viewModel.removeCustomizationGroup(groupIndex) },
                        onAddOption = { viewModel.addOptionToGroup(groupIndex) },
                        onRemoveOption = { optionIndex -> viewModel.removeOptionFromGroup(groupIndex, optionIndex) },
                        onOptionChange = { optionIndex, option -> viewModel.updateOptionInGroup(groupIndex, optionIndex, option) }
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                }
            }

            Spacer(modifier = Modifier.height(32.dp))

            // 4. Save Button
            if (uiState.value is AddMenuItemViewModel.AddMenuItemState.Loading) {
                Button(
                    onClick = { },
                    enabled = false,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(60.dp),
                    shape = RoundedCornerShape(30.dp),
                    colors = ButtonDefaults.buttonColors(
                        disabledContainerColor = Color(0xFFEAEAEC),
                        disabledContentColor = TextGray
                    )
                ) {
                    Text(text = "Saving Item...", fontSize = 16.sp, fontWeight = FontWeight.Bold)
                }
            } else {
                if (uiState.value is AddMenuItemViewModel.AddMenuItemState.Error) {
                    Text(
                        text = (uiState.value as AddMenuItemViewModel.AddMenuItemState.Error).message,
                        color = Color(0xFFF44336),
                        modifier = Modifier.padding(bottom = 8.dp)
                    )
                }
                Button(
                    onClick = { viewModel.addMenuItem() },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(60.dp)
                        .shadow(16.dp, RoundedCornerShape(30.dp), spotColor = PrimaryOrange.copy(alpha = 0.5f)),
                    shape = RoundedCornerShape(30.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = PrimaryOrange)
                ) {
                    Text(text = "SAVE MENU ITEM", fontSize = 15.sp, fontWeight = FontWeight.Bold, color = Color.White)
                }
            }

            Spacer(modifier = Modifier.height(40.dp))
        }
    }
}

// Reusable White Card for main sections
@Composable
fun SectionCard(title: String, content: @Composable () -> Unit) {
    Column {
        Text(
            text = title,
            style = TextStyle(fontSize = 18.sp, fontWeight = FontWeight.Bold, color = Color(0xFF323643)),
            modifier = Modifier.padding(bottom = 12.dp)
        )
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .shadow(elevation = 8.dp, shape = RoundedCornerShape(16.dp), spotColor = Color(0x1A000000))
                .background(Color.White, RoundedCornerShape(16.dp))
                .padding(20.dp)
        ) {
            Column {
                content()
            }
        }
    }
}

@Composable
fun CustomizationGroupItem(
    group: CustomizationGroup,
    onGroupChange: (CustomizationGroup) -> Unit,
    onRemoveGroup: () -> Unit,
    onAddOption: () -> Unit,
    onRemoveOption: (Int) -> Unit,
    onOptionChange: (Int, CustomizationOption) -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .shadow(elevation = 4.dp, shape = RoundedCornerShape(16.dp), spotColor = Color(0x1A000000))
            .background(Color.White, RoundedCornerShape(16.dp))
            .padding(16.dp)
    ) {
        Column {
            // Group Header
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                CustomizationTextField(
                    value = group.name,
                    onValueChange = { onGroupChange(group.copy(name = it)) },
                    placeholder = "Group Name (e.g. Size)",
                    modifier = Modifier.weight(1f)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Box(
                    modifier = Modifier
                        .size(42.dp)
                        .background(Color(0xFFFFF2F2), RoundedCornerShape(10.dp))
                        .clickable { onRemoveGroup() },
                    contentAlignment = Alignment.Center
                ) {
                    Icon(imageVector = Icons.Default.Delete, contentDescription = "Remove Group", tint = Color(0xFFFF4B4B), modifier = Modifier.size(20.dp))
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Group Settings
            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.SpaceBetween, modifier = Modifier.fillMaxWidth()) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Checkbox(
                        checked = group.isRequired,
                        onCheckedChange = { onGroupChange(group.copy(isRequired = it)) },
                        colors = CheckboxDefaults.colors(checkedColor = PrimaryOrange)
                    )
                    Text(text = "Required", fontSize = 14.sp, color = Color(0xFF323643), fontWeight = FontWeight.Medium)
                }

                CustomizationTextField(
                    value = group.maxSelections.toString(),
                    onValueChange = { onGroupChange(group.copy(maxSelections = it.toIntOrNull() ?: 1)) },
                    placeholder = "Max Qty",
                    modifier = Modifier.width(90.dp),
                    isNumeric = true
                )
            }

            Spacer(modifier = Modifier.height(16.dp))
            Divider(color = Color(0xFFF1F2F6), thickness = 1.dp)
            Spacer(modifier = Modifier.height(16.dp))

            // Options List
            group.options.forEachIndexed { optionIndex, option ->
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    CustomizationTextField(
                        value = option.name,
                        onValueChange = { onOptionChange(optionIndex, option.copy(name = it)) },
                        placeholder = "Option (e.g. Large)",
                        modifier = Modifier.weight(1f)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    CustomizationTextField(
                        value = if(option.price == 0.0) "" else option.price.toString(), // Hide 0.0 for a cleaner empty state
                        onValueChange = { onOptionChange(optionIndex, option.copy(price = it.toDoubleOrNull() ?: 0.0)) },
                        placeholder = "Price +$",
                        modifier = Modifier.width(90.dp),
                        isNumeric = true
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    IconButton(onClick = { onRemoveOption(optionIndex) }, modifier = Modifier.size(36.dp)) {
                        Icon(imageVector = Icons.Default.Delete, contentDescription = "Remove Option", tint = TextGray, modifier = Modifier.size(20.dp))
                    }
                }
            }

            // Add Option Button
            Button(
                onClick = onAddOption,
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFF6F6F9), contentColor = Color(0xFF323643)),
                shape = RoundedCornerShape(10.dp) // Matched the text field corner radius
            ) {
                Icon(imageVector = Icons.Default.Add, contentDescription = null, modifier = Modifier.size(18.dp))
                Spacer(modifier = Modifier.width(8.dp))
                Text(text = "Add Option", fontSize = 14.sp, fontWeight = FontWeight.SemiBold)
            }
        }
    }
}

@Composable
fun CustomizationTextField(
    value: String,
    onValueChange: (String) -> Unit,
    placeholder: String,
    modifier: Modifier = Modifier,
    isNumeric: Boolean = false
) {
    BasicTextField(
        value = value,
        onValueChange = onValueChange,
        modifier = modifier
            // A soft, modern gray background with rounded corners
            .background(Color(0xFFF6F6F9), RoundedCornerShape(10.dp))
            .padding(horizontal = 14.dp, vertical = 12.dp),
        textStyle = TextStyle(
            fontSize = 14.sp,
            color = Color(0xFF323643),
            fontWeight = FontWeight.Medium
        ),
        keyboardOptions = KeyboardOptions(
            keyboardType = if (isNumeric) KeyboardType.Decimal else KeyboardType.Text
        ),
        singleLine = true,
        decorationBox = { innerTextField ->
            Box(
                modifier = Modifier.fillMaxWidth(),
                contentAlignment = Alignment.CenterStart
            ) {
                // Show placeholder when empty
                if (value.isEmpty()) {
                    Text(
                        text = placeholder,
                        style = TextStyle(fontSize = 14.sp, color = Color(0xFFAAAAAA))
                    )
                }
                // The actual input field
                innerTextField()
            }
        }
    )
}