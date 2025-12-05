package com.example.mapmidtermproject.models

import java.util.Date

data class WoundAnalysis(
    var id: String = "",
    val label: String = "",         // Contoh: "Diabetic Wound"
    val confidence: Float = 0f,     // Contoh: 0.95
    val localImagePath: String = "", // Path file di HP: "/data/user/0/.../foto.jpg"
    val timestamp: Date = Date()
)