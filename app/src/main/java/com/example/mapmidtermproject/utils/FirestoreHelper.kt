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

// Menambahkan ID agar bisa dihapus
data class FoodLog(
    var id: String = "",
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

    // PENTING: Panggil ini di LoginActivity saat sukses login
    // Mencegah data lama (No HP) tertimpa oleh data baru yang kosong
    fun initUserDataIfNew() {
        val user = auth.currentUser ?: return
        val uid = user.uid
        val docRef = db.collection("users").document(uid)

        docRef.get().addOnSuccessListener { document ->
            if (!document.exists()) {
                Log.d("Firestore", "User baru, inisialisasi data...")
                val newData = hashMapOf(
                    "username" to (user.displayName ?: "Pengguna"),
                    "email" to (user.email ?: ""),
                    "phone" to "" // Default string kosong agar tidak null
                )
                // Gunakan merge agar aman
                docRef.set(newData, SetOptions.merge())
            } else {
                Log.d("Firestore", "User lama, skip inisialisasi (Data aman).")
            }
        }.addOnFailureListener { e ->
            Log.e("Firestore", "Gagal cek user init: $e")
        }
    }

    // Update Username (Aman: Tidak menghapus Phone)
    fun updateUsername(newUsername: String, onSuccess: () -> Unit) {
        val uid = auth.currentUser?.uid ?: return
        val data = hashMapOf<String, Any>("username" to newUsername)
        auth.currentUser?.email?.let { data["email"] = it }

        db.collection("users").document(uid)
            .set(data, SetOptions.merge())
            .addOnSuccessListener { onSuccess() }
            .addOnFailureListener { Log.e("Firestore", "Gagal update username: $it") }
    }

    // Update Phone (Aman: Tidak menghapus Username)
    fun updatePhone(newPhone: String, onSuccess: () -> Unit) {
        val uid = auth.currentUser?.uid ?: return
        // Pastikan tidak null
        val validPhone = if (newPhone.isBlank()) "" else newPhone

        val data = hashMapOf<String, Any>("phone" to validPhone)
        auth.currentUser?.email?.let { data["email"] = it }

        db.collection("users").document(uid)
            .set(data, SetOptions.merge())
            .addOnSuccessListener { onSuccess() }
            .addOnFailureListener { Log.e("Firestore", "Gagal update phone: $it") }
    }

    // Ambil Profil (Realtime Listener)
    fun getUserProfile(onResult: (UserProfile?) -> Unit) {
        val uid = auth.currentUser?.uid ?: return

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

    // --- FOOD LOG (CRUD) ---

    // 1. CREATE (Simpan Log)
    fun saveFoodLog(food: String, sugar: Int, onSuccess: () -> Unit, onFailure: (Exception) -> Unit) {
        val uid = auth.currentUser?.uid ?: return
        // ID dikosongkan dulu, Firebase akan generate otomatis
        val log = FoodLog(id = "", foodName = food, bloodSugar = sugar, timestamp = Date())

        db.collection("users").document(uid).collection("food_logs")
            .add(log)
            .addOnSuccessListener { onSuccess() }
            .addOnFailureListener { onFailure(it) }
    }

    // 2. READ (Ambil Data Realtime)
    fun listenToFoodLogs(onResult: (List<FoodLog>) -> Unit): ListenerRegistration? {
        val uid = auth.currentUser?.uid ?: return null
        return db.collection("users").document(uid).collection("food_logs")
            .orderBy("timestamp", com.google.firebase.firestore.Query.Direction.ASCENDING)
            .addSnapshotListener { value, error ->
                if (error != null) return@addSnapshotListener

                val list = ArrayList<FoodLog>()
                for (doc in value!!) {
                    val log = doc.toObject(FoodLog::class.java)
                    log.id = doc.id // PENTING: Simpan ID dokumen agar bisa dihapus
                    list.add(log)
                }
                onResult(list)
            }
    }

    // 3. DELETE (Hapus Log Tertentu)
    fun deleteFoodLog(logId: String, onSuccess: () -> Unit, onFailure: (Exception) -> Unit) {
        val uid = auth.currentUser?.uid ?: return
        db.collection("users").document(uid).collection("food_logs").document(logId)
            .delete()
            .addOnSuccessListener { onSuccess() }
            .addOnFailureListener { onFailure(it) }
    }

    // --- WOUND LOG (Database Online - Opsional jika pakai lokal) ---
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

    // --- HAPUS AKUN (COMPLETE CLEANUP) ---
    fun deleteAccount(onSuccess: () -> Unit, onReauthRequired: () -> Unit, onFailure: (Exception) -> Unit) {
        val user = auth.currentUser
        val uid = user?.uid ?: return

        // 1. Hapus Sub-koleksi Food Logs
        deleteSubCollection("users/$uid/food_logs") {
            // 2. Hapus Sub-koleksi Wound Logs
            deleteSubCollection("users/$uid/wound_logs") {
                // 3. Hapus Dokumen User Utama
                db.collection("users").document(uid).delete().addOnCompleteListener {
                    // 4. Hapus Akun Autentikasi (Login)
                    user.delete()
                        .addOnSuccessListener { onSuccess() }
                        .addOnFailureListener { e ->
                            if (e is FirebaseAuthRecentLoginRequiredException) {
                                onReauthRequired()
                            } else {
                                onFailure(e)
                            }
                        }
                }
            }
        }
    }

    // Helper untuk menghapus koleksi (Batch Limit 500)
    private fun deleteSubCollection(path: String, onComplete: () -> Unit) {
        val collectionRef = db.collection(path)

        // Ambil 500 dokumen (batas batch)
        collectionRef.limit(500).get().addOnSuccessListener { snapshot ->
            if (snapshot.isEmpty) {
                onComplete() // Sudah bersih
                return@addOnSuccessListener
            }

            val batch = db.batch()
            for (doc in snapshot.documents) {
                batch.delete(doc.reference)
            }

            batch.commit()
                .addOnSuccessListener {
                    // Panggil lagi (rekursif) jika masih ada sisa data
                    deleteSubCollection(path, onComplete)
                }
                .addOnFailureListener {
                    Log.e("Firestore", "Gagal hapus batch subcollection: $it")
                    onComplete() // Tetap lanjut agar tidak stuck
                }
        }.addOnFailureListener { onComplete() }
    }
}