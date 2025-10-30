package com.developerstring.nexpay.ui.screens.applock

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.*
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Security
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.delay
import androidx.navigation.NavController
import com.developerstring.nexpay.SplashScreenRoute
import com.developerstring.nexpay.ui.theme.AppColors
import com.developerstring.nexpay.viewmodel.SharedViewModel
import com.kmpalette.color
import kotlinx.serialization.Serializable

@Serializable
data object PinSetupScreenRoute

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PinSetupScreen(
    sharedViewModel: SharedViewModel,
    navController: NavController,
) {

    val imagePalette by sharedViewModel.imagePalette.collectAsState()

    val appLockState by sharedViewModel.appLockState.collectAsState()
    val pinInput by sharedViewModel.pinInput.collectAsState()

    var isConfirmingPin by remember { mutableStateOf(false) }
    var firstPin by remember { mutableStateOf("") }
    var isVisible by remember { mutableStateOf(false) }

    val isAppLocked by sharedViewModel.isAppLocked.collectAsState()

    // Animation states
    val headerScale by animateFloatAsState(
        targetValue = if (isVisible) 1f else 0.8f,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessLow
        )
    )
    val contentAlpha by animateFloatAsState(
        targetValue = if (isVisible) 1f else 0f,
        animationSpec = tween(800, delayMillis = 200)
    )

    // Handle PIN setup completion
    LaunchedEffect(appLockState.isAppLockEnabled) {
        if (appLockState.isAppLockEnabled && isAppLocked) {
            navController.navigate(SplashScreenRoute)
        }
    }

    // Handle PIN input completion
    LaunchedEffect(pinInput) {
        if (pinInput.length == 6) {
            if (!isConfirmingPin) {
                firstPin = pinInput
                isConfirmingPin = true
                sharedViewModel.clearPin()
            } else {
                sharedViewModel.setupPin(firstPin, pinInput)
            }
        }
    }

    LaunchedEffect(Unit) {
        isVisible = true
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
    ) {
        // Clean white interface
        Column(
            modifier = Modifier
                .fillMaxSize()
                .alpha(contentAlpha)
                .verticalScroll(rememberScrollState())
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(40.dp))

            // Back button with clean styling
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Start
            ) {
                IconButton(
                    onClick = { navController.popBackStack() },
                    modifier = Modifier
                        .size(48.dp)
                        .clip(CircleShape)
                        .background(Color.White)
                        .border(
                            width = 1.dp,
                            color = Color.Black.copy(alpha = 0.2f),
                            shape = CircleShape
                        )
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "Back",
                        tint = Color.Black,
                        modifier = Modifier.size(20.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(40.dp))

            // Clean security icon
            CleanSecurityHeader(
                modifier = Modifier.scale(headerScale)
            )

            Spacer(modifier = Modifier.height(32.dp))

            // Title and description with clean typography
            Text(
                text = if (isConfirmingPin) "Confirm Your PIN" else "Create Your PIN",
                style = MaterialTheme.typography.headlineMedium.copy(
                    fontSize = 28.sp,
                    letterSpacing = 0.8.sp
                ),
                fontWeight = FontWeight.Light,
                color = Color.Black,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(12.dp))

            Text(
                text = if (isConfirmingPin)
                    "Please re-enter your 6-digit PIN to confirm"
                else
                    "Create a secure 6-digit PIN to protect your wallet",
                style = MaterialTheme.typography.bodyLarge,
                color = Color.Black.copy(alpha = 0.6f),
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(horizontal = 16.dp),
                letterSpacing = 0.3.sp
            )

            Spacer(modifier = Modifier.height(48.dp))

            // Clean PIN dots indicator
            CleanPinDotsIndicator(
                pinLength = pinInput.length,
                isError = appLockState.authenticationError != null && isConfirmingPin,
                isConfirmStep = isConfirmingPin
            )

            Spacer(modifier = Modifier.height(32.dp))

            // Error message with clean styling
            appLockState.authenticationError?.let { error ->
                CleanPinErrorMessage(error = error)
                Spacer(modifier = Modifier.height(24.dp))
            }

            Spacer(modifier = Modifier.weight(1f))

            // Clean PIN keypad (reusing from AppLockScreen)
            ModernPinKeypad(
                onNumberClick = { digit: String ->
                    sharedViewModel.addPinDigit(digit)
                },
                onBackspaceClick = {
                    sharedViewModel.removePinDigit()
                },
                enabled = !appLockState.isAuthenticating,
                colorLight = imagePalette?.palette?.lightVibrantSwatch?.color ?: Color.White,
                colorDark = imagePalette?.palette?.darkVibrantSwatch?.color ?: Color.Black
            )

            Spacer(modifier = Modifier.height(32.dp))
        }

        // Loading overlay with clean design
        if (appLockState.isAuthenticating) {
            CleanLoadingOverlay()
        }
    }
}

@Composable
fun CleanSecurityHeader(
    modifier: Modifier = Modifier
) {
    val pulseAnimation = rememberInfiniteTransition()
    val pulseAlpha by pulseAnimation.animateFloat(
        initialValue = 0.1f,
        targetValue = 0.3f,
        animationSpec = infiniteRepeatable(
            animation = tween(2500, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        )
    )

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = modifier
    ) {
        // Security icon with subtle glow effect
        Box(
            modifier = Modifier.size(90.dp),
            contentAlignment = Alignment.Center
        ) {
            // Subtle glow background
            Box(
                modifier = Modifier
                    .size(90.dp)
                    .clip(CircleShape)
                    .background(
                        Brush.radialGradient(
                            colors = listOf(
                                Color.Black.copy(alpha = pulseAlpha * 0.1f),
                                Color.Transparent
                            ),
                            radius = 70f
                        )
                    )
            )

            // Icon container
            Box(
                modifier = Modifier
                    .size(70.dp)
                    .clip(CircleShape)
                    .border(
                        width = 1.dp,
                        color = Color.Black.copy(alpha = 0.2f),
                        shape = CircleShape
                    )
                    .background(
                        Brush.radialGradient(
                            colors = listOf(
                                Color.Black.copy(alpha = 0.05f),
                                Color.Transparent
                            )
                        )
                    ),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.Security,
                    contentDescription = "Security",
                    modifier = Modifier.size(32.dp),
                    tint = Color.Black.copy(alpha = 0.8f)
                )
            }
        }
    }
}

@Composable
fun CleanPinDotsIndicator(
    pinLength: Int,
    isError: Boolean = false,
    isConfirmStep: Boolean = false
) {
    Row(
        horizontalArrangement = Arrangement.spacedBy(16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        repeat(6) { index ->
            val scale by animateFloatAsState(
                targetValue = if (index < pinLength) 1.2f else 1f,
                animationSpec = spring(
                    dampingRatio = Spring.DampingRatioMediumBouncy,
                    stiffness = Spring.StiffnessHigh
                )
            )

            Box(
                modifier = Modifier
                    .size(14.dp)
                    .scale(scale)
                    .clip(CircleShape)
                    .border(
                        width = 1.dp,
                        color = when {
                            isError -> Color(0xFFE53E3E)
                            isConfirmStep -> Color.Black.copy(alpha = 0.4f)
                            else -> Color.Black.copy(alpha = 0.3f)
                        },
                        shape = CircleShape
                    )
                    .background(
                        color = when {
                            isError -> Color(0xFFE53E3E)
                            index < pinLength -> Color.Black
                            else -> Color.Transparent
                        },
                        shape = CircleShape
                    )
            )
        }
    }
}

@Composable
fun CleanPinErrorMessage(error: String) {
    var isVisible by remember { mutableStateOf(false) }

    LaunchedEffect(error) {
        isVisible = true
        delay(4000)
        isVisible = false
    }

    AnimatedVisibility(
        visible = isVisible,
        enter = slideInVertically() + fadeIn(),
        exit = slideOutVertically() + fadeOut()
    ) {
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(
                containerColor = Color(0xFFFFF5F5)
            ),
            shape = RoundedCornerShape(12.dp),
            border = BorderStroke(1.dp, Color(0xFFE53E3E).copy(alpha = 0.3f))
        ) {
            Text(
                text = error,
                color = Color(0xFFE53E3E),
                style = MaterialTheme.typography.bodyMedium,
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(16.dp),
                fontWeight = FontWeight.Medium
            )
        }
    }
}

@Composable
fun CleanLoadingOverlay() {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black.copy(alpha = 0.3f)),
        contentAlignment = Alignment.Center
    ) {
        Card(
            colors = CardDefaults.cardColors(
                containerColor = Color.White
            ),
            shape = RoundedCornerShape(20.dp),
            border = BorderStroke(1.dp, Color.Black.copy(alpha = 0.1f)),
            elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
        ) {
            Column(
                modifier = Modifier.padding(40.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                CircularProgressIndicator(
                    color = Color.Black,
                    strokeWidth = 2.dp,
                    modifier = Modifier.size(32.dp)
                )
                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    text = "Setting up PIN...",
                    color = Color.Black,
                    style = MaterialTheme.typography.bodyLarge,
                    fontWeight = FontWeight.Medium
                )
            }
        }
    }
}


@Composable
fun ModernPinErrorMessage(error: String) {
    var isVisible by remember { mutableStateOf(false) }

    LaunchedEffect(error) {
        isVisible = true
        delay(4000)
        isVisible = false
    }

    AnimatedVisibility(
        visible = isVisible,
        enter = slideInVertically(
            animationSpec = tween(300),
            initialOffsetY = { -it }
        ) + fadeIn(animationSpec = tween(300)),
        exit = slideOutVertically(
            animationSpec = tween(300),
            targetOffsetY = { -it }
        ) + fadeOut(animationSpec = tween(300))
    ) {
        Card(
            colors = CardDefaults.cardColors(
                containerColor = Color.Transparent
            ),
            border = BorderStroke(1.dp, AppColors.marsRed.copy(alpha = 0.3f)),
            shape = RoundedCornerShape(12.dp)
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(
                        Brush.horizontalGradient(
                            colors = listOf(
                                AppColors.marsRed.copy(alpha = 0.1f),
                                AppColors.marsRed.copy(alpha = 0.05f)
                            )
                        )
                    )
                    .padding(16.dp)
            ) {
                Text(
                    text = error,
                    color = AppColors.marsRed.copy(alpha = 0.9f),
                    style = MaterialTheme.typography.bodyMedium.copy(
                        letterSpacing = 0.3.sp
                    ),
                    textAlign = TextAlign.Center,
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }
    }
}



