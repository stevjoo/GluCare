package com.example.mapmidtermproject.settings

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.example.mapmidtermproject.R
import com.example.mapmidtermproject.activities.AnalysisActivity
import com.example.mapmidtermproject.activities.LogActivity
import com.example.mapmidtermproject.activities.LoginActivity
import com.example.mapmidtermproject.activities.MainActivity
import com.example.mapmidtermproject.viewmodels.UserViewModel
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase

class SettingsActivity : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth
    private lateinit var tvUsername: TextView
    private lateinit var tvPhone: TextView
    private lateinit var viewModel: UserViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_settings)

        auth = Firebase.auth
        viewModel = ViewModelProvider(this)[UserViewModel::class.java]

        tvUsername = findViewById(R.id.tvUsername)
        tvPhone = findViewById(R.id.tvPhone)

        val btnAccount = findViewById<LinearLayout>(R.id.btnAccount)
        val btnFAQ = findViewById<LinearLayout>(R.id.btnFAQ)
        val btnPrivacyPolicy = findViewById<LinearLayout>(R.id.btnPrivacyPolicy)
        val btnLogout = findViewById<Button>(R.id.btnLogout)

        btnAccount.setOnClickListener { startActivity(Intent(this, AccountSettingsActivity::class.java)) }
        btnFAQ.setOnClickListener { startActivity(Intent(this, FAQActivity::class.java)) }
        btnPrivacyPolicy.setOnClickListener { startActivity(Intent(this, PrivacyPolicyActivity::class.java)) }
        btnLogout.setOnClickListener { signOut() }

        viewModel.userProfile.observe(this) { profile ->
            val currentUser = auth.currentUser
            val googleName = currentUser?.displayName ?: "Pengguna"

            if (profile != null) {
                tvUsername.text = if (!profile.username.isNullOrEmpty()) profile.username else googleName
                tvPhone.text = if (!profile.phone.isNullOrEmpty()) profile.phone else "Belum diatur"
            } else {
                tvUsername.text = googleName
                tvPhone.text = "Belum diatur"
            }
        }

        setupBottomNavigation()
    }

    override fun onResume() {
        super.onResume()
        viewModel.loadProfile()
        val bottomNav: BottomNavigationView = findViewById(R.id.bottom_navigation)
        if (bottomNav.selectedItemId != R.id.nav_settings) {
            bottomNav.selectedItemId = R.id.nav_settings
        }
    }

    // --- NAVIGASI YANG SUDAH DIPERBAIKI (ANIMASI AKTIF) ---
    private fun setupBottomNavigation() {
        val bottomNav: BottomNavigationView = findViewById(R.id.bottom_navigation)

        bottomNav.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.nav_home -> {
                    startActivity(Intent(this, MainActivity::class.java).apply { flags = Intent.FLAG_ACTIVITY_REORDER_TO_FRONT })
                    // HAPUS overridePendingTransition
                    true
                }
                R.id.nav_log -> {
                    startActivity(Intent(this, LogActivity::class.java).apply { flags = Intent.FLAG_ACTIVITY_REORDER_TO_FRONT })
                    // HAPUS overridePendingTransition
                    true
                }
                R.id.nav_camera -> {
                    startActivity(Intent(this, AnalysisActivity::class.java).apply { flags = Intent.FLAG_ACTIVITY_REORDER_TO_FRONT })
                    // HAPUS overridePendingTransition
                    true
                }
                R.id.nav_settings -> true
                else -> false
            }
        }
    }

    private fun signOut() {
        auth.signOut()
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN).build()
        GoogleSignIn.getClient(this, gso).signOut().addOnCompleteListener {
            val intent = Intent(this, LoginActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivity(intent)
            finish()
        }
    }
}