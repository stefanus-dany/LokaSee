package com.bangkit.lokasee.ui.auth.login

import androidx.lifecycle.ViewModel
import com.bangkit.lokasee.data.repo.Repository

class LoginViewModel(private val repository: Repository) : ViewModel() {

    fun login(email: String, password: String) = repository.login(email, password)

    fun saveUser(
        userId: Int,
        userName: String,
        email: String,
        phoneNumber: String,
        address: String,
        avatarUrl: String,
        token: String
    ) {
        repository.saveAuthUser(userId, userName, email, phoneNumber, address, avatarUrl, token)
    }
}