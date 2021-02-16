package com.kabbodev.educational.ui.fragments

import android.content.Intent
import android.net.Uri
import android.view.LayoutInflater
import android.view.ViewGroup
import com.kabbodev.educational.data.model.Plan
import com.kabbodev.educational.databinding.FragmentLiveClassJoinBinding
import com.kabbodev.educational.ui.base.BaseFragment
import com.kabbodev.educational.ui.utils.snackbar
import com.kabbodev.educational.ui.viewModels.DashboardViewModel

class LiveClassJoinFragment : BaseFragment<FragmentLiveClassJoinBinding, DashboardViewModel>() {

    private var planDetail: Plan? = null

    override fun getFragmentBinding(inflater: LayoutInflater, container: ViewGroup?) =
        FragmentLiveClassJoinBinding.inflate(inflater, container, false)

    override fun getViewModel() = DashboardViewModel::class.java

    override fun setupTheme() {
        viewModel.getSelectedPlan().observe(viewLifecycleOwner, { plan ->
            plan?.let {
                planDetail = it
                updateUI(planDetail!!)
            }
        })
    }

    private fun updateUI(planDetail: Plan) {
        binding.bodyText.text = planDetail.contact_info
    }

    override fun setupClickListeners() {
        binding.paymentBtn.setOnClickListener {
            binding.root.snackbar("Opening!")
            val intent = Intent(Intent.ACTION_VIEW, Uri.parse(planDetail?.payment_link.toString()))
            startActivity(intent)
        }
    }

}