package com.kabbodev.educationalapp.ui.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.kabbodev.educationalapp.databinding.FragmentStatsBinding
import com.kabbodev.educationalapp.ui.base.BaseFragment
import com.kabbodev.educationalapp.ui.viewModels.DashboardViewModel

class StatsFragment : BaseFragment<FragmentStatsBinding, DashboardViewModel>() {

    override fun getFragmentBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ) = FragmentStatsBinding.inflate(inflater, container, false)

    override fun getViewModel(): Class<DashboardViewModel> = DashboardViewModel::class.java

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupTheme()
        setupClickListeners()
    }

    private fun setupTheme() {

    }

    private fun setupClickListeners() {

    }

}