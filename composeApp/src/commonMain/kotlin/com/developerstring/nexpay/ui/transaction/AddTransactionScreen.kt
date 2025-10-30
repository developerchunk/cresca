package com.developerstring.nexpay.ui.transaction

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.automirrored.rounded.ArrowBack
import androidx.compose.material.icons.automirrored.rounded.Send
import androidx.compose.material.icons.rounded.Money
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import com.developerstring.nexpay.data.room_db.model.TransactionStatus
import com.developerstring.nexpay.viewmodel.AptosViewModel
import com.developerstring.nexpay.viewmodel.SharedViewModel
import kotlinx.datetime.*
import kotlinx.serialization.Serializable
import nexpay.composeapp.generated.resources.Res
import nexpay.composeapp.generated.resources.rounded_wallet
import org.jetbrains.compose.resources.vectorResource
import kotlin.time.Clock
import kotlin.time.ExperimentalTime

@Serializable
data class AddTransactionScreenRoute(val receiverAddress: String? = null)

@OptIn(ExperimentalMaterial3Api::class, ExperimentalTime::class)
@Composable
fun AddTransactionScreen(
    sharedViewModel: SharedViewModel,
    navController: NavController,
    aptosViewModel: AptosViewModel,
    initialReceiverAddress: String? = null
) {
    var receiverAddress by remember { mutableStateOf(initialReceiverAddress ?: "") }
    var amount by remember { mutableStateOf("") }
    var notes by remember { mutableStateOf("") }
    var isScheduled by remember { mutableStateOf(false) }
    var selectedDate by remember { mutableStateOf<LocalDate?>(null) }
    var selectedTime by remember { mutableStateOf<LocalTime?>(null) }
    var showDatePicker by remember { mutableStateOf(false) }
    var showTimePicker by remember { mutableStateOf(false) }
    var isLoading by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf<String?>(null) }

    // Get current wallet address from AptosViewModel
    val aptosUiState by aptosViewModel.uiState.collectAsStateWithLifecycle()
    val currentWalletAddress = aptosUiState.walletAddress ?: ""

    // SharedViewModel color theme
    val lightVibrantColor by sharedViewModel.lightVibrantColor
    val darkVibrantColor by sharedViewModel.darkVibrantColor
    val vibrantColor by sharedViewModel.vibrantColor

    // Save receiver address from NFC to SharedViewModel
    LaunchedEffect(initialReceiverAddress) {
        initialReceiverAddress?.let { address ->
            // Save the NFC received address to SharedViewModel
            sharedViewModel.setReceiverWalletAddress(address)
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
            .padding(top = 50.dp)
    ) {
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 20.dp, vertical = 16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            item {
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
                            Icons.AutoMirrored.Rounded.ArrowBack,
                            contentDescription = "Back",
                            tint = darkVibrantColor,
                            modifier = Modifier.size(20.dp).clickable(
                                onClick = {
                                    navController.navigateUp()
                                }
                            )
                        )
                    }

                    Text(
                        text = "New Transaction",
                        fontSize = 22.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = darkVibrantColor
                    )

                    Spacer(modifier = Modifier.width(40.dp))
                }
            }

            item {
                // From Wallet (Read-only)
                CleanInputCard(
                    title = "From",
                    titleColor = darkVibrantColor,
                    content = {
                        OutlinedTextField(
                            value = if (currentWalletAddress.length > 20) {
                                "${currentWalletAddress.take(10)}...${currentWalletAddress.takeLast(10)}"
                            } else currentWalletAddress,
                            onValueChange = { },
                            enabled = false,
                            modifier = Modifier.fillMaxWidth(),
                            placeholder = { Text("Your wallet address", color = darkVibrantColor.copy(alpha = 0.5f)) },
                            colors = OutlinedTextFieldDefaults.colors(
                                disabledTextColor = darkVibrantColor.copy(alpha = 0.7f),
                                disabledBorderColor = lightVibrantColor.copy(alpha = 0.3f),
                                disabledPlaceholderColor = darkVibrantColor.copy(alpha = 0.5f),
                                focusedBorderColor = vibrantColor,
                                unfocusedBorderColor = lightVibrantColor.copy(alpha = 0.5f)
                            ),
                            shape = RoundedCornerShape(16.dp),
                            leadingIcon = {
                                Icon(
                                    imageVector = vectorResource(Res.drawable.rounded_wallet),
                                    contentDescription = null,
                                    tint = darkVibrantColor.copy(alpha = 0.6f),
                                    modifier = Modifier.size(20.dp)
                                )
                            }
                        )
                    }
                )
            }

            item {
                // To Wallet
                CleanInputCard(
                    title = "To",
                    titleColor = darkVibrantColor,
                    content = {
                        OutlinedTextField(
                            value = receiverAddress,
                            onValueChange = { receiverAddress = it },
                            modifier = Modifier.fillMaxWidth(),
                            placeholder = { Text("Enter recipient address", color = darkVibrantColor.copy(alpha = 0.5f)) },
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedTextColor = darkVibrantColor,
                                unfocusedTextColor = darkVibrantColor,
                                focusedBorderColor = vibrantColor,
                                unfocusedBorderColor = lightVibrantColor.copy(alpha = 0.5f),
                                cursorColor = vibrantColor
                            ),
                            shape = RoundedCornerShape(16.dp),
                            leadingIcon = {
                                Icon(
                                    Icons.AutoMirrored.Rounded.Send,
                                    contentDescription = null,
                                    tint = darkVibrantColor.copy(alpha = 0.6f),
                                    modifier = Modifier.size(20.dp)
                                )
                            },
                            trailingIcon = {
                                IconButton(
                                    onClick = { /* TODO: Implement QR scan */ },
                                    modifier = Modifier
                                        .size(32.dp)
                                        .clip(RoundedCornerShape(8.dp))
                                        .background(lightVibrantColor.copy(alpha = 0.2f))
                                ) {
                                    Icon(
                                        Icons.Default.QrCode,
                                        contentDescription = "Scan QR",
                                        tint = darkVibrantColor,
                                        modifier = Modifier.size(16.dp)
                                    )
                                }
                            }
                        )
                    }
                )
            }

            item {
                // Amount and Crypto Type
                CleanInputCard(
                    title = "Amount",
                    titleColor = darkVibrantColor,
                    content = {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(12.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            OutlinedTextField(
                                value = amount,
                                onValueChange = { amount = it },
                                modifier = Modifier.weight(1f),
                                placeholder = { Text("0.00", color = darkVibrantColor.copy(alpha = 0.5f)) },
                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedTextColor = darkVibrantColor,
                                    unfocusedTextColor = darkVibrantColor,
                                    focusedBorderColor = vibrantColor,
                                    unfocusedBorderColor = lightVibrantColor.copy(alpha = 0.5f),
                                    cursorColor = vibrantColor
                                ),
                                shape = RoundedCornerShape(16.dp),
                                leadingIcon = {
                                    Icon(
                                        Icons.Rounded.Money,
                                        contentDescription = null,
                                        tint = darkVibrantColor.copy(alpha = 0.6f),
                                        modifier = Modifier.size(20.dp)
                                    )
                                }
                            )

                            // APT Chip
                            Box(
                                modifier = Modifier
                                    .height(48.dp)
                                    .clip(RoundedCornerShape(28.dp))
                                    .background(darkVibrantColor)
                                    .padding(horizontal = 20.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = "APT",
                                    color = Color.White,
                                    fontWeight = FontWeight.Medium,
                                    fontSize = 14.sp
                                )
                            }
                        }
                    }
                )
            }

            item {
                // Schedule Toggle
                CleanInputCard(
                    title = "Schedule",
                    titleColor = darkVibrantColor,
                    content = {
                        Column(
                            verticalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = "Schedule for later",
                                    color = darkVibrantColor,
                                    fontSize = 16.sp
                                )
                                Switch(
                                    checked = isScheduled,
                                    onCheckedChange = { isScheduled = it },
                                    colors = SwitchDefaults.colors(
                                        checkedThumbColor = Color.White,
                                        checkedTrackColor = vibrantColor,
                                        uncheckedThumbColor = Color.White,
                                        uncheckedTrackColor = lightVibrantColor.copy(alpha = 0.3f)
                                    )
                                )
                            }

                            if (isScheduled) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                                ) {
                                    // Date Picker
                                    OutlinedTextField(
                                        value = selectedDate?.toString() ?: "",
                                        onValueChange = { },
                                        modifier = Modifier
                                            .weight(1f)
                                            .clickable { showDatePicker = true },
                                        placeholder = { Text("Date", color = darkVibrantColor.copy(alpha = 0.5f)) },
                                        enabled = false,
                                        colors = OutlinedTextFieldDefaults.colors(
                                            disabledTextColor = darkVibrantColor,
                                            disabledBorderColor = lightVibrantColor.copy(alpha = 0.3f),
                                            disabledPlaceholderColor = darkVibrantColor.copy(alpha = 0.5f)
                                        ),
                                        shape = RoundedCornerShape(16.dp),
                                        trailingIcon = {
                                            Icon(
                                                Icons.Default.DateRange,
                                                contentDescription = "Select Date",
                                                tint = darkVibrantColor.copy(alpha = 0.6f),
                                                modifier = Modifier.size(20.dp)
                                            )
                                        }
                                    )

                                    // Time Picker
                                    OutlinedTextField(
                                        value = selectedTime?.toString() ?: "",
                                        onValueChange = { },
                                        modifier = Modifier
                                            .weight(1f)
                                            .clickable { showTimePicker = true },
                                        placeholder = { Text("Time", color = darkVibrantColor.copy(alpha = 0.5f)) },
                                        enabled = false,
                                        colors = OutlinedTextFieldDefaults.colors(
                                            disabledTextColor = darkVibrantColor,
                                            disabledBorderColor = lightVibrantColor.copy(alpha = 0.3f),
                                            disabledPlaceholderColor = darkVibrantColor.copy(alpha = 0.5f)
                                        ),
                                        shape = RoundedCornerShape(16.dp),
                                        trailingIcon = {
                                            Icon(
                                                Icons.Default.AccessTime,
                                                contentDescription = "Select Time",
                                                tint = darkVibrantColor.copy(alpha = 0.6f),
                                                modifier = Modifier.size(20.dp)
                                            )
                                        }
                                    )
                                }
                            }
                        }
                    }
                )
            }

            item {
                // Notes
                CleanInputCard(
                    title = "Notes",
                    titleColor = darkVibrantColor,
                    content = {
                        OutlinedTextField(
                            value = notes,
                            onValueChange = { notes = it },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(100.dp),
                            placeholder = { Text("Add a note (optional)", color = darkVibrantColor.copy(alpha = 0.5f)) },
                            maxLines = 3,
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedTextColor = darkVibrantColor,
                                unfocusedTextColor = darkVibrantColor,
                                focusedBorderColor = vibrantColor,
                                unfocusedBorderColor = lightVibrantColor.copy(alpha = 0.5f),
                                cursorColor = vibrantColor
                            ),
                            shape = RoundedCornerShape(16.dp)
                        )
                    }
                )
            }

            item {
                // Error Message
                if (errorMessage != null) {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(
                            containerColor = Color.Red.copy(alpha = 0.1f)
                        ),
                        shape = RoundedCornerShape(16.dp)
                    ) {
                        Text(
                            text = errorMessage!!,
                            color = Color.Red,
                            modifier = Modifier.padding(16.dp),
                            textAlign = TextAlign.Center,
                            fontSize = 14.sp
                        )
                    }
                }
            }

            item {
                // Submit Button
                Button(
                    onClick = {
                        createTransaction(
                            sharedViewModel = sharedViewModel,
                            aptosViewModel = aptosViewModel,
                            currentWalletAddress = currentWalletAddress,
                            receiverAddress = receiverAddress,
                            amount = amount,
                            notes = notes,
                            isScheduled = isScheduled,
                            selectedDate = selectedDate,
                            selectedTime = selectedTime,
                            onLoading = { isLoading = it },
                            onError = { errorMessage = it },
                            onSuccess = {
                                navController.navigate(ConfirmationScreenRoute(
                                    senderAddress = currentWalletAddress,
                                    recipientAddress = receiverAddress,
                                    amount = amount,
                                    isSuccess = true,
                                    scheduledAt = if (isScheduled && selectedDate != null && selectedTime != null) {
                                        selectedDate!!.atTime(selectedTime!!).toInstant(TimeZone.currentSystemDefault()).toEpochMilliseconds()
                                    } else null,
                                    executedAt = Clock.System.now().toEpochMilliseconds(),
                                    note = notes
                                ))
                            }
                        )
                    },
                    enabled = !isLoading && receiverAddress.isNotBlank() && amount.isNotBlank() &&
                            (!isScheduled || (selectedDate != null && selectedTime != null)),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = darkVibrantColor,
                        contentColor = Color.White,
                        disabledContainerColor = lightVibrantColor.copy(alpha = 0.3f),
                        disabledContentColor = darkVibrantColor.copy(alpha = 0.5f)
                    ),
                    shape = RoundedCornerShape(16.dp)
                ) {
                    if (isLoading) {
                        Row(
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            CircularProgressIndicator(
                                modifier = Modifier.size(16.dp),
                                color = Color.White,
                                strokeWidth = 2.dp
                            )
                            Text(
                                text = "Creating...",
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Medium
                            )
                        }
                    } else {
                        Text(
                            text = "Send Transaction",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Medium
                        )
                    }
                }
            }

            item {
                Spacer(modifier = Modifier.height(80.dp)) // Bottom padding
            }
        }
    }

    // Date Picker Dialog
    if (showDatePicker) {
        val datePickerState = rememberDatePickerState()
        DatePickerDialog(
            onDismissRequest = { showDatePicker = false },
            confirmButton = {
                TextButton(
                    onClick = {
                        datePickerState.selectedDateMillis?.let { millis ->
                            selectedDate = Instant.fromEpochMilliseconds(millis)
                                .toLocalDateTime(TimeZone.currentSystemDefault()).date
                        }
                        showDatePicker = false
                    }
                ) {
                    Text("OK", color = darkVibrantColor, fontWeight = FontWeight.Medium)
                }
            },
            dismissButton = {
                TextButton(onClick = { showDatePicker = false }) {
                    Text("Cancel", color = darkVibrantColor.copy(alpha = 0.7f))
                }
            }
        ) {
            DatePicker(
                state = datePickerState,
                colors = DatePickerDefaults.colors(
                    containerColor = Color.White,
                    titleContentColor = darkVibrantColor,
                    headlineContentColor = darkVibrantColor,
                    weekdayContentColor = darkVibrantColor.copy(alpha = 0.7f),
                    subheadContentColor = darkVibrantColor.copy(alpha = 0.7f),
                    dayContentColor = darkVibrantColor,
                    selectedDayContentColor = Color.White,
                    selectedDayContainerColor = vibrantColor,
                    todayContentColor = darkVibrantColor,
                    todayDateBorderColor = vibrantColor
                )
            )
        }
    }

    // Time Picker Dialog
    if (showTimePicker) {
        val timePickerState = rememberTimePickerState()
        AlertDialog(
            onDismissRequest = { showTimePicker = false },
            confirmButton = {
                TextButton(
                    onClick = {
                        selectedTime = LocalTime(timePickerState.hour, timePickerState.minute)
                        showTimePicker = false
                    }
                ) {
                    Text("OK", color = darkVibrantColor, fontWeight = FontWeight.Medium)
                }
            },
            dismissButton = {
                TextButton(onClick = { showTimePicker = false }) {
                    Text("Cancel", color = darkVibrantColor.copy(alpha = 0.7f))
                }
            },
            text = {
                TimePicker(
                    state = timePickerState,
                    colors = TimePickerDefaults.colors(
                        containerColor = Color.White,
                        timeSelectorSelectedContainerColor = vibrantColor,
                        timeSelectorUnselectedContainerColor = lightVibrantColor.copy(alpha = 0.2f),
                        timeSelectorSelectedContentColor = Color.White,
                        timeSelectorUnselectedContentColor = darkVibrantColor
                    )
                )
            },
            containerColor = Color.White
        )
    }
}

@Composable
private fun CleanInputCard(
    title: String,
    titleColor: Color = Color.Black,
    content: @Composable () -> Unit
) {
    Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Text(
            text = title,
            fontSize = 16.sp,
            fontWeight = FontWeight.Medium,
            color = titleColor
        )
        content()
    }
}

@OptIn(ExperimentalTime::class)
private fun createTransaction(
    sharedViewModel: SharedViewModel,
    aptosViewModel: AptosViewModel,
    currentWalletAddress: String,
    receiverAddress: String,
    amount: String,
    notes: String,
    isScheduled: Boolean,
    selectedDate: LocalDate?,
    selectedTime: LocalTime?,
    onLoading: (Boolean) -> Unit,
    onError: (String) -> Unit,
    onSuccess: () -> Unit
) {
    // Validation
    if (currentWalletAddress.isBlank()) {
        onError("Current wallet address not found")
        return
    }

    if (receiverAddress.isBlank()) {
        onError("Please enter receiver's wallet address")
        return
    }

    if (amount.isBlank()) {
        onError("Please enter amount")
        return
    }

    val amountDouble = amount.toDoubleOrNull()
    if (amountDouble == null || amountDouble <= 0) {
        onError("Please enter a valid amount")
        return
    }

    if (isScheduled && (selectedDate == null || selectedTime == null)) {
        onError("Please select both date and time for scheduled transaction")
        return
    }

    onLoading(true)

    try {
        if (isScheduled && selectedDate != null && selectedTime != null) {
            // Create scheduled transaction
            val scheduledDateTime = selectedDate.atTime(selectedTime)
            val scheduledMillis = scheduledDateTime.toInstant(TimeZone.currentSystemDefault()).toEpochMilliseconds()

            sharedViewModel.createScheduledTransaction(
                fromWalletAddress = currentWalletAddress,
                toWalletAddress = receiverAddress,
                amount = amount,
                cryptoType = "APT",
                scheduledAt = scheduledMillis,
                notes = notes,
                accountId = 1, // TODO: Get actual account ID
                executedAt = Clock.System.now().toEpochMilliseconds()
            )
            onSuccess()
        } else {
            aptosViewModel.initWallet {
                aptosViewModel.sendCoins(
                    receiverAddress, amount = (amountDouble * 100_000_000).toULong()
                ) { _ ->
                    aptosViewModel.refreshBalance()
                    onSuccess()
                }
            }
            // Create immediate transaction
            sharedViewModel.createTransaction(
                fromWalletAddress = currentWalletAddress,
                toWalletAddress = receiverAddress,
                amount = amount,
                cryptoType = "APT",
                notes = notes,
                accountId = 1, // TODO: Get actual account ID
                status = TransactionStatus.COMPLETED,
                executedAt = Clock.System.now().toEpochMilliseconds()
            )
        }

        onLoading(false)
    } catch (e: Exception) {
        onLoading(false)
        onError(e.message ?: "Failed to create transaction")
    }
}