package com.example.pricecompute.model

import kotlinx.serialization.Serializable

@Serializable
data class MachineSpecs(
    val ram:Int,
    val gpu:Int,
    val ssd:Int,
    val hdd:Int
)
