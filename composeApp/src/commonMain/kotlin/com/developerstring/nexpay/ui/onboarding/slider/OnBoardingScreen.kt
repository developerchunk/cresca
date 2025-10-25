package com.developerstring.nexpay.ui.onboarding.slider

import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.developerstring.nexpay.ui.onboarding.create_profile.CreateProfileScreenRoute
import com.developerstring.nexpay.ui.screens.applock.PinSetupScreenRoute
import com.developerstring.nexpay.viewmodel.SharedViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.serialization.Serializable
import kotlin.time.Clock
import kotlin.time.ExperimentalTime

data class OnboardingSlide(
    val title: String,
    val subtitle: String,
    val description: String,
    val icon: ImageVector,
    val primaryColor: Color = Color.Black
)

@Serializable
data object OnBoardingScreenRoute

@OptIn(ExperimentalTime::class)
@Composable
fun OnBoardingScreen(
    sharedViewModel: SharedViewModel,
    navController: NavController
) {
    val slides = remember {
        listOf(
            OnboardingSlide(
                title = "Schedule Smart",
                subtitle = "Future Crypto Payments",
                description = "Schedule all your future crypto payments at once. Set up recurring payments, future transfers, and never miss a payment again.",
                icon = Icons.Default.Schedule
            ),
            OnboardingSlide(
                title = "Universal Platform",
                subtitle = "Person to Person & Vendors",
                description = "From person to person to vendors, all at one platform. Send crypto to friends, pay merchants, and manage all transactions seamlessly.",
                icon = Icons.Default.Group
            ),
            OnboardingSlide(
                title = "Powered by Aptos",
                subtitle = "Fast & Secure Network",
                description = "Built on the Aptos network for lightning-fast transactions with minimal fees. Experience the future of decentralized payments.",
                icon = Icons.Default.Bolt
            )
        )
    }

    val pagerState = rememberPagerState(pageCount = { slides.size })
    val coroutineScope = rememberCoroutineScope()

    // Auto-progress with pause on user interaction
    var isPaused by remember { mutableStateOf(false) }
    var progressStartTime by remember { mutableStateOf(Clock.System.now().toEpochMilliseconds()) }

    LaunchedEffect(pagerState.currentPage) {
        progressStartTime = Clock.System.now().toEpochMilliseconds()
        if (!isPaused) {
            delay(4000) // 4 seconds per slide
            if (pagerState.currentPage < slides.size - 1) {
                pagerState.animateScrollToPage(pagerState.currentPage + 1)
            }
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
            .padding(vertical = 40.dp)
    ) {
        Column(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Instagram-style story progress indicators
            StoryProgressIndicators(
                totalStories = slides.size,
                currentStory = pagerState.currentPage,
                modifier = Modifier.padding(horizontal = 20.dp, vertical = 40.dp)
            )

//            // App Logo/Brand
//            Column(
//                horizontalAlignment = Alignment.CenterHorizontally,
//                modifier = Modifier.padding(horizontal = 24.dp)
//            ) {
//                Box(
//                    modifier = Modifier
//                        .size(80.dp)
//                        .clip(RoundedCornerShape(20.dp))
//                        .background(Color.Black),
//                    contentAlignment = Alignment.Center
//                ) {
//                    Text(
//                        text = "NP",
//                        color = Color.White,
//                        fontSize = 32.sp,
//                        fontWeight = FontWeight.Bold
//                    )
//                }
//
//                Spacer(modifier = Modifier.height(16.dp))
//
//                Text(
//                    text = "NexPay",
//                    fontSize = 28.sp,
//                    fontWeight = FontWeight.Bold,
//                    color = Color.Black
//                )
//
//                Text(
//                    text = "Next-gen crypto payments",
//                    fontSize = 16.sp,
//                    color = Color.Gray,
//                    modifier = Modifier.padding(top = 4.dp)
//                )
//            }

            Spacer(modifier = Modifier.height(60.dp))

            // Horizontal Pager for slides
            HorizontalPager(
                state = pagerState,
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
                    .clickable {
                        isPaused = !isPaused
                    }
            ) { page ->
                OnboardingSlideContent(
                    slide = slides[page],
                    isActive = pagerState.currentPage == page
                )
            }

            Spacer(modifier = Modifier.height(40.dp))

            // Bottom navigation
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 24.dp, vertical = 20.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Skip button
                TextButton(
                    onClick = {
                        sharedViewModel.setOnboardingDone(true)
                        navController.navigate(PinSetupScreenRoute) {
                            popUpTo(OnBoardingScreenRoute) { inclusive = true }
                        }
                    }
                ) {
                    Text(
                        text = "Skip",
                        color = Color.Gray,
                        fontSize = 16.sp
                    )
                }

                // Page dots
                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    repeat(slides.size) { index ->
                        Box(
                            modifier = Modifier
                                .size(if (index == pagerState.currentPage) 12.dp else 8.dp)
                                .clip(CircleShape)
                                .background(
                                    if (index == pagerState.currentPage) Color.Black
                                    else Color.Gray.copy(alpha = 0.3f)
                                )
                        )
                    }
                }

                // Next/Get Started button
                Button(
                    onClick = {
                        coroutineScope.launch {
                            if (pagerState.currentPage < slides.size - 1) {
                                pagerState.animateScrollToPage(pagerState.currentPage + 1)
                            } else {
                                sharedViewModel.setOnboardingDone(true)
                                navController.navigate(CreateProfileScreenRoute) {
                                    popUpTo(OnBoardingScreenRoute) { inclusive = true }
                                }
                            }
                        }
                    },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color.Black,
                        contentColor = Color.White
                    ),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.height(48.dp)
                ) {
                    Text(
                        text = if (pagerState.currentPage == slides.size - 1) "Get Started" else "Next",
                        fontWeight = FontWeight.Medium
                    )
                }
            }
        }
    }
}

@Composable
fun StoryProgressIndicators(
    totalStories: Int,
    currentStory: Int,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        repeat(totalStories) { index ->
            Box(
                modifier = Modifier
                    .weight(1f)
                    .height(3.dp)
                    .clip(RoundedCornerShape(2.dp))
                    .background(Color.Gray.copy(alpha = 0.3f))
            ) {
                // Animated progress bar for current story
                if (index <= currentStory) {
                    val progress by animateFloatAsState(
                        targetValue = if (index < currentStory) 1f else if (index == currentStory) 1f else 0f,
                        animationSpec = if (index == currentStory) {
                            tween(durationMillis = 4000, easing = LinearEasing)
                        } else {
                            tween(durationMillis = 0)
                        },
                        label = "storyProgress"
                    )

                    Box(
                        modifier = Modifier
                            .fillMaxHeight()
                            .fillMaxWidth(progress)
                            .background(Color.Black)
                    )
                }
            }
        }
    }
}

@Composable
fun OnboardingSlideContent(
    slide: OnboardingSlide,
    isActive: Boolean
) {
    val scale by animateFloatAsState(
        targetValue = if (isActive) 1f else 0.95f,
        animationSpec = tween(300),
        label = "slideScale"
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        // Icon with animated background
        Box(
            modifier = Modifier
                .size(120.dp)
                .clip(CircleShape)
                .background(Color.Black.copy(alpha = 0.05f))
                .border(1.dp, Color.Black.copy(alpha = 0.1f), CircleShape),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = slide.icon,
                contentDescription = null,
                modifier = Modifier.size(48.dp),
                tint = Color.Black
            )
        }

        Spacer(modifier = Modifier.height(40.dp))

        // Title
        Text(
            text = slide.title,
            fontSize = 32.sp,
            fontWeight = FontWeight.Bold,
            color = Color.Black,
            textAlign = TextAlign.Center,
            lineHeight = 36.sp
        )

        Spacer(modifier = Modifier.height(8.dp))

        // Subtitle
        Text(
            text = slide.subtitle,
            fontSize = 18.sp,
            fontWeight = FontWeight.Medium,
            color = Color.Gray,
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(24.dp))

        // Description
        Text(
            text = slide.description,
            fontSize = 16.sp,
            color = Color.Black.copy(alpha = 0.8f),
            textAlign = TextAlign.Center,
            lineHeight = 24.sp,
            modifier = Modifier.padding(horizontal = 16.dp)
        )

        Spacer(modifier = Modifier.height(40.dp))

        // Feature highlights based on slide
        when (slide.title) {
            "Schedule Smart" -> {
                FeatureHighlight(
                    icon = Icons.Default.AccessTime,
                    text = "Set recurring payments"
                )
                Spacer(modifier = Modifier.height(12.dp))
                FeatureHighlight(
                    icon = Icons.Default.EventNote,
                    text = "Future-dated transfers"
                )
            }
            "Universal Platform" -> {
                FeatureHighlight(
                    icon = Icons.Default.Person,
                    text = "Send to friends & family"
                )
                Spacer(modifier = Modifier.height(12.dp))
                FeatureHighlight(
                    icon = Icons.Default.Store,
                    text = "Pay merchants directly"
                )
            }
            "Powered by Aptos" -> {
                FeatureHighlight(
                    icon = Icons.Default.Speed,
                    text = "Lightning-fast transactions"
                )
                Spacer(modifier = Modifier.height(12.dp))
                FeatureHighlight(
                    icon = Icons.Default.Security,
                    text = "Bank-level security"
                )
            }
        }
    }
}

@Composable
fun FeatureHighlight(
    icon: ImageVector,
    text: String
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            modifier = Modifier.size(20.dp),
            tint = Color.Black.copy(alpha = 0.7f)
        )
        Text(
            text = text,
            fontSize = 14.sp,
            color = Color.Black.copy(alpha = 0.7f),
            fontWeight = FontWeight.Medium
        )
    }
}