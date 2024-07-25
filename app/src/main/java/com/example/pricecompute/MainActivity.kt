package com.example.pricecompute

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import com.example.pricecompute.provider.ComputeViewModel
import com.example.pricecompute.screens.ScreenFlow
import com.example.pricecompute.ui.theme.PriceComputeTheme

class MainActivity : ComponentActivity() {
    private val viewModel:ComputeViewModel by viewModels { ComputeViewModel.Factory  }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            PriceComputeTheme {
                ScreenFlow()
            }
        }
    }
    override fun onStop() {
        super.onStop()
        viewModel.saveToDB()
    }

    override fun onStart() {
        super.onStart()
        viewModel.getFromDB()
    }

}


