package com.kabbodev.educational.ui.fragments

import android.app.Dialog
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.os.bundleOf
import androidx.recyclerview.widget.LinearLayoutManager
import com.kabbodev.educational.R
import com.kabbodev.educational.data.model.Doubt
import com.kabbodev.educational.databinding.FragmentDoubtsBinding
import com.kabbodev.educational.ui.adapters.DoubtAdapter
import com.kabbodev.educational.ui.adapters.DoubtInterface
import com.kabbodev.educational.ui.base.BaseFragment
import com.kabbodev.educational.ui.interfaces.DoubtCallback
import com.kabbodev.educational.ui.utils.createDialog
import com.kabbodev.educational.ui.utils.snackbar
import com.kabbodev.educational.ui.viewModels.DashboardViewModel

class DoubtsFragment : BaseFragment<FragmentDoubtsBinding, DashboardViewModel>(), DoubtInterface, DoubtCallback {

    private lateinit var adapter: DoubtAdapter
    private lateinit var loadingDialog: Dialog

    override fun getFragmentBinding(inflater: LayoutInflater, container: ViewGroup?) =
        FragmentDoubtsBinding.inflate(inflater, container, false)

    override fun getViewModel() = DashboardViewModel::class.java

    override fun setupTheme() {
        loadingDialog = createDialog(requireContext(), R.layout.loading_progress_dialog, R.drawable.progress_circle, false)
        loadingDialog.show()
        initRecyclerView()
        viewModel.loadDoubtsList(this)
    }

    override fun setupClickListeners() {}

    private fun initRecyclerView() {
        with(binding.recyclerView) {
            layoutManager = LinearLayoutManager(requireContext())
            setHasFixedSize(true)
            isNestedScrollingEnabled = false
        }
        adapter = DoubtAdapter(this)
        binding.recyclerView.adapter = adapter
    }

    override fun onInfoBtnClick(itemPos: Int) {
        navController.navigate(
            R.id.action_navigation_doubts_to_doubtViewFragment,
            bundleOf("que_solve_link" to adapter.doubtList[itemPos].solveLink)
        )
    }

    override fun onSuccessListener(totalEmpty: Boolean, doubtList: List<Doubt>?) {
        loadingDialog.dismiss()
        if (!totalEmpty) {
            doubtList?.let {
                adapter.updateList(doubtList)
            }
        }
    }

    override fun onFailureListener(e: Exception) {
        loadingDialog.dismiss()
        binding.root.snackbar("Failed! ${e.message}")
    }

}