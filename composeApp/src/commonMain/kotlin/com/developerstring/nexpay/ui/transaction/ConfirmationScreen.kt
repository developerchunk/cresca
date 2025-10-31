package com.developerstring.nexpay.ui.transaction

import androidx.compose.animation.core.*
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.slideInVertically
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.*
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.developerstring.nexpay.viewmodel.AptosViewModel
import kotlinx.datetime.*
import kotlinx.serialization.Serializable

@Serializable
data class ConfirmationScreenRoute(
    val senderAddress: String,
    val recipientAddress: String,
    val amount: String,
    val note: String?,
    val executedAt: Long?,
    val scheduledAt: Long?,
    val isSuccess: Boolean
)

@Composable
fun ConfirmationScreen(
    aptosViewModel: AptosViewModel,
    sharedViewModel: com.developerstring.nexpay.viewmodel.SharedViewModel,
    senderAddress: String,
    recipientAddress: String,
    amount: String,
    executedAt: Long? = null,
    scheduledAt: Long? = null,
    isSuccess: Boolean,
    note: String? = null,
    navController: NavController,
) {
    var animationStarted by remember { mutableStateOf(false) }
    val gasFees by aptosViewModel.gasFees.collectAsState()
    val hex by aptosViewModel.hex.collectAsState()

    // SharedViewModel color theme
    val lightVibrantColor by sharedViewModel.lightVibrantColor
    val darkVibrantColor by sharedViewModel.darkVibrantColor

    // Start animation on composition
    LaunchedEffect(Unit) {
        animationStarted = true
    }

    // Animation phases
    val checkAnimationProgress by animateFloatAsState(
        targetValue = if (animationStarted) 1f else 0f,
        animationSpec = tween(
            durationMillis = 1500,
            easing = CubicBezierEasing(0.25f, 0.1f, 0.25f, 1f)
        ),
        label = "check_animation"
    )

    val circleScale by animateFloatAsState(
        targetValue = if (animationStarted) 1f else 0f,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessLow
        ),
        label = "circle_scale"
    )

    val pulseAnimation by rememberInfiniteTransition(label = "pulse").animateFloat(
        initialValue = 0.8f,
        targetValue = 1.2f,
        animationSpec = infiniteRepeatable(
            animation = tween(2000, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "pulse_animation"
    )

    // Staggered animations for details
    val detailsVisible by remember {
        derivedStateOf { checkAnimationProgress > 0.7f }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    colors = listOf(
                        Color.White,
                        lightVibrantColor.copy(alpha = 0.03f)
                    )
                )
            )
    ) {
        Column(modifier = Modifier.fillMaxSize().verticalScroll(rememberScrollState())) {
            // Header
            PhantomHeader(
                title = if (isSuccess) "Transaction Successful" else "Transaction Failed",
                darkVibrantColor = darkVibrantColor,
                lightVibrantColor = lightVibrantColor,
                onBackClick = { navController.navigateUp() }
            )

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Spacer(modifier = Modifier.height(20.dp))

                // Animated success/failure circle with checkmark
                Box(
                    modifier = Modifier.size(200.dp),
                    contentAlignment = Alignment.Center
                ) {
                    // Pulsing background circle
                    Canvas(
                        modifier = Modifier
                            .size(200.dp)
                            .scale(pulseAnimation * circleScale)
                    ) {
                        drawCircle(
                            color = if (isSuccess) Color(0xFF10B981).copy(alpha = 0.1f)
                                   else Color(0xFFEF4444).copy(alpha = 0.1f),
                            radius = size.minDimension / 2
                        )
                    }

                    // Main circle with animated stroke
                    Canvas(
                        modifier = Modifier
                            .size(160.dp)
                            .scale(circleScale)
                    ) {
                        val strokeWidth = 8.dp.toPx()
                        val radius = (size.minDimension - strokeWidth) / 2

                        // Background circle
                        drawCircle(
                            color = Color.White,
                            radius = radius,
                            style = Stroke(width = strokeWidth)
                        )

                        // Animated progress circle
                        drawArc(
                            color = if (isSuccess) Color(0xFF10B981) else Color(0xFFEF4444),
                            startAngle = -90f,
                            sweepAngle = 360f * checkAnimationProgress,
                            useCenter = false,
                            style = Stroke(
                                width = strokeWidth,
                                cap = StrokeCap.Round
                            )
                        )
                    }

                    // Animated checkmark or X
                    AnimatedIcon(
                        isSuccess = isSuccess,
                        progress = checkAnimationProgress,
                        modifier = Modifier.size(80.dp)
                    )
                }

                Spacer(modifier = Modifier.height(40.dp))

                // Animated status text
                AnimatedVisibility(
                    visible = checkAnimationProgress > 0.5f,
                    enter = fadeIn(
                        animationSpec = tween(800, delayMillis = 200)
                    ) + slideInVertically(
                        initialOffsetY = { it / 2 },
                        animationSpec = tween(800, delayMillis = 200)
                    )
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = if (isSuccess) {
                                if (scheduledAt != null) "Scheduled Successfully!" else "Completed Successfully!"
                            } else {
                                "Transaction Failed"
                            },
                            style = MaterialTheme.typography.headlineMedium.copy(
                                fontWeight = FontWeight.Bold,
                                fontSize = 24.sp,
                                color = if (isSuccess) Color(0xFF10B981) else Color(0xFFEF4444),
                                letterSpacing = (-0.3).sp
                            ),
                            textAlign = TextAlign.Center
                        )
                    }
                }

                Spacer(modifier = Modifier.height(40.dp))

                // Transaction details in 2 main cards
                AnimatedVisibility(
                    visible = detailsVisible,
                    enter = fadeIn(
                        animationSpec = tween(600, delayMillis = 800)
                    ) + slideInVertically(
                        initialOffsetY = { it / 2 },
                        animationSpec = tween(600, delayMillis = 800)
                    )
                ) {
                    Column(
                        modifier = Modifier.fillMaxWidth(),
                        verticalArrangement = Arrangement.spacedBy(20.dp)
                    ) {
                        // Card 1: Big card with amount and main info
                        BigTransactionCard(
                            amount = amount,
                            fee = gasFees,
                            executedAt = executedAt,
                            scheduledAt = scheduledAt,
                            lightVibrantColor = lightVibrantColor,
                            darkVibrantColor = darkVibrantColor
                        )

                        // Card 2: Addresses and other details
                        AddressDetailsCard(
                            senderAddress = senderAddress,
                            recipientAddress = recipientAddress,
                            hex = hex,
                            note = note,
                            lightVibrantColor = lightVibrantColor,
                            darkVibrantColor = darkVibrantColor
                        )
                    }
                }

                Spacer(modifier = Modifier.weight(1f))

                // Animated action buttons
                AnimatedVisibility(
                    visible = detailsVisible,
                    enter = fadeIn(
                        animationSpec = tween(600, delayMillis = 1200)
                    ) + slideInVertically(
                        initialOffsetY = { it },
                        animationSpec = tween(600, delayMillis = 1200)
                    )
                ) {
                    Column(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        // Primary action button
                        Button(
                            onClick = {
                                // Navigate to transaction details or home
                                navController.popBackStack()
                            },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(56.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = if (isSuccess) Color(0xFF10B981) else Color(0xFFEF4444)
                            ),
                            shape = RoundedCornerShape(16.dp)
                        ) {
                            Text(
                                text = if (isSuccess) "View Transaction" else "Try Again",
                                style = MaterialTheme.typography.titleMedium.copy(
                                    fontWeight = FontWeight.SemiBold,
                                    fontSize = 16.sp
                                ),
                                color = Color.White
                            )
                        }

                        // Secondary action button
                        OutlinedButton(
                            onClick = { navController.popBackStack() },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(56.dp),
                            colors = ButtonDefaults.outlinedButtonColors(
                                contentColor = Color(0xFF6B7280)
                            ),
                            border = BorderStroke(
                                width = 1.dp,
                                color = Color(0xFFE5E7EB)
                            ),
                            shape = RoundedCornerShape(16.dp)
                        ) {
                            Text(
                                text = "Back to Home",
                                style = MaterialTheme.typography.titleMedium.copy(
                                    fontWeight = FontWeight.Medium,
                                    fontSize = 16.sp
                                )
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun AnimatedIcon(
    isSuccess: Boolean,
    progress: Float,
    modifier: Modifier = Modifier
) {
    Canvas(modifier = modifier) {
        val strokeWidth = 6.dp.toPx()
        val color = if (isSuccess) Color(0xFF10B981) else Color(0xFFEF4444)

        if (isSuccess) {
            // Draw animated checkmark
            val checkProgress = maxOf(0f, (progress - 0.3f) / 0.7f)
            if (checkProgress > 0f) {
                val path = Path().apply {
                    val centerX = size.width / 2
                    val centerY = size.height / 2
                    val checkSize = size.minDimension * 0.4f

                    // Checkmark path
                    moveTo(centerX - checkSize * 0.3f, centerY)
                    lineTo(centerX - checkSize * 0.1f, centerY + checkSize * 0.2f)
                    lineTo(centerX + checkSize * 0.3f, centerY - checkSize * 0.2f)
                }

                val pathMeasure = PathMeasure()
                pathMeasure.setPath(path, false)
                val animatedPath = Path()
                pathMeasure.getSegment(0f, pathMeasure.length * checkProgress, animatedPath, true)

                drawPath(
                    path = animatedPath,
                    color = color,
                    style = Stroke(
                        width = strokeWidth,
                        cap = StrokeCap.Round,
                        join = StrokeJoin.Round
                    )
                )
            }
        } else {
            // Draw animated X
            val xProgress = maxOf(0f, (progress - 0.3f) / 0.7f)
            if (xProgress > 0f) {
                val centerX = size.width / 2
                val centerY = size.height / 2
                val xSize = size.minDimension * 0.3f

                // First diagonal
                val line1Progress = minOf(1f, xProgress * 2f)
                if (line1Progress > 0f) {
                    drawLine(
                        color = color,
                        start = Offset(centerX - xSize, centerY - xSize),
                        end = Offset(
                            centerX - xSize + (xSize * 2 * line1Progress),
                            centerY - xSize + (xSize * 2 * line1Progress)
                        ),
                        strokeWidth = strokeWidth,
                        cap = StrokeCap.Round
                    )
                }

                // Second diagonal
                val line2Progress = maxOf(0f, xProgress * 2f - 1f)
                if (line2Progress > 0f) {
                    drawLine(
                        color = color,
                        start = Offset(centerX + xSize, centerY - xSize),
                        end = Offset(
                            centerX + xSize - (xSize * 2 * line2Progress),
                            centerY - xSize + (xSize * 2 * line2Progress)
                        ),
                        strokeWidth = strokeWidth,
                        cap = StrokeCap.Round
                    )
                }
            }
        }
    }
}

@Composable
private fun PhantomHeader(
    title: String,
    darkVibrantColor: Color,
    lightVibrantColor: Color,
    onBackClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 24.dp, vertical = 16.dp)
            .padding(top = 34.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        IconButton(
            onClick = onBackClick,
            modifier = Modifier
                .size(40.dp)
                .clip(RoundedCornerShape(12.dp))
                .background(lightVibrantColor.copy(alpha = 0.3f))
        ) {
            Icon(
                Icons.AutoMirrored.Rounded.ArrowBack,
                contentDescription = "Back",
                tint = darkVibrantColor,
                modifier = Modifier.size(20.dp)
            )
        }

        Text(
            text = title,
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold,
            color = darkVibrantColor,
            letterSpacing = (-0.3).sp,
            textAlign = TextAlign.Center,
            modifier = Modifier.weight(1f)
        )

        Spacer(modifier = Modifier.width(40.dp))
    }
}

@Composable
private fun BigTransactionCard(
    amount: String,
    fee: Long,
    executedAt: Long?,
    scheduledAt: Long?,
    lightVibrantColor: Color,
    darkVibrantColor: Color
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(20.dp))
            .background(lightVibrantColor.copy(alpha = 0.3f))
            .padding(24.dp)
    ) {
        Column(
            modifier = Modifier.fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Amount - Big text
            Text(
                text = "$amount APT",
                fontSize = 32.sp,
                fontWeight = FontWeight.Bold,
                color = darkVibrantColor,
                textAlign = TextAlign.Center
            )

            // Fee - smaller but prominent
            Text(
                text = "Fee: ${fee / 100000000.0} APT",
                fontSize = 16.sp,
                fontWeight = FontWeight.Medium,
                color = darkVibrantColor.copy(alpha = 0.7f),
                textAlign = TextAlign.Center
            )

            // Timestamp if available
            val timestamp = scheduledAt ?: executedAt
            if (timestamp != null) {
                val dateTime = Instant.fromEpochMilliseconds(timestamp)
                    .toLocalDateTime(TimeZone.currentSystemDefault())
                val timeLabel = if (scheduledAt != null) "Scheduled for" else "Completed at"
                val timeValue = "${dateTime.date.month.number}/${dateTime.date.dayOfMonth}/${dateTime.date.year} ${
                    dateTime.time.hour.toString().padStart(2, '0')
                }:${dateTime.time.minute.toString().padStart(2, '0')}"

                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Text(
                        text = timeLabel,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Medium,
                        color = darkVibrantColor.copy(alpha = 0.6f),
                        letterSpacing = 0.5.sp
                    )
                    Text(
                        text = timeValue,
                        fontSize = 15.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = darkVibrantColor
                    )
                }
            }
        }
    }
}

@Composable
private fun AddressDetailsCard(
    senderAddress: String,
    recipientAddress: String,
    hex: String,
    note: String?,
    lightVibrantColor: Color,
    darkVibrantColor: Color
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(20.dp))
            .background(lightVibrantColor.copy(alpha = 0.3f))
            .padding(24.dp)
    ) {
        Column(
            modifier = Modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(20.dp)
        ) {
            // From Address
            AddressRow(
                label = "From",
                address = senderAddress,
                darkVibrantColor = darkVibrantColor
            )

            // To Address
            AddressRow(
                label = "To",
                address = recipientAddress,
                darkVibrantColor = darkVibrantColor
            )

            // Transaction Hash
            AddressRow(
                label = "Hash",
                address = hex,
                darkVibrantColor = darkVibrantColor
            )

            // Note if available
            if (note?.isNotEmpty() == true) {
                Column(
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text(
                        text = "Note",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Medium,
                        color = darkVibrantColor.copy(alpha = 0.6f),
                        letterSpacing = 0.5.sp
                    )
                    Text(
                        text = note,
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Medium,
                        color = darkVibrantColor,
                        lineHeight = 20.sp
                    )
                }
            }
        }
    }
}

@Composable
private fun AddressRow(
    label: String,
    address: String,
    darkVibrantColor: Color
) {
    Column(
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Text(
            text = label,
            fontSize = 13.sp,
            fontWeight = FontWeight.Medium,
            color = darkVibrantColor.copy(alpha = 0.6f),
            letterSpacing = 0.5.sp
        )
        Text(
            text = if (address.length > 20) {
                "${address.take(12)}...${address.takeLast(12)}"
            } else address,
            fontSize = 15.sp,
            fontWeight = FontWeight.SemiBold,
            color = darkVibrantColor,
            letterSpacing = 0.2.sp
        )
    }
}