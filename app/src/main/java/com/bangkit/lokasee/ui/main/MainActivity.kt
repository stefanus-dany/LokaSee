package com.bangkit.lokasee.ui.main

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.os.Bundle
import android.view.MenuItem
import android.view.View
import androidx.annotation.MenuRes
import androidx.annotation.StringRes
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.fragment.app.Fragment
import androidx.navigation.NavController
import androidx.navigation.NavDestination
import androidx.navigation.findNavController
import com.bangkit.lokasee.R
import com.bangkit.lokasee.databinding.ActivityMainBinding
import com.bangkit.lokasee.ui.main.home.HomeFragmentDirections
import com.bangkit.lokasee.ui.main.search.SearchFragmentDirections
import com.bangkit.lokasee.util.*
import com.google.android.material.transition.MaterialFadeThrough
import com.google.android.material.transition.MaterialSharedAxis

class MainActivity : AppCompatActivity(), Toolbar.OnMenuItemClickListener, NavController.OnDestinationChangedListener {

    private lateinit var binding: ActivityMainBinding
    private lateinit var bottomNavDrawer: BottomNavDrawerFragment

    private var currentEmailId = -1L

    val currentNavigationFragment: Fragment?
        get() = supportFragmentManager.findFragmentById(R.id.nav_host_fragment)
            ?.childFragmentManager
            ?.fragments
            ?.first()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_LAYOUT_STABLE or
                View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN or
                View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION

        setContentView(binding.root)
        bottomNavDrawer = supportFragmentManager.findFragmentById(R.id.bottom_nav_drawer) as BottomNavDrawerFragment
        setUpBottomNavigationAndFab()
    }

    private fun setUpBottomNavigationAndFab() {
        // Wrap binding.run to ensure ContentViewBindingDelegate is calling this Activity's
        // setContentView before accessing views
        binding.run {
            findNavController(R.id.nav_host_fragment).addOnDestinationChangedListener(
                this@MainActivity
            )
        }

        // Set a custom animation for showing and hiding the FAB
        binding.fabMain.apply {
            setShowMotionSpecResource(R.animator.fab_show)
            setHideMotionSpecResource(R.animator.fab_hide)
            setOnClickListener {
            }
        }

        bottomNavDrawer.apply {
            addOnSlideAction(HalfClockwiseRotateSlideAction(binding.bottomAppBarChevron))
            addOnSlideAction(AlphaSlideAction(binding.bottomAppBarTitle, true))
            addOnStateChangedAction(ShowHideFabStateAction(binding.fabMain))
            addOnStateChangedAction(ChangeSettingsMenuStateAction { showSettings ->
                // Toggle between the current destination's BAB menu and the menu which should
                // be displayed when the BottomNavigationDrawer is open.
                binding.bottomAppBar.replaceMenu(if (showSettings) {
                    R.menu.bottom_app_bar_settings_menu
                } else {
                    getBottomAppBarMenuForDestination()
                })
            })

            addOnSandwichSlideAction(HalfCounterClockwiseRotateSlideAction(binding.bottomAppBarChevron))
        }

        // Set up the BottomAppBar menu
        binding.bottomAppBar.apply {
            setNavigationOnClickListener {
                bottomNavDrawer.toggle()
            }
            setOnMenuItemClickListener(this@MainActivity)
        }

        // Set up the BottomNavigationDrawer's open/close affordance
        binding.bottomAppBarContentContainer.setOnClickListener {
            bottomNavDrawer.toggle()
        }
    }

    override fun onDestinationChanged(
        controller: NavController,
        destination: NavDestination,
        arguments: Bundle?
    ) {
        // Set the currentEmail being viewed so when the FAB is pressed, the correct email
        // reply is created. In a real app, this should be done in a ViewModel but is done
        // here to keep things simple. Here we're also setting the configuration of the
        // BottomAppBar and FAB based on the current destination.
        when (destination.id) {
            R.id.homeFragment -> {
                currentEmailId = -1
                setBottomAppBarForHome(getBottomAppBarMenuForDestination(destination))
            }
            R.id.searchFragment -> {
                currentEmailId = -1
                setBottomAppBarForSearch()
            }
        }
    }

    /**
     * Helper function which returns the menu which should be displayed for the current
     * destination.
     *
     * Used both when the destination has changed, centralizing destination-to-menu mapping, as
     * well as switching between the alternate menu used when the BottomNavigationDrawer is
     * open and closed.
     */
    @MenuRes
    private fun getBottomAppBarMenuForDestination(destination: NavDestination? = null): Int {
        val dest = destination ?: findNavController(R.id.nav_host_fragment).currentDestination
        return when (dest?.id) {
            R.id.homeFragment -> R.menu.bottom_app_bar_home_menu
            else -> R.menu.bottom_app_bar_home_menu
        }
    }

    private fun setBottomAppBarForHome(@MenuRes menuRes: Int) {
        binding.run {
            fabMain.setImageState(intArrayOf(-android.R.attr.state_activated), true)
            bottomAppBar.visibility = View.VISIBLE
            bottomAppBar.replaceMenu(menuRes)
            bottomAppBarTitle.visibility = View.VISIBLE
            bottomAppBar.performShow()
            fabMain.show()
        }
    }

    private fun setBottomAppBarForSearch() {
        hideBottomAppBar()
        binding.fabMain.hide()
    }

    private fun hideBottomAppBar() {
        binding.run {
            bottomAppBar.performHide()
            // Get a handle on the animator that hides the bottom app bar so we can wait to hide
            // the fab and bottom app bar until after it's exit animation finishes.
            bottomAppBar.animate().setListener(object : AnimatorListenerAdapter() {
                var isCanceled = false
                override fun onAnimationEnd(animation: Animator?) {
                    if (isCanceled) return

                    // Hide the BottomAppBar to avoid it showing above the keyboard
                    // when composing a new email.
                    bottomAppBar.visibility = View.GONE
                    fabMain.visibility = View.INVISIBLE
                }
                override fun onAnimationCancel(animation: Animator?) {
                    isCanceled = true
                }
            })
        }
    }

    override fun onMenuItemClick(item: MenuItem?): Boolean {
        when (item?.itemId) {
            R.id.menu_settings -> {
                val directions = HomeFragmentDirections.actionGlobalProfileFragment()
                findNavController(R.id.nav_host_fragment).navigate(directions)
                bottomNavDrawer.close()
            }
            R.id.menu_search -> navigateToSearch()
            R.id.menu_filter -> showFilterModal()
        }
        return true
    }

    fun navigateToHome(@StringRes titleRes: Int) {
        binding.bottomAppBarTitle.text = getString(titleRes)
        currentNavigationFragment?.apply {
            exitTransition = MaterialFadeThrough().apply {
                duration = resources.getInteger(R.integer.lokasee_motion_duration_large).toLong()
            }
        }
        val directions = HomeFragmentDirections.actionGlobalHomeFragment()
        findNavController(R.id.nav_host_fragment).navigate(directions)
    }

    private fun navigateToSearch() {
        currentNavigationFragment?.apply {
            exitTransition = MaterialSharedAxis(MaterialSharedAxis.Z, true).apply {
                duration = resources.getInteger(R.integer.lokasee_motion_duration_large).toLong()
            }
            reenterTransition = MaterialSharedAxis(MaterialSharedAxis.Z, false).apply {
                duration = resources.getInteger(R.integer.lokasee_motion_duration_large).toLong()
            }
        }
        val directions = SearchFragmentDirections.actionGlobalSearchFragment()
        findNavController(R.id.nav_host_fragment).navigate(directions)
    }

    private fun showFilterModal() {
        FilterBottomSheet().show(supportFragmentManager, null)
    }
}
