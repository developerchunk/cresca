package com.developerstring.nexpay

import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import com.developerstring.nexpay.ui.theme.AppColors
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.developerstring.nexpay.ui.MainScreenRoute
import com.developerstring.nexpay.ui.onboarding.create_profile.CreateProfileScreenRoute
import com.developerstring.nexpay.ui.screens.applock.AppLockScreenRoute
import com.developerstring.nexpay.ui.components.StarFieldBackground
import com.developerstring.nexpay.ui.components.StarFieldConfig
import com.developerstring.nexpay.ui.onboarding.create_profile.ColorSchema
import com.developerstring.nexpay.ui.screens.applock.AppLockSettingsScreenRoute
import com.developerstring.nexpay.ui.screens.applock.PinSetupScreenRoute
import com.developerstring.nexpay.viewmodel.SharedViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.serialization.Serializable
import nexpay.composeapp.generated.resources.Res
import nexpay.composeapp.generated.resources.avt_1
import nexpay.composeapp.generated.resources.avt_2
import nexpay.composeapp.generated.resources.avt_3
import nexpay.composeapp.generated.resources.avt_4
import nexpay.composeapp.generated.resources.avt_5
import nexpay.composeapp.generated.resources.avt_6
import nexpay.composeapp.generated.resources.avt_7
import nexpay.composeapp.generated.resources.avt_8
import nexpay.composeapp.generated.resources.avt_9
import org.jetbrains.compose.resources.imageResource
import kotlin.math.*
import kotlin.random.Random

@Serializable
data object SplashScreenRoute

@Composable
fun SplashScreen(
    navController: NavController,
    sharedViewModel: SharedViewModel,
) {

    var onComplete by remember { mutableStateOf(false) }
    var selectedImageIndex by remember { mutableIntStateOf(0) }

    var isProfileCreated by remember { mutableStateOf(false) }
    var isAppLockEnabled by remember { mutableStateOf(false) }
    var isAppLocked by remember { mutableStateOf(true) }

    LaunchedEffect(Unit) {
        sharedViewModel.getCryptoCurrencies()
        launch {
            sharedViewModel.appLockState.collect { appLockState ->
                isAppLockEnabled = appLockState.isAppLockEnabled
            }
        }
        launch {
            sharedViewModel.isAppLocked.collect { locked ->
                isAppLocked = locked
            }
        }
        launch {
            sharedViewModel.getUserName().collect { index ->
                selectedImageIndex = index ?: 0
                isProfileCreated = index != null
            }
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                color = Color.Black
            )
    ) {
        Column(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            val globeSize = 300.dp

            // Globe with animation - optimized to minimize recomposition
            GlobeAnimated(
                size = globeSize,
                onComplete = {
                    onComplete = true
                },
                modifier = Modifier
            )

            Spacer(modifier = Modifier.height(24.dp))

            // Text with independent fade animation
            AnimatedText()
        }
    }

    if (onComplete) {

        if (isProfileCreated) {
            val listOfImages = listOf(
                imageResource(Res.drawable.avt_1),
                imageResource(Res.drawable.avt_2),
                imageResource(Res.drawable.avt_3),
                imageResource(Res.drawable.avt_4),
                imageResource(Res.drawable.avt_5),
                imageResource(Res.drawable.avt_6),
                imageResource(Res.drawable.avt_7),
                imageResource(Res.drawable.avt_8),
                imageResource(Res.drawable.avt_9),
            )

            ColorSchema(
                imageBitmap = listOfImages[selectedImageIndex],
                imageColors = { paletteState ->
                    sharedViewModel.setImagePalette(paletteState)
                }
            )
        }

        navController.navigate(
            when {
                !isProfileCreated -> CreateProfileScreenRoute
                !isAppLockEnabled -> PinSetupScreenRoute
                isAppLockEnabled && isAppLocked -> AppLockScreenRoute
                else -> AppLockScreenRoute
            }
        ) {
            popUpTo(SplashScreenRoute) { inclusive = true }
        }
    }
}

@Composable
private fun AnimatedText() {
    val textAlpha = remember { Animatable(0f) }

    LaunchedEffect(Unit) {
        textAlpha.animateTo(1f, animationSpec = tween(durationMillis = 900, easing = FastOutSlowInEasing))
        delay(3000)
        textAlpha.animateTo(0f, animationSpec = tween(durationMillis = 600, easing = LinearEasing))
    }

    Text(
        text = "Getting things ready for you!",
        fontSize = 18.sp,
        fontWeight = FontWeight.Medium,
        color = Color.White.copy(alpha = textAlpha.value)
    )
}

// Data class for animated dots inside the globe
private data class GlobeDot(
    val id: Int,
    val lat: Double,
    val lon: Double,
    val velocity: Double,
    val phase: Double
)

@Composable
private fun GlobeAnimated(
    size: Dp,
    onComplete: () -> Unit,
    modifier: Modifier = Modifier
) {
    val density = LocalDensity.current
    val sizePx = remember(density, size) { with(density) { size.toPx() } }

    // Animation states
    val rotation = remember { Animatable(0f) }
    val scale = remember { Animatable(0f) }

    // Static dots - created once, never change
    val dots = remember {
        val rnd = Random(54321)
        List(80) { i ->
            GlobeDot(
                id = i,
                lat = rnd.nextDouble(-60.0, 60.0),
                lon = rnd.nextDouble(-180.0, 180.0),
                velocity = rnd.nextDouble(0.3, 1.2),
                phase = rnd.nextDouble(0.0, 2 * PI)
            )
        }
    }

    // Infinite animation for time - this drives dot movement
    val infiniteTransition = rememberInfiniteTransition(label = "dotMovement")
    val animationTime by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 10000f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 10000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "time"
    )

    // Main animation sequence
    LaunchedEffect(Unit) {
        scale.animateTo(1f, animationSpec = tween(durationMillis = 400, easing = FastOutSlowInEasing))
        rotation.animateTo(60f, animationSpec = tween(durationMillis = 300, easing = LinearEasing))
        rotation.animateTo(540f, animationSpec = tween(durationMillis = 1500, easing = FastOutLinearInEasing))
        scale.animateTo(1.2f, animationSpec = tween(durationMillis = 400, easing = OvershootInterpolatorEasing(2f)))
        delay(100)
        scale.animateTo(0f, animationSpec = tween(durationMillis = 200, easing = LinearOutSlowInEasing))
        onComplete()
    }

    // Derive speed multiplier from rotation - only recalculates when rotation changes
    val speedMultiplier by remember {
        derivedStateOf {
            when {
                rotation.value < 60f -> 0.015f + (rotation.value / 60f) * 0.01f
                rotation.value < 480f -> {
                    val progress = (rotation.value - 60f) / (480f - 60f)
                    0.025f + progress * 0.025f
                }

                else -> {
                    val progress = (rotation.value - 480f) / (540f - 480f)
                    0.05f + progress * 0.03f
                }
            }
        }
    }

    // Static constants
    val initialCenterLon = remember { -95f }

    // Canvas drawing - only recomposes when scale, rotation, or animationTime changes
    Canvas(
        modifier = modifier.size(size)
    ) {
        val cx = sizePx / 2f
        val cy = sizePx / 2f
        val globeRadius = sizePx * 0.45f * scale.value

        if (scale.value > 0.001f) {
            // Shadow layers
            drawCircle(
                color = AppColors.AppColorVariants.materialBlueAlpha08,
                center = Offset(cx, cy + globeRadius * 0.18f),
                radius = globeRadius * 1.8f
            )
            drawCircle(
                color = AppColors.AppColorVariants.materialBlueAlpha06,
                center = Offset(cx, cy),
                radius = globeRadius * 1.25f
            )

            // Globe boundary
            drawCircle(
                color = AppColors.AppColorVariants.lightBlueAlpha95,
                center = Offset(cx, cy),
                radius = globeRadius,
                style = Stroke(width = 3f)
            )

            val centerLon = initialCenterLon + rotation.value

            // Draw lat/lon grid
            drawLatLonLines(cx, cy, globeRadius, centerLon)

            // Draw moving dots
            drawMovingDots(dots, cx, cy, globeRadius, centerLon, animationTime, speedMultiplier)
        }
    }
}

// Optimized lat/lon line drawing - pure function, no state
private fun DrawScope.drawLatLonLines(centerX: Float, centerY: Float, radius: Float, centerLon: Float) {
    val path = Path()
    val lonStep = 30
    val latStep = 30

    // Latitude lines
    for (lat in -60..60 step latStep) {
        path.reset()
        var started = false
        for (lon in -180..180 step 6) {
            val p = project(
                lat.toDouble(),
                lon.toDouble(),
                centerLon.toDouble(),
                centerX.toDouble(),
                centerY.toDouble(),
                radius.toDouble()
            )
            if (p.visible) {
                if (!started) {
                    path.moveTo(p.x.toFloat(), p.y.toFloat())
                    started = true
                } else {
                    path.lineTo(p.x.toFloat(), p.y.toFloat())
                }
            } else {
                started = false
            }
        }
        drawPath(path = path, color = AppColors.AppColorVariants.mediumBlueAlpha55, style = Stroke(width = 1f))
    }

    // Longitude lines
    for (lon in -180..180 step lonStep) {
        path.reset()
        var started = false
        for (lat in -90..90 step 3) {
            val p = project(
                lat.toDouble(),
                lon.toDouble(),
                centerLon.toDouble(),
                centerX.toDouble(),
                centerY.toDouble(),
                radius.toDouble()
            )
            if (p.visible) {
                if (!started) {
                    path.moveTo(p.x.toFloat(), p.y.toFloat())
                    started = true
                } else {
                    path.lineTo(p.x.toFloat(), p.y.toFloat())
                }
            } else {
                started = false
            }
        }
        drawPath(path = path, color = AppColors.AppColorVariants.mediumBlueAlpha45, style = Stroke(width = 0.9f))
    }
}

// Projection result
private data class Proj(val x: Double, val y: Double, val visible: Boolean)

// Pure function - no side effects, fully deterministic
@Suppress("NOTHING_TO_INLINE")
private inline fun project(lat: Double, lon: Double, centerLon: Double, cx: Double, cy: Double, radius: Double): Proj {
    val latRad = lat * PI / 180.0
    val lonRad = lon * PI / 180.0
    val centerLonRad = centerLon * PI / 180.0
    val deltaLon = lonRad - centerLonRad

    val cosLat = cos(latRad)
    val visible = (cosLat * cos(deltaLon)) > 0
    if (!visible) return Proj(0.0, 0.0, false)

    val x = cx + radius * cosLat * sin(deltaLon)
    val y = cy - radius * sin(latRad)
    return Proj(x, y, true)
}

// Optimized dot drawing - pure function
private fun DrawScope.drawMovingDots(
    dots: List<GlobeDot>,
    centerX: Float,
    centerY: Float,
    radius: Float,
    centerLon: Float,
    time: Float,
    speedMultiplier: Float
) {
    for (dot in dots) {
        val effectiveSpeed = dot.velocity * speedMultiplier
        val latOffset = sin(time * effectiveSpeed + dot.phase) * 15.0
        val lonOffset = cos(time * effectiveSpeed * 0.7 + dot.phase * 1.5) * 25.0

        val currentLat = (dot.lat + latOffset).coerceIn(-85.0, 85.0)
        val currentLon = (dot.lon + lonOffset + 360.0) % 360.0 - 180.0

        val p = project(
            currentLat,
            currentLon,
            centerLon.toDouble(),
            centerX.toDouble(),
            centerY.toDouble(),
            radius.toDouble()
        )
        if (!p.visible) continue

        val sizeVariation = 1f + sin(time * effectiveSpeed * 2 + dot.phase).toFloat() * 0.3f
        val dotRadius = (2.2f + dot.velocity.toFloat() * 0.8f) * sizeVariation
        val intensity = 0.7f + speedMultiplier * 0.06f

        drawCircle(
            color = AppColors.lightBlue.copy(alpha = intensity),
            center = Offset(p.x.toFloat(), p.y.toFloat()),
            radius = dotRadius
        )

        // Glow only appears if speed exceeds threshold (never happens with current speeds)
        if (speedMultiplier > 2f) {
            drawCircle(
                color = Color.White.copy(alpha = 0.15f * (speedMultiplier - 2f) / 3f),
                center = Offset(p.x.toFloat(), p.y.toFloat()),
                radius = dotRadius * 1.8f
            )
        }
    }
}

// Easing helper - stateless
private class OvershootInterpolatorEasing(private val tension: Float) : Easing {
    override fun transform(fraction: Float): Float {
        val t = fraction - 1.0f
        return t * t * ((tension + 1) * t + tension) + 1.0f
    }
}