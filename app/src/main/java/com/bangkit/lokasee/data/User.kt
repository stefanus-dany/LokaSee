package com.bangkit.lokasee.data

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize

@Parcelize
data class User(
	@field:SerializedName("id")
	var id: Int,

	@field:SerializedName("name")
	var name: String,

	@field:SerializedName("email")
	var email: String,

	@field:SerializedName("phone_number")
	var phoneNumber: String,

	@field:SerializedName("address")
	var address: String,

	@field:SerializedName("avatar_url")
	var avatarUrl: String,

	@field:SerializedName("provinsi_id")
	var provinsiId: Int? = null,

	@field:SerializedName("provinsi")
	var provinsi: Provinsi? = null,

	@field:SerializedName("kabupaten_id")
	var kabupatenId: Int? = null,

	@field:SerializedName("kabupaten")
	var kabupaten: Kabupaten? = null,

	@field:SerializedName("kecamatan_id")
	var kecamatanId: Int? = null,

	@field:SerializedName("kecamatan")
	var kecamatan: Kecamatan? = null,

	@field:SerializedName("updated_at")
	var updatedAt: String? = null,

	@field:SerializedName("created_at")
	var createdAt: String? = null,

	@field:SerializedName("email_verified_at")
	var emailVerifiedAt: String? = null,
) : Parcelable