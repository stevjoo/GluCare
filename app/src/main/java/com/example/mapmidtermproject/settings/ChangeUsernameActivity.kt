package com.example.mapmidtermproject.settings

import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.mapmidtermproject.R
import com.example.mapmidtermproject.utils.FirestoreHelper

class ChangeUsernameActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_change_username)

        val etNewUsername = findViewById<EditText>(R.id.etNewUsername)
        val btnSave = findViewById<Button>(R.id.btnSaveUsername)
        val btnBack = findViewById<ImageView>(R.id.btnBack)

        btnBack.setOnClickListener { finish() }

        btnSave.setOnClickListener {
            val username = etNewUsername.text.toString().trim()
            if (username.isEmpty()) {
                Toast.makeText(this, "Username tidak boleh kosong", Toast.LENGTH_SHORT).show()
            } else {
                // Tampilkan pesan loading kecil
                btnSave.isEnabled = false
                btnSave.text = "Menyimpan..."

                FirestoreHelper.updateUserProfile(username, null) {
                    Toast.makeText(this, "Username diperbarui!", Toast.LENGTH_SHORT).show()
                    finish() // Kembali ke Settings, onResume di sana akan memuat nama baru
                }
            }
        }
    }
}