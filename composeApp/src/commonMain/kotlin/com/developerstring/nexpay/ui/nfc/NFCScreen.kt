package com.developerstring.nexpay.ui.nfc

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.rounded.Nfc
import androidx.compose.material.icons.rounded.QrCode
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.blur
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import com.developerstring.nexpay.utils.copyToClipboard
import com.developerstring.nexpay.viewmodel.AptosViewModel
import com.developerstring.nexpay.viewmodel.SharedViewModel
import kotlinx.serialization.Serializable

@Composable
fun NFCScreen(
    sharedViewModel: SharedViewModel,
    aptosViewModel: AptosViewModel,
    navController: NavController,
    viewModel: NFCViewModel = viewModel(),
    onNavigateToSend: (String) -> Unit = {},
    onNavigateToAddTransaction: (String) -> Unit = {}
) {

    val currentAptosState by aptosViewModel.uiState.collectAsStateWithLifecycle()
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    val lightVibrantColor by sharedViewModel.lightVibrantColor
    val darkVibrantColor by sharedViewModel.darkVibrantColor
    val vibrantColor by sharedViewModel.vibrantColor

    // Animation states
    val scaleAnimation = remember { Animatable(0.8f) }

    // Launch animation when screen loads
    LaunchedEffect(Unit) {
        viewModel.setWalletAddress(currentAptosState.walletAddress.toString())
        viewModel.refreshNFCStatus()

        scaleAnimation.animateTo(
            targetValue = 1f,
            animationSpec = tween(
                durationMillis = 800,
                easing = FastOutSlowInEasing
            )
        )
    }

    // Handle received wallet address from NFC tap
    LaunchedEffect(uiState.receivedWalletAddress) {
        uiState.receivedWalletAddress?.let { walletAddress ->
            onNavigateToAddTransaction(walletAddress)
            viewModel.clearReceivedAddress()
        }
    }

    // Animated background colors
    val animatedColor1 by animateColorAsState(
        targetValue = vibrantColor,
        animationSpec = tween(800, easing = FastOutSlowInEasing),
        label = "backgroundColor1"
    )

    val animatedColor2 by animateColorAsState(
        targetValue = lightVibrantColor,
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
                .padding(top = 40.dp, start = 16.dp, end = 16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Top app bar
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 40.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(
                    onClick = { navController.popBackStack() }
                ) {
                    Icon(
                        Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "Back",
                        tint = Color.Black,
                        modifier = Modifier.size(28.dp)
                    )
                }

                Spacer(modifier = Modifier.weight(1f))

                Text(
                    text = "NFC Tap to Pay",
                    fontSize = 22.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.Black
                )

                Spacer(modifier = Modifier.weight(1f))

                // Empty space for centering
                Box(modifier = Modifier.size(48.dp))
            }

            Spacer(modifier = Modifier.height(20.dp))

            // NFC Status and Main Content
            if (uiState.isNFCAvailable && uiState.isNFCEnabled) {

                // Main NFC Card
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 20.dp)
                        .scale(scaleAnimation.value),
                    shape = RoundedCornerShape(20.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White.copy(alpha = 0.4f)),
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(32.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        // NFC Icon with pulse animation
                        NFCPulseIcon(darkVibrantColor)

                        Spacer(modifier = Modifier.height(24.dp))

                        Text(
                            text = "Ready for NFC",
                            fontSize = 20.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = darkVibrantColor,
                            textAlign = TextAlign.Center
                        )

                        Text(
                            text = "Hold devices close to exchange payments",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Medium,
                            color = darkVibrantColor.copy(alpha = 0.7f),
                            textAlign = TextAlign.Center,
                            modifier = Modifier.padding(top = 8.dp, bottom = 32.dp)
                        )

                        // Clean Buttons
                        Column(
                            modifier = Modifier.fillMaxWidth(),
                            verticalArrangement = Arrangement.spacedBy(16.dp)
                        ) {
                            // Tap to Pay Button
                            Button(
                                onClick = { viewModel.startReading() },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(56.dp),
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = darkVibrantColor.copy(alpha = 0.3f),
                                    contentColor = Color.White
                                ),
                                shape = RoundedCornerShape(100)
                            ) {
                                Row(
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Text(
                                        text = "Tap to Pay",
                                        fontSize = 16.sp,
                                        fontWeight = FontWeight.Medium,
                                        modifier = Modifier.padding(top = 4.dp)
                                    )
                                    Icon(
                                        Icons.Rounded.Nfc,
                                        contentDescription = "Tap to Pay",
                                        modifier = Modifier.size(26.dp)
                                    )
                                }
                            }

                            // Receive Button
                            Button(
                                onClick = { viewModel.startSharing() },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(56.dp),
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = darkVibrantColor.copy(alpha = 0.3f),
                                    contentColor = Color.White
                                ),
                                shape = RoundedCornerShape(100)
                            ) {
                                Row(
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Text(
                                        text = "Receive",
                                        fontSize = 16.sp,
                                        fontWeight = FontWeight.Medium,
                                        modifier = Modifier.padding(top = 4.dp)
                                    )

                                    Icon(
                                        Icons.Rounded.QrCode,
                                        contentDescription = "Receive",
                                        modifier = Modifier.size(26.dp)
                                    )
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(24.dp))

                        // Wallet Address Section
                        if (uiState.currentWalletAddress.isNotEmpty()) {
                            Column(
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Text(
                                    text = "Your Wallet Address",
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.Medium,
                                    color = Color.Black.copy(alpha = 0.6f),
                                    modifier = Modifier.padding(bottom = 8.dp)
                                )

                                // Compressed wallet address
                                val walletAddress = uiState.currentWalletAddress
                                val compressedAddress = if (walletAddress.length > 16) {
                                    "${walletAddress.take(8)}...${walletAddress.takeLast(8)}"
                                } else {
                                    walletAddress
                                }

                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .background(
                                            Color.Black.copy(alpha = 0.05f),
                                            RoundedCornerShape(12.dp)
                                        )
                                        .padding(10.dp)
                                        .clickable(
                                            onClick = {
                                                copyToClipboard(walletAddress)
                                            }
                                        ),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.Center
                                ) {
                                    Text(
                                        text = compressedAddress,
                                        fontSize = 14.sp,
                                        fontWeight = FontWeight.Medium,
                                        fontFamily = FontFamily.Monospace,
                                        color = Color.Black,
                                    )
                                }
                            }
                        }
                    }
                }

                // Received Address Card
                uiState.receivedWalletAddress?.let { address ->
                    Spacer(modifier = Modifier.height(20.dp))

                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 20.dp),
                        shape = RoundedCornerShape(20.dp),
                        colors = CardDefaults.cardColors(containerColor = lightVibrantColor.copy(alpha = 0.9f)),
                        elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(24.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Icon(
                                        Icons.Default.CheckCircle,
                                        contentDescription = null,
                                        tint = Color.Green,
                                        modifier = Modifier.size(24.dp)
                                    )
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text(
                                        text = "Address Received!",
                                        fontSize = 16.sp,
                                        fontWeight = FontWeight.SemiBold,
                                        color = Color.Black
                                    )
                                }

                                IconButton(
                                    onClick = { viewModel.clearReceivedAddress() }
                                ) {
                                    Icon(
                                        Icons.Default.Close,
                                        contentDescription = "Dismiss",
                                        tint = Color.Black
                                    )
                                }
                            }

                            Spacer(modifier = Modifier.height(16.dp))

                            Button(
                                onClick = { onNavigateToSend(address) },
                                modifier = Modifier.fillMaxWidth(),
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = Color.Black,
                                    contentColor = Color.White
                                ),
                                shape = RoundedCornerShape(12.dp)
                            ) {
                                Icon(
                                    Icons.AutoMirrored.Filled.Send,
                                    contentDescription = null,
                                    modifier = Modifier.size(18.dp)
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = "Send Payment",
                                    fontSize = 16.sp,
                                    fontWeight = FontWeight.Medium
                                )
                            }
                        }
                    }
                }

            } else {
                // NFC Not Available/Disabled state
                Spacer(modifier = Modifier.weight(1f))

                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(20.dp),
                    shape = RoundedCornerShape(20.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White.copy(alpha = 0.9f)),
                    elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier.padding(40.dp)
                    ) {
                        Icon(
                            imageVector = if (!uiState.isNFCAvailable) Icons.Default.Error else Icons.Default.Warning,
                            contentDescription = null,
                            tint = if (!uiState.isNFCAvailable) Color.Red else Color(0xFFFF9800),
                            modifier = Modifier.size(48.dp)
                        )

                        Spacer(modifier = Modifier.height(16.dp))

                        Text(
                            text = if (!uiState.isNFCAvailable) "NFC Not Available" else "NFC Disabled",
                            fontSize = 20.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = Color.Black,
                            textAlign = TextAlign.Center
                        )

                        Spacer(modifier = Modifier.height(12.dp))

                        Text(
                            text = if (!uiState.isNFCAvailable)
                                "This device doesn't support NFC functionality"
                            else
                                "Please enable NFC in your device settings to use tap to pay",
                            fontSize = 14.sp,
                            color = Color.Black.copy(alpha = 0.7f),
                            textAlign = TextAlign.Center,
                            lineHeight = 20.sp
                        )

                        if (uiState.isNFCAvailable && !uiState.isNFCEnabled) {
                            Spacer(modifier = Modifier.height(24.dp))

                            Button(
                                onClick = { viewModel.refreshNFCStatus() },
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = darkVibrantColor,
                                    contentColor = Color.White
                                ),
                                shape = RoundedCornerShape(12.dp)
                            ) {
                                Icon(
                                    Icons.Default.Refresh,
                                    contentDescription = null,
                                    modifier = Modifier.size(18.dp)
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = "Check Again",
                                    fontSize = 16.sp,
                                    fontWeight = FontWeight.Medium
                                )
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.weight(1f))
            }
        }
    }

    // NFC Reading Dialog
    NFCReadingDialog(
        isVisible = uiState.isReading,
        onDismiss = { viewModel.stopReading() },
        onCancelReading = { viewModel.stopReading() }
    )

    // NFC Sharing Dialog
    NFCSharingDialog(
        isVisible = uiState.isSharing,
        walletAddress = uiState.currentWalletAddress,
        onDismiss = { viewModel.stopSharing() },
        onStopSharing = { viewModel.stopSharing() }
    )

    // Loading Indicator
    if (uiState.isLoading) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            CircularProgressIndicator(color = darkVibrantColor)
        }
    }
}

@Composable
private fun NFCPulseIcon(
    color: Color
) {
    val infiniteTransition = rememberInfiniteTransition(label = "nfc_pulse")

    val animatedScale by infiniteTransition.animateFloat(
        initialValue = 1f,
        targetValue = 1.1f,
        animationSpec = infiniteRepeatable(
            animation = tween(1500, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "scale_animation"
    )

    val animatedAlpha by infiniteTransition.animateFloat(
        initialValue = 0.7f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(1500, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "alpha_animation"
    )

    Box(
        modifier = Modifier
            .size(80.dp)
            .scale(animatedScale)
            .background(
                color.copy(alpha = animatedAlpha * 0.2f),
                RoundedCornerShape(20.dp)
            ),
        contentAlignment = Alignment.Center
    ) {
        Icon(
            Icons.Default.Nfc,
            contentDescription = "NFC",
            modifier = Modifier.size(40.dp),
            tint = color.copy(alpha = animatedAlpha)
        )
    }
}

// Navigation route for NFCScreen
@Serializable
data object NFCScreenRoute
