package com.developerstring.nexpay.navigation

import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.composable
import androidx.navigation.navigation
import com.developerstring.nexpay.SplashScreen
import com.developerstring.nexpay.SplashScreenRoute
import com.developerstring.nexpay.ui.MainScreen
import com.developerstring.nexpay.ui.MainScreenRoute
import com.developerstring.nexpay.ui.bottom_nav.ExploreScreen
import com.developerstring.nexpay.ui.bottom_nav.ExploreScreenRoute
import com.developerstring.nexpay.ui.onboarding.create_profile.CreateProfileScreen
import com.developerstring.nexpay.ui.onboarding.create_profile.CreateProfileScreenRoute
import com.developerstring.nexpay.ui.onboarding.slider.OnBoardingScreen
import com.developerstring.nexpay.ui.onboarding.slider.OnBoardingScreenRoute
import com.developerstring.nexpay.ui.bottom_nav.WalletScreen
import com.developerstring.nexpay.ui.bottom_nav.WalletScreenRoute
import com.developerstring.nexpay.ui.screens.applock.AppLockScreen
import com.developerstring.nexpay.ui.screens.applock.AppLockScreenRoute
import com.developerstring.nexpay.ui.screens.applock.AppLockSettingsScreen
import com.developerstring.nexpay.ui.screens.applock.AppLockSettingsScreenRoute
import com.developerstring.nexpay.ui.screens.applock.PinSetupScreen
import com.developerstring.nexpay.ui.screens.applock.PinSetupScreenRoute
import com.developerstring.nexpay.ui.transaction.AddTransactionScreen
import com.developerstring.nexpay.ui.transaction.AddTransactionScreenRoute
import com.developerstring.nexpay.ui.screens.BundleDetailScreen
import com.developerstring.nexpay.ui.screens.BundleDetailScreenRoute
import com.developerstring.nexpay.viewmodel.AptosViewModel
import com.developerstring.nexpay.viewmodel.SharedViewModel
import androidx.navigation.toRoute

fun NavGraphBuilder.navGraph(navController: NavHostController, sharedViewModel: SharedViewModel, aptosViewModel: AptosViewModel) {

    navigation(route = Graph.NAV_GRAPH, startDestination = SplashScreenRoute.toString()) {

        composable<MainScreenRoute> { MainScreen(sharedViewModel = sharedViewModel, aptosViewModel = aptosViewModel) }

        composable(SplashScreenRoute.toString()) { SplashScreen(sharedViewModel = sharedViewModel,navController = navController) }
        composable<SplashScreenRoute> { SplashScreen(sharedViewModel = sharedViewModel,navController = navController) }

        composable<OnBoardingScreenRoute> { OnBoardingScreen(sharedViewModel = sharedViewModel, navController = navController) }

        composable<CreateProfileScreenRoute> { CreateProfileScreen(sharedViewModel = sharedViewModel, navController = navController) }

        composable<WalletScreenRoute> { WalletScreen(sharedViewModel = sharedViewModel, navController = navController, aptosViewModel = aptosViewModel) }

        composable<AddTransactionScreenRoute> { AddTransactionScreen(sharedViewModel = sharedViewModel, navController = navController, aptosViewModel = aptosViewModel) }

        composable<ExploreScreenRoute> { ExploreScreen(sharedViewModel = sharedViewModel, navController = navController) }

        composable<BundleDetailScreenRoute> { backStackEntry ->
            val route = backStackEntry.toRoute<BundleDetailScreenRoute>()
            BundleDetailScreen(
                bundleId = route.bundleId,
                sharedViewModel = sharedViewModel,
                navController = navController
            )
        }

        // app lock
        composable<AppLockScreenRoute> { AppLockScreen(sharedViewModel = sharedViewModel, navController = navController) }
        composable<AppLockSettingsScreenRoute> { AppLockSettingsScreen(sharedViewModel = sharedViewModel, navController = navController) }
        composable<PinSetupScreenRoute> { PinSetupScreen(sharedViewModel = sharedViewModel, navController = navController) }

    }

}

