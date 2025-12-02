package com.example.mapmidtermproject.viewmodels

import android.app.Application
import android.net.Uri
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.mapmidtermproject.repositories.LocalWoundImage
import com.example.mapmidtermproject.repositories.WoundRepository
import com.example.mapmidtermproject.utils.Event
import com.example.mapmidtermproject.utils.WoundClassifierHelper
import java.io.File

class WoundViewModel(application: Application) : AndroidViewModel(application), WoundClassifierHelper.ClassifierListener {

    private val repository = WoundRepository(application.applicationContext)

    private val _woundImages = MutableLiveData<List<LocalWoundImage>>()
    val woundImages: LiveData<List<LocalWoundImage>> get() = _woundImages

    // MENGGUNAKAN EVENT WRAPPER UNTUK HASIL ANALISIS
    private val _analysisResult = MutableLiveData<Event<String>>()
    val analysisResult: LiveData<Event<String>> get() = _analysisResult

    private val _isDiabeticDetected = MutableLiveData<Boolean>()
    val isDiabeticDetected: LiveData<Boolean> get() = _isDiabeticDetected

    private val woundClassifier = WoundClassifierHelper(application.applicationContext, this)

    fun loadImages() {
        _woundImages.postValue(repository.getAllImages())
    }

    fun saveImage(uri: Uri) {
        repository.saveImageToInternalStorage(uri)
        loadImages()
    }

    fun deleteImage(file: File) {
        repository.deleteImage(file)
        loadImages()
    }

    fun analyzeImage(uri: Uri) {
        // Kita tidak mengirim status loading ke LiveData result agar tidak membingungkan Event
        // Loading ditangani oleh Activity
        woundClassifier.classify(uri)
    }

    // --- CALLBACK DARI HELPER ---
    override fun onResults(label: String, score: Float) {
        val scorePercent = score * 100

        // Ambang batas keyakinan (Threshold)
        if (score < 0.50f) {
            val message = "❓ Hasil meragukan (${String.format("%.1f", scorePercent)}%)\nCoba foto ulang lebih jelas."
            _isDiabeticDetected.postValue(false)
            _analysisResult.postValue(Event(message)) // Bungkus dengan Event
            return
        }

        val isDiabetic = label.contains("Diabetic", ignoreCase = true)
        _isDiabeticDetected.postValue(isDiabetic)

        val textResult = if (isDiabetic) {
            "⚠️ Terdeteksi: $label\n(Confidence: ${String.format("%.1f", scorePercent)}%)"
        } else {
            "✅ Terdeteksi: Luka Non-Diabetes ($label)\n(Confidence: ${String.format("%.1f", scorePercent)}%)"
        }

        _analysisResult.postValue(Event(textResult)) // Bungkus dengan Event
    }

    override fun onError(error: String) {
        _isDiabeticDetected.postValue(false)
        _analysisResult.postValue(Event("Gagal: $error")) // Bungkus dengan Event
    }
}