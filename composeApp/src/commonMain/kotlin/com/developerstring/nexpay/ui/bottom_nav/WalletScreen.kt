package com.developerstring.nexpay.ui.bottom_nav

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import com.developerstring.nexpay.viewmodel.AptosViewModel
import com.developerstring.nexpay.viewmodel.SharedViewModel
import kotlinx.serialization.Serializable

@Serializable
data object WalletScreenRoute

@Composable
fun WalletScreen(
    sharedViewModel: SharedViewModel,
    aptosViewModel: AptosViewModel,
    navController: NavController,
) {
    val uiState by aptosViewModel.uiState.collectAsStateWithLifecycle()
    val clipboardManager = LocalClipboardManager.current
    val coroutineScope = rememberCoroutineScope()
    var selectedWallet by remember { mutableStateOf("Wallet 1") }
    var showCopiedMessage by remember { mutableStateOf(false) }

    // Reset copied message after delay
    LaunchedEffect(showCopiedMessage) {
        if (showCopiedMessage) {
            kotlinx.coroutines.delay(2000)
            showCopiedMessage = false
        }
    }

    // Sample data for "What's new" section
    val newTokens = remember {
        listOf(
            TokenItem("Bitcoin", Color.Black, "BTC"),
            TokenItem("First Digital", Color.White, "1"),
            TokenItem("Ethereum", Color.Yellow, "ETH")
        )
    }

    Column(
        modifier = Modifier
            .verticalScroll(rememberScrollState())
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    colors = listOf(
                        Color(0xFF6B46C1), // Purple
                        Color(0xFF7C3AED), // Darker purple
                        Color(0xFF5B21B6)  // Even darker purple
                    )
                )
            )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp)
        ) {
            Spacer(modifier = Modifier.height(40.dp))

            // QR Code Scanner Icon
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Start
            ) {
                IconButton(
                    onClick = { /* Handle QR scan */ },
                    modifier = Modifier
                        .size(40.dp)
                        .clip(RoundedCornerShape(8.dp))
                        .background(Color.White.copy(alpha = 0.1f))
                ) {
                    Icon(
                        imageVector = Icons.Default.QrCodeScanner,
                        contentDescription = "QR Scanner",
                        tint = Color.White,
                        modifier = Modifier.size(20.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(30.dp))

            // Wallet Selector with Triangle Icon
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Default.Warning,
                    contentDescription = "Wallet Icon",
                    tint = Color.White,
                    modifier = Modifier.size(24.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = selectedWallet,
                    color = Color.White,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Medium
                )
                Spacer(modifier = Modifier.width(8.dp))
                Icon(
                    imageVector = Icons.Default.KeyboardArrowDown,
                    contentDescription = "Dropdown",
                    tint = Color.White,
                    modifier = Modifier.size(20.dp)
                )
            }

            Spacer(modifier = Modifier.height(40.dp))

            // Balance Display
            Text(
                text = if (uiState.isConnected) uiState.balance else "$0",
                color = Color.White,
                fontSize = 42.sp,
                fontWeight = FontWeight.Light,
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth()
            )

            // Error Message Display
            uiState.error?.let { errorMessage ->
                Spacer(modifier = Modifier.height(16.dp))
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(
                        containerColor = Color.Red.copy(alpha = 0.1f)
                    ),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text(
                        text = errorMessage,
                        color = Color.Red,
                        fontSize = 14.sp,
                        modifier = Modifier.padding(16.dp),
                        textAlign = TextAlign.Center
                    )
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            // Action Buttons Row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp, Alignment.CenterHorizontally),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Copy Address Button
                Button(
                    onClick = {
                        uiState.walletAddress?.let { address ->
                            clipboardManager.setText(AnnotatedString(address))
                            showCopiedMessage = true
                        }
                    },
                    modifier = Modifier.height(48.dp),
                    enabled = uiState.isConnected,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = if (showCopiedMessage) {
                            Color.Green.copy(alpha = 0.2f)
                        } else {
                            Color.White.copy(alpha = if (uiState.isConnected) 0.15f else 0.05f)
                        }
                    ),
                    shape = RoundedCornerShape(24.dp)
                ) {
                    Text(
                        text = if (showCopiedMessage) "Copied!" else "Copy address",
                        color = if (showCopiedMessage) {
                            Color.Green
                        } else {
                            Color.White.copy(alpha = if (uiState.isConnected) 1f else 0.5f)
                        },
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Medium
                    )
                }

                // Buy Button
                Button(
                    onClick = { /* Handle buy */ },
                    modifier = Modifier.height(48.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color.White.copy(alpha = 0.15f)
                    ),
                    shape = RoundedCornerShape(24.dp)
                ) {
                    Text(
                        text = "Buy",
                        color = Color.White,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Medium
                    )
                }

                // Refresh Balance Button (visible when connected)
                if (uiState.isConnected) {
                    IconButton(
                        onClick = { aptosViewModel.refreshBalance() },
                        modifier = Modifier
                            .size(48.dp)
                            .clip(CircleShape)
                            .background(Color.White.copy(alpha = 0.15f))
                    ) {
                        Icon(
                            imageVector = Icons.Default.Refresh,
                            contentDescription = "Refresh Balance",
                            tint = Color.White,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                }

                // More Options Button
                IconButton(
                    onClick = { /* Handle more options */ },
                    modifier = Modifier
                        .size(48.dp)
                        .clip(CircleShape)
                        .background(Color.White.copy(alpha = 0.15f))
                ) {
                    Icon(
                        imageVector = Icons.Default.MoreVert,
                        contentDescription = "More Options",
                        tint = Color.White,
                        modifier = Modifier.size(20.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(40.dp))

            // Connect Wallet Button or Connected Status
            if (uiState.isConnected) {
                // Connected state - show wallet info without disconnect option
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = Color.Black.copy(alpha = 0.8f)),
                    shape = RoundedCornerShape(24.dp)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(20.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = "Wallet Connected",
                                color = Color.White,
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Medium
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = uiState.walletAddress?.let {
                                    "${it.take(8)}...${it.takeLast(8)}"
                                } ?: "No address",
                                color = Color.White.copy(alpha = 0.7f),
                                fontSize = 12.sp
                            )
                        }
                        // Success icon instead of disconnect button
                        Icon(
                            imageVector = Icons.Default.CheckCircle,
                            contentDescription = "Connected",
                            tint = Color.White,
                            modifier = Modifier.size(24.dp)
                        )
                    }
                }
            } else {
                // Disconnected state - show connect button
                Button(
                    onClick = { aptosViewModel.connectWallet() },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp),
                    enabled = !uiState.isLoading,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color.Black.copy(alpha = 0.8f),
                        disabledContainerColor = Color.Black.copy(alpha = 0.5f)
                    ),
                    shape = RoundedCornerShape(24.dp)
                ) {
                    if (uiState.isLoading) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.Center
                        ) {
                            CircularProgressIndicator(
                                modifier = Modifier.size(20.dp),
                                color = Color.White,
                                strokeWidth = 2.dp
                            )
                            Spacer(modifier = Modifier.width(12.dp))
                            Text(
                                text = "Connecting...",
                                color = Color.White,
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Medium
                            )
                        }
                    } else {
                        Text(
                            text = "Connect Wallet",
                            color = Color.White,
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Medium
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(30.dp))
        }

        // Get Started Section - Fixed at bottom with white background
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp))
                .background(Color.White)
                .padding(20.dp)
        ) {
            // Get Started Header
            Text(
                text = "Get started",
                color = Color.Black,
                fontSize = 24.sp,
                fontWeight = FontWeight.SemiBold,
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "Start by buying crypto or receiving tokens.",
                color = Color.Gray,
                fontSize = 16.sp,
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(24.dp))

            // Receive Button
            Button(
                onClick = { /* Handle receive */ },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color.Black
                ),
                shape = RoundedCornerShape(28.dp)
            ) {
                Text(
                    text = "Receive",
                    color = Color.White,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Medium
                )
            }

            Spacer(modifier = Modifier.height(32.dp))

            // What's New Section
            Text(
                text = "What's new",
                color = Color.Black,
                fontSize = 18.sp,
                fontWeight = FontWeight.SemiBold,
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Token Cards Row
            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(16.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                items(newTokens) { token ->
                    TokenCard(token = token)
                }
            }

            Spacer(modifier = Modifier.height(100.dp))
        }
    }
}

data class TokenItem(
    val name: String,
    val backgroundColor: Color,
    val symbol: String
)

@Composable
fun TokenCard(token: TokenItem) {
    Box(
        modifier = Modifier
            .size(80.dp)
            .clip(CircleShape)
            .border(2.dp, Color(0xFF3B82F6), CircleShape)
            .background(token.backgroundColor),
        contentAlignment = Alignment.Center
    ) {
        when (token.symbol) {
            "BTC" -> {
                // Bitcoin pattern with dots
                Canvas(modifier = Modifier.fillMaxSize()) {
                    val dotSize = 3.dp.toPx()
                    val spacing = 8.dp.toPx()
                    for (x in 0 until (size.width / spacing).toInt()) {
                        for (y in 0 until (size.height / spacing).toInt()) {
                            if ((x + y) % 2 == 0) {
                                drawCircle(
                                    color = Color.White,
                                    radius = dotSize / 2,
                                    center = androidx.compose.ui.geometry.Offset(
                                        x * spacing + spacing / 2,
                                        y * spacing + spacing / 2
                                    )
                                )
                            }
                        }
                    }
                }
            }
            "1" -> {
                Text(
                    text = "1\"",
                    color = Color.Black,
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold
                )
            }
            "ETH" -> {
                // Ethereum pattern with dots
                Canvas(modifier = Modifier.fillMaxSize()) {
                    val dotSize = 2.dp.toPx()
                    val spacing = 6.dp.toPx()
                    for (x in 0 until (size.width / spacing).toInt()) {
                        for (y in 0 until (size.height / spacing).toInt()) {
                            drawCircle(
                                color = Color.Black,
                                radius = dotSize / 2,
                                center = androidx.compose.ui.geometry.Offset(
                                    x * spacing + spacing / 2,
                                    y * spacing + spacing / 2
                                )
                            )
                        }
                    }
                }
            }
        }

        // Blue star in corner
        Box(
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .offset(x = 8.dp, y = 8.dp)
                .size(20.dp)
                .clip(CircleShape)
                .background(Color(0xFF3B82F6)),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = Icons.Default.Star,
                contentDescription = "Star",
                tint = Color.White,
                modifier = Modifier.size(12.dp)
            )
        }
    }
}