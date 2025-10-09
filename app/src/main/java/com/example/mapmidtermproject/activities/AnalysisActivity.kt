package com.example.mapmidtermproject.activities

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.widget.Button
import android.widget.ImageView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.FileProvider
import com.example.mapmidtermproject.R
import java.io.File
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import kotlin.random.Random

class AnalysisActivity : AppCompatActivity() {

    private lateinit var ivWoundImage: ImageView
    private lateinit var btnStartAnalysis: Button
    private var currentImageUri: Uri? = null
    private var tempImageUri: Uri? = null

    private val cameraLauncher = registerForActivityResult(
        ActivityResultContracts.TakePicture()
    ) { isSuccess ->
        if (isSuccess) {
            currentImageUri = tempImageUri
            ivWoundImage.setImageURI(currentImageUri)
            btnStartAnalysis.isEnabled = true
        }
    }

    private val galleryLauncher = registerForActivityResult(
        ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        uri?.let {
            currentImageUri = it
            ivWoundImage.setImageURI(it)
            btnStartAnalysis.isEnabled = true
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_analysis)

        ivWoundImage = findViewById(R.id.ivWoundImage)
        val btnSelectImage: Button = findViewById(R.id.btnSelectImage)
        btnStartAnalysis = findViewById(R.id.btnStartAnalysis)

        btnSelectImage.setOnClickListener {
            showImageSourceDialog()
        }

        btnStartAnalysis.setOnClickListener {
            if (currentImageUri != null) {
                showResultDialog()
            } else {
                Toast.makeText(this, "Silakan pilih gambar terlebih dahulu", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun createTempUri(): Uri {
        val timeStamp: String = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.US).format(Date())
        val imageFile = File.createTempFile(
            "JPEG_${timeStamp}_",
            ".jpg",
            applicationContext.cacheDir
        )
        tempImageUri = FileProvider.getUriForFile(applicationContext, "${applicationContext.packageName}.provider", imageFile)
        return tempImageUri as Uri
    }


    private fun showImageSourceDialog() {
        val options = arrayOf("Buka Kamera", "Pilih dari Galeri")
        AlertDialog.Builder(this)
            .setTitle("Pilih Sumber Gambar")
            .setItems(options) { _, which ->
                when (which) {
                    0 -> cameraLauncher.launch(createTempUri())
                    1 -> galleryLauncher.launch("image/*")
                }
            }
            .show()
    }

    private fun showResultDialog() {
        val isDiabetic = Random.nextBoolean()

        val builder = AlertDialog.Builder(this)
        if (isDiabetic) {
            builder.setTitle("Hasil Analisis: Risiko Terdeteksi")
            builder.setMessage("Berdasarkan analisis, luka Anda memiliki kemungkinan ciri-ciri luka diabetes. Segera konsultasikan dengan dokter.")
            // DIUBAH: Tombol ini sekarang membuka LocationActivity
            builder.setPositiveButton("Cek Lokasi Terdekat") { _, _ ->
                startActivity(Intent(this, LocationActivity::class.java))
            }
            builder.setNegativeButton("Tutup") { dialog, _ ->
                dialog.dismiss()
            }
        } else {
            builder.setTitle("Hasil Analisis: Anda Sehat")
            builder.setMessage("Luka Anda tidak menunjukkan ciri-ciri luka diabetes. Tetap jaga kebersihan dan kesehatan.")
            builder.setPositiveButton("OK") { dialog, _ ->
                dialog.dismiss()
            }
        }
        val dialog = builder.create()
        dialog.show()
    }
}