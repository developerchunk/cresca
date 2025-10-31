package com.developerstring.nexpay.ui.bottom_nav

import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.rounded.RotateLeft
import androidx.compose.material.icons.automirrored.rounded.Send
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.rounded.Add
import androidx.compose.material.icons.rounded.ChevronRight
import androidx.compose.material.icons.rounded.ContentCopy
import androidx.compose.material.icons.rounded.Key
import androidx.compose.material.icons.rounded.Nfc
import androidx.compose.material.icons.rounded.QrCode
import androidx.compose.material.icons.rounded.QrCodeScanner
import androidx.compose.material.icons.rounded.Rotate90DegreesCw
import androidx.compose.material.icons.rounded.RotateLeft
import androidx.compose.material.icons.rounded.SwapHoriz
import androidx.compose.material3.*
import androidx.compose.runtime.*

import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.blur
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.TextStyle

import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlin.math.*

import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import com.developerstring.nexpay.Constants
import com.developerstring.nexpay.data.currencies.CryptoCurrency
import com.developerstring.nexpay.ui.components.CryptoCurrencyItem
import com.developerstring.nexpay.ui.crypto.CurrencyScreenRoute
import com.developerstring.nexpay.ui.nfc.NFCScreenRoute
import com.developerstring.nexpay.ui.onboarding.create_profile.CreateProfileScreenRoute
import com.developerstring.nexpay.ui.transaction.AddTransactionScreenRoute
import com.developerstring.nexpay.ui.transaction.QRScannerScreenRoute
import com.developerstring.nexpay.ui.transaction.ReceivePaymentScreenRoute
import com.developerstring.nexpay.ui.transaction.SwapScreenRoute
import com.developerstring.nexpay.viewmodel.AptosViewModel
import com.developerstring.nexpay.viewmodel.SharedViewModel
import com.kmpalette.color
import kotlinx.serialization.Serializable
import org.jetbrains.compose.resources.imageResource

@Serializable
data object WalletScreenRoute

// Data class for animated ball
data class AnimatedBall(
    val id: Int,
    val color: Color,
    val size: Dp,
    var currentX: Float = 0f,
    var currentY: Float = 0f,
    var targetX: Float = 0f,
    var targetY: Float = 0f,
    var trail: MutableList<Pair<Offset, Float>> = mutableListOf() // Position and alpha pairs
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WalletScreen(
    sharedViewModel: SharedViewModel,
    aptosViewModel: AptosViewModel,
    navController: NavController,
) {

    sharedViewModel.getCryptoCurrencies()

    val uiState by aptosViewModel.uiState.collectAsStateWithLifecycle()
    val clipboardManager = LocalClipboardManager.current
    var showCopiedMessage by remember { mutableStateOf(false) }
    val bottomSheetState = rememberModalBottomSheetState()
    var showBottomSheet by remember { mutableStateOf(false) }

    val imagePalette by sharedViewModel.imagePalette.collectAsState()

    // SharedViewModel color theme
    val lightVibrantColor by sharedViewModel.lightVibrantColor
    val darkVibrantColor by sharedViewModel.darkVibrantColor
    val vibrantColor by sharedViewModel.vibrantColor

    // Animated balls setup - fewer but bigger balls
    val density = LocalDensity.current
    val balls = remember {
        listOf(
            AnimatedBall(
                id = 1,
                color = imagePalette?.palette?.lightVibrantSwatch?.color ?: Color(0xFF8B5CF6),
                size = 200.dp
            ),
            AnimatedBall(
                id = 2,
                color = imagePalette?.palette?.vibrantSwatch?.color ?: Color(0xFF06B6D4),
                size = 180.dp
            ),
            AnimatedBall(
                id = 3,
                color = imagePalette?.palette?.lightMutedSwatch?.color ?: Color(0xFFEC4899),
                size = 160.dp
            )
        )
    }

    // Continuous animation states for each ball
    val infiniteTransition = rememberInfiniteTransition(label = "ball_animation")

    val imageIndex by sharedViewModel.getUserName().collectAsState(initial = 0)

    // Individual continuous animations for each ball with different speeds and patterns
    val ball1X by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(12000, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "ball1_x"
    )
    val ball1Y by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(15000, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "ball1_y"
    )

    val ball2X by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(18000, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "ball2_x"
    )
    val ball2Y by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(10000, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "ball2_y"
    )

    val ball3X by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(14000, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "ball3_x"
    )
    val ball3Y by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(20000, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "ball3_y"
    )

    // Reset copied message after delay
    LaunchedEffect(showCopiedMessage) {
        if (showCopiedMessage) {
            kotlinx.coroutines.delay(2000)
            showCopiedMessage = false
        }
    }

    Box(
        modifier = Modifier.fillMaxSize()
    ) {
        // Animated background with continuously moving balls
        Canvas(
            modifier = Modifier
                .fillMaxSize()
                .blur(60.dp) // Enhanced blur effect
        ) {
            // Draw base gradient
            drawRect(
                color = imagePalette?.palette?.darkVibrantSwatch?.color ?: Color(0xFF1E1B4B),
            )

            // Get animation values for each ball
            val animationValues = listOf(
                Pair(ball1X, ball1Y),
                Pair(ball2X, ball2Y),
                Pair(ball3X, ball3Y)
            )

            balls.forEachIndexed { index, ball ->
                val ballSizePx = with(density) { ball.size.toPx() }
                val (xAnim, yAnim) = animationValues[index]

                // Calculate continuous smooth position using sine waves for figure-8 and circular patterns
                // Expand movement area to cover entire screen including top regions
                val baseX = size.width * 0.5f
                val baseY = size.height * 0.4f // Move base position higher to include top area
                val radiusX = (size.width - ballSizePx) * 0.4f // Increase horizontal range
                val radiusY = (size.height - ballSizePx) * 0.45f // Increase vertical range significantly

                // Create different movement patterns for each ball with full screen coverage
                val (newX, newY) = when (index) {
                    0 -> { // Figure-8 pattern covering top to bottom
                        val angle = xAnim * 2 * PI
                        Pair(
                            (baseX + radiusX * sin(angle)).toFloat(),
                            (baseY + radiusY * sin(2 * angle)).toFloat()
                        )
                    }

                    1 -> { // Large circular pattern covering entire screen
                        val angleX = xAnim * 2 * PI
                        val angleY = yAnim * 2 * PI
                        Pair(
                            (baseX + radiusX * 0.9f * cos(angleX)).toFloat(),
                            (size.height * 0.3f + radiusY * 0.8f * sin(angleY)).toFloat() // Move to upper area
                        )
                    }

                    2 -> { // Wide oval pattern with vertical emphasis
                        val angle = xAnim * 2 * PI + PI / 3
                        Pair(
                            (baseX + radiusX * 1.1f * cos(angle)).toFloat(),
                            (size.height * 0.5f + radiusY * 0.9f * sin(angle * 1.3)).toFloat() // Cover more vertical space
                        )
                    }

                    else -> Pair(baseX, baseY)
                }

                // Add current position to trail
                val currentPos = Offset(newX + ballSizePx / 2, newY + ballSizePx / 2)
                ball.trail.add(Pair(currentPos, 1f))

                // Update trail with longer, smoother fade
                ball.trail = ball.trail.mapIndexed { trailIndex, (pos, _) ->
                    val age = ball.trail.size - trailIndex - 1
                    val alpha = maxOf(0f, 1f - age * 0.03f) // Slower fade for longer trails
                    Pair(pos, alpha)
                }.filter { it.second > 0.02f }.toMutableList()

                // Limit trail length to prevent memory issues
                if (ball.trail.size > 100) {
                    ball.trail = ball.trail.takeLast(100).toMutableList()
                }

                // Draw trail with gradient effect
                ball.trail.forEach { (pos, alpha) ->
                    val sizeMultiplier = 0.3f + alpha * 0.7f // Trail gets smaller as it fades
                    drawCircle(
                        color = ball.color.copy(alpha = alpha * 0.4f),
                        radius = ballSizePx / 2 * sizeMultiplier,
                        center = pos
                    )
                }

                // Draw main ball with enhanced glow effect
                drawCircle(
                    color = ball.color.copy(alpha = 0.9f),
                    radius = ballSizePx / 2,
                    center = Offset(newX + ballSizePx / 2, newY + ballSizePx / 2)
                )

                // Draw inner glow
                drawCircle(
                    color = ball.color.copy(alpha = 0.6f),
                    radius = ballSizePx / 3,
                    center = Offset(newX + ballSizePx / 2, newY + ballSizePx / 2)
                )
            }
        }

        // Content over the animated background
        Column(
            modifier = Modifier
                .verticalScroll(rememberScrollState())
                .fillMaxSize()
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
                        onClick = {
                            navController.navigate(QRScannerScreenRoute)
                        },
                        modifier = Modifier
                            .size(40.dp)
                            .clip(RoundedCornerShape(8.dp))
                            .background(Color.White.copy(alpha = 0.1f))
                    ) {
                        Icon(
                            imageVector = Icons.Rounded.QrCodeScanner,
                            contentDescription = "QR Scanner",
                            tint = Color.White,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.Center
                ) {
                    Image(
                        bitmap = imageResource(Constants.listOfImages[imageIndex ?: 0]),
                        contentDescription = "Avatar Image",
                        modifier = Modifier
                            .size(160.dp)
                            .clip(CircleShape)
                            .clickable {
                                navController.navigate(CreateProfileScreenRoute)
                            },
                        contentScale = ContentScale.Crop
                    )
                }

                // Wallet Selector with Triangle Icon
                Spacer(modifier = Modifier.height(30.dp))

                // Balance Display
                Text(
                    text = if (uiState.isConnected) uiState.balance else "$0",
                    color = Color.White,
                    fontSize = 42.sp,
                    fontWeight = FontWeight.Medium,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.fillMaxWidth()
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        "Wallet Balance", color = Color.White,
                        fontSize = 14.sp,
                        textAlign = TextAlign.Center,
                        fontWeight = FontWeight.Medium,
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Icon(
                        imageVector = Icons.AutoMirrored.Rounded.RotateLeft,
                        contentDescription = "Copy",
                        tint = Color.White.copy(alpha = 0.6f),
                        modifier = Modifier
                            .size(18.dp)
                            .clickable {
                                uiState.walletAddress?.let {
                                    aptosViewModel.refreshBalance()
                                }
                            }
                    )
                }

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

                // Minimal Connect/Connected Status
                if (uiState.isConnected) {
                    // Connected state - minimal display
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp),
                        horizontalArrangement = Arrangement.Center,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.CheckCircle,
                            contentDescription = "Connected",
                            tint = lightVibrantColor,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = uiState.walletAddress?.let {
                                "${it.take(6)}...${it.takeLast(6)}"
                            } ?: "Connected",
                            color = Color.White.copy(alpha = 0.8f),
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Normal
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Icon(
                            imageVector = Icons.Rounded.ContentCopy,
                            contentDescription = "Copy",
                            tint = Color.White.copy(alpha = 0.6f),
                            modifier = Modifier
                                .size(16.dp)
                                .clickable {
                                    uiState.walletAddress?.let { address ->
                                        clipboardManager.setText(AnnotatedString(address))
                                        showCopiedMessage = true
                                    }
                                }
                        )
                    }
                } else {
                    // Connect button - minimal design
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 40.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Button(
                            onClick = { showBottomSheet = true },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(48.dp),
                            enabled = !uiState.isLoading,
                            colors = ButtonDefaults.buttonColors(
                                containerColor = Color.White.copy(alpha = 0.15f),
                                disabledContainerColor = Color.White.copy(alpha = 0.08f)
                            ),
                            shape = RoundedCornerShape(16.dp),
                            elevation = ButtonDefaults.buttonElevation(
                                defaultElevation = 0.dp,
                                pressedElevation = 0.dp
                            )
                        ) {
                            if (uiState.isLoading) {
                                CircularProgressIndicator(
                                    modifier = Modifier.size(18.dp),
                                    color = Color.White,
                                    strokeWidth = 2.dp
                                )
                            } else {
                                Text(
                                    text = "Connect Wallet",
                                    color = Color.White,
                                    fontSize = 15.sp,
                                    fontWeight = FontWeight.Medium
                                )
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(20.dp))

                // First Row - Send and Receive
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    // Send Button Card
                    Card(
                        onClick = {
                            navController.navigate(AddTransactionScreenRoute())
                        },
                        modifier = Modifier
                            .weight(1f)
                            .height(100.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = lightVibrantColor.copy(alpha = 0.4f)
                        ),
                        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp),
                        shape = RoundedCornerShape(16.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(16.dp),
                        ) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Rounded.Send,
                                contentDescription = "Send",
                                tint = Color.White,
                                modifier = Modifier.size(28.dp).align(Alignment.TopEnd).rotate(-45f)
                            )
                            Text(
                                modifier = Modifier.align(Alignment.CenterStart).padding(start = 15.dp),
                                text = "Send",
                                color = Color.White,
                                fontSize = 18.sp,
                                fontWeight = FontWeight.Medium
                            )
                        }
                    }

                    // Receive Button Card
                    Card(
                        onClick = {
                            navController.navigate(ReceivePaymentScreenRoute)
                        },
                        modifier = Modifier
                            .weight(1f)
                            .height(100.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = vibrantColor.copy(alpha = 0.4f)
                        ),
                        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp),
                        shape = RoundedCornerShape(16.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(16.dp),
                        ) {
                            Icon(
                                imageVector = Icons.Rounded.QrCode,
                                contentDescription = "Receive",
                                tint = Color.White,
                                modifier = Modifier.size(28.dp).align(Alignment.TopEnd)
                            )
                            Text(
                                text = "Receive",
                                color = Color.White,
                                fontSize = 18.sp,
                                fontWeight = FontWeight.Medium,
                                modifier = Modifier.align(Alignment.CenterStart).padding(start = 15.dp),
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Second Row - Tap To Pay and Swap
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    // Tap To Pay Button Card
                    Card(
                        onClick = {
                            navController.navigate(NFCScreenRoute)
                        },
                        modifier = Modifier
                            .weight(1f)
                            .height(100.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = vibrantColor.copy(alpha = 0.4f)
                        ),
                        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp),
                        shape = RoundedCornerShape(16.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(16.dp),
                        ) {
                            Icon(
                                imageVector = Icons.Rounded.Nfc,
                                contentDescription = "Tap To Pay",
                                tint = Color.White,
                                modifier = Modifier.size(28.dp).align(Alignment.TopEnd)
                            )
                            Text(
                                text = "Tap To Pay",
                                color = Color.White,
                                fontSize = 18.sp,
                                fontWeight = FontWeight.Medium,
                                modifier = Modifier.align(Alignment.CenterStart).padding(start = 15.dp)
                            )
                        }
                    }

                    // Swap Button Card
                    Card(
                        onClick = {
                            navController.navigate(SwapScreenRoute)
                        },
                        modifier = Modifier
                            .weight(1f)
                            .height(100.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = lightVibrantColor.copy(alpha = 0.4f)
                        ),
                        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp),
                        shape = RoundedCornerShape(16.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(16.dp),
                        ) {
                            Icon(
                                imageVector = Icons.Rounded.SwapHoriz,
                                contentDescription = "Swap",
                                tint = Color.White,
                                modifier = Modifier.size(28.dp).align(Alignment.TopEnd)
                            )
                            Text(
                                text = "Swap",
                                color = Color.White,
                                fontSize = 18.sp,
                                fontWeight = FontWeight.Medium,
                                modifier = Modifier.align(Alignment.CenterStart).padding(start = 15.dp),
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
                Spacer(modifier = Modifier.height(24.dp))

                // TODO List of crypto
                ListCryptoCurrencies(
                    sharedViewModel = sharedViewModel,
                    navController = navController,
                    darkVibrantColor = darkVibrantColor
                )
                Spacer(modifier = Modifier.height(100.dp))
            }
        }

        // Bottom Sheet for Wallet Connection Options
        if (showBottomSheet) {
            ModalBottomSheet(
                onDismissRequest = { showBottomSheet = false },
                sheetState = bottomSheetState,
                containerColor = Color.White,
                modifier = Modifier.fillMaxWidth(),
                dragHandle = {
                    Surface(
                        modifier = Modifier
                            .padding(vertical = 8.dp)
                            .width(40.dp)
                            .height(4.dp),
                        shape = RoundedCornerShape(2.dp),
                        color = lightVibrantColor.copy(alpha = 0.3f)
                    ) {}
                }
            ) {
                WalletConnectionBottomSheet(
                    lightVibrantColor = lightVibrantColor,
                    darkVibrantColor = darkVibrantColor,
                    vibrantColor = vibrantColor,
                    onCreateWallet = {
                        showBottomSheet = false
                        aptosViewModel.connectWallet()
                    },
                    onImportWallet = { privateKey ->
                        showBottomSheet = false
                        aptosViewModel.connectWallet(privateKey)
                    }
                )
            }
        }
    }
}

@Composable
fun ListCryptoCurrencies(
    sharedViewModel: SharedViewModel,
    navController: NavController,
    darkVibrantColor: Color
) {

    val data by sharedViewModel.cryptoCurrencyList.collectAsState()
    var cryptoCurrency by remember {
        mutableStateOf<CryptoCurrency?>(null)
    }

    data.forEach {
        CryptoCurrencyItem(darkColor = darkVibrantColor,sharedViewModel = sharedViewModel, cryptoCurrency = it, onClick = { selectedCurrency ->
            sharedViewModel.emptyMarketChart()
            sharedViewModel.getMarketChart(selectedCurrency.id)
            sharedViewModel.currencyCrypto.value = selectedCurrency
            cryptoCurrency = selectedCurrency

            navController.navigate(CurrencyScreenRoute)
        })
    }
}

@Composable
private fun WalletConnectionBottomSheet(
    lightVibrantColor: Color,
    darkVibrantColor: Color,
    vibrantColor: Color,
    onCreateWallet: () -> Unit,
    onImportWallet: (privateKey: String) -> Unit
) {
    var isImportMode by remember { mutableStateOf(false) }
    var privateKey by remember { mutableStateOf("") }
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(24.dp),
        verticalArrangement = Arrangement.spacedBy(24.dp)
    ) {
        if (!isImportMode) {
            // Header
            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "Connect Your Wallet",
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold,
                    color = darkVibrantColor,
                    textAlign = TextAlign.Center
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "Choose how you'd like to set up your wallet",
                    fontSize = 16.sp,
                    color = darkVibrantColor.copy(alpha = 0.7f),
                    textAlign = TextAlign.Center
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Create Wallet Button
            Card(
                onClick = onCreateWallet,
                modifier = Modifier
                    .fillMaxWidth(),
                colors = CardDefaults.cardColors(
                    containerColor = lightVibrantColor.copy(alpha = 0.1f)
                ),
                shape = RoundedCornerShape(16.dp),
                elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
            ) {
                Row(
                    modifier = Modifier
                        .padding(20.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(40.dp)
                            .background(
                                lightVibrantColor.copy(alpha = 0.2f),
                                CircleShape
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Rounded.Add,
                            contentDescription = "Create Wallet",
                            tint = darkVibrantColor,
                            modifier = Modifier.size(24.dp)
                        )
                    }

                    Column {
                        Text(
                            text = "Create New Wallet",
                            fontSize = 18.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = darkVibrantColor
                        )
                        Text(
                            text = "Generate a new wallet with secure keys",
                            fontSize = 14.sp,
                            color = darkVibrantColor.copy(alpha = 0.7f)
                        )
                    }

                    Icon(
                        imageVector = Icons.Default.ChevronRight,
                        contentDescription = "Go",
                        tint = darkVibrantColor.copy(alpha = 0.5f),
                        modifier = Modifier.size(20.dp)
                    )
                }
            }

            // Import Wallet Button
            Card(
                onClick = { isImportMode = true },
                modifier = Modifier
                    .fillMaxWidth(),
                colors = CardDefaults.cardColors(
                    containerColor = vibrantColor.copy(alpha = 0.1f)
                ),
                shape = RoundedCornerShape(16.dp),
                elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(20.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(40.dp)
                            .background(
                                vibrantColor.copy(alpha = 0.2f),
                                CircleShape
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Rounded.Key,
                            contentDescription = "Import Wallet",
                            tint = darkVibrantColor,
                            modifier = Modifier.size(24.dp)
                        )
                    }

                    Column {
                        Text(
                            text = "Import Wallet",
                            fontSize = 18.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = darkVibrantColor,
                        )

                        Text(
                            text = "Import an existing wallet using your private key",
                            fontSize = 14.sp,
                            color = darkVibrantColor.copy(alpha = 0.7f)
                        )
                    }

                    Icon(
                        imageVector = Icons.Rounded.ChevronRight,
                        contentDescription = "Go",
                        tint = darkVibrantColor.copy(alpha = 0.5f),
                        modifier = Modifier.size(20.dp)
                    )
                }
            }
        } else {
            // Import Wallet Mode - Private Key Input
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(24.dp)
            ) {
                // Header with back button
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    IconButton(
                        onClick = { isImportMode = false },
                        modifier = Modifier
                            .size(40.dp)
                            .background(
                                lightVibrantColor.copy(alpha = 0.1f),
                                CircleShape
                            )
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back",
                            tint = darkVibrantColor,
                            modifier = Modifier.size(20.dp)
                        )
                    }

                    Column {
                        Text(
                            text = "Import Wallet",
                            fontSize = 24.sp,
                            fontWeight = FontWeight.Bold,
                            color = darkVibrantColor
                        )
                        Text(
                            text = "Enter your private key to import wallet",
                            fontSize = 14.sp,
                            color = darkVibrantColor.copy(alpha = 0.7f)
                        )
                    }
                }

                // Private Key Input Field
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text(
                        text = "Private Key",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Medium,
                        color = darkVibrantColor
                    )

                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(
                                lightVibrantColor.copy(alpha = 0.2f),
                                RoundedCornerShape(16.dp)
                            )
                            .padding(16.dp)
                    ) {
                        BasicTextField(
                            value = privateKey,
                            onValueChange = { privateKey = it },
                            modifier = Modifier.fillMaxWidth(),
                            textStyle = TextStyle(
                                color = darkVibrantColor,
                                fontSize = 14.sp,
                                lineHeight = 20.sp
                            ),
                            cursorBrush = SolidColor(vibrantColor),
                            decorationBox = { innerTextField ->
                                if (privateKey.isEmpty()) {
                                    Text(
                                        text = "Enter your private key here...",
                                        color = darkVibrantColor.copy(alpha = 0.5f),
                                        fontSize = 14.sp,
                                        lineHeight = 20.sp
                                    )
                                }
                                innerTextField()
                            }
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Connect Wallet Button
                Button(
                    onClick = {
                        if (privateKey.isNotBlank()) {
                            onImportWallet(privateKey)
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp),
                    enabled = privateKey.isNotBlank(),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = darkVibrantColor,
                        contentColor = Color.White,
                        disabledContainerColor = lightVibrantColor.copy(alpha = 0.3f),
                        disabledContentColor = darkVibrantColor.copy(alpha = 0.5f)
                    ),
                    shape = RoundedCornerShape(16.dp)
                ) {
                    Text(
                        text = "Connect Wallet",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Medium
                    )
                }
            }
        }

        if (!isImportMode) {
            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}
