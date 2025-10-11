package com.example.mapmidtermproject.activities

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.widget.Button
import android.widget.ImageView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.example.mapmidtermproject.R
import kotlin.random.Random

class AnalysisActivity : AppCompatActivity() {

    private lateinit var ivWoundImage: ImageView
    private lateinit var btnStartAnalysis: Button
    private var currentImageUri: Uri? = null

    // Launcher untuk kamera custom (CameraActivity)
    private val cameraActivityLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                // ambil URI string dari CameraActivity
                val uriStr = result.data?.getStringExtra("captured_uri")
                if (!uriStr.isNullOrEmpty()) {
                    val uri = Uri.parse(uriStr)
                    currentImageUri = uri
                    ivWoundImage.setImageURI(uri)
                    btnStartAnalysis.isEnabled = true
                }
            }
        }

    // Launcher untuk pilih gambar dari galeri (tetap seperti sebelumnya)
    private val galleryLauncher =
        registerForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
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

        btnSelectImage.setOnClickListener { showImageSourceDialog() }

        btnStartAnalysis.setOnClickListener {
            if (currentImageUri != null) {
                showResultDialog()
            } else {
                Toast.makeText(this, "Silakan pilih gambar terlebih dahulu", Toast.LENGTH_SHORT).show()
            }
        }
    }

    // Dialog sumber gambar: Kamera custom atau Galeri
    private fun showImageSourceDialog() {
        val options = arrayOf("Buka Kamera", "Pilih dari Galeri")
        AlertDialog.Builder(this)
            .setTitle("Pilih Sumber Gambar")
            .setItems(options) { _, which ->
                when (which) {
                    0 -> openInAppCamera()
                    1 -> galleryLauncher.launch("image/*")
                }
            }
            .show()
    }

    private fun openInAppCamera() {
        val intent = Intent(this, CameraActivity::class.java)
        cameraActivityLauncher.launch(intent)
    }

    // --- Bagian hasil analisis (tetap) ---
    private fun showResultDialog() {
        val isDiabetic = Random.nextBoolean()

        val builder = AlertDialog.Builder(this)
        if (isDiabetic) {
            builder.setTitle("Hasil Analisis: Risiko Terdeteksi")
            builder.setMessage(
                "Berdasarkan analisis, luka Anda memiliki kemungkinan ciri-ciri luka diabetes. " +
                        "Segera konsultasikan dengan dokter."
            )
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
        builder.create().show()
    }
}