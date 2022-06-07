package com.bangkit.lokasee.data.response

import android.os.Parcelable
import com.bangkit.lokasee.data.User
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize

@Parcelize
data class LoginResponse(
	@field:SerializedName("data")
	val data: User? = null,

	@field:SerializedName("access_token")
	val accessToken: String? = null,

	@field:SerializedName("token_type")
	val tokenType: String? = null,

	@field:SerializedName("message")
	val message: String
) : Parcelable