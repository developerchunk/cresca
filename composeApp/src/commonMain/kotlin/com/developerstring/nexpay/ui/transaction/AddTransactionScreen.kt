package com.developerstring.nexpay.ui.transaction

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowBack
import androidx.compose.material.icons.automirrored.rounded.Send
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.rounded.ArrowBack
import androidx.compose.material.icons.rounded.Money
import androidx.compose.material.icons.rounded.Send
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import com.developerstring.nexpay.ui.components.AnimatedGlowButton
import com.developerstring.nexpay.ui.theme.AppColors
import com.developerstring.nexpay.viewmodel.AptosViewModel
import com.developerstring.nexpay.viewmodel.SharedViewModel
import kotlinx.datetime.*
import kotlinx.datetime.format.DateTimeComponents
import kotlinx.serialization.Serializable
import nexpay.composeapp.generated.resources.Res
import nexpay.composeapp.generated.resources.rounded_wallet
import org.jetbrains.compose.resources.imageResource
import org.jetbrains.compose.resources.vectorResource
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
                            .background(Color.Black.copy(alpha = 0.05f))
                    ) {
                        Icon(
                            Icons.Rounded.ArrowBack,
                            contentDescription = "Back",
                            tint = Color.Black,
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
                        color = Color.Black
                    )

                    Spacer(modifier = Modifier.width(40.dp))
                }
            }

            item {
                // From Wallet (Read-only)
                CleanInputCard(
                    title = "From",
                    content = {
                        OutlinedTextField(
                            value = if (currentWalletAddress.length > 20) {
                                "${currentWalletAddress.take(10)}...${currentWalletAddress.takeLast(10)}"
                            } else currentWalletAddress,
                            onValueChange = { },
                            enabled = false,
                            modifier = Modifier.fillMaxWidth(),
                            placeholder = { Text("Your wallet address", color = Color.Gray) },
                            colors = OutlinedTextFieldDefaults.colors(
                                disabledTextColor = Color.Black.copy(alpha = 0.7f),
                                disabledBorderColor = Color.Black.copy(alpha = 0.1f),
                                disabledPlaceholderColor = Color.Gray,
                                focusedBorderColor = Color.Black,
                                unfocusedBorderColor = Color.Black.copy(alpha = 0.2f)
                            ),
                            shape = RoundedCornerShape(16.dp),
                            leadingIcon = {
                                Icon(
                                    imageVector = vectorResource(Res.drawable.rounded_wallet),
                                    contentDescription = null,
                                    tint = Color.Black.copy(alpha = 0.6f),
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
                    content = {
                        OutlinedTextField(
                            value = receiverAddress,
                            onValueChange = { receiverAddress = it },
                            modifier = Modifier.fillMaxWidth(),
                            placeholder = { Text("Enter recipient address", color = Color.Gray) },
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedTextColor = Color.Black,
                                unfocusedTextColor = Color.Black,
                                focusedBorderColor = Color.Black,
                                unfocusedBorderColor = Color.Black.copy(alpha = 0.2f),
                                cursorColor = Color.Black
                            ),
                            shape = RoundedCornerShape(16.dp),
                            leadingIcon = {
                                Icon(
                                    Icons.Rounded.Send,
                                    contentDescription = null,
                                    tint = Color.Black.copy(alpha = 0.6f),
                                    modifier = Modifier.size(20.dp)
                                )
                            },
                            trailingIcon = {
                                IconButton(
                                    onClick = { /* TODO: Implement QR scan */ },
                                    modifier = Modifier
                                        .size(32.dp)
                                        .clip(RoundedCornerShape(8.dp))
                                        .background(Color.Black.copy(alpha = 0.05f))
                                ) {
                                    Icon(
                                        Icons.Default.QrCode,
                                        contentDescription = "Scan QR",
                                        tint = Color.Black,
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
                                placeholder = { Text("0.00", color = Color.Gray) },
                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedTextColor = Color.Black,
                                    unfocusedTextColor = Color.Black,
                                    focusedBorderColor = Color.Black,
                                    unfocusedBorderColor = Color.Black.copy(alpha = 0.2f),
                                    cursorColor = Color.Black
                                ),
                                shape = RoundedCornerShape(16.dp),
                                leadingIcon = {
                                    Icon(
                                        Icons.Rounded.Money,
                                        contentDescription = null,
                                        tint = Color.Black.copy(alpha = 0.6f),
                                        modifier = Modifier.size(20.dp)
                                    )
                                }
                            )

                            // APT Chip
                            Box(
                                modifier = Modifier
                                    .height(48.dp)
                                    .clip(RoundedCornerShape(28.dp))
                                    .background(Color.Black)
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
                                    color = Color.Black,
                                    fontSize = 16.sp
                                )
                                Switch(
                                    checked = isScheduled,
                                    onCheckedChange = { isScheduled = it },
                                    colors = SwitchDefaults.colors(
                                        checkedThumbColor = Color.White,
                                        checkedTrackColor = Color.Black,
                                        uncheckedThumbColor = Color.White,
                                        uncheckedTrackColor = Color.Gray.copy(alpha = 0.3f)
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
                                        placeholder = { Text("Date", color = Color.Gray) },
                                        enabled = false,
                                        colors = OutlinedTextFieldDefaults.colors(
                                            disabledTextColor = Color.Black,
                                            disabledBorderColor = Color.Black.copy(alpha = 0.2f),
                                            disabledPlaceholderColor = Color.Gray
                                        ),
                                        shape = RoundedCornerShape(16.dp),
                                        trailingIcon = {
                                            Icon(
                                                Icons.Default.DateRange,
                                                contentDescription = "Select Date",
                                                tint = Color.Black.copy(alpha = 0.6f),
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
                                        placeholder = { Text("Time", color = Color.Gray) },
                                        enabled = false,
                                        colors = OutlinedTextFieldDefaults.colors(
                                            disabledTextColor = Color.Black,
                                            disabledBorderColor = Color.Black.copy(alpha = 0.2f),
                                            disabledPlaceholderColor = Color.Gray
                                        ),
                                        shape = RoundedCornerShape(16.dp),
                                        trailingIcon = {
                                            Icon(
                                                Icons.Default.AccessTime,
                                                contentDescription = "Select Time",
                                                tint = Color.Black.copy(alpha = 0.6f),
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
                    content = {
                        OutlinedTextField(
                            value = notes,
                            onValueChange = { notes = it },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(100.dp),
                            placeholder = { Text("Add a note (optional)", color = Color.Gray) },
                            maxLines = 3,
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedTextColor = Color.Black,
                                unfocusedTextColor = Color.Black,
                                focusedBorderColor = Color.Black,
                                unfocusedBorderColor = Color.Black.copy(alpha = 0.2f),
                                cursorColor = Color.Black
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
                            currentWalletAddress = currentWalletAddress,
                            receiverAddress = receiverAddress,
                            amount = amount,
                            notes = notes,
                            isScheduled = isScheduled,
                            selectedDate = selectedDate,
                            selectedTime = selectedTime,
                            onLoading = { isLoading = it },
                            onError = { errorMessage = it },
                            onSuccess = { navController.navigateUp() }
                        )
                    },
                    enabled = !isLoading && receiverAddress.isNotBlank() && amount.isNotBlank() &&
                             (!isScheduled || (selectedDate != null && selectedTime != null)),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color.Black,
                        contentColor = Color.White,
                        disabledContainerColor = Color.Gray.copy(alpha = 0.3f),
                        disabledContentColor = Color.Gray
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
                    Text("OK", color = Color.Black, fontWeight = FontWeight.Medium)
                }
            },
            dismissButton = {
                TextButton(onClick = { showDatePicker = false }) {
                    Text("Cancel", color = Color.Gray)
                }
            }
        ) {
            DatePicker(
                state = datePickerState,
                colors = DatePickerDefaults.colors(
                    containerColor = Color.White,
                    titleContentColor = Color.Black,
                    headlineContentColor = Color.Black,
                    weekdayContentColor = Color.Gray,
                    subheadContentColor = Color.Gray,
                    dayContentColor = Color.Black,
                    selectedDayContentColor = Color.White,
                    selectedDayContainerColor = Color.Black,
                    todayContentColor = Color.Black,
                    todayDateBorderColor = Color.Black
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
                    Text("OK", color = Color.Black, fontWeight = FontWeight.Medium)
                }
            },
            dismissButton = {
                TextButton(onClick = { showTimePicker = false }) {
                    Text("Cancel", color = Color.Gray)
                }
            },
            text = {
                TimePicker(
                    state = timePickerState,
                    colors = TimePickerDefaults.colors(
                        containerColor = Color.White,
                        timeSelectorSelectedContainerColor = Color.Black,
                        timeSelectorUnselectedContainerColor = Color.Gray.copy(alpha = 0.2f),
                        timeSelectorSelectedContentColor = Color.White,
                        timeSelectorUnselectedContentColor = Color.Black
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
            color = Color.Black
        )
        content()
    }
}

@OptIn(ExperimentalTime::class)
private fun createTransaction(
    sharedViewModel: SharedViewModel,
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
    onError("")

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
                accountId = 1 // TODO: Get actual account ID
            )
        } else {
            // Create immediate transaction
            sharedViewModel.createTransaction(
                fromWalletAddress = currentWalletAddress,
                toWalletAddress = receiverAddress,
                amount = amount,
                cryptoType = "APT",
                notes = notes,
                accountId = 1 // TODO: Get actual account ID
            )
        }

        onLoading(false)
        onSuccess()
    } catch (e: Exception) {
        onLoading(false)
        onError(e.message ?: "Failed to create transaction")
    }
}