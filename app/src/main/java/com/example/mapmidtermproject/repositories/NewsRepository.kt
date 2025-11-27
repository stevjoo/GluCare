package com.example.mapmidtermproject.repositories

import com.example.mapmidtermproject.models.NewsArticle
import com.example.mapmidtermproject.utils.FirestoreHelper

class NewsRepository {
    fun getNews(onResult: (List<NewsArticle>) -> Unit) {
        FirestoreHelper.getNews(onResult)
    }
}