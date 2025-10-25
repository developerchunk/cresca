package com.developerstring.nexpay.ui.bottom_nav

import androidx.compose.runtime.Composable
import androidx.navigation.NavController
import com.developerstring.nexpay.viewmodel.SharedViewModel
import kotlinx.serialization.Serializable

@Serializable
data object ProfileScreenRoute

@Composable
fun ProfileScreen(
    sharedViewModel: SharedViewModel,
    navController: NavController
) {

}