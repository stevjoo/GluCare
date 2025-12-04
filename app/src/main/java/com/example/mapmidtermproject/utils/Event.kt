package com.example.mapmidtermproject.utils

/**
 * Wrapper untuk data LiveData yang hanya boleh dikonsumsi satu kali (Single Live Event).
 * Mencegah dialog/notifikasi muncul berulang saat rotasi layar.
 */
open class Event<out T>(private val content: T) {

    var hasBeenHandled = false
        private set // Hanya boleh diubah di dalam class ini

    /**
     * Mengembalikan konten jika belum pernah ditangani (handled).
     * Jika sudah pernah, mengembalikan null.
     */
    fun getContentIfNotHandled(): T? {
        return if (hasBeenHandled) {
            null
        } else {
            hasBeenHandled = true
            content
        }
    }

    /**
     * Mengintip konten tanpa mengubah status handled.
     */
    fun peekContent(): T = content
}