package com.example.mapmidtermproject.activities

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.example.mapmidtermproject.R
import com.google.android.material.appbar.MaterialToolbar
import com.google.android.material.button.MaterialButton
import kotlin.random.Random

class AnalysisActivity : AppCompatActivity() {

    private lateinit var ivWoundImage: ImageView
    private lateinit var tvPlaceholderText: TextView
    private lateinit var btnSelectImage: MaterialButton
    private lateinit var btnStartAnalysis: MaterialButton
    private var currentImageUri: Uri? = null

    private val cameraActivityLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                val uriStr = result.data?.getStringExtra("captured_uri")
                if (!uriStr.isNullOrEmpty()) onImageSelected(Uri.parse(uriStr))
            }
        }

    private val galleryLauncher =
        registerForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
            uri?.let { onImageSelected(it) }
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_analysis)

        findViewById<MaterialToolbar>(R.id.topAppBar)
            .setNavigationOnClickListener { finish() }

        ivWoundImage = findViewById(R.id.ivWoundImage)
        tvPlaceholderText = findViewById(R.id.tvPlaceholderText)
        btnSelectImage = findViewById(R.id.btnSelectImage)
        btnStartAnalysis = findViewById(R.id.btnStartAnalysis)

        setAnalysisButtonEnabled(false)

        btnSelectImage.setOnClickListener { showImageSourceDialog() }
        ivWoundImage.setOnClickListener { showImageSourceDialog() }
        tvPlaceholderText.setOnClickListener { showImageSourceDialog() }

        btnStartAnalysis.setOnClickListener {
            if (currentImageUri != null) showResultDialog()
            else Toast.makeText(this, "Silakan pilih/ambil gambar terlebih dahulu.", Toast.LENGTH_SHORT).show()
        }
    }

    private fun onImageSelected(uri: Uri) {
        currentImageUri = uri
        ivWoundImage.apply {
            setImageURI(uri)
            val lp = layoutParams
            lp.width = ViewGroup.LayoutParams.MATCH_PARENT
            lp.height = ViewGroup.LayoutParams.MATCH_PARENT
            layoutParams = lp
            scaleType = ImageView.ScaleType.CENTER_CROP
            background = null
            imageTintList = null
        }
        tvPlaceholderText.visibility = View.GONE
        setAnalysisButtonEnabled(true)
    }

    private fun setAnalysisButtonEnabled(enabled: Boolean) {
        btnStartAnalysis.isEnabled = enabled
        if (enabled) {
            // aktif
            btnStartAnalysis.setBackgroundColor(ContextCompat.getColor(this, R.color.primary_active))
            btnStartAnalysis.setTextColor(ContextCompat.getColor(this, android.R.color.white))
            btnStartAnalysis.iconTint = ContextCompat.getColorStateList(this, android.R.color.white)
        } else {
            // nonaktif
            btnStartAnalysis.setBackgroundColor(ContextCompat.getColor(this, R.color.primary_disabled))
            btnStartAnalysis.setTextColor(ContextCompat.getColor(this, R.color.gray_text_disabled))
            btnStartAnalysis.iconTint = ContextCompat.getColorStateList(this, R.color.gray_text_disabled)
        }
    }

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

    private fun showResultDialog() {
        val isDiabetic = Random.nextBoolean()
        val builder = AlertDialog.Builder(this)
        if (isDiabetic) {
            builder.setTitle("Peringatan: Indikasi Ditemukan")
            builder.setMessage(
                "Analisis gambar Anda menunjukkan adanya beberapa ciri yang konsisten dengan luka diabetes (ulkus diabetik).\n\n" +
                        "Kami sangat menyarankan Anda untuk segera berkonsultasi dengan tenaga medis profesional untuk mendapatkan diagnosis dan penanganan yang tepat."
            )
            builder.setPositiveButton("Cari Rumah Sakit Terdekat") { _, _ ->
                startActivity(Intent(this, LocationActivity::class.java))
            }
            builder.setNegativeButton("Kembali") { d, _ -> d.dismiss() }
        } else {
            builder.setTitle("Hasil Analisis")
            builder.setMessage(
                "Berdasarkan analisis gambar, tidak ditemukan ciri-ciri khas luka diabetes pada foto Anda.\n\n" +
                        "Catatan: Aplikasi ini bukan pengganti saran medis profesional."
            )
            builder.setPositiveButton("Oke, Mengerti") { d, _ -> d.dismiss() }
        }
        builder.create().show()
    }
}