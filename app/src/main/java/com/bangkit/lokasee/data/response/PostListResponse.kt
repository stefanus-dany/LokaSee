package com.bangkit.lokasee.data.response

import android.os.Parcelable
import com.bangkit.lokasee.data.Post
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize

@Parcelize
data class PostListResponse(

	@field:SerializedName("data")
	val data: List<Post?>? = null,

	@field:SerializedName("message")
	val message: String
) : Parcelable