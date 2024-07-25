package com.example.pricecompute.screens

import android.widget.Toast
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.pricecompute.provider.ComputeViewModel
import com.example.pricecompute.provider.selectedMachine
import com.example.pricecompute.screens.ai.ChatScreen


@Composable
fun ScreenFlow(
    viewModel: ComputeViewModel = androidx.lifecycle.viewmodel.compose.viewModel(factory = ComputeViewModel.Factory)
){
    val navController = rememberNavController()
    val context = LocalContext.current
    val currentMachine by viewModel.currentMachine.collectAsState()
    NavHost(navController = navController, startDestination = "home") {
        composable("home"){
                HomeScreen(onUpgradeClick = {
                    navController.navigate("upg")
                },
                    onFabClick = {
                        navController.navigate("ai")
                    },
                    currentMachine = currentMachine
                )
            }

        composable("upg"){
            UpgradeScreen(onMachineClick = {
                selectedMachine = it
                navController.navigate("plan")
            },
                machineList = viewModel.machines
            )
        }
        composable("plan"){
            PlanScreen(
                machine = selectedMachine,
                onBuyClick = {
                    viewModel.changeMachine(it)
                    Toast.makeText(
                        context,
                        "Purchased",
                        Toast.LENGTH_SHORT
                    ).show()
                    navController.navigate("home")
                }
            )
        }
        composable("ai"){
            ChatScreen()
        }

    }
}


