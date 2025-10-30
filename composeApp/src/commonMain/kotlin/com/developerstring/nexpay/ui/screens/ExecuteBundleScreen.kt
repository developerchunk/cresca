package com.developerstring.nexpay.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.TrendingDown
import androidx.compose.material.icons.automirrored.filled.TrendingUp
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import com.developerstring.nexpay.data.model.BundleData
import com.developerstring.nexpay.viewmodel.AptosViewModel
import com.developerstring.nexpay.viewmodel.SharedViewModel
import kotlin.math.roundToInt

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ExecuteBundleScreen(
    bundleId: Int,
    tradeType: String,
    sharedViewModel: SharedViewModel,
    navController: NavController,
    aptosViewModel: AptosViewModel
) {
    val bundle = remember { BundleData.allBundles.find { it.id == bundleId } }
    val lightVibrantColor by sharedViewModel.lightVibrantColor
    val vibrantColor by sharedViewModel.vibrantColor
    val darkVibrantColor by sharedViewModel.darkVibrantColor

    val cryptoCurrencyList by sharedViewModel.cryptoCurrencyList.collectAsStateWithLifecycle()

    var payAmount by remember { mutableStateOf("") }
    var leverage by remember { mutableStateOf(1f) }

    // Calculate total bundle value
    val totalBundleValue = remember(cryptoCurrencyList, bundle) {
        bundle?.tokens?.sumOf { token ->
            val crypto = cryptoCurrencyList.find { it.symbol.equals(token.symbol, ignoreCase = true) }
            (crypto?.current_price ?: 0.0) * (token.percentage / 100.0)
        } ?: 0.0
    }

    val currencyValues = remember(cryptoCurrencyList, bundle) {
        bundle?.tokens?.map { token ->
            val crypto = cryptoCurrencyList.find { it.symbol.equals(token.symbol, ignoreCase = true) }
            (crypto?.current_price ?: 0.0) * (token.percentage / 100.0)
        } ?: emptyList()
    }

    val payAmountDouble = payAmount.toDoubleOrNull() ?: 0.0
    val positionSize = payAmountDouble * leverage
    val isValidAmount = payAmountDouble >= 2.0

    val tradeColor = if (tradeType == "LONG") Color(0xFF4CAF50) else Color(0xFFE53935)


    if (bundle == null) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.White),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = "Bundle not found",
                color = darkVibrantColor,
                fontSize = 18.sp
            )
        }
        return
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
            .padding(top = 50.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 20.dp, vertical = 16.dp),
            verticalArrangement = Arrangement.spacedBy(20.dp)
        ) {
            // Header
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
                        .background(lightVibrantColor.copy(alpha = 0.15f))
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "Back",
                        tint = darkVibrantColor,
                        modifier = Modifier.size(20.dp)
                    )
                }

                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = if (tradeType == "LONG") Icons.AutoMirrored.Filled.TrendingUp else Icons.AutoMirrored.Filled.TrendingDown,
                        contentDescription = null,
                        tint = tradeColor,
                        modifier = Modifier.size(24.dp)
                    )
                    Text(
                        text = tradeType,
                        fontSize = 22.sp,
                        fontWeight = FontWeight.Bold,
                        color = tradeColor
                    )
                }

                Spacer(modifier = Modifier.size(40.dp))
            }

            // Bundle Info Card
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(
                    containerColor = lightVibrantColor.copy(alpha = 0.08f)
                ),
                elevation = CardDefaults.cardElevation(0.dp)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(20.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = bundle.name,
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF1A1A2E)
                        )

                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(10.dp))
                                .background(vibrantColor)
                                .padding(horizontal = 12.dp, vertical = 6.dp)
                        ) {
                            Text(
                                text = bundle.symbol,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.White
                            )
                        }
                    }

                    Text(
                        text = "Current Value: $${((totalBundleValue * 100).roundToInt() / 100.0)}",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Medium,
                        color = Color(0xFF1A1A2E).copy(alpha = 0.6f)
                    )
                }
            }

            // Pay Amount Section
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Text(
                    text = "Pay Amount",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF1A1A2E)
                )

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
                            .padding(20.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        BasicTextField(
                            value = payAmount,
                            onValueChange = { value ->
                                if (value.isEmpty() || value.matches(Regex("^\\d*\\.?\\d*$"))) {
                                    payAmount = value
                                }
                            },
                            textStyle = TextStyle(
                                fontSize = 32.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF1A1A2E)
                            ),
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                            cursorBrush = SolidColor(vibrantColor),
                            decorationBox = { innerTextField ->
                                if (payAmount.isEmpty()) {
                                    Text(
                                        text = "0.00",
                                        fontSize = 32.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = Color(0xFF1A1A2E).copy(alpha = 0.3f)
                                    )
                                }
                                innerTextField()
                            },
                            modifier = Modifier.weight(1f)
                        )

                        Text(
                            text = "USDC",
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF1A1A2E).copy(alpha = 0.7f)
                        )
                    }
                }
            }

            // Leverage Slider
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Leverage",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF1A1A2E)
                    )

                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(12.dp))
                            .background(tradeColor.copy(alpha = 0.15f))
                            .padding(horizontal = 16.dp, vertical = 8.dp)
                    ) {
                        Text(
                            text = "${leverage.roundToInt()}x",
                            fontSize = 24.sp,
                            fontWeight = FontWeight.Bold,
                            color = tradeColor
                        )
                    }
                }

                Slider(
                    value = leverage,
                    onValueChange = { leverage = it },
                    valueRange = 1f..150f,
                    modifier = Modifier.fillMaxWidth(),
                    colors = SliderDefaults.colors(
                        thumbColor = tradeColor,
                        activeTrackColor = tradeColor.copy(alpha = 0.7f),
                        inactiveTrackColor = lightVibrantColor.copy(alpha = 0.3f)
                    )
                )

                Text(
                    text = "Slide to adjust leverage (1x-150x)",
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Medium,
                    color = Color(0xFF1A1A2E).copy(alpha = 0.5f)
                )
            }

            // Position Size Display
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Text(
                    text = "Position Size",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF1A1A2E)
                )

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
                            .padding(20.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "${((positionSize * 100).roundToInt() / 100.0)}",
                            fontSize = 32.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF1A1A2E)
                        )

                        Text(
                            text = "USDC",
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF1A1A2E).copy(alpha = 0.7f)
                        )
                    }
                }
            }

            // Warning Text
            if (!isValidAmount && payAmount.isNotEmpty()) {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = Color(0xFFE53935).copy(alpha = 0.12f)
                    ),
                    elevation = CardDefaults.cardElevation(0.dp)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        horizontalArrangement = Arrangement.spacedBy(12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .size(8.dp)
                                .clip(CircleShape)
                                .background(Color(0xFFE53935))
                        )
                        Text(
                            text = "Minimum pay amount is 3 USDC",
                            fontSize = 14.sp,
                            color = Color(0xFFE53935),
                            fontWeight = FontWeight.Medium
                        )
                    }
                }
            }

            // Execute Button
            Button(
                onClick = {
                    // TODO: Implement trade execution logic
                    aptosViewModel.depositCollateral(amountAPT = payAmountDouble) {
                        println("DEPOSIT")
//                        aptosViewModel.viewDashboardExample()
                        println("==========================")
                        println("START CREATE BUCKET=========================")
                        aptosViewModel.createBucket(
                            assets = listOf(
                                "0xae478ff7d83ed072dbc5e264250e67ef58f57c99d89b447efd8a0a2e8b2be76e",
                                "0x5e156f1207d0ebfa19a9eeff00d62a282278fb8719f4fab3a586a0a2c0fffbea",
                                "0xdd89c0e695df0692205912fb69fc290418bed0dbe6e4573d744a6d5e6bab6c13"
                            ),
                            weights = listOf(50, 30, 20),
                            leverage = leverage.roundToInt(),
                        ) {
                            println("CREATED BUCKET=========================")
                            println("ORACLE Start=========================")
                            aptosViewModel.updateOracle(
                                prices = currencyValues,
                            ) {
                                println("UPDATED ORACLE===================")
                                println("OPEN Position start===================")
                                aptosViewModel.openPosition(
                                    bucketId = 0,
                                    isLong = tradeType == "LONG",
                                    marginAPT = 0.001, // Dummy value for margin in APT
                                ) {
                                    println("OPENED POSITION=============")
//                                    aptosViewModel.viewDashboardExample()
                                    println("CLOSE Position start===================")
                                    aptosViewModel.closePosition(
                                        positionId = 0,
                                    ) {
                                        println("CLOSED POSITION================")
                                        aptosViewModel.refreshBalance()
//                                        aptosViewModel.viewDashboardExample()
                                    }
                                }

                            }
                        }
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                enabled = isValidAmount,
                shape = RoundedCornerShape(16.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = tradeColor,
                    disabledContainerColor = lightVibrantColor.copy(alpha = 0.3f)
                )
            ) {
                Text(
                    text = "Open ${tradeType} Position",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = Color.White
                )
            }

            Spacer(modifier = Modifier.height(120.dp))
        }
    }
}

