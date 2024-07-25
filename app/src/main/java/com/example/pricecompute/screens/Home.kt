package com.example.pricecompute.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.pricecompute.model.Machine
import com.example.pricecompute.provider.ComputeViewModel
import com.example.pricecompute.provider.machineList
import com.example.pricecompute.ui.theme.PriceComputeTheme

@Composable
fun HomeScreen(
    modifier: Modifier = Modifier,
    onUpgradeClick: () -> Unit = {},
    onFabClick: () -> Unit = {},
    currentMachine:Machine
) {
    Column(modifier= modifier
        .fillMaxSize()
        .background(Color.Black),verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally){
        Card(
            modifier = modifier
                .fillMaxWidth()
                .padding(16.dp),
            elevation = CardDefaults.cardElevation(8.dp),
            colors = CardDefaults.cardColors(
                containerColor = Color(27, 41, 64),
                contentColor = Color.White
            )
        ) {
            Column(
                modifier = modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(text = "Current Plan", style = MaterialTheme.typography.headlineSmall)
                Spacer(modifier = modifier.height(16.dp))

                Column(
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = currentMachine.machineName,
                        style = MaterialTheme.typography.headlineMedium
                    )
                    Text(
                        text = currentMachine.desc,
                        style = MaterialTheme.typography.headlineMedium
                    )
                }

                Spacer(modifier = modifier.height(16.dp))

                Row {
                    Text(
                        text = "RAM: ${currentMachine.plan.cpuLimit}GB",
                        style = MaterialTheme.typography.headlineMedium,
                        modifier = modifier.padding(start = 16.dp)
                    )
                    Spacer(modifier = Modifier.width(80.dp))
                    Text(
                        text = "GPU: ${currentMachine.plan.gpuLimit}",
                        style = MaterialTheme.typography.headlineMedium
                    )
                }
                Spacer(modifier = Modifier.height(16.dp))
                Row(modifier = modifier.padding(bottom = 16.dp)) {
                    Text(
                        text = "SSD: ${currentMachine.plan.ssd}GB",
                        style = MaterialTheme.typography.headlineMedium,
                        modifier = modifier.padding(start = 16.dp)
                    )
                    Spacer(modifier = Modifier.width(70.dp))
                    Text(
                        text = "HDD: ${currentMachine.plan.hdd}",
                        style = MaterialTheme.typography.headlineMedium
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                }

                Text(
                    text = "expiry: ${currentMachine.plan.expiryDate}",
                    style = MaterialTheme.typography.headlineSmall
                )
            }
            Spacer(modifier = Modifier.height(16.dp))
        }

        Column(
            modifier = Modifier.fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            OutlinedButton(
                onClick = { onUpgradeClick() }, colors = ButtonDefaults.outlinedButtonColors(
                    containerColor = Color(27, 41, 64),
                    contentColor = Color.White
                )
            ) {
                Text(text = "Upgrade plan", style = MaterialTheme.typography.headlineMedium)
            }
            Spacer(modifier = modifier.height(16.dp))
            OutlinedButton(
                onClick = { onFabClick() }, colors = ButtonDefaults.outlinedButtonColors(
                    containerColor = Color(27, 41, 64),
                    contentColor = Color.White
                )
            ) {
                Text(text = "Ask AI assistant", style = MaterialTheme.typography.headlineMedium)
            }
        }
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
private fun Home() {
    PriceComputeTheme{
        HomeScreen(currentMachine = machineList[0])
    }
}