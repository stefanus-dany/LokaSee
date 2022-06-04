package com.bangkit.lokasee.data

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class Filter (
    var landtype: Int = -1,
    var priceMin: Int = -1,
    var priceMax: Int = -1,
    var areaMin: Int = -1,
    var areaMax: Int = -1,
    var distance: Int = -1,
    var idProvinsi: Int = -1,
    var idKota: Int = -1,
    var idKecamatan: Int = -1
) : Parcelable