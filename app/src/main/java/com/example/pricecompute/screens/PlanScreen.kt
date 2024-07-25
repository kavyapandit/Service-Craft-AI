package com.example.pricecompute.screens

import android.icu.text.DecimalFormat
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.pricecompute.model.Machine
import com.example.pricecompute.model.Plan
import com.example.pricecompute.provider.ComputeViewModel
import com.example.pricecompute.provider.decode
import com.example.pricecompute.provider.selectedMachine
import com.example.pricecompute.ui.theme.PriceComputeTheme
import java.time.LocalDate

@Composable
fun PlanScreen(
    modifier: Modifier = Modifier,
    machine: Machine,
    onBuyClick: (machine:Machine) -> Unit = {},
    viewModel: ComputeViewModel = androidx.lifecycle.viewmodel.compose.viewModel(factory = ComputeViewModel.Factory)
    ) {

    var duration by remember {
        mutableStateOf("")
    }
    var ram by remember {
        mutableStateOf("")
    }
    var gpu by remember {
        mutableStateOf("")
    }
    var hddlt by remember {
        mutableStateOf("")
    }
    var ssdlt by remember {
        mutableStateOf("")
    }


    var buy by remember { mutableStateOf(false) }

    Column(modifier = modifier
        .fillMaxSize()
        .background(Color.Black), verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally) {

        OutlinedCard(modifier = modifier
            .fillMaxWidth()
            .padding(16.dp),
            elevation = CardDefaults.outlinedCardElevation(16.dp),
            colors = CardDefaults.outlinedCardColors(containerColor = Color(27, 41, 64), contentColor = Color.White)) {
            Column(
                 modifier = modifier
                .padding(16.dp)
            ) {
                Text(text = machine.machineName,style = MaterialTheme.typography.titleMedium)
                Text(text = machine.desc,style = MaterialTheme.typography.titleMedium)
                Text(text = "price: ${machine.price} per hour",style = MaterialTheme.typography.titleMedium)


                Spacer(modifier = modifier.height(16.dp))

                OutlinedTextField(
                    value =duration ,
                    onValueChange ={duration = it} ,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                    label = {Text(text = "Duration in days", color = Color.White)},
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedTextColor = Color.White,
                        unfocusedTextColor = Color.White
                    )

                )
                Spacer(modifier = modifier.height(16.dp))
                DynamicSelectTextField(
                    selectedValue = ram,
                    options = listOf("4GB", "8GB", "16GB", "32GB", "64GB"),
                    label = "RAM",
                    onValueChangedEvent = { ram = it }
                )
                Spacer(modifier = modifier.height(16.dp))
                DynamicSelectTextField(
                    selectedValue = gpu,
                    options = listOf("0","1", "2", "4", "8"),
                    label = "GPU",
                    onValueChangedEvent = { gpu = it }
                )
                Spacer(modifier = modifier.height(16.dp))
                DynamicSelectTextField(
                    selectedValue = ssdlt,
                    options = listOf("128GB", "256GB", "512GB", "1TB"),
                    label = "SSD",
                    onValueChangedEvent = { ssdlt = it }
                )
                Spacer(modifier = modifier.height(16.dp))
                DynamicSelectTextField(
                    selectedValue = hddlt,
                    options = listOf("128GB", "256GB", "512GB", "1TB"),
                    label = "HDD",
                    onValueChangedEvent = { hddlt = it }
                )
                Spacer(modifier = modifier.height(16.dp))

                OutlinedButton(onClick = { buy = true }) {
                    Text(text = "Compute", style = MaterialTheme.typography.titleMedium)
                }

                if (buy) {
                    val plan = Plan(
                        cpuLimit = decode[ram],
                        gpuLimit = gpu.toIntOrNull(),
                        ssd = decode[ssdlt],
                        hdd = decode[hddlt],
                        expiryDate = LocalDate.now().plusDays(duration.toLong())
                    )
                    val price = viewModel.computePrice(ram,gpu,ssdlt,hddlt,duration.toLong())
                    Text(text = "Estimated price: ${DecimalFormat("#.##").format(price)}")
                    Spacer(modifier = modifier.height(16.dp))
                    OutlinedButton(onClick = {
                        selectedMachine = selectedMachine.copy(plan = plan)
                        onBuyClick(selectedMachine)
                    }) {
                        Text(text = "Buy", style = MaterialTheme.typography.titleMedium)
                    }
                }
            }
        }
    }
}
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DynamicSelectTextField(
    selectedValue: String,
    options: List<String>,
    label: String,
    onValueChangedEvent: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    var expanded by remember { mutableStateOf(false) }

    ExposedDropdownMenuBox(
        expanded = expanded,
        onExpandedChange = { expanded = !expanded },
        modifier = modifier
    ) {
        OutlinedTextField(
            readOnly = true,
            value = selectedValue,
            onValueChange = {},
            label = { Text(text = label, color = Color.White) },
            trailingIcon = {
                ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded)
            },
            colors = OutlinedTextFieldDefaults.colors(
                focusedTextColor = Color.White,
                unfocusedTextColor = Color.White,
                focusedBorderColor = Color.White,
                unfocusedBorderColor = Color.White
            ),
            modifier = Modifier
                .menuAnchor()
                .fillMaxWidth()
        )

        ExposedDropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
            options.forEach { option: String ->
                DropdownMenuItem(
                    text = { Text(text = option) },
                    onClick = {
                        expanded = false
                        onValueChangedEvent(option)
                    }
                )
            }
        }
    }
}

@Preview(showSystemUi = true, showBackground = true)
@Composable
private fun PlanScreenPrev() {
    PriceComputeTheme {
        PlanScreen(
            machine = Machine().apply {
                machineName = "EC2"
                desc = "Virtual Servers in the Cloud"
                price = 0.023
            }
        )
    }
}