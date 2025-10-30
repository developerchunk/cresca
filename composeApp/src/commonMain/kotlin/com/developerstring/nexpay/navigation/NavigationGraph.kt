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
import com.developerstring.nexpay.ui.transaction.ReceivePaymentScreen
import com.developerstring.nexpay.ui.transaction.ReceivePaymentScreenRoute
import com.developerstring.nexpay.ui.nfc.NFCScreen
import com.developerstring.nexpay.ui.nfc.NFCScreenRoute
import com.developerstring.nexpay.ui.screens.BundleDetailScreen
import com.developerstring.nexpay.ui.screens.BundleDetailScreenRoute
import com.developerstring.nexpay.ui.screens.ExecuteBundleScreen
import com.developerstring.nexpay.ui.screens.ExecuteBundleScreenRoute
import com.developerstring.nexpay.viewmodel.AptosViewModel
import com.developerstring.nexpay.viewmodel.SharedViewModel
import androidx.navigation.toRoute
import com.developerstring.nexpay.ui.crypto.CurrencyScreen
import com.developerstring.nexpay.ui.crypto.CurrencyScreenRoute
import com.developerstring.nexpay.ui.transaction.ConfirmationScreen
import com.developerstring.nexpay.ui.transaction.ConfirmationScreenRoute
import com.developerstring.nexpay.ui.transaction.QRScannerScreen
import com.developerstring.nexpay.ui.transaction.QRScannerScreenRoute
import com.developerstring.nexpay.ui.transaction.SwapScreen
import com.developerstring.nexpay.ui.transaction.SwapScreenRoute
import com.developerstring.nexpay.ui.transaction.ViewAllTransactionRoute
import com.developerstring.nexpay.ui.transaction.ViewAllTransactionScreen

fun NavGraphBuilder.navGraph(navController: NavHostController, sharedViewModel: SharedViewModel, aptosViewModel: AptosViewModel) {

    navigation(route = Graph.NAV_GRAPH, startDestination = SplashScreenRoute.toString()) {

        composable<MainScreenRoute> { MainScreen(sharedViewModel = sharedViewModel, aptosViewModel = aptosViewModel) }

        composable(SplashScreenRoute.toString()) { SplashScreen(sharedViewModel = sharedViewModel,navController = navController) }

        composable<SplashScreenRoute> { SplashScreen(sharedViewModel = sharedViewModel,navController = navController) }

        composable<CreateProfileScreenRoute> { CreateProfileScreen(sharedViewModel = sharedViewModel, navController = navController) }

        composable<WalletScreenRoute> { WalletScreen(sharedViewModel = sharedViewModel, navController = navController, aptosViewModel = aptosViewModel) }

        composable<QRScannerScreenRoute> { QRScannerScreen(navController = navController, sharedViewModel = sharedViewModel) }

        composable<ViewAllTransactionRoute> { ViewAllTransactionScreen(sharedViewModel = sharedViewModel, navController = navController) }

        composable<CurrencyScreenRoute> { CurrencyScreen(sharedViewModel = sharedViewModel, navController = navController) }

        composable<SwapScreenRoute> { SwapScreen(sharedViewModel = sharedViewModel, navController = navController, aptosViewModel = aptosViewModel) }

        composable<ConfirmationScreenRoute> { backStackEntry ->
            val route = backStackEntry.toRoute<ConfirmationScreenRoute>()
            ConfirmationScreen(
                navController = navController,
                senderAddress = route.senderAddress,
                recipientAddress = route.recipientAddress,
                amount = route.amount,
                isSuccess = route.isSuccess,
                scheduledAt = route.scheduledAt,
                executedAt = route.executedAt,
                note = route.note,
                aptosViewModel = aptosViewModel
            )
        }

        composable<AddTransactionScreenRoute> { backStackEntry ->
            val route = backStackEntry.toRoute<AddTransactionScreenRoute>()
            AddTransactionScreen(
                sharedViewModel = sharedViewModel,
                navController = navController,
                aptosViewModel = aptosViewModel,
                initialReceiverAddress = route.receiverAddress
            )
        }

        composable<NFCScreenRoute> {
            NFCScreen(
                onNavigateToSend = { address ->
                    // Navigate to send screen with address
                    navController.navigate(AddTransactionScreenRoute(receiverAddress = address))
                },
                onNavigateToAddTransaction = { receiverAddress ->
                    // Navigate to AddTransactionScreen with receiver address
                    navController.navigate(AddTransactionScreenRoute(receiverAddress = receiverAddress))
                },
                aptosViewModel = aptosViewModel,
                sharedViewModel = sharedViewModel, navController = navController
            )
        }

        composable<ReceivePaymentScreenRoute> {
            ReceivePaymentScreen(
                sharedViewModel = sharedViewModel,
                aptosViewModel = aptosViewModel,
                navController = navController
            )
        }

        composable<ExploreScreenRoute> { ExploreScreen(sharedViewModel = sharedViewModel, navController = navController) }

        composable<BundleDetailScreenRoute> { backStackEntry ->
            val route = backStackEntry.toRoute<BundleDetailScreenRoute>()
            BundleDetailScreen(
                bundleId = route.bundleId,
                sharedViewModel = sharedViewModel,
                navController = navController
            )
        }

        composable<ExecuteBundleScreenRoute> { backStackEntry ->
            val route = backStackEntry.toRoute<ExecuteBundleScreenRoute>()
            ExecuteBundleScreen(
                bundleId = route.bundleId,
                tradeType = route.tradeType,
                sharedViewModel = sharedViewModel,
                navController = navController,
                aptosViewModel = aptosViewModel
            )
        }

        // app lock
        composable<AppLockScreenRoute> { AppLockScreen(sharedViewModel = sharedViewModel, navController = navController) }
        composable<AppLockSettingsScreenRoute> { AppLockSettingsScreen(sharedViewModel = sharedViewModel, navController = navController) }
        composable<PinSetupScreenRoute> { PinSetupScreen(sharedViewModel = sharedViewModel, navController = navController) }

    }

}

