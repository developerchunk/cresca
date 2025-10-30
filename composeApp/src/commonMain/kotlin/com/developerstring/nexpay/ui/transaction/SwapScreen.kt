package com.developerstring.nexpay.ui.transaction

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowBack
import androidx.compose.material.icons.rounded.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import coil3.compose.AsyncImage
import com.developerstring.nexpay.data.currencies.CryptoCurrency
import com.developerstring.nexpay.utils.formatToTwoDecimalPlaces
import com.developerstring.nexpay.viewmodel.AptosViewModel
import com.developerstring.nexpay.viewmodel.SharedViewModel
import kotlinx.serialization.Serializable

@Serializable
object SwapScreenRoute

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SwapScreen(
    aptosViewModel: AptosViewModel,
    sharedViewModel: SharedViewModel,
    navController: NavController,
) {

    sharedViewModel.getCryptoCurrencies()

    val cryptoCurrencies by sharedViewModel.cryptoCurrencyList.collectAsState()
    val lightVibrantColor by sharedViewModel.lightVibrantColor
    val darkVibrantColor by sharedViewModel.darkVibrantColor
    val vibrantColor by sharedViewModel.vibrantColor

    // State for the swap
    var fromCurrency by remember { mutableStateOf<CryptoCurrency?>(null) }
    var toCurrency by remember { mutableStateOf<CryptoCurrency?>(null) }
    var fromAmount by remember { mutableStateOf("") }
    var toAmount by remember { mutableStateOf("") }
    var showBottomSheet by remember { mutableStateOf(false) }
    var selectingForFrom by remember { mutableStateOf(true) }
    var isSwapAnimating by remember { mutableStateOf(false) }
    var searchQuery by remember { mutableStateOf("") }

    // Animated rotation for swap icon
    val swapIconRotation by animateFloatAsState(
        targetValue = if (isSwapAnimating) 180f else 0f,
        animationSpec = tween(300),
        finishedListener = { isSwapAnimating = false }
    )

    // Calculate exchange rate and amounts
    LaunchedEffect(fromCurrency, toCurrency, fromAmount) {
        if (fromAmount.isNotEmpty() && fromCurrency != null && toCurrency != null) {
            try {
                val amount = fromAmount.toDouble()
                val fromPrice = fromCurrency?.current_price ?: 0.0
                val toPrice = toCurrency?.current_price ?: 0.0
                if (toPrice > 0) {
                    toAmount = ((amount * fromPrice) / toPrice).toString()
                }
            } catch (_: Exception) {
                toAmount = ""
            }
        } else {
            toAmount = ""
        }
    }

    val bottomSheetState = rememberModalBottomSheetState(skipPartiallyExpanded = false)

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
            .padding(top = 50.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 20.dp, vertical = 16.dp)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
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
                        .background(lightVibrantColor.copy(alpha = 0.25f))
                ) {
                    Icon(
                        Icons.AutoMirrored.Rounded.ArrowBack,
                        contentDescription = "Back",
                        tint = darkVibrantColor,
                        modifier = Modifier.size(20.dp)
                    )
                }

                Text(
                    text = "Swap",
                    fontSize = 22.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = darkVibrantColor
                )

                IconButton(
                    onClick = { /* Settings or options */ },
                    modifier = Modifier
                        .size(40.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .background(lightVibrantColor.copy(alpha = 0.25f))
                ) {
                    Icon(
                        Icons.Rounded.Settings,
                        contentDescription = "Settings",
                        tint = darkVibrantColor,
                        modifier = Modifier.size(20.dp)
                    )
                }
            }

            // Swap Container
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(24.dp),
                colors = CardDefaults.cardColors(
                    containerColor = lightVibrantColor.copy(alpha = 0.25f)
                ),
                elevation = CardDefaults.cardElevation(0.dp)
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(0.dp)
                ) {
                    // From Currency Section
                    SwapCurrencyCard(
                        label = "You pay",
                        currency = fromCurrency,
                        amount = fromAmount,
                        onAmountChange = { fromAmount = it },
                        onCurrencyClick = {
                            selectingForFrom = true
                            searchQuery = ""
                            showBottomSheet = true
                        },
                        lightVibrantColor = lightVibrantColor,
                        darkVibrantColor = darkVibrantColor,
                        vibrantColor = vibrantColor
                    )

                    // Swap Button
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 8.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        IconButton(
                            onClick = {
                                isSwapAnimating = true
                                val temp = fromCurrency
                                fromCurrency = toCurrency
                                toCurrency = temp
                                fromAmount = toAmount
                            },
                            modifier = Modifier
                                .size(44.dp)
                                .clip(CircleShape)
                                .background(Color.White)
                                .then(
                                    Modifier.background(
                                        lightVibrantColor.copy(alpha = 0.5f),
                                        CircleShape
                                    )
                                )
                        ) {
                            Icon(
                                Icons.Rounded.SwapVert,
                                contentDescription = "Swap",
                                tint = darkVibrantColor,
                                modifier = Modifier
                                    .size(24.dp)
                                    .rotate(swapIconRotation)
                            )
                        }
                    }

                    // To Currency Section
                    SwapCurrencyCard(
                        label = "You receive",
                        currency = toCurrency,
                        amount = toAmount,
                        onAmountChange = { },
                        onCurrencyClick = {
                            selectingForFrom = false
                            searchQuery = ""
                            showBottomSheet = true
                        },
                        lightVibrantColor = lightVibrantColor,
                        darkVibrantColor = darkVibrantColor,
                        vibrantColor = vibrantColor,
                        readOnly = true
                    )
                }
            }

            // Exchange Rate Info
            if (fromCurrency != null && toCurrency != null) {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = lightVibrantColor.copy(alpha = 0.28f)
                    )
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Text(
                            text = "Exchange Rate",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Medium,
                            color = darkVibrantColor.copy(alpha = 0.7f)
                        )

                        val rate = if ((toCurrency?.current_price ?: 0.0) > 0) {
                            (fromCurrency?.current_price ?: 0.0) / (toCurrency?.current_price
                                ?: 1.0)
                        } else 0.0

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "1 ${fromCurrency?.symbol?.uppercase()} = ${rate.formatToTwoDecimalPlaces()} ${toCurrency?.symbol?.uppercase()}",
                                fontSize = 16.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = darkVibrantColor
                            )

                            Icon(
                                Icons.Rounded.SwapHoriz,
                                contentDescription = null,
                                tint = vibrantColor,
                                modifier = Modifier.size(20.dp)
                            )
                        }

                        HorizontalDivider(
                            color = lightVibrantColor.copy(alpha = 0.3f),
                            thickness = 1.dp
                        )

                        SwapInfoRow(
                            label = "Network Fee",
                            value = "~$0.01",
                            darkVibrantColor = darkVibrantColor
                        )

                        SwapInfoRow(
                            label = "Estimated Time",
                            value = "~30 seconds",
                            darkVibrantColor = darkVibrantColor
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.weight(0.5f))

            // Review Swap Button
            Button(
                onClick = { /* Handle swap */ },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                enabled = fromCurrency != null && toCurrency != null && fromAmount.isNotEmpty(),
                shape = RoundedCornerShape(16.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = vibrantColor,
                    disabledContainerColor = lightVibrantColor.copy(alpha = 0.3f)
                )
            ) {
                Text(
                    text = "Review Swap",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = Color.White
                )
            }

            Spacer(modifier = Modifier.height(130.dp))
        }

        // Currency Selection Bottom Sheet
        if (showBottomSheet) {
            ModalBottomSheet(
                onDismissRequest = { showBottomSheet = false },
                sheetState = bottomSheetState,
                containerColor = Color.White,
                modifier = Modifier
                    .fillMaxWidth(),
                dragHandle = {
                    Surface(
                        modifier = Modifier
                            .padding(vertical = 8.dp)
                            .width(40.dp)
                            .height(4.dp),
                        shape = RoundedCornerShape(2.dp),
                        color = lightVibrantColor.copy(alpha = 0.3f)
                    ) {}
                }
            ) {
                CurrencySelectionBottomSheet(
                    currencies = cryptoCurrencies,
                    searchQuery = searchQuery,
                    onSearchQueryChange = { searchQuery = it },
                    onCurrencySelected = { currency ->
                        if (selectingForFrom) {
                            fromCurrency = currency
                        } else {
                            toCurrency = currency
                        }
                        showBottomSheet = false
                    },
                    lightVibrantColor = lightVibrantColor,
                    darkVibrantColor = darkVibrantColor,
                    vibrantColor = vibrantColor,
                    title = if (selectingForFrom) "Select token to pay" else "Select token to receive"
                )
            }
        }
    }
}

@Composable
fun SwapCurrencyCard(
    label: String,
    currency: CryptoCurrency?,
    amount: String,
    onAmountChange: (String) -> Unit,
    onCurrencyClick: () -> Unit,
    lightVibrantColor: Color,
    darkVibrantColor: Color,
    vibrantColor: Color,
    readOnly: Boolean = false
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(Color.White)
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Text(
            text = label,
            fontSize = 13.sp,
            fontWeight = FontWeight.Medium,
            color = darkVibrantColor.copy(alpha = 0.6f)
        )

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Currency Selector
            Card(
                modifier = Modifier
                    .clip(RoundedCornerShape(12.dp))
                    .clickable { onCurrencyClick() },
                colors = CardDefaults.cardColors(
                    containerColor = lightVibrantColor.copy(alpha = 0.15f)
                ),
                elevation = CardDefaults.cardElevation(0.dp)
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 10.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    if (currency != null) {
                        AsyncImage(
                            model = currency.image,
                            contentDescription = currency.name,
                            modifier = Modifier
                                .size(28.dp)
                                .clip(CircleShape),
                            contentScale = ContentScale.Crop
                        )
                        Text(
                            text = currency.symbol.uppercase(),
                            fontSize = 16.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = darkVibrantColor
                        )
                    } else {
                        Icon(
                            Icons.Rounded.Add,
                            contentDescription = null,
                            tint = darkVibrantColor,
                            modifier = Modifier.size(24.dp)
                        )
                        Text(
                            text = "Select",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = darkVibrantColor
                        )
                    }
                    Icon(
                        Icons.Rounded.KeyboardArrowDown,
                        contentDescription = null,
                        tint = darkVibrantColor.copy(alpha = 0.6f),
                        modifier = Modifier.size(20.dp)
                    )
                }
            }

            // Amount Input
            if (currency != null) {
                OutlinedTextField(
                    value = amount,
                    onValueChange = onAmountChange,
                    modifier = Modifier
                        .weight(1f)
                        .padding(start = 12.dp),
                    placeholder = {
                        Text(
                            "0.00",
                            color = darkVibrantColor.copy(alpha = 0.3f),
                            fontSize = 24.sp,
                            textAlign = TextAlign.End,
                            modifier = Modifier.fillMaxWidth()
                        )
                    },
                    textStyle = LocalTextStyle.current.copy(
                        fontSize = 24.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = darkVibrantColor,
                        textAlign = TextAlign.End
                    ),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                    singleLine = false,
                    enabled = !readOnly,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = Color.Transparent,
                        unfocusedBorderColor = Color.Transparent,
                        disabledBorderColor = Color.Transparent,
                        focusedContainerColor = Color.Transparent,
                        unfocusedContainerColor = Color.Transparent,
                        disabledContainerColor = Color.Transparent,
                        cursorColor = vibrantColor
                    )
                )
            }
        }

        // USD Equivalent
        if (currency != null && amount.isNotEmpty()) {
            val usdValue = try {
                amount.toDouble() * (currency.current_price ?: 0.0)
            } catch (_: Exception) {
                0.0
            }

            Text(
                text = "≈ $${usdValue.formatToTwoDecimalPlaces()}",
                fontSize = 14.sp,
                fontWeight = FontWeight.Medium,
                color = darkVibrantColor.copy(alpha = 0.5f),
                modifier = Modifier.align(Alignment.End)
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CurrencySelectionBottomSheet(
    currencies: List<CryptoCurrency>,
    searchQuery: String,
    onSearchQueryChange: (String) -> Unit,
    onCurrencySelected: (CryptoCurrency) -> Unit,
    lightVibrantColor: Color,
    darkVibrantColor: Color,
    vibrantColor: Color,
    title: String
) {
    val filteredCurrencies = remember(currencies, searchQuery) {
        if (searchQuery.isEmpty()) {
            currencies
        } else {
            currencies.filter {
                it.name.contains(searchQuery, ignoreCase = true) ||
                        it.symbol.contains(searchQuery, ignoreCase = true)
            }
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 20.dp)
    ) {

        Spacer(Modifier.height(35.dp))
        Text(
            text = title,
            fontSize = 20.sp,
            fontWeight = FontWeight.SemiBold,
            color = darkVibrantColor,
            modifier = Modifier.padding(bottom = 16.dp)
        )

        // Search Bar
        OutlinedTextField(
            value = searchQuery,
            onValueChange = onSearchQueryChange,
            modifier = Modifier.fillMaxWidth(),
            placeholder = { Text("Search tokens", color = darkVibrantColor.copy(alpha = 0.5f)) },
            leadingIcon = {
                Icon(
                    Icons.Rounded.Search,
                    contentDescription = null,
                    tint = darkVibrantColor.copy(alpha = 0.6f)
                )
            },
            trailingIcon = {
                if (searchQuery.isNotEmpty()) {
                    IconButton(onClick = { onSearchQueryChange("") }) {
                        Icon(
                            Icons.Rounded.Close,
                            contentDescription = "Clear",
                            tint = darkVibrantColor.copy(alpha = 0.6f),
                            modifier = Modifier.size(20.dp)
                        )
                    }
                }
            },
            shape = RoundedCornerShape(16.dp),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = vibrantColor.copy(alpha = 0.5f),
                unfocusedBorderColor = lightVibrantColor.copy(alpha = 0.3f),
                focusedContainerColor = lightVibrantColor.copy(alpha = 0.08f),
                unfocusedContainerColor = lightVibrantColor.copy(alpha = 0.08f),
                cursorColor = vibrantColor
            ),
            singleLine = true
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Currency List
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(filteredCurrencies) { currency ->
                CurrencySelectionItem(
                    currency = currency,
                    onClick = { onCurrencySelected(currency) },
                    lightVibrantColor = lightVibrantColor,
                    darkVibrantColor = darkVibrantColor
                )
            }

            item {
                Spacer(modifier = Modifier.height(100.dp))
            }
        }
    }
}

@Composable
fun CurrencySelectionItem(
    currency: CryptoCurrency,
    onClick: () -> Unit,
    lightVibrantColor: Color,
    darkVibrantColor: Color
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .clickable { onClick() },
        colors = CardDefaults.cardColors(
            containerColor = lightVibrantColor.copy(alpha = 0.08f)
        ),
        elevation = CardDefaults.cardElevation(0.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            AsyncImage(
                model = currency.image,
                contentDescription = currency.name,
                modifier = Modifier
                    .size(40.dp)
                    .clip(CircleShape),
                contentScale = ContentScale.Crop
            )

            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Text(
                    text = currency.name,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = darkVibrantColor,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Text(
                    text = currency.symbol.uppercase(),
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Normal,
                    color = darkVibrantColor.copy(alpha = 0.6f)
                )
            }

            Column(
                horizontalAlignment = Alignment.End,
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Text(
                    text = "$${(currency.current_price ?: 0.0).formatToTwoDecimalPlaces()}",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = darkVibrantColor
                )

                val priceChange = currency.price_change_percentage_24h ?: 0.0
                val isPositive = priceChange >= 0

                Text(
                    text = "${if (isPositive) "+" else ""}${priceChange.formatToTwoDecimalPlaces()}%",
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Medium,
                    color = if (isPositive) Color(0xFF4CAF50) else Color(0xFFE53935)
                )
            }
        }
    }
}

@Composable
fun SwapInfoRow(
    label: String,
    value: String,
    darkVibrantColor: Color
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = label,
            fontSize = 14.sp,
            fontWeight = FontWeight.Normal,
            color = darkVibrantColor.copy(alpha = 0.6f)
        )
        Text(
            text = value,
            fontSize = 14.sp,
            fontWeight = FontWeight.Medium,
            color = darkVibrantColor
        )
    }
}
