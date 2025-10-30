package com.developerstring.nexpay.ui.crypto

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.ArrowDropUp
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import kotlin.math.round
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import coil3.compose.AsyncImage
import com.developerstring.nexpay.ui.components.LineChartSingle
import com.developerstring.nexpay.ui.theme.AppColors
import com.developerstring.nexpay.viewmodel.SharedViewModel
import kotlinx.serialization.Serializable

private enum class TimeRange {
    ONE_DAY,
    ONE_WEEK,
    ONE_MONTH,
    ONE_YEAR,
    ALL_TIME
}

@Serializable
object CurrencyScreenRoute

@Composable
fun CurrencyScreen(
    sharedViewModel: SharedViewModel,
    navController: NavController
) {
    val cryptoCurrency by sharedViewModel.currencyCrypto
    val marketData by sharedViewModel.currencyMarketData.collectAsState()
    val scrollState = rememberScrollState()

    val vibrant by sharedViewModel.vibrantColor

    var selectedTimeRange by remember { mutableStateOf(TimeRange.ONE_DAY) }

    val priceChange = cryptoCurrency?.price_change_percentage_24h ?: 0.0
    val isPositive = priceChange >= 0

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(scrollState)
        ) {
            // Top App Bar
            TopAppBar(
                onBackClick = { navController.popBackStack() },
                cryptoCurrency = cryptoCurrency
            )

            // Main Content
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 24.dp)
            ) {
                Spacer(modifier = Modifier.height(20.dp))

                // Currency Header
                CurrencyHeader(
                    imageUrl = cryptoCurrency?.image ?: "",
                    name = cryptoCurrency?.name ?: "Unknown",
                    symbol = cryptoCurrency?.symbol?.uppercase() ?: ""
                )

                Spacer(modifier = Modifier.height(24.dp))

                // Price Display
                PriceDisplay(
                    currentPrice = cryptoCurrency?.current_price ?: 0.0,
                    priceChange = priceChange,
                    isPositive = isPositive
                )

                Spacer(modifier = Modifier.height(32.dp))

                // Chart Section
                if (marketData.isNotEmpty()) {
                    ChartSection(
                        marketData = marketData,
                        selectedTimeRange = selectedTimeRange,
                        onTimeRangeSelected = { selectedTimeRange = it },
                        isPositive = isPositive,
                        color = vibrant
                    )
                }

                Spacer(modifier = Modifier.height(32.dp))

                // Statistics Grid
                StatisticsGrid(cryptoCurrency)

                Spacer(modifier = Modifier.height(32.dp))

                // Action Buttons
                ActionButtons()

                Spacer(modifier = Modifier.height(32.dp))
            }
        }
    }
}

@Composable
private fun TopAppBar(
    onBackClick: () -> Unit,
    @Suppress("UNUSED_PARAMETER") cryptoCurrency: com.developerstring.nexpay.data.currencies.CryptoCurrency?
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        IconButton(
            onClick = onBackClick,
            modifier = Modifier.size(40.dp)
        ) {
            Icon(
                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                contentDescription = "Back",
                tint = Color.Black,
                modifier = Modifier.size(24.dp)
            )
        }

        Spacer(modifier = Modifier.width(8.dp))

        Text(
            text = "Currency Details",
            fontSize = 18.sp,
            fontWeight = FontWeight.SemiBold,
            color = Color.Black
        )
    }
}

@Composable
private fun CurrencyHeader(
    imageUrl: String,
    name: String,
    symbol: String
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        AsyncImage(
            modifier = Modifier
                .size(48.dp)
                .clip(CircleShape)
                .background(Color.Gray.copy(alpha = 0.1f)),
            model = imageUrl,
            contentDescription = name,
            contentScale = ContentScale.Crop
        )

        Spacer(modifier = Modifier.width(16.dp))

        Column {
            Text(
                text = name,
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                color = Color.Black
            )
            Text(
                text = symbol,
                fontSize = 14.sp,
                fontWeight = FontWeight.Medium,
                color = Color.Gray
            )
        }
    }
}

@Composable
private fun PriceDisplay(
    currentPrice: Double,
    priceChange: Double,
    isPositive: Boolean
) {
    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.Start
    ) {
        Text(
            text = "$${formatDecimal(currentPrice, 2)}",
            fontSize = 40.sp,
            fontWeight = FontWeight.Bold,
            color = Color.Black
        )

        Spacer(modifier = Modifier.height(8.dp))

        Row(
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = if (isPositive) Icons.Default.ArrowDropUp else Icons.Default.ArrowDropDown,
                contentDescription = null,
                tint = if (isPositive) AppColors.nebulaBlue else AppColors.marsRed,
                modifier = Modifier.size(24.dp)
            )

            Text(
                text = "${if (isPositive) "+" else ""}${formatDecimal(priceChange, 2)}%",
                fontSize = 18.sp,
                fontWeight = FontWeight.SemiBold,
                color = if (isPositive) AppColors.nebulaBlue else AppColors.marsRed
            )

            Spacer(modifier = Modifier.width(8.dp))

            Text(
                text = "24h",
                fontSize = 14.sp,
                fontWeight = FontWeight.Normal,
                color = Color.Gray
            )
        }
    }
}

@Composable
private fun ChartSection(
    marketData: List<Pair<Long, Double>>,
    color: Color,
    selectedTimeRange: TimeRange,
    onTimeRangeSelected: (TimeRange) -> Unit,
    @Suppress("UNUSED_PARAMETER") isPositive: Boolean
) {
    Column(modifier = Modifier.fillMaxWidth()) {
//        // Time Range Selector
//        TimeRangeSelector(
//            selectedTimeRange = selectedTimeRange,
//            onTimeRangeSelected = onTimeRangeSelected
//        )

        Spacer(modifier = Modifier.height(24.dp))

        // Chart
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .height(220.dp),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(
                containerColor = Color.Gray.copy(alpha = 0.05f)
            ),
            elevation = CardDefaults.cardElevation(0.dp)
        ) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp)
            ) {
                val dataPoints = marketData.map { it.second }
                LineChartSingle(
                    modifier = Modifier.fillMaxSize(),
                    chartInfo = dataPoints,
                    graphColor = if (isPositive) AppColors.nebulaBlue else AppColors.marsRed
                )
            }
        }
    }
}

@Composable
private fun TimeRangeSelector(
    selectedTimeRange: TimeRange,
    onTimeRangeSelected: (TimeRange) -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        val timeRanges = listOf(
            TimeRange.ONE_DAY to "1D",
            TimeRange.ONE_WEEK to "1W",
            TimeRange.ONE_MONTH to "1M",
            TimeRange.ONE_YEAR to "1Y",
            TimeRange.ALL_TIME to "ALL"
        )

        timeRanges.forEach { (range, label) ->
            TimeRangeChip(
                label = label,
                isSelected = selectedTimeRange == range,
                onClick = { onTimeRangeSelected(range) }
            )
        }
    }
}

@Composable
private fun TimeRangeChip(
    label: String,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    val backgroundColor by animateColorAsState(
        targetValue = if (isSelected) AppColors.primaryBlue else Color.Transparent,
        animationSpec = tween(300)
    )

    val textColor by animateColorAsState(
        targetValue = if (isSelected) Color.White else Color.Gray,
        animationSpec = tween(300)
    )

    val scale by animateFloatAsState(
        targetValue = if (isSelected) 1.05f else 1f,
        animationSpec = tween(300)
    )

    Box(
        modifier = Modifier
            .scale(scale)
            .clip(RoundedCornerShape(20.dp))
            .background(backgroundColor)
            .border(
                width = 1.dp,
                color = if (isSelected) AppColors.primaryBlue else Color.Gray.copy(alpha = 0.3f),
                shape = RoundedCornerShape(20.dp)
            )
            .clickable(
                onClick = onClick,
                indication = null,
                interactionSource = remember { MutableInteractionSource() }
            )
            .padding(horizontal = 16.dp, vertical = 8.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = label,
            fontSize = 14.sp,
            fontWeight = if (isSelected) FontWeight.SemiBold else FontWeight.Medium,
            color = textColor
        )
    }
}

@Composable
private fun StatisticsGrid(
    cryptoCurrency: com.developerstring.nexpay.data.currencies.CryptoCurrency?
) {
    Column(
        modifier = Modifier.fillMaxWidth()
    ) {
        Text(
            text = "Statistics",
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold,
            color = Color.Black
        )

        Spacer(modifier = Modifier.height(16.dp))

        Column(
            modifier = Modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                StatCard(
                    modifier = Modifier.weight(1f),
                    title = "Market Cap",
                    value = formatLargeNumber(cryptoCurrency?.market_cap ?: 0.0)
                )
                StatCard(
                    modifier = Modifier.weight(1f),
                    title = "24h Volume",
                    value = formatLargeNumber(cryptoCurrency?.total_volume ?: 0.0)
                )
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                StatCard(
                    modifier = Modifier.weight(1f),
                    title = "24h High",
                    value = "$${formatPrice(cryptoCurrency?.high_24h ?: 0.0)}"
                )
                StatCard(
                    modifier = Modifier.weight(1f),
                    title = "24h Low",
                    value = "$${formatPrice(cryptoCurrency?.low_24h ?: 0.0)}"
                )
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                StatCard(
                    modifier = Modifier.weight(1f),
                    title = "All Time High",
                    value = "$${formatPrice(cryptoCurrency?.ath ?: 0.0)}"
                )
                StatCard(
                    modifier = Modifier.weight(1f),
                    title = "All Time Low",
                    value = "$${formatPrice(cryptoCurrency?.atl ?: 0.0)}"
                )
            }
        }
    }
}

@Composable
private fun StatCard(
    modifier: Modifier = Modifier,
    title: String,
    value: String
) {
    Card(
        modifier = modifier,
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color.Gray.copy(alpha = 0.05f)
        ),
        elevation = CardDefaults.cardElevation(0.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Text(
                text = title,
                fontSize = 12.sp,
                fontWeight = FontWeight.Medium,
                color = Color.Gray
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = value,
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                color = Color.Black
            )
        }
    }
}

@Composable
private fun ActionButtons() {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        ActionButton(
            modifier = Modifier.weight(1f),
            text = "Buy",
            backgroundColor = AppColors.primaryBlue,
            textColor = Color.White,
            onClick = { /* TODO: Implement buy action */ }
        )

        ActionButton(
            modifier = Modifier.weight(1f),
            text = "Sell",
            backgroundColor = Color.Gray.copy(alpha = 0.1f),
            textColor = Color.Black,
            onClick = { /* TODO: Implement sell action */ }
        )
    }
}

@Composable
private fun ActionButton(
    modifier: Modifier = Modifier,
    text: String,
    backgroundColor: Color,
    textColor: Color,
    onClick: () -> Unit
) {
    Box(
        modifier = modifier
            .height(56.dp)
            .clip(RoundedCornerShape(28.dp))
            .background(backgroundColor)
            .clickable(onClick = onClick),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = text,
            fontSize = 16.sp,
            fontWeight = FontWeight.SemiBold,
            color = textColor
        )
    }
}

// Helper functions
private fun formatDecimal(value: Double, decimals: Int): String {
    val multiplier = when (decimals) {
        2 -> 100.0
        4 -> 10000.0
        6 -> 1000000.0
        8 -> 100000000.0
        else -> 100.0
    }
    val rounded = round(value * multiplier) / multiplier

    // Build format string manually for multiplatform compatibility
    val intPart = rounded.toLong()
    // Use absolute value for decimal calculation to avoid negative decimal parts
    val decimalPart = kotlin.math.abs((rounded - intPart) * multiplier).toLong().toString().padStart(decimals, '0')
    return "$intPart.$decimalPart"
}

private fun formatLargeNumber(number: Double): String {
    return when {
        number >= 1_000_000_000_000 -> "${formatDecimal(number / 1_000_000_000_000, 2)}T"
        number >= 1_000_000_000 -> "${formatDecimal(number / 1_000_000_000, 2)}B"
        number >= 1_000_000 -> "${formatDecimal(number / 1_000_000, 2)}M"
        number >= 1_000 -> "${formatDecimal(number / 1_000, 2)}K"
        else -> formatDecimal(number, 2)
    }
}

private fun formatPrice(price: Double): String {
    return when {
        price >= 1 -> formatDecimal(price, 2)
        price >= 0.01 -> formatDecimal(price, 4)
        price >= 0.0001 -> formatDecimal(price, 6)
        else -> formatDecimal(price, 8)
    }
}