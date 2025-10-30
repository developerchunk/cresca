package com.developerstring.nexpay.data.currencies

import kotlinx.serialization.Serializable

@Serializable
data class MarketChartResponseModel(
    val prices: List<List<Double>> // Nested lists: [timestamp, price]
)
