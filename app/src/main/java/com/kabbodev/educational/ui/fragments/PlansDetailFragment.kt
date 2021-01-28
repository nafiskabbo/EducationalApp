package com.kabbodev.educational.ui.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.NavController
import androidx.navigation.fragment.findNavController
import com.kabbodev.educational.R
import com.kabbodev.educational.data.model.Plan
import com.kabbodev.educational.databinding.FragmentPlansDetailBinding
import com.kabbodev.educational.ui.base.BaseFragment
import com.kabbodev.educational.ui.viewModels.DashboardViewModel

class PlansDetailFragment : BaseFragment<FragmentPlansDetailBinding, DashboardViewModel>() {

    private lateinit var navController: NavController
    private var planDetail: Plan? = null

    override fun getFragmentBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ) = FragmentPlansDetailBinding.inflate(inflater, container, false)

    override fun getViewModel() = DashboardViewModel::class.java

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        navController = findNavController()
        setupTheme()
        setupClickListeners()
    }

    override fun onResume() {
        super.onResume()
        (context as AppCompatActivity).supportActionBar?.title = planDetail?.title
    }

    private fun setupTheme() {
        viewModel.getSelectedPlan().observe(viewLifecycleOwner, { plan ->
            plan?.let {
                planDetail = it
                updateUI(planDetail!!)
            }
        })
    }

    private fun setupClickListeners() {
        binding.typeBtn.setOnClickListener {
            navController.navigate(R.id.action_global_subscriptionBottomSheetFragment)
        }
    }

    private fun updateUI(plan: Plan) {
        (context as AppCompatActivity).supportActionBar?.title = plan.title
        binding.typeBtn.text = plan.type
        binding.title.text = plan.title
        binding.subtitle.text = plan.subtitle
        binding.subtitle.text = plan.subtitle
        binding.price.text = String.format(
            getString(R.string.price_1_month),
            plan.price
        )
    }

}