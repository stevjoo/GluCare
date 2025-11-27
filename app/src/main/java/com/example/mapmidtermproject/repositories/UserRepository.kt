package com.example.mapmidtermproject.repositories

import com.example.mapmidtermproject.utils.FirestoreHelper
import com.example.mapmidtermproject.utils.UserProfile

class UserRepository {

    fun initUserIfNew() {
        FirestoreHelper.initUserDataIfNew()
    }

    fun getUserProfile(onResult: (UserProfile?) -> Unit) {
        FirestoreHelper.getUserProfile(onResult)
    }

    fun updateUsername(username: String, onSuccess: () -> Unit) {
        FirestoreHelper.updateUsername(username, onSuccess)
    }

    fun updatePhone(phone: String, onSuccess: () -> Unit) {
        FirestoreHelper.updatePhone(phone, onSuccess)
    }

    fun deleteAccount(onSuccess: () -> Unit, onReauthRequired: () -> Unit, onFailure: (Exception) -> Unit) {
        FirestoreHelper.deleteAccount(onSuccess, onReauthRequired, onFailure)
    }
}