# Bundle Trading Navigation Flow

## Screen Flow Diagram

```
┌─────────────────────────────────────────────────────────────────────┐
│                         ExploreScreen                                │
│  - Browse all crypto bundles                                         │
│  - View categories (DeFi, Layer 1, Meme Coins, etc.)               │
│  - See bundle cards with basic info                                 │
└─────────────────────────────────┬───────────────────────────────────┘
                                  │
                                  │ Click Bundle Card
                                  ▼
┌─────────────────────────────────────────────────────────────────────┐
│                      BundleDetailScreen                              │
│  ┌───────────────────────────────────────────────────────────────┐ │
│  │ Bundle Header Card (with shimmer animation)                   │ │
│  │ - Bundle name, symbol, category                               │ │
│  │ - Total bundle value (calculated from live data)              │ │
│  │ - 24h performance with trending icon                          │ │
│  └───────────────────────────────────────────────────────────────┘ │
│                                                                       │
│  ┌───────────────────────────────────────────────────────────────┐ │
│  │ Token Items (for each token in bundle)                        │ │
│  │ - Token symbol and name                                        │ │
│  │ - Current price                                                │ │
│  │ - Allocation percentage                                        │ │
│  │ - 24h price change                                             │ │
│  └───────────────────────────────────────────────────────────────┘ │
│                                                                       │
│  ┌───────────────────────────────────────────────────────────────┐ │
│  │ Allocation Chart                                               │ │
│  │ - Donut chart visualization                                    │ │
│  │ - Legend with percentages                                      │ │
│  └───────────────────────────────────────────────────────────────┘ │
│                                                                       │
│  ┌───────────────────────────────────────────────────────────────┐ │
│  │ Trading Actions                                                │ │
│  │  ┌─────────────────┐    ┌─────────────────┐                  │ │
│  │  │   LONG BUTTON   │    │  SHORT BUTTON   │                  │ │
│  │  │   🟢 Green      │    │   🔴 Red        │                  │ │
│  │  └────────┬────────┘    └────────┬────────┘                  │ │
│  └───────────┼──────────────────────┼───────────────────────────┘ │
└──────────────┼──────────────────────┼─────────────────────────────┘
               │                      │
               │                      │
        Click Long            Click Short
               │                      │
               └──────────┬───────────┘
                          ▼
┌─────────────────────────────────────────────────────────────────────┐
│                    ExecuteBundleScreen                               │
│                                                                       │
│  ┌───────────────────────────────────────────────────────────────┐ │
│  │ Header: [← Back]         LONG/SHORT 🟢/🔴                     │ │
│  └───────────────────────────────────────────────────────────────┘ │
│                                                                       │
│  ┌───────────────────────────────────────────────────────────────┐ │
│  │ Bundle Info Card                                               │ │
│  │ - Bundle name and symbol                                       │ │
│  │ - Current value                                                │ │
│  └───────────────────────────────────────────────────────────────┘ │
│                                                                       │
│  ┌───────────────────────────────────────────────────────────────┐ │
│  │ Pay Amount                                                     │ │
│  │ ┌─────────────────────────────────────────┐                   │ │
│  │ │  [Input Field]  USDC                    │                   │ │
│  │ └─────────────────────────────────────────┘                   │ │
│  └───────────────────────────────────────────────────────────────┘ │
│                                                                       │
│  ┌───────────────────────────────────────────────────────────────┐ │
│  │ Leverage                              [1x-150x]               │ │
│  │ ═════════════○═════════════════════ 1x                        │ │
│  │ Slide to adjust leverage (1x-150x)                            │ │
│  └───────────────────────────────────────────────────────────────┘ │
│                                                                       │
│  ┌───────────────────────────────────────────────────────────────┐ │
│  │ Position Size                                                  │ │
│  │ ┌─────────────────────────────────────────┐                   │ │
│  │ │  [Calculated Value]  USDC               │                   │ │
│  │ └─────────────────────────────────────────┘                   │ │
│  └───────────────────────────────────────────────────────────────┘ │
│                                                                       │
│  ┌───────────────────────────────────────────────────────────────┐ │
│  │ ⚠️  Minimum pay amount is 3 USDC                              │ │
│  │    (Shows only if amount < 3 USDC)                            │ │
│  └───────────────────────────────────────────────────────────────┘ │
│                                                                       │
│  ┌───────────────────────────────────────────────────────────────┐ │
│  │            [  Open LONG/SHORT Position  ]                     │ │
│  │         (Enabled only if amount >= 3 USDC)                    │ │
│  └───────────────────────────────────────────────────────────────┘ │
└─────────────────────────────────────────────────────────────────────┘
```

## Navigation Code Examples

### From ExploreScreen to BundleDetailScreen:
```kotlin
navController.navigate(BundleDetailScreenRoute(bundleId = bundle.id))
```

### From BundleDetailScreen to ExecuteBundleScreen:
```kotlin
// Long trade
navController.navigate(ExecuteBundleScreenRoute(bundleId = bundleId, tradeType = "LONG"))

// Short trade
navController.navigate(ExecuteBundleScreenRoute(bundleId = bundleId, tradeType = "SHORT"))
```

## Data Passed Between Screens

### BundleDetailScreenRoute:
```kotlin
@Serializable
data class BundleDetailScreenRoute(val bundleId: Int)
```

### ExecuteBundleScreenRoute:
```kotlin
@Serializable
data class ExecuteBundleScreenRoute(
    val bundleId: Int,
    val tradeType: String  // "LONG" or "SHORT"
)
```

## Color Coding by Trade Type

| Trade Type | Primary Color | Icon |
|------------|---------------|------|
| LONG       | Green #4ADE80 | ⬆️ TrendingUp |
| SHORT      | Red #EF4444   | ⬇️ TrendingDown |

## User Actions at Each Screen

### ExploreScreen:
- ✅ Browse bundles
- ✅ Click bundle to view details

### BundleDetailScreen:
- ✅ View bundle information
- ✅ See token allocations
- ✅ Check current prices
- ✅ Review 24h performance
- ✅ Click Long button
- ✅ Click Short button
- ✅ Navigate back

### ExecuteBundleScreen:
- ✅ Enter pay amount
- ✅ Adjust leverage (slider)
- ✅ View calculated position size
- ✅ Execute trade (if valid)
- ✅ Navigate back

## Validation Flow

```
User enters amount
       ↓
Is amount >= 3 USDC?
       ↓
   Yes → Enable button, hide warning
       ↓
   No → Disable button, show warning
```

## Data Sources

### Market Data (via SharedViewModel):
- Real-time crypto prices
- 24h price changes
- Token metadata (name, symbol)

### Bundle Data (from BundleData):
- Bundle information
- Token allocations
- Categories
- Descriptions

---

**Implementation Status**: ✅ Complete and Ready for Use

