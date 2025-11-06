package com.example.mapmidtermproject.settings

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.example.mapmidtermproject.R
import com.example.mapmidtermproject.activities.AnalysisActivity
import com.example.mapmidtermproject.activities.LoginActivity
import com.example.mapmidtermproject.activities.MainActivity
import com.example.mapmidtermproject.models.UserDoc
import com.example.mapmidtermproject.utils.PreferenceHelper
import com.example.mapmidtermproject.utils.UserRepository
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
    private var userListener: com.google.firebase.firestore.ListenerRegistration? = null

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

        btnAccount.setOnClickListener { startActivity(Intent(this, AccountSettingsActivity::class.java)) }
        btnFAQ.setOnClickListener { startActivity(Intent(this, FAQActivity::class.java)) }
        btnPrivacyPolicy.setOnClickListener { startActivity(Intent(this, PrivacyPolicyActivity::class.java)) }

        btnLogout.setOnClickListener {
            auth.signOut()
            val client = GoogleSignIn.getClient(this, GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN).build())
            client.signOut().addOnCompleteListener {
                client.revokeAccess().addOnCompleteListener {
                    val i = Intent(this, LoginActivity::class.java)
                    i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
                    startActivity(i)
                    finishAffinity()
                }
            }
        }

        setupBottomNavigation()
    }

    override fun onResume() {
        super.onResume()
        val u = auth.currentUser
        if (u != null) {
            userListener?.remove()
            userListener = UserRepository.observeUser(u.uid) { doc: UserDoc? ->
                if (doc != null) {
                    tvUsername.text = if (doc.name.isNotBlank()) doc.name else "Belum diatur"
                    tvPhone.text = doc.phone ?: "Belum diatur"
                    val pref = PreferenceHelper(this)
                    if (doc.name.isNotBlank()) pref.saveUsername(doc.name)
                    if (!doc.phone.isNullOrBlank()) pref.savePhone(doc.phone!!)
                }
            }
        }
    }

    override fun onPause() {
        userListener?.remove()
        userListener = null
        super.onPause()
    }

    private fun setupBottomNavigation() {
        val bottomNav: BottomNavigationView = findViewById(R.id.bottom_navigation)
        bottomNav.selectedItemId = R.id.nav_settings
        bottomNav.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.nav_home -> {
                    val intent = Intent(this, MainActivity::class.java)
                    intent.flags = Intent.FLAG_ACTIVITY_REORDER_TO_FRONT
                    startActivity(intent)
                    overridePendingTransition(0, 0)
                    true
                }
                R.id.nav_camera -> {
                    val intent = Intent(this, AnalysisActivity::class.java)
                    intent.flags = Intent.FLAG_ACTIVITY_REORDER_TO_FRONT
                    startActivity(intent)
                    overridePendingTransition(0, 0)
                    true
                }
                R.id.nav_settings -> true
                else -> false
            }
        }
    }
}
