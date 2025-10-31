package com.developerstring.nexpay.ui.transaction

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectHorizontalDragGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.automirrored.rounded.ArrowBack
import androidx.compose.material.icons.automirrored.rounded.Send
import androidx.compose.material.icons.rounded.QrCode
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.text.input.KeyboardType
import kotlinx.coroutines.delay
import kotlin.math.roundToInt
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
            .background(
                Brush.verticalGradient(
                    colors = listOf(
                        Color.White,
                        lightVibrantColor.copy(alpha = 0.03f)
                    )
                )
            )
    ) {
        Column(modifier = Modifier.fillMaxSize()) {
            // Header
            PhantomHeader(
                darkVibrantColor = darkVibrantColor,
                lightVibrantColor = lightVibrantColor,
                onBackClick = { navController.navigateUp() }
            )

            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(start = 24.dp, end = 24.dp, top = 8.dp, bottom = 120.dp),
                verticalArrangement = Arrangement.spacedBy(20.dp)
            ) {
            item {

                // From Wallet
                PhantomInputCard(
                    lightVibrantColor = lightVibrantColor,
                    darkVibrantColor = darkVibrantColor
                ) {
                    Column(
                        modifier = Modifier.fillMaxWidth(),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Text(
                            text = "From",
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Medium,
                            color = darkVibrantColor,
                            letterSpacing = 0.5.sp
                        )
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(12.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(40.dp)
                                    .clip(CircleShape)
                                    .background(lightVibrantColor.copy(alpha = 0.4f)),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = vectorResource(Res.drawable.rounded_wallet),
                                    contentDescription = null,
                                    tint = darkVibrantColor,
                                    modifier = Modifier.size(20.dp)
                                )
                            }
                            Text(
                                text = if (currentWalletAddress.length > 20) {
                                    "${currentWalletAddress.take(8)}...${currentWalletAddress.takeLast(8)}"
                                } else currentWalletAddress,
                                fontSize = 15.sp,
                                fontWeight = FontWeight.Medium,
                                color = darkVibrantColor
                            )
                        }

                        Spacer(Modifier.height(5.dp))

                        Column(
                            modifier = Modifier.fillMaxWidth(),
                            verticalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            Text(
                                text = "To",
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Medium,
                                color = darkVibrantColor,
                                letterSpacing = 0.5.sp
                            )
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clip(RoundedCornerShape(12.dp))
                                    .background(Color.White)
                            ) {
                                OutlinedTextField(
                                    value = receiverAddress,
                                    onValueChange = { receiverAddress = it },
                                    modifier = Modifier.fillMaxWidth(),
                                    placeholder = {
                                        Text(
                                            "Enter wallet address",
                                            color = darkVibrantColor.copy(alpha = 0.4f),
                                            fontSize = 15.sp
                                        )
                                    },
                                    colors = OutlinedTextFieldDefaults.colors(
                                        focusedTextColor = darkVibrantColor,
                                        unfocusedTextColor = darkVibrantColor,
                                        focusedBorderColor = Color.Transparent,
                                        unfocusedBorderColor = Color.Transparent,
                                        cursorColor = vibrantColor,
                                        focusedContainerColor = lightVibrantColor.copy(alpha = 0.05f),
                                        unfocusedContainerColor = lightVibrantColor.copy(alpha = 0.05f)
                                    ),
                                    shape = RoundedCornerShape(12.dp),
                                    singleLine = false,
                                    trailingIcon = {

                                        Row {
                                            IconButton(
                                                onClick = {
                                                    navController.navigate(QRScannerScreenRoute)
                                                },
                                                modifier = Modifier
                                                    .size(36.dp)
                                                    .clip(RoundedCornerShape(10.dp))
                                                    .background(lightVibrantColor.copy(alpha = 0.2f))
                                            ) {
                                                Icon(
                                                    Icons.Rounded.QrCode,
                                                    contentDescription = "Scan QR",
                                                    tint = darkVibrantColor,
                                                    modifier = Modifier.size(18.dp)
                                                )
                                            }

                                            Spacer(Modifier.width(10.dp))
                                        }
                                    }
                                )
                            }
                        }
                    }
                }
            }

            item {
                // Amount
                PhantomInputCard(
                    lightVibrantColor = lightVibrantColor,
                    darkVibrantColor = darkVibrantColor
                ) {
                    Column(
                        modifier = Modifier.fillMaxWidth(),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "Amount",
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Medium,
                                color = darkVibrantColor,
                                letterSpacing = 0.5.sp
                            )
                        }
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(12.dp))
                                .background(Color.White)
                        ) {
                            OutlinedTextField(
                                value = amount,
                                onValueChange = { amount = it },
                                modifier = Modifier.fillMaxWidth(),
                                placeholder = {
                                    Text(
                                        "0.00",
                                        color = darkVibrantColor.copy(alpha = 0.4f),
                                        fontSize = 24.sp,
                                        fontWeight = FontWeight.SemiBold
                                    )
                                },
                                textStyle = androidx.compose.ui.text.TextStyle(
                                    fontSize = 24.sp,
                                    fontWeight = FontWeight.SemiBold,
                                    color = darkVibrantColor
                                ),
                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedTextColor = darkVibrantColor,
                                    unfocusedTextColor = darkVibrantColor,
                                    focusedBorderColor = Color.Transparent,
                                    unfocusedBorderColor = Color.Transparent,
                                    cursorColor = vibrantColor,
                                    focusedContainerColor = lightVibrantColor.copy(alpha = 0.05f),
                                    unfocusedContainerColor = lightVibrantColor.copy(alpha = 0.05f)
                                ),
                                shape = RoundedCornerShape(12.dp),
                                singleLine = true,
                                trailingIcon = {
                                    Row {
                                        Box(
                                            modifier = Modifier
                                                .clip(RoundedCornerShape(20.dp))
                                                .background(darkVibrantColor)
                                                .padding(horizontal = 16.dp, vertical = 6.dp)
                                        ) {
                                            Text(
                                                text = "APT",
                                                color = Color.White,
                                                fontWeight = FontWeight.SemiBold,
                                                fontSize = 12.sp,
                                                letterSpacing = 0.5.sp
                                            )
                                        }

                                        Spacer(Modifier.width(10.dp))

                                    }
                                }
                            )
                        }
                    }
                }
            }

            item {
                // Schedule Toggle
                PhantomInputCard(
                    lightVibrantColor = lightVibrantColor,
                    darkVibrantColor = darkVibrantColor
                ) {
                    Column(
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                                Text(
                                    text = "Schedule Transaction",
                                    color = darkVibrantColor,
                                    fontSize = 15.sp,
                                    fontWeight = FontWeight.Medium
                                )
                                Text(
                                    text = "Send at a later time",
                                    color = darkVibrantColor,
                                    fontSize = 12.sp
                                )
                            }
                            Switch(
                                checked = isScheduled,
                                onCheckedChange = { isScheduled = it },
                                colors = SwitchDefaults.colors(
                                    checkedThumbColor = Color.White,
                                    checkedTrackColor = vibrantColor,
                                    uncheckedThumbColor = Color.White,
                                    uncheckedTrackColor = lightVibrantColor.copy(alpha = 0.3f),
                                    uncheckedBorderColor = vibrantColor
                                )
                            )
                        }

                        if (isScheduled) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(12.dp)
                            ) {
                                Box(
                                    modifier = Modifier
                                        .weight(1f)
                                        .clip(RoundedCornerShape(12.dp))
                                        .background(Color.White)
                                        .background(lightVibrantColor.copy(alpha = 0.05f))
                                        .clickable { showDatePicker = true }
                                        .padding(16.dp)
                                ) {
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                                    ) {
                                        Icon(
                                            Icons.Default.DateRange,
                                            contentDescription = "Date",
                                            tint = darkVibrantColor,
                                            modifier = Modifier.size(18.dp)
                                        )
                                        Text(
                                            text = selectedDate?.toString() ?: "Select Date",
                                            color = if (selectedDate != null) darkVibrantColor else darkVibrantColor.copy(
                                                alpha = 0.6f
                                            ),
                                            fontSize = 14.sp,
                                            fontWeight = FontWeight.Medium
                                        )
                                    }
                                }

                                Box(
                                    modifier = Modifier
                                        .weight(1f)
                                        .clip(RoundedCornerShape(12.dp))
                                        .background(Color.White)
                                        .background(lightVibrantColor.copy(alpha = 0.05f))
                                        .clickable { showTimePicker = true }
                                        .padding(16.dp)
                                ) {
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                                    ) {
                                        Icon(
                                            Icons.Default.AccessTime,
                                            contentDescription = "Time",
                                            tint = darkVibrantColor,
                                            modifier = Modifier.size(18.dp)
                                        )
                                        Text(
                                            text = selectedTime?.toString() ?: "Select Time",
                                            color = if (selectedTime != null) darkVibrantColor else darkVibrantColor.copy(
                                                alpha = 0.6f
                                            ),
                                            fontSize = 14.sp,
                                            fontWeight = FontWeight.Medium
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }

            item {
                // Notes
                PhantomInputCard(
                    lightVibrantColor = lightVibrantColor,
                    darkVibrantColor = darkVibrantColor
                ) {
                    Column(
                        modifier = Modifier.fillMaxWidth(),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Text(
                            text = "Note (Optional)",
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Medium,
                            color = darkVibrantColor,
                            letterSpacing = 0.5.sp
                        )
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(12.dp))
                                .background(Color.White)
                        ) {
                            OutlinedTextField(
                                value = notes,
                                onValueChange = { notes = it },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .heightIn(min = 75.dp),
                                placeholder = {
                                    Text(
                                        "Add a note...",
                                        color = darkVibrantColor,
                                        fontSize = 15.sp
                                    )
                                },
                                maxLines = 3,
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedTextColor = darkVibrantColor,
                                    unfocusedTextColor = darkVibrantColor,
                                    focusedBorderColor = Color.Transparent,
                                    unfocusedBorderColor = Color.Transparent,
                                    cursorColor = vibrantColor,
                                    focusedContainerColor = lightVibrantColor.copy(alpha = 0.05f),
                                    unfocusedContainerColor = lightVibrantColor.copy(alpha = 0.05f)
                                ),
                                shape = RoundedCornerShape(12.dp)
                            )
                        }
                    }
                }
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

            }
        }

        // Floating Swipe-to-Send Button at bottom
        Box(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .fillMaxWidth()
        ) {
            SwipeToSendButton(
            enabled = !isLoading && receiverAddress.isNotBlank() && amount.isNotBlank() &&
                    (!isScheduled || (selectedDate != null && selectedTime != null)),
            vibrantColor = vibrantColor,
            lightVibrantColor = lightVibrantColor,
            darkVibrantColor = darkVibrantColor,
            isProcessing = isLoading,
            onSendComplete = {
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
                        isLoading = false
                        navController.navigate(
                            ConfirmationScreenRoute(
                                senderAddress = currentWalletAddress,
                                recipientAddress = receiverAddress,
                                amount = amount,
                                isSuccess = true,
                                scheduledAt = if (isScheduled && selectedDate != null && selectedTime != null) {
                                    selectedDate!!.atTime(selectedTime!!).toInstant(TimeZone.currentSystemDefault())
                                        .toEpochMilliseconds()
                                } else null,
                                executedAt = Clock.System.now().toEpochMilliseconds(),
                                note = notes
                            )
                        )
                    }
                )
            }
        )
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
fun PhantomHeader(
    darkVibrantColor: Color,
    lightVibrantColor: Color,
    onBackClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 24.dp, vertical = 16.dp)
            .padding(top = 34.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        IconButton(
            onClick = onBackClick,
            modifier = Modifier
                .size(40.dp)
                .clip(RoundedCornerShape(12.dp))
                .background(lightVibrantColor.copy(alpha = 0.3f))
        ) {
            Icon(
                Icons.AutoMirrored.Rounded.ArrowBack,
                contentDescription = "Back",
                tint = darkVibrantColor,
                modifier = Modifier.size(20.dp)
            )
        }

        Text(
            text = "Send",
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold,
            color = darkVibrantColor,
            letterSpacing = (-0.3).sp
        )

        Spacer(modifier = Modifier.width(40.dp))
    }
}

@Composable
private fun PhantomInputCard(
    lightVibrantColor: Color,
    darkVibrantColor: Color,
    content: @Composable () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(20.dp))
            .background(lightVibrantColor.copy(alpha = 0.3f))
            .padding(20.dp)
    ) {
        content()
    }
}

@Composable
private fun SwipeToSendButton(
    enabled: Boolean,
    vibrantColor: Color,
    lightVibrantColor: Color,
    darkVibrantColor: Color,
    isProcessing: Boolean,
    onSendComplete: () -> Unit
) {
    var offsetX by remember { mutableStateOf(0f) }
    var isCompleted by remember { mutableStateOf(false) }
    var showProgress by remember { mutableStateOf(false) }

    val maxWidth = remember { mutableStateOf(0f) }
    val buttonSize = 60.dp

    val animatedOffset by animateFloatAsState(
        targetValue = offsetX,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessLow
        ),
        label = "offset_animation"
    )

    // Calculate slide progress (0 to 1)
    val slideProgress = remember(animatedOffset, maxWidth.value) {
        if (maxWidth.value > 0) {
            (animatedOffset / (maxWidth.value * 0.85f)).coerceIn(0f, 1f)
        } else 0f
    }

    // Animate track background color based on slide progress
    val trackBackgroundColor by animateColorAsState(
        targetValue = when {
            !enabled -> vibrantColor.copy(alpha = 0.1f)
            slideProgress > 0.1f -> vibrantColor.copy(alpha = 0.15f + (slideProgress * 0.35f))
            else -> vibrantColor.copy(alpha = 0.15f)
        },
        animationSpec = tween(200),
        label = "track_bg_color"
    )

    LaunchedEffect(isCompleted) {
        if (isCompleted && !isProcessing) {
            showProgress = true
            onSendComplete()
            delay(500)
            offsetX = 0f
            isCompleted = false
            showProgress = false
        }
    }

    Box(
        modifier = Modifier
            .padding(start = 35.dp, end = 35.dp, bottom = 50.dp)
            .background(Color.White,shape = RoundedCornerShape(100))
            .background(
                color = trackBackgroundColor,
                shape = RoundedCornerShape(100)
            )
            .fillMaxWidth()
            .height(60.dp)
            .onGloballyPositioned { coordinates ->
                maxWidth.value = coordinates.size.width.toFloat()
            }
    ) {
        // Text label that fades out
        Box(
            modifier = Modifier
                .fillMaxSize()
                .alpha((1f - (slideProgress * 1.3f)).coerceIn(0f, 1f)),
            contentAlignment = Alignment.Center
        ) {
            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    Icons.AutoMirrored.Rounded.Send,
                    contentDescription = null,
                    tint = if (enabled) {
                        if (slideProgress > 0.3f) Color.White.copy(alpha = 0.7f - (slideProgress * 0.5f))
                        else darkVibrantColor.copy(alpha = 0.6f)
                    } else darkVibrantColor.copy(alpha = 0.4f),
                    modifier = Modifier.size(18.dp)
                )
                Text(
                    text = if (enabled) "Slide to Send" else "Fill all fields",
                    color = if (enabled) {
                        if (slideProgress > 0.3f) Color.White.copy(alpha = 0.7f - (slideProgress * 0.5f))
                        else darkVibrantColor.copy(alpha = 0.6f)
                    } else darkVibrantColor.copy(alpha = 0.6f),
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Medium,
                    letterSpacing = 0.3.sp
                )
            }
        }

        // Floating sliding button
        val slidingModifier = if (enabled && !showProgress) {
            Modifier.pointerInput(Unit) {
                detectHorizontalDragGestures(
                    onDragEnd = {
                        val threshold = maxWidth.value - buttonSize.toPx() - 8f
                        if (offsetX >= threshold) {
                            isCompleted = true
                        } else {
                            offsetX = 0f
                        }
                    },
                    onHorizontalDrag = { _, dragAmount ->
                        val newOffset = (offsetX + dragAmount).coerceIn(
                            0f,
                            maxWidth.value - buttonSize.toPx()
                        )
                        offsetX = newOffset
                    }
                )
            }
        } else {
            Modifier
        }

        val buttonBackground = when {
            showProgress || isProcessing -> vibrantColor
            enabled -> vibrantColor
            else -> lightVibrantColor.copy(alpha = 0.25f)
        }

        Box(
            modifier = Modifier
                .offset { IntOffset(animatedOffset.roundToInt(), 0) }
                .size(buttonSize)
                .clip(CircleShape)
                .background(buttonBackground)
                .then(slidingModifier),
            contentAlignment = Alignment.Center
        ) {
            if (showProgress || isProcessing) {
                CircularProgressIndicator(
                    modifier = Modifier.size(22.dp),
                    color = Color.White,
                    strokeWidth = 2.dp
                )
            } else {
                Icon(
                    Icons.AutoMirrored.Rounded.Send,
                    contentDescription = "Send",
                    tint = Color.White,
                    modifier = Modifier.size(22.dp)
                )
            }
        }
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

    } catch (e: Exception) {
        onLoading(false)
        onError(e.message ?: "Failed to create transaction")
    }
}