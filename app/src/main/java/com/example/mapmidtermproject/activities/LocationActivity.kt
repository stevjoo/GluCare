package com.example.mapmidtermproject.activities

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat

class LocationActivity : AppCompatActivity() {

    // Launcher untuk meminta izin lokasi
    private val requestPermissionLauncher =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted: Boolean ->
            if (isGranted) {
                // Jika izin diberikan, buka peta
                openMapsForNearbyHospitals()
            } else {
                // Jika izin ditolak, beri tahu pengguna dan tutup activity
                Toast.makeText(this, "Izin lokasi dibutuhkan untuk fitur ini.", Toast.LENGTH_LONG).show()
                finish()
            }
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // Activity ini tidak butuh layout, tugasnya hanya memproses
        checkLocationPermission()
    }

    private fun checkLocationPermission() {
        when {
            ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED -> {
                // Izin sudah diberikan, langsung buka peta
                openMapsForNearbyHospitals()
            }
            else -> {
                // Minta izin ke pengguna
                requestPermissionLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION)
            }
        }
    }

    private fun openMapsForNearbyHospitals() {
        // Membuat query pencarian untuk "rumah sakit"
        // geo:0,0?q=rumah sakit akan mencari rumah sakit di sekitar lokasi pengguna saat ini
        val gmmIntentUri = Uri.parse("geo:0,0?q=Rumah Sakit")
        val mapIntent = Intent(Intent.ACTION_VIEW, gmmIntentUri)
        // Mengatur paket agar preferensi diberikan ke Google Maps jika ada
        mapIntent.setPackage("com.google.android.apps.maps")

        // Memeriksa apakah ada aplikasi yang bisa menangani intent ini
        if (mapIntent.resolveActivity(packageManager) != null) {
            startActivity(mapIntent)
        } else {
            Toast.makeText(this, "Tidak ada aplikasi peta yang terinstall.", Toast.LENGTH_LONG).show()
        }
        // Tutup activity ini setelah selesai
        finish()
    }
}