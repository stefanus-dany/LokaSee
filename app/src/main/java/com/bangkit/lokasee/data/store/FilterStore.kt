package com.bangkit.lokasee.data.store

const val PRICE_MIN = "price_min"
const val PRICE_MAX = "price_max"
const val AREA_MIN = "area_min"
const val AREA_MAX = "area_max"
//const val TYPE = "type"
const val PROVINSI = "provinsi"
const val KABUPATEN = "kabupaten"
const val KECAMATAN = "kecamatan"

object FilterStore {
    private var currentFilter : HashMap<String, Any?> = HashMap()

    init {
        currentFilter[PRICE_MIN] = null
        currentFilter[PRICE_MAX] = null
        currentFilter[AREA_MIN] = null
        currentFilter[AREA_MAX] = null
        //currentFilter[TYPE] = null
        currentFilter[PROVINSI] = null
        currentFilter[KABUPATEN] = null
        currentFilter[KECAMATAN] = null
    }

    fun setFilter(key: String, value: Any?){
        currentFilter[key] = value
    }

    fun getFiter() : HashMap<String, Any?> = currentFilter
}