package com.example.mapmidtermproject.models

import java.io.Serializable

data class NewsArticle(
    val id: Int = 0,
    val title: String = "",
    val summary: String = "",
    val date: String = "",
    // GANTI: imageResId (Int) -> imageCode (String)
    // Contoh isi: "diabetes_symptoms"
    val imageCode: String = "",
    val content: String = ""
) : Serializable