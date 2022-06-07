package com.bangkit.lokasee.util.retrofit

import com.bangkit.lokasee.data.response.*
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.http.*

interface ApiService {
    @Multipart
    @POST("post")
    fun uploadPost(
        @Part file: MultipartBody.Part,
        @Part("description") description: RequestBody,
        @Part("lat") lat: RequestBody?,
        @Part("lon") lon: RequestBody?,
    ): Call<PostListResponse>

    @POST("/login")
    fun login(@Body params: RequestBody): Call<LoginResponse>

    @POST("/register")
    fun register(@Body params: RequestBody): Call<RegisterResponse>
}