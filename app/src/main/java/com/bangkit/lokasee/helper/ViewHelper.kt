package com.bangkit.lokasee.helper

import android.view.View

object ViewHelper {

    fun View.visible(){
        this.visibility = View.VISIBLE
    }

    fun View.gone(){
        this.visibility = View.GONE
    }

    fun View.invisible(){
        this.visibility = View.INVISIBLE
    }
}