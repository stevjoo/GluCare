package com.example.mapmidtermproject.settings

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.example.mapmidtermproject.R
import com.example.mapmidtermproject.activities.LoginActivity
import com.example.mapmidtermproject.utils.PreferenceHelper
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase


class SettingsActivity : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth
    private lateinit var tvUsername: TextView
    private lateinit var tvPhone: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_settings)

        auth = Firebase.auth

        tvUsername = findViewById(R.id.tvUsername)
        tvPhone = findViewById(R.id.tvPhone)

        val btnAccount = findViewById<LinearLayout>(R.id.btnAccount)
        val btnFAQ = findViewById<LinearLayout>(R.id.btnFAQ)
        val btnPrivacyPolicy = findViewById<LinearLayout>(R.id.btnPrivacyPolicy)
        val btnLogout = findViewById<Button>(R.id.btnLogout)

        val pref = PreferenceHelper(this)
        tvUsername.text = pref.getUsername()
        tvPhone.text = pref.getPhone()

        btnAccount.setOnClickListener {
            startActivity(Intent(this, AccountSettingsActivity::class.java))
        }

        btnFAQ.setOnClickListener {
            startActivity(Intent(this, FAQActivity::class.java))
        }

        btnPrivacyPolicy.setOnClickListener {
            startActivity(Intent(this, PrivacyPolicyActivity::class.java))
        }

        btnLogout.setOnClickListener {
            signOut()
        }
    }

    override fun onResume() {
        super.onResume()
        val pref = PreferenceHelper(this)
        tvUsername.text = pref.getUsername()
        tvPhone.text = pref.getPhone()
    }

    private fun signOut() {
        auth.signOut()

        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN).build()
        val googleSignInClient = GoogleSignIn.getClient(this, gso)
        googleSignInClient.signOut().addOnCompleteListener {
            // Setelah Google Sign-Out selesai, arahkan ke Login
            val intent = Intent(this, LoginActivity::class.java)
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
            startActivity(intent)
            finish()
        }
    }
}
