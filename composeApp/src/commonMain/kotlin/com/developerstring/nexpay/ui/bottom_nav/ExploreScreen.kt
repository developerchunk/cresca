package com.developerstring.nexpay.ui.bottom_nav

import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import coil3.compose.AsyncImage
import com.developerstring.nexpay.data.currencies.CryptoCurrency
import com.developerstring.nexpay.data.model.BundleData
import com.developerstring.nexpay.data.model.CryptoBundle
import com.developerstring.nexpay.ui.screens.BundleDetailScreenRoute
import com.developerstring.nexpay.viewmodel.SharedViewModel
import kotlinx.serialization.Serializable

@Serializable
data object ExploreScreenRoute

@Composable
fun ExploreScreen(
    sharedViewModel: SharedViewModel,
    navController: NavController
) {
    val bundles = remember { BundleData.allBundles }
    val lightVibrantColor by sharedViewModel.lightVibrantColor
    val vibrantColor by sharedViewModel.vibrantColor
    val darkVibrantColor by sharedViewModel.darkVibrantColor
    val cryptoCurrencyList by sharedViewModel.cryptoCurrencyList.collectAsState()

    // Animation state
    var isVisible by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        isVisible = true
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
    ) {
        // Top header section - Phantom style
        PhantomStyleHeader(
            darkVibrantColor = darkVibrantColor,
            isVisible = isVisible
        )

        // Bundles content
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(horizontal = 24.dp, vertical = 8.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            itemsIndexed(bundles) { index, bundle ->
                AnimatedBundleCard(
                    bundle = bundle,
                    vibrantColor = vibrantColor,
                    lightVibrantColor = lightVibrantColor,
                    darkVibrantColor = darkVibrantColor,
                    cryptoCurrencyList = cryptoCurrencyList,
                    index = index,
                    isVisible = isVisible,
                    onClick = {
                        sharedViewModel.setSelectedBundle(bundle)
                        navController.navigate(BundleDetailScreenRoute(bundle.id))
                    }
                )
            }

            item {
                Spacer(modifier = Modifier.height(100.dp))
            }
        }
    }
}

@Composable
private fun PhantomStyleHeader(
    darkVibrantColor: Color,
    isVisible: Boolean
) {
    val headerAlpha by animateFloatAsState(
        targetValue = if (isVisible) 1f else 0f,
        animationSpec = tween(800, delayMillis = 200),
        label = "header_alpha"
    )

    val headerOffset by animateIntAsState(
        targetValue = if (isVisible) 0 else 50,
        animationSpec = tween(800, delayMillis = 200),
        label = "header_offset"
    )

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .alpha(headerAlpha)
            .offset(y = headerOffset.dp)
            .padding(horizontal = 24.dp)
            .padding(top = 60.dp, bottom = 32.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "Crypto Bundles",
            fontSize = 28.sp,
            fontWeight = FontWeight.ExtraBold,
            color = darkVibrantColor,
            textAlign = TextAlign.Center,
            letterSpacing = (-0.5).sp
        )

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = "Diversified portfolios\ncurated by experts",
            fontSize = 16.sp,
            fontWeight = FontWeight.Normal,
            color = darkVibrantColor.copy(alpha = 0.65f),
            textAlign = TextAlign.Center,
            lineHeight = 22.sp
        )
    }
}

@Composable
fun AnimatedBundleCard(
    bundle: CryptoBundle,
    vibrantColor: Color,
    lightVibrantColor: Color,
    darkVibrantColor: Color,
    cryptoCurrencyList: List<CryptoCurrency>,
    index: Int,
    isVisible: Boolean,
    onClick: () -> Unit
) {
    val animationDelay = 100 * index

    val cardAlpha by animateFloatAsState(
        targetValue = if (isVisible) 1f else 0f,
        animationSpec = tween(600, delayMillis = 300 + animationDelay),
        label = "card_alpha"
    )

    val cardScale by animateFloatAsState(
        targetValue = if (isVisible) 1f else 0.8f,
        animationSpec = tween(600, delayMillis = 300 + animationDelay),
        label = "card_scale"
    )

    val cardOffset by animateIntAsState(
        targetValue = if (isVisible) 0 else 50,
        animationSpec = tween(600, delayMillis = 300 + animationDelay),
        label = "card_offset"
    )

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .alpha(cardAlpha)
            .scale(cardScale)
            .offset(y = cardOffset.dp)
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(
            containerColor = lightVibrantColor.copy(alpha = 0.65f),
        )
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    Brush.verticalGradient(
                        colors = listOf(
                            lightVibrantColor.copy(alpha = 0.01f),
                            lightVibrantColor.copy(alpha = 0.02f)
                        )
                    )
                )
                .padding(24.dp)
        ) {
            Column(
                verticalArrangement = Arrangement.spacedBy(20.dp)
            ) {
                // Header with symbol badge
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = bundle.name,
                            fontSize = 22.sp,
                            fontWeight = FontWeight.Bold,
                            color = darkVibrantColor,
                            letterSpacing = (-0.3).sp
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = bundle.category.uppercase(),
                            fontSize = 12.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = vibrantColor,
                            letterSpacing = 0.8.sp
                        )
                    }

                    // Phantom-style symbol badge
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(16.dp))
                            .background(
                                Brush.horizontalGradient(
                                    colors = listOf(
                                        vibrantColor,
                                        vibrantColor.copy(alpha = 0.8f)
                                    )
                                )
                            )
                            .padding(horizontal = 16.dp, vertical = 10.dp)
                    ) {
                        Text(
                            text = bundle.symbol,
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White,
                            letterSpacing = 0.5.sp
                        )
                    }
                }

                // Description
                Text(
                    text = bundle.description,
                    fontSize = 15.sp,
                    color = darkVibrantColor.copy(alpha = 0.75f),
                    lineHeight = 22.sp,
                    maxLines = 3,
                    overflow = TextOverflow.Ellipsis,
                    fontWeight = FontWeight.Normal
                )

                // Stacked currency images
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    StackedCurrencyImages(
                        bundle = bundle,
                        cryptoCurrencyList = cryptoCurrencyList,
                        lightVibrantColor = lightVibrantColor
                    )
                    Text(
                        text = "${bundle.tokens.size} tokens",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Medium,
                        color = darkVibrantColor.copy(alpha = 0.6f),
                        letterSpacing = 0.2.sp
                    )
                }
            }
        }
    }
}

@Composable
private fun StackedCurrencyImages(
    bundle: CryptoBundle,
    cryptoCurrencyList: List<CryptoCurrency>,
    lightVibrantColor: Color
) {
    val bundleSymbols = bundle.tokens.map { it.symbol.uppercase() }
    val matchingCurrencies = bundleSymbols.mapNotNull { symbol ->
        cryptoCurrencyList.find { it.symbol.uppercase() == symbol }
    }.take(4) // Limit to 4 currencies for better visual balance

    Box(
        modifier = Modifier.height(36.dp)
    ) {
        matchingCurrencies.forEachIndexed { index, currency ->
            Box(
                modifier = Modifier
                    .offset(x = (index * 24).dp) // Slightly more spacing between images
                    .size(36.dp)
                    .clip(CircleShape)
                    .border(
                        width = 3.dp,
                        color = Color.White,
                        shape = CircleShape
                    )
                    .background(lightVibrantColor.copy(alpha = 0.1f))
            ) {
                AsyncImage(
                    model = currency.image,
                    contentDescription = currency.name,
                    modifier = Modifier
                        .fillMaxSize()
                        .clip(CircleShape),
                    contentScale = ContentScale.Crop
                )
            }
        }

        // Show "+X more" indicator if there are more currencies
        if (bundle.tokens.size > 4) {
            Box(
                modifier = Modifier
                    .offset(x = (4 * 24).dp)
                    .size(36.dp)
                    .clip(CircleShape)
                    .border(
                        width = 3.dp,
                        color = Color.White,
                        shape = CircleShape
                    )
                    .background(lightVibrantColor.copy(alpha = 0.8f)),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "+${bundle.tokens.size - 4}",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
            }
        }
    }
}

