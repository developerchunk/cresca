package com.developerstring.nexpay.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import com.developerstring.nexpay.data.model.BundleData
import com.developerstring.nexpay.ui.theme.AppColors
import com.developerstring.nexpay.viewmodel.AptosViewModel
import com.developerstring.nexpay.viewmodel.BundleTransaction
import com.developerstring.nexpay.viewmodel.SharedViewModel
import kotlinx.serialization.Serializable
import kotlinx.datetime.Instant
import kotlin.math.abs

@Serializable
object BundleSuccessScreenRoute

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BundleSuccessScreen(
    sharedViewModel: SharedViewModel,
    navController: NavController,
    aptosViewModel: AptosViewModel
) {

    LaunchedEffect(Unit) {aptosViewModel.getLastID()}
    val lastID = aptosViewModel.lastID.value

    val bundleTransaction by aptosViewModel.bundleTransaction.collectAsState()

    val bundle = remember { BundleData.allBundles.find { it.id ==  lastID} }
    val cryptoCurrencyList by sharedViewModel.cryptoCurrencyList.collectAsStateWithLifecycle()

    // Use actual transaction data from aptosViewModel
    val transactionHash = bundleTransaction.hex
    val transactionTime = remember(bundleTransaction.timestamp) {
        if (bundleTransaction.timestamp > 0) {
            val instant = kotlinx.datetime.Instant.fromEpochSeconds(bundleTransaction.timestamp)
            val dateTime = instant.toString().replace("T", " ").replace("Z", " UTC")
            dateTime
        } else {
            "Pending..."
        }
    }

    // Calculate transaction amount and USD equivalent based on bundle composition and current prices
    val (transactionAmount, usdAmount) = remember(bundleTransaction, cryptoCurrencyList, bundle) {
        if (bundle != null && cryptoCurrencyList.isNotEmpty()) {
            // Calculate bundle value based on current prices and weights from BundleTransaction
            val btcValue = (bundleTransaction.btcWeight / 100.0) * bundleTransaction.btcPrice
            val ethValue = (bundleTransaction.ethWeight / 100.0) * bundleTransaction.ethPrice
            val solValue = (bundleTransaction.solWeight / 100.0) * bundleTransaction.solPrice
            val totalUsdValue = btcValue + ethValue + solValue

            // Convert to APT (assuming 1 APT = $8.50 for demo, replace with actual APT price)
            val aptPrice = 8.50 // This should come from actual market data
            val aptAmount = totalUsdValue / aptPrice

            Pair( aptAmount,  totalUsdValue)
        } else {
            Pair("0.0000", "0.00")
        }
    }

    val networkFee = remember(bundleTransaction.gasFees) {
       bundleTransaction.gasFees / 100_000_000.0 // Convert from octas to APT
    }

    // Calculate bundle value and performance
    val totalBundleValue = remember(cryptoCurrencyList, bundle) {
        bundle?.tokens?.sumOf { token ->
            val crypto = cryptoCurrencyList.find { it.symbol.uppercase() == token.symbol.uppercase() }
            (crypto?.current_price ?: 0.0) * (token.percentage / 100.0)
        } ?: 0.0
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
            .statusBarsPadding()
    ) {
        // Top App Bar
        TopAppBar(
            title = { },
            navigationIcon = {
                IconButton(onClick = { navController.navigateUp() }) {
                    Icon(
                        imageVector = Icons.Default.Close,
                        contentDescription = "Close",
                        tint = Color.Black
                    )
                }
            },
            actions = {
                IconButton(onClick = { /* Share transaction */ }) {
                    Icon(
                        imageVector = Icons.Default.Share,
                        contentDescription = "Share",
                        tint = Color.Black
                    )
                }
            },
            colors = TopAppBarDefaults.topAppBarColors(
                containerColor = Color.White
            )
        )

        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Success Icon and Status
            Box(
                modifier = Modifier
                    .size(80.dp)
                    .background(
                        Color(0xFF4CAF50).copy(alpha = 0.1f),
                        CircleShape
                    ),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.CheckCircle,
                    contentDescription = "Success",
                    tint = Color(0xFF4CAF50),
                    modifier = Modifier.size(48.dp)
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = "Transaction Successful!",
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                color = Color.Black,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "Success",
                fontSize = 16.sp,
                color = Color.Gray,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(32.dp))

            // Transaction Details Card
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = Color(0xFFF8F9FA)),
                elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
            ) {
                Column(
                    modifier = Modifier.padding(20.dp)
                ) {
                    Text(
                        text = "Transaction Details",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = Color.Black
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    // Transaction Hash
                    DetailRow(
                        label = "Transaction Hash",
                        value = "${transactionHash.take(10)}...${transactionHash.takeLast(10)}",
                        valueColor = AppColors.primaryBlue
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    // Transaction Time
                    DetailRow(
                        label = "Time",
                        value = transactionTime
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    // Amount
                    DetailRow(
                        label = "Collateral Amount",
                        value = "${bundleTransaction.amount}"
                    )

                    // TODO
                    DetailRow(
                        label = "Leverage",
                        value = "${bundleTransaction.leverage}"
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    // Network Fee
                    DetailRow(
                        label = "Network Fee",
                        value = "$networkFee APT"
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    Spacer(modifier = Modifier.height(12.dp))

                    // Transaction Status
                    DetailRow(
                        label = "Status",
                        value = if (bundleTransaction.success) "Success" else "Failed",
                        valueColor = if (bundleTransaction.success) Color(0xFF4CAF50) else Color(0xFFE53E3E)
                    )

                    if (bundleTransaction.message.isNotEmpty()) {
                        Spacer(modifier = Modifier.height(12.dp))

                        DetailRow(
                            label = "Message",
                            value = bundleTransaction.message
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Bundle Information Card
            bundle?.let { bundleInfo ->
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFFF8F9FA)),
                    elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
                ) {
                    Column(
                        modifier = Modifier.padding(20.dp)
                    ) {
                        Text(
                            text = "Bundle Information",
                            fontSize = 18.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = Color.Black
                        )

                        Spacer(modifier = Modifier.height(16.dp))

                        // Bundle Name
                        DetailRow(
                            label = "Bundle Name",
                            value = bundleInfo.name
                        )

                        Spacer(modifier = Modifier.height(12.dp))

                        // Bundle Symbol
                        DetailRow(
                            label = "Symbol",
                            value = bundleInfo.symbol
                        )

                        Spacer(modifier = Modifier.height(12.dp))

                        // Bundle Value - using actual transaction data
                        DetailRow(
                            label = "Bundle Value",
                            value = "$${
                                (bundleTransaction.btcWeight / 100.0) * bundleTransaction.btcPrice +
                                (bundleTransaction.ethWeight / 100.0) * bundleTransaction.ethPrice +
                                (bundleTransaction.solWeight / 100.0) * bundleTransaction.solPrice}"
                        )

                        Spacer(modifier = Modifier.height(12.dp))

                        // Transaction Prices
                        DetailRow(
                            label = "BTC Price at Transaction",
                            value = "${bundleTransaction.btcPrice}"
                        )

                        Spacer(modifier = Modifier.height(8.dp))

                        DetailRow(
                            label = "ETH Price at Transaction",
                            value = "$${bundleTransaction.ethPrice}"
                        )

                        Spacer(modifier = Modifier.height(8.dp))

                        DetailRow(
                            label = "SOL Price at Transaction",
                            value = "${bundleTransaction.solPrice}"
                        )

                        Spacer(modifier = Modifier.height(16.dp))

                        // Token Composition
                        Text(
                            text = "Token Composition",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Medium,
                            color = Color.Black
                        )

                        Spacer(modifier = Modifier.height(12.dp))

                        // Use actual transaction weights
                        val actualTokens = listOf(
                            Pair("BTC", bundleTransaction.btcWeight),
                            Pair("ETH", bundleTransaction.ethWeight),
                            Pair("SOL", bundleTransaction.solWeight)
                        )

                        actualTokens.forEach { (symbol, weight) ->
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 4.dp),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Box(
                                        modifier = Modifier
                                            .size(8.dp)
                                            .background(
                                                when (symbol) {
                                                    "BTC" -> Color(0xFFF7931A)
                                                    "ETH" -> Color(0xFF627EEA)
                                                    "SOL" -> Color(0xFF9945FF)
                                                    else -> AppColors.primaryBlue
                                                },
                                                CircleShape
                                            )
                                    )

                                    Spacer(modifier = Modifier.width(8.dp))

                                    Text(
                                        text = symbol,
                                        fontSize = 14.sp,
                                        color = Color.Black,
                                        fontWeight = FontWeight.Medium
                                    )
                                }

                                Text(
                                    text = "$weight%",
                                    fontSize = 14.sp,
                                    color = Color.Gray
                                )
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}

@Composable
private fun DetailRow(
    label: String,
    value: String,
    valueColor: Color = Color.Black
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.Top
    ) {
        Text(
            text = label,
            fontSize = 14.sp,
            color = Color.Gray,
            modifier = Modifier.weight(1f)
        )

        Spacer(modifier = Modifier.width(16.dp))

        Text(
            text = value,
            fontSize = 14.sp,
            color = valueColor,
            fontWeight = FontWeight.Medium,
            textAlign = TextAlign.End,
            modifier = Modifier.weight(1f)
        )
    }
}