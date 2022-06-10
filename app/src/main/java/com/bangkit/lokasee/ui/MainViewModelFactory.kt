package com.bangkit.lokasee.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.bangkit.lokasee.data.repo.Repository
import com.bangkit.lokasee.ui.main.MainViewModel

class MainViewModelFactory(private val repo: Repository): ViewModelProvider.NewInstanceFactory() {
    override fun <T : ViewModel> create(modelClass: Class<T>): T = MainViewModel(repo) as T
}