package com.developerstring.nexpay.viewmodel

import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.developerstring.nexpay.data.currencies.CryptoCurrency
import com.developerstring.nexpay.data.room_db.dao.BundleTransactionDao
import com.developerstring.nexpay.data.room_db.model.BundleTransaction
import com.developerstring.nexpay.repository.AppLockRepository
import com.developerstring.nexpay.utils.error_handlers.NetworkError
import com.developerstring.nexpay.utils.error_handlers.onError
import com.developerstring.nexpay.utils.error_handlers.onSuccess
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withTimeout
import xyz.mcxross.kaptos.Aptos
import xyz.mcxross.kaptos.model.*
import xyz.mcxross.kaptos.model.MoveVector.Companion.string
import xyz.mcxross.kaptos.util.APTOS_COIN
import kotlin.time.Clock
import kotlin.time.ExperimentalTime
import xyz.mcxross.kaptos.account.Account as AptosAccount


class AptosViewModel(
    private val appLockRepository: AppLockRepository,
    private val bundleTransactionDao: BundleTransactionDao
) : ViewModel() {

    val aptos = Aptos(AptosConfig(AptosSettings(network = Network.TESTNET)))

    private val _uiState = MutableStateFlow(AptosWalletUiState())
    val uiState: StateFlow<AptosWalletUiState> = _uiState.asStateFlow()

    private var aptosClient: Aptos? = null
    private var currentAccount: AptosAccount? = null

    init {
        // Initialize Kaptos client with mainnet using AptosConfig
        try {
            val config = AptosConfig(AptosSettings(network = Network.TESTNET))
            aptosClient = Aptos(config)
        } catch (_: Exception) {
            // Fallback initialization if AptosConfig doesn't work as expected
            aptosClient = null
        }

        // Check for existing account on initialization
        loadExistingAccount()
    }

    private fun loadExistingAccount() {
        viewModelScope.launch {
            try {
                val existingAccount = appLockRepository.getPersistentAccount()
                if (existingAccount != null) {
                    currentAccount = existingAccount
                    val balance = getAccountBalance()

                    _uiState.value = _uiState.value.copy(
                        isConnected = true,
                        walletAddress = existingAccount.accountAddress.toString(),
                        balance = balance
                    )
                }
            } catch (_: Exception) {
                // If loading fails, we'll create a new account when connectWallet is called
            }
        }
    }

    fun connectWallet(
        userPrivateKey: String? = null,
    ) {
        viewModelScope.launch {
            try {
                _uiState.value = _uiState.value.copy(isLoading = true, error = null)

                val account = appLockRepository.initializePersistentAccount(userPrivateKey)

                currentAccount = account
                val balance = getAccountBalance()

                _uiState.value = _uiState.value.copy(
                    isConnected = true,
                    isLoading = false,
                    walletAddress = account.accountAddress.toString(),
                    balance = balance,
                    error = null
                )
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    error = e.message ?: "Failed to connect wallet"
                )
            }
        }
    }

    private suspend fun getAccountBalance(): String {
        return try {
            if (currentAccount == null) {
                return "0.0000 APT"
            } else {
//                aptos.fundAccount(currentAccount!!.accountAddress, 10000L).expect("Failed to fund Alice's account")
                val balance = aptos.getAccountAPTAmount(currentAccount!!.accountAddress)
                val balanceAPT = balance.destruct()?.div(100_000_000.0)

                return "$balanceAPT APT"
            }

        } catch (e: Exception) {
            println("Error fetching balance: ${e.message}")
            e.printStackTrace()
            // If account doesn't exist or has no balance, return 0
            // This is common for newly created accounts that haven't been funded
            "0.0000 APT"
        }
    }

    fun refreshBalance() {
        currentAccount?.let { account ->
            viewModelScope.launch {
                try {
                    val balance = getAccountBalance()
                    _uiState.value = _uiState.value.copy(balance = balance)
                } catch (e: Exception) {
                    _uiState.value = _uiState.value.copy(
                        error = "Failed to refresh balance: ${e.message}"
                    )
                }
            }
        }
    }

    fun disconnectWallet() {
        viewModelScope.launch {
            try {
                appLockRepository.clearAccount()
                currentAccount = null
                _uiState.value = AptosWalletUiState()
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    error = "Failed to disconnect wallet: ${e.message}"
                )
            }
        }
    }

    /**
     * Execute smart contract on Aptos blockchain
     * Contract: smart_wallet at 0x5f971a43ff0c97789f67dc7f75a9fba019695943e0ecebbb81adc851eaa0a36f
     *
     * @param functionName The function to call: "init_wallet", "send_coins", or "receive_coins"
     * @param args Arguments for the function (e.g., recipient address and amount for send_coins)
     * @return Result with success message or error
     *
     * NOTE: The Kaptos 0.1.2-beta library has limited API in commonMain.
     * The transaction building/signing/submission methods are not available.
     * You may need to:
     * 1. Use platform-specific implementations (Android/iOS)
     * 2. Upgrade to a newer version of Kaptos when available
     * 3. Use raw HTTP API calls to Aptos REST API
     */


    private val _hex = MutableStateFlow("")
    val hex: StateFlow<String> = _hex.asStateFlow()

    private val _gasFees = MutableStateFlow(0L)
    val gasFees: StateFlow<Long> = _gasFees.asStateFlow()

    private fun encodeAddressVector(addresses: List<AccountAddress>): MoveVector<AccountAddress> {
        return MoveVector(addresses)
    }

    private fun encodeU64Vector(values: List<ULong>): MoveVector<U64> {
        return MoveVector(values.map { U64(it) })
    }

    companion object {
        const val OCTAS_PER_APT = 100_000_000L
        const val CONTRACT_ADDRESS = "0x5f971a43ff0c97789f67dc7f75a9fba019695943e0ecebbb81adc851eaa0a36f"
        const val TRANSACTION_TIMEOUT_MS = 30_000L
        const val ADDRESS_LENGTH = 66 // "0x" + 64 hex chars
    }

    // Validation Helpers
    private fun validateAddress(address: String): Boolean {
        return address.startsWith("0x") && address.length == ADDRESS_LENGTH
    }

    private fun validateAddresses(addresses: List<String>): Result<List<AccountAddress>> {
        return try {
            require(addresses.all { validateAddress(it) }) {
                "Invalid address format. Must be 66 characters starting with 0x"
            }
            Result.success(addresses.map { AccountAddress.fromString(it) })
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    private fun createBundleTransaction(
        hex: String,
        gasFees: Long,
        success: Boolean,
        message: String,
        btcPrice: Double,
        ethPrice: Double,
        solPrice: Double,
        timestamp: Long,
        btcWeight: Int = 50,
        ethWeight: Int = 30,
        solWeight: Int = 20,
        amount: String,
        leverage: Int,
        isLong: Boolean,
    ) {
        val bundleTransaction = BundleTransaction(
            hex = hex,
            gasFees = gasFees,
            success = success,
            message = message,
            btcPrice = btcPrice,
            ethPrice = ethPrice,
            solPrice = solPrice,
            timestamp = timestamp,
            btcWeight = btcWeight,
            ethWeight = ethWeight,
            solWeight = solWeight,
            amount = amount,
            leverage = leverage,
            isLong = isLong
        )

        viewModelScope.launch {
            bundleTransactionDao.insertBundlesTrans(bundleTransaction)
        }
    }

    private val _getBundleTransactions = MutableStateFlow<List<BundleTransaction>>(emptyList())
    val getBundleTransactions: StateFlow<List<BundleTransaction>> = _getBundleTransactions

    private var _bundleTransaction = MutableStateFlow(
        BundleTransaction(
            id = 0,
            hex = "",
            gasFees = 0L,
            success = false,
            message = "",
            btcPrice = 0.0,
            ethPrice = 0.0,
            solPrice = 0.0,
            timestamp = 0L,
            amount = "",
            leverage = 0,
            isLong = true,
        )
    )

    fun getAllBundleTransactions() {
        viewModelScope.launch {
            try {
                bundleTransactionDao.getBundlesTrans().collect {
                    _getBundleTransactions.value = it
                }
            } catch (e: Exception) {
            }
        }
    }

    var lastID: MutableState<Int> = mutableStateOf(0)
    suspend fun getLastID() {
        lastID.value = appLockRepository.getLastId()
    }

    val bundleTransaction: StateFlow<BundleTransaction> = _bundleTransaction.asStateFlow()

    var btcPrice: MutableState<Double> = mutableStateOf(0.0)
    var ethPrice: MutableState<Double> = mutableStateOf(0.0)
    var solPrice: MutableState<Double> = mutableStateOf(0.0)

    var successBundle: MutableState<Boolean> = mutableStateOf(false)

    var listOfBundle: MutableList<BundleTransaction> = mutableListOf()

    var amountBundle: MutableState<String> = mutableStateOf("")

    var leverage: MutableState<Int> = mutableStateOf(0)

    var isLong: MutableState<Boolean> = mutableStateOf(true)

    var newAmount: MutableState<String> = mutableStateOf("")

    @OptIn(ExperimentalTime::class)
    private suspend fun executeOnChain(
        functionName: String,
        typeArgs: TypeArguments?,
        funArgs: FunctionArguments? = null,
        transactionAmount: String? = null
    ): Result<String> {
        return try {

            println(
                "Transaction start\n============================================="
            )

            val account =
                currentAccount ?: return Result.failure(Exception("No wallet connected. Please connect wallet first."))

            val txn = if (funArgs != null) {
                aptos.buildTransaction.simple(
                    sender = account.accountAddress,
                    data = entryFunctionData {
                        function = functionName
                        functionArguments = funArgs
                    }
                )
            } else {
                aptos.buildTransaction.simple(
                    sender = currentAccount!!.accountAddress,
                    data = entryFunctionData {
                        function = functionName
                    }
                )
            }

            val committed = aptos.signAndSubmitTransaction(account, txn)
            val executed = aptos.waitForTransaction(HexInput.fromString(committed.expect("Tx failed").hash))

            // Extract and update gas fee and hex from transaction
            val transactionHash = committed.expect("Tx failed").hash

            // Try to extract gas information from the executed transaction response
            val executedTxn = executed.expect("Failed to get transaction details")

            // Also extract hash from executed response as verification
            val executedHash = try {
                val responseString = executedTxn.toString()
                val hashRegex = """hash=(0x[a-fA-F0-9]+)""".toRegex()
                hashRegex.find(responseString)?.groupValues?.get(1) ?: transactionHash
            } catch (_: Exception) {
                transactionHash
            }

            val gasUsed = try {
                val responseString = executedTxn.toString()
                // Extract gasUsed from UserTransactionResponse format: gasUsed=496
                val gasUsedRegex = """gasUsed=(\d+)""".toRegex()
                gasUsedRegex.find(responseString)?.groupValues?.get(1)?.toLongOrNull() ?: 0L
            } catch (_: Exception) {
                0L
            }

            val gasUnitPrice = try {
                val responseString = executedTxn.toString()
                // Extract gasUnitPrice from UserTransactionResponse format: gasUnitPrice=100
                val gasPriceRegex = """gasUnitPrice=(\d+)""".toRegex()
                gasPriceRegex.find(responseString)?.groupValues?.get(1)?.toLongOrNull() ?: 0L
            } catch (_: Exception) {
                0L
            }

            val totalGasFee = gasUsed * gasUnitPrice

            // Update state with transaction details
            _hex.value = executedHash
            _gasFees.value = totalGasFee

            if (transactionAmount!= null) {
                createBundleTransaction(
                    hex = executedHash,
                    gasFees = totalGasFee,
                    success = true,
                    message = "Transaction successful",
                    timestamp = Clock.System.now().toEpochMilliseconds(),
                    btcPrice = btcPrice.value,
                    ethPrice = ethPrice.value,
                    solPrice = solPrice.value,
                    amount = transactionAmount,
                    leverage = leverage.value,
                    isLong = isLong.value,
                )

                _bundleTransaction.value = BundleTransaction(
                    hex = executedHash,
                    gasFees = totalGasFee,
                    success = true,
                    message = "Transaction successful",
                    timestamp = Clock.System.now().toEpochMilliseconds(),
                    btcPrice = btcPrice.value,
                    ethPrice = ethPrice.value,
                    solPrice = solPrice.value,
                    id = appLockRepository.getLastId(),
                    amount = transactionAmount,
                    leverage = leverage.value,
                    isLong = isLong.value,
                )
            }

            println("✅ Executed: $executed")
            println("💰 Gas Used: $gasUsed units")
            println("💵 Gas Unit Price: $gasUnitPrice octas")
            println("🔥 Total Gas Fee: $totalGasFee octas (${totalGasFee / 100_000_000.0} APT)")
            println("🔗 Transaction Hash: $executedHash")
            println("📋 Committed Hash: $transactionHash")
            println("✅ Hash Match: ${executedHash == transactionHash}")

            successBundle.value = true

            Result.success("Transaction successful: ${committed.expect("Tx failed").hash}")

        } catch (e: Exception) {
            successBundle.value = false
            println(e.message + " <- error on contract run")
            Result.failure(Exception("Transaction failed: ${e.message}"))
        }
    }

    /**
     * Initialize wallet on the smart contract
     */
    fun initWallet(useDefault: Boolean = false, onResult: (Result<String>) -> Unit) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, error = null)

            val module =
                "0x2bc654f1f5009c045ba5486d11252d46724d7e0522db6dbde2ff0fe7e275a1bf::smart_wallet_v2::init_wallet"


            val result = executeOnChain(functionName = module, typeArgs = null, transactionAmount = "0.0")

            result.fold(
                onSuccess = { message ->
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        error = null
                    )
                    onResult(Result.success(message))
                },
                onFailure = { error ->
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        error = error.message
                    )
                    onResult(Result.failure(error))
                }
            )
        }
    }

    /**
     * Send coins using the smart contract
     * @param recipientAddress Address to send coins to
     * @param amount Amount in octas (1 APT = 100,000,000 octas)
     */

    // receiver "0x25714b39ba0b43b9bde30f4500c24fdc746faaf371969fa28011fb508791eda4"
    fun sendCoins(
        recipientAddress: String,
        amount: ULong,
        useDefault: Boolean = false,
        onResult: (Result<String>) -> Unit
    ) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, error = null)

            try {
                val module = if (useDefault) {
                    "0x1::coin::transfer"
                } else {
                    "0x2bc654f1f5009c045ba5486d11252d46724d7e0522db6dbde2ff0fe7e275a1bf::smart_wallet_v2::send_coins"
                }

                val result = executeOnChain(
                    module,
                    typeArgs = null,
                    funArgs = functionArguments {
                        +AccountAddress.fromString(recipientAddress)
                        +U64(amount)
                    },
                )

                result.fold(
                    onSuccess = { message ->
                        _uiState.value = _uiState.value.copy(
                            isLoading = false,
                            error = null
                        )
                        // Refresh balance after successful send
                        refreshBalance()
                        onResult(Result.success(message))
                    },
                    onFailure = { error ->
                        _uiState.value = _uiState.value.copy(
                            isLoading = false,
                            error = error.message
                        )
                        onResult(Result.failure(error))
                    }
                )
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    error = "Invalid parameters: ${e.message}"
                )
                onResult(Result.failure(e))
            }
        }
    }

    // Bucket Contract Execution

    private val contractAddressForBucket = "0x33ec41711fe3c92c3f1a010909342e1c2c5de962e50645c8f8c8eda119122d6b"

    fun initializeBundle(leverage_: Int, onResult: (Result<String>) -> Unit) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, error = null)

            val module = "$contractAddressForBucket::bucket_protocol::init"

            leverage.value = leverage_

            val result = executeOnChain(
                functionName = module, typeArgs = null,
                funArgs = functionArguments {
                    +U64(leverage_.toULong())
                },
            )

            result.fold(
                onSuccess = { message ->
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        error = null
                    )
                    onResult(Result.success(message))
                },
                onFailure = { error ->
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        error = error.message
                    )
                    onResult(Result.failure(error))
                }
            )
        }
    }

    // ✅ FIXED: depositCollateral - Remove unnecessary type args
    fun depositCollateral(
        amountAPT: Double,
        onResult: (Result<String>) -> Unit
    ) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, error = null)

            try {

                val amountOctas = (amountAPT * 100_000_000).toULong()
                val module = "$contractAddressForBucket::bucket_protocol::deposit_collateral"

                amountBundle.value = amountAPT.toString()

                newAmount.value = amountAPT.toString()

                val result = executeOnChain(
                    module,
                    typeArgs = null, // FIXED: No type args needed
                    funArgs = functionArguments {
                        +U64(amountOctas)
                    },
                    transactionAmount = amountAPT.toString()
                )

                result.fold(
                    onSuccess = { message ->
                        _uiState.value = _uiState.value.copy(isLoading = false, error = null)
                        refreshBalance()
                        onResult(Result.success(message))
                    },
                    onFailure = { error ->
                        _uiState.value = _uiState.value.copy(isLoading = false, error = error.message)
                        onResult(Result.failure(error))
                    }
                )
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(isLoading = false, error = "Invalid parameters: ${e.message}")
                onResult(Result.failure(e))
            }
        }
    }

    fun openLong(
        onResult: (Result<String>) -> Unit
    ) {
        viewModelScope.launch {
            val lastID = appLockRepository.getLastId()
            _uiState.value = _uiState.value.copy(isLoading = true, error = null)
            try {
                val module = "$contractAddressForBucket::bucket_protocol::open_long"
                appLockRepository.setLastId(lastID + 1)

                isLong.value = true

                val result = executeOnChain(
                    module,
                    typeArgs = null, // FIXED: No type args needed
                    funArgs = functionArguments {
                        +U64((lastID + 1).toULong())
                    },
                )

                result.fold(
                    onSuccess = { message ->
                        _uiState.value = _uiState.value.copy(isLoading = false, error = null)
                        refreshBalance()
                        onResult(Result.success(message))
                    },
                    onFailure = { error ->
                        _uiState.value = _uiState.value.copy(isLoading = false, error = error.message)
                        onResult(Result.failure(error))
                    }
                )
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(isLoading = false, error = "Invalid parameters: ${e.message}")
                onResult(Result.failure(e))
            }
        }
    }

    fun openShort(
        bucketId: Int = 0,
        onResult: (Result<String>) -> Unit
    ) {
        viewModelScope.launch {
            val lastID = appLockRepository.getLastId()
            _uiState.value = _uiState.value.copy(isLoading = true, error = null)

            try {
                val module = "$contractAddressForBucket::bucket_protocol::open_short"
                appLockRepository.setLastId(lastID + 1)

                isLong.value = false

                val result = executeOnChain(
                    module,
                    typeArgs = null, // FIXED: No type args needed
                    funArgs = functionArguments {
                        +U64((lastID + 1).toULong())
                    },
                )

                result.fold(
                    onSuccess = { message ->
                        _uiState.value = _uiState.value.copy(isLoading = false, error = null)
                        refreshBalance()
                        onResult(Result.success(message))
                    },
                    onFailure = { error ->
                        _uiState.value = _uiState.value.copy(isLoading = false, error = error.message)
                        onResult(Result.failure(error))
                    }
                )
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(isLoading = false, error = "Invalid parameters: ${e.message}")
                onResult(Result.failure(e))
            }
        }
    }

    fun createBucket(
        assets: List<String>,
        weights: List<Int>,
        leverage: Int,
        onResult: (Result<String>) -> Unit
    ) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, error = null)
            try {

//                require(assets.size == weights.size) { "Assets and weights must have same length" }
//                require(leverage in 1..20) { "Leverage must be between 1 and 20" }
//                require(weights.sum() == 100) { "Weights must sum to 100" }

                val module = "$contractAddressForBucket::bucket_protocol::create_bucket"

                val result = executeOnChain(
                    module,
                    typeArgs = typeArguments {
                        +TypeTagStruct(APTOS_COIN)
                    },
                    funArgs = functionArguments {
                        // Convert assets to bytes for Move vector<u8>
                        +MoveString(assets.toString())
//                        +MoveString("f,g")
                        // Convert weights to U64 values for Move vector<u64>
//                        +MoveString("f,g")
                        +MoveString(weights.map { it.toString() }.toString())
                        // Convert leverage to U64
                        +U8(leverage.toUByte().toByte())
                    },
                )

                result.fold(
                    onSuccess = { message ->
                        _uiState.value = _uiState.value.copy(
                            isLoading = false,
                            error = null
                        )
                        // Refresh balance after successful send
                        refreshBalance()
                        onResult(Result.success(message))
                    },
                    onFailure = { error ->
                        _uiState.value = _uiState.value.copy(
                            isLoading = false,
                            error = error.message
                        )
                        onResult(Result.failure(error))
                    }
                )
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    error = "Invalid parameters: ${e.message}"
                )
                onResult(Result.failure(e))
            }
        }
    }

    fun updateOracle(
        btcPrice_: Double,
        ethPrice_: Double,
        solPrice_: Double,
        onResult: (Result<String>) -> Unit
    ) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, error = null)
            try {

                val module = "$contractAddressForBucket::bucket_protocol::update_oracle"

                val btcCents = (btcPrice_ * 100).toULong()
                val ethCents = (ethPrice_ * 100).toULong()
                val solCents = (solPrice_ * 100).toULong()

                val result = executeOnChain(
                    module,
                    typeArgs = null,
                    funArgs = functionArguments {
                        // Convert prices to U64 values for Move vector<u64> (price in smallest units)
                        +U64(btcCents)
                        +U64(ethCents)
                        +U64(solCents)
//                        +string(prices.map { it.toString() })
                    },
                )

                result.fold(
                    onSuccess = { message ->
                        _uiState.value = _uiState.value.copy(
                            isLoading = false,
                            error = null
                        )
                        // Refresh balance after successful send
                        refreshBalance()
                        onResult(Result.success(message))
                    },
                    onFailure = { error ->
                        _uiState.value = _uiState.value.copy(
                            isLoading = false,
                            error = error.message
                        )
                        onResult(Result.failure(error))
                    }
                )
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    error = "Invalid parameters: ${e.message}"
                )
                onResult(Result.failure(e))
            }
        }
    }

    // ✅ FIXED: openPosition - Use U64 instead of U128
    fun openPosition(
        positionId: Int,
        onResult: (Result<String>) -> Unit
    ) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, error = null)
            try {
                val module = "$contractAddressForBucket::bucket_protocol::close_position"

                val result = executeOnChain(
                    module,
                    typeArgs = null,
                    funArgs = functionArguments {
                        +U64(positionId.toULong()) // FIXED: Changed from U128 to U64
                    },
                )

                result.fold(
                    onSuccess = { message ->
                        _uiState.value = _uiState.value.copy(isLoading = false, error = null)
                        refreshBalance()
                        onResult(Result.success(message))
                    },
                    onFailure = { error ->
                        _uiState.value = _uiState.value.copy(isLoading = false, error = error.message)
                        onResult(Result.failure(error))
                    }
                )
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(isLoading = false, error = "Invalid parameters: ${e.message}")
                onResult(Result.failure(e))
            }
        }
    }

    // ✅ FIXED: closePosition - Already correct
    fun closePosition(
        positionId: Long,
        onResult: (Result<String>) -> Unit
    ) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, error = null)
            try {
                val module = "$contractAddressForBucket::bucket_protocol::close_position"

                val result = executeOnChain(
                    module,
                    typeArgs = null,
                    funArgs = functionArguments {
                        +U64(positionId.toULong())
                    },
                )

                result.fold(
                    onSuccess = { message ->
                        _uiState.value = _uiState.value.copy(isLoading = false, error = null)
                        refreshBalance()
                        onResult(Result.success(message))
                    },
                    onFailure = { error ->
                        _uiState.value = _uiState.value.copy(isLoading = false, error = error.message)
                        onResult(Result.failure(error))
                    }
                )
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(isLoading = false, error = "Invalid parameters: ${e.message}")
                onResult(Result.failure(e))
            }
        }
    }

}

data class AptosWalletUiState(
    val isConnected: Boolean = false,
    val isLoading: Boolean = false,
    val walletAddress: String? = null,
    val balance: String = "0.0000 APT",
    val error: String? = null
)