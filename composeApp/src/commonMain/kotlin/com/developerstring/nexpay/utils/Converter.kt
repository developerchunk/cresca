package com.developerstring.nexpay.utils

import kotlin.math.roundToInt

fun Double.formatToTwoDecimalPlaces(): String {
    val rounded = (this * 100).roundToInt() / 100.0
    val integerPart = rounded.toInt()
    // Use absolute value to avoid negative fractional parts
    val fractionalPart = kotlin.math.abs((rounded - integerPart) * 100).roundToInt()
    return if (fractionalPart == 0) {
        "$integerPart.00"
    } else if (fractionalPart < 10) {
        "$integerPart.0$fractionalPart"
    } else {
        "$integerPart.$fractionalPart"
    }
}