package com.kabbodev.educational.ui.fragments

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import com.kabbodev.educational.R
import com.kabbodev.educational.databinding.FragmentPlansBinding
import com.kabbodev.educational.ui.adapters.PlanAdapter
import com.kabbodev.educational.ui.adapters.PlansInterface
import com.kabbodev.educational.ui.base.BaseFragment
import com.kabbodev.educational.ui.viewModels.DashboardViewModel

class PlansFragment : BaseFragment<FragmentPlansBinding, DashboardViewModel>(), PlansInterface {

    private lateinit var adapter: PlanAdapter

    override fun getFragmentBinding(inflater: LayoutInflater, container: ViewGroup?) =
        FragmentPlansBinding.inflate(inflater, container, false)

    override fun getViewModel() = DashboardViewModel::class.java

    override fun setupTheme() {
        initRecyclerView()
        val user = viewModel.getUser().value
        val userClass = user?.class_
        val userBoard = user?.board
        viewModel.getPlansList(userClass!!, board = userBoard!!).observe(viewLifecycleOwner, { list ->
            list?.let {
                adapter.updateList(it)
            }
        })
    }

    override fun setupClickListeners() {}

    private fun initRecyclerView() {
        with(binding.recyclerView) {
            layoutManager = LinearLayoutManager(requireContext())
            setHasFixedSize(true)
            isNestedScrollingEnabled = false
        }
        adapter = PlanAdapter(this)
        binding.recyclerView.adapter = adapter
    }

    override fun onViewClick(itemPos: Int) {
        viewModel.setSelectedPlan(itemPos)
        navController.navigate(R.id.action_navigation_plan_to_plansDetailFragment)
    }

    override fun onBtnClick(itemPos: Int) {
        viewModel.setSelectedPlan(itemPos)
        if (adapter.plansList[itemPos].type == "Join") {
            navController.navigate(R.id.action_navigation_plan_to_plansDetailFragment)
        } else {
            navController.navigate(R.id.action_global_subscriptionBottomSheetFragment)
        }
    }

}