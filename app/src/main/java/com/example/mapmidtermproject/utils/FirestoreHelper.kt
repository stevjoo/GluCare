package com.example.mapmidtermproject.utils

import android.util.Log
import com.example.mapmidtermproject.data.NewsData
import com.example.mapmidtermproject.models.NewsArticle
import com.google.firebase.auth.FirebaseAuthRecentLoginRequiredException
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.ListenerRegistration
import com.google.firebase.firestore.SetOptions
import com.google.firebase.firestore.Source
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import java.util.Date

// --- DATA CLASSES ---
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

    // --- PROFIL USER ---

    // PENTING: Panggil ini di LoginActivity/MainActivity agar data tidak hilang/tertimpa
    fun initUserDataIfNew() {
        val user = auth.currentUser ?: return
        val uid = user.uid
        val docRef = db.collection("users").document(uid)

        docRef.get().addOnSuccessListener { document ->
            if (!document.exists()) {
                // Hanya buat data baru jika belum ada
                val newData = hashMapOf(
                    "username" to (user.displayName ?: "Pengguna"),
                    "email" to (user.email ?: ""),
                    "phone" to "" // Default kosong, jangan null
                )
                docRef.set(newData, SetOptions.merge())
            }
        }.addOnFailureListener { e ->
            Log.e("Firestore", "Gagal cek user init: $e")
        }
    }

    // Update Username (Aman: Menggunakan Merge)
    fun updateUsername(newUsername: String, onSuccess: () -> Unit) {
        val uid = auth.currentUser?.uid ?: return
        val data = hashMapOf<String, Any>("username" to newUsername)
        auth.currentUser?.email?.let { data["email"] = it }

        db.collection("users").document(uid)
            .set(data, SetOptions.merge())
            .addOnSuccessListener { onSuccess() }
            .addOnFailureListener { Log.e("Firestore", "Gagal update username: $it") }
    }

    // Update Phone (Aman: Menggunakan Merge)
    fun updatePhone(newPhone: String, onSuccess: () -> Unit) {
        val uid = auth.currentUser?.uid ?: return
        // Pastikan newPhone dikirim sebagai String yang valid
        val validPhone = if (newPhone.isBlank()) "" else newPhone

        val data = hashMapOf<String, Any>("phone" to validPhone)
        auth.currentUser?.email?.let { data["email"] = it }

        db.collection("users").document(uid)
            .set(data, SetOptions.merge())
            .addOnSuccessListener {
                Log.d("Firestore", "Sukses update phone ke: $validPhone")
                onSuccess()
            }
            .addOnFailureListener { Log.e("Firestore", "Gagal update phone: $it") }
    }

    // Ambil Profil dengan Prioritas Server
    fun getUserProfile(onResult: (UserProfile?) -> Unit) {
        val uid = auth.currentUser?.uid ?: return

        // Menggunakan addSnapshotListener agar update secara realtime (lebih cepat dan akurat)
        // Ini mengatasi masalah data "telat" muncul
        db.collection("users").document(uid).addSnapshotListener { document, error ->
            if (error != null) {
                Log.e("Firestore", "Error listen profile: $error")
                onResult(null)
                return@addSnapshotListener
            }

            if (document != null && document.exists()) {
                val username = document.getString("username")
                val phone = document.getString("phone")
                val email = document.getString("email")
                onResult(UserProfile(username, phone, email))
            } else {
                onResult(null)
            }
        }
    }

    // --- FOOD LOG ---
    fun saveFoodLog(food: String, sugar: Int, onSuccess: () -> Unit, onFailure: (Exception) -> Unit) {
        val uid = auth.currentUser?.uid ?: return
        val log = FoodLog(food, sugar, Date())
        db.collection("users").document(uid).collection("food_logs")
            .add(log).addOnSuccessListener { onSuccess() }.addOnFailureListener { onFailure(it) }
    }

    fun listenToFoodLogs(onResult: (List<FoodLog>) -> Unit): ListenerRegistration? {
        val uid = auth.currentUser?.uid ?: return null
        return db.collection("users").document(uid).collection("food_logs")
            .orderBy("timestamp", com.google.firebase.firestore.Query.Direction.ASCENDING)
            .addSnapshotListener { v, e ->
                if (e == null) onResult(v?.toObjects(FoodLog::class.java) ?: emptyList())
            }
    }

    // --- WOUND LOG ---
    fun saveWoundLog(imageUri: String, isDiabetic: Boolean) {
        val uid = auth.currentUser?.uid ?: return
        val log = WoundLog(imageUri, isDiabetic, Date())
        db.collection("users").document(uid).collection("wound_logs").add(log)
    }

    // --- BERITA ---
    fun getNews(onResult: (List<NewsArticle>) -> Unit) {
        db.collection("news").orderBy("id").get()
            .addOnSuccessListener { result ->
                if (result.isEmpty) {
                    val local = NewsData.getNewsList()
                    uploadDummyNews(local)
                    onResult(local)
                } else {
                    onResult(result.toObjects(NewsArticle::class.java))
                }
            }
            .addOnFailureListener { onResult(NewsData.getNewsList()) }
    }

    private fun uploadDummyNews(list: List<NewsArticle>) {
        val batch = db.batch()
        list.forEach { batch.set(db.collection("news").document(), it) }
        batch.commit()
    }

    // --- HAPUS AKUN (SOLUSI MASALAH 2) ---
    fun deleteAccount(onSuccess: () -> Unit, onReauthRequired: () -> Unit, onFailure: (Exception) -> Unit) {
        val user = auth.currentUser
        val uid = user?.uid ?: return

        // 1. Hapus Food Logs
        deleteSubCollection("users/$uid/food_logs") {
            // 2. Hapus Wound Logs
            deleteSubCollection("users/$uid/wound_logs") {
                // 3. Hapus Dokumen User
                db.collection("users").document(uid).delete().addOnCompleteListener {
                    // 4. Hapus User Auth
                    user.delete()
                        .addOnSuccessListener { onSuccess() }
                        .addOnFailureListener { e ->
                            if (e is FirebaseAuthRecentLoginRequiredException) onReauthRequired() else onFailure(e)
                        }
                }
            }
        }
    }

    // Helper Hapus Subcollection yang lebih kuat
    private fun deleteSubCollection(path: String, onComplete: () -> Unit) {
        val collectionRef = db.collection(path)
        collectionRef.get().addOnSuccessListener { snapshot ->
            if (snapshot.isEmpty) {
                onComplete()
                return@addOnSuccessListener
            }

            val batch = db.batch()
            // Loop semua dokumen
            for (doc in snapshot.documents) {
                batch.delete(doc.reference)
            }

            batch.commit()
                .addOnSuccessListener {
                    // Cek lagi apakah masih ada sisa (karena limit batch 500)
                    // Panggil rekursif jika masih ada
                    deleteSubCollection(path, onComplete)
                }
                .addOnFailureListener {
                    Log.e("Firestore", "Gagal hapus batch: $it")
                    onComplete() // Lanjut saja agar tidak stuck
                }
        }.addOnFailureListener { onComplete() }
    }
}