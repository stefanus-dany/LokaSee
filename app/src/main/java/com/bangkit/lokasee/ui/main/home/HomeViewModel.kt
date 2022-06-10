package com.bangkit.lokasee.ui.main.home

import androidx.lifecycle.ViewModel
import com.bangkit.lokasee.data.repo.Repository

class HomeViewModel(private val repository: Repository) : ViewModel() {
    // POST
    fun getAllPostsFiltered() = repository.getAllPostsFiltered()

    //LOCATION
    fun getAllProvinsi() = repository.getAllProvinsi()
    fun getKabupatenByProvinsi(provinsiId: Int) = repository.getKabupatenByProvinsi(provinsiId)
    fun getKecamatanByKabupaten(kabupatenId: Int) = repository.getKecamatanByKabupaten(kabupatenId)
}