package com.kabbodev.educational.ui.fragments

import android.view.LayoutInflater
import android.view.ViewGroup
import android.webkit.WebViewClient
import com.kabbodev.educational.databinding.FragmentDoubtViewBinding
import com.kabbodev.educational.ui.activities.MainActivity
import com.kabbodev.educational.ui.base.BaseFragment
import com.kabbodev.educational.ui.viewModels.DashboardViewModel

class DoubtViewFragment : BaseFragment<FragmentDoubtViewBinding, DashboardViewModel>() {

    override fun getFragmentBinding(inflater: LayoutInflater, container: ViewGroup?) =
        FragmentDoubtViewBinding.inflate(inflater, container, false)

    override fun getViewModel() = DashboardViewModel::class.java

    override fun setupTheme() {
        val questionSolveLink = arguments?.getString("que_solve_link")

        with(binding.webView) {
            webViewClient = WebViewClient()
            loadUrl(questionSolveLink.toString())
        }

        val webSettings = binding.webView.settings
        webSettings.javaScriptEnabled = true

        MainActivity.webView = binding.webView
    }

    override fun setupClickListeners() {}

}