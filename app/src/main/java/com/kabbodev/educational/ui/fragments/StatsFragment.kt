package com.kabbodev.educational.ui.fragments

import android.app.Dialog
import android.content.Intent
import android.net.Uri
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import com.kabbodev.educational.R
import com.kabbodev.educational.databinding.FragmentStatsBinding
import com.kabbodev.educational.ui.interfaces.StatsCallback
import com.kabbodev.educational.ui.base.BaseFragment
import com.kabbodev.educational.ui.utils.createDialog
import com.kabbodev.educational.ui.utils.snackbar
import com.kabbodev.educational.ui.viewModels.DashboardViewModel

class StatsFragment : BaseFragment<FragmentStatsBinding, DashboardViewModel>(), StatsCallback {

    private lateinit var loadingDialog: Dialog

    override fun getFragmentBinding(inflater: LayoutInflater, container: ViewGroup?) =
        FragmentStatsBinding.inflate(inflater, container, false)

    override fun getViewModel() = DashboardViewModel::class.java

    override fun setupTheme() {
        loadingDialog = createDialog(requireContext(), R.layout.loading_progress_dialog, R.drawable.progress_circle, false)
    }

    override fun setupClickListeners() {
        binding.downloadDailyTaskBtn.setOnClickListener {
            downloadReport(getString(R.string.daily_task))
        }
        binding.downloadMyTestsBtn.setOnClickListener {
            downloadReport(getString(R.string.my_tests))
        }
        binding.seeMyDoubtsBtn.setOnClickListener {
            findNavController().navigate(R.id.action_navigation_stats_to_doubtListFragment)
        }
    }

    private fun downloadReport(type: String) {
        loadingDialog.show()
        viewModel.downloadReport(type, this)
    }

    override fun onSuccessListener(totalEmpty: Boolean, downloadFile: String?) {
        loadingDialog.dismiss()
        if (totalEmpty) {
            binding.root.snackbar("No File Available")
        } else {
            binding.root.snackbar("Downloading!")
            val intent = Intent(Intent.ACTION_VIEW, Uri.parse(downloadFile))
            startActivity(intent)
        }
    }

    override fun onFailureListener(e: Exception) {
        loadingDialog.dismiss()
        binding.root.snackbar("Failed! ${e.message}")
    }

}