package com.kabbodev.educational.ui.activities

import android.content.DialogInterface
import android.util.Log
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import androidx.appcompat.app.AlertDialog
import androidx.navigation.NavController
import androidx.navigation.Navigation
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.kabbodev.educational.R
import com.kabbodev.educational.databinding.ActivityMainBinding
import com.kabbodev.educational.ui.base.BaseActivity
import com.kabbodev.educational.ui.viewModels.DashboardViewModel
import com.razorpay.PaymentData
import com.razorpay.PaymentResultWithDataListener

class MainActivity : BaseActivity<ActivityMainBinding, DashboardViewModel>(),
    PaymentResultWithDataListener {

    private val TAG = "Subscription"
    private lateinit var navController: NavController
    private var currentFragment: Int = -1

    override fun getActivityBinding(inflater: LayoutInflater) =
        ActivityMainBinding.inflate(layoutInflater)

    override fun getViewModel() = DashboardViewModel::class.java

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

        navController = Navigation.findNavController(this, R.id.nav_host_fragment)
        val appBarConfiguration = AppBarConfiguration(
            setOf(
                R.id.navigation_home, R.id.navigation_plan, R.id.navigation_stats
            )
        )
        setupActionBarWithNavController(navController, appBarConfiguration)
        binding.navView.setupWithNavController(navController)

        navController.addOnDestinationChangedListener { _, destination, _ ->
            currentFragment = -1
            val id = destination.id
            if (id == R.id.splashFragment ||
                id == R.id.loginFragment ||
                id == R.id.registrationFragment ||
                id == R.id.resetPasswordFragment ||
                id == R.id.questionsFragment ||
                id == R.id.resultFragment
            ) {
                supportActionBar?.hide()
                if (binding.navView.visibility == View.VISIBLE) {
                    binding.navView.visibility = View.GONE
                }
                if (id == R.id.questionsFragment) {
                    currentFragment = 2
                }
                if (id == R.id.resultFragment) {
                    currentFragment = 3
                }

            } else {
                if (id == R.id.navigation_home) {
                    currentFragment = 1
                }
                supportActionBar?.show()
                if (id == R.id.plansDetailFragment) {
                    if (binding.navView.visibility == View.VISIBLE) {
                        binding.navView.visibility = View.GONE
                    }
                } else {
                    if (binding.navView.visibility == View.GONE) {
                        binding.navView.visibility = View.VISIBLE
                    }
                }
            }
        }
    }

    override fun setupClickListeners() {

    }

    override fun onBackPressed() {
        when (currentFragment) {
            1 -> {
                val clickListener: DialogInterface.OnClickListener =
                    DialogInterface.OnClickListener { _, _ ->
                        finishAndRemoveTask()
                        super.onBackPressed()
                    }

                AlertDialog.Builder(this@MainActivity)
                    .setTitle(getString(R.string.are_you_sure))
                    .setMessage(
                        String.format(
                            getString(R.string.process_delete),
                            getString(R.string.app_name)
                        )
                    )
                    .setPositiveButton(getString(R.string.ok), clickListener)
                    .setNegativeButton(getString(R.string.cancel), null)
                    .show()
            }
            2 -> {
                val clickListener: DialogInterface.OnClickListener =
                    DialogInterface.OnClickListener { _, _ ->
                        super.onBackPressed()
                    }

                AlertDialog.Builder(this@MainActivity)
                    .setTitle(getString(R.string.alert))
                    .setMessage(getString(R.string.alert_on))
                    .setPositiveButton(getString(R.string.ok), clickListener)
                    .setNegativeButton(getString(R.string.cancel), null)
                    .show()
            }
            3 -> {
                onResult = true
                super.onBackPressed()
            }
            else -> {
                super.onBackPressed()
            }
        }
    }

    override fun onPaymentSuccess(razorpayPaymentId: String?, paymentData: PaymentData?) {
        Log.d(TAG, "successful $razorpayPaymentId")
        try {
            viewModel.setOnSuccess(true)
        } catch (e: Exception) {
            Log.d(TAG, "error ${e.message}")
        }
    }

    override fun onPaymentError(code: Int, description: String?, paymentData: PaymentData?) {
        try {
            Log.d(TAG, "error code $code -- Payment failed $description")
            viewModel.setOnError(true)
        } catch (e: Exception) {
            Log.d(TAG, "error ${e.message}")
        }
    }

    companion object {
        var onResult: Boolean = false
    }

}