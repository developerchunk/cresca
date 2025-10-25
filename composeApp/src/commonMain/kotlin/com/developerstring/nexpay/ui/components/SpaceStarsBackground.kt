package com.developerstring.nexpay.ui.components

import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import kotlin.random.Random

data class StarFieldConfig(
    val starCount: Int = 80,
    val cometCount: Int = 8,
    val showNebula: Boolean = true,
    val showTexturedStar: Boolean = true,
    val nebulaCenter: Pair<Float, Float> = Pair(0.35f, 0.4f),
    val texturedStarCenter: Pair<Float, Float> = Pair(1.15f, 0.25f),
    val texturedStarRadius: Float = 0.25f
)

@Composable
fun StarFieldBackground(
    modifier: Modifier = Modifier,
    config: StarFieldConfig = StarFieldConfig()
) {
    val random = remember { Random(42) }

    val stars = remember(config.starCount) {
        List(config.starCount) {
            val x = random.nextFloat()
            val y = random.nextFloat()
            val radius = random.nextFloat() * 1.8f + 0.3f
            val delay = random.nextInt(0, 2000)
            Triple(x, y, Pair(radius, delay))
        }
    }

    // Define multiple comet paths with different angles and starting positions
    val cometPaths = remember(config.cometCount) {
        List(config.cometCount) { _ ->
            val startXRel = random.nextFloat() * 0.3f - 0.2f  // Start from left side with variation
            val startYRel = random.nextFloat() * 0.4f - 0.1f  // Start from top with variation
            val endXRel = startXRel + 0.8f + random.nextFloat() * 0.6f  // Different end positions
            val endYRel = startYRel + 0.6f + random.nextFloat() * 0.8f  // Different end positions
            val speed = 0.8f + random.nextFloat() * 0.4f  // Different speeds
            Triple(
                Pair(startXRel, startYRel),  // Start position
                Pair(endXRel, endYRel),      // End position
                speed                        // Speed multiplier
            )
        }
    }

    val infiniteTransition = rememberInfiniteTransition()

    // Precompute animation states at composable scope
    val starAlphas = stars.map { triple ->
        infiniteTransition.animateFloat(
            initialValue = 0.25f,
            targetValue = 1f,
            animationSpec = infiniteRepeatable(
                animation = tween(durationMillis = 1500, delayMillis = triple.third.second, easing = LinearEasing),
                repeatMode = RepeatMode.Reverse
            )
        )
    }

    val cometStates = cometPaths.mapIndexed { i, path ->
        val baseDelay = i * 800  // Stagger comets more
        val baseDuration = 4000
        val duration = (baseDuration * path.third).toInt()  // Use speed multiplier
        infiniteTransition.animateFloat(
            initialValue = -0.2f,
            targetValue = 1.2f,
            animationSpec = infiniteRepeatable(
                animation = tween(durationMillis = duration, delayMillis = baseDelay, easing = LinearEasing),
                repeatMode = RepeatMode.Restart
            )
        )
    }

    Canvas(modifier = modifier) {
        val w = size.width
        val h = size.height

        // Soft nebula glow
        if (config.showNebula) {
            drawCircle(
                brush = Brush.radialGradient(
                    colors = listOf(Color(0xFF2b2e7a).copy(alpha = 0.25f), Color.Transparent),
                    center = Offset(x = w * config.nebulaCenter.first, y = h * config.nebulaCenter.second),
                    radius = w * 0.6f
                ),
                radius = w * 0.6f,
                center = Offset(x = w * config.nebulaCenter.first, y = h * config.nebulaCenter.second)
            )
        }

        // Large textured star positioned half off-screen
        if (config.showTexturedStar) {
            val starCenter = Offset(x = w * config.texturedStarCenter.first, y = h * config.texturedStarCenter.second)
            val starRadius = w * config.texturedStarRadius

            // Base star body with warm gradient
            drawCircle(
                brush = Brush.radialGradient(
                    colors = listOf(
                        Color(0xFFffd89b).copy(alpha = 0.9f), // Warm yellow center
                        Color(0xFFff8a65).copy(alpha = 0.8f), // Orange mid
                        Color(0xFFd84315).copy(alpha = 0.6f)  // Red-orange edge
                    ),
                    center = starCenter,
                    radius = starRadius
                ),
                radius = starRadius,
                center = starCenter
            )

            // Add surface texture with multiple smaller gradients
            val textureSpots = listOf(
                Triple(0.3f, 0.2f, 0.15f), // x offset, y offset, size ratio
                Triple(-0.2f, 0.4f, 0.12f),
                Triple(0.1f, -0.3f, 0.18f),
                Triple(-0.4f, -0.1f, 0.10f),
                Triple(0.35f, -0.15f, 0.14f),
                Triple(-0.15f, 0.25f, 0.11f)
            )

            textureSpots.forEach { (offsetX, offsetY, sizeRatio) ->
                val spotCenter = Offset(
                    starCenter.x + starRadius * offsetX,
                    starCenter.y + starRadius * offsetY
                )
                val spotRadius = starRadius * sizeRatio

                // Dark crater-like spots
                drawCircle(
                    brush = Brush.radialGradient(
                        colors = listOf(
                            Color(0xFF8d4004).copy(alpha = 0.8f), // Dark center
                            Color(0xFFbf360c).copy(alpha = 0.4f), // Lighter edge
                            Color.Transparent
                        ),
                        center = spotCenter,
                        radius = spotRadius
                    ),
                    radius = spotRadius,
                    center = spotCenter
                )
            }

            // Add bright surface highlights
            val highlights = listOf(
                Triple(-0.1f, -0.2f, 0.08f),
                Triple(0.25f, 0.1f, 0.06f),
                Triple(-0.3f, 0.15f, 0.05f)
            )

            highlights.forEach { (offsetX, offsetY, sizeRatio) ->
                val highlightCenter = Offset(
                    starCenter.x + starRadius * offsetX,
                    starCenter.y + starRadius * offsetY
                )
                val highlightRadius = starRadius * sizeRatio

                drawCircle(
                    brush = Brush.radialGradient(
                        colors = listOf(
                            Color(0xFFfff3e0).copy(alpha = 0.9f), // Bright center
                            Color(0xFFffcc02).copy(alpha = 0.6f), // Golden
                            Color.Transparent
                        ),
                        center = highlightCenter,
                        radius = highlightRadius
                    ),
                    radius = highlightRadius,
                    center = highlightCenter
                )
            }

            // Add atmospheric glow around the star
            drawCircle(
                brush = Brush.radialGradient(
                    colors = listOf(
                        Color.Transparent,
                        Color(0xFFff6f00).copy(alpha = 0.15f), // Orange glow
                        Color(0xFFff8f00).copy(alpha = 0.08f)  // Extended glow
                    ),
                    center = starCenter,
                    radius = starRadius * 1.4f
                ),
                radius = starRadius * 1.4f,
                center = starCenter
            )
        }

        // Small twinkling stars
        stars.forEachIndexed { index, triple ->
            val xRel = triple.first
            val yRel = triple.second
            val radius = triple.third.first
            val alpha = starAlphas.getOrNull(index)?.value ?: 1f

            drawCircle(
                color = Color.White.copy(alpha = alpha * 0.7f),
                radius = radius,
                center = Offset(xRel * w, yRel * h)
            )
        }

        // Multiple comet showers with different paths
        cometPaths.forEachIndexed { i, path ->
            val t = cometStates.getOrNull(i)?.value ?: 0f

            if (t >= 0f && t <= 1f) {  // Only draw when comet is on screen
                val startPos = path.first
                val endPos = path.second

                val startX = startPos.first * w
                val startY = startPos.second * h
                val endX = endPos.first * w
                val endY = endPos.second * h

                val curX = startX + (endX - startX) * t
                val curY = startY + (endY - startY) * t

                // Draw tail with reduced opacity to keep it behind cards
                val tailLength = 6
                for (k in 0 until tailLength) {
                    val kt = (t - k * 0.04f).coerceIn(0f, 1f)
                    val tx = startX + (endX - startX) * kt
                    val ty = startY + (endY - startY) * kt
                    val tailAlpha = ((1f - k * 0.15f).coerceAtLeast(0f)) * 0.4f  // Reduced alpha
                    val tailRadius = (4f * (1f - k * 0.12f)).coerceAtLeast(0.5f)
                    drawCircle(
                        color = Color.White.copy(alpha = tailAlpha),
                        radius = tailRadius,
                        center = Offset(tx, ty)
                    )
                }

                // Comet head with reduced opacity
                drawCircle(
                    color = Color.White.copy(alpha = 0.6f),
                    radius = 4f,
                    center = Offset(curX, curY)
                )
            }
        }
    }
}
