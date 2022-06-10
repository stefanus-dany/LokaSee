package com.bangkit.lokasee.ui.main

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import androidx.annotation.MenuRes
import androidx.annotation.StringRes
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.NavController
import androidx.navigation.NavDestination
import androidx.navigation.findNavController
import cn.pedant.SweetAlert.SweetAlertDialog
import com.bangkit.lokasee.R
import com.bangkit.lokasee.data.Result
import com.bangkit.lokasee.databinding.ActivityMainBinding
import com.bangkit.lokasee.di.Injection
import com.bangkit.lokasee.ui.MainViewModelFactory
import com.bangkit.lokasee.ui.ViewModelFactory
import com.bangkit.lokasee.ui.auth.AuthActivity
import com.bangkit.lokasee.ui.main.home.HomeFragmentDirections
import com.bangkit.lokasee.ui.main.navigation.BottomNavDrawerFragment
import com.bangkit.lokasee.ui.main.navigation.NavigationAdapter
import com.bangkit.lokasee.ui.main.navigation.NavigationModelItem
import com.bangkit.lokasee.ui.main.profile.ProfileFragmentDirections
import com.bangkit.lokasee.ui.main.search.SearchFragmentDirections
import com.bangkit.lokasee.util.*
import com.google.android.material.transition.MaterialFadeThrough
import com.google.android.material.transition.MaterialSharedAxis


class MainActivity : AppCompatActivity(), Toolbar.OnMenuItemClickListener, NavController.OnDestinationChangedListener, NavigationAdapter.NavigationAdapterListener {

    private lateinit var binding: ActivityMainBinding
    private lateinit var bottomNavDrawer: BottomNavDrawerFragment
    private lateinit var mainViewModel: MainViewModel

    val currentNavigationFragment: Fragment?
        get() = supportFragmentManager.findFragmentById(R.id.nav_host_fragment)
            ?.childFragmentManager
            ?.fragments
            ?.first()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setUpViewModel()
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_LAYOUT_STABLE or
                View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN or
                View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
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
            addNavigationListener(this@MainActivity)
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
        when (destination.id) {
            R.id.homeFragment -> {
                setBottomAppBarForHome(getBottomAppBarMenuForDestination(destination))
            }
            R.id.searchFragment -> {
                setBottomAppBarForSearch()
            }
        }
    }

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
            bottomAppBar.animate().setListener(object : AnimatorListenerAdapter() {
                var isCanceled = false
                override fun onAnimationEnd(animation: Animator?) {
                    if (isCanceled) return

                    bottomAppBar.visibility = View.GONE
                    fabMain.visibility = View.INVISIBLE
                }
                override fun onAnimationCancel(animation: Animator?) {
                    isCanceled = true
                }
            })
        }
    }

    override fun onNavMenuItemClicked(item: NavigationModelItem.NavMenuItem) {
        Toast.makeText(this, item.title, Toast.LENGTH_LONG).show()
        when(item.id){
            4 -> logout()
        }
    }

    override fun onMenuItemClick(item: MenuItem?): Boolean {
        when (item?.itemId) {
            R.id.menu_settings -> {
                bottomNavDrawer.close()
                navigateToProfile()
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

    private fun navigateToProfile() {
        currentNavigationFragment?.apply {
            exitTransition = MaterialSharedAxis(MaterialSharedAxis.Z, true).apply {
                duration = resources.getInteger(R.integer.lokasee_motion_duration_large).toLong()
            }
            reenterTransition = MaterialSharedAxis(MaterialSharedAxis.Z, false).apply {
                duration = resources.getInteger(R.integer.lokasee_motion_duration_large).toLong()
            }
        }
        val directions = ProfileFragmentDirections.actionGlobalProfileFragment()
        findNavController(R.id.nav_host_fragment).navigate(directions)
    }

    private fun showFilterModal() {
        FilterBottomSheet.newInstance().show(supportFragmentManager, null)
    }

    private fun setUpViewModel() {
        mainViewModel = ViewModelProvider(this, MainViewModelFactory(Injection.provideRepository(this)))[MainViewModel::class.java]
    }

    private fun logout(){
        val pDialog = SweetAlertDialog(this, SweetAlertDialog.PROGRESS_TYPE)
            pDialog.progressHelper.barColor = Color.parseColor("#A5DC86")
            pDialog.titleText = "Logging Out"
            pDialog.setCancelable(false)

        mainViewModel.logout().observe(this) { result ->
            if (result != null) {
                when (result) {
                    is Result.Loading -> {
                        pDialog.show()
                    }
                    is Result.Success -> {
                        pDialog.hide()
                        mainViewModel.deleteUser()
                        val intent = Intent(this, AuthActivity::class.java)
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK)
                        startActivity(intent)
                        finish()
                    }
                    is Result.Error -> {
                        pDialog.hide()
                        Toast.makeText(this, result.error, Toast.LENGTH_LONG).show()
                    }
                }
            }
        }
    }
}
