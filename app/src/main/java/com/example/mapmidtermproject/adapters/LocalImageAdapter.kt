package com.example.mapmidtermproject.adapters

import android.graphics.BitmapFactory
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.mapmidtermproject.R
import com.example.mapmidtermproject.repositories.LocalWoundImage

class LocalImageAdapter(
    private val onItemClick: (LocalWoundImage) -> Unit
) : RecyclerView.Adapter<LocalImageAdapter.ViewHolder>() {

    private var items = listOf<LocalWoundImage>()

    fun submitList(newItems: List<LocalWoundImage>) {
        items = newItems
        notifyDataSetChanged()
    }

    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val img: ImageView = view.findViewById(R.id.ivLocalImage)
        val date: TextView = view.findViewById(R.id.tvDate)

        init {
            view.setOnClickListener { onItemClick(items[adapterPosition]) }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_local_image, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = items[position]
        holder.date.text = item.dateAdded

        // Load image dari File
        val bitmap = BitmapFactory.decodeFile(item.file.absolutePath)
        holder.img.setImageBitmap(bitmap)
    }

    override fun getItemCount() = items.size
}