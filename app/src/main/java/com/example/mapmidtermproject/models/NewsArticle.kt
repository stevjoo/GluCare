package com.example.mapmidtermproject.models

import java.io.Serializable // <-- 1. Tambahkan import ini

data class NewsArticle(
    val id: Int,
    val title: String,
    val summary: String,
    val date: String,
    // 2. Tambahkan dua properti baru untuk gambar dan konten lengkap
    val imageResId: Int,
    val content: String
) : Serializable // <-- 3. Tambahkan : Serializable