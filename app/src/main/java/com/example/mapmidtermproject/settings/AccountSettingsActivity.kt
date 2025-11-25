package com.example.mapmidtermproject.settings

import android.content.Intent
import android.os.Bundle
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.example.mapmidtermproject.R
import com.example.mapmidtermproject.activities.LoginActivity
import com.example.mapmidtermproject.utils.FirestoreHelper
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.material.button.MaterialButton

class AccountSettingsActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_account_settings)

        val btnBack = findViewById<ImageView>(R.id.btnBack)
        val btnChangeUsername = findViewById<LinearLayout>(R.id.btnChangeUsername)
        val btnChangePhone = findViewById<LinearLayout>(R.id.btnChangePhone)
        val btnDeleteAccount = findViewById<MaterialButton>(R.id.btnDeleteAccount)

        btnBack.setOnClickListener { finish() }
        btnChangeUsername.setOnClickListener { startActivity(Intent(this, ChangeUsernameActivity::class.java)) }
        btnChangePhone.setOnClickListener { startActivity(Intent(this, ChangePhoneActivity::class.java)) }

        btnDeleteAccount.setOnClickListener {
            AlertDialog.Builder(this)
                .setTitle("Hapus Akun Permanen?")
                .setMessage("Yakin ingin menghapus? Anda akan langsung keluar dari aplikasi.")
                .setPositiveButton("Hapus") { _, _ -> processDeleteAccount() }
                .setNegativeButton("Batal", null)
                .show()
        }
    }

    private fun processDeleteAccount() {
        // Tampilkan loading
        val loading = AlertDialog.Builder(this)
            .setMessage("Menghapus akun...")
            .setCancelable(false)
            .create()
        loading.show()

        FirestoreHelper.deleteAccount(
            onSuccess = {
                // --- SUKSES HAPUS ---
                loading.dismiss() // 1. Matikan loading SEGERA

                // 2. Bersihkan sesi Google (Fire-and-forget, jangan ditunggu)
                val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN).build()
                try {
                    GoogleSignIn.getClient(this, gso).signOut()
                } catch (e: Exception) {
                    e.printStackTrace()
                }

                // 3. LANGSUNG TENDANG KE LOGIN
                Toast.makeText(this, "Akun berhasil dihapus.", Toast.LENGTH_SHORT).show()
                val intent = Intent(this, LoginActivity::class.java)
                intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                startActivity(intent)
                finishAffinity() // Tutup semua activity di belakang
            },
            onReauthRequired = {
                // --- PERLU LOGIN ULANG ---
                loading.dismiss()
                AlertDialog.Builder(this)
                    .setTitle("Gagal: Sesi Habis")
                    .setMessage("Demi keamanan, mohon Logout dulu lalu Login kembali, baru coba hapus akun lagi.")
                    .setPositiveButton("Logout Sekarang") { _, _ ->
                        forceLogout()
                    }
                    .setNegativeButton("Batal", null)
                    .show()
            },
            onFailure = { e ->
                // --- ERROR ---
                loading.dismiss()
                Toast.makeText(this, "Gagal: ${e.message}", Toast.LENGTH_SHORT).show()
            }
        )
    }

    private fun forceLogout() {
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN).build()
        GoogleSignIn.getClient(this, gso).signOut()
        val intent = Intent(this, LoginActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent)
        finishAffinity()
    }
}