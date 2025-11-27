package com.example.mapmidtermproject.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.mapmidtermproject.models.NewsArticle
import com.example.mapmidtermproject.repositories.NewsRepository

class NewsViewModel : ViewModel() {
    private val repository = NewsRepository()

    private val _newsList = MutableLiveData<List<NewsArticle>>()
    val newsList: LiveData<List<NewsArticle>> = _newsList

    fun loadNews() {
        repository.getNews { list ->
            _newsList.value = list
        }
    }
}