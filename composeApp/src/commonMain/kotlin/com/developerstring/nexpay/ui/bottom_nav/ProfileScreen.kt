package com.developerstring.nexpay.ui.bottom_nav

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.navigation.NavController
import com.developerstring.nexpay.ui.transaction.ViewAllTransactionScreen
import com.developerstring.nexpay.viewmodel.AptosViewModel
import com.developerstring.nexpay.viewmodel.SharedViewModel
import kotlinx.serialization.Serializable

@Serializable
data object ProfileScreenRoute

@Composable
fun ProfileScreen(
    sharedViewModel: SharedViewModel,
    navController: NavController,
    aptosViewModel: AptosViewModel
) {

//    val t by remember { mutableStateOf(true) }
//
//    Column(modifier = Modifier.fillMaxSize(),verticalArrangement = Arrangement.Center,horizontalAlignment = Alignment.CenterHorizontally) {
//        Button(
//            onClick = {
//                aptosViewModel.initWallet {
//                    aptosViewModel.sendCoins(recipientAddress = "0x25714b39ba0b43b9bde30f4500c24fdc746faaf371969fa28011fb508791eda4",amount = 1000L.toULong()) {
//                        aptosViewModel.refreshBalance()
//                    }
//                }
//            }
//        ) {
//            Text("ggg")
//        }
//    }

    ViewAllTransactionScreen(
        sharedViewModel = sharedViewModel,
        navController = navController,
        aptosViewModel = aptosViewModel
    )

}