package com.example.pricecompute.provider

import com.example.pricecompute.gen.BasicMachine
import com.example.pricecompute.gen.MachinePrefs
import com.example.pricecompute.model.Machine
import com.example.pricecompute.model.MachineSpecs
import com.example.pricecompute.model.Plan
import java.time.LocalDate

val decode:Map<String, Int> =
    mapOf("128GB" to 128, "256GB" to 256, "512GB" to 512, "1TB" to 1024, "2TB" to 2048)

val priceMapRam:Map<String, Double> =
    mapOf("4GB" to 0.023, "8GB" to 0.046, "16GB" to 0.092, "32GB" to 0.18)

val priceMapGpu:Map<String, Double> =
    mapOf("0" to 0.0, "1" to 0.5, "2" to 1.0, "4" to 2.0, "8" to 4.0)

val priceMapSsd:Map<String, Double> =
    mapOf("128GB" to 0.015, "256GB" to 0.023, "512GB" to 0.046, "1TB" to 0.092)

val priceMapHdd:Map<String, Double> =
    mapOf("128GB" to 0.015, "256GB" to 0.023, "512GB" to 0.046, "1TB" to 0.092)


val machineList:MutableList<Machine> = mutableListOf(
    Machine(
        machineName = "EC2",
        desc = "Virtual Servers in the Cloud",
        price = 0.023
    ),
    Machine(
        machineName = "S3",
        desc = "Simple Storage Service",
        price = 0.008
        ),
    Machine(
        machineName = "RDS",
        desc = "Relational Database Service",
        price = 0.008
        ),
    Machine(
        machineName = "DynamoDB",
        desc = "NoSQL Database Service",
        price = 0.011
    ),
    Machine(
        machineName = "EBS",
        desc = "Elastic Block Storage",
        price = 0.001
    ),
    Machine(
        machineName = "EFS",
        desc = "Elastic File System",
        price = 0.023
    )
)

fun Machine.toMachinePrefs():MachinePrefs =
    MachinePrefs.newBuilder()
        .setMachineName(machineName)
        .setDesc(desc)
        .setCpuLimit(plan.cpuLimit ?: 0)
        .setGpuLimit(plan.gpuLimit ?: 0)
        .setSsd(plan.ssd ?: 0)
        .setHdd(plan.hdd ?: 0)
        .setExpiryDate(plan.expiryDate.toString())
        .build()

fun MachinePrefs.toMachine():Machine =
    Machine(machineName, desc,plan= Plan(cpuLimit, gpuLimit, ssd, hdd, expiryDate = LocalDate.parse(expiryDate)))

fun MachineSpecs.toMachine():Machine =
    Machine(
        "Custom Machine",
        "Custom Machine for user",
        price = 0.2,
        plan = Plan(
            cpuLimit = ram,
            gpuLimit = gpu,
            ssd = ssd,
            hdd = hdd,
        )
    )

fun BasicMachine.toMachine():Machine =
    Machine(
        machineName,
        desc,
        price
    )
fun Machine.toBasicMachine():BasicMachine =
    BasicMachine.newBuilder()
        .setMachineName(machineName)
        .setDesc(desc)
        .setPrice(price)
        .build()

var selectedMachine:Machine = machineList[0]