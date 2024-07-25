package com.example.pricecompute.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.pricecompute.model.Machine
import com.example.pricecompute.provider.machineList
import com.example.pricecompute.ui.theme.PriceComputeTheme

@Composable
fun UpgradeScreen(
    modifier: Modifier = Modifier,
    onMachineClick: (Machine) -> Unit = {},
    machineList: List<Machine>
) {

    LazyColumn(modifier=modifier.fillMaxSize().background(Color.Black)) {
        items(machineList){
           MachineCard(machine = it) { conf->
               onMachineClick(conf)
           }
        }
    }
}

@Composable
fun MachineCard(
    modifier: Modifier = Modifier,
    machine: Machine,
    onMachineClick: (Machine) -> Unit
) {
    Column(modifier = modifier
        .padding(16.dp).background(Color.Black)
        .clickable { onMachineClick(machine) }) {
        Text(text = machine.machineName,style = MaterialTheme.typography.titleMedium, color = Color.White)
        Text(text = machine.desc,style = MaterialTheme.typography.titleMedium, color = Color.White)
        Text(text = "price: ${machine.price} per hour",style = MaterialTheme.typography.titleMedium, color = Color.White)
        Text(text = "CPU:${machine.plan.cpuLimit}  GPU:${machine.plan.gpuLimit}  SSD:${machine.plan!!.ssd}  HDD:${machine.plan!!.hdd}",
            style = MaterialTheme.typography.titleMedium, color = Color.White
        )
        HorizontalDivider()
    }
}

@Preview(showSystemUi = true, showBackground = true)
@Composable
private fun UpgradeScreenPrev() {
    PriceComputeTheme {
//        UpgradeScreen()
    }
}