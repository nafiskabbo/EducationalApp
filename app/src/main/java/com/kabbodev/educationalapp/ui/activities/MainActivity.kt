package com.kabbodev.educationalapp.ui.activities

import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import androidx.navigation.NavController
import androidx.navigation.Navigation
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.kabbodev.educationalapp.R
import com.kabbodev.educationalapp.databinding.ActivityMainBinding
import com.kabbodev.educationalapp.ui.base.BaseActivity

class MainActivity : BaseActivity<ActivityMainBinding>() {

    private lateinit var navController: NavController

    override fun getActivityBinding(inflater: LayoutInflater) =
        ActivityMainBinding.inflate(layoutInflater)

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == android.R.id.home) {
            onBackPressed()
        }
        return super.onOptionsItemSelected(item)
    }

    override fun setupTheme() {
        setSupportActionBar(binding.include.toolbar)
        supportActionBar?.setDisplayShowTitleEnabled(true)
        supportActionBar?.setDisplayHomeAsUpEnabled(false)
        supportActionBar?.title = getString(R.string.app_name)

        val appBarConfiguration = AppBarConfiguration(
            setOf(
                R.id.navigation_home, R.id.navigation_plan, R.id.navigation_stats
            )
        )
        setupActionBarWithNavController(navController, appBarConfiguration)
        binding.navView.setupWithNavController(navController)

        navController = Navigation.findNavController(this, R.id.nav_host_fragment)
        navController.addOnDestinationChangedListener { _, destination, _ ->
            val id = destination.id
            if (id == R.id.splashFragment ||
                id == R.id.loginFragment ||
                id == R.id.registrationFragment ||
                id == R.id.resetPasswordFragment
            ) {
                supportActionBar?.hide()
                if (binding.navView.visibility == View.VISIBLE) {
                    binding.navView.visibility = View.GONE
                }
            } else {
                supportActionBar?.show()
                if (binding.navView.visibility == View.GONE) {
                    binding.navView.visibility = View.VISIBLE
                }
            }
        }
    }

    override fun setupClickListeners() {

    }

}