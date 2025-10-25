package com.developerstring.nexpay.ui.nfc

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import kotlinx.serialization.Serializable

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NFCScreen(
    modifier: Modifier = Modifier,
    viewModel: NFCViewModel = viewModel(),
    onNavigateToSend: (String) -> Unit = {},
    onNavigateToReceive: () -> Unit = {},
    onNavigateToAddTransaction: (String) -> Unit = {}
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    // Set a default wallet address for demo purposes and test NFC immediately
    LaunchedEffect(Unit) {
        viewModel.setWalletAddress("0x1234567890abcdef1234567890abcdef12345678")
        // Trigger NFC status check immediately for debugging
        viewModel.refreshNFCStatus()
    }

    // Handle received wallet address from NFC tap
    LaunchedEffect(uiState.receivedWalletAddress) {
        uiState.receivedWalletAddress?.let { walletAddress ->
            // Navigate to AddTransactionScreen with the received wallet address
            onNavigateToAddTransaction(walletAddress)
            // Clear the received address to avoid re-navigation
            viewModel.clearReceivedAddress()
        }
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        // Top App Bar
        CenterAlignedTopAppBar(
            title = {
                Text(
                    text = "NFC Tap to Pay",
                    fontWeight = FontWeight.Bold
                )
            }
        )

        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // NFC Status Card
            item {
                NFCStatusCard(
                    isNFCAvailable = uiState.isNFCAvailable,
                    isNFCEnabled = uiState.isNFCEnabled,
                    onRefreshClick = { viewModel.refreshNFCStatus() }
                )
            }

            // Main Tap to Pay Card
            if (uiState.isNFCAvailable && uiState.isNFCEnabled) {
                item {
                    TapToPayCard(
                        onTapToPayClick = { viewModel.startReading() },
                        onReceiveClick = { viewModel.startSharing() }
                    )
                }

                // Current Wallet Address Display
                item {
                    WalletAddressCard(
                        walletAddress = uiState.currentWalletAddress,
                        onWriteToTag = { viewModel.writeWalletAddressToTag(it) }
                    )
                }

                // Received Address Card
                uiState.receivedWalletAddress?.let { address ->
                    item {
                        ReceivedAddressCard(
                            walletAddress = address,
                            onSendToAddress = { onNavigateToSend(address) },
                            onDismiss = { viewModel.clearReceivedAddress() }
                        )
                    }
                }
            }

            // Instructions Card
            item {
                InstructionsCard()
            }
        }
    }

    // NFC Reading Dialog
    NFCReadingDialog(
        isVisible = uiState.isReading,
        onDismiss = { viewModel.stopReading() },
        onCancelReading = { viewModel.stopReading() }
    )

    // NFC Sharing Dialog
    NFCSharingDialog(
        isVisible = uiState.isSharing,
        walletAddress = uiState.currentWalletAddress,
        onDismiss = { viewModel.stopSharing() },
        onStopSharing = { viewModel.stopSharing() }
    )

    // Error/Success Snackbars
    uiState.errorMessage?.let { message ->
        LaunchedEffect(message) {
            // Show error snackbar (you could use SnackbarHost here)
            viewModel.clearMessages()
        }
    }

    uiState.successMessage?.let { message ->
        LaunchedEffect(message) {
            // Show success snackbar (you could use SnackbarHost here)
            viewModel.clearMessages()
        }
    }

    // Loading Indicator
    if (uiState.isLoading) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            CircularProgressIndicator()
        }
    }
}

@Composable
private fun NFCStatusCard(
    isNFCAvailable: Boolean,
    isNFCEnabled: Boolean,
    onRefreshClick: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = when {
                !isNFCAvailable -> MaterialTheme.colorScheme.errorContainer
                !isNFCEnabled -> MaterialTheme.colorScheme.warningContainer
                else -> MaterialTheme.colorScheme.primaryContainer
            }
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = when {
                            !isNFCAvailable -> Icons.Default.Error
                            !isNFCEnabled -> Icons.Default.Warning
                            else -> Icons.Default.CheckCircle
                        },
                        contentDescription = null,
                        tint = when {
                            !isNFCAvailable -> MaterialTheme.colorScheme.onErrorContainer
                            !isNFCEnabled -> MaterialTheme.colorScheme.onSurface
                            else -> MaterialTheme.colorScheme.onPrimaryContainer
                        }
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "NFC Status",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                }

                IconButton(onClick = onRefreshClick) {
                    Icon(
                        imageVector = Icons.Default.Refresh,
                        contentDescription = "Refresh"
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = when {
                    !isNFCAvailable -> "NFC is not available on this device"
                    !isNFCEnabled -> "NFC is available but not enabled. Please enable NFC in settings."
                    else -> "NFC is available and enabled. Ready for transactions!"
                },
                style = MaterialTheme.typography.bodyMedium
            )
        }
    }
}

@Composable
private fun WalletAddressCard(
    walletAddress: String,
    onWriteToTag: (String) -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Text(
                text = "Your Wallet Address",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.height(12.dp))

            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surfaceVariant
                )
            ) {
                Text(
                    text = walletAddress,
                    modifier = Modifier.padding(12.dp),
                    style = MaterialTheme.typography.bodySmall,
                    textAlign = TextAlign.Center
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            OutlinedButton(
                onClick = { onWriteToTag(walletAddress) },
                modifier = Modifier.fillMaxWidth()
            ) {
                Icon(
                    imageVector = Icons.Default.Nfc,
                    contentDescription = null,
                    modifier = Modifier.size(18.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text("Write to NFC Tag")
            }
        }
    }
}

@Composable
private fun ReceivedAddressCard(
    walletAddress: String,
    onSendToAddress: () -> Unit,
    onDismiss: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.secondaryContainer
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.CheckCircle,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.onSecondaryContainer
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Address Received!",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSecondaryContainer
                    )
                }

                IconButton(onClick = onDismiss) {
                    Icon(
                        imageVector = Icons.Default.Close,
                        contentDescription = "Dismiss"
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surface
                )
            ) {
                Text(
                    text = walletAddress,
                    modifier = Modifier.padding(12.dp),
                    style = MaterialTheme.typography.bodySmall,
                    textAlign = TextAlign.Center
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            ElevatedButton(
                onClick = onSendToAddress,
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.elevatedButtonColors(
                    containerColor = MaterialTheme.colorScheme.primary
                )
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.Send,
                    contentDescription = null,
                    modifier = Modifier.size(18.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text("Send to this Address")
            }
        }
    }
}

@Composable
private fun InstructionsCard() {
    Card(
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Default.Info,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "How to Use NFC",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            InstructionItem(
                number = "1",
                text = "Tap 'Tap to Pay' to receive a wallet address from another device"
            )

            InstructionItem(
                number = "2",
                text = "Tap 'Receive' to share your wallet address with another device"
            )

            InstructionItem(
                number = "3",
                text = "Hold devices close together (within 4cm) for NFC to work"
            )

            InstructionItem(
                number = "4",
                text = "Make sure both devices have NFC enabled in settings"
            )
        }
    }
}

@Composable
private fun InstructionItem(
    number: String,
    text: String
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        verticalAlignment = Alignment.Top
    ) {
        Box(
            modifier = Modifier
                .size(24.dp)
                .padding(2.dp),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = number,
                style = MaterialTheme.typography.bodySmall,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary
            )
        }
        Spacer(modifier = Modifier.width(12.dp))
        Text(
            text = text,
            style = MaterialTheme.typography.bodyMedium,
            modifier = Modifier.weight(1f)
        )
    }
}

// Extension to fix the warning container color
@get:Composable
private val ColorScheme.warningContainer: androidx.compose.ui.graphics.Color
    get() = androidx.compose.ui.graphics.Color(0xFFFFF8E1) // Light amber

@get:Composable
private val ColorScheme.onWarningContainer: androidx.compose.ui.graphics.Color
    get() = androidx.compose.ui.graphics.Color(0xFF8F6200) // Dark amber

// Navigation route for NFCScreen
@Serializable
data object NFCScreenRoute
