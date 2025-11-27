package com.example.mapmidtermproject.activities

import android.os.Bundle
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.mapmidtermproject.R
import com.example.mapmidtermproject.adapters.LocalImageAdapter
import com.example.mapmidtermproject.viewmodels.WoundViewModel

class LocalGalleryActivity : AppCompatActivity() {

    private lateinit var viewModel: WoundViewModel
    private lateinit var adapter: LocalImageAdapter
    private lateinit var tvEmpty: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_local_gallery)

        val btnBack = findViewById<ImageView>(R.id.btnBack)
        tvEmpty = findViewById(R.id.tvEmpty)
        val rvGallery = findViewById<RecyclerView>(R.id.rvGallery)

        btnBack.setOnClickListener { finish() }

        // Setup RecyclerView
        rvGallery.layoutManager = GridLayoutManager(this, 2) // 2 Kolom
        adapter = LocalImageAdapter { localImage ->
            // Logic Hapus saat item diklik panjang atau diklik
            AlertDialog.Builder(this)
                .setTitle("Hapus Foto?")
                .setMessage("Foto ini akan dihapus dari penyimpanan internal.")
                .setPositiveButton("Hapus") { _, _ ->
                    viewModel.deleteImage(localImage.file)
                }
                .setNegativeButton("Batal", null)
                .show()
        }
        rvGallery.adapter = adapter

        // Setup ViewModel
        viewModel = ViewModelProvider(this)[WoundViewModel::class.java]

        // Observe Data (MVVM Magic)
        viewModel.woundImages.observe(this) { images ->
            if (images.isEmpty()) {
                tvEmpty.visibility = View.VISIBLE
                rvGallery.visibility = View.GONE
            } else {
                tvEmpty.visibility = View.GONE
                rvGallery.visibility = View.VISIBLE
                adapter.submitList(images)
            }
        }

        // Load data awal
        viewModel.loadImages()
    }
}