package com.kabbodev.educationalapp.ui.base

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.viewbinding.ViewBinding

abstract class BaseActivity<viewBinding : ViewBinding>: AppCompatActivity() {

    protected lateinit var binding: viewBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = getActivityBinding(layoutInflater)
        setContentView(binding.root)
        setupTheme()
        setupClickListeners()
    }

    abstract fun getActivityBinding(inflater: LayoutInflater): viewBinding

    abstract fun setupTheme()

    abstract fun setupClickListeners()

}