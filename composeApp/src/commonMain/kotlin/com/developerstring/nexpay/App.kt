package com.developerstring.nexpay

import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.*
import androidx.navigation.compose.rememberNavController
import com.developerstring.nexpay.navigation.RootNavGraph
import com.developerstring.nexpay.viewmodel.AptosViewModel
import com.developerstring.nexpay.viewmodel.SharedViewModel
import org.jetbrains.compose.ui.tooling.preview.Preview
import org.koin.compose.viewmodel.koinViewModel

@Composable
@Preview
fun App() {
    MaterialTheme {
        val navController = rememberNavController()

        val sharedViewModel: SharedViewModel = koinViewModel()
        val aptosViewModel: AptosViewModel = koinViewModel()

        RootNavGraph(navController = navController, sharedViewModel = sharedViewModel, aptosViewModel = aptosViewModel)
    }
}