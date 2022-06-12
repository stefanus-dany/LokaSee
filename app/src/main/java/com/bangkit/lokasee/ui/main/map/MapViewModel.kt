package com.bangkit.lokasee.ui.main.map

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.bangkit.lokasee.data.Post
import com.bangkit.lokasee.data.repo.Repository
import com.bangkit.lokasee.data.response.PostListResponse

class MapViewModel(private val repository: Repository) : ViewModel() {

    fun getAllPostsFiltered() = repository.getAllPostsFiltered()

    //LOCATION
    fun getAllProvinsi() = repository.getAllProvinsi()
    fun getKabupatenByProvinsi(provinsiId: Int) = repository.getKabupatenByProvinsi(provinsiId)
    fun getKecamatanByKabupaten(kabupatenId: Int) = repository.getKecamatanByKabupaten(kabupatenId)
}