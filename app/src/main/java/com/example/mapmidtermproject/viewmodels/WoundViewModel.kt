package com.example.mapmidtermproject.viewmodels

import android.app.Application
import android.net.Uri
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.mapmidtermproject.repositories.LocalWoundImage
import com.example.mapmidtermproject.repositories.WoundRepository
import com.example.mapmidtermproject.utils.WoundClassifierHelper
import java.io.File

// Hapus import Classifications dari Task Library karena kita akan pakai cara manual (Interpreter)
// import org.tensorflow.lite.task.vision.classifier.Classifications

class WoundViewModel(application: Application) : AndroidViewModel(application), WoundClassifierHelper.ClassifierListener {

    private val repository = WoundRepository(application.applicationContext)

    private val _woundImages = MutableLiveData<List<LocalWoundImage>>()
    val woundImages: LiveData<List<LocalWoundImage>> get() = _woundImages

    // --- PENTING: INI HARUS DIDEKLARASIKAN DULUAN ---
    private val _analysisResult = MutableLiveData<String>()
    val analysisResult: LiveData<String> get() = _analysisResult

    private val _isDiabeticDetected = MutableLiveData<Boolean>()
    val isDiabeticDetected: LiveData<Boolean> get() = _isDiabeticDetected

    // --- BARU DEKLARASI HELPER (Biar variable di atas udah siap pas dipanggil) ---
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
        _analysisResult.value = "Menganalisis..."
        woundClassifier.classify(uri)
    }

    // --- CALLBACK DARI HELPER (Sekarang return Pair<String, Float>) ---
    override fun onResults(label: String, score: Float) {
        // Logika Threshold
        // Kalau model lu sudah pakai Softmax (output 0-1), pakai threshold 0.6f aman.
        // Tapi kalau outputnya angka besar (misal 10.0), threshold ini harus disesuaikan.
        // Cek Logcat "WoundDebug" nanti buat liat range angkanya berapa.

        // Asumsi sementara: Model sudah softmax (0.0 - 1.0)
        val scorePercent = score * 100

        // Ambang batas keyakinan (misal 50% atau 60%)
        if (score < 0.50f) {
            _analysisResult.postValue("❓ Hasil meragukan (${String.format("%.1f", scorePercent)}%)\nCoba foto ulang lebih jelas.")
            _isDiabeticDetected.postValue(false)
            return
        }

        val isDiabetic = label.contains("Diabetic", ignoreCase = true)
        _isDiabeticDetected.postValue(isDiabetic)

        // ... kode menampilkan hasil sama seperti sebelumnya ...
        val textResult = if (isDiabetic) {
            "⚠️ Terdeteksi: $label\n(Confidence: ${String.format("%.1f", scorePercent)}%)"
        } else {
            "✅ Terdeteksi: Luka Non-Diabetes ($label)\n(Confidence: ${String.format("%.1f", scorePercent)}%)"
        }
        _analysisResult.postValue(textResult)
    }

    override fun onError(error: String) {
        // Logika aman: Cek null check (walaupun sudah dihandle urutan deklarasi)
        val currentText = _analysisResult.value ?: ""
        _analysisResult.postValue("Gagal: $error")
        _isDiabeticDetected.postValue(false)
    }
}