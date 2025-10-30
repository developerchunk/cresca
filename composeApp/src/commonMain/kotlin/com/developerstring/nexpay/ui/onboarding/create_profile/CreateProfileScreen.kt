package com.developerstring.nexpay.ui.onboarding.create_profile

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.*
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectHorizontalDragGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.blur
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.developerstring.nexpay.ui.MainScreenRoute
import com.developerstring.nexpay.ui.bottom_nav.HomeScreenRoute
import com.developerstring.nexpay.ui.screens.applock.PinSetupScreenRoute
import com.developerstring.nexpay.viewmodel.SharedViewModel
import com.kmpalette.PaletteState
import com.kmpalette.rememberPaletteState
import kotlinx.coroutines.delay
import kotlinx.serialization.Serializable
import nexpay.composeapp.generated.resources.*
import org.jetbrains.compose.resources.imageResource
import kotlin.math.abs

@Serializable
object CreateProfileScreenRoute

@Composable
fun CreateProfileScreen(
    sharedViewModel: SharedViewModel,
    navController: NavController
) {
    val listOfImages = listOf(
        imageResource(Res.drawable.avt_1),
        imageResource(Res.drawable.avt_2),
        imageResource(Res.drawable.avt_3),
        imageResource(Res.drawable.avt_4),
        imageResource(Res.drawable.avt_5),
        imageResource(Res.drawable.avt_6),
        imageResource(Res.drawable.avt_7),
        imageResource(Res.drawable.avt_8),
        imageResource(Res.drawable.avt_9),
    )

    var selectedImageIndex by remember { mutableIntStateOf(6) }
    var isLoading by remember { mutableStateOf(false) }

    val userName by sharedViewModel.getUserName().collectAsState(initial = null)
    if (userName != null) {
        ColorSchema(
            imageBitmap = listOfImages[selectedImageIndex],
            imageColors = { paletteState ->
                sharedViewModel.setImagePalette(paletteState)
            }
        )
    }

    LaunchedEffect(userName) {
        if (userName != null) {
            selectedImageIndex = userName ?: 6
        }
    }



    // Handle navigation with delay when loading starts
    LaunchedEffect(isLoading) {
        if (isLoading) {
            delay(1500) // 1.5 second delay

            sharedViewModel.setUserName("$selectedImageIndex")

            if (userName != null) {
                navController.popBackStack()
            } else {
                navController.navigate(PinSetupScreenRoute) {
                    popUpTo(CreateProfileScreenRoute) { inclusive = true }
                }
            }
        }
    }

    // Generate color palette for selected image
    var backgroundColor1 by remember { mutableStateOf(Color(0xFF6B4EFF)) }
    var backgroundColor2 by remember { mutableStateOf(Color(0xFFFF6B9D)) }

    ColorSchema(
        imageBitmap = listOfImages[selectedImageIndex],
        imageColors = { paletteState ->
            paletteState.palette?.let { palette ->
                backgroundColor1 = Color(palette.vibrantSwatch?.rgb ?: 0xFF6B4EFF.toInt())
                backgroundColor2 = Color(palette.lightVibrantSwatch?.rgb ?: 0xFFFF6B9D.toInt())
            }
        }
    )

    // Animated background colors
    val animatedColor1 by animateColorAsState(
        targetValue = backgroundColor1,
        animationSpec = tween(800, easing = FastOutSlowInEasing),
        label = "backgroundColor1"
    )

    val animatedColor2 by animateColorAsState(
        targetValue = backgroundColor2,
        animationSpec = tween(800, easing = FastOutSlowInEasing),
        label = "backgroundColor2"
    )

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.radialGradient(
                    colors = listOf(
                        animatedColor1.copy(alpha = 0.6f),
                        animatedColor2.copy(alpha = 0.4f),
                        animatedColor1.copy(alpha = 0.3f)
                    ),
                    radius = 1500f
                )
            )
    ) {
//         Blurred background layer
        Box(
            modifier = Modifier
                .fillMaxSize()
                .blur(50.dp)
                .background(
                    Brush.linearGradient(
                        colors = listOf(
                            animatedColor1.copy(alpha = 0.3f),
                            animatedColor2.copy(alpha = 0.5f)
                        )
                    )
                )
        )

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.padding(top = 60.dp)
            ) {
                Text(
                    text = "Choose Your Avatar",
                    fontSize = 32.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "Pick an avatar that represents you",
                    fontSize = 16.sp,
                    color = Color.White.copy(alpha = 0.7f)
                )
            }

            // Avatar Carousel
            AvatarCarousel(
                images = listOfImages,
                selectedIndex = selectedImageIndex,
                onIndexChange = { newIndex ->
                    selectedImageIndex = newIndex
                }
            )

            // Continue Button
            AnimatedContinueButton(
                isLoading = isLoading,
                onClick = {
                    isLoading = true
                }
            )
        }
    }
}

@Composable
fun AvatarCarousel(
    images: List<ImageBitmap>,
    selectedIndex: Int,
    onIndexChange: (Int) -> Unit
) {
    var dragOffset by remember { mutableFloatStateOf(0f) }

    // Reset drag offset when index changes
    LaunchedEffect(selectedIndex) {
        dragOffset = 0f
    }

    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(280.dp),
            contentAlignment = Alignment.Center
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .pointerInput(selectedIndex) {
                        detectHorizontalDragGestures(
                            onDragEnd = {
                                when {
                                    dragOffset > 100 && selectedIndex > 0 -> {
                                        onIndexChange(selectedIndex - 1)
                                    }

                                    dragOffset < -100 && selectedIndex < images.size - 1 -> {
                                        onIndexChange(selectedIndex + 1)
                                    }
                                }
                                dragOffset = 0f
                            },
                            onHorizontalDrag = { _, dragAmount ->
                                dragOffset += dragAmount
                            }
                        )
                    },
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                AvatarCard(
                    image = images[selectedIndex],
                    position = 0,
                    dragProgress = (dragOffset / 300f).coerceIn(-1f, 1f),
                    currentSelectedIndex = selectedIndex,
                    cardIndex = selectedIndex
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Avatar indicator
        AvatarIndicator(
            totalImages = images.size,
            selectedIndex = selectedIndex
        )
    }
}

@Composable
fun AvatarCard(
//    modifier: Modifier,
    image: ImageBitmap,
    position: Int, // -1 = left, 0 = center, 1 = right
    dragProgress: Float, // -1 to 1, represents drag direction and amount
    currentSelectedIndex: Int,
    cardIndex: Int
) {
    val isSelected = position == 0

    // Calculate scale with smooth transitions - more dramatic difference between selected/unselected
    val baseScale = when (position) {
        -1 -> 0.5f  // Left card much smaller
        0 -> 1.0f   // Center card full size
        1 -> 0.5f   // Right card much smaller
        else -> 0.5f
    }

    // Adjust scale based on drag progress for responsive feedback
    val dragScaleAdjustment = when (position) {
        -1 -> if (dragProgress > 0) dragProgress * 0.5f else 0f // Growing more when dragging right
        0 -> -abs(dragProgress) * 0.25f // Shrinking when dragging
        1 -> if (dragProgress < 0) -dragProgress * 0.5f else 0f // Growing more when dragging left
        else -> 0f
    }

    val targetScale = (baseScale + dragScaleAdjustment).coerceIn(0.4f, 1.15f)

    // Long smooth animation for scale changes
    val animatedScale by animateFloatAsState(
        targetValue = targetScale,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioLowBouncy,
            stiffness = Spring.StiffnessVeryLow
        ),
        label = "cardScale_$cardIndex"
    )

    // Calculate alpha based on position and drag
    val baseAlpha = when (position) {
        -1 -> 0.3f
        0 -> 1f
        1 -> 0.3f
        else -> 0.3f
    }

    val dragAlphaAdjustment = when (position) {
        -1 -> if (dragProgress > 0) dragProgress * 0.7f else 0f
        0 -> -abs(dragProgress) * 0.4f
        1 -> if (dragProgress < 0) -dragProgress * 0.7f else 0f
        else -> 0f
    }

    val targetAlpha = (baseAlpha + dragAlphaAdjustment).coerceIn(0.2f, 1f)

    // Long smooth animation for alpha changes
    val animatedAlpha by animateFloatAsState(
        targetValue = targetAlpha,
        animationSpec = tween(600, easing = FastOutSlowInEasing),
        label = "cardAlpha_$cardIndex"
    )

    // Size based on selection with more dramatic difference
    val cardSize = if (isSelected) 240.dp else 90.dp

    // Long smooth animation for size changes
    val animatedSize by animateDpAsState(
        targetValue = cardSize,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioLowBouncy,
            stiffness = Spring.StiffnessVeryLow
        ),
        label = "cardSize_$cardIndex"
    )

    Box(
        modifier = Modifier
            .size(animatedSize)
            .scale(animatedScale)
            .graphicsLayer {
                alpha = animatedAlpha
            },
        contentAlignment = Alignment.Center
    ) {
        // Circular white border/background
        Box(
            modifier = Modifier
                .fillMaxSize()
                .clip(CircleShape)
                .background(Color.White.copy(alpha = 0.2f))
                .padding(8.dp),
            contentAlignment = Alignment.Center
        ) {
            // Circular image
            Image(
                bitmap = image,
                contentDescription = "Avatar ${cardIndex + 1}",
                modifier = Modifier
                    .fillMaxSize()
                    .clip(CircleShape),
                contentScale = ContentScale.Crop
            )
        }
    }
}

@Composable
fun AvatarIndicator(
    totalImages: Int,
    selectedIndex: Int
) {
    Row(
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        repeat(totalImages) { index ->
            val isSelected = index == selectedIndex

            // Animated shape transition
            val animatedCornerRadius by animateDpAsState(
                targetValue = if (isSelected) 8.dp else 50.dp,
                animationSpec = spring(
                    dampingRatio = Spring.DampingRatioMediumBouncy,
                    stiffness = Spring.StiffnessMedium
                ),
                label = "cornerRadius_$index"
            )

            // Animated width for selected indicator
            val animatedWidth by animateDpAsState(
                targetValue = if (isSelected) 24.dp else 8.dp,
                animationSpec = spring(
                    dampingRatio = Spring.DampingRatioMediumBouncy,
                    stiffness = Spring.StiffnessMedium
                ),
                label = "width_$index"
            )

            Box(
                modifier = Modifier
                    .width(animatedWidth)
                    .height(8.dp)
                    .clip(RoundedCornerShape(animatedCornerRadius))
                    .background(
                        if (isSelected)
                            Color.White
                        else
                            Color.White.copy(alpha = 0.3f)
                    )
            )
        }
    }
}

@Composable
fun AnimatedContinueButton(
    isLoading: Boolean,
    onClick: () -> Unit
) {
    // Button entrance animation
    var hasAppeared by remember { mutableStateOf(false) }
    val appearScale by animateFloatAsState(
        targetValue = if (hasAppeared) 1f else 0f,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessMedium
        ),
        label = "appearScale"
    )

    LaunchedEffect(Unit) {
        delay(300)
        hasAppeared = true
    }

    // Width animation for loading state
    val buttonWidth by animateDpAsState(
        targetValue = if (isLoading) 64.dp else 280.dp,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessMedium
        ),
        label = "buttonWidth"
    )

    Box(
        modifier = Modifier
            .padding(bottom = 32.dp)
            .scale(appearScale),
        contentAlignment = Alignment.Center
    ) {
        Button(
            onClick = {
                if (!isLoading) onClick()
            },
            modifier = Modifier
                .width(buttonWidth)
                .height(64.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = Color.White,
                contentColor = Color.Black
            ),
            shape = if (isLoading) CircleShape else RoundedCornerShape(32.dp),
        ) {
            if (isLoading) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(36.dp),
                        color = Color.Black,
                        strokeWidth = 4.dp,
                    )
                }
            } else {
                Text(
                    text = "Continue",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.SemiBold
                )
            }
        }
    }
}

@Composable
fun ColorSchema(
    imageBitmap: ImageBitmap,
    imageColors: (PaletteState<ImageBitmap>) -> Unit,
) {
    val paletteState = rememberPaletteState()
    LaunchedEffect(imageBitmap) {
        paletteState.generate(imageBitmap)
        imageColors(paletteState)
    }
}

