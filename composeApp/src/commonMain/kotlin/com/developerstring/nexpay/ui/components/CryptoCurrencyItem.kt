package com.developerstring.nexpay.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil3.compose.AsyncImage
import com.developerstring.nexpay.data.currencies.CryptoCurrency
import com.developerstring.nexpay.utils.formatToTwoDecimalPlaces
import com.developerstring.nexpay.viewmodel.SharedViewModel

@Composable
fun CryptoCurrencyItem(
    modifier: Modifier = Modifier,
    cryptoCurrency: CryptoCurrency,
    sharedViewModel: SharedViewModel,
    darkColor: Color,
    onClick: (CryptoCurrency) -> Unit
) {
    // SharedViewModel color theme
    val lightVibrantColor by sharedViewModel.lightVibrantColor
    val vibrantColor by sharedViewModel.vibrantColor

    Card(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 5.dp, vertical = 4.dp)
            .clickable { onClick(cryptoCurrency) },
        colors = CardDefaults.cardColors(
            containerColor = vibrantColor.copy(alpha = 0.08f)
        ),
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(
            defaultElevation = 0.dp
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.weight(1f)
            ) {
                // Crypto image with subtle border
                Box(
                    modifier = Modifier
                        .size(42.dp)
                        .background(
                            lightVibrantColor.copy(alpha = 0.1f),
                            CircleShape
                        )
                        .padding(2.dp)
                ) {
                    AsyncImage(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(CircleShape),
                        model = cryptoCurrency.image,
                        contentDescription = null,
                        contentScale = ContentScale.Crop
                    )
                }

                Spacer(modifier = Modifier.width(12.dp))

                Column {
                    Text(
                        text = cryptoCurrency.name,
                        fontWeight = FontWeight.Medium,
                        fontSize = 15.sp,
                        color = Color.Black,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                    Text(
                        text = cryptoCurrency.symbol.uppercase(),
                        fontWeight = FontWeight.Normal,
                        fontSize = 13.sp,
                        color = Color.Black.copy(alpha = 0.7f)
                    )
                }
            }

            Column(
                horizontalAlignment = Alignment.End
            ) {
                Text(
                    text = "$${cryptoCurrency.current_price?.formatToTwoDecimalPlaces() ?: "0.00"}",
                    fontWeight = FontWeight.Medium,
                    fontSize = 15.sp,
                    color = Color.Black
                )

                val isPositive = (cryptoCurrency.price_change_percentage_24h ?: 0.0) >= 0
                val percentage = cryptoCurrency.price_change_percentage_24h?: 0.0

                Text(
                    text = "${if (isPositive) "+" else ""}${percentage}%",
                    fontWeight = FontWeight.Medium,
                    fontSize = 13.sp,
                    color = darkColor
                )
            }
        }

    }

}