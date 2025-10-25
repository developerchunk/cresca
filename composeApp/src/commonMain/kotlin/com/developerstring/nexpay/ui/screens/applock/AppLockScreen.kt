package com.developerstring.nexpay.ui.screens.applock

import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Backspace
import androidx.compose.material.icons.filled.Fingerprint
import androidx.compose.material.icons.filled.Lock
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
import androidx.navigation.NavController
import com.developerstring.nexpay.ui.MainScreenRoute
import com.developerstring.nexpay.ui.theme.AppColors
import com.developerstring.nexpay.viewmodel.SharedViewModel
import kotlinx.coroutines.delay
import kotlinx.serialization.Serializable

@Serializable
data object AppLockScreenRoute

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AppLockScreen(
    sharedViewModel: SharedViewModel,
    navController: NavController,
) {
    val appLockState by sharedViewModel.appLockState.collectAsState()
    val pinInput by sharedViewModel.pinInput.collectAsState()
    val isAppLocked by sharedViewModel.isAppLocked.collectAsState()

    // Animation states
    var isVisible by remember { mutableStateOf(false) }
    val logoScale by animateFloatAsState(
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

    LaunchedEffect(Unit) {
        isVisible = true
    }

    LaunchedEffect(pinInput) {
        if (pinInput.length == 6) {
            sharedViewModel.validatePin(pinInput)
        }
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
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(60.dp))

            // Clean logo/brand section
            Card(
                modifier = Modifier
                    .size(80.dp)
                    .scale(logoScale),
                colors = CardDefaults.cardColors(
                    containerColor = Color.Transparent
                ),
                shape = CircleShape,
                border = BorderStroke(2.dp, Color.Black.copy(alpha = 0.1f))
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
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
                        imageVector = Icons.Default.Lock,
                        contentDescription = "NexPay",
                        modifier = Modifier.size(32.dp),
                        tint = Color.Black.copy(alpha = 0.8f)
                    )
                }
            }

            Spacer(modifier = Modifier.height(32.dp))

            // Brand name with clean typography
            Text(
                text = "NexPay",
                style = MaterialTheme.typography.headlineLarge.copy(
                    fontSize = 32.sp,
                    letterSpacing = 1.sp
                ),
                fontWeight = FontWeight.Light,
                color = Color.Black
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "Secure Access",
                style = MaterialTheme.typography.bodyLarge,
                color = Color.Black.copy(alpha = 0.6f),
                fontWeight = FontWeight.Normal
            )

            Spacer(modifier = Modifier.height(48.dp))

            // Clean PIN dots indicator
            ModernPinDotsIndicator(
                pinLength = pinInput.length,
                isError = !appLockState.isPinValid
            )

            Spacer(modifier = Modifier.height(32.dp))

            // Error message with clean design
            appLockState.authenticationError?.let { error ->
                AnimatedErrorMessage(error = error)
            }

            Spacer(modifier = Modifier.height(48.dp))

            // Biometric authentication button
            if (appLockState.isBiometricEnabled && appLockState.isBiometricAvailable) {
                ModernBiometricButton(
                    onClick = { sharedViewModel.authenticateWithBiometric() },
                    enabled = !appLockState.isAuthenticating
                )

                Spacer(modifier = Modifier.height(32.dp))
            }

            Spacer(modifier = Modifier.weight(1f))

            // Clean PIN keypad
            ModernPinKeypad(
                onNumberClick = { digit ->
                    sharedViewModel.addPinDigit(digit)
                },
                onBackspaceClick = {
                    sharedViewModel.removePinDigit()
                },
                enabled = !appLockState.isAuthenticating
            )

            Spacer(modifier = Modifier.height(32.dp))
        }

        // Loading overlay with clean design
        if (appLockState.isAuthenticating) {
            ModernLoadingOverlay()
        }
    }

    if (!isAppLocked) {
        navController.navigate(MainScreenRoute)
    }
}

@Composable
fun ModernPinDotsIndicator(
    pinLength: Int,
    isError: Boolean = false
) {
    Row(
        horizontalArrangement = Arrangement.spacedBy(16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        repeat(6) { index ->
            val scale by animateFloatAsState(
                targetValue = if (index < pinLength) 1.1f else 1f,
                animationSpec = spring(
                    dampingRatio = Spring.DampingRatioMediumBouncy,
                    stiffness = Spring.StiffnessHigh
                )
            )

            Box(
                modifier = Modifier
                    .size(12.dp)
                    .scale(scale)
                    .clip(CircleShape)
                    .border(
                        width = 1.dp,
                        color = Color.Black.copy(alpha = 0.3f),
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
fun AnimatedErrorMessage(error: String) {
    var isVisible by remember { mutableStateOf(false) }

    LaunchedEffect(error) {
        isVisible = true
        delay(4000)
        isVisible = false
    }

    androidx.compose.animation.AnimatedVisibility(
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
                modifier = Modifier.padding(16.dp)
            )
        }
    }
}

@Composable
fun ModernBiometricButton(
    onClick: () -> Unit,
    enabled: Boolean
) {
    val scale by animateFloatAsState(
        targetValue = if (enabled) 1f else 0.95f,
        animationSpec = spring()
    )

    Card(
        onClick = onClick,
        enabled = enabled,
        modifier = Modifier
            .fillMaxWidth()
            .scale(scale),
        colors = CardDefaults.cardColors(
            containerColor = Color.White
        ),
        shape = RoundedCornerShape(16.dp),
        border = BorderStroke(1.dp, Color.Black.copy(alpha = 0.2f)),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = Icons.Default.Fingerprint,
                contentDescription = "Biometric Authentication",
                modifier = Modifier.size(24.dp),
                tint = Color.Black.copy(alpha = 0.8f)
            )
            Spacer(modifier = Modifier.width(12.dp))
            Text(
                text = "Use Biometric",
                style = MaterialTheme.typography.titleMedium,
                color = Color.Black,
                fontWeight = FontWeight.Medium
            )
        }
    }
}

@Composable
fun ModernLoadingOverlay() {
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
                    text = "Authenticating...",
                    color = Color.Black,
                    style = MaterialTheme.typography.bodyLarge,
                    fontWeight = FontWeight.Medium
                )
            }
        }
    }
}

@Composable
fun ModernPinKeypad(
    onNumberClick: (String) -> Unit,
    onBackspaceClick: () -> Unit,
    enabled: Boolean = true
) {
    Column(
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Row 1: 1, 2, 3
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            ModernKeypadButton("1", onNumberClick, enabled)
            ModernKeypadButton("2", onNumberClick, enabled)
            ModernKeypadButton("3", onNumberClick, enabled)
        }

        // Row 2: 4, 5, 6
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            ModernKeypadButton("4", onNumberClick, enabled)
            ModernKeypadButton("5", onNumberClick, enabled)
            ModernKeypadButton("6", onNumberClick, enabled)
        }

        // Row 3: 7, 8, 9
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            ModernKeypadButton("7", onNumberClick, enabled)
            ModernKeypadButton("8", onNumberClick, enabled)
            ModernKeypadButton("9", onNumberClick, enabled)
        }

        // Row 4: Empty, 0, Backspace
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            Spacer(modifier = Modifier.size(64.dp))

            ModernKeypadButton("0", onNumberClick, enabled)

            ModernBackspaceButton(onBackspaceClick, enabled)
        }
    }
}

@Composable
fun ModernKeypadButton(
    text: String,
    onNumberClick: (String) -> Unit,
    enabled: Boolean
) {
    val scale by animateFloatAsState(
        targetValue = 1f,
        animationSpec = spring()
    )

    var isPressed by remember { mutableStateOf(false) }
    val buttonScale by animateFloatAsState(
        targetValue = if (isPressed) 0.95f else 1f,
        animationSpec = spring(stiffness = Spring.StiffnessHigh)
    )

    Box(
        modifier = Modifier
            .size(64.dp)
            .scale(buttonScale)
            .clip(CircleShape)
            .background(
                if (enabled) Color.White
                else Color.White.copy(alpha = 0.5f)
            )
            .border(
                width = 1.dp,
                color = if (enabled) Color.Black.copy(alpha = 0.2f)
                       else Color.Black.copy(alpha = 0.1f),
                shape = CircleShape
            )
            .clickable(enabled = enabled) {
                isPressed = true
                onNumberClick(text)
            },
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = text,
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.Normal,
            color = if (enabled) Color.Black else Color.Black.copy(alpha = 0.3f)
        )
    }

    LaunchedEffect(isPressed) {
        if (isPressed) {
            delay(100)
            isPressed = false
        }
    }
}

@Composable
fun ModernBackspaceButton(
    onBackspaceClick: () -> Unit,
    enabled: Boolean
) {
    var isPressed by remember { mutableStateOf(false) }
    val buttonScale by animateFloatAsState(
        targetValue = if (isPressed) 0.95f else 1f,
        animationSpec = spring(stiffness = Spring.StiffnessHigh)
    )

    Box(
        modifier = Modifier
            .size(64.dp)
            .scale(buttonScale)
            .clip(CircleShape)
            .background(
                if (enabled) Color.White
                else Color.White.copy(alpha = 0.5f)
            )
            .border(
                width = 1.dp,
                color = if (enabled) Color.Black.copy(alpha = 0.2f)
                       else Color.Black.copy(alpha = 0.1f),
                shape = CircleShape
            )
            .clickable(enabled = enabled) {
                isPressed = true
                onBackspaceClick()
            },
        contentAlignment = Alignment.Center
    ) {
        Icon(
            imageVector = Icons.AutoMirrored.Filled.Backspace,
            contentDescription = "Delete",
            tint = if (enabled) Color.Black else Color.Black.copy(alpha = 0.3f),
            modifier = Modifier.size(20.dp)
        )
    }

    LaunchedEffect(isPressed) {
        if (isPressed) {
            delay(100)
            isPressed = false
        }
    }
}
