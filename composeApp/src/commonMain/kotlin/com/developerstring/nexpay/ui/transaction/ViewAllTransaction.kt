package com.developerstring.nexpay.ui.transaction

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.CallMade
import androidx.compose.material.icons.filled.*
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

    val list = aptosViewModel.listOfBundle

    var selectedFilter by remember { mutableStateOf("All") }
    var selectedCrypto by remember { mutableStateOf("All") }

    // Sample transaction data - replace with actual data from viewModel
    val sampleTransactions by sharedViewModel.getAllTransactions().collectAsState(initial = emptyList())

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

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF8F9FA))
            .padding(top = 40.dp)
    ) {
        // Header with back button
        Surface(
            color = Color.White,
            shadowElevation = 1.dp
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(
                    onClick = { navController.navigateUp() },
                    modifier = Modifier.size(40.dp)
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "Back",
                        tint = Color(0xFF1A1B23)
                    )
                }

                Spacer(modifier = Modifier.width(8.dp))

                Text(
                    text = "All Transactions",
                    style = MaterialTheme.typography.headlineSmall.copy(
                        fontWeight = FontWeight.Bold,
                        fontSize = 24.sp
                    ),
                    color = Color(0xFF1A1B23)
                )

                Spacer(modifier = Modifier.weight(1f))

                // Filter/Search icon
                IconButton(
                    onClick = { /* TODO: Implement search */ },
                    modifier = Modifier.size(40.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Search,
                        contentDescription = "Search",
                        tint = Color(0xFF6C757D)
                    )
                }
            }
        }

        // Filter chips section
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .background(Color.White)
                .padding(horizontal = 16.dp, vertical = 12.dp)
        ) {
            // Status filters
            Text(
                text = "Status",
                style = MaterialTheme.typography.labelMedium.copy(
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 12.sp
                ),
                color = Color(0xFF6C757D),
                modifier = Modifier.padding(bottom = 8.dp)
            )

            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(listOf("All", "Completed", "Pending", "Failed", "Scheduled")) { status ->
                    FilterChip(
                        selected = selectedFilter == status,
                        onClick = { selectedFilter = status },
                        label = {
                            Text(
                                text = status,
                                style = MaterialTheme.typography.labelSmall.copy(
                                    fontWeight = FontWeight.Medium,
                                    fontSize = 11.sp
                                )
                            )
                        },
                        colors = FilterChipDefaults.filterChipColors(
                            containerColor = Color.Transparent,
                            selectedContainerColor = Color(0xFF7C3AED).copy(alpha = 0.1f),
                            labelColor = Color(0xFF6C757D),
                            selectedLabelColor = Color(0xFF7C3AED)
                        ),
                        border = FilterChipDefaults.filterChipBorder(
                            borderColor = Color(0xFFE9ECEF),
                            selectedBorderColor = Color(0xFF7C3AED).copy(alpha = 0.3f),
                            borderWidth = 1.dp,
                            enabled = true,
                            selected = selectedFilter == status
                        )
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Crypto type filters
            Text(
                text = "Cryptocurrency",
                style = MaterialTheme.typography.labelMedium.copy(
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 12.sp
                ),
                color = Color(0xFF6C757D),
                modifier = Modifier.padding(bottom = 8.dp)
            )

            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(listOf("All", "APT", "ETH", "BTC")) { crypto ->
                    FilterChip(
                        selected = selectedCrypto == crypto,
                        onClick = { selectedCrypto = crypto },
                        label = {
                            Text(
                                text = crypto,
                                style = MaterialTheme.typography.labelSmall.copy(
                                    fontWeight = FontWeight.Medium,
                                    fontSize = 11.sp
                                )
                            )
                        },
                        colors = FilterChipDefaults.filterChipColors(
                            containerColor = Color.Transparent,
                            selectedContainerColor = Color(0xFF06B6D4).copy(alpha = 0.1f),
                            labelColor = Color(0xFF6C757D),
                            selectedLabelColor = Color(0xFF06B6D4)
                        ),
                        border = FilterChipDefaults.filterChipBorder(
                            borderColor = Color(0xFFE9ECEF),
                            selectedBorderColor = Color(0xFF06B6D4).copy(alpha = 0.3f),
                            borderWidth = 1.dp,
                            enabled = true,
                            selected = selectedCrypto == crypto
                        )
                    )
                }
            }
        }

        // Transactions list
        if (filteredTransactions.isEmpty()) {
            // Empty state
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(32.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Icon(
                    imageVector = Icons.Default.Receipt,
                    contentDescription = null,
                    modifier = Modifier.size(64.dp),
                    tint = Color(0xFFCED4DA)
                )
                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    text = "No transactions found",
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontWeight = FontWeight.Medium
                    ),
                    color = Color(0xFF6C757D),
                    textAlign = TextAlign.Center
                )
                Text(
                    text = "Try adjusting your filters or make your first transaction",
                    style = MaterialTheme.typography.bodySmall,
                    color = Color(0xFF9E9E9E),
                    textAlign = TextAlign.Center,
                    modifier = Modifier.padding(top = 4.dp)
                )
            }
        } else {
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(filteredTransactions) { transaction ->
                    TransactionCard(
                        transaction = transaction,
                        onClick = {
                            // TODO: Navigate to transaction details
                        }
                    )
                }
            }
        }
    }
}

@Composable
private fun TransactionCard(
    transaction: Transaction,
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
            containerColor = Color.White
        ),
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(
            defaultElevation = 2.dp,
            hoveredElevation = 6.dp
        ),
        border = BorderStroke(
            width = 1.dp,
            color = Color(0xFFE9ECEF)
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