package com.example.mapmidtermproject.settings

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

class ChangePhoneActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_change_phone)

        val etNewPhone = findViewById<EditText>(R.id.etNewPhone)
        val btnSave = findViewById<Button>(R.id.btnSavePhone)
        val pref = PreferenceHelper(this)

        btnSave.setOnClickListener {
            val phone = etNewPhone.text.toString().trim()
            if (phone.isEmpty()) {
                Toast.makeText(this, "Nomor telepon tidak boleh kosong", Toast.LENGTH_SHORT).show()
            } else {
                val uid = Firebase.auth.currentUser?.uid
                if (uid == null) {
                    Toast.makeText(this, "Tidak ada pengguna masuk", Toast.LENGTH_SHORT).show()
                    return@setOnClickListener
                }
                UserRepository.updateUserFields(uid, mapOf("phone" to phone))
                    .addOnSuccessListener {
                        pref.savePhone(phone)
                        Toast.makeText(this, "Nomor telepon berhasil diperbarui", Toast.LENGTH_SHORT).show()
                        finish()
                    }
                    .addOnFailureListener {
                        Toast.makeText(this, "Gagal memperbarui nomor telepon", Toast.LENGTH_SHORT).show()
                    }
            }
        }

        val btnBack = findViewById<ImageView>(R.id.btnBack)
        btnBack.setOnClickListener { finish() }
    }
}
