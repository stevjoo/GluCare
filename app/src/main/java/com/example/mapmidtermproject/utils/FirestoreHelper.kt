package com.example.mapmidtermproject.utils

import com.google.firebase.auth.FirebaseAuthRecentLoginRequiredException
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.ListenerRegistration
import com.google.firebase.firestore.SetOptions
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import java.util.Date

data class UserProfile(
    val username: String? = null,
    val phone: String? = null,
    val email: String? = null
)

data class FoodLog(
    val foodName: String = "",
    val bloodSugar: Int = 0,
    val timestamp: Date = Date()
)

data class WoundLog(
    val imageUri: String = "",
    val isDiabeticIndication: Boolean = false,
    val timestamp: Date = Date()
)

object FirestoreHelper {
    private val db = Firebase.firestore
    private val auth = Firebase.auth

    // --- LOGIC PROFIL (PERBAIKAN USERNAME) ---
    fun updateUserProfile(username: String?, phone: String?, onSuccess: () -> Unit) {
        val uid = auth.currentUser?.uid ?: return
        val data = hashMapOf<String, Any>()

        // Hanya masukkan data jika tidak null/kosong
        if (!username.isNullOrEmpty()) data["username"] = username
        if (!phone.isNullOrEmpty()) data["phone"] = phone
        auth.currentUser?.email?.let { data["email"] = it }

        db.collection("users").document(uid)
            .set(data, SetOptions.merge()) // Merge agar data lain tidak hilang
            .addOnSuccessListener { onSuccess() }
    }

    fun getUserProfile(onResult: (UserProfile?) -> Unit) {
        val uid = auth.currentUser?.uid ?: return
        db.collection("users").document(uid).get()
            .addOnSuccessListener { document ->
                if (document.exists()) {
                    val profile = document.toObject(UserProfile::class.java)
                    onResult(profile)
                } else {
                    onResult(null) // Beritahu bahwa user belum punya data di DB
                }
            }
            .addOnFailureListener {
                onResult(null)
            }
    }

    // --- LOGIC HAPUS AKUN (ANTI STUCK) ---
    fun deleteAccount(onSuccess: () -> Unit, onReauthRequired: () -> Unit, onFailure: (Exception) -> Unit) {
        val user = auth.currentUser
        val uid = user?.uid ?: return

        // Coba hapus Data Database dulu
        db.collection("users").document(uid).delete()
            .addOnCompleteListener { dbTask ->
                // TIDAK PEDULI dbTask sukses atau gagal, kita LANJUT hapus akun user.
                // Prioritasnya adalah user terhapus dan logout.

                user.delete()
                    .addOnSuccessListener {
                        onSuccess() // Sukses, Auth Guard di MainActivity akan bekerja
                    }
                    .addOnFailureListener { e ->
                        if (e is FirebaseAuthRecentLoginRequiredException) {
                            onReauthRequired()
                        } else {
                            // Jika error lain, tetap coba panggil failure
                            // Tapi biasanya karena network, jadi kita serahkan ke UI handle
                            onFailure(e)
                        }
                    }
            }
    }

    // --- LOGIC LAINNYA (TETAP SAMA) ---
    fun listenToFoodLogs(onResult: (List<FoodLog>) -> Unit): ListenerRegistration? {
        val uid = auth.currentUser?.uid ?: return null
        return db.collection("users").document(uid).collection("food_logs")
            .orderBy("timestamp", com.google.firebase.firestore.Query.Direction.ASCENDING)
            .addSnapshotListener { value, _ ->
                val logs = value?.toObjects(FoodLog::class.java) ?: emptyList()
                onResult(logs)
            }
    }

    fun saveFoodLog(food: String, sugar: Int, onSuccess: () -> Unit, onFailure: (Exception) -> Unit) {
        val uid = auth.currentUser?.uid ?: return
        val log = FoodLog(food, sugar, Date())
        db.collection("users").document(uid).collection("food_logs")
            .add(log).addOnSuccessListener { onSuccess() }.addOnFailureListener { onFailure(it) }
    }

    fun saveWoundLog(imageUri: String, isDiabetic: Boolean) {
        val uid = auth.currentUser?.uid ?: return
        val log = WoundLog(imageUri, isDiabetic, Date())
        db.collection("users").document(uid).collection("wound_logs").add(log)
    }
}