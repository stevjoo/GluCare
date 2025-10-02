package com.example.mapmidtermproject.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.mapmidtermproject.models.NewsArticle
import com.example.mapmidtermproject.R

class NewsAdapter(private val newsList: List<NewsArticle>) :
    RecyclerView.Adapter<NewsAdapter.NewsViewHolder>() {

    class NewsViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val titleTextView: TextView = itemView.findViewById(R.id.tvNewsTitle)
        val dateTextView: TextView = itemView.findViewById(R.id.tvNewsDate)
        val summaryTextView: TextView = itemView.findViewById(R.id.tvNewsSummary)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NewsViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.list_item_news, parent, false)
        return NewsViewHolder(view)
    }

    override fun onBindViewHolder(holder: NewsViewHolder, position: Int) {
        val currentArticle = newsList[position]
        holder.titleTextView.text = currentArticle.title
        holder.dateTextView.text = currentArticle.date
        holder.summaryTextView.text = currentArticle.summary
    }

    override fun getItemCount(): Int {
        return newsList.size
    }
}