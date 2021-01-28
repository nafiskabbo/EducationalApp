package com.kabbodev.educational.ui.fragments

import android.app.Dialog
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.os.bundleOf
import androidx.navigation.NavController
import androidx.navigation.NavOptions
import androidx.navigation.fragment.findNavController
import com.kabbodev.educational.R
import com.kabbodev.educational.data.model.Question
import com.kabbodev.educational.databinding.FragmentResultBinding
import com.kabbodev.educational.ui.base.BaseFragment
import com.kabbodev.educational.ui.utils.createDialog
import com.kabbodev.educational.ui.viewModels.DashboardViewModel

class ResultFragment : BaseFragment<FragmentResultBinding, DashboardViewModel>() {

    private val TAG = "Result"
    private lateinit var navController: NavController
    private lateinit var loadingDialog: Dialog
    private var studentMark: Int = 0

    override fun getFragmentBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ) = FragmentResultBinding.inflate(inflater, container, false)

    override fun getViewModel() = DashboardViewModel::class.java

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        navController = findNavController()
        loadingDialog = createDialog(
            requireContext(),
            R.layout.loading_progress_dialog,
            R.drawable.progress_circle,
            false
        )
        loadingDialog.show()
        setupTheme()
        setupClickListeners()
    }

    private fun setupTheme() {
        viewModel.getUserQueList().observe(viewLifecycleOwner, {
            it?.let { list ->
                updateUI(list)
            }
        })
    }

    private fun updateUI(list: ArrayList<Question>) {
        binding.totalMark.text = list.size.toString()

        if (arguments?.getString("recap_mark") == null) {
            viewModel.getAnswerList().forEachIndexed { index, ans ->
                if (ans == list[index].answer) {
                    Log.d(TAG, "ans 1! $ans")
                    Log.d(TAG, "ans 2! ${list[index].answer}")
                    Log.d(TAG, "adding! $studentMark")
                    studentMark++
                }
            }
        } else {
            studentMark = arguments?.getString("recap_mark")!!.toInt()
        }

        binding.studentMark.text = studentMark.toString()
        loadingDialog.dismiss()
    }

    private fun setupClickListeners() {
        binding.recapBtn.setOnClickListener {
            navController.navigate(
                R.id.action_resultFragment_to_questionsFragment,
                bundleOf("recap" to "yes", "recap_mark" to studentMark.toString())
            )
        }
        binding.previousBtn.setOnClickListener {
            requireActivity().onBackPressed()
        }
        binding.nextBtn.setOnClickListener {
            val navOptions = NavOptions.Builder()
                .setEnterAnim(R.anim.slide_in_right)
                .setExitAnim(R.anim.slide_out_left)
                .setPopEnterAnim(R.anim.slide_in_left)
                .setPopExitAnim(R.anim.slide_out_right)
                .setPopUpTo(R.id.navigation_home, true)
                .build()
            navController.navigate(R.id.action_global_homeFragment, null, navOptions)
        }
    }

}