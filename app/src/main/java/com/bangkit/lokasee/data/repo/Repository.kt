package com.bangkit.lokasee.data.repo

import androidx.lifecycle.LiveData
import androidx.lifecycle.liveData
import com.bangkit.lokasee.data.AppPreferences
import com.bangkit.lokasee.data.Result
import com.bangkit.lokasee.data.body.BodyLogin
import com.bangkit.lokasee.data.body.BodyRegister
import com.bangkit.lokasee.data.response.*
import com.bangkit.lokasee.data.retrofit.ApiService
import com.bangkit.lokasee.data.store.FilterStore
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.http.QueryMap

class Repository(private val apiService: ApiService, private val pref: AppPreferences) {
    // Auth Repo
    fun register(
        name: String,
        email: String,
        phoneNumber: String,
        password: String
    ): LiveData<Result<RegisterResponse>> = liveData {
        emit(Result.Loading)
        try {
            val response = apiService.register(BodyRegister(name, email, phoneNumber, password))
            emit(Result.Success(response))
        } catch (e: Exception) {
            emit(Result.Error(e.message.toString()))
        }
    }

    fun login(email: String, password: String): LiveData<Result<LoginResponse>> = liveData {
        emit(Result.Loading)
        try {
            val response = apiService.login(BodyLogin(email, password))
            emit(Result.Success(response))
        } catch (e: Exception) {
            emit(Result.Error(e.message.toString()))
        }
    }

    fun logout(): LiveData<Result<LogoutResponse>> = liveData {
        emit(Result.Loading)
        try {
            val response = apiService.logout()
            emit(Result.Success(response))
        } catch (e: Exception) {
            emit(Result.Error(e.message.toString()))
        }
    }

    fun saveUser(
        userId: Int,
        userName: String,
        email: String,
        phoneNumber: String,
        avatarUrl: String,
        token: String
    ) {
        CoroutineScope(Dispatchers.IO).launch {
            pref.saveUserLogin(userId, userName, email, phoneNumber, avatarUrl, token)
        }
    }

    fun deleteUser( ) {
        CoroutineScope(Dispatchers.IO).launch {
            pref.deleteUserLogin()
        }
    }

    // Post Repo
    fun getAllPosts(): LiveData<Result<PostListResponse>> = liveData {
        emit(Result.Loading)
        try {
            val response = apiService.getAllPosts()
            emit(Result.Success(response))
        } catch (e: Exception) {
            emit(Result.Error(e.message.toString()))
        }
    }

    fun getAllPostsFiltered(): LiveData<Result<PostListResponse>> = liveData {
        emit(Result.Loading)
        try {
            val response = apiService.getAllPostsFiltered(FilterStore.getFiter())
            emit(Result.Success(response))
        } catch (e: Exception) {
            emit(Result.Error(e.message.toString()))
        }
    }

    fun getPost(id: Int): LiveData<Result<PostGetCreateUpdateDeleteResponse>> = liveData {
        emit(Result.Loading)
        try {
            val response = apiService.getPost(id)
            emit(Result.Success(response))
        } catch (e: Exception) {
            emit(Result.Error(e.message.toString()))
        }
    }

    fun deletePost(id: Int): LiveData<Result<PostGetCreateUpdateDeleteResponse>> = liveData {
        emit(Result.Loading)
        try {
            val response = apiService.deletePost(id)
            emit(Result.Success(response))
        } catch (e: Exception) {
            emit(Result.Error(e.message.toString()))
        }
    }

    fun createPost(params: Map<String, RequestBody>, images: Array<MultipartBody.Part>): LiveData<Result<PostGetCreateUpdateDeleteResponse>> = liveData {
        emit(Result.Loading)
        try {
            val response = apiService.testCreatePost(params, images)
            emit(Result.Success(response))
        } catch (e: Exception) {
            emit(Result.Error(e.message.toString()))
        }
    }
//    fun getAllMarkerMaps(token: String, location: Int): LiveData<Result<StoryResponse>> = liveData {
//        emit(Result.Loading)
//        try {
//            val response = apiService.getAllMarkerMaps(token, location)
//            emit(Result.Success(response))
//        } catch (e: Exception) {
//            Log.e(TAG, "onFailure: ${e.message.toString()}")
//            emit(Result.Error(e.message.toString()))
//        }
//    }
//
//    fun uploadStory(
//        token: String,
//        file: MultipartBody.Part,
//        description: RequestBody,
//        lat: RequestBody,
//        lon: RequestBody
//    ): LiveData<Result<FileUploadResponse>> = liveData {
//        emit(Result.Loading)
//        try {
//            val response = apiService.uploadImage(token, file, description, lat, lon)
//            emit(Result.Success(response))
//        } catch (e: Exception) {
//            Log.e(TAG, "onFailure: ${e.message.toString()}")
//            emit(Result.Error(e.message.toString()))
//        }
//    }

    companion object {
        @Volatile
        private var instance: Repository? = null
        fun getInstance(
            apiService: ApiService,
            pref: AppPreferences
        ): Repository =
            instance ?: synchronized(this) {
                instance ?: Repository(apiService, pref)
            }.also { instance = it }
    }
}