package com.developerstring.nexpay.ui.transaction

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.CallMade
import androidx.compose.material.icons.filled.*
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.developerstring.nexpay.data.room_db.model.BundleTransaction
import com.developerstring.nexpay.data.room_db.model.Transaction
import com.developerstring.nexpay.data.room_db.model.TransactionStatus
import com.developerstring.nexpay.viewmodel.AptosViewModel
import com.developerstring.nexpay.viewmodel.SharedViewModel
import kotlinx.datetime.*
import kotlinx.serialization.Serializable

@Serializable
object ViewAllTransactionRoute

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ViewAllTransactionScreen(
    sharedViewModel: SharedViewModel,
    navController: NavController,
    aptosViewModel: AptosViewModel
) {
    // Get color theme from shared view model
    val lightVibrantColor by sharedViewModel.lightVibrantColor
    val vibrantColor by sharedViewModel.vibrantColor
    val darkVibrantColor by sharedViewModel.darkVibrantColor

    // Get bundle transactions
    aptosViewModel.getAllBundleTransactions()
    val listOfBundles by aptosViewModel.getBundleTransactions.collectAsState()

    // Get regular transactions
    val sampleTransactions by sharedViewModel.getAllTransactions().collectAsState(initial = emptyList())

    // Tab state
    var selectedTab by remember { mutableStateOf(0) }
    val tabs = listOf("All Transactions", "Bundle Transactions")

    // Filter states
    var selectedFilter by remember { mutableStateOf("All") }
    var selectedCrypto by remember { mutableStateOf("All") }

    val filteredTransactions = remember(selectedFilter, selectedCrypto, sampleTransactions) {
        sampleTransactions.filter { transaction ->
            val statusMatch = when (selectedFilter) {
                "All" -> true
                "Completed" -> transaction.status == TransactionStatus.COMPLETED
                "Pending" -> transaction.status == TransactionStatus.PENDING
                "Failed" -> transaction.status == TransactionStatus.FAILED
                "Scheduled" -> transaction.status == TransactionStatus.SCHEDULED
                else -> true
            }
            val cryptoMatch = when (selectedCrypto) {
                "All" -> true
                else -> transaction.cryptoType == selectedCrypto
            }
            statusMatch && cryptoMatch
        }.sortedByDescending { it.createdAt }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                brush = Brush.verticalGradient(
                    colors = listOf(
                        darkVibrantColor.copy(alpha = 0.05f),
                        lightVibrantColor.copy(alpha = 0.02f),
                        Color.White
                    )
                )
            )
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(top = 40.dp)
        ) {
            // Phantom-style Header
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp, vertical = 16.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    IconButton(
                        onClick = { navController.navigateUp() },
                        modifier = Modifier
                            .size(44.dp)
                            .background(
                                color = lightVibrantColor.copy(alpha = 0.1f),
                                shape = CircleShape
                            )
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back",
                            tint = vibrantColor,
                            modifier = Modifier.size(20.dp)
                        )
                    }

                    Spacer(modifier = Modifier.weight(1f))

                    IconButton(
                        onClick = { /* TODO: Implement search */ },
                        modifier = Modifier
                            .size(44.dp)
                            .background(
                                color = lightVibrantColor.copy(alpha = 0.1f),
                                shape = CircleShape
                            )
                    ) {
                        Icon(
                            imageVector = Icons.Default.Search,
                            contentDescription = "Search",
                            tint = vibrantColor,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))

                // Title and Description
                Text(
                    text = "Activity",
                    style = MaterialTheme.typography.headlineLarge.copy(
                        fontWeight = FontWeight.ExtraBold,
                        fontSize = 32.sp
                    ),
                    color = Color(0xFF1A1B23)
                )

                Text(
                    text = "View all your transaction history",
                    style = MaterialTheme.typography.bodyLarge.copy(
                        fontSize = 16.sp
                    ),
                    color = Color(0xFF6C757D),
                    modifier = Modifier.padding(top = 4.dp)
                )

                Spacer(modifier = Modifier.height(32.dp))

                // Floating Tab Layout
                FloatingTabLayout(
                    tabs = tabs,
                    selectedTab = selectedTab,
                    onTabSelected = { selectedTab = it },
                    vibrantColor = vibrantColor,
                    lightVibrantColor = lightVibrantColor
                )
            }

            // Content based on selected tab
            AnimatedContent(
                targetState = selectedTab,
                transitionSpec = {
                    slideInHorizontally(
                        initialOffsetX = { if (targetState > initialState) 300 else -300 },
                        animationSpec = tween(300, easing = EaseInOutCubic)
                    ) + fadeIn(animationSpec = tween(300)) togetherWith
                    slideOutHorizontally(
                        targetOffsetX = { if (targetState > initialState) -300 else 300 },
                        animationSpec = tween(300, easing = EaseInOutCubic)
                    ) + fadeOut(animationSpec = tween(300))
                },
                label = "tab_content"
            ) { tabIndex ->
                when (tabIndex) {
                    0 -> {
                        // All Transactions Tab
                        TransactionList(
                            transactions = filteredTransactions,
                            selectedFilter = selectedFilter,
                            selectedCrypto = selectedCrypto,
                            onFilterChange = { selectedFilter = it },
                            onCryptoChange = { selectedCrypto = it },
                            vibrantColor = vibrantColor,
                            lightVibrantColor = lightVibrantColor,
                            darkVibrantColor = darkVibrantColor
                        )
                    }
                    1 -> {
                        // Bundle Transactions Tab
                        BundleTransactionList(
                            bundles = listOfBundles,
                            vibrantColor = vibrantColor,
                            lightVibrantColor = lightVibrantColor,
                            darkVibrantColor = darkVibrantColor
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun FloatingTabLayout(
    tabs: List<String>,
    selectedTab: Int,
    onTabSelected: (Int) -> Unit,
    vibrantColor: Color,
    lightVibrantColor: Color,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier
            .fillMaxWidth()
            .height(56.dp),
        color = lightVibrantColor.copy(alpha = 0.1f),
        shape = RoundedCornerShape(28.dp),
        border = BorderStroke(1.dp, lightVibrantColor.copy(alpha = 0.2f))
    ) {
        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(4.dp),
            horizontalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            tabs.forEachIndexed { index, tab ->
                val isSelected = selectedTab == index
                val animatedWeight by animateFloatAsState(
                    targetValue = if (isSelected) 1f else 1f,
                    animationSpec = tween(300, easing = EaseInOutCubic),
                    label = "tab_weight"
                )

                Surface(
                    modifier = Modifier
                        .weight(animatedWeight)
                        .fillMaxHeight()
                        .clickable(
                            indication = null,
                            interactionSource = remember { MutableInteractionSource() }
                        ) { onTabSelected(index) },
                    color = if (isSelected) vibrantColor else Color.Transparent,
                    shape = RoundedCornerShape(24.dp)
                ) {
                    Box(
                        contentAlignment = Alignment.Center,
                        modifier = Modifier.fillMaxSize()
                    ) {
                        AnimatedContent(
                            targetState = isSelected,
                            transitionSpec = {
                                fadeIn(animationSpec = tween(200)) togetherWith
                                fadeOut(animationSpec = tween(200))
                            },
                            label = "tab_text"
                        ) { selected ->
                            Text(
                                text = tab,
                                style = MaterialTheme.typography.titleSmall.copy(
                                    fontWeight = if (selected) FontWeight.Bold else FontWeight.Medium,
                                    fontSize = 14.sp
                                ),
                                color = if (selected) Color.White else vibrantColor,
                                textAlign = TextAlign.Center
                            )
                        }
                    }
                }
            }
        }
    }
}

@Suppress("UNUSED_PARAMETER")
@Composable
private fun TransactionList(
    transactions: List<Transaction>,
    selectedFilter: String,
    selectedCrypto: String,
    onFilterChange: (String) -> Unit,
    onCryptoChange: (String) -> Unit,
    vibrantColor: Color,
    lightVibrantColor: Color,
    darkVibrantColor: Color,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier.fillMaxSize()) {
        // Filter chips
        LazyRow(
            modifier = Modifier.padding(horizontal = 20.dp, vertical = 12.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(listOf("All", "Completed", "Pending", "Failed", "Scheduled")) { status ->
                FilterChip(
                    selected = selectedFilter == status,
                    onClick = { onFilterChange(status) },
                    label = {
                        Text(
                            text = status,
                            style = MaterialTheme.typography.labelSmall.copy(
                                fontWeight = FontWeight.Medium,
                                fontSize = 12.sp
                            )
                        )
                    },
                    colors = FilterChipDefaults.filterChipColors(
                        containerColor = Color.Transparent,
                        selectedContainerColor = vibrantColor.copy(alpha = 0.15f),
                        labelColor = Color(0xFF6C757D),
                        selectedLabelColor = vibrantColor
                    ),
                    border = FilterChipDefaults.filterChipBorder(
                        borderColor = lightVibrantColor.copy(alpha = 0.3f),
                        selectedBorderColor = vibrantColor.copy(alpha = 0.4f),
                        borderWidth = 1.dp,
                        enabled = true,
                        selected = selectedFilter == status
                    )
                )
            }
        }

        if (transactions.isEmpty()) {
            // Empty state
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(32.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Box(
                    modifier = Modifier
                        .size(80.dp)
                        .background(
                            color = lightVibrantColor.copy(alpha = 0.1f),
                            shape = CircleShape
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Receipt,
                        contentDescription = null,
                        modifier = Modifier.size(40.dp),
                        tint = vibrantColor.copy(alpha = 0.6f)
                    )
                }
                Spacer(modifier = Modifier.height(20.dp))
                Text(
                    text = "No transactions yet",
                    style = MaterialTheme.typography.titleLarge.copy(
                        fontWeight = FontWeight.Bold,
                        fontSize = 20.sp
                    ),
                    color = Color(0xFF1A1B23),
                    textAlign = TextAlign.Center
                )
                Text(
                    text = "Your transaction history will appear here",
                    style = MaterialTheme.typography.bodyMedium,
                    color = Color(0xFF6C757D),
                    textAlign = TextAlign.Center,
                    modifier = Modifier.padding(top = 8.dp)
                )
            }
        } else {
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(bottom = 120.dp, start = 16.dp, end = 16.dp, top = 16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp),
            ) {
                itemsIndexed(transactions.reversed()) { index, transaction ->
                    val animationDelay = (index * 50).coerceAtMost(300)
                    var visible by remember { mutableStateOf(false) }

                    LaunchedEffect(Unit) {
                        kotlinx.coroutines.delay(animationDelay.toLong())
                        visible = true
                    }

                    AnimatedVisibility(
                        visible = visible,
                        enter = slideInVertically(
                            initialOffsetY = { it / 2 },
                            animationSpec = tween(400, easing = EaseOutCubic)
                        ) + fadeIn(animationSpec = tween(400))
                    ) {
                        TransactionCard(
                            transaction = transaction,
                            vibrantColor = vibrantColor,
                            lightVibrantColor = lightVibrantColor,
                            darkVibrantColor = darkVibrantColor,
                            onClick = {
                                // TODO: Navigate to transaction details
                            }
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun BundleTransactionList(
    bundles: List<BundleTransaction>,
    vibrantColor: Color,
    lightVibrantColor: Color,
    darkVibrantColor: Color,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier.fillMaxSize()) {
        if (bundles.isEmpty()) {
            // Empty state
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(32.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Box(
                    modifier = Modifier
                        .size(80.dp)
                        .background(
                            color = lightVibrantColor.copy(alpha = 0.1f),
                            shape = CircleShape
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.AccountBalanceWallet,
                        contentDescription = null,
                        modifier = Modifier.size(40.dp),
                        tint = vibrantColor.copy(alpha = 0.6f)
                    )
                }
                Spacer(modifier = Modifier.height(20.dp))
                Text(
                    text = "No bundle trades yet",
                    style = MaterialTheme.typography.titleLarge.copy(
                        fontWeight = FontWeight.Bold,
                        fontSize = 20.sp
                    ),
                    color = Color(0xFF1A1B23),
                    textAlign = TextAlign.Center
                )
                Text(
                    text = "Your bundle trading history will appear here",
                    style = MaterialTheme.typography.bodyMedium,
                    color = Color(0xFF6C757D),
                    textAlign = TextAlign.Center,
                    modifier = Modifier.padding(top = 8.dp)
                )
            }
        } else {
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(bottom = 120.dp, start = 16.dp, end = 16.dp, top = 16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp),
                 
            ) {
                itemsIndexed(bundles.reversed()) { index, bundle ->
                    val animationDelay = (index * 50).coerceAtMost(300)
                    var visible by remember { mutableStateOf(false) }

                    LaunchedEffect(Unit) {
                        kotlinx.coroutines.delay(animationDelay.toLong())
                        visible = true
                    }

                    AnimatedVisibility(
                        visible = visible,
                        enter = slideInVertically(
                            initialOffsetY = { it / 2 },
                            animationSpec = tween(400, easing = EaseOutCubic)
                        ) + fadeIn(animationSpec = tween(400))
                    ) {
                        BundleCard(
                            bundle = bundle,
                            vibrantColor = vibrantColor,
                            lightVibrantColor = lightVibrantColor,
                            darkVibrantColor = darkVibrantColor,
                            onClick = {
                                // TODO: Navigate to bundle details
                            }
                        )
                    }
                }
            }
        }
    }
}

@Suppress("UNUSED_PARAMETER")
@Composable
private fun TransactionCard(
    transaction: Transaction,
    vibrantColor: Color,
    lightVibrantColor: Color,
    darkVibrantColor: Color,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val transactionDateTime = remember(transaction.executedAt, transaction.createdAt) {
        val timestamp = transaction.executedAt ?: transaction.createdAt
        Instant.fromEpochMilliseconds(timestamp)
            .toLocalDateTime(TimeZone.currentSystemDefault())
    }

    val scheduledDateTime = remember(transaction.scheduledAt) {
        transaction.scheduledAt?.let { timestamp ->
            Instant.fromEpochMilliseconds(timestamp)
                .toLocalDateTime(TimeZone.currentSystemDefault())
        }
    }

    Card(
        modifier = modifier
            .fillMaxWidth()
            .clickable { onClick() },
        colors = CardDefaults.cardColors(
            containerColor = lightVibrantColor.copy(0.1f)
        ),
        shape = RoundedCornerShape(16.dp),
        border = BorderStroke(
            width = 1.dp,
            color = lightVibrantColor.copy(0.05f)
        )
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            // Header row with crypto type and status
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Crypto type badge
                Surface(
                    color = getCryptoColor(transaction.cryptoType).copy(alpha = 0.1f),
                    shape = RoundedCornerShape(8.dp),
                    modifier = Modifier.padding(end = 8.dp)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(8.dp)
                                .background(
                                    color = getCryptoColor(transaction.cryptoType),
                                    shape = CircleShape
                                )
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = transaction.cryptoType.uppercase(),
                            style = MaterialTheme.typography.labelSmall.copy(
                                fontWeight = FontWeight.Bold,
                                fontSize = 10.sp
                            ),
                            color = getCryptoColor(transaction.cryptoType)
                        )
                    }
                }

                // Status indicator
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(8.dp)
                            .background(
                                color = getStatusColor(transaction.status),
                                shape = CircleShape
                            )
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = getStatusText(transaction.status),
                        style = MaterialTheme.typography.labelSmall.copy(
                            fontWeight = FontWeight.Medium,
                            fontSize = 11.sp
                        ),
                        color = Color(0xFF6C757D)
                    )
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Transaction direction and address
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = if (transaction.status == TransactionStatus.COMPLETED)
                        Icons.AutoMirrored.Filled.CallMade else Icons.Default.Schedule,
                    contentDescription = null,
                    modifier = Modifier.size(16.dp),
                    tint = Color(0xFF6C757D)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "To: ",
                    style = MaterialTheme.typography.bodySmall.copy(
                        fontWeight = FontWeight.Medium,
                        fontSize = 12.sp
                    ),
                    color = Color(0xFF6C757D)
                )
                Text(
                    text = "${transaction.toWalletAddress.take(8)}...${transaction.toWalletAddress.takeLast(6)}",
                    style = MaterialTheme.typography.bodySmall.copy(
                        fontWeight = FontWeight.Medium,
                        fontSize = 12.sp
                    ),
                    color = Color(0xFF1A1B23),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }

            // Gas fee if available
            if (transaction.gasFee.isNotEmpty()) {
                Spacer(modifier = Modifier.height(8.dp))
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.LocalGasStation,
                        contentDescription = null,
                        modifier = Modifier.size(14.dp),
                        tint = Color(0xFF9E9E9E)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Gas: ${transaction.gasFee} ${transaction.cryptoType}",
                        style = MaterialTheme.typography.labelSmall.copy(
                            fontSize = 10.sp
                        ),
                        color = Color(0xFF9E9E9E)
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Amount and date/time row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Bottom
            ) {
                // Amount
                Column {
                    Text(
                        text = "Amount",
                        style = MaterialTheme.typography.labelSmall.copy(
                            fontWeight = FontWeight.Medium,
                            fontSize = 10.sp
                        ),
                        color = Color(0xFF6C757D)
                    )
                    Text(
                        text = "${transaction.amount} ${transaction.cryptoType}",
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = FontWeight.Bold,
                            fontSize = 16.sp
                        ),
                        color = Color(0xFF1A1B23)
                    )
                }

                // Date and time
                Column(
                    horizontalAlignment = Alignment.End
                ) {
                    val displayDateTime = if (transaction.status == TransactionStatus.SCHEDULED)
                        scheduledDateTime else transactionDateTime

                    displayDateTime?.let { dateTime ->
                        Text(
                            text = "${dateTime.date.month.number}/${dateTime.date.dayOfMonth}/${dateTime.date.year}",
                            style = MaterialTheme.typography.bodySmall.copy(
                                fontWeight = FontWeight.Medium,
                                fontSize = 12.sp
                            ),
                            color = Color(0xFF1A1B23)
                        )
                        Text(
                            text = "${dateTime.time.hour.toString().padStart(2, '0')}:${
                                dateTime.time.minute.toString().padStart(2, '0')
                            }",
                            style = MaterialTheme.typography.labelSmall.copy(
                                fontWeight = FontWeight.Medium,
                                fontSize = 11.sp
                            ),
                            color = Color(0xFF6C757D)
                        )
                    }

                    // Show "Scheduled" label for scheduled transactions
                    if (transaction.status == TransactionStatus.SCHEDULED) {
                        Text(
                            text = "Scheduled",
                            style = MaterialTheme.typography.labelSmall.copy(
                                fontWeight = FontWeight.Medium,
                                fontSize = 9.sp
                            ),
                            color = Color(0xFFFFA726)
                        )
                    }
                }
            }

            // Gradient line at bottom
            Spacer(modifier = Modifier.height(14.dp))
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(2.dp)
                    .background(
                        brush = Brush.horizontalGradient(
                            colors = listOf(
                                getCryptoColor(transaction.cryptoType).copy(alpha = 0.6f),
                                getStatusColor(transaction.status).copy(alpha = 0.4f),
                                Color.Transparent
                            )
                        ),
                        shape = RoundedCornerShape(1.dp)
                    )
            )
        }
    }
}

// Helper functions for colors and status text
private fun getCryptoColor(cryptoType: String): Color {
    return when (cryptoType.uppercase()) {
        "APT" -> Color(0xFF7C3AED)
        "ETH" -> Color(0xFF06B6D4)
        "BTC" -> Color(0xFFF59E0B)
        else -> Color(0xFF6C757D)
    }
}

private fun getStatusColor(status: TransactionStatus): Color {
    return when (status) {
        TransactionStatus.SCHEDULED -> Color(0xFFFFA726)
        TransactionStatus.PENDING -> Color(0xFF42A5F5)
        TransactionStatus.PROCESSING -> Color(0xFF9C27B0)
        TransactionStatus.COMPLETED -> Color(0xFF66BB6A)
        TransactionStatus.FAILED -> Color(0xFFEF5350)
        TransactionStatus.CANCELLED -> Color(0xFF9E9E9E)
    }
}

private fun getStatusText(status: TransactionStatus): String {
    return when (status) {
        TransactionStatus.SCHEDULED -> "Scheduled"
        TransactionStatus.PENDING -> "Pending"
        TransactionStatus.PROCESSING -> "Processing"
        TransactionStatus.COMPLETED -> "Completed"
        TransactionStatus.FAILED -> "Failed"
        TransactionStatus.CANCELLED -> "Cancelled"
    }
}

@Composable
private fun BundleCard(
    bundle: BundleTransaction,
    vibrantColor: Color,
    lightVibrantColor: Color,
    darkVibrantColor: Color,
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

    Surface(
        modifier = modifier
            .fillMaxWidth()
            .clickable { onClick() },
        color = lightVibrantColor.copy(0.1f),
        shape = RoundedCornerShape(20.dp),
        border = BorderStroke(1.dp, lightVibrantColor.copy(alpha = 0.05f))
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp)
        ) {
            // Header with status and bundle ID
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Bundle ID and type
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(48.dp)
                            .background(
                                brush = Brush.radialGradient(
                                    colors = listOf(
                                        if (bundle.success) lightVibrantColor.copy(alpha = 0.3f) else Color(0xFFFFEBEE),
                                        if (bundle.success) lightVibrantColor.copy(alpha = 0.1f) else Color(0xFFFFCDD2)
                                    )
                                ),
                                shape = CircleShape
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = if (bundle.success) Icons.Default.AccountBalanceWallet else Icons.Default.Error,
                            contentDescription = null,
                            tint = if (bundle.success) vibrantColor else Color(0xFFE57373),
                            modifier = Modifier.size(24.dp)
                        )
                    }

                    Spacer(modifier = Modifier.width(16.dp))

                    Column {
                        Text(
                            text = "Bundle #${bundle.id}",
                            style = MaterialTheme.typography.titleMedium.copy(
                                fontWeight = FontWeight.Bold,
                                fontSize = 18.sp
                            ),
                            color = Color(0xFF1A1B23)
                        )

                        Text(
                            text = "Crypto Bundle Trade",
                            style = MaterialTheme.typography.bodySmall.copy(
                                fontSize = 12.sp
                            ),
                            color = Color(0xFF6C757D),
                            modifier = Modifier.padding(top = 2.dp)
                        )
                    }
                }

                // Status badge
                Surface(
                    color = if (bundle.success) Color(0xFF10B981).copy(alpha = 0.1f) else Color(0xFFEF4444).copy(alpha = 0.1f),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(6.dp)
                                .background(
                                    color = if (bundle.success) Color(0xFF10B981) else Color(0xFFEF4444),
                                    shape = CircleShape
                                )
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = if (bundle.success) "Success" else "Failed",
                            style = MaterialTheme.typography.labelSmall.copy(
                                fontWeight = FontWeight.Bold,
                                fontSize = 11.sp
                            ),
                            color = if (bundle.success) Color(0xFF10B981) else Color(0xFFEF4444)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            // Bundle composition
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                // BTC
                BundleCryptoItem(
                    symbol = "BTC",
                    weight = bundle.btcWeight.toDouble(),
                    price = bundle.btcPrice,
                    color = Color(0xFFF59E0B)
                )

                // ETH
                BundleCryptoItem(
                    symbol = "ETH",
                    weight = bundle.ethWeight.toDouble(),
                    price = bundle.ethPrice,
                    color = Color(0xFF06B6D4)
                )

                // SOL
                BundleCryptoItem(
                    symbol = "SOL",
                    weight = bundle.solWeight.toDouble(),
                    price = bundle.solPrice,
                    color = Color(0xFF7C3AED)
                )
            }

            Spacer(modifier = Modifier.height(20.dp))

            // Total value and timestamp
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Bottom
            ) {
                Column {
                    Text(
                        text = "Total Value",
                        style = MaterialTheme.typography.labelMedium.copy(
                            fontWeight = FontWeight.Medium,
                            fontSize = 12.sp
                        ),
                        color = Color(0xFF6C757D)
                    )
                    Text(
                        text = bundle.amount,
                        style = MaterialTheme.typography.headlineSmall.copy(
                            fontWeight = FontWeight.ExtraBold,
                            fontSize = 24.sp
                        ),
                        color = Color(0xFF1A1B23)
                    )
                }

                Column(
                    horizontalAlignment = Alignment.End
                ) {
                    Text(
                        text = "${bundleDateTime.date.month.number}/${bundleDateTime.date.dayOfMonth}/${bundleDateTime.date.year}",
                        style = MaterialTheme.typography.bodyMedium.copy(
                            fontWeight = FontWeight.Medium,
                            fontSize = 14.sp
                        ),
                        color = Color(0xFF1A1B23)
                    )
                    Text(
                        text = "${bundleDateTime.time.hour.toString().padStart(2, '0')}:${
                            bundleDateTime.time.minute.toString().padStart(2, '0')
                        }",
                        style = MaterialTheme.typography.labelMedium.copy(
                            fontWeight = FontWeight.Medium,
                            fontSize = 12.sp
                        ),
                        color = Color(0xFF6C757D)
                    )
                }
            }

            // Animated gradient line
            Spacer(modifier = Modifier.height(16.dp))
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(3.dp)
                    .background(
                        brush = Brush.horizontalGradient(
                            colors = listOf(
                                vibrantColor.copy(alpha = 0.8f),
                                lightVibrantColor.copy(alpha = 0.6f),
                                darkVibrantColor.copy(alpha = 0.4f),
                                Color.Transparent
                            )
                        ),
                        shape = RoundedCornerShape(2.dp)
                    )
            )
        }
    }
}

@Composable
private fun BundleCryptoItem(
    symbol: String,
    weight: Double,
    price: Double,
    color: Color,
    modifier: Modifier = Modifier
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = modifier
    ) {
        // Crypto icon background
        Box(
            modifier = Modifier
                .size(36.dp)
                .background(
                    color = color.copy(alpha = 0.15f),
                    shape = CircleShape
                ),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = symbol,
                style = MaterialTheme.typography.labelSmall.copy(
                    fontWeight = FontWeight.ExtraBold,
                    fontSize = 10.sp
                ),
                color = color
            )
        }

        Spacer(modifier = Modifier.height(8.dp))

        // Weight percentage
        Text(
            text = "${weight.toInt()}%",
            style = MaterialTheme.typography.titleSmall.copy(
                fontWeight = FontWeight.Bold,
                fontSize = 14.sp
            ),
            color = Color(0xFF1A1B23)
        )

        // Price
        Text(
            text = "$${price.toString().take(6)}",
            style = MaterialTheme.typography.labelSmall.copy(
                fontSize = 10.sp
            ),
            color = Color(0xFF9E9E9E)
        )
    }
}

