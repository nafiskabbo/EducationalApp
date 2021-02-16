package com.kabbodev.educational.ui.fragments

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import com.kabbodev.educational.R
import com.kabbodev.educational.data.model.Plan
import com.kabbodev.educational.databinding.FragmentPlansDetailBinding
import com.kabbodev.educational.ui.base.BaseFragment
import com.kabbodev.educational.ui.viewModels.DashboardViewModel

class PlansDetailFragment : BaseFragment<FragmentPlansDetailBinding, DashboardViewModel>() {

    private var planDetail: Plan? = null

    override fun getFragmentBinding(inflater: LayoutInflater, container: ViewGroup?) =
        FragmentPlansDetailBinding.inflate(inflater, container, false)

    override fun getViewModel() = DashboardViewModel::class.java

    override fun onResume() {
        super.onResume()
        (context as AppCompatActivity).supportActionBar?.title = planDetail?.title
    }

    override fun setupTheme() {
        viewModel.getSelectedPlan().observe(viewLifecycleOwner, { plan ->
            plan?.let {
                planDetail = it
                updateUI(planDetail!!)
            }
        })
    }

    override fun setupClickListeners() {
        binding.typeBtn.setOnClickListener {
            if (planDetail?.type == "Join") {
                navController.navigate(R.id.action_plansDetailFragment_to_liveClassJoinFragment)
            } else {
                navController.navigate(R.id.action_global_subscriptionBottomSheetFragment)
            }
        }
    }

    private fun updateUI(plan: Plan) {
        (context as AppCompatActivity).supportActionBar?.title = plan.title
        with(binding) {
            typeBtn.text = plan.type
            title.text = plan.title
            subtitle.text = plan.subtitle
            subtitle.text = plan.subtitle
            price.text = String.format(getString(R.string.price_1_month), plan.price)
        }
    }

}