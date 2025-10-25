package com.developerstring.jetco_kmp.components.button.switch_button

import androidx.compose.animation.Crossfade
import androidx.compose.animation.core.Easing
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp

/**
 * A composable that provides animated icon switching for use within the [SwitchButton] or standalone.
 *
 * Supports smooth rotation and crossfade animations when toggling between two icons.
 *
 * @param modifier Modifier to apply to the composable.
 * @param isSelected Determines which icon to show (true = [selectedIcon], false = [unSelectedIcon]).
 * @param selectedIcon Icon displayed when selected/on.
 * @param unSelectedIcon Icon displayed when unselected/off.
 * @param iconModifier Modifier applied to the icon (default size: 25.dp).
 * @param enableRotate Enables or disables rotation animation.
 * @param rotationEasing Easing curve for the rotation animation.
 * @param rotationDuration Duration of the rotation animation in milliseconds.
 * @param rotationAngle Target rotation angle when toggling on.
 * @param initialRotationAngle Initial rotation angle before animation starts.
 * @param contentDescription Description for accessibility.
 * @param iconColor Tint color applied to the icon.
 *
 * ### Example:
 * ```
 * SwitchButtonIcon(
 *     isSelected = state,
 *     selectedIcon = Icons.Default.Check,
 *     unSelectedIcon = Icons.Default.Close
 * )
 * ```
 */

@Composable
fun SwitchButtonIcon(
    modifier: Modifier = Modifier,
    isSelected: Boolean,
    selectedIcon: ImageVector,
    unSelectedIcon: ImageVector,
    iconModifier: Modifier = Modifier.size(25.dp),
    enableRotate: Boolean = true,
    rotationEasing: Easing = FastOutSlowInEasing,
    rotationDuration: Int = 600,
    rotationAngle: Float = 360f,
    initialRotationAngle: Float = 0f,
    contentDescription: String? = null,
    iconColor: Color = Color(0xFF16212B)
) {

    val rotation by animateFloatAsState(
        targetValue = if (isSelected) rotationAngle else initialRotationAngle,
        animationSpec = tween(durationMillis = rotationDuration, easing = rotationEasing),
        label = "rotationAnim"
    )

    Crossfade(
        modifier = modifier,
        targetState = isSelected,
        animationSpec = tween(durationMillis = rotationDuration, easing = rotationEasing)
    ) { selected ->
        Icon(
            imageVector = if (selected) selectedIcon else unSelectedIcon,
            contentDescription = contentDescription,
            tint = iconColor,
            modifier = iconModifier
                .rotate(if (enableRotate) rotation else 0f)
        )
    }
}