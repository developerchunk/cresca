package com.developerstring.nexpay.navigation.bottom_nav

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.developerstring.nexpay.navigation.Graph
import com.developerstring.nexpay.navigation.navGraph
import com.developerstring.nexpay.ui.bottom_nav.CalenderScreen
import com.developerstring.nexpay.ui.bottom_nav.CalenderScreenRoute
import com.developerstring.nexpay.ui.bottom_nav.ExploreScreen
import com.developerstring.nexpay.ui.bottom_nav.ExploreScreenRoute
import com.developerstring.nexpay.ui.bottom_nav.HomeScreen
import com.developerstring.nexpay.ui.bottom_nav.HomeScreenRoute
import com.developerstring.nexpay.ui.bottom_nav.ProfileScreen
import com.developerstring.nexpay.ui.bottom_nav.ProfileScreenRoute
import com.developerstring.nexpay.ui.bottom_nav.WalletScreen
import com.developerstring.nexpay.ui.bottom_nav.WalletScreenRoute
import com.developerstring.nexpay.viewmodel.AptosViewModel
import com.developerstring.nexpay.viewmodel.SharedViewModel

@Composable
fun BottomNavGraph(navController: NavHostController, sharedViewModel: SharedViewModel, aptosViewModel: AptosViewModel) {

    NavHost(route = Graph.BOTTOM_NAV_GRAPH,navController = navController,startDestination = WalletScreenRoute.toString()) {

        navGraph(
            navController = navController,
            sharedViewModel = sharedViewModel,
            aptosViewModel = aptosViewModel
        )

        composable(HomeScreenRoute.toString()) { HomeScreen(sharedViewModel = sharedViewModel, navController = navController) }
        composable(CalenderScreenRoute.toString()) { CalenderScreen(sharedViewModel = sharedViewModel, navController = navController) }
        composable(ProfileScreenRoute.toString()) { ProfileScreen(sharedViewModel = sharedViewModel, navController = navController) }
        composable(WalletScreenRoute.toString()) { WalletScreen(sharedViewModel = sharedViewModel, navController = navController, aptosViewModel = aptosViewModel) }
        composable(ExploreScreenRoute.toString()) { ExploreScreen(sharedViewModel = sharedViewModel, navController = navController) }

    }

}