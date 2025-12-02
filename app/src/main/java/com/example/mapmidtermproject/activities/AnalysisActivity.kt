package com.example.mapmidtermproject.activities

import android.app.ProgressDialog
import android.content.Intent
import android.content.res.ColorStateList
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.net.Uri
import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import android.view.animation.OvershootInterpolator
import android.widget.ImageView
import android.widget.LinearLayout
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

    // UI Header: ImageView (sesuai perbaikan sebelumnya)
    private lateinit var btnViewGallery: ImageView

    private var currentImageUri: Uri? = null

    private lateinit var viewModel: WoundViewModel
    private lateinit var loadingDialog: ProgressDialog

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

        viewModel = ViewModelProvider(this)[WoundViewModel::class.java]

        // Setup Loading Dialog
        loadingDialog = ProgressDialog(this).apply {
            setMessage("Menganalisis Luka...")
            setCancelable(false)
        }

        // Inisialisasi View
        ivWoundImage = findViewById(R.id.ivWoundImage)
        btnSelectImage = findViewById(R.id.btnSelectImage)
        btnStartAnalysis = findViewById(R.id.btnStartAnalysis)
        btnViewGallery = findViewById(R.id.btnViewGallery) // ImageView

        // Listeners
        btnSelectImage.setOnClickListener { showImageSourceDialog() }

        // Klik area card placeholder juga memicu dialog pilih gambar
        findViewById<ViewGroup>(R.id.cardWoundImage).setOnClickListener { showImageSourceDialog() }

        btnStartAnalysis.setOnClickListener {
            if (currentImageUri != null) {
                loadingDialog.show()
                viewModel.analyzeImage(uri = currentImageUri!!)
            } else {
                Toast.makeText(this, "Mohon pilih gambar terlebih dahulu.", Toast.LENGTH_SHORT).show()
            }
        }

        btnViewGallery.setOnClickListener {
            startActivity(Intent(this, LocalGalleryActivity::class.java))
            // Animasi transisi slide
            overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left)
        }

        setupBottomNavigation()
        setupObservers()
    }

    private fun setupObservers() {
        // OBSERVE EVENT: Menggunakan Event Wrapper agar dialog tidak muncul 2x saat rotasi
        viewModel.analysisResult.observe(this) { event ->
            event.getContentIfNotHandled()?.let { rawResult ->

                loadingDialog.dismiss()

                val cleanResult = rawResult
                    .replace("⚠️", "")
                    .replace("✅", "")
                    .replace("❓", "")
                    .trim()

                val isSevere = isSevereCondition(cleanResult)

                showResultDialog(cleanResult, isSevere)

                // Simpan gambar otomatis setelah analisis selesai
                currentImageUri?.let { viewModel.saveImage(it) }
            }
        }
    }

    // --- ANIMASI SAAT GAMBAR DIPILIH ---
    private fun onImageSelected(uri: Uri) {
        currentImageUri = uri

        // 1. Sembunyikan Placeholder (Kotak garis putus-putus)
        val layoutPlaceholder = findViewById<LinearLayout>(R.id.layoutPlaceholder)
        layoutPlaceholder?.visibility = View.GONE

        // 2. Set Gambar ke ImageView
        ivWoundImage.setImageURI(uri)

        // 3. Animasi "Pop" (Fade In + Scale Up + Overshoot)
        // Set kondisi awal
        ivWoundImage.alpha = 0f
        ivWoundImage.scaleX = 0.8f
        ivWoundImage.scaleY = 0.8f

        // Jalankan animasi
        ivWoundImage.animate()
            .alpha(1f)       // Muncul pelan
            .scaleX(1f)      // Membesar ke normal
            .scaleY(1f)      // Membesar ke normal
            .setDuration(600)
            .setInterpolator(OvershootInterpolator()) // Efek membal
            .start()

        // 4. Update Tombol
        btnStartAnalysis.isEnabled = true

        // (Opsional) Ubah warna tombol jadi Teal terang saat aktif agar user tahu
        btnStartAnalysis.backgroundTintList = ColorStateList.valueOf(Color.parseColor("#009688"))
    }

    private fun isSevereCondition(resultText: String): Boolean {
        val dangerousKeywords = listOf(
            "Diabetic Wounds", "Burns", "Pressure Wounds",
            "Surgical Wounds", "Venous Wounds", "Laseration"
        )
        return dangerousKeywords.any { resultText.contains(it, ignoreCase = true) }
    }

    private fun showResultDialog(resultText: String, isSevere: Boolean) {
        val dialogView = layoutInflater.inflate(R.layout.dialog_custom_result, null)
        val builder = AlertDialog.Builder(this)
        val dialog = builder.create()

        val tvTitle = dialogView.findViewById<TextView>(R.id.tvDialogTitle)
        val tvMessage = dialogView.findViewById<TextView>(R.id.tvDialogMessage)
        val btnPositive = dialogView.findViewById<MaterialButton>(R.id.btnDialogPositive)
        val btnNegative = dialogView.findViewById<MaterialButton>(R.id.btnDialogNegative)
        val ivIcon = dialogView.findViewById<ImageView>(R.id.ivResultIcon)

        if (isSevere) {
            tvTitle.text = "PERHATIAN MEDIS"
            tvTitle.setTextColor(Color.RED)
            tvMessage.text = "$resultText\n\nREKOMENDASI:\nTerdeteksi luka berisiko tinggi. Segera konsultasikan ke dokter."

            // Ubah icon jadi merah/warning
            ivIcon.setColorFilter(Color.RED)
            ivIcon.setImageResource(android.R.drawable.stat_sys_warning)

            btnPositive.text = "Cari Rumah Sakit"
            btnPositive.setIconResource(android.R.drawable.ic_dialog_map)
            // Pastikan Anda punya color resource ini atau gunakan Color.RED
            btnPositive.backgroundTintList = ColorStateList.valueOf(Color.RED)

            btnPositive.setOnClickListener {
                dialog.dismiss()
                openGoogleMaps()
            }

            // Tampilkan tombol kembali untuk kondisi parah
            btnNegative.visibility = ViewGroup.VISIBLE
            btnNegative.text = "Kembali"
            btnNegative.setOnClickListener { dialog.dismiss() }

        } else {
            tvTitle.text = "HASIL ANALISIS"
            tvTitle.setTextColor(Color.parseColor("#009688")) // Teal
            tvMessage.text = "$resultText\n\nSARAN:\nLuka tampak ringan. Jaga kebersihan dan pantau secara berkala."

            // Ubah icon jadi hijau/info
            ivIcon.setColorFilter(Color.parseColor("#009688"))

            btnPositive.text = "Mengerti"
            btnPositive.setOnClickListener { dialog.dismiss() }

            btnNegative.visibility = ViewGroup.GONE
        }

        dialog.setView(dialogView)
        dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        dialog.show()
    }

    private fun openGoogleMaps() {
        val gmmIntentUri = Uri.parse("geo:0,0?q=Rumah+Sakit+Terdekat")
        val mapIntent = Intent(Intent.ACTION_VIEW, gmmIntentUri)
        mapIntent.setPackage("com.google.android.apps.maps")

        try {
            startActivity(mapIntent)
        } catch (e: Exception) {
            val browserIntent = Intent(Intent.ACTION_VIEW,
                Uri.parse("https://www.google.com/maps/search/Rumah+Sakit+Terdekat"))
            startActivity(browserIntent)
        }
    }

    private fun showImageSourceDialog() {
        val dialogView = layoutInflater.inflate(R.layout.dialog_image_source, null)
        val builder = AlertDialog.Builder(this)
        val dialog = builder.create()

        dialogView.findViewById<MaterialButton>(R.id.btnOpenCamera).setOnClickListener {
            dialog.dismiss()
            val intent = Intent(this, CameraActivity::class.java)
            cameraActivityLauncher.launch(intent)
        }

        dialogView.findViewById<MaterialButton>(R.id.btnOpenGallery).setOnClickListener {
            dialog.dismiss()
            galleryLauncher.launch("image/*")
        }

        dialog.setView(dialogView)
        dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
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

    override fun onResume() {
        super.onResume()
        val bottomNav: BottomNavigationView = findViewById(R.id.bottom_navigation)
        if (bottomNav.selectedItemId != R.id.nav_camera) {
            bottomNav.selectedItemId = R.id.nav_camera
        }
    }
}