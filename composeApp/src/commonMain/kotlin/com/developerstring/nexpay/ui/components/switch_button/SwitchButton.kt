package com.developerstring.jetco_kmp.components.button.switch_button

import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

/**
 * A customizable and animated switch button composable.
 *
 * This component provides a smooth, animated toggle interaction with full control over appearance,
 * colors, shapes, and animation behavior. Ideal for feature toggles, settings, or binary state switches.
 *
 * @param buttonSizeWidth The overall width of the switch button.
 * @param buttonSizeHeight The overall height of the switch button.
 * @param switchButtonConfig configure the switch using [SwitchButtonConfig] — defines colors, shapes, padding, and more.
 * @param animation Animation configuration using [SwitchButtonAnimation] — defines duration, delay, and easing.
 * @param isSelected The initial state of the switch (true = selected/on).
 * @param icon Can use [SwitchButtonIcon] to customize the icon within the switch — optional.
 * @param onStateChange Callback invoked whenever the switch state changes (returns the new state).
 *
 * ### Example:
 * ```
 * SwitchButton(
 *     isSelected = state,
 *     onStateChange = { newState -> state = newState }
 * )
 * ```
 */
@Composable
fun SwitchButton(
    modifier: Modifier = Modifier,
    buttonSizeWidth: Dp = 60.dp,
    buttonSizeHeight: Dp = 35.dp,
    switchButtonConfig: SwitchButtonConfig = SwitchButtonConfig(),
    animation: SwitchButtonAnimation = SwitchButtonAnimation(),
    isSelected: Boolean,
    icon: @Composable () -> Unit = {},
    onStateChange: (Boolean) -> Unit
) {
    val switchSize by remember {
        mutableStateOf(buttonSizeHeight - switchButtonConfig.switchPadding * 2)
    }
    val interactionSource = remember { MutableInteractionSource() }

    var switchClicked by remember { mutableStateOf(isSelected) }

    var internalPadding by remember {
        mutableStateOf(0.dp)
    }

    internalPadding =
        if (switchClicked) buttonSizeWidth - switchSize - switchButtonConfig.switchPadding * 2
        else 0.dp

    val animateSize by animateDpAsState(
        targetValue = if (switchClicked) internalPadding else 0.dp,
        tween(
            durationMillis = animation.animationDuration,
            delayMillis = animation.animationDelay,
            easing = animation.animationEasing
        )
    )

    Box(
        modifier = modifier
            .width(buttonSizeWidth)
            .height(buttonSizeHeight)
            .clip(shape = switchButtonConfig.switchShape)
            .background(
                if (switchClicked) switchButtonConfig.selectedBackgroundColor
                else switchButtonConfig.unSelectedBackgroundColor
            )
            .clickable(
                interactionSource = interactionSource,
                indication = null
            ) {
                switchClicked = !switchClicked
                onStateChange(switchClicked)
            },
    ) {

        Row(modifier = Modifier.fillMaxSize()) {
            Box(
                modifier = Modifier
                    .fillMaxHeight()
                    .width(animateSize)
                    .background(Color.Transparent)
            )
            Box(
                modifier = Modifier
                    .padding(switchButtonConfig.switchPadding)
                    .size(switchSize)
                    .clip(shape = switchButtonConfig.innerBoxShape)
                    .background(switchButtonConfig.innerBoxColor),
                contentAlignment = Alignment.Center
            ) {

                icon()

            }
        }

    }

}