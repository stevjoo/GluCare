package com.example.mapmidtermproject.viewmodels

import android.app.Application
import android.net.Uri
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.mapmidtermproject.repositories.LocalWoundImage
import com.example.mapmidtermproject.repositories.WoundRepository
import java.io.File

// Pakai AndroidViewModel karena butuh Context/Application
class WoundViewModel(application: Application) : AndroidViewModel(application) {

    private val repository = WoundRepository(application.applicationContext)

    // LiveData: Data yang bisa dipantau oleh Activity
    private val _woundImages = MutableLiveData<List<LocalWoundImage>>()
    val woundImages: LiveData<List<LocalWoundImage>> get() = _woundImages

    // Load data awal
    fun loadImages() {
        _woundImages.value = repository.getAllImages()
    }

    // Fungsi Simpan (Dipanggil dari AnalysisActivity)
    fun saveImage(uri: Uri) {
        repository.saveImageToInternalStorage(uri)
        loadImages() // Refresh data setelah simpan
    }

    // Fungsi Hapus (Dipanggil dari GalleryActivity)
    fun deleteImage(file: File) {
        repository.deleteImage(file)
        loadImages() // Refresh data setelah hapus
    }
}