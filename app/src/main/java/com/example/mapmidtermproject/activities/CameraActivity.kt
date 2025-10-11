package com.example.mapmidtermproject.activities

import android.Manifest
import android.content.ContentValues
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.widget.ImageButton
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.camera.core.*
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.core.content.ContextCompat
import com.example.mapmidtermproject.R

class CameraActivity : AppCompatActivity() {

    // 🟢 View elements dari layout
    private lateinit var previewView: PreviewView
    private lateinit var btnShutter: ImageButton
    private lateinit var btnFlip: ImageButton
    private lateinit var btnFlash: ImageButton
    private lateinit var btnClose: ImageButton

    // 🟢 Variabel kamera
    private var imageCapture: ImageCapture? = null
    private var cameraSelector: CameraSelector = CameraSelector.DEFAULT_BACK_CAMERA
    private var camera: Camera? = null
    private var flashOn = false

    // 🟢 Request permission kamera
    private val requestCamPerm = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { granted ->
        if (granted) startCamera()
        else {
            Toast.makeText(this, "Izin kamera ditolak", Toast.LENGTH_SHORT).show()
            setResult(RESULT_CANCELED)
            finish()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_camera)

        // 🔹 Hubungkan view dari layout
        previewView = findViewById(R.id.previewView)
        btnShutter = findViewById(R.id.btnShutter)
        btnFlip = findViewById(R.id.btnFlip)
        btnFlash = findViewById(R.id.btnFlash)
        btnClose = findViewById(R.id.btnClose)

        // 🔹 Tombol tutup kamera (batal ambil foto)
        btnClose.setOnClickListener {
            setResult(RESULT_CANCELED)
            finish()
        }

        // 🔹 Tombol ganti kamera (depan/belakang)
        btnFlip.setOnClickListener {
            cameraSelector =
                if (cameraSelector == CameraSelector.DEFAULT_BACK_CAMERA)
                    CameraSelector.DEFAULT_FRONT_CAMERA
                else CameraSelector.DEFAULT_BACK_CAMERA
            startCamera()
        }

        // 🔹 Tombol flash
        btnFlash.setOnClickListener {
            flashOn = !flashOn
            imageCapture?.flashMode =
                if (flashOn) ImageCapture.FLASH_MODE_ON else ImageCapture.FLASH_MODE_OFF
            camera?.cameraControl?.enableTorch(flashOn)
        }

        // 🔹 Tombol shutter (ambil foto)
        btnShutter.setOnClickListener { takePhoto() }

        // 🔹 Cek permission kamera
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA)
            == PackageManager.PERMISSION_GRANTED
        ) startCamera()
        else requestCamPerm.launch(Manifest.permission.CAMERA)
    }

    // 🟢 Mulai kamera
    private fun startCamera() {
        val cameraProviderFuture = ProcessCameraProvider.getInstance(this)
        cameraProviderFuture.addListener({
            val provider = cameraProviderFuture.get()

            val preview = Preview.Builder().build().also {
                it.setSurfaceProvider(previewView.surfaceProvider)
            }

            imageCapture = ImageCapture.Builder()
                .setCaptureMode(ImageCapture.CAPTURE_MODE_MINIMIZE_LATENCY)
                .setFlashMode(
                    if (flashOn) ImageCapture.FLASH_MODE_ON
                    else ImageCapture.FLASH_MODE_OFF
                )
                .build()

            try {
                provider.unbindAll()
                camera = provider.bindToLifecycle(
                    this,
                    cameraSelector,
                    preview,
                    imageCapture
                )
                btnFlash.isEnabled = camera?.cameraInfo?.hasFlashUnit() == true
            } catch (e: Exception) {
                Toast.makeText(this, "Gagal membuka kamera", Toast.LENGTH_SHORT).show()
                finish()
            }
        }, ContextCompat.getMainExecutor(this))
    }

    // 🟢 Ambil foto dan kirim hasilnya ke AnalysisActivity
    private fun takePhoto() {
        val capture = imageCapture ?: return

        val name = "IMG_${System.currentTimeMillis()}"
        val values = ContentValues().apply {
            put(MediaStore.MediaColumns.DISPLAY_NAME, name)
            put(MediaStore.MediaColumns.MIME_TYPE, "image/jpeg")
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                put(MediaStore.Images.Media.RELATIVE_PATH, "Pictures/DiabetesApp")
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
                    Toast.makeText(
                        this@CameraActivity,
                        "Gagal mengambil foto",
                        Toast.LENGTH_SHORT
                    ).show()
                }

                override fun onImageSaved(result: ImageCapture.OutputFileResults) {
                    val uri = result.savedUri?.toString()
                    setResult(RESULT_OK, Intent().putExtra("captured_uri", uri))
                    finish()
                }
            }
        )
    }
}