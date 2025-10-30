package com.developerstring.nexpay.ui.transaction

import androidx.compose.animation.core.*
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.slideInVertically
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
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
                brush = Brush.verticalGradient(
                    colors = if (isSuccess) {
                        listOf(
                            Color(0xFF10B981).copy(alpha = 0.1f),
                            Color(0xFF059669).copy(alpha = 0.05f),
                            Color.White
                        )
                    } else {
                        listOf(
                            Color(0xFFEF4444).copy(alpha = 0.1f),
                            Color(0xFFDC2626).copy(alpha = 0.05f),
                            Color.White
                        )
                    }
                )
            )
    ) {
        // Background floating particles
        AnimatedFloatingParticles(isSuccess = isSuccess)

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(80.dp))

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
                            if (scheduledAt != null) "Transaction Scheduled!" else "Transaction Successful!"
                        } else {
                            "Transaction Failed"
                        },
                        style = MaterialTheme.typography.headlineMedium.copy(
                            fontWeight = FontWeight.Bold,
                            fontSize = 28.sp,
                            color = if (isSuccess) Color(0xFF10B981) else Color(0xFFEF4444)
                        ),
                        textAlign = TextAlign.Center
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    Text(
                        text = if (isSuccess) {
                            if (scheduledAt != null) "Your transaction has been scheduled successfully"
                            else "Your transaction has been completed successfully"
                        } else {
                            "Something went wrong with your transaction"
                        },
                        style = MaterialTheme.typography.bodyLarge.copy(
                            color = Color(0xFF6B7280),
                            fontSize = 16.sp
                        ),
                        textAlign = TextAlign.Center
                    )
                }
            }

            Spacer(modifier = Modifier.height(40.dp))

            // Animated transaction details
            AnimatedTransactionDetails(
                visible = detailsVisible,
                senderAddress = senderAddress,
                recipientAddress = recipientAddress,
                amount = amount,
                hex = hex,
                fee = gasFees,
                executedAt = executedAt,
                scheduledAt = scheduledAt,
                note = note
            )

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
private fun AnimatedTransactionDetails(
    visible: Boolean,
    senderAddress: String,
    recipientAddress: String,
    amount: String,
    hex: String,
    fee: Long,
    executedAt: Long?,
    scheduledAt: Long?,
    note: String?
) {
    val detailItems = listOf(
        "Amount" to "$amount APT",
        "From" to "${senderAddress.take(8)}...${senderAddress.takeLast(6)}",
        "To" to "${recipientAddress.take(8)}...${recipientAddress.takeLast(6)}",
        "Fee" to "${fee / 100000000.0} APT",
        "Hash" to "${hex.take(8)}...${hex.takeLast(6)}",
    ).let { items ->
        if (note?.isNotEmpty() == true) items + ("Note" to note) else items
    }.let { items ->
        val timestamp = scheduledAt ?: executedAt
        if (timestamp != null) {
            val dateTime = Instant.fromEpochMilliseconds(timestamp)
                .toLocalDateTime(TimeZone.currentSystemDefault())
            val timeLabel = if (scheduledAt != null) "Scheduled for" else "Completed at"
            val timeValue = "${dateTime.date.month.number}/${dateTime.date.dayOfMonth}/${dateTime.date.year} ${
                dateTime.time.hour.toString().padStart(2, '0')
            }:${dateTime.time.minute.toString().padStart(2, '0')}"
            items + (timeLabel to timeValue)
        } else items
    }

    LazyColumn(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        items(detailItems.size) { index ->
            val (label, value) = detailItems[index]

            AnimatedVisibility(
                visible = visible,
                enter = fadeIn(
                    animationSpec = tween(500, delayMillis = 800 + index * 100)
                ) + slideInVertically(
                    initialOffsetY = { it / 2 },
                    animationSpec = tween(
                        durationMillis = 600,
                        delayMillis = 800 + index * 100,
                        easing = FastOutSlowInEasing
                    )
                )
            ) {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(
                        containerColor = Color.White
                    ),
                    elevation = CardDefaults.cardElevation(
                        defaultElevation = 4.dp
                    ),
                    shape = RoundedCornerShape(16.dp)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(20.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = label,
                            style = MaterialTheme.typography.bodyMedium.copy(
                                fontWeight = FontWeight.Medium,
                                color = Color(0xFF6B7280)
                            )
                        )

                        Text(
                            text = value,
                            style = MaterialTheme.typography.bodyMedium.copy(
                                fontWeight = FontWeight.SemiBold,
                                color = Color(0xFF1F2937)
                            ),
                            textAlign = TextAlign.End
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun AnimatedFloatingParticles(isSuccess: Boolean) {
    val infiniteTransition = rememberInfiniteTransition(label = "particles")

    // Create multiple floating particles
    repeat(12) { index ->
        val offsetX by infiniteTransition.animateFloat(
            initialValue = (-50).dp.value,
            targetValue = 50.dp.value,
            animationSpec = infiniteRepeatable(
                animation = tween(
                    durationMillis = 3000 + index * 200,
                    easing = LinearEasing
                ),
                repeatMode = RepeatMode.Reverse
            ),
            label = "particle_x_$index"
        )

        val offsetY by infiniteTransition.animateFloat(
            initialValue = 0f,
            targetValue = -100f,
            animationSpec = infiniteRepeatable(
                animation = tween(
                    durationMillis = 4000 + index * 300,
                    easing = LinearEasing
                ),
                repeatMode = RepeatMode.Restart
            ),
            label = "particle_y_$index"
        )

        val alpha by infiniteTransition.animateFloat(
            initialValue = 0f,
            targetValue = 0.6f,
            animationSpec = infiniteRepeatable(
                animation = tween(2000 + index * 100),
                repeatMode = RepeatMode.Reverse
            ),
            label = "particle_alpha_$index"
        )

        Box(
            modifier = Modifier
                .offset(
                    x = offsetX.dp + (index * 30).dp,
                    y = offsetY.dp + (index * 50).dp
                )
                .size((4 + index % 3 * 2).dp)
                .background(
                    color = if (isSuccess)
                        Color(0xFF10B981).copy(alpha = alpha * 0.5f)
                    else
                        Color(0xFFEF4444).copy(alpha = alpha * 0.5f),
                    shape = CircleShape
                )
        )
    }
}