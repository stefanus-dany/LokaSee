package com.bangkit.lokasee.ui.main

import androidx.lifecycle.ViewModel
import com.bangkit.lokasee.data.repo.Repository

class MainViewModel(private val repository: Repository) : ViewModel() {
    // AUTH
    fun logout() = repository.logout()
    fun deleteUser() = repository.deleteAuthUser()
}