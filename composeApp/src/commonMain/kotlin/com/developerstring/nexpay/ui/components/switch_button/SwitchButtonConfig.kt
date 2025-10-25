package com.developerstring.jetco_kmp.components.button.switch_button

import androidx.compose.animation.core.Easing
import androidx.compose.animation.core.LinearOutSlowInEasing
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

/**
 * Defines the visual configuration for the [SwitchButton].
 *
 * Use this class to customize the colors, shapes, and padding of the switch’s container and inner knob.
 *
 * @property selectedBackgroundColor Background color when the switch is selected/on.
 * @property unSelectedBackgroundColor Background color when the switch is unselected/off.
 * @property switchPadding Padding between the switch border and inner knob.
 * @property switchShape Shape of the outer switch container.
 * @property innerBoxColor Color of the inner knob that moves on toggle.
 * @property innerBoxShape Shape of the inner knob.
 */
data class SwitchButtonConfig(
    val selectedBackgroundColor: Color = Color(0xFF1E90FF),
    val unSelectedBackgroundColor: Color = Color(0xFF636B7B),
    val switchPadding: Dp = 3.dp,
    val switchShape: Shape = CircleShape,
    val innerBoxColor: Color = Color.White,
    val innerBoxShape: Shape = CircleShape,
)

/**
 * Defines animation behavior for the [SwitchButton].
 *
 * Allows control over duration, delay, and easing curve to fine-tune the toggle animation.
 *
 * @property animationDuration Duration of the switch movement animation in milliseconds.
 * @property animationDelay Optional delay before the animation starts.
 * @property animationEasing Easing function that defines how the animation progresses.
 *
 * Example:
 * ```
 * SwitchButtonAnimation(
 *     animationDuration = 500,
 *     animationEasing = FastOutSlowInEasing
 * )
 * ```
 */
data class SwitchButtonAnimation(
    val animationDuration: Int = 700,
    val animationDelay: Int = 0,
    val animationEasing: Easing = LinearOutSlowInEasing
)