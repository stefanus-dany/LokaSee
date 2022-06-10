package com.bangkit.lokasee.ui.main

import androidx.lifecycle.ViewModel
import com.bangkit.lokasee.data.repo.Repository

class MainViewModel(private val repository: Repository) : ViewModel() {
    // POST
    fun getAllPostsFiltered() = repository.getAllPostsFiltered()

    //LOCATION
    fun getAllProvinsi() = repository.getAllProvinsi()
    fun getKabupatenByProvinsi(provinsiId: Int) = repository.getKabupatenByProvinsi(provinsiId)
    fun getKecamatanByKabupaten(kabupatenId: Int) = repository.getKecamatanByKabupaten(kabupatenId)

    // AUTH
    fun logout() = repository.logout()
    fun deleteUser() = repository.deleteUser()
}