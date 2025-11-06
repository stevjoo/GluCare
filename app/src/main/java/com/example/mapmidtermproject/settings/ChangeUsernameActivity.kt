package com.example.mapmidtermproject.settings

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.mapmidtermproject.R
import com.example.mapmidtermproject.utils.PreferenceHelper
import com.example.mapmidtermproject.utils.UserRepository
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase

class ChangeUsernameActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_change_username)

        val etNewUsername = findViewById<EditText>(R.id.etNewUsername)
        val btnSave = findViewById<Button>(R.id.btnSaveUsername)
        val btnBack = findViewById<ImageView>(R.id.btnBack)
        val pref = PreferenceHelper(this)

        btnSave.setOnClickListener {
            val username = etNewUsername.text.toString().trim()
            if (username.isEmpty()) {
                Toast.makeText(this, "Username tidak boleh kosong", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            val uid = Firebase.auth.currentUser?.uid
            if (uid == null) {
                Toast.makeText(this, "Tidak ada pengguna masuk", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            UserRepository.updateUserFields(uid, mapOf("name" to username))
                .addOnSuccessListener {
                    pref.saveUsername(username)
                    Toast.makeText(this, "Username diperbarui", Toast.LENGTH_SHORT).show()
                    val i = Intent(this, SettingsActivity::class.java)
                    i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP)
                    startActivity(i)
                    finish()
                }
                .addOnFailureListener {
                    Toast.makeText(this, "Gagal memperbarui username", Toast.LENGTH_SHORT).show()
                }
        }

        btnBack.setOnClickListener { finish() }
    }
}
