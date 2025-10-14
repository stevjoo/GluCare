package com.example.mapmidtermproject.settings

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.LinearLayout
import androidx.appcompat.app.AppCompatActivity
import com.example.mapmidtermproject.R
import com.example.mapmidtermproject.activities.LoginActivity
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase

class SettingsActivity : AppCompatActivity() {
    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_settings)
        auth = Firebase.auth

        val btnAccount = findViewById<LinearLayout>(R.id.btnAccount)
        val btnFAQ = findViewById<LinearLayout>(R.id.btnFAQ)
        val btnPrivacyPolicy = findViewById<LinearLayout>(R.id.btnPrivacyPolicy)
        val btnLogout = findViewById<Button>(R.id.btnLogout)

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

    private fun signOut() {
        // Sign out from Firebase
        auth.signOut()

        // Sign out from Google
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN).build()
        GoogleSignIn.getClient(this, gso).signOut().addOnCompleteListener {
            // Redirect to LoginActivity after sign out is complete
            val intent = Intent(this, LoginActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivity(intent)
            finish()
        }
    }
}