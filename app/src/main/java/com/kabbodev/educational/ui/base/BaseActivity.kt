package com.kabbodev.educational.ui.base

import android.os.Bundle
import android.view.LayoutInflater
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.viewbinding.ViewBinding

abstract class BaseActivity<viewBinding : ViewBinding, viewModel : ViewModel>: AppCompatActivity() {

    protected lateinit var binding: viewBinding
    protected lateinit var viewModel: viewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = getActivityBinding(layoutInflater)
        setContentView(binding.root)
        val factory =
            ViewModelProvider.AndroidViewModelFactory.getInstance(application)
        viewModel = ViewModelProvider(this, factory).get(getViewModel())
        setupTheme()
        setupClickListeners()
    }

    abstract fun getActivityBinding(inflater: LayoutInflater): viewBinding

    abstract fun getViewModel(): Class<viewModel>

    abstract fun setupTheme()

    abstract fun setupClickListeners()

}