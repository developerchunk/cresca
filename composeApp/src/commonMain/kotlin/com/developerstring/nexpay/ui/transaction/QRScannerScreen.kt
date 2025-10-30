package com.developerstring.nexpay.ui.transaction

import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.FlashOff
import androidx.compose.material.icons.filled.FlashOn
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.developerstring.nexpay.ui.theme.AppColors
import com.developerstring.nexpay.ui.theme.AppColors.AppColorVariants
import com.developerstring.nexpay.viewmodel.SharedViewModel
import kotlinx.serialization.Serializable
import qrscanner.QrScanner

@Serializable
object QRScannerScreenRoute

@Composable
fun QRScannerScreen(
    navController: NavController,
    @Suppress("UNUSED_PARAMETER")
    sharedViewModel: SharedViewModel,
) {
    var flashlightOn by remember { mutableStateOf(false) }
    var scanError by remember { mutableStateOf<String?>(null) }

    // Animated pulse effect for the scanner border
    val infiniteTransition = rememberInfiniteTransition()
    val pulseAlpha by infiniteTransition.animateFloat(
        initialValue = 0.3f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(1500, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        )
    )

    val scannerScale by infiniteTransition.animateFloat(
        initialValue = 1f,
        targetValue = 1.02f,
        animationSpec = infiniteRepeatable(
            animation = tween(2000, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        )
    )

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Top Bar with back button
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 16.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(
                    onClick = { navController.popBackStack() },
                    modifier = Modifier
                        .size(48.dp)
                        .clip(CircleShape)
                        .background(AppColorVariants.primaryBlueAlpha10)
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "Back",
                        tint = AppColors.primaryBlue,
                        modifier = Modifier.size(24.dp)
                    )
                }

                IconButton(
                    onClick = { flashlightOn = !flashlightOn },
                    modifier = Modifier
                        .size(48.dp)
                        .clip(CircleShape)
                        .background(
                            if (flashlightOn) AppColorVariants.brightCyanAlpha30
                            else AppColorVariants.primaryBlueAlpha10
                        )
                ) {
                    Icon(
                        imageVector = if (flashlightOn) Icons.Default.FlashOn else Icons.Default.FlashOff,
                        contentDescription = "Toggle Flash",
                        tint = if (flashlightOn) AppColors.brightCyan else AppColors.primaryBlue,
                        modifier = Modifier.size(24.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(32.dp))

            // Title and Instructions
            Text(
                text = "Scan QR Code",
                fontSize = 32.sp,
                fontWeight = FontWeight.Bold,
                color = AppColors.primaryBlue,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(12.dp))

            Text(
                text = "Position the QR code within the frame to scan",
                fontSize = 16.sp,
                fontWeight = FontWeight.Normal,
                color = AppColors.primaryBlue.copy(alpha = 0.6f),
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(horizontal = 16.dp)
            )

            Spacer(modifier = Modifier.height(48.dp))

            // QR Scanner with animated border
            Box(
                modifier = Modifier
                    .size(280.dp)
                    .scale(scannerScale),
                contentAlignment = Alignment.Center
            ) {
                // Outer glowing border
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .border(
                            width = 3.dp,
                            brush = Brush.linearGradient(
                                colors = listOf(
                                    AppColors.primaryBlue.copy(alpha = pulseAlpha),
                                    AppColors.brightCyan.copy(alpha = pulseAlpha * 0.8f)
                                )
                            ),
                            shape = RoundedCornerShape(24.dp)
                        )
                )

                // Scanner
                QrScanner(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(8.dp)
                        .clip(RoundedCornerShape(20.dp)),
                    flashlightOn = flashlightOn,
                    onCompletion = { walletAddress ->
                        navController.navigate(AddTransactionScreenRoute(walletAddress))
                    },
                    onFailure = { _ ->
                        scanError = "Failed to scan QR code"
                    },
                    onGalleryCallBackHandler = {},
                    launchGallery = false
                )
            }

            Spacer(modifier = Modifier.height(32.dp))

            // Corner guides for better UX
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Center
            ) {
                Card(
                    modifier = Modifier
                        .padding(horizontal = 8.dp),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = AppColorVariants.primaryBlueAlpha10
                    ),
                    elevation = CardDefaults.cardElevation(0.dp)
                ) {
                    Text(
                        text = "Align QR code within frame",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Medium,
                        color = AppColors.primaryBlue,
                        modifier = Modifier.padding(horizontal = 20.dp, vertical = 12.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.weight(1f))

            // Error message
            scanError?.let { error ->
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 16.dp),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = Color(0xFFFFEBEE)
                    )
                ) {
                    Text(
                        text = error,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Medium,
                        color = Color(0xFFD32F2F),
                        textAlign = TextAlign.Center,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp)
                    )
                }
            }

            // Bottom hint
            Text(
                text = "Make sure the QR code is clear and well-lit",
                fontSize = 14.sp,
                fontWeight = FontWeight.Normal,
                color = AppColors.primaryBlue.copy(alpha = 0.5f),
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(bottom = 24.dp)
            )
        }
    }
}