package com.example.mapmidtermproject.utils

import com.example.mapmidtermproject.models.UserDoc
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions

object UserRepository {
    private val db = FirebaseFirestore.getInstance()

    fun upsertUser(u: FirebaseUser) {
        val now = System.currentTimeMillis()
        val doc = hashMapOf(
            "uid" to u.uid,
            "name" to (u.displayName ?: ""),
            "email" to u.email,
            "phone" to null,
            "photoUrl" to (u.photoUrl?.toString()),
            "updatedAt" to now
        )
        db.collection("users").document(u.uid).get().addOnSuccessListener {
            if (!it.exists()) doc["createdAt"] = now
            db.collection("users").document(u.uid).set(doc, SetOptions.merge())
        }.addOnFailureListener {
            db.collection("users").document(u.uid).set(doc, SetOptions.merge())
        }
    }

    fun observeUser(uid: String, onChange: (UserDoc?) -> Unit) =
        db.collection("users").document(uid).addSnapshotListener { s, _ ->
            onChange(s?.toObject(UserDoc::class.java))
        }

    fun updateUserFields(uid: String, fields: Map<String, Any>): Task<Void> {
        val map = HashMap(fields)
        map["updatedAt"] = System.currentTimeMillis()
        return db.collection("users").document(uid).update(map as Map<String, Any>)
    }

    fun deleteUser(uid: String): Task<Void> =
        db.collection("users").document(uid).delete()
}
