package com.example.mapmidtermproject.activities

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import com.example.mapmidtermproject.R
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider

class LoginActivity : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth
    private lateinit var googleClient: GoogleSignInClient

    private val signInLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        val task = GoogleSignIn.getSignedInAccountFromIntent(result.data)
        try {
            val account = task.getResult(ApiException::class.java)
            val idToken = account.idToken
            Log.d("LOGIN", "account=${account.email}, idTokenNull=${idToken.isNullOrEmpty()}")
            if (idToken.isNullOrEmpty()) {
                Toast.makeText(this, "ID Token kosong. Cek default_web_client_id & SHA-1.", Toast.LENGTH_LONG).show()
                return@registerForActivityResult
            }
            val credential = GoogleAuthProvider.getCredential(idToken, null)
            auth.signInWithCredential(credential)
                .addOnSuccessListener {
                    startActivity(Intent(this, MainActivity::class.java))
                    finish()
                }
                .addOnFailureListener { e ->
                    Log.e("LOGIN", "signInWithCredential failure", e)
                    Toast.makeText(this, "Gagal masuk: ${e.localizedMessage}", Toast.LENGTH_LONG).show()
                }
        } catch (e: ApiException) {
            Log.e("LOGIN", "Google sign in failed: code=${e.statusCode}", e)
            Toast.makeText(this, "Google Sign-In gagal (${e.statusCode})", Toast.LENGTH_LONG).show()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        auth = FirebaseAuth.getInstance()

        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.default_web_client_id))
            .requestEmail()
            .build()
        googleClient = GoogleSignIn.getClient(this, gso)

        findViewById<com.google.android.gms.common.SignInButton>(R.id.btnGoogleSignIn)
            .setOnClickListener {
                signInLauncher.launch(googleClient.signInIntent)
            }
    }

    override fun onStart() {
        super.onStart()
        if (auth.currentUser != null) {
            startActivity(Intent(this, MainActivity::class.java))
            finish()
        }
    }
}
