package com.example.pricecompute.model

import kotlinx.serialization.Contextual
import kotlinx.serialization.Serializable
import java.time.LocalDate

@Serializable
data class Plan(
    var cpuLimit: Int?= 8,
    var gpuLimit: Int? = 0,
    var ssd: Int? = 0,
    var hdd: Int? = 0,
    @Contextual
    var expiryDate: LocalDate = LocalDate.now(),
    var isExpired: Boolean = LocalDate.now() > expiryDate
)
