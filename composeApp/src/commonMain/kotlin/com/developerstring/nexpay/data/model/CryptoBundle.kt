package com.developerstring.nexpay.data.model

data class CryptoBundle(
    val id: Int,
    val name: String,
    val description: String,
    val tokens: List<BundleToken>,
    val symbol: String,
    val category: String,
    val backgroundColor: List<Long> = listOf(0xFF1a1a2e, 0xFF16213e)
)

// total value of the bundle can be computed by summing up the value of each token
// change in 24h can be computed based on the individual token changes and their weights in the bundle

data class BundleToken(
    val symbol: String,
    val percentage: Double,
)

// Sample data for the two bundles
object BundleData {
    val bundle35 = CryptoBundle(
        id = 5,
        name = "BTC, SOL, & ETH Growth Bundle",
        description = "Designed for long term BTC, SOL, & ETH bulls",
        symbol = "BTGL",
        category = "Bundle Token",
        backgroundColor = listOf(0xFF6366f1, 0xFF4f46e5),
        tokens = listOf(
            BundleToken("BTC", 50.0),
            BundleToken("ETH", 30.0),
            BundleToken("SOL", 20.0),
        )
    )

    val bundle5 = CryptoBundle(
        id = 35,
        name = "Stable Coin Bundle",
        description = "This bundle offers a balanced exposure to ICP and SUI, two promising cryptocurrencies each comprising 50% of the portfolio. It's designed for investors seeking to diversify their portfolio across these two specific assets.",
        symbol = "ICPS",
        category = "Bundle Token",
        backgroundColor = listOf(0xFF0ea5e9, 0xFF0284c7),
        tokens = listOf(
            BundleToken("BTC", 50.0),
            BundleToken("ETH", 30.0),
            BundleToken("SOL", 20.0),
        )
    )

    val allBundles = listOf(bundle35, bundle5)
}

