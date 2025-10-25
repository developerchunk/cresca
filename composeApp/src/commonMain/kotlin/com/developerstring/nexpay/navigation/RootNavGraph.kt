package com.developerstring.nexpay.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.developerstring.nexpay.ui.MainScreen
import com.developerstring.nexpay.ui.MainScreenRoute
import com.developerstring.nexpay.viewmodel.AptosViewModel
import com.developerstring.nexpay.viewmodel.SharedViewModel

@Composable
fun RootNavGraph(
    navController: NavHostController,
    sharedViewModel: SharedViewModel,
    aptosViewModel: AptosViewModel,
) {

    NavHost(
        route = Graph.ROOT_NAV_GRAPH,
        startDestination = Graph.NAV_GRAPH,
        navController = navController
    ) {

        navGraph(
            navController = navController,
            sharedViewModel = sharedViewModel,
            aptosViewModel = aptosViewModel,
        )

        composable<MainScreenRoute> {
            MainScreen(sharedViewModel = sharedViewModel, aptosViewModel = aptosViewModel)
        }

    }

}

object Graph {
    const val ROOT_NAV_GRAPH = "set_up_nav_graph"
    const val BOTTOM_NAV_GRAPH = "bottom_nav_graph"
    const val NAV_GRAPH = "nav_graph"
}