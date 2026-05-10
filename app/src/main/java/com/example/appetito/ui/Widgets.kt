package com.example.appetito.ui
import androidx.activity.ComponentActivity
import androidx.compose.animation.AnimatedContentTransitionScope
import androidx.compose.animation.EnterTransition
import androidx.compose.animation.ExitTransition
import androidx.compose.animation.SizeTransform
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldColors
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavBackStackEntry
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import com.example.appetito.R
import com.example.appetito.ui.features.auth.BaseAuthViewModel
import com.example.appetito.ui.theme.Primary
import kotlin.reflect.KClass
import kotlin.reflect.KType

@Composable
fun GroupSocialButtons(
    color: Color = Color.White,
    viewModel: BaseAuthViewModel
) {
    Column {
        Row(
            modifier = Modifier.fillMaxWidth().padding(bottom = 20.dp),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {
            HorizontalDivider(
                modifier = Modifier.weight(1f),
                thickness = 1.dp,
                color = color.copy(alpha = 0.3f)
            )
            Text(
                text = stringResource(id = R.string.sign_in_with),
                color = color.copy(alpha = 0.8f),
                fontSize = 14.sp,
                modifier = Modifier.padding(horizontal = 16.dp)
            )
            HorizontalDivider(
                modifier = Modifier.weight(1f),
                thickness = 1.dp,
                color = color.copy(alpha = 0.3f)
            )
        }

        val context = LocalContext.current as ComponentActivity

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(16.dp) // Equal spacing between buttons
        ) {
            SocialButton(
                modifier = Modifier.weight(1f),
                icon = R.drawable.ic_facebook,
                title = R.string.sign_with_facebook,
                onClick = { viewModel.onFacebookClicked(context) }
            )
            SocialButton(
                modifier = Modifier.weight(1f),
                icon = R.drawable.ic_google,
                title = R.string.sign_with_google,
                onClick = { viewModel.onGoogleClicked(context) }
            )
        }
    }
}

@Composable
fun SocialButton(
    modifier: Modifier = Modifier,
    icon: Int,
    title: Int,
    onClick: () -> Unit
){
    Button(
        onClick = onClick,
        modifier = modifier.height(54.dp), // Taller touch target
        colors = ButtonDefaults.buttonColors(containerColor = Color.White),
        shape = RoundedCornerShape(30.dp), // Perfect pill shape
        border = BorderStroke(1.dp, Color(0xFFEAEAEC)) // Subtle border so it doesn't get lost on white backgrounds
    ) {
        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.Center) {
            Image(
                painter = painterResource(id = icon),
                contentDescription = null,
                modifier = Modifier.size(24.dp)
            )
            Spacer(modifier = Modifier.size(12.dp))
            Text(
                text = stringResource(id = title),
                color = Color(0xFF323643),
                fontWeight = FontWeight.SemiBold,
                fontSize = 14.sp
            )
        }
    }
}

@Composable
fun FoodHubTextField(
    value: String,
    onValueChange: (String) -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    readOnly: Boolean = false,
    textStyle: TextStyle = LocalTextStyle.current,
    label: @Composable (() -> Unit)? = null,
    placeholder: @Composable (() -> Unit)? = null,
    leadingIcon: @Composable (() -> Unit)? = null,
    trailingIcon: @Composable (() -> Unit)? = null,
    prefix: @Composable (() -> Unit)? = null,
    suffix: @Composable (() -> Unit)? = null,
    supportingText: @Composable (() -> Unit)? = null,
    isError: Boolean = false,
    visualTransformation: VisualTransformation = VisualTransformation.None,
    keyboardOptions: KeyboardOptions = KeyboardOptions.Default,
    keyboardActions: KeyboardActions = KeyboardActions.Default,
    singleLine: Boolean = true, // Default to true for standard inputs
    maxLines: Int = if (singleLine) 1 else Int.MAX_VALUE,
    minLines: Int = 1,
    interactionSource: MutableInteractionSource = remember { MutableInteractionSource() },
    shape: Shape = RoundedCornerShape(16.dp), // Softer corners
    colors: TextFieldColors = OutlinedTextFieldDefaults.colors().copy(
        focusedIndicatorColor = Primary,
        unfocusedIndicatorColor = Color(0xFFEAEAEC), // Softer default outline
        focusedContainerColor = Color.White,
        unfocusedContainerColor = Color(0xFFFCFCFD) // Slight off-white when unfocused
    )
){
    Column(modifier = Modifier.padding(vertical = 6.dp)) {
        label?.let {
            Row(modifier = Modifier.padding(start = 4.dp, bottom = 6.dp)) {
                it()
            }
        }

        OutlinedTextField(
            value = value,
            onValueChange = onValueChange,
            modifier = modifier.fillMaxWidth(), // Removed fixed height so it scales properly
            enabled = enabled,
            readOnly = readOnly,
            textStyle = textStyle.copy(fontWeight = FontWeight.Medium, color = Color(0xFF323643)),
            label = null, // We keep the label above the text field
            placeholder = placeholder,
            leadingIcon = leadingIcon,
            trailingIcon = trailingIcon,
            prefix = prefix,
            suffix = suffix,
            supportingText = supportingText,
            isError = isError,
            visualTransformation = visualTransformation,
            keyboardOptions = keyboardOptions,
            keyboardActions = keyboardActions,
            singleLine = singleLine,
            maxLines = maxLines,
            minLines = minLines,
            interactionSource = interactionSource,
            shape = shape,
            colors = colors,
        )
    }
}

@Composable
fun BasicDialog(title: String, description: String, onClick: () -> Unit) {
    Surface(
        shape = RoundedCornerShape(24.dp),
        color = Color.White,
        shadowElevation = 8.dp
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = title,
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF323643),
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.size(12.dp))

            Text(
                text = description,
                fontSize = 15.sp,
                color = Color(0xFF9796A1),
                textAlign = TextAlign.Center,
                lineHeight = 22.sp
            )

            Spacer(modifier = Modifier.size(24.dp))

            Button(
                onClick = onClick,
                colors = ButtonDefaults.buttonColors(containerColor = Primary),
                shape = RoundedCornerShape(30.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(54.dp)
            ) {
                Text(
                    text = stringResource(id = R.string.ok),
                    color = Color.White,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}

fun LazyListScope.gridItems(
    count: Int,
    nColumns: Int,
    horizontalArrangement: Arrangement.Horizontal = Arrangement.Start,
    itemContent: @Composable BoxScope.(Int) -> Unit,
) {
    gridItems(
        data = List(count) { it },
        nColumns = nColumns,
        horizontalArrangement = horizontalArrangement,
        itemContent = itemContent,
    )
}

fun <T> LazyListScope.gridItems(
    data: List<T>,
    nColumns: Int,
    horizontalArrangement: Arrangement.Horizontal = Arrangement.Start,
    key: ((item: T) -> Any)? = null,
    itemContent: @Composable BoxScope.(T) -> Unit,
) {
    val rows = if (data.isEmpty()) 0 else 1 + (data.count() - 1) / nColumns
    items(rows) { rowIndex ->
        Row(horizontalArrangement = horizontalArrangement) {
            for (columnIndex in 0 until nColumns) {
                val itemIndex = rowIndex * nColumns + columnIndex
                if (itemIndex < data.count()) {
                    val item = data[itemIndex]
                    androidx.compose.runtime.key(key?.invoke(item)) {
                        Box(
                            modifier = Modifier.weight(1f, fill = true),
                            propagateMinConstraints = true
                        ) {
                            itemContent.invoke(this, item)
                        }
                    }
                } else {
                    Spacer(Modifier.weight(1f, fill = true))
                }
            }
        }
    }
}


@Composable
fun FoodHubNavHost(
    navController: NavHostController,
    startDestination: Any,
    modifier: Modifier = Modifier,
    contentAlignment: Alignment = Alignment.TopStart,
    route: KClass<*>? = null,
    typeMap: Map<KType, @JvmSuppressWildcards NavType<*>> = emptyMap(),
    enterTransition:
    (@JvmSuppressWildcards
    AnimatedContentTransitionScope<NavBackStackEntry>.() -> EnterTransition) =
        {
            slideIntoContainer(
                towards = AnimatedContentTransitionScope.SlideDirection.Left,
                animationSpec = tween(300)
            ) + fadeIn(animationSpec = tween(300))
        },
    exitTransition:
    (@JvmSuppressWildcards
    AnimatedContentTransitionScope<NavBackStackEntry>.() -> ExitTransition) =
        {
            slideOutOfContainer(
                towards = AnimatedContentTransitionScope.SlideDirection.Left,
                animationSpec = tween(300)
            ) + fadeOut(animationSpec = tween(300))
        },
    popEnterTransition:
    (@JvmSuppressWildcards
    AnimatedContentTransitionScope<NavBackStackEntry>.() -> EnterTransition) =
        {
            slideIntoContainer(
                towards = AnimatedContentTransitionScope.SlideDirection.Right,
                animationSpec = tween(300)
            ) + fadeIn(animationSpec = tween(300))
        },
    popExitTransition:
    (@JvmSuppressWildcards
    AnimatedContentTransitionScope<NavBackStackEntry>.() -> ExitTransition) =
        {
            slideOutOfContainer(
                towards = AnimatedContentTransitionScope.SlideDirection.Right,
                animationSpec = tween(300)
            ) + fadeOut(animationSpec = tween(300))
        },
    sizeTransform:
    (@JvmSuppressWildcards
    AnimatedContentTransitionScope<NavBackStackEntry>.() -> SizeTransform?)? =
        null,
    builder: NavGraphBuilder.() -> Unit
) {
    NavHost(
        navController = navController,
        startDestination = startDestination,
        modifier = modifier,
        contentAlignment = contentAlignment,
        route= route,
        typeMap = typeMap,
        enterTransition = enterTransition,
        exitTransition = exitTransition,
        popEnterTransition = popEnterTransition,
        popExitTransition = popExitTransition,
        sizeTransform = sizeTransform,
        builder = builder
    )
}
