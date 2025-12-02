package com.example.mapmidtermproject.activities

import android.app.ProgressDialog
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.net.Uri
import android.os.Bundle
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.example.mapmidtermproject.R
import com.example.mapmidtermproject.settings.SettingsActivity
import com.example.mapmidtermproject.viewmodels.WoundViewModel
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.button.MaterialButton

class AnalysisActivity : AppCompatActivity() {

    private lateinit var ivWoundImage: ImageView
    private lateinit var btnSelectImage: MaterialButton
    private lateinit var btnStartAnalysis: MaterialButton
    private lateinit var btnViewGallery: MaterialButton
    private var currentImageUri: Uri? = null

    private lateinit var viewModel: WoundViewModel
    private lateinit var loadingDialog: ProgressDialog // Biar user tau lagi mikir

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

        // Init Loading Dialog (Optional tapi bagus buat UX)
        loadingDialog = ProgressDialog(this).apply {
            setMessage("Sedang Menganalisis...")
            setCancelable(false)
        }

        ivWoundImage = findViewById(R.id.ivWoundImage)
        btnSelectImage = findViewById(R.id.btnSelectImage)
        btnStartAnalysis = findViewById(R.id.btnStartAnalysis)
        btnViewGallery = findViewById(R.id.btnViewGallery)

        btnSelectImage.setOnClickListener { showImageSourceDialog() }
        ivWoundImage.setOnClickListener { showImageSourceDialog() }

        // --- 1. TOMBOL START CUMA MEMICU ANALISIS ---
        btnStartAnalysis.setOnClickListener {
            if (currentImageUri != null) {
                loadingDialog.show() // Munculin loading
                viewModel.analyzeImage(currentImageUri!!) // Panggil AI beneran!
            } else {
                Toast.makeText(this, "Pilih gambar dulu!", Toast.LENGTH_SHORT).show()
            }
        }

        btnViewGallery.setOnClickListener {
            startActivity(Intent(this, LocalGalleryActivity::class.java))
            overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left)
        }

        setupBottomNavigation()
        setupObservers() // Setup pengamat hasil AI
    }

    // --- 2. FUNGSI UNTUK MENGAMATI HASIL DARI VIEWMODEL ---
    private fun setupObservers() {
        // Observer: String Hasil Analisis (Teks lengkap)
        viewModel.analysisResult.observe(this) { resultText ->
            loadingDialog.dismiss() // Tutup loading

            // Kita ambil status diabetes dari LiveData satunya lagi biar gampang if-else nya
            val isDiabetic = viewModel.isDiabeticDetected.value ?: false

            // Tampilkan Dialog dengan hasil ASLI dari AI
            showResultDialog(isDiabetic, resultText)

            // Simpan gambar otomatis kalau mau (Opsional)
            currentImageUri?.let { viewModel.saveImage(it) }
        }
    }

    override fun onResume() {
        super.onResume()
        val bottomNav: BottomNavigationView = findViewById(R.id.bottom_navigation)
        if (bottomNav.selectedItemId != R.id.nav_camera) {
            bottomNav.selectedItemId = R.id.nav_camera
        }
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

    // --- 3. DIALOG MENERIMA HASIL DARI AI, BUKAN RANDOM ---
    private fun showResultDialog(isDiabetic: Boolean, detailText: String) {
        val dialogView = layoutInflater.inflate(R.layout.dialog_custom_result, null)
        val builder = AlertDialog.Builder(this)
        builder.setView(dialogView)
        val dialog = builder.create()
        dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

        val tvTitle = dialogView.findViewById<TextView>(R.id.tvDialogTitle)
        val tvMessage = dialogView.findViewById<TextView>(R.id.tvDialogMessage)
        val btnPositive = dialogView.findViewById<MaterialButton>(R.id.btnDialogPositive)

        // Gunakan parameter isDiabetic yang dikirim dari ViewModel
        if (isDiabetic) {
            tvTitle.text = "Indikasi Ditemukan"
            // Tampilkan detail text dari AI (misal: "Diabetic Wounds (85%)")
            tvMessage.text = "$detailText\n\nSaran: Segera konsultasi ke dokter."
        } else {
            tvTitle.text = "Hasil Normal / Tidak Jelas"
            tvMessage.text = "$detailText\n\nSaran: Tetap jaga kebersihan luka."
        }

        btnPositive.setOnClickListener { dialog.dismiss() }
        dialog.show()
    }

    private fun setupBottomNavigation() {
        val bottomNav: BottomNavigationView = findViewById(R.id.bottom_navigation)

        bottomNav.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.nav_home -> {
                    startActivity(Intent(this, MainActivity::class.java).apply { flags = Intent.FLAG_ACTIVITY_REORDER_TO_FRONT })
                    true
                }
                R.id.nav_log -> {
                    startActivity(Intent(this, LogActivity::class.java).apply { flags = Intent.FLAG_ACTIVITY_REORDER_TO_FRONT })
                    true
                }
                R.id.nav_camera -> true
                R.id.nav_settings -> {
                    startActivity(Intent(this, SettingsActivity::class.java).apply { flags = Intent.FLAG_ACTIVITY_REORDER_TO_FRONT })
                    true
                }
                else -> false
            }
        }
    }
}