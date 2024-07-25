package com.example.pricecompute.model

import kotlinx.serialization.Serializable

@Serializable
data class Machine (
    var machineName: String = "",
    var desc: String = "",
    var price: Double = 0.0,
    var duration: Long = 30,
    var plan: Plan =
        Plan(
            cpuLimit = 4,
            gpuLimit = 0,
            ssd = 128,
            hdd = 128,
        )
)

