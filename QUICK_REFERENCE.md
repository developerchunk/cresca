# Quick Reference - Smart Contract Execution

## Function Signatures

### executeOnChain()
```kotlin
fun executeOnChain(
    functionName: String,                        // "init_wallet", "send_coins", or "receive_coins"
    args: List<EntryFunctionArgument> = listOf() // Function arguments
): Result<String>                                 // Returns success message or error
```

### initWallet()
```kotlin
fun initWallet(
    onResult: (Result<String>) -> Unit  // Callback with result
)
```

### sendCoins()
```kotlin
fun sendCoins(
    recipientAddress: String,           // Recipient's Aptos address (0x...)
    amount: ULong,                      // Amount in octas (1 APT = 100,000,000 octas)
    onResult: (Result<String>) -> Unit  // Callback with result
)
```

### receiveCoins()
```kotlin
fun receiveCoins(
    senderAddress: String,              // Sender's Aptos address (0x...)
    amount: ULong,                      // Amount in octas
    onResult: (Result<String>) -> Unit  // Callback with result
)
```

## Usage Examples

### 1. Initialize Wallet
```kotlin
viewModel.initWallet { result ->
    result.fold(
        onSuccess = { message -> 
            Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
        },
        onFailure = { error -> 
            Toast.makeText(context, "Error: ${error.message}", Toast.LENGTH_SHORT).show()
        }
    )
}
```

### 2. Send 0.01 APT
```kotlin
viewModel.sendCoins(
    recipientAddress = "0x742d35Cc6634C0532925a3b844Bc9e7595f0bEb2",
    amount = 1_000_000uL, // 0.01 APT
) { result ->
    println(result.getOrNull() ?: result.exceptionOrNull()?.message)
}
```

### 3. Receive 1 APT
```kotlin
viewModel.receiveCoins(
    senderAddress = "0x123abc...",
    amount = 100_000_000uL, // 1 APT
) { result ->
    // Handle result
}
```

## Amount Conversions

```kotlin
// APT to Octas
val amountInOctas = aptAmount * 100_000_000uL

// Common amounts
val oneApt = 100_000_000uL      // 1 APT
val halfApt = 50_000_000uL      // 0.5 APT
val pointOneApt = 10_000_000uL  // 0.1 APT
val pointZeroOneApt = 1_000_000uL // 0.01 APT
```

## UI State

```kotlin
data class AptosWalletUiState(
    val isConnected: Boolean = false,      // Wallet connected?
    val isLoading: Boolean = false,        // Transaction in progress?
    val walletAddress: String? = null,     // Current wallet address
    val balance: String = "0.0000 APT",   // Current balance
    val error: String? = null              // Error message if any
)
```

### Observe UI State
```kotlin
val uiState by viewModel.uiState.collectAsState()

when {
    uiState.isLoading -> CircularProgressIndicator()
    uiState.error != null -> Text(uiState.error!!, color = Color.Red)
    else -> Text("Balance: ${uiState.balance}")
}
```

## Contract Details

| Property | Value |
|----------|-------|
| **Contract Address** | `0x5f971a43ff0c97789f67dc7f75a9fba019695943e0ecebbb81adc851eaa0a36f` |
| **Module Name** | `smart_wallet` |
| **Network** | Mainnet |

### Functions

| Function Name | Parameters | Description |
|--------------|------------|-------------|
| `init_wallet` | None | Initialize wallet for account |
| `send_coins` | `address to, u64 amount` | Send coins to address |
| `receive_coins` | `address from, u64 amount` | Receive coins from address |

## Error Messages

| Error | Meaning | Solution |
|-------|---------|----------|
| "No wallet connected" | Wallet not initialized | Call `connectWallet()` first |
| "Aptos client not initialized" | Client setup failed | Check network connection |
| "Transaction API not yet implemented" | API limitation | See implementation guide |
| "Invalid parameters" | Bad input | Check address format and amount |

## Complete Flow Example

```kotlin
@Composable
fun TransactionScreen(viewModel: AptosViewModel = koinViewModel()) {
    val uiState by viewModel.uiState.collectAsState()
    var recipientAddress by remember { mutableStateOf("") }
    var amount by remember { mutableStateOf("") }
    
    LaunchedEffect(Unit) {
        viewModel.connectWallet()
    }
    
    Column(modifier = Modifier.padding(16.dp)) {
        // Wallet Info
        if (uiState.isConnected) {
            Text("Address: ${uiState.walletAddress}")
            Text("Balance: ${uiState.balance}")
            Spacer(modifier = Modifier.height(16.dp))
            
            // Initialize Wallet
            Button(
                onClick = {
                    viewModel.initWallet { result ->
                        result.fold(
                            onSuccess = { Log.d("Wallet", it) },
                            onFailure = { Log.e("Wallet", it.message ?: "Unknown") }
                        )
                    }
                },
                enabled = !uiState.isLoading
            ) {
                Text("Initialize Wallet")
            }
            
            Spacer(modifier = Modifier.height(16.dp))
            
            // Send Coins Form
            TextField(
                value = recipientAddress,
                onValueChange = { recipientAddress = it },
                label = { Text("Recipient Address") },
                placeholder = { Text("0x...") }
            )
            
            Spacer(modifier = Modifier.height(8.dp))
            
            TextField(
                value = amount,
                onValueChange = { amount = it },
                label = { Text("Amount (APT)") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal)
            )
            
            Spacer(modifier = Modifier.height(16.dp))
            
            Button(
                onClick = {
                    val amountInOctas = (amount.toDoubleOrNull() ?: 0.0) * 100_000_000
                    viewModel.sendCoins(
                        recipientAddress = recipientAddress,
                        amount = amountInOctas.toULong()
                    ) { result ->
                        result.fold(
                            onSuccess = { Log.d("Send", it) },
                            onFailure = { Log.e("Send", it.message ?: "Unknown") }
                        )
                    }
                },
                enabled = !uiState.isLoading && recipientAddress.isNotEmpty() && amount.isNotEmpty()
            ) {
                Text("Send Coins")
            }
        }
        
        // Loading Indicator
        if (uiState.isLoading) {
            Spacer(modifier = Modifier.height(16.dp))
            CircularProgressIndicator()
        }
        
        // Error Display
        uiState.error?.let { error ->
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = error,
                color = MaterialTheme.colorScheme.error,
                style = MaterialTheme.typography.bodyMedium
            )
        }
    }
}
```

## Testing Checklist

- [ ] Connect wallet
- [ ] Check balance displays correctly
- [ ] Call `initWallet()` - see loading state
- [ ] Handle success/error callbacks
- [ ] Call `sendCoins()` with valid data
- [ ] Verify balance updates after send
- [ ] Test error cases (invalid address, insufficient balance)
- [ ] Test `receiveCoins()` function

## Important Notes

⚠️ **Current Status**: The transaction payload is properly structured, but the actual submission to the blockchain is not yet implemented due to Kaptos API limitations. See `SMART_CONTRACT_EXECUTION_GUIDE.md` for implementation options.

✅ **What Works**: 
- Wallet connection
- Balance checking
- Transaction payload building
- UI state management
- Error handling

❌ **What Needs Work**:
- Transaction signing and submission
- Argument serialization (MoveAddress, MoveUInt64)

---

For detailed implementation instructions, see:
- `SMART_CONTRACT_EXECUTION_GUIDE.md`
- `IMPLEMENTATION_SUMMARY.md`

