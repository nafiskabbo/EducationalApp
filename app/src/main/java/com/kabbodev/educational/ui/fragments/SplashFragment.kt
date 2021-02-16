package com.kabbodev.educational.ui.fragments

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.lifecycle.lifecycleScope
import androidx.navigation.NavOptions
import com.kabbodev.educational.R
import com.kabbodev.educational.databinding.FragmentSplashBinding
import com.kabbodev.educational.ui.base.BaseFragment
import com.kabbodev.educational.ui.viewModels.DashboardViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class SplashFragment : BaseFragment<FragmentSplashBinding, DashboardViewModel>() {

    override fun getFragmentBinding(inflater: LayoutInflater, container: ViewGroup?) =
        FragmentSplashBinding.inflate(inflater, container, false)

    override fun getViewModel() = DashboardViewModel::class.java

    override fun onResume() {
        super.onResume()
        val navOptions = NavOptions.Builder()
            .setEnterAnim(R.anim.slide_in_right)
            .setExitAnim(R.anim.slide_out_left)
            .setPopEnterAnim(R.anim.slide_in_left)
            .setPopExitAnim(R.anim.slide_out_right)
            .setPopUpTo(R.id.splashFragment, true)
            .build()

        lifecycleScope.launch(Dispatchers.IO) {
            delay(1500)

            withContext(Dispatchers.Main) {
                if (viewModel.getCurrentUser() == null) {
                    navController.navigate(R.id.action_splashFragment_to_loginFragment, null, navOptions)
                } else {
                    navController.navigate(R.id.action_global_homeFragment, null, navOptions)
                }
            }
        }
    }

    override fun setupTheme() {}

    override fun setupClickListeners() {}

}