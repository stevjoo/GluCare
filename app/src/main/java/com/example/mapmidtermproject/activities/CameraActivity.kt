package com.example.mapmidtermproject.activities

import android.Manifest
import android.content.ContentValues
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.view.View
import android.widget.ImageButton
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.camera.core.*
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.core.content.ContextCompat
import com.example.mapmidtermproject.R
import com.google.android.material.button.MaterialButton
import com.google.android.material.floatingactionbutton.FloatingActionButton
import kotlin.random.Random

class CameraActivity : AppCompatActivity() {

    // View
    private lateinit var previewView: PreviewView
    private lateinit var btnShutter: FloatingActionButton
    private lateinit var btnFlip: ImageButton
    private lateinit var btnFlash: ImageButton
    private lateinit var btnClose: ImageButton
    private lateinit var btnGallery: ImageButton

    // CameraX
    private var imageCapture: ImageCapture? = null
    private var cameraSelector: CameraSelector = CameraSelector.DEFAULT_BACK_CAMERA
    private var camera: Camera? = null
    private var flashOn = false
    private var isSaving = false
    private var lastCapturedUri: Uri? = null

    // Permission
    private val requestCamPerm = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { granted ->
        if (granted) startCamera() else {
            Toast.makeText(this, "Izin kamera ditolak", Toast.LENGTH_SHORT).show()
            setResult(RESULT_CANCELED)
            finish()
        }
    }

    // Pick from gallery (ikon kiri bawah di layar kamera)
    private val pickFromGallery =
        registerForActivityResult(ActivityResultContracts.GetContent()) { uri ->
            if (uri != null) {
                // Dari galeri di dalam kamera → langsung kirim balik ke AnalysisActivity
                setResult(RESULT_OK, Intent().putExtra("captured_uri", uri.toString()))
                finish()
            }
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_camera)

        // Bind views
        previewView = findViewById(R.id.previewView)
        btnShutter = findViewById(R.id.btnShutter)
        btnFlip = findViewById(R.id.btnFlip)
        btnFlash = findViewById(R.id.btnFlash)
        btnClose = findViewById(R.id.btnClose)
        btnGallery = findViewById(R.id.btnGallery)

        // Actions
        btnClose.setOnClickListener {
            setResult(RESULT_CANCELED)
            finish()
        }

        btnFlip.setOnClickListener {
            cameraSelector =
                if (cameraSelector == CameraSelector.DEFAULT_BACK_CAMERA)
                    CameraSelector.DEFAULT_FRONT_CAMERA
                else CameraSelector.DEFAULT_BACK_CAMERA
            startCamera()
        }

        btnFlash.setOnClickListener {
            flashOn = !flashOn
            updateFlashUi()
            imageCapture?.flashMode = if (flashOn) ImageCapture.FLASH_MODE_ON else ImageCapture.FLASH_MODE_OFF
            camera?.cameraControl?.enableTorch(flashOn)
        }

        btnGallery.setOnClickListener { pickFromGallery.launch("image/*") }

        btnShutter.setOnClickListener { takePhoto() }

        // Permission
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA)
            == PackageManager.PERMISSION_GRANTED
        ) startCamera() else requestCamPerm.launch(Manifest.permission.CAMERA)
    }

    private fun startCamera() {
        val cameraProviderFuture = ProcessCameraProvider.getInstance(this)
        cameraProviderFuture.addListener({
            val provider = cameraProviderFuture.get()

            val preview = Preview.Builder().build().also {
                it.setSurfaceProvider(previewView.surfaceProvider)
            }

            imageCapture = ImageCapture.Builder()
                .setCaptureMode(ImageCapture.CAPTURE_MODE_MINIMIZE_LATENCY)
                .setTargetRotation(previewView.display.rotation) // orientasi benar
                .setFlashMode(if (flashOn) ImageCapture.FLASH_MODE_ON else ImageCapture.FLASH_MODE_OFF)
                .build()

            try {
                provider.unbindAll()
                camera = provider.bindToLifecycle(this, cameraSelector, preview, imageCapture)

                // enable/disable flash button sesuai ketersediaan
                val hasFlash = camera?.cameraInfo?.hasFlashUnit() == true
                btnFlash.isEnabled = hasFlash
                if (!hasFlash) flashOn = false
                updateFlashUi()
            } catch (e: Exception) {
                Toast.makeText(this, "Gagal membuka kamera", Toast.LENGTH_SHORT).show()
                finish()
            }
        }, ContextCompat.getMainExecutor(this))
    }

    private fun updateFlashUi() {
        // ganti ikon on/off (pakai drawable yang sudah ada di project)
        btnFlash.setImageResource(
            if (flashOn) R.drawable.ic_baseline_flash_on_24
            else R.drawable.ic_baseline_flash_off_24
        )
        btnFlash.alpha = if (flashOn) 1f else 0.8f
    }

    private fun takePhoto() {
        if (isSaving) return
        val capture = imageCapture ?: return

        isSaving = true
        btnShutter.isEnabled = false

        val name = "IMG_${System.currentTimeMillis()}"
        val values = ContentValues().apply {
            put(MediaStore.MediaColumns.DISPLAY_NAME, name)
            put(MediaStore.MediaColumns.MIME_TYPE, "image/jpeg")
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                put(MediaStore.Images.Media.RELATIVE_PATH, "Pictures/GluCare")
            }
        }

        val out = ImageCapture.OutputFileOptions.Builder(
            contentResolver,
            MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
            values
        ).build()

        capture.takePicture(
            out,
            ContextCompat.getMainExecutor(this),
            object : ImageCapture.OnImageSavedCallback {
                override fun onError(exc: ImageCaptureException) {
                    isSaving = false
                    btnShutter.isEnabled = true
                    Toast.makeText(this@CameraActivity, "Gagal mengambil foto", Toast.LENGTH_SHORT).show()
                }

                override fun onImageSaved(result: ImageCapture.OutputFileResults) {
                    isSaving = false
                    btnShutter.isEnabled = true
                    lastCapturedUri = result.savedUri

                    showAnalysisResultDialog()
                }
            }
        )
    }

    private fun showAnalysisResultDialog() {
        val isDiabetic = Random.nextBoolean()

        // 1. Inflate layout kustom
        val dialogView = layoutInflater.inflate(R.layout.dialog_custom_result, null)

        // 2. Buat dialog builder
        val builder = AlertDialog.Builder(this)
        builder.setView(dialogView)

        // 3. Buat dialog dan atur latar transparan
        val dialog = builder.create()
        dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        dialog.setCancelable(false) // Mencegah dialog ditutup dengan tombol back

        // 4. Ambil referensi UI
        val tvTitle = dialogView.findViewById<TextView>(R.id.tvDialogTitle)
        val tvMessage = dialogView.findViewById<TextView>(R.id.tvDialogMessage)
        val btnPositive = dialogView.findViewById<MaterialButton>(R.id.btnDialogPositive)
        val btnNegative = dialogView.findViewById<MaterialButton>(R.id.btnDialogNegative)

        if (isDiabetic) {
            // --- KONDISI PERINGATAN ---
            tvTitle.text = "Peringatan: Indikasi Ditemukan"
            tvMessage.text = "Analisis gambar Anda menunjukkan beberapa ciri yang konsisten dengan luka diabetes (ulkus diabetik).\n\nSegera pertimbangkan untuk konsultasi dengan tenaga medis profesional."

            btnPositive.text = "Cek RS Terdekat"
            btnPositive.setOnClickListener {
                startActivity(Intent(this, LocationActivity::class.java))
                dialog.dismiss() // Tutup dialog setelah aksi
            }

            // Tombol kedua sekarang untuk "Ambil Ulang"
            btnNegative.visibility = View.VISIBLE
            btnNegative.text = "Ambil Ulang"
            btnNegative.setOnClickListener {
                dialog.dismiss()
            }

        } else {
            // --- KONDISI NORMAL ---
            tvTitle.text = "Hasil Analisis"
            tvMessage.text = "Tidak ditemukan ciri khas luka diabetes pada foto Anda.\n\nPENTING: Aplikasi ini bukan pengganti nasihat medis."

            // Aksi utama adalah lanjut ke halaman analisis
            btnPositive.text = "Lanjut Analisis"
            btnPositive.setOnClickListener {
                lastCapturedUri?.let {
                    setResult(RESULT_OK, Intent().putExtra("captured_uri", it.toString()))
                }
                finish()
            }

            // Tombol kedua untuk "Ambil Ulang"
            btnNegative.visibility = View.VISIBLE
            btnNegative.text = "Ambil Ulang"
            btnNegative.setOnClickListener {
                dialog.dismiss()
            }
        }

        dialog.show()
    }
}