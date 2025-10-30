package com.developerstring.nexpay.ui.transaction

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.tween
import kotlinx.coroutines.async
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.blur
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Color.Companion.Transparent
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import com.developerstring.nexpay.utils.copyToClipboard
import com.developerstring.nexpay.viewmodel.AptosViewModel
import com.developerstring.nexpay.viewmodel.SharedViewModel
import io.github.alexzhirkevich.qrose.options.QrBallShape
import io.github.alexzhirkevich.qrose.options.QrBrush
import io.github.alexzhirkevich.qrose.options.QrColors
import io.github.alexzhirkevich.qrose.options.QrFrameShape
import io.github.alexzhirkevich.qrose.options.QrPixelShape
import io.github.alexzhirkevich.qrose.options.QrShapes
import io.github.alexzhirkevich.qrose.options.roundCorners
import io.github.alexzhirkevich.qrose.options.solid
import io.github.alexzhirkevich.qrose.rememberQrCodePainter
import kotlinx.serialization.Serializable

@Serializable
object ReceivePaymentScreenRoute

@Composable
fun ReceivePaymentScreen(
    sharedViewModel: SharedViewModel,
    aptosViewModel: AptosViewModel,
    navController: NavController
) {
    val aptosUiState by aptosViewModel.uiState.collectAsStateWithLifecycle()

    val lightVibrantColor by sharedViewModel.lightVibrantColor
    val darkVibrantColor by sharedViewModel.darkVibrantColor
    val vibrantColor by sharedViewModel.vibrantColor

    // Animation states
    val scaleAnimation = remember { Animatable(0.1f) }
    val offsetAnimation = remember { Animatable(-800f) }
    val density = LocalDensity.current

    // Launch animation when wallet is connected
    LaunchedEffect(aptosUiState.isConnected) {
        if (aptosUiState.isConnected && aptosUiState.walletAddress != null) {
            // Start both animations simultaneously
            val scaleJob = async {
                scaleAnimation.animateTo(
                    targetValue = 1f,
                    animationSpec = tween(
                        durationMillis = 1200,
                        easing = FastOutSlowInEasing
                    )
                )
            }
            val offsetJob = async {
                offsetAnimation.animateTo(
                    targetValue = 0f,
                    animationSpec = tween(
                        durationMillis = 1200,
                        easing = FastOutSlowInEasing
                    )
                )
            }
            scaleJob.await()
            offsetJob.await()
        } else {
            scaleAnimation.snapTo(0.1f)
            offsetAnimation.snapTo(-800f)
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
                    onClick = { navController.popBackStack() },
                    colors = IconButtonDefaults.iconButtonColors(containerColor = Transparent),
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
                    text = "Receive Payment",
                    fontSize = 22.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.Black
                )

                Spacer(modifier = Modifier.weight(1f))

                // Empty space for centering
                Box(modifier = Modifier.size(48.dp))
            }

            // QR Code section
            if (aptosUiState.isConnected && aptosUiState.walletAddress != null) {

                Spacer(modifier = Modifier.height(20.dp))

                // Animated QR Code Card
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 20.dp)
                        .scale(scaleAnimation.value)
                        .offset {
                            IntOffset(
                                x = 0,
                                y = with(density) { offsetAnimation.value.dp.roundToPx() }
                            )
                        },
                    shape = RoundedCornerShape(20.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White.copy(alpha = 0.3f)),
//                    elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(32.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        // Title
                        Text(
                            text = "Scan QR Code",
                            fontSize = 20.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = Color.Black,
                            textAlign = TextAlign.Center,
                            modifier = Modifier.padding(bottom = 8.dp)
                        )

                        Text(
                            text = "to send payment",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Medium,
                            color = Color.Black.copy(alpha = 0.7f),
                            textAlign = TextAlign.Center,
                            modifier = Modifier.padding(bottom = 32.dp)
                        )

                        // QR Code
                        Box(
                            modifier = Modifier
                                .size(240.dp)
                                .background(
                                    Color.White,
                                    RoundedCornerShape(16.dp)
                                )
                                .padding(16.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            val qrcodePainter = rememberQrCodePainter(
                                data = aptosUiState.walletAddress ?: "",
                                shapes = QrShapes(
                                    darkPixel = QrPixelShape.roundCorners(),
                                    lightPixel = QrPixelShape.roundCorners(),
                                    frame = QrFrameShape.roundCorners(50f),
                                    ball = QrBallShape.roundCorners(100f)
                                ),
                                colors = QrColors(
                                    dark = QrBrush.solid(darkVibrantColor),
                                    ball = QrBrush.solid(darkVibrantColor),
                                )
                            )

                            Image(
                                painter = qrcodePainter,
                                contentDescription = "QR code for wallet address",
                                modifier = Modifier.fillMaxSize()
                            )
                        }

                        Spacer(modifier = Modifier.height(32.dp))

                        // Compressed wallet address
                        val walletAddress = aptosUiState.walletAddress ?: ""

                        // Wallet Address Section
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            modifier = Modifier
                                .drawBehind {
                                    drawRoundRect(
                                        color = darkVibrantColor,
                                        style = Stroke(
                                            width = 1.dp.toPx(),
                                            pathEffect = PathEffect.dashPathEffect(floatArrayOf(10f, 10f), 0f)
                                        ),
                                        cornerRadius = CornerRadius(12.dp.toPx())
                                    )
                                }.padding(horizontal = 10.dp, vertical = 10.dp).clickable(
                                    onClick = {
                                        copyToClipboard(walletAddress)
                                    }
                                ),
                        ) {
                            Text(
                                text = "Wallet Address",
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Medium,
                                color = Color.Black.copy(alpha = 0.6f),
                                modifier = Modifier.padding(bottom = 8.dp)
                            )

                                Text(
                                    text = walletAddress,
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Medium,
                                    fontFamily = FontFamily.Monospace,
                                    color = Color.Black,
                                )
                        }
                    }
                }

            } else {
                // No wallet connected state
                Spacer(modifier = Modifier.weight(1f))

                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier.padding(32.dp)
                ) {
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        shape = RoundedCornerShape(20.dp),
                        colors = CardDefaults.cardColors(containerColor = Color.White.copy(alpha = 0.9f)),
                        elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
                    ) {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            modifier = Modifier.padding(40.dp)
                        ) {
                            Text(
                                text = "No Wallet Connected",
                                fontSize = 20.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = Color.Black,
                                textAlign = TextAlign.Center
                            )

                            Spacer(modifier = Modifier.height(16.dp))

                            Text(
                                text = "Connect your wallet to generate a QR code for receiving payments",
                                fontSize = 14.sp,
                                color = Color.Black.copy(alpha = 0.7f),
                                textAlign = TextAlign.Center,
                                lineHeight = 20.sp
                            )

//                            Spacer(modifier = Modifier.height(32.dp))
//
//                            Button(
//                                onClick = { aptosViewModel.connectWallet() },
//                                modifier = Modifier.fillMaxWidth(),
//                                colors = ButtonDefaults.buttonColors(
//                                    containerColor = Color.Black,
//                                    contentColor = Color.White
//                                ),
//                                shape = RoundedCornerShape(12.dp)
//                            ) {
//                                Text(
//                                    text = "Connect Wallet",
//                                    fontSize = 16.sp,
//                                    fontWeight = FontWeight.Medium,
//                                    modifier = Modifier.padding(vertical = 4.dp)
//                                )
//                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.weight(1f))
            }
        }
    }
}