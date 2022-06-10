package com.bangkit.lokasee.data.retrofit

import com.bangkit.lokasee.data.body.BodyLogin
import com.bangkit.lokasee.data.body.BodyRegister
import com.bangkit.lokasee.data.response.*
import com.bangkit.lokasee.data.store.UserStore.currentUserToken
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.http.*

interface ApiService {
    @GET("post")
    suspend fun getAllPosts(): PostListResponse

    @GET("post/filter")
    suspend fun getAllPostsFiltered(
        @QueryMap options: Map<String, Int?>
    ): PostListResponse

    @GET("post/{id}")
    suspend fun getPost(
        @Path("id") id: Int,
    ): PostGetCreateUpdateDeleteResponse

    @DELETE("post/{id}")
    suspend fun deletePost(
        @Path("id") id: Int,
    ): PostGetCreateUpdateDeleteResponse

    @Multipart
    @POST("post")
    suspend fun testCreatePost(
        @PartMap params: Map<String,RequestBody>,
        @Part images: Array<MultipartBody.Part>,
    ): PostGetCreateUpdateDeleteResponse

    @Multipart
    @POST("post")
    suspend fun createPost(
        @Part("title") title: RequestBody,
        @Part("desc") desc: RequestBody,
        @Part("price") price: RequestBody,
        @Part("area") area: RequestBody,
        @Part("address") address: RequestBody,
        @Part("latitude") latitude: RequestBody,
        @Part("longitude") longitude: RequestBody,
        @Part("user_id") user_id: RequestBody,
        @Part("provinsi_id") provinsi_id: RequestBody,
        @Part("kabupaten_id") kabupaten_id: RequestBody,
        @Part("kecamatan_id") kecamatan_id: RequestBody,
        @Part images: Array<MultipartBody.Part>,
    ): PostGetCreateUpdateDeleteResponse

    @Multipart
    @POST("post/{id}")
    suspend fun updatePost(
        @Query("_method") _method: String = "PUT",
        @Path("id") id: Int,
        @Part("title") title: RequestBody,
        @Part("desc") desc: RequestBody,
        @Part("price") price: RequestBody,
        @Part("area") area: RequestBody,
        @Part("address") address: RequestBody,
        @Part("latitude") latitude: RequestBody,
        @Part("longitude") longitude: RequestBody,
        @Part("user_id") user_id: RequestBody,
        @Part("provinsi_id") provinsi_id: RequestBody,
        @Part("kabupaten_id") kabupaten_id: RequestBody,
        @Part("kecamatan_id") kecamatan_id: RequestBody,
        @Part images: Array<MultipartBody.Part>,
    ): PostGetCreateUpdateDeleteResponse

    @GET("location/provinsi")
    suspend fun getAllProvinsi(): ProvinsiListResponse

    @GET("location/kabupaten")
    suspend fun getKabupatenByProvinsi(
        @Query("provinsi") provinsi: Int,
    ): KabupatenListResponse

    @GET("location/kecamatan")
    suspend fun getKecamatanByKabupaten(
        @Query("kabupaten") kabupaten: Int,
    ): KecamatanListResponse

    @POST("login")
    suspend fun login(@Body params: BodyLogin): LoginResponse

    @POST("register")
    suspend fun register(@Body params: BodyRegister): RegisterResponse

    @POST("logout")
    suspend fun logout(): LogoutResponse
}