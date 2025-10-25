package com.developerstring.nexpay.ui.bottom_nav

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowLeft
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
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
import com.developerstring.nexpay.ui.transaction.AddTransactionScreenRoute
import com.developerstring.nexpay.viewmodel.SharedViewModel
import kotlinx.datetime.*
import kotlinx.serialization.Serializable
import kotlin.time.Clock
import kotlin.time.ExperimentalTime

@Serializable
data object CalenderScreenRoute

@OptIn(ExperimentalTime::class)
@Composable
fun CalenderScreen(
    sharedViewModel: SharedViewModel,
    navController: NavController,
) {
    val today = Clock.System.todayIn(TimeZone.currentSystemDefault())
    val endOfMonth = today.plus(1, DateTimeUnit.MONTH).minus(today.day, DateTimeUnit.DAY)
    val scheduledTransactions by sharedViewModel.getScheduledTransactions().collectAsState(initial = emptyList())

    // Filter transactions for current month (from today to end of month)
    val currentMonthTransactions = scheduledTransactions.filter { transaction ->
        val scheduledDate = Instant.fromEpochMilliseconds(transaction.scheduledAt ?: 0L)
            .toLocalDateTime(TimeZone.currentSystemDefault()).date
        scheduledDate >= today && scheduledDate <= endOfMonth
    }

    LazyColumn(
        modifier = Modifier.fillMaxSize().background(Color.White).padding(bottom = 80.dp),
        verticalArrangement = Arrangement.Top,
    ) {
        item {
            CalenderComponent(
                navController = navController,
                onDateSelected = { selectedDate ->
                    // Handle date selection - you can add your custom logic here
                    println("Selected date: $selectedDate")
                }
            )
        }

        item {

            Row(
                modifier = Modifier
                    .padding(top = 16.dp, bottom = 0.dp, start = 16.dp, end = 16.dp)
                    .fillMaxWidth()
                    .height(50.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Button(
                    onClick = {
                        navController.navigate(AddTransactionScreenRoute)
                    },
                    modifier = Modifier.weight(0.5f).fillMaxHeight(),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color.Black
                    ),
                    shape = RoundedCornerShape(28.dp)
                ) {
                    Text(
                        text = "Add Payment",
                        color = Color.White,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Medium
                    )
                }

                Button(
                    onClick = {
                        navController.navigate(AddTransactionScreenRoute)
                    },
                    modifier = Modifier.weight(0.5f).fillMaxHeight(),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color.Black
                    ),
                    shape = RoundedCornerShape(28.dp)
                ) {
                    Text(
                        text = "Execute",
                        color = Color.White,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Medium
                    )
                }
            }

        }

        item {
            ScheduledPaymentsSection(
                transactions = currentMonthTransactions,
                onViewAllClick = {
                    // Navigate to all scheduled payments screen
                    // You can implement this navigation as needed
                }
            )
        }
    }
}

@OptIn(ExperimentalTime::class)
@Composable
fun CalenderComponent(
    modifier: Modifier = Modifier,
    navController: NavController,
    onDateSelected: (LocalDate) -> Unit = {}
) {
    val today = Clock.System.todayIn(TimeZone.currentSystemDefault())
    var currentMonth by remember { mutableStateOf(today.month) }
    var currentYear by remember { mutableStateOf(today.year) }
    var selectedDate by remember { mutableStateOf<LocalDate?>(null) }

    // Animation state for smooth transitions
    var animationTrigger by remember { mutableStateOf(0) }
    val alpha by animateFloatAsState(
        targetValue = 1f,
        animationSpec = tween(300),
        label = "calendar_fade"
    )

    Card(
        modifier = modifier
            .padding(top = 50.dp)
            .fillMaxWidth()
            .padding(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 8.dp),
        shape = androidx.compose.foundation.shape.RoundedCornerShape(20.dp)
    ) {
        Column(
            modifier = Modifier.padding(20.dp)
        ) {
            // Calendar Header with navigation
            CalendarHeader(
                currentMonth = currentMonth,
                currentYear = currentYear,
                onPreviousMonth = {
                    animationTrigger++
                    if (currentMonth == Month.JANUARY) {
                        currentMonth = Month.DECEMBER
                        currentYear--
                    } else {
                        currentMonth = Month(currentMonth.number - 1)
                    }
                },
                onNextMonth = {
                    animationTrigger++
                    if (currentMonth == Month.DECEMBER) {
                        currentMonth = Month.JANUARY
                        currentYear++
                    } else {
                        currentMonth = Month(currentMonth.number + 1)
                    }
                },
                modifier = Modifier.alpha(alpha)
            )

            Spacer(modifier = Modifier.height(20.dp))

            // Days of week header
            DaysOfWeekHeader()

            Spacer(modifier = Modifier.height(16.dp))

            // Calendar Grid
            CalendarGrid(
                currentMonth = currentMonth,
                currentYear = currentYear,
                today = today,
                selectedDate = selectedDate,
                onDateClick = { date ->
                    selectedDate = date
                    onDateSelected(date)
                },
                modifier = Modifier.alpha(alpha)
            )

        }
    }
}

@Composable
private fun CalendarHeader(
    currentMonth: Month,
    currentYear: Int,
    onPreviousMonth: () -> Unit,
    onNextMonth: () -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Previous month button
        IconButton(
            onClick = onPreviousMonth,
            modifier = Modifier
                .size(40.dp)
                .clip(CircleShape)
                .background(Color(0xFFF8F9FA))
        ) {
            Icon(
                imageVector = Icons.AutoMirrored.Default.KeyboardArrowLeft,
                contentDescription = "Previous month",
                tint = Color(0xFF6C757D),
                modifier = Modifier.size(20.dp)
            )
        }

        // Month and year display
        Column(
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = currentMonth.name.lowercase().replaceFirstChar { it.uppercase() },
                style = MaterialTheme.typography.headlineMedium.copy(
                    fontWeight = FontWeight.Bold,
                    fontSize = 22.sp
                ),
                color = Color(0xFF212529)
            )
            Text(
                text = currentYear.toString(),
                style = MaterialTheme.typography.bodyMedium.copy(
                    fontWeight = FontWeight.Medium,
                    fontSize = 14.sp
                ),
                color = Color(0xFF6C757D)
            )
        }

        // Next month button
        IconButton(
            onClick = onNextMonth,
            modifier = Modifier
                .size(40.dp)
                .clip(CircleShape)
                .background(Color(0xFFF8F9FA))
        ) {
            Icon(
                imageVector = Icons.AutoMirrored.Default.KeyboardArrowRight,
                contentDescription = "Next month",
                tint = Color(0xFF6C757D),
                modifier = Modifier.size(20.dp)
            )
        }
    }
}

@Composable
private fun DaysOfWeekHeader() {
    val daysOfWeek = listOf("Sun", "Mon", "Tue", "Wed", "Thu", "Fri", "Sat")

    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceEvenly
    ) {
        daysOfWeek.forEach { day ->
            Text(
                text = day,
                style = MaterialTheme.typography.bodySmall.copy(
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 12.sp
                ),
                color = Color(0xFF6C757D),
                textAlign = TextAlign.Center,
                modifier = Modifier.weight(1f)
            )
        }
    }
}

@Composable
private fun CalendarGrid(
    currentMonth: Month,
    currentYear: Int,
    today: LocalDate,
    selectedDate: LocalDate?,
    onDateClick: (LocalDate) -> Unit,
    modifier: Modifier = Modifier
) {
    val firstDayOfMonth = LocalDate(currentYear, currentMonth, 1)
    val daysInMonth = firstDayOfMonth.daysUntil(
        firstDayOfMonth.plus(1, DateTimeUnit.MONTH)
    )

    // Get the day of week for the first day (0 = Monday, 6 = Sunday in kotlinx-datetime)
    // We need to convert to 0 = Sunday for our calendar
    val firstDayOfWeek = when (firstDayOfMonth.dayOfWeek) {
        DayOfWeek.SUNDAY -> 0
        DayOfWeek.MONDAY -> 1
        DayOfWeek.TUESDAY -> 2
        DayOfWeek.WEDNESDAY -> 3
        DayOfWeek.THURSDAY -> 4
        DayOfWeek.FRIDAY -> 5
        DayOfWeek.SATURDAY -> 6
    }

    // Calculate previous month's trailing dates
    val previousMonthDate = firstDayOfMonth.minus(1, DateTimeUnit.DAY)
    val daysInPreviousMonth = previousMonthDate.day

    Column(
        modifier = modifier.fillMaxWidth()
    ) {
        var dayCounter = 1
        var nextMonthCounter = 1

        // Create up to 6 weeks (6 rows)
        for (week in 0..5) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                for (dayOfWeek in 0..6) {
                    val dayPosition = week * 7 + dayOfWeek

                    when {
                        // Previous month days
                        dayPosition < firstDayOfWeek -> {
                            val previousMonthDay = daysInPreviousMonth - (firstDayOfWeek - dayPosition - 1)
                            val date = firstDayOfMonth.minus(firstDayOfWeek - dayPosition, DateTimeUnit.DAY)
                            CalendarDay(
                                day = previousMonthDay,
                                isCurrentMonth = false,
                                isToday = date == today,
                                isSelected = date == selectedDate,
                                onClick = { onDateClick(date) },
                                modifier = Modifier.weight(1f)
                            )
                        }
                        // Current month days
                        dayCounter <= daysInMonth -> {
                            val date = LocalDate(currentYear, currentMonth, dayCounter)
                            CalendarDay(
                                day = dayCounter,
                                isCurrentMonth = true,
                                isToday = date == today,
                                isSelected = date == selectedDate,
                                onClick = { onDateClick(date) },
                                modifier = Modifier.weight(1f)
                            )
                            dayCounter++
                        }
                        // Next month days
                        else -> {
                            val date = firstDayOfMonth.plus(daysInMonth + nextMonthCounter - 1, DateTimeUnit.DAY)
                            CalendarDay(
                                day = nextMonthCounter,
                                isCurrentMonth = false,
                                isToday = date == today,
                                isSelected = date == selectedDate,
                                onClick = { onDateClick(date) },
                                modifier = Modifier.weight(1f)
                            )
                            nextMonthCounter++
                        }
                    }
                }
            }

            // Add spacing between weeks
            if (week < 5) {
                Spacer(modifier = Modifier.height(4.dp))
            }

            // Break if we've shown all days of current month and filled the row
            if (dayCounter > daysInMonth && week >= 4) {
                break
            }
        }
    }
}

@Composable
private fun CalendarDay(
    day: Int,
    isCurrentMonth: Boolean,
    isToday: Boolean,
    isSelected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    label: String? = null
) {
    val backgroundColor = when {
        isSelected -> Color(0xFF7C3AED) // Purple from wallet
        isToday && !isSelected -> Color(0xFF7C3AED).copy(alpha = 0.15f) // Light purple
        else -> Color.Transparent
    }

    val textColor = when {
        isSelected -> Color.White
        isToday && !isSelected -> Color(0xFF7C3AED)
        isCurrentMonth -> Color(0xFF202124)
        else -> Color(0xFF9AA0A6)
    }

    Card(
        modifier = modifier
            .aspectRatio(1f)
            .padding(2.dp),
        onClick = onClick,
        colors = CardDefaults.cardColors(containerColor = backgroundColor),
        shape = RoundedCornerShape(8.dp) // Rounded corners instead of circle
    ) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Text(
                    text = day.toString(),
                    style = MaterialTheme.typography.bodyMedium.copy(
                        fontWeight = FontWeight.Medium, // Medium font weight
                        fontSize = 14.sp
                    ),
                    color = textColor,
                    textAlign = TextAlign.Center
                )

                // Optional label below the date
                if (label != null) {
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = label,
                        style = MaterialTheme.typography.labelSmall.copy(
                            fontSize = 8.sp,
                            fontWeight = FontWeight.Medium
                        ),
                        color = textColor.copy(alpha = 0.7f),
                        textAlign = TextAlign.Center,
                        maxLines = 1
                    )
                }
            }
        }
    }
}

@Composable
private fun ScheduledPaymentsSection(
    transactions: List<Transaction>,
    onViewAllClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp)
            .padding(top = 24.dp)
    ) {
        // Header with title and view all button
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Scheduled Payments",
                style = MaterialTheme.typography.headlineSmall.copy(
                    fontWeight = FontWeight.Bold,
                    fontSize = 20.sp
                ),
                color = Color(0xFF1A1B23)
            )

            AssistChip(
                onClick = onViewAllClick,
                label = {
                    Text(
                        text = "View All",
                        style = MaterialTheme.typography.bodySmall.copy(
                            fontWeight = FontWeight.Medium,
                            fontSize = 12.sp
                        ),
                        color = Color(0xFF7C3AED)
                    )
                },
                border = androidx.compose.foundation.BorderStroke(
                    width = 1.dp,
                    color = Color(0xFF7C3AED).copy(alpha = 0.3f)
                ),
                colors = AssistChipDefaults.assistChipColors(
                    containerColor = Color(0xFF7C3AED).copy(alpha = 0.08f)
                ),
                shape = RoundedCornerShape(20.dp)
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Transaction cards
        if (transactions.isEmpty()) {
            EmptyScheduledPaymentsCard()
        } else {
            transactions.forEach { transaction ->
                ScheduledPaymentCard(
                    transaction = transaction,
                    modifier = Modifier.padding(bottom = 12.dp)
                )
            }
        }

        Spacer(modifier = Modifier.height(24.dp))
    }
}

@Composable
private fun EmptyScheduledPaymentsCard() {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(120.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color(0xFFF8F9FA)
        ),
        shape = RoundedCornerShape(16.dp),
        border = androidx.compose.foundation.BorderStroke(
            width = 1.dp,
            color = Color(0xFFE9ECEF)
        )
    ) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Icon(
                    imageVector = Icons.Default.Schedule,
                    contentDescription = null,
                    tint = Color(0xFF9AA0A6),
                    modifier = Modifier.size(32.dp)
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "No scheduled payments this month",
                    style = MaterialTheme.typography.bodyMedium.copy(
                        fontWeight = FontWeight.Medium,
                        fontSize = 14.sp
                    ),
                    color = Color(0xFF9AA0A6),
                    textAlign = TextAlign.Center
                )
            }
        }
    }
}

@OptIn(ExperimentalTime::class)
@Composable
private fun ScheduledPaymentCard(
    transaction: Transaction,
    modifier: Modifier = Modifier
) {
    val scheduledDateTime = remember(transaction.scheduledAt) {
        transaction.scheduledAt?.let { timestamp ->
            Instant.fromEpochMilliseconds(timestamp)
                .toLocalDateTime(TimeZone.currentSystemDefault())
        }
    }

    Card(
        modifier = modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = Color.White
        ),
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(
            defaultElevation = 4.dp,
            hoveredElevation = 8.dp
        ),
        border = androidx.compose.foundation.BorderStroke(
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
                    color = Color(0xFF7C3AED).copy(alpha = 0.1f),
                    shape = RoundedCornerShape(8.dp),
                    modifier = Modifier.padding(end = 8.dp)
                ) {
                    Text(
                        text = transaction.cryptoType.uppercase(),
                        style = MaterialTheme.typography.labelSmall.copy(
                            fontWeight = FontWeight.Bold,
                            fontSize = 10.sp
                        ),
                        color = Color(0xFF7C3AED),
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                    )
                }

                // Status indicator
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(8.dp)
                            .background(
                                color = when (transaction.status) {
                                    TransactionStatus.SCHEDULED -> Color(0xFFFFA726)
                                    TransactionStatus.PENDING -> Color(0xFF42A5F5)
                                    TransactionStatus.COMPLETED -> Color(0xFF66BB6A)
                                    TransactionStatus.FAILED -> Color(0xFFEF5350)
                                    else -> Color(0xFF9E9E9E)
                                },
                                shape = CircleShape
                            )
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = transaction.status.name.lowercase()
                            .replaceFirstChar { it.uppercase() },
                        style = MaterialTheme.typography.labelSmall.copy(
                            fontWeight = FontWeight.Medium,
                            fontSize = 11.sp
                        ),
                        color = Color(0xFF6C757D)
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Wallet address
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
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
                    scheduledDateTime?.let { dateTime ->
                        Text(
                            text = "${dateTime.date.month.number}/${dateTime.date.day}/${dateTime.date.year}",
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
                }
            }

            // Optional gradient line at bottom
            Spacer(modifier = Modifier.height(12.dp))
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(2.dp)
                    .background(
                        brush = Brush.horizontalGradient(
                            colors = listOf(
                                Color(0xFF7C3AED).copy(alpha = 0.6f),
                                Color(0xFF06B6D4).copy(alpha = 0.6f),
                                Color(0xFF10B981).copy(alpha = 0.6f)
                            )
                        ),
                        shape = RoundedCornerShape(1.dp)
                    )
            )
        }
    }
}

