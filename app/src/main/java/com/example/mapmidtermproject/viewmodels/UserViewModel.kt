package com.example.mapmidtermproject.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.mapmidtermproject.repositories.UserRepository
import com.example.mapmidtermproject.utils.UserProfile

class UserViewModel : ViewModel() {
    private val repository = UserRepository()

    private val _userProfile = MutableLiveData<UserProfile?>()
    val userProfile: LiveData<UserProfile?> = _userProfile

    fun loadProfile() {
        repository.getUserProfile { profile ->
            _userProfile.value = profile
        }
    }

    fun updateUsername(name: String, onSuccess: () -> Unit) {
        repository.updateUsername(name, onSuccess)
    }

    fun updatePhone(phone: String, onSuccess: () -> Unit) {
        repository.updatePhone(phone, onSuccess)
    }

    fun deleteAccount(onSuccess: () -> Unit, onReauth: () -> Unit, onFailure: (String) -> Unit) {
        repository.deleteAccount(onSuccess, onReauth) { e ->
            onFailure(e.message ?: "Error")
        }
    }
}