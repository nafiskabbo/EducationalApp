package com.kabbodev.educational.ui.fragments

import android.app.Dialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.kabbodev.educational.R
import com.kabbodev.educational.data.model.User
import com.kabbodev.educational.databinding.FragmentStatsBinding
import com.kabbodev.educational.ui.`interface`.FirebaseCallback
import com.kabbodev.educational.ui.`interface`.StatsCallback
import com.kabbodev.educational.ui.base.BaseFragment
import com.kabbodev.educational.ui.utils.createDialog
import com.kabbodev.educational.ui.utils.snackbar
import com.kabbodev.educational.ui.viewModels.DashboardViewModel
import java.lang.Exception

class StatsFragment : BaseFragment<FragmentStatsBinding, DashboardViewModel>(), StatsCallback {

    private lateinit var loadingDialog: Dialog

    override fun getFragmentBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ) = FragmentStatsBinding.inflate(inflater, container, false)

    override fun getViewModel() = DashboardViewModel::class.java

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupTheme()
        setupClickListeners()
    }

    private fun setupTheme() {
        loadingDialog = createDialog(
            requireContext(),
            R.layout.loading_progress_dialog,
            R.drawable.slider_background,
            false
        )
    }

    private fun setupClickListeners() {
        binding.downloadDailyTaskBtn.setOnClickListener {
            downloadReport(getString(R.string.daily_task))
        }
        binding.downloadMyTestsBtn.setOnClickListener {
            downloadReport(getString(R.string.my_tests))
        }
        binding.downloadMyDoubtsBtn.setOnClickListener {
            downloadReport(getString(R.string.my_doubts))
        }
    }

    private fun downloadReport(type: String) {
        loadingDialog.show()
        viewModel.downloadReport(type, this)
    }

    override fun onSuccessListener(totalEmpty: Boolean) {
        loadingDialog.dismiss()
        if (totalEmpty) {
            binding.root.snackbar("No File Available")
        } else {
            binding.root.snackbar("Downloaded Successfully!")
        }
    }


    override fun onFailureListener(e: Exception) {
        loadingDialog.dismiss()
        binding.root.snackbar("Failed! ${e.message}")
    }

}