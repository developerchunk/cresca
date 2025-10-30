// Test file to verify formatting fixes
import kotlin.math.roundToInt

fun Double.formatToTwoDecimalPlaces(): String {
    val rounded = (this * 100).roundToInt() / 100.0
    val integerPart = rounded.toInt()
    val fractionalPart = ((rounded - integerPart) * 100).roundToInt()
    return if (fractionalPart == 0) {
        "$integerPart.00"
    } else if (fractionalPart < 10) {
        "$integerPart.0$fractionalPart"
    } else {
        "$integerPart.$fractionalPart"
    }
}

// Test cases that were causing "-2.-39%" issues
val testValues = listOf(-2.39, -2.66, 1.23, 0.0, -0.05, 10.999)

println("Testing formatToTwoDecimalPlaces function:")
testValues.forEach { value ->
    val formatted = value.formatToTwoDecimalPlaces()
    println("$value -> $formatted%")
}
