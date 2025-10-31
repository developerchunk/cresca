package com.developerstring.nexpay.ui.screens

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.TrendingDown
import androidx.compose.material.icons.automirrored.filled.TrendingUp
import androidx.compose.material.icons.rounded.Warning
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import com.developerstring.nexpay.data.model.BundleToken
import com.developerstring.nexpay.data.model.CryptoBundle
import com.developerstring.nexpay.data.model.BundleData
import com.developerstring.nexpay.viewmodel.SharedViewModel
import kotlinx.serialization.Serializable

@Serializable
data class BundleDetailScreenRoute(val bundleId: Int)

@Serializable
data class ExecuteBundleScreenRoute(val bundleId: Int, val tradeType: String)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BundleDetailScreen(
    bundleId: Int,
    navController: NavController,
    sharedViewModel: SharedViewModel
) {
    val bundle = remember(bundleId) {
        BundleData.allBundles.find { it.id == bundleId }
    }

    val lightVibrantColor by sharedViewModel.lightVibrantColor
    val vibrantColor by sharedViewModel.vibrantColor
    val darkVibrantColor by sharedViewModel.darkVibrantColor

    val cryptoCurrencyList by sharedViewModel.cryptoCurrencyList.collectAsStateWithLifecycle()

    // Calculate total bundle value from real market data
    val totalValue = remember(cryptoCurrencyList, bundle) {
        bundle?.tokens?.sumOf { token ->
            val crypto = cryptoCurrencyList.find { it.symbol.uppercase() == token.symbol.uppercase() }
            (crypto?.current_price ?: 0.0) * (token.percentage / 100.0)
        } ?: 0.0
    }

    // Calculate 24h performance
    val performance24h = remember(cryptoCurrencyList, bundle) {
        bundle?.tokens?.sumOf { token ->
            val crypto = cryptoCurrencyList.find { it.symbol.uppercase() == token.symbol.uppercase() }
            (crypto?.price_change_percentage_24h ?: 0.0) * (token.percentage / 100.0)
        } ?: 0.0
    }

    if (bundle == null) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.White),
            contentAlignment = Alignment.Center
        ) {
            Text("Bundle not found", color = darkVibrantColor)
        }
        return
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
    ) {
        // Scrollable content
        LazyColumn(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
                .padding(top = 50.dp),
            contentPadding = PaddingValues(start = 20.dp, end = 20.dp, top = 16.dp, bottom = 136.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    IconButton(
                        onClick = { navController.navigateUp() },
                        modifier = Modifier
                            .size(40.dp)
                            .clip(RoundedCornerShape(12.dp))
                            .background(lightVibrantColor.copy(alpha = 0.22f))
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back",
                            tint = darkVibrantColor,
                            modifier = Modifier.size(20.dp)
                        )
                    }

                    Text(
                        text = "Bundle Details",
                        fontSize = 22.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = darkVibrantColor
                    )

                    Spacer(modifier = Modifier.size(40.dp))
                }
            }

            item {
                BundleHeaderCard(
                    bundle = bundle,
                    totalValue = totalValue,
                    performance24h = performance24h,
                    vibrantColor = vibrantColor,
                    lightVibrantColor = lightVibrantColor
                )
            }

            items(bundle.tokens) { token ->
                TokenItem(
                    token = token,
                    cryptoCurrencyList = cryptoCurrencyList,
                    vibrantColor = vibrantColor,
                    lightVibrantColor = lightVibrantColor
                )
            }

            item {
                AllocationChart(
                    tokens = bundle.tokens,
                    vibrantColor = vibrantColor,
                    lightVibrantColor = lightVibrantColor
                )
            }
        }

        // Fixed trading buttons at bottom
        FixedTradingActions(
            bundleId = bundleId,
            navController = navController,
            lightVibrantColor = lightVibrantColor
        )
    }
}

@Composable
fun BundleHeaderCard(
    bundle: CryptoBundle,
    totalValue: Double,
    performance24h: Double,
    vibrantColor: Color,
    lightVibrantColor: Color
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(
            containerColor = lightVibrantColor.copy(alpha = 0.08f)
        ),
        elevation = CardDefaults.cardElevation(0.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly,
                verticalAlignment = Alignment.Top
            ) {
                Column(
                    modifier = Modifier.weight(1f)
                ) {
                    Text(
                        text = bundle.name,
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF1A1A2E),
                        maxLines = 3,
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = bundle.category,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Medium,
                        color = Color(0xFF1A1A2E).copy(alpha = 0.6f)
                    )
                }

                Spacer(modifier = Modifier.height(10.dp))

                Box(
                    modifier = Modifier
                        .width(80.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .background(vibrantColor)
                        .padding(horizontal = 12.dp, vertical = 6.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = bundle.symbol,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                }
            }

            Text(
                text = bundle.description,
                fontSize = 14.sp,
                color = Color(0xFF1A1A2E).copy(alpha = 0.7f),
                lineHeight = 20.sp
            )

            HorizontalDivider(
                color = lightVibrantColor.copy(alpha = 0.3f),
                modifier = Modifier.padding(vertical = 8.dp)
            )

            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                Text(
                    text = "Total Bundle Value",
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Medium,
                    color = Color(0xFF1A1A2E).copy(alpha = 0.6f)
                )
                Text(
                    text = "$${formatDouble(totalValue, 2)}",
                    fontSize = 36.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF1A1A2E)
                )

                // 24h Performance
                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = if (performance24h >= 0) Icons.AutoMirrored.Filled.TrendingUp else Icons.AutoMirrored.Filled.TrendingDown,
                        contentDescription = null,
                        tint = if (performance24h >= 0) Color(0xFF4CAF50) else Color(0xFFE53935),
                        modifier = Modifier.size(20.dp)
                    )
                    Text(
                        text = "${if (performance24h >= 0) "+" else ""}${formatDouble(performance24h, 2)}%",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = if (performance24h >= 0) Color(0xFF4CAF50) else Color(0xFFE53935)
                    )
                    Text(
                        text = "24h",
                        fontSize = 14.sp,
                        color = Color(0xFF1A1A2E).copy(alpha = 0.5f)
                    )
                }
            }
        }
    }
}

@Composable
fun TokenItem(
    token: BundleToken,
    cryptoCurrencyList: List<com.developerstring.nexpay.data.currencies.CryptoCurrency>,
    vibrantColor: Color,
    lightVibrantColor: Color
) {
    val crypto = remember(cryptoCurrencyList, token) {
        cryptoCurrencyList.find { it.symbol.uppercase() == token.symbol.uppercase() }
    }

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = lightVibrantColor.copy(alpha = 0.08f)
        ),
        elevation = CardDefaults.cardElevation(0.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(48.dp)
                        .clip(CircleShape)
                        .background(vibrantColor.copy(alpha = 0.2f)),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = token.symbol.take(1),
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        color = vibrantColor
                    )
                }

                Column {
                    Text(
                        text = token.symbol.uppercase(),
                        fontSize = 16.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = Color(0xFF1A1A2E)
                    )
                    Text(
                        text = crypto?.name ?: token.symbol,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Normal,
                        color = Color(0xFF1A1A2E).copy(alpha = 0.6f)
                    )
                }
            }

            Column(
                horizontalAlignment = Alignment.End,
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Text(
                    text = "$${formatDouble(crypto?.current_price ?: 0.0, 2)}",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = Color(0xFF1A1A2E)
                )
                Row(
                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "${formatDouble(token.percentage, 1)}%",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Medium,
                        color = vibrantColor
                    )
                    val priceChange = crypto?.price_change_percentage_24h ?: 0.0
                    Text(
                        text = "${if (priceChange >= 0) "+" else ""}${formatDouble(priceChange, 2)}%",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Medium,
                        color = if (priceChange >= 0) Color(0xFF4CAF50) else Color(0xFFE53935)
                    )
                }
            }
        }
    }
}

@Composable
fun AllocationChart(
    tokens: List<BundleToken>,
    vibrantColor: Color,
    lightVibrantColor: Color
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(
            containerColor = lightVibrantColor.copy(alpha = 0.08f)
        ),
        elevation = CardDefaults.cardElevation(0.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(20.dp)
        ) {
            Text(
                text = "Allocation Breakdown",
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF1A1A2E)
            )

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(200.dp),
                contentAlignment = Alignment.Center
            ) {
                DonutChart(tokens, vibrantColor, lightVibrantColor)
            }

            // Legend
            Column(
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                tokens.forEachIndexed { index, token ->
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(
                            horizontalArrangement = Arrangement.spacedBy(10.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(12.dp)
                                    .clip(CircleShape)
                                    .background(getChartColor(index, vibrantColor, lightVibrantColor))
                            )
                            Text(
                                text = token.symbol.uppercase(),
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Medium,
                                color = Color(0xFF1A1A2E)
                            )
                        }
                        Text(
                            text = "${token.percentage}%",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = Color(0xFF1A1A2E).copy(alpha = 0.7f)
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun DonutChart(
    tokens: List<BundleToken>,
    vibrantColor: Color,
    lightVibrantColor: Color
) {
    Canvas(modifier = Modifier.size(200.dp)) {
        var startAngle = -90f
        tokens.forEachIndexed { index, token ->
            val sweepAngle = ((token.percentage / 100.0) * 360.0).toFloat()
            drawArc(
                color = getChartColor(index, vibrantColor, lightVibrantColor),
                startAngle = startAngle,
                sweepAngle = sweepAngle,
                useCenter = false,
                topLeft = Offset(size.width * 0.1f, size.height * 0.1f),
                size = Size(size.width * 0.8f, size.height * 0.8f),
                style = Stroke(width = 45f, cap = StrokeCap.Round)
            )
            startAngle += sweepAngle
        }
    }
}

fun getChartColor(index: Int, vibrantColor: Color, lightVibrantColor: Color): Color {
    val alphas = listOf(1.0f, 0.7f, 0.5f, 0.3f, 0.2f)
    return when {
        index % 2 == 0 -> vibrantColor.copy(alpha = alphas[index % alphas.size])
        else -> lightVibrantColor.copy(alpha = alphas[index % alphas.size])
    }
}

// Helper function to format double values
private fun formatDouble(value: Double, decimals: Int = 2): String {
    val multiplier = when (decimals) {
        2 -> 100.0
        3 -> 1000.0
        4 -> 10000.0
        else -> 100.0
    }
    val rounded = kotlin.math.round(value * multiplier) / multiplier

    // Build format string manually for multiplatform compatibility
    val intPart = rounded.toLong()
    // Use absolute value for decimal calculation to avoid negative decimal parts
    val decimalPart = kotlin.math.abs((rounded - intPart) * multiplier).toLong().toString().padStart(decimals, '0')
    return "$intPart.$decimalPart"
}

@Composable
fun FixedTradingActions(
    bundleId: Int,
    navController: NavController,
    lightVibrantColor: Color
) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        color = Color.White,
        shadowElevation = 8.dp
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(start = 20.dp, end = 20.dp, bottom = 50.dp, top = 16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {

                Icon(
                    imageVector = Icons.Rounded.Warning,
                    contentDescription = "Warning",
                    tint = lightVibrantColor,
                )

                Spacer(Modifier.width(15.dp))

                Text(
                    text = "Choose Long to bet on price increase, or Short to bet on price decrease",
                    fontSize = 12.sp,
                    color = Color(0xFF1A1A2E).copy(alpha = 0.6f),
                    modifier = Modifier.padding(horizontal = 4.dp)
                )
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                // Long Button
                Button(
                    onClick = {
                        navController.navigate(ExecuteBundleScreenRoute(bundleId, "LONG"))
                    },
                    modifier = Modifier
                        .weight(1f)
                        .height(52.dp),
                    shape = RoundedCornerShape(16.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFF4CAF50)
                    )
                ) {
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.TrendingUp,
                            contentDescription = "Long",
                            tint = Color.White,
                            modifier = Modifier.size(20.dp)
                        )
                        Text(
                            text = "Long",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                    }
                }

                // Short Button
                Button(
                    onClick = {
                        navController.navigate(ExecuteBundleScreenRoute(bundleId, "SHORT"))
                    },
                    modifier = Modifier
                        .weight(1f)
                        .height(52.dp),
                    shape = RoundedCornerShape(16.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFFE53935)
                    )
                ) {
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.TrendingDown,
                            contentDescription = "Short",
                            tint = Color.White,
                            modifier = Modifier.size(20.dp)
                        )
                        Text(
                            text = "Short",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                    }
                }
            }
        }
    }
}



