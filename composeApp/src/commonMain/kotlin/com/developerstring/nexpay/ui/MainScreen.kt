package com.developerstring.nexpay.ui

import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
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
import androidx.compose.foundation.layout.offset
import androidx.compose.ui.draw.shadow
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
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.colorspace.WhitePoint

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
import com.developerstring.nexpay.ui.transaction.AddTransactionScreenRoute
import com.developerstring.nexpay.ui.transaction.ReceivePaymentScreenRoute

import com.developerstring.nexpay.viewmodel.AptosViewModel
import com.developerstring.nexpay.viewmodel.SharedViewModel
import com.kmpalette.PaletteState
import com.kmpalette.color
import com.kmpalette.palette.graphics.Palette
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

    val imagePalette by sharedViewModel.imagePalette.collectAsState()

    val lightVibrantColor by sharedViewModel.lightVibrantColor
    val darkVibrantColor by sharedViewModel.darkVibrantColor
    val vibrantColor by sharedViewModel.vibrantColor

    BottomSheetScaffold(
        scaffoldState = bottomSheetScaffoldState,
        sheetContent = {
//            SendReceiveBottomSheet(
//                lightColor = lightVibrantColor,
//                darkColor = darkVibrantColor,
//                vibrant = vibrantColor,
//                onClose = {
//                    scope.launch {
//                        bottomSheetScaffoldState.bottomSheetState.partialExpand()
//                    }
//                },
//                navController = nav
//            )
        },
        sheetPeekHeight = 0.dp,
        sheetDragHandle = null,
        containerColor = Color.Unspecified,
        sheetContainerColor = Color.White,
        sheetShadowElevation = 16.dp,
        modifier = Modifier.fillMaxSize(),
        content = { _ ->
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
                        imagePalette = imagePalette,
                        navController = nav,
                    )
                },
                content = { _ ->
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
    imagePalette: PaletteState<ImageBitmap>?,
    navController: NavHostController,
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

    val gradient = Brush.verticalGradient(
        colors = listOf(
            Color(0x00FFFFFF),
            Color(0xBAFFFFFF),
            Color(0xE2FFFFFF),
            Color(0xFFFFFFFF)
        )
    )

    if (bottomBarDestination) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp),
            contentAlignment = Alignment.Center
        ) {

            Box(
                modifier = Modifier.height(100.dp).fillMaxWidth()
                    .background(brush = gradient)
                    .align(Alignment.BottomCenter)
            )

            // Floating rounded navigation bar
            Box(
                modifier = Modifier
                    .navigationBarsPadding()
                    .padding(top = 15.dp, bottom = 10.dp)
                    .fillMaxWidth()
                    .shadow(
                        elevation = 16.dp,
                        shape = RoundedCornerShape(28.dp),
                        clip = false
                    )
                    .background(color = Color.White, shape = RoundedCornerShape(28.dp))
                    .background(
                        color = imagePalette?.palette?.lightVibrantSwatch?.color?.copy(0.05f) ?: Color.White,
                        shape = RoundedCornerShape(28.dp)
                    )
                    .clip(RoundedCornerShape(28.dp)),
            ) {

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 12.dp, vertical = 8.dp),
                    horizontalArrangement = Arrangement.SpaceEvenly,
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    screens.forEach { screen ->
                        FloatingNavItem(
                            iconColor = imagePalette?.palette?.darkVibrantSwatch?.color ?: Color.Black,
                            screen = screen,
                            currentDestination = currentDestination,
                            navController = navController,
                        )
                    }
                }
            }

//            Box(
//                modifier = Modifier
//                    .size(52.dp)
//                    .shadow(
//                        elevation = 8.dp,
//                        shape = RoundedCornerShape(20.dp)
//                    )
//                    .clip(RoundedCornerShape(20.dp))
//                    .background(imagePalette?.palette?.darkVibrantSwatch?.color ?: Color.Black)
//                    .align(Alignment.TopCenter)
//                    .clickable(
//                        onClick = {
//
//                        }
//                    ),
//                contentAlignment = Alignment.Center
//            ) {
//                Icon(
//                    painter = painterResource(resource = BottomNavRoute.Send.icon),
//                    contentDescription = "Send/Receive",
//                    tint = Color.White,
//                    modifier = Modifier.size(28.dp)
//                )
//            }
        }
    }
}

@Composable
fun FloatingNavItem(
    iconColor: Color,
    screen: BottomNavRoute,
    currentDestination: NavDestination?,
    navController: NavHostController,
) {
    val selected = currentDestination?.hierarchy?.any { it.route == screen.route } == true

    // Slow and visible scale animation for icons
    val scale by animateFloatAsState(
        targetValue = 1f,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessVeryLow
        ),
        label = "scale_animation"
    )

    val interactionSource = remember { MutableInteractionSource() }

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .padding(vertical = 8.dp)
            .clickable(
                interactionSource = interactionSource,
                indication = null,
                onClick = {
                    navController.navigate(screen.route) {
                        popUpTo(navController.graph.findStartDestination().id)
                        launchSingleTop = true
                    }
                }
            )
    ) {
        Box(
            modifier = Modifier
                .size(28.dp),
            contentAlignment = Alignment.Center
        ) {

            Icon(
                painter = painterResource(resource = screen.icon),
                contentDescription = screen.title,
                tint = if (selected) iconColor else iconColor.copy(alpha = 0.4f),
                modifier = Modifier
                    .size(24.dp)
                    .scale(scale)
            )
        }

        val textHeight by animateDpAsState(
            targetValue = if (selected) 18.dp else 0.dp,
            animationSpec = spring(
                dampingRatio = Spring.DampingRatioMediumBouncy,
                stiffness = Spring.StiffnessVeryLow
            ),
            label = "text_height_animation"
        )

        Box(
            modifier = Modifier.height(textHeight),
            contentAlignment = Alignment.BottomCenter
        ) {
            this@Column.AnimatedVisibility(
                visible = selected,
                enter = slideInVertically(
                    initialOffsetY = { fullHeight -> fullHeight / 2 },
                    animationSpec = spring(
                        dampingRatio = Spring.DampingRatioMediumBouncy,
                        stiffness = Spring.StiffnessVeryLow
                    )
                ) + fadeIn(
                    animationSpec = spring(
                        dampingRatio = Spring.DampingRatioMediumBouncy,
                        stiffness = Spring.StiffnessVeryLow
                    )
                ),
                exit = slideOutVertically(
                    targetOffsetY = { fullHeight -> fullHeight / 2 },
                    animationSpec = spring(
                        dampingRatio = Spring.DampingRatioMediumBouncy,
                        stiffness = Spring.StiffnessVeryLow
                    )
                ) + fadeOut(
                    animationSpec = spring(
                        dampingRatio = Spring.DampingRatioMediumBouncy,
                        stiffness = Spring.StiffnessVeryLow
                    )
                )
            ) {
                Text(
                    text = screen.title,
                    style = MaterialTheme.typography.labelSmall.copy(
                        color = iconColor,
                        fontWeight = FontWeight.SemiBold
                    ),
                    modifier = Modifier.padding(top = 2.dp)
                )
            }
        }
    }
}

@Composable
fun SendReceiveBotthomSheet(
    lightColor: Color,
    darkColor: Color,
    vibrant: Color,
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
                    color = darkColor,
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
                    tint = darkColor
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
                        navController.navigate(AddTransactionScreenRoute())
                        onClose()
                    },
                colors = androidx.compose.material3.CardDefaults.cardColors(
                    containerColor = Color.White
                ),
                border = BorderStroke(1.dp, darkColor.copy(alpha = 0.1f)),
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
                                darkColor,
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
                            color = darkColor,
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
                        navController.navigate(ReceivePaymentScreenRoute)
                        onClose()
                    },
                colors = androidx.compose.material3.CardDefaults.cardColors(
                    containerColor = Color.White
                ),
                border = BorderStroke(1.dp, darkColor.copy(alpha = 0.1f)),
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
                                darkColor,
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
                            color = darkColor,
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
                    navController.navigate(NFCScreenRoute)
                    onClose()
                },
            colors = androidx.compose.material3.CardDefaults.cardColors(
                containerColor = darkColor.copy(alpha = 0.05f)
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
                            darkColor,
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
                            color = darkColor,
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
