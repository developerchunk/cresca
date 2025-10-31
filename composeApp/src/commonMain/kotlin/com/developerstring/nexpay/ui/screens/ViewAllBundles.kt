package com.developerstring.nexpay.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.developerstring.nexpay.data.room_db.model.BundleTransaction
import com.developerstring.nexpay.ui.theme.AppColors
import com.developerstring.nexpay.viewmodel.AptosViewModel
import com.developerstring.nexpay.viewmodel.SharedViewModel
import kotlinx.datetime.*
import kotlinx.serialization.Serializable

@Serializable
object ViewAllBundlesRoute

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ViewAllBundlesScreen(
    sharedViewModel: SharedViewModel,
    navController: NavController,
    aptosViewModel: AptosViewModel
) {
    val list = aptosViewModel.listOfBundle

    val bottomSheetState = rememberModalBottomSheetState()
    var showBottomSheet by remember { mutableStateOf(false) }
    var selectedBundle by remember { mutableStateOf<BundleTransaction?>(null) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "All Bundles",
                        fontWeight = FontWeight.Bold,
                        color = Color.Black
                    )
                },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back",
                            tint = Color.Black
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color.White
                )
            )
        },
        containerColor = Color(0xFFF8F9FA)
    ) { paddingValues ->

        if (list.isEmpty()) {
            // Empty state
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Icon(
                    imageVector = Icons.Default.Inbox,
                    contentDescription = null,
                    modifier = Modifier.size(64.dp),
                    tint = Color(0xFF9E9E9E)
                )
                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    text = "No Bundles Yet",
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold,
                    color = Color.Black,
                    textAlign = TextAlign.Center
                )
                Text(
                    text = "Your bundle transactions will appear here",
                    style = MaterialTheme.typography.bodySmall,
                    color = Color(0xFF9E9E9E),
                    textAlign = TextAlign.Center,
                    modifier = Modifier.padding(top = 4.dp)
                )
            }
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(list) { bundle ->
                    BundleCard(
                        bundle = bundle,
                        onClick = {
                            selectedBundle = bundle
                            showBottomSheet = true
                        }
                    )
                }
            }
        }

        // Bottom Sheet for Bundle Details
        if (showBottomSheet && selectedBundle != null) {
            ModalBottomSheet(
                onDismissRequest = {
                    showBottomSheet = false
                    selectedBundle = null
                },
                sheetState = bottomSheetState,
                containerColor = Color.White,
                modifier = Modifier.fillMaxWidth(),
                dragHandle = {
                    Surface(
                        modifier = Modifier
                            .padding(vertical = 8.dp)
                            .width(40.dp)
                            .height(4.dp),
                        shape = RoundedCornerShape(2.dp),
                        color = AppColors.primaryBlue.copy(alpha = 0.3f)
                    ) {}
                }
            ) {
                BundleDetailsBottomSheet(
                    bundle = selectedBundle!!,
                    onDismiss = {
                        showBottomSheet = false
                        selectedBundle = null
                    }
                )
            }
        }
    }
}

@Composable
private fun BundleCard(
    bundle: BundleTransaction,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val bundleDateTime = remember(bundle.timestamp) {
        Instant.fromEpochMilliseconds(bundle.timestamp)
            .toLocalDateTime(TimeZone.currentSystemDefault())
    }

    val totalValue = remember(bundle) {
        (bundle.btcPrice * bundle.btcWeight / 100.0) +
        (bundle.ethPrice * bundle.ethWeight / 100.0) +
        (bundle.solPrice * bundle.solWeight / 100.0)
    }

    Card(
        modifier = modifier
            .fillMaxWidth()
            .clickable { onClick() },
        colors = CardDefaults.cardColors(
            containerColor = Color.White
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        shape = RoundedCornerShape(12.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            // Header Row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(40.dp)
                            .clip(CircleShape)
                            .background(
                                if (bundle.success) AppColors.primaryBlue.copy(alpha = 0.1f)
                                else Color.Red.copy(alpha = 0.1f)
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = if (bundle.success) Icons.Default.CheckCircle else Icons.Default.Error,
                            contentDescription = null,
                            tint = if (bundle.success) AppColors.primaryBlue else Color.Red,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(12.dp))
                    Column {
                        Text(
                            text = "Bundle #${bundle.id}",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = Color.Black
                        )
                        Text(
                            text = if (bundle.success) "Completed" else "Failed",
                            style = MaterialTheme.typography.bodySmall,
                            color = if (bundle.success) AppColors.primaryBlue else Color.Red
                        )
                    }
                }

                Column(
                    horizontalAlignment = Alignment.End
                ) {
                    Text(
                        text = "$"+bundle.amount,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = Color.Black
                    )
                    Text(
                        text = "${bundle.leverage}x Leverage",
                        style = MaterialTheme.typography.bodySmall,
                        color = Color(0xFF666666)
                    )
                    Text(
                        text = "Trade: "+if (bundle.isLong) "Long" else "Short",
                        style = MaterialTheme.typography.bodySmall,
                        color = Color(0xFF666666)
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Bundle Composition Row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                BundleAssetInfo("BTC", bundle.btcWeight)
                BundleAssetInfo("ETH", bundle.ethWeight)
                BundleAssetInfo("SOL", bundle.solWeight)
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Date and Gas Fee Row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "${bundleDateTime.date} • ${bundleDateTime.time.hour}:${bundleDateTime.time.minute.toString().padStart(2, '0')}",
                    style = MaterialTheme.typography.bodySmall,
                    color = Color(0xFF666666)
                )
                Text(
                    text = "Gas: ${bundle.gasFees / 100000000.0} APT",
                    style = MaterialTheme.typography.bodySmall,
                    color = Color(0xFF666666)
                )
            }
        }
    }
}

@Composable
private fun BundleAssetInfo(
    symbol: String,
    weight: Int,
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = symbol,
            style = MaterialTheme.typography.bodySmall,
            fontWeight = FontWeight.Bold,
            color = Color.Black
        )
        Text(
            text = "${weight}%",
            style = MaterialTheme.typography.bodySmall,
            color = AppColors.primaryBlue
        )
//        Text(
//            text = "$${price.toInt()}",
//            style = MaterialTheme.typography.bodySmall,
//            color = Color(0xFF666666)
//        )
    }
}

@Composable
private fun BundleDetailsBottomSheet(
    bundle: BundleTransaction,
    onDismiss: () -> Unit
) {
    val bundleDateTime = remember(bundle.timestamp) {
        Instant.fromEpochMilliseconds(bundle.timestamp)
            .toLocalDateTime(TimeZone.currentSystemDefault())
    }

    val totalValue = remember(bundle) {
        (bundle.btcPrice * bundle.btcWeight / 100.0) +
        (bundle.ethPrice * bundle.ethWeight / 100.0) +
        (bundle.solPrice * bundle.solWeight / 100.0)
    }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
            .padding(bottom = 32.dp)
    ) {
        // Header
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Bundle Details",
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold,
                color = Color.Black
            )
            IconButton(onClick = onDismiss) {
                Icon(
                    imageVector = Icons.Default.Close,
                    contentDescription = "Close",
                    tint = Color.Black
                )
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        // Status Card
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(
                containerColor = if (bundle.success) AppColors.primaryBlue.copy(alpha = 0.1f)
                else Color.Red.copy(alpha = 0.1f)
            )
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = if (bundle.success) Icons.Default.CheckCircle else Icons.Default.Error,
                    contentDescription = null,
                    tint = if (bundle.success) AppColors.primaryBlue else Color.Red,
                    modifier = Modifier.size(24.dp)
                )
                Spacer(modifier = Modifier.width(12.dp))
                Column {
                    Text(
                        text = if (bundle.success) "Transaction Successful" else "Transaction Failed",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = if (bundle.success) AppColors.primaryBlue else Color.Red
                    )
                    Text(
                        text = bundle.message,
                        style = MaterialTheme.typography.bodyMedium,
                        color = Color.Black
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        // Bundle Information
        DetailRow("Bundle ID", "#${bundle.id}")
        DetailRow("Amount", "$${bundle.amount}")
        DetailRow("Trade", if (bundle.isLong) "Long" else "Short")
        DetailRow("Leverage", "${bundle.leverage}x")
        DetailRow("Gas Fees", "${bundle.gasFees / 100000000.0} APT")
        DetailRow("Date", "${bundleDateTime.date}")
        DetailRow("Time", "${bundleDateTime.time.hour}:${bundleDateTime.time.minute.toString().padStart(2, '0')}")

        Spacer(modifier = Modifier.height(24.dp))

        // Asset Allocation
        Text(
            text = "Asset Allocation",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold,
            color = Color.Black
        )

        Spacer(modifier = Modifier.height(16.dp))

        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = Color(0xFFF8F9FA))
        ) {
            Column(
                modifier = Modifier.padding(16.dp)
            ) {
                AssetAllocationRow("Bitcoin (BTC)", bundle.btcWeight, bundle.btcPrice)
                Spacer(modifier = Modifier.height(12.dp))
                AssetAllocationRow("Ethereum (ETH)", bundle.ethWeight, bundle.ethPrice)
                Spacer(modifier = Modifier.height(12.dp))
                AssetAllocationRow("Solana (SOL)", bundle.solWeight, bundle.solPrice)
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        // Transaction Hash
        Text(
            text = "Transaction Hash",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold,
            color = Color.Black
        )

        Spacer(modifier = Modifier.height(8.dp))

        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = Color(0xFFF8F9FA))
        ) {
            Text(
                text = bundle.hex,
                style = MaterialTheme.typography.bodyMedium,
                color = Color.Black,
                modifier = Modifier.padding(16.dp),
                maxLines = 3,
                overflow = TextOverflow.Ellipsis
            )
        }
    }
}

@Composable
private fun DetailRow(
    label: String,
    value: String,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.bodyMedium,
            color = Color(0xFF666666)
        )
        Text(
            text = value,
            style = MaterialTheme.typography.bodyMedium,
            fontWeight = FontWeight.Medium,
            color = Color.Black
        )
    }
}

@Composable
private fun AssetAllocationRow(
    name: String,
    weight: Int,
    price: Double
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column {
            Text(
                text = name,
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.Medium,
                color = Color.Black
            )
            Text(
                text = "$${price.toString().take(8)}",
                style = MaterialTheme.typography.bodySmall,
                color = Color(0xFF666666)
            )
        }

        Text(
            text = "${weight}%",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold,
            color = AppColors.primaryBlue
        )
    }
}
