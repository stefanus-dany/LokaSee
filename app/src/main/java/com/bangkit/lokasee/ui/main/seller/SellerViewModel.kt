package com.bangkit.lokasee.ui.main.seller

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.bangkit.lokasee.data.Post
import com.bangkit.lokasee.data.repo.Repository
import com.bangkit.lokasee.data.response.PostListResponse
import okhttp3.MultipartBody
import okhttp3.RequestBody

class SellerViewModel(private val repository: Repository) : ViewModel() {
    private val _listPost =  MutableLiveData<Result<PostListResponse>>()
    val listPost: LiveData<Result<PostListResponse>> = _listPost

    // POST
    fun getUserPost() = repository.getUserPosts()
    fun getPost(id: Int) = repository.getPost(id)
    fun createPost(params: Map<String, RequestBody>, images: Array<MultipartBody.Part>) = repository.createPost(params, images)
    fun updatePost(id: Int, params: Map<String, RequestBody>, images: Array<MultipartBody.Part?>) = repository.updatePost(id, params, images)

    //LOCATION
    fun getAllProvinsi() = repository.getAllProvinsi()
    fun getKabupatenByProvinsi(provinsiId: Int) = repository.getKabupatenByProvinsi(provinsiId)
    fun getKecamatanByKabupaten(kabupatenId: Int) = repository.getKecamatanByKabupaten(kabupatenId)
}