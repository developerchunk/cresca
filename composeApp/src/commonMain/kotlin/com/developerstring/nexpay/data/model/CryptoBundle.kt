package com.developerstring.nexpay.data.model

data class CryptoBundle(
    val id: Int,
    val name: String,
    val description: String,
    val totalValue: String,
    val change24h: Double,
    val tokens: List<BundleToken>,
    val symbol: String,
    val category: String,
    val backgroundColor: List<Long> = listOf(0xFF1a1a2e, 0xFF16213e)
)

data class BundleToken(
    val symbol: String,
    val name: String,
    val percentage: Double,
    val value: String,
    val iconUrl: String = ""
)

// Sample data for the two bundles
object BundleData {
    val bundle35 = CryptoBundle(
        id = 5,
        name = "Gold Standard 2.0",
        description = "Gold Standard 2.0: a 50/50 blend of Bitcoin and gold. BTC’s upside + gold’s resilience in one simple allocation. Hard money, two ways",
        totalValue = "$34.69",
        change24h = 0.00,
        symbol = "BTGL",
        category = "Bundle Token",
        backgroundColor = listOf(0xFF6366f1, 0xFF4f46e5),
        tokens = listOf(
            BundleToken("BTC", "Bitcoin", 51.6, "$17.96", ""),
            BundleToken("XAUT", "Gold", 48.4, "$16.87", ""),
        )
    )

    val bundle5 = CryptoBundle(
        id = 35,
        name = "ICP and SUI Balanced Bundle",
        description = "This bundle offers a balanced exposure to ICP and SUI, two promising cryptocurrencies each comprising 50% of the portfolio. It's designed for investors seeking to diversify their portfolio across these two specific assets.",
        totalValue = "$55.29",
        change24h = 0.00,
        symbol = "ICPS",
        category = "Bundle Token",
        backgroundColor = listOf(0xFF0ea5e9, 0xFF0284c7),
        tokens = listOf(
            BundleToken("ICP", "Internet Computer", 50.4, "$27.81", ""),
            BundleToken("SUI", "Sui", 49.6, "$27.36", ""),
        )
    )

    val allBundles = listOf(bundle35, bundle5)
}

