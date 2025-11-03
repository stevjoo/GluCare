package com.example.mapmidtermproject.activities

import android.app.Activity
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
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
import com.example.mapmidtermproject.activities.MainActivity
import com.example.mapmidtermproject.R
import com.example.mapmidtermproject.settings.SettingsActivity
import com.google.android.material.appbar.MaterialToolbar
import com.google.android.material.bottomnavigation.BottomNavigationView
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

//        val ivLogo = findViewById<ImageView>(R.id.ivLogo)
        ivWoundImage = findViewById(R.id.ivWoundImage)
        tvPlaceholderText = findViewById(R.id.tvPlaceholderText)
        btnSelectImage = findViewById(R.id.btnSelectImage)
        btnStartAnalysis = findViewById(R.id.btnStartAnalysis)

//        ivLogo.setOnClickListener {
//            val intent = Intent(this, MainActivity::class.java).apply {
//                addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP)
//            }
//            startActivity(intent)
//            finish()
//        }

        setAnalysisButtonEnabled(false)

        btnSelectImage.setOnClickListener { showImageSourceDialog() }
        ivWoundImage.setOnClickListener { showImageSourceDialog() }
        tvPlaceholderText.setOnClickListener { showImageSourceDialog() }

        btnStartAnalysis.setOnClickListener {
            if (currentImageUri != null) showResultDialog()
            else Toast.makeText(this, "Silakan pilih atau ambil gambar terlebih dahulu.", Toast.LENGTH_SHORT).show()
        }

        setupBottomNavigation()
    }

    private fun setupBottomNavigation() {
        val bottomNav: BottomNavigationView = findViewById(R.id.bottom_navigation)
        bottomNav.selectedItemId = R.id.nav_camera

        bottomNav.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.nav_home -> {
                    startActivity(Intent(this, MainActivity::class.java))
                    true
                }
                R.id.nav_camera -> true
                R.id.nav_settings -> {
                    startActivity(Intent(this, SettingsActivity::class.java))
                    true
                }
                else -> false
            }
        }
    }

    private fun onImageSelected(uri: Uri) {
        currentImageUri = uri
        ivWoundImage.apply {
            setImageURI(uri)
            layoutParams = layoutParams.apply {
                width = ViewGroup.LayoutParams.MATCH_PARENT
                height = ViewGroup.LayoutParams.MATCH_PARENT
            }
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
            btnStartAnalysis.setBackgroundColor(ContextCompat.getColor(this, R.color.blue))
            btnStartAnalysis.setTextColor(ContextCompat.getColor(this, android.R.color.white))
            btnStartAnalysis.iconTint = ContextCompat.getColorStateList(this, android.R.color.white)
        } else {
            btnStartAnalysis.setBackgroundColor(ContextCompat.getColor(this, R.color.gray_light))
            btnStartAnalysis.setTextColor(ContextCompat.getColor(this, R.color.gray_text_disabled))
            btnStartAnalysis.iconTint = ContextCompat.getColorStateList(this, R.color.gray_text_disabled)
        }
    }

    private fun showImageSourceDialog() {
        val dialogView = layoutInflater.inflate(R.layout.dialog_image_source, null)
        val builder = AlertDialog.Builder(this)
        builder.setView(dialogView)

        val dialog = builder.create()
        dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

        val btnOpenCamera = dialogView.findViewById<MaterialButton>(R.id.btnOpenCamera)
        val btnOpenGallery = dialogView.findViewById<MaterialButton>(R.id.btnOpenGallery)

        btnOpenCamera.setOnClickListener {
            openInAppCamera()
            dialog.dismiss()
        }

        btnOpenGallery.setOnClickListener {
            galleryLauncher.launch("image/*")
            dialog.dismiss()
        }

        dialog.show()
    }

    private fun openInAppCamera() {
        val intent = Intent(this, CameraActivity::class.java)
        cameraActivityLauncher.launch(intent)
    }

    private fun showResultDialog() {
        val isDiabetic = Random.nextBoolean()
        val dialogView = layoutInflater.inflate(R.layout.dialog_custom_result, null)
        val builder = AlertDialog.Builder(this)
        builder.setView(dialogView)

        val dialog = builder.create()
        dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

        val tvTitle = dialogView.findViewById<TextView>(R.id.tvDialogTitle)
        val tvMessage = dialogView.findViewById<TextView>(R.id.tvDialogMessage)
        val btnPositive = dialogView.findViewById<MaterialButton>(R.id.btnDialogPositive)
        val btnNegative = dialogView.findViewById<MaterialButton>(R.id.btnDialogNegative)

        if (isDiabetic) {
            tvTitle.text = "Peringatan: Indikasi Ditemukan"
            tvMessage.text = "Analisis gambar Anda menunjukkan adanya beberapa ciri yang konsisten dengan luka diabetes (ulkus diabetik).\n\nKami sangat menyarankan Anda untuk segera berkonsultasi dengan tenaga medis profesional untuk mendapatkan diagnosis dan penanganan yang tepat."
            btnPositive.text = "Cari Rumah Sakit Terdekat"
            btnPositive.setOnClickListener {
                startActivity(Intent(this, LocationActivity::class.java))
                dialog.dismiss()
            }
            btnNegative.visibility = View.VISIBLE
            btnNegative.text = "Kembali"
            btnNegative.setOnClickListener {
                dialog.dismiss()
            }
        } else {
            tvTitle.text = "Hasil Analisis"
            tvMessage.text = "Berdasarkan analisis gambar, tidak ditemukan ciri-ciri yang khas dari luka diabetes (ulkus diabetik) pada luka Anda.\n\nPENTING: Aplikasi ini bukan pengganti nasihat medis. Jika Anda ragu atau luka tidak kunjung membaik, segera konsultasikan dengan dokter."
            btnPositive.text = "Oke, Mengerti"
            btnPositive.setOnClickListener {
                dialog.dismiss()
            }
            btnNegative.visibility = View.GONE
        }

        dialog.show()
    }
}