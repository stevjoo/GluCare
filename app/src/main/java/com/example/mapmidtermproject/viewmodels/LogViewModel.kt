package com.example.mapmidtermproject.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.mapmidtermproject.repositories.FoodRepository
import com.example.mapmidtermproject.utils.FoodLog
import com.google.firebase.firestore.ListenerRegistration

class LogViewModel : ViewModel() {

    private val repository = FoodRepository()

    // Data yang dipantau oleh Activity
    private val _logs = MutableLiveData<List<FoodLog>>()
    val logs: LiveData<List<FoodLog>> = _logs

    private var listener: ListenerRegistration? = null

    // Mulai memantau data (Realtime)
    fun startListening() {
        listener = repository.getFoodLogs { newLogs ->
            _logs.value = newLogs
        }
    }

    // Berhenti memantau
    fun stopListening() {
        listener?.remove()
    }

    // Simpan Data
    fun saveLog(food: String, sugar: Int, onSuccess: () -> Unit, onFailure: (String) -> Unit) {
        repository.addFoodLog(food, sugar, onSuccess) { e ->
            onFailure(e.message ?: "Gagal menyimpan")
        }
    }

    // Hapus Data
    fun deleteLog(logId: String, onSuccess: () -> Unit, onFailure: (String) -> Unit) {
        repository.deleteFoodLog(logId, onSuccess) { e ->
            onFailure(e.message ?: "Gagal menghapus")
        }
    }

    override fun onCleared() {
        super.onCleared()
        stopListening()
    }

    fun updateLog(logId: String, food: String, sugar: Int, onSuccess: () -> Unit, onFailure: (String) -> Unit) {
        // Kita panggil FirestoreHelper langsung atau via Repository (disini langsung ke Helper agar ringkas sesuai pola sebelumnya)
        com.example.mapmidtermproject.utils.FirestoreHelper.updateFoodLog(logId, food, sugar, onSuccess) { e ->
            onFailure(e.message ?: "Gagal update")
        }
    }
}