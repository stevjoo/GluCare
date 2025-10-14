package com.example.mapmidtermproject.settings

import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.mapmidtermproject.R
import com.example.mapmidtermproject.utils.PreferenceHelper


class ChangeUsernameActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_change_username)

        val etNewUsername = findViewById<EditText>(R.id.etNewUsername)
        val btnSave = findViewById<Button>(R.id.btnSaveUsername)

        val pref = PreferenceHelper(this)

        btnSave.setOnClickListener {
            val username = etNewUsername.text.toString().trim()
            if (username.isEmpty()) {
                Toast.makeText(this, "Username tidak boleh kosong", Toast.LENGTH_SHORT).show()
            } else {
                pref.saveUsername(username)
                Toast.makeText(this, "Username berhasil diperbarui", Toast.LENGTH_SHORT).show()
                finish()
            }
        }

        val btnBack = findViewById<ImageView>(R.id.btnBack)
        btnBack.setOnClickListener {
            finish()
        }
    }
}

