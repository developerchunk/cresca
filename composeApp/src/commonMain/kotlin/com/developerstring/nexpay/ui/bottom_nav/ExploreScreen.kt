package com.developerstring.nexpay.ui.bottom_nav

import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
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

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
    ) {
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(horizontal = 20.dp, vertical = 16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            item {
                Column(
                    modifier = Modifier.padding(vertical = 16.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text(
                        text = "Crypto Bundles",
                        fontSize = 32.sp,
                        fontWeight = FontWeight.Bold,
                        color = darkVibrantColor,
                    )
                    Text(
                        text = "Diversified portfolios curated by experts",
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Medium,
                        color = darkVibrantColor.copy(alpha = 0.6f),
                    )
                }
            }

            items(bundles) { bundle ->
                BundleCard(
                    bundle = bundle,
                    vibrantColor = vibrantColor,
                    lightVibrantColor = lightVibrantColor,
                    darkVibrantColor = darkVibrantColor,
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
fun BundleCard(
    bundle: CryptoBundle,
    vibrantColor: Color,
    lightVibrantColor: Color,
    darkVibrantColor: Color,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(
            containerColor = lightVibrantColor.copy(alpha = 0.08f)
        ),
        elevation = CardDefaults.cardElevation(0.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Header
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = bundle.name,
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF1A1A2E)
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = bundle.category,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Medium,
                        color = Color(0xFF1A1A2E).copy(alpha = 0.6f)
                    )
                }

                // Symbol badge
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(12.dp))
                        .background(vibrantColor)
                        .padding(horizontal = 14.dp, vertical = 8.dp)
                ) {
                    Text(
                        text = bundle.symbol,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                }
            }

            // Description
            Text(
                text = bundle.description,
                fontSize = 14.sp,
                color = Color(0xFF1A1A2E).copy(alpha = 0.7f),
                lineHeight = 20.sp,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis
            )

            // Token count with icon-like visual
            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(6.dp)
                        .clip(CircleShape)
                        .background(vibrantColor)
                )
                Text(
                    text = "${bundle.tokens.size} tokens in this bundle",
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Medium,
                    color = Color(0xFF1A1A2E).copy(alpha = 0.5f)
                )
            }
        }
    }
}