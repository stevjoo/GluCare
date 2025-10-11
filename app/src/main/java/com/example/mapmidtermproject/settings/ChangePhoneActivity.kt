package com.example.mapmidtermproject.settings

import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.mapmidtermproject.R

class ChangePhoneActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_change_phone)

        val etNewPhone = findViewById<EditText>(R.id.etNewPhone)
        val btnSave = findViewById<Button>(R.id.btnSavePhone)

        btnSave.setOnClickListener {
            val phone = etNewPhone.text.toString().trim()
            if (phone.isEmpty()) {
                Toast.makeText(this, "Nomor telepon tidak boleh kosong", Toast.LENGTH_SHORT).show()
            } else {
                // TODO: Tambahkan logika simpan nomor baru ke database / API
                Toast.makeText(this, "Nomor telepon berhasil diperbarui", Toast.LENGTH_SHORT).show()
                finish()
            }
        }
        val btnBack = findViewById<ImageView>(R.id.btnBack)
        btnBack.setOnClickListener {
            finish()
        }
    }
}
