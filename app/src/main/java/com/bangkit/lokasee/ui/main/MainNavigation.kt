package com.bangkit.lokasee.ui.main

import androidx.annotation.DrawableRes
import androidx.annotation.StringRes

/**
 * An object which represents a search suggestion.
 */
data class MainNavigationModel(
    val id: Int,
    @DrawableRes val icon: Int,
    @StringRes val titleRes: Int,
    val checked: Boolean
)

object MainNavigation{
    const val HOME_ID = 0
    const val FAVORITE_ID = 1
    const val MANAGE_ID = 2
    const val LOGOUT_ID = 3
}