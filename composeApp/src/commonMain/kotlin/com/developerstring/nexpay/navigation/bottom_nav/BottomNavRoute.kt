package com.developerstring.nexpay.navigation.bottom_nav

import com.developerstring.nexpay.ui.SendReceiveScreenRoute
import com.developerstring.nexpay.ui.bottom_nav.CalenderScreenRoute
import com.developerstring.nexpay.ui.bottom_nav.ExploreScreenRoute
import com.developerstring.nexpay.ui.bottom_nav.HomeScreenRoute
import com.developerstring.nexpay.ui.bottom_nav.ProfileScreenRoute
import com.developerstring.nexpay.ui.bottom_nav.WalletScreenRoute
import com.developerstring.nexpay.ui.transaction.AddTransactionScreenRoute
import com.developerstring.nexpay.ui.transaction.SwapScreenRoute
import nexpay.composeapp.generated.resources.Res
import nexpay.composeapp.generated.resources.rounded_account
import nexpay.composeapp.generated.resources.rounded_explore_toggle
import nexpay.composeapp.generated.resources.rounded_globe_book
import nexpay.composeapp.generated.resources.rounded_history
import nexpay.composeapp.generated.resources.rounded_send_money
import nexpay.composeapp.generated.resources.rounded_wallet
import org.jetbrains.compose.resources.DrawableResource

sealed class BottomNavRoute(
    val route: String,
    val title: String,
    val icon: DrawableResource,
) {

    object Wallet : BottomNavRoute(
        route = WalletScreenRoute.toString(),
        title = "Wallet",
        icon = Res.drawable.rounded_wallet,
    )

    // for home
    object History : BottomNavRoute(
        route = ExploreScreenRoute.toString(),
        title = "Explore",
        icon = Res.drawable.rounded_explore_toggle,
    )

    // for report
    object Calender : BottomNavRoute(
        route = CalenderScreenRoute.toString(),
        title = "Calender",
        icon = Res.drawable.rounded_globe_book,
    )

    object Profile : BottomNavRoute(
        route = ProfileScreenRoute.toString(),
        title = "Profile",
        icon = Res.drawable.rounded_account,
    )

    object Send : BottomNavRoute(
        route = SwapScreenRoute.toString(),
        title = "Swap",
        icon = Res.drawable.rounded_send_money,
    )
}