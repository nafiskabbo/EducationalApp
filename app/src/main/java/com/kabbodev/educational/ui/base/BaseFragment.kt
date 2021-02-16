package com.kabbodev.educational.ui.base

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.NavController
import androidx.navigation.fragment.findNavController
import androidx.viewbinding.ViewBinding
import com.kabbodev.educational.data.preferences.UserPreferences

abstract class BaseFragment<viewBinding : ViewBinding, viewModel : ViewModel> : Fragment() {

    protected lateinit var binding: viewBinding
    protected lateinit var viewModel: viewModel
    protected lateinit var navController: NavController
    protected lateinit var userPreferences: UserPreferences

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        userPreferences = UserPreferences(requireContext())
        binding = getFragmentBinding(inflater, container)
        val factory = ViewModelProvider.AndroidViewModelFactory.getInstance(requireActivity().application)
        viewModel = ViewModelProvider(requireActivity(), factory).get(getViewModel())
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        navController = findNavController()
        setupTheme()
        setupClickListeners()
    }

    abstract fun getFragmentBinding(inflater: LayoutInflater, container: ViewGroup?): viewBinding

    abstract fun getViewModel(): Class<viewModel>

    abstract fun setupTheme()

    abstract fun setupClickListeners()

}