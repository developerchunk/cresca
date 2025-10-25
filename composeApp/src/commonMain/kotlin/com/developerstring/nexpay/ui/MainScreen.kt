package com.developerstring.nexpay.ui

import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowUpward
import androidx.compose.material.icons.rounded.ArrowDownward
import androidx.compose.material.icons.rounded.Close

import androidx.compose.material.icons.rounded.TapAndPlay
import androidx.compose.material3.BottomSheetDefaults
import androidx.compose.material3.BottomSheetScaffold
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SheetValue
import androidx.compose.material3.Text
import androidx.compose.material3.rememberBottomSheetScaffoldState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import androidx.navigation.NavController
import kotlinx.coroutines.launch
import androidx.navigation.NavDestination
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.developerstring.nexpay.navigation.bottom_nav.BottomNavGraph
import com.developerstring.nexpay.navigation.bottom_nav.BottomNavRoute
import com.developerstring.nexpay.ui.nfc.NFCScreenRoute

import com.developerstring.nexpay.viewmodel.AptosViewModel
import com.developerstring.nexpay.viewmodel.SharedViewModel
import kotlinx.serialization.Serializable
import org.jetbrains.compose.resources.painterResource

@Serializable
data object MainScreenRoute

@Serializable
data object SendReceiveScreenRoute

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainScreen(
    sharedViewModel: SharedViewModel,
    aptosViewModel: AptosViewModel,
) {
    val nav = rememberNavController()
    val bottomSheetScaffoldState = rememberBottomSheetScaffoldState()
    val scope = rememberCoroutineScope()

    BottomSheetScaffold(
        scaffoldState = bottomSheetScaffoldState,
        sheetContent = {
            SendReceiveBottomSheet(
                onClose = {
                    scope.launch {
                        bottomSheetScaffoldState.bottomSheetState.partialExpand()
                    }
                },
                navController = nav
            )
        },
        sheetPeekHeight = 0.dp,
        sheetDragHandle = null,
        containerColor = Color.Transparent,
        sheetContainerColor = Color.White,
        sheetShadowElevation = 16.dp,
        modifier = Modifier.fillMaxSize(),
        content = { paddingValues ->
            val isDimVisible by remember {
                derivedStateOf {
                    bottomSheetScaffoldState.bottomSheetState.targetValue == SheetValue.Expanded
                }
            }
            if (isDimVisible) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .zIndex(1f)
                        .background(color = BottomSheetDefaults.ScrimColor)
                )
            }
            Scaffold(
                bottomBar = {
                    BottomNavBar(
                        navController = nav,
                        onSendReceiveClick = {
                            scope.launch {
                                bottomSheetScaffoldState.bottomSheetState.expand()
                            }
                        }
                    )
                },
                content = { innerPadding ->
                    Column(
                        modifier = Modifier
                    ) {
                        BottomNavGraph(
                            navController = nav,
                            sharedViewModel = sharedViewModel,
                            aptosViewModel = aptosViewModel
                        )
                    }
                }
            )
        }
    )
}

@Composable
fun BottomNavBar(
    navController: NavHostController,
    onSendReceiveClick: () -> Unit = {}
) {
    val screens = listOf(
        BottomNavRoute.Wallet,
        BottomNavRoute.History,
        BottomNavRoute.Send,
        BottomNavRoute.Calender,
        BottomNavRoute.Profile,
    )

    val navStackBackEntry by navController.currentBackStackEntryAsState()
    val currentDestination = navStackBackEntry?.destination

    val bottomBarDestination = screens.any { it.route == currentDestination?.route }

    if (bottomBarDestination) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .navigationBarsPadding(),
            contentAlignment = Alignment.Center
        ) {
            // Clean DeFi-themed navigation bar - no floating, no rounded edges
            Box(
                modifier = Modifier
                    .height(72.dp)
                    .fillMaxWidth()
                    .background(Color.White)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 20.dp, vertical = 12.dp),
                    horizontalArrangement = Arrangement.SpaceEvenly,
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    screens.forEach { screen ->
                        CleanNavItem(
                            screen = screen,
                            currentDestination = currentDestination,
                            navController = navController,
                            onSendReceiveClick = onSendReceiveClick
                        )
                    }
                }
            }
            Box(modifier = Modifier.fillMaxWidth().height(1.dp).align(Alignment.TopCenter).background(Color.LightGray))
        }
    }
}

@Composable
fun CleanNavItem(
    screen: BottomNavRoute,
    currentDestination: NavDestination?,
    navController: NavHostController,
    onSendReceiveClick: () -> Unit = {}
) {
    val selected = currentDestination?.hierarchy?.any { it.route == screen.route } == true
    val isSendItem = screen.route == SendReceiveScreenRoute.toString()
    
    // Animation for scale when selected (only for non-Send items)
    val scale by animateFloatAsState(
        targetValue = if (selected && !isSendItem) 1.15f else 1f,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessMedium
        ),
        label = "scale_animation"
    )

    val interactionSource = remember { MutableInteractionSource() }

    Box(
        modifier = Modifier
            .size(48.dp)
            .clickable(
                interactionSource = interactionSource,
                indication = null,
                onClick = {
                    if (screen.route == SendReceiveScreenRoute.toString()) {
                        onSendReceiveClick()
                    } else {
                        navController.navigate(screen.route) {
                            popUpTo(navController.graph.findStartDestination().id)
                            launchSingleTop = true
                        }
                    }
                }
            ),
        contentAlignment = Alignment.Center
    ) {
        if (isSendItem) {
            // Send item with permanent black background card
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(Color.Black),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    painter = painterResource(resource = screen.icon),
                    contentDescription = "Send/Receive",
                    tint = Color.White,
                    modifier = Modifier.size(24.dp)
                )
            }
        } else {
            // Other items with scaling animation
            Icon(
                painter = painterResource(resource = screen.icon),
                contentDescription = screen.title,
                tint = if (selected) Color.Black else Color.Black.copy(alpha = 0.4f),
                modifier = Modifier
                    .size(24.dp)
                    .scale(scale)
            )
        }
    }
}

@Composable
fun SendReceiveBottomSheet(
    onClose: () -> Unit,
    navController: NavController
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color.White)
            .padding(24.dp)
    ) {
        // Header with close button
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 24.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Send & Receive",
                style = MaterialTheme.typography.headlineSmall.copy(
                    color = Color.Black,
                    fontWeight = FontWeight.Bold
                )
            )
            IconButton(
                onClick = onClose,
                modifier = Modifier.size(24.dp)
            ) {
                Icon(
                    imageVector = Icons.Rounded.Close,
                    contentDescription = "Close",
                    tint = Color.Black
                )
            }
        }

        // Send and Receive Options
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 16.dp),
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Send Option
            Card(
                modifier = Modifier
                    .weight(1f)
                    .clickable {
                        // TODO: Navigate to Send screen
                        onClose()
                    },
                colors = androidx.compose.material3.CardDefaults.cardColors(
                    containerColor = Color.White
                ),
                border = BorderStroke(1.dp, Color.Black.copy(alpha = 0.1f)),
                shape = RoundedCornerShape(16.dp)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Box(
                        modifier = Modifier
                            .size(56.dp)
                            .background(
                                Color.Black,
                                RoundedCornerShape(16.dp)
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.ArrowUpward,
                            contentDescription = "Send",
                            tint = Color.White,
                            modifier = Modifier.size(24.dp)
                        )
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    Text(
                        text = "Send",
                        style = MaterialTheme.typography.titleMedium.copy(
                            color = Color.Black,
                            fontWeight = FontWeight.SemiBold
                        )
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    Text(
                        text = "Transfer crypto to others",
                        style = MaterialTheme.typography.bodyMedium.copy(
                            color = Color.Gray,
                            textAlign = TextAlign.Center
                        )
                    )
                }
            }

            // Receive Option
            Card(
                modifier = Modifier
                    .weight(1f)
                    .clickable {
                        // TODO: Navigate to Receive screen
                        onClose()
                    },
                colors = androidx.compose.material3.CardDefaults.cardColors(
                    containerColor = Color.White
                ),
                border = BorderStroke(1.dp, Color.Black.copy(alpha = 0.1f)),
                shape = RoundedCornerShape(16.dp)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Box(
                        modifier = Modifier
                            .size(56.dp)
                            .background(
                                Color.Black,
                                RoundedCornerShape(16.dp)
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Rounded.ArrowDownward,
                            contentDescription = "Receive",
                            tint = Color.White,
                            modifier = Modifier.size(24.dp)
                        )
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    Text(
                        text = "Receive",
                        style = MaterialTheme.typography.titleMedium.copy(
                            color = Color.Black,
                            fontWeight = FontWeight.SemiBold
                        )
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    Text(
                        text = "Get crypto from others",
                        style = MaterialTheme.typography.bodyMedium.copy(
                            color = Color.Gray,
                            textAlign = TextAlign.Center
                        )
                    )
                }
            }
        }

        // NFC Tap to Pay Option
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 16.dp)
                .clickable {
                    navController?.navigate(NFCScreenRoute)
                    onClose()
                },
            colors = androidx.compose.material3.CardDefaults.cardColors(
                containerColor = Color.Black.copy(alpha = 0.05f)
            ),
            shape = RoundedCornerShape(16.dp)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(48.dp)
                        .background(
                            Color.Black,
                            RoundedCornerShape(12.dp)
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Rounded.TapAndPlay,
                        contentDescription = "QR Code",
                        tint = Color.White,
                        modifier = Modifier.size(20.dp)
                    )
                }

                Spacer(modifier = Modifier.width(16.dp))

                Column {
                    Text(
                        text = "Tap to Pay",
                        style = MaterialTheme.typography.titleMedium.copy(
                            color = Color.Black,
                            fontWeight = FontWeight.SemiBold
                        )
                    )

                    Text(
                        text = "Tap on receiver device with NFC to send crypto quickly",
                        style = MaterialTheme.typography.bodyMedium.copy(
                            color = Color.Gray
                        )
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))
    }
}
