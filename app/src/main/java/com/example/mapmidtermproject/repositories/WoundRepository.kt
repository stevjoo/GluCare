package com.example.mapmidtermproject.repositories

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import java.io.File
import java.io.FileOutputStream
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

// Model data sederhana untuk file lokal
data class LocalWoundImage(
    val file: File,
    val dateAdded: String
)

class WoundRepository(private val context: Context) {

    // Folder khusus di dalam memori internal HP (User lain gak bisa lihat)
    private fun getOutputDirectory(): File {
        val mediaDir = context.filesDir.let {
            File(it, "wound_gallery").apply { mkdirs() }
        }
        return mediaDir
    }

    // FUNGSI 1: SIMPAN FOTO (Create)
    fun saveImageToInternalStorage(uri: Uri): Boolean {
        return try {
            val inputStream = context.contentResolver.openInputStream(uri)
            val bitmap = BitmapFactory.decodeStream(inputStream)

            val timestamp = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.US).format(Date())
            val file = File(getOutputDirectory(), "WOUND_$timestamp.jpg")

            val outputStream = FileOutputStream(file)
            bitmap.compress(Bitmap.CompressFormat.JPEG, 90, outputStream)
            outputStream.flush()
            outputStream.close()
            true
        } catch (e: Exception) {
            e.printStackTrace()
            false
        }
    }

    // FUNGSI 2: AMBIL SEMUA FOTO (Read)
    fun getAllImages(): List<LocalWoundImage> {
        val directory = getOutputDirectory()
        val files = directory.listFiles()

        // Urutkan dari yang terbaru
        return files?.filter { it.extension == "jpg" }
            ?.sortedByDescending { it.lastModified() }
            ?.map {
                val date = SimpleDateFormat("dd MMM yyyy HH:mm", Locale.getDefault()).format(Date(it.lastModified()))
                LocalWoundImage(it, date)
            } ?: emptyList()
    }

    // FUNGSI 3: HAPUS FOTO (Delete)
    fun deleteImage(file: File): Boolean {
        return if (file.exists()) {
            file.delete()
        } else {
            false
        }
    }
}