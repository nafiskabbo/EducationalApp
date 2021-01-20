package com.kabbodev.educationalapp.ui.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.lifecycleScope
import androidx.navigation.NavOptions
import androidx.navigation.Navigation
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import com.kabbodev.educationalapp.R
import com.kabbodev.educationalapp.databinding.FragmentSplashBinding
import com.kabbodev.educationalapp.ui.base.BaseFragment
import com.kabbodev.educationalapp.ui.viewModels.DashboardViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class SplashFragment : BaseFragment<FragmentSplashBinding, DashboardViewModel>() {

    override fun getFragmentBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ) = FragmentSplashBinding.inflate(inflater, container, false)

    override fun getViewModel() = DashboardViewModel::class.java

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val navController = Navigation.findNavController(view)

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

}