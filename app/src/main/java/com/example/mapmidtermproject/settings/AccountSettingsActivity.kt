package com.example.mapmidtermproject.settings

import android.content.Intent
import android.os.Bundle
import android.widget.ImageView
import android.widget.LinearLayout
import androidx.appcompat.app.AppCompatActivity
import com.example.mapmidtermproject.R

class AccountSettingsActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_account_settings)

        val btnBack = findViewById<ImageView>(R.id.btnBack)
        val btnChangeUsername = findViewById<LinearLayout>(R.id.btnChangeUsername)
        val btnChangePhone = findViewById<LinearLayout>(R.id.btnChangePhone)
        btnBack.setOnClickListener {
            finish()
        }
        btnChangeUsername.setOnClickListener {
            val intent = Intent(this, ChangeUsernameActivity::class.java)
            startActivity(intent)
        }
        btnChangePhone.setOnClickListener {
            val intent = Intent(this, ChangePhoneActivity::class.java)
            startActivity(intent)
        }
    }
}
