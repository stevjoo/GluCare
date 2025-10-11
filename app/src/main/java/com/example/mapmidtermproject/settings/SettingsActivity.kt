package com.example.mapmidtermproject.settings

import android.content.Intent
import android.os.Bundle
import android.widget.LinearLayout
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import com.example.mapmidtermproject.R

class SettingsActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_settings)

        val btnAccount = findViewById<LinearLayout>(R.id.btnAccount)
        val btnFAQ = findViewById<LinearLayout>(R.id.btnFAQ)
        val btnPrivacyPolicy = findViewById<LinearLayout>(R.id.btnPrivacyPolicy)
        val btnLogout = findViewById<Button>(R.id.btnLogout)

        btnAccount.setOnClickListener {
            val intent = Intent(this, AccountSettingsActivity::class.java)
            startActivity(intent)
        }

        btnFAQ.setOnClickListener {
            val intent = Intent(this, FAQActivity::class.java)
            startActivity(intent)
        }

        btnPrivacyPolicy.setOnClickListener {
            val intent = Intent(this, PrivacyPolicyActivity::class.java)
            startActivity(intent)
        }

        btnLogout.setOnClickListener {
            finishAffinity()
        }
    }
}
