package com.developerstring.nexpay.ui.theme

import androidx.compose.ui.graphics.Color

// Primary brand colors
object AppColors {
    // Background gradients
    val backgroundDark1 = Color(0xFF0b1020)
    val backgroundDark2 = Color(0xFF061025)
    val backgroundDark3 = Color(0xFF030417)

    val cosmicBlue = Color(0xFF1A1A2E)
    val deepSpaceBlue = Color(0xFF16213E)
    val midnightBlue = Color(0xFF0F3460)

    // Primary accent colors
    val primaryBlue = Color(0xFF4facfe)
    val electricBlue = Color(0xFF1E88E5)
    val brightCyan = Color(0xFF00D4FF)

    // Secondary colors
    val nebulaBlue = Color(0xFF4A90E2)
    val spacePurple = Color(0xFF9B59B6)
    val marsRed = Color(0xFFB03A2E)

    // Additional blue variants for splash screen
    val materialBlue = Color(0xFF2196F3)
    val lightBlue = Color(0xFF90CAF9)
    val mediumBlue = Color(0xFF64B5F6)

    // Text colors
    val lightSteelBlue = Color(0xFFB0C4DE)
    val textPrimary = Color.White
    val textSecondary = lightSteelBlue
    val textSelected = brightCyan

    // Utility colors
    val transparent = Color.Transparent
    val white = Color.White
    val black = Color.Black
    val plainBlack = Color(0xFF0A0A0A)
    val darkGray = Color(0xFF1A1A1A)
    val mediumGray = Color(0xFF2A2A2A)

    object AppColorVariants {
        val primaryBlueAlpha20 = AppColors.primaryBlue.copy(alpha = 0.2f)
        val primaryBlueAlpha10 = AppColors.primaryBlue.copy(alpha = 0.1f)
        val primaryBlueAlpha80 = AppColors.primaryBlue.copy(alpha = 0.8f)

        val brightCyanAlpha30 = AppColors.brightCyan.copy(alpha = 0.3f)
        val brightCyanAlpha60 = AppColors.brightCyan.copy(alpha = 0.6f)

        val nebulaBlueAlpha30 = AppColors.nebulaBlue.copy(alpha = 0.3f)
        val spacePurpleAlpha30 = AppColors.spacePurple.copy(alpha = 0.3f)
        val marsRedAlpha20 = AppColors.marsRed.copy(alpha = 0.2f)

        val electricBlueAlpha20 = AppColors.electricBlue.copy(alpha = 0.2f)

        // Additional blue variants for splash screen
        val materialBlueAlpha08 = AppColors.materialBlue.copy(alpha = 0.08f)
        val materialBlueAlpha06 = AppColors.materialBlue.copy(alpha = 0.06f)
        val lightBlueAlpha95 = AppColors.lightBlue.copy(alpha = 0.95f)
        val mediumBlueAlpha55 = AppColors.mediumBlue.copy(alpha = 0.55f)
        val mediumBlueAlpha45 = AppColors.mediumBlue.copy(alpha = 0.45f)

        // Additional purple variant
        val purpleAlpha30 = Color(0xFF9C27B0).copy(alpha = 0.3f)
    }
}

