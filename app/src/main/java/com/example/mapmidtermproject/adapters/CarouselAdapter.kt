package com.example.mapmidtermproject.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.mapmidtermproject.R
import com.example.mapmidtermproject.models.NewsArticle

class CarouselAdapter(
    private var items: List<NewsArticle>,
    private val onItemClick: (NewsArticle) -> Unit
) : RecyclerView.Adapter<CarouselAdapter.CarouselViewHolder>() {

    fun updateData(newItems: List<NewsArticle>) {
        items = newItems
        notifyDataSetChanged()
    }

    inner class CarouselViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val img: ImageView = itemView.findViewById(R.id.imageCarousel)
        val title: TextView = itemView.findViewById(R.id.tvCarouselTitle)
        val date: TextView = itemView.findViewById(R.id.tvCarouselDate) // Tambahan

        init {
            itemView.setOnClickListener {
                onItemClick(items[adapterPosition])
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CarouselViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_carousel, parent, false)
        return CarouselViewHolder(view)
    }

    override fun onBindViewHolder(holder: CarouselViewHolder, position: Int) {
        val article = items[position]

        holder.title.text = article.title
        holder.date.text = article.date // Set tanggal

        // Load Image
        val context = holder.itemView.context
        val imageResId = context.resources.getIdentifier(article.imageCode, "drawable", context.packageName)

        if (imageResId != 0) {
            holder.img.setImageResource(imageResId)
        } else {
            holder.img.setImageResource(R.drawable.ic_image_placeholder)
        }
    }

    override fun getItemCount(): Int = items.size
}