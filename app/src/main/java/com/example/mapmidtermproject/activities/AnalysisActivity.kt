package com.example.mapmidtermproject.activities

import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.net.Uri
import android.os.Bundle
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.example.mapmidtermproject.R
import com.example.mapmidtermproject.viewmodels.WoundViewModel
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.button.MaterialButton
import kotlin.random.Random

class AnalysisActivity : AppCompatActivity() {

    private lateinit var ivWoundImage: ImageView
    private lateinit var btnSelectImage: MaterialButton
    private lateinit var btnStartAnalysis: MaterialButton
    private lateinit var btnViewGallery: MaterialButton // Tombol Baru
    private var currentImageUri: Uri? = null

    // Panggil ViewModel
    private lateinit var viewModel: WoundViewModel

    private val cameraActivityLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == RESULT_OK) {
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

        // Init ViewModel
        viewModel = ViewModelProvider(this)[WoundViewModel::class.java]

        ivWoundImage = findViewById(R.id.ivWoundImage)
        btnSelectImage = findViewById(R.id.btnSelectImage)
        btnStartAnalysis = findViewById(R.id.btnStartAnalysis)

        // Tambahkan tombol ini di XML nanti
        btnViewGallery = findViewById(R.id.btnViewGallery)

        btnSelectImage.setOnClickListener { showImageSourceDialog() }
        ivWoundImage.setOnClickListener { showImageSourceDialog() }

        btnStartAnalysis.setOnClickListener {
            if (currentImageUri != null) showResultDialog()
            else Toast.makeText(this, "Pilih gambar dulu!", Toast.LENGTH_SHORT).show()
        }

        btnViewGallery.setOnClickListener {
            // Buka Galeri Lokal
            startActivity(Intent(this, LocalGalleryActivity::class.java))
        }

        setupBottomNavigation()
    }

    private fun onImageSelected(uri: Uri) {
        currentImageUri = uri
        ivWoundImage.setImageURI(uri)
        btnStartAnalysis.isEnabled = true
    }

    private fun showImageSourceDialog() {
        val builder = AlertDialog.Builder(this)
        builder.setTitle("Pilih Sumber")
        builder.setItems(arrayOf("Kamera", "Galeri")) { _, which ->
            if (which == 0) {
                val intent = Intent(this, CameraActivity::class.java)
                cameraActivityLauncher.launch(intent)
            } else {
                galleryLauncher.launch("image/*")
            }
        }
        builder.show()
    }

    private fun showResultDialog() {
        // --- MACHINE LEARNING DUMMY (Akan kita ganti nanti) ---
        val isDiabetic = Random.nextBoolean()
        // ----------------------------------------------------

        // SIMPAN KE INTERNAL STORAGE (MVVM WAY)
        currentImageUri?.let {
            viewModel.saveImage(it)
            Toast.makeText(this, "Foto tersimpan di Galeri Lokal", Toast.LENGTH_SHORT).show()
        }

        val dialogView = layoutInflater.inflate(R.layout.dialog_custom_result, null)
        val builder = AlertDialog.Builder(this)
        builder.setView(dialogView)
        val dialog = builder.create()
        dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

        val tvTitle = dialogView.findViewById<TextView>(R.id.tvDialogTitle)
        val tvMessage = dialogView.findViewById<TextView>(R.id.tvDialogMessage)
        val btnPositive = dialogView.findViewById<MaterialButton>(R.id.btnDialogPositive)

        if (isDiabetic) {
            tvTitle.text = "Indikasi Ditemukan"
            tvMessage.text = "Analisis menunjukkan ciri luka diabetes. Segera konsultasi ke dokter."
        } else {
            tvTitle.text = "Hasil Normal"
            tvMessage.text = "Tidak ditemukan ciri khas luka diabetes. Tetap jaga kebersihan luka."
        }

        btnPositive.setOnClickListener { dialog.dismiss() }
        dialog.show()
    }

    private fun setupBottomNavigation() {
        val bottomNav: BottomNavigationView = findViewById(R.id.bottom_navigation)
        bottomNav.selectedItemId = R.id.nav_camera
        // ... (Kode navigasi sama seperti sebelumnya) ...
    }
}