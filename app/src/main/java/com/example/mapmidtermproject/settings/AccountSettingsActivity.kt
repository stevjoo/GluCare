package com.example.mapmidtermproject.settings

import android.content.Intent
import android.os.Bundle
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.example.mapmidtermproject.R
import com.example.mapmidtermproject.activities.LoginActivity
import com.example.mapmidtermproject.utils.UserRepository
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.material.button.MaterialButton
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase

class AccountSettingsActivity : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth
    private lateinit var googleClient: GoogleSignInClient

    private val reauthLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        val u = auth.currentUser ?: return@registerForActivityResult
        if (result.resultCode == RESULT_OK) {
            val task = GoogleSignIn.getSignedInAccountFromIntent(result.data)
            runCatching { task.result }.onSuccess { acct ->
                if (acct?.idToken != null) {
                    val cred = GoogleAuthProvider.getCredential(acct.idToken, null)
                    u.reauthenticate(cred)
                        .addOnSuccessListener { proceedDelete(u.uid) }
                        .addOnFailureListener { toast("Reauth gagal") }
                } else toast("Gagal ambil token Google")
            }.onFailure { toast("Reauth dibatalkan") }
        } else toast("Reauth dibatalkan")
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_account_settings)

        auth = Firebase.auth
        googleClient = GoogleSignIn.getClient(
            this,
            GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .requestIdToken(getString(R.string.default_web_client_id))
                .build()
        )

        findViewById<ImageView>(R.id.btnBack).setOnClickListener { finish() }
        findViewById<LinearLayout>(R.id.btnChangeUsername).setOnClickListener {
            startActivity(Intent(this, ChangeUsernameActivity::class.java))
        }
        findViewById<LinearLayout>(R.id.btnChangePhone).setOnClickListener {
            startActivity(Intent(this, ChangePhoneActivity::class.java))
        }
        findViewById<MaterialButton>(R.id.btnDeleteAccount).setOnClickListener {
            val u = auth.currentUser ?: return@setOnClickListener toast("Tidak ada pengguna masuk")
            AlertDialog.Builder(this)
                .setTitle("Hapus Akun?")
                .setMessage("Akun dan data profil akan dihapus permanen.")
                .setPositiveButton("Hapus") { _, _ -> reauthThenDelete() }
                .setNegativeButton("Batal", null)
                .show()
        }
    }

    private fun reauthThenDelete() {
        val u = auth.currentUser ?: return
        val last = GoogleSignIn.getLastSignedInAccount(this)
        if (last?.idToken != null) {
            val cred = GoogleAuthProvider.getCredential(last.idToken, null)
            u.reauthenticate(cred)
                .addOnSuccessListener { proceedDelete(u.uid) }
                .addOnFailureListener { silentOrInteractiveReauth() }
        } else silentOrInteractiveReauth()
    }

    private fun silentOrInteractiveReauth() {
        googleClient.silentSignIn().addOnCompleteListener { task ->
            val acct: GoogleSignInAccount? = if (task.isSuccessful) task.result else null
            val u = auth.currentUser
            if (u != null && acct?.idToken != null) {
                val cred = GoogleAuthProvider.getCredential(acct.idToken, null)
                u.reauthenticate(cred)
                    .addOnSuccessListener { proceedDelete(u.uid) }
                    .addOnFailureListener { launchInteractiveReauth() }
            } else launchInteractiveReauth()
        }
    }

    private fun launchInteractiveReauth() {
        reauthLauncher.launch(googleClient.signInIntent)
    }

    private fun proceedDelete(uid: String) {
        UserRepository.deleteUser(uid).addOnCompleteListener {
            val u = auth.currentUser
            if (u == null) {
                forceToLogin()
                return@addOnCompleteListener
            }
            u.delete().addOnCompleteListener { del ->
                if (del.isSuccessful) {
                    toast("Akun dihapus")
                    forceToLogin()
                } else {
                    // Jika gagal hapus akun, tetap paksa logout ke Login agar user tidak “nyangkut”
                    toast("Gagal hapus akun. Anda akan keluar.")
                    forceToLogin()
                }
            }
        }
    }

    private fun forceToLogin() {
        // signOut + revokeAccess lalu clear task dan buka LoginActivity
        val basicClient = GoogleSignIn.getClient(this, GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN).build())
        auth.signOut()
        basicClient.signOut().addOnCompleteListener {
            basicClient.revokeAccess().addOnCompleteListener {
                val i = Intent(this, LoginActivity::class.java)
                i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
                startActivity(i)
                finishAffinity()
            }
        }
    }

    private fun toast(msg: String) = Toast.makeText(this, msg, Toast.LENGTH_SHORT).show()
}
