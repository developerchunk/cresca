package com.developerstring.nexpay.ui.components

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.EaseInOutCubic
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.PathMeasure
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.unit.dp
import kotlin.math.roundToInt

@Composable
fun LineChartSingle(
    modifier: Modifier = Modifier,
    chartInfo: List<Double> = emptyList(),
    graphColor: Color = Color.Green,
    showGradient: Boolean = true,
    animationDuration: Int = 1500
) {
    if (chartInfo.isEmpty()) return

    val spacing = 50f
    val upperValue = remember(chartInfo) { (chartInfo.maxOrNull()?.plus(1))?.roundToInt() ?: 0 }
    val lowerValue = remember(chartInfo) { chartInfo.minOrNull()?.toInt() ?: 0 }

    // Animation progress from 0f to 1f (drawing from left to right)
    val animationProgress = remember { Animatable(0f) }

    LaunchedEffect(chartInfo) {
        animationProgress.snapTo(0f)
        animationProgress.animateTo(
            targetValue = 1f,
            animationSpec = tween(
                durationMillis = animationDuration,
                easing = EaseInOutCubic
            )
        )
    }

    Canvas(modifier = modifier.fillMaxSize()) {
        val width = size.width
        val height = size.height

        if (chartInfo.size < 2) return@Canvas

        val spacePerPoint = (width - spacing * 2) / (chartInfo.size - 1).coerceAtLeast(1)
        val valueRange = (upperValue - lowerValue).coerceAtLeast(1)

        // Create the full path
        val fullPath = Path()
        val gradientPath = Path()

        chartInfo.forEachIndexed { index, value ->
            val ratio = (value - lowerValue) / valueRange
            val x = spacing + index * spacePerPoint
            val y = height - spacing - (ratio * (height - spacing * 2)).toFloat()

            when (index) {
                0 -> {
                    fullPath.moveTo(x, y)
                    gradientPath.moveTo(x, y)
                }
                else -> {
                    val prevValue = chartInfo[index - 1]
                    val prevRatio = (prevValue - lowerValue) / valueRange
                    val prevX = spacing + (index - 1) * spacePerPoint
                    val prevY = height - spacing - (prevRatio * (height - spacing * 2)).toFloat()

                    // Smooth curve using quadratic bezier
                    val controlX = (prevX + x) / 2f
                    fullPath.quadraticTo(prevX, prevY, controlX, (prevY + y) / 2f)
                    gradientPath.quadraticTo(prevX, prevY, controlX, (prevY + y) / 2f)

                    if (index == chartInfo.lastIndex) {
                        fullPath.lineTo(x, y)
                        gradientPath.lineTo(x, y)
                    }
                }
            }
        }

        // Complete the gradient path to the bottom
        if (showGradient) {
            val lastX = spacing + (chartInfo.size - 1) * spacePerPoint
            gradientPath.lineTo(lastX, height - spacing)
            gradientPath.lineTo(spacing, height - spacing)
            gradientPath.close()
        }

        // Measure the path to get the current segment based on animation progress
        val pathMeasure = PathMeasure()
        pathMeasure.setPath(fullPath, false)
        val pathLength = pathMeasure.length

        // Calculate the current length to draw
        val currentLength = pathLength * animationProgress.value

        // Create animated path segment
        val animatedPath = Path()
        pathMeasure.getSegment(0f, currentLength, animatedPath, true)

        // Draw gradient fill with fade effect
        if (showGradient && animationProgress.value > 0.1f) {
            val gradientAlpha = (animationProgress.value * 0.3f).coerceIn(0f, 0.3f)
            val gradientBrush = Brush.verticalGradient(
                colors = listOf(
                    graphColor.copy(alpha = gradientAlpha),
                    graphColor.copy(alpha = 0f)
                ),
                startY = spacing,
                endY = height - spacing
            )

            // Create partial gradient path based on animation
            val partialGradientPath = Path()
            chartInfo.take((chartInfo.size * animationProgress.value).toInt().coerceAtLeast(2)).forEachIndexed { index, value ->
                val ratio = (value - lowerValue) / valueRange
                val x = spacing + index * spacePerPoint
                val y = height - spacing - (ratio * (height - spacing * 2)).toFloat()

                when (index) {
                    0 -> partialGradientPath.moveTo(x, y)
                    else -> {
                        val prevValue = chartInfo[index - 1]
                        val prevRatio = (prevValue - lowerValue) / valueRange
                        val prevX = spacing + (index - 1) * spacePerPoint
                        val prevY = height - spacing - (prevRatio * (height - spacing * 2)).toFloat()
                        val controlX = (prevX + x) / 2f
                        partialGradientPath.quadraticTo(prevX, prevY, controlX, (prevY + y) / 2f)
                        if (index == (chartInfo.size * animationProgress.value).toInt().coerceAtMost(chartInfo.lastIndex)) {
                            partialGradientPath.lineTo(x, y)
                        }
                    }
                }
            }

            val lastDrawnIndex = (chartInfo.size * animationProgress.value).toInt().coerceAtMost(chartInfo.lastIndex)
            val lastX = spacing + lastDrawnIndex * spacePerPoint
            partialGradientPath.lineTo(lastX, height - spacing)
            partialGradientPath.lineTo(spacing, height - spacing)
            partialGradientPath.close()

            drawPath(
                path = partialGradientPath,
                brush = gradientBrush
            )
        }

        // Draw the animated line with glow effect
        drawPath(
            path = animatedPath,
            brush = Brush.horizontalGradient(
                colors = listOf(
                    graphColor.copy(alpha = 0.5f),
                    graphColor,
                    graphColor
                ),
                startX = 0f,
                endX = currentLength
            ),
            style = Stroke(
                width = 3.dp.toPx(),
                cap = StrokeCap.Round,
                join = StrokeJoin.Round
            )
        )

        // Add a subtle glow effect
        drawPath(
            path = animatedPath,
            color = graphColor.copy(alpha = 0.2f),
            style = Stroke(
                width = 6.dp.toPx(),
                cap = StrokeCap.Round,
                join = StrokeJoin.Round
            )
        )

        // Draw animated endpoint circle
        if (animationProgress.value > 0.05f) {
            val currentPointIndex = ((chartInfo.size - 1) * animationProgress.value).toInt().coerceIn(0, chartInfo.lastIndex)
            val currentValue = chartInfo[currentPointIndex]
            val currentRatio = (currentValue - lowerValue) / valueRange
            val currentX = spacing + currentPointIndex * spacePerPoint
            val currentY = height - spacing - (currentRatio * (height - spacing * 2)).toFloat()

            // Outer glow circle
            drawCircle(
                color = graphColor.copy(alpha = 0.2f),
                radius = 8.dp.toPx(),
                center = Offset(currentX, currentY)
            )

            // Main circle
            drawCircle(
                color = graphColor,
                radius = 4.dp.toPx(),
                center = Offset(currentX, currentY)
            )

            // Inner white circle
            drawCircle(
                color = Color.White,
                radius = 2.dp.toPx(),
                center = Offset(currentX, currentY)
            )
        }
    }
}
