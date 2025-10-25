package com.developerstring.nexpay.ui.bottom_nav

import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.gestures.detectTransformGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Fill
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.developerstring.nexpay.Constants.CountriesWithCoordinates
import com.developerstring.nexpay.ui.components.AnimatedGlowButton
import com.developerstring.nexpay.ui.components.StarFieldBackground
import com.developerstring.nexpay.ui.components.StarFieldConfig
import com.developerstring.nexpay.ui.theme.AppColors.AppColorVariants
import com.developerstring.nexpay.viewmodel.SharedViewModel
import com.developerstring.jetco_kmp.components.button.switch_button.SwitchButton
import com.developerstring.jetco_kmp.components.button.switch_button.SwitchButtonIcon
import kotlinx.serialization.Serializable
import nexpay.composeapp.generated.resources.Res
import nexpay.composeapp.generated.resources.rounded_explore_off
import nexpay.composeapp.generated.resources.rounded_explore_toggle
import org.jetbrains.compose.resources.vectorResource
import kotlin.math.*

@Serializable
data object HomeScreenRoute

@Composable
fun HomeScreen(
    sharedViewModel: SharedViewModel,
    navController: NavController,
) {

}