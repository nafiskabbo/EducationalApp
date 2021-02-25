package com.kabbodev.educational.ui.fragments

import android.app.Dialog
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import com.kabbodev.educational.R
import com.kabbodev.educational.data.model.Plan
import com.kabbodev.educational.data.model.User
import com.kabbodev.educational.databinding.FragmentPlansDetailBinding
import com.kabbodev.educational.databinding.FragmentSubscriptionBottomSheetBinding
import com.kabbodev.educational.ui.base.BaseFragment
import com.kabbodev.educational.ui.interfaces.FirebaseCallback
import com.kabbodev.educational.ui.utils.createDialog
import com.kabbodev.educational.ui.utils.snackbar
import com.kabbodev.educational.ui.viewModels.DashboardViewModel
import com.razorpay.Checkout
import org.json.JSONObject

class PlansDetailFragment : BaseFragment<FragmentPlansDetailBinding, DashboardViewModel>(), FirebaseCallback {

    private val TAG = "Subscription"
    private var planDetail: Plan? = null
    private var paymentAmount1Month: String? = null
    private var paymentAmount: String? = null
    private var paymentMonth: String? = null
    private lateinit var dialog: Dialog
    private lateinit var dialogBinding: FragmentSubscriptionBottomSheetBinding

    override fun getFragmentBinding(inflater: LayoutInflater, container: ViewGroup?) =
        FragmentPlansDetailBinding.inflate(inflater, container, false)

    override fun getViewModel() = DashboardViewModel::class.java

    override fun onResume() {
        super.onResume()
        (context as AppCompatActivity).supportActionBar?.title = planDetail?.title
    }

    override fun setupTheme() {
        setupDialog()
        viewModel.getSelectedPlan().observe(viewLifecycleOwner, { plan ->
            plan?.let {
                planDetail = it
                updateUI(planDetail!!)
            }
        })
        viewModel.getOnSuccess().observe(viewLifecycleOwner, {
            it.let { onSuccess ->
                if (onSuccess) {
                    onPaymentSuccess()
                }
            }
        })
        viewModel.getOnError().observe(viewLifecycleOwner, {
            it.let { onError ->
                if (onError) {
                    onPaymentError()
                }
            }
        })
    }

    private fun setupDialog() {
        dialogBinding = FragmentSubscriptionBottomSheetBinding.inflate(layoutInflater, binding.root, false)
        dialog = createDialog(dialogBinding, R.drawable.slider_background, true)
    }

    override fun setupClickListeners() {
        binding.typeBtn.setOnClickListener {
            if (planDetail?.type == "Join") {
                navController.navigate(R.id.action_plansDetailFragment_to_liveClassJoinFragment)
            } else {
                dialog.show()
            }
        }
        dialogBinding.oneMonth.setOnClickListener {
            paymentMonth = "1"
            paymentAmount = paymentAmount1Month
            razorpay()
        }
        dialogBinding.threeMonth.setOnClickListener {
            paymentMonth = "3"
            paymentAmount = (paymentAmount1Month!!.toInt() * 3).toString()
            razorpay()
        }
        dialogBinding.sixMonth.setOnClickListener {
            paymentMonth = "6"
            paymentAmount = (paymentAmount1Month!!.toInt() * 6).toString()
            razorpay()
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

        paymentAmount1Month = plan.price
        with(dialogBinding) {
            title.text = plan.title
            oneMonth.text = String.format(getString(R.string.rs_month), paymentAmount1Month)
            threeMonth.text = String.format(getString(R.string.rs_months), (paymentAmount1Month!!.toInt() * 3), "3")
            sixMonth.text = String.format(getString(R.string.rs_months), (paymentAmount1Month!!.toInt() * 6), "6")
        }
    }

    private fun razorpay() {
        val checkout = Checkout()
        checkout.setImage(R.drawable.ic_test)

        try {
            val options = JSONObject()
            options.put("name", getString(R.string.app_name))
            options.put("description", "Subscription")
            options.put("currency", "INR")
            options.put("send_sms_hash", true)

            val total: Double = paymentAmount!!.toDouble() * 100
            options.put("amount", total)
            checkout.open(requireActivity(), options)
        } catch (e: Exception) {
            Log.d(TAG, "error ${e.message}")
        }

    }

    private fun onPaymentSuccess() {
        try {
            viewModel.saveSubscriptionDetails(viewModel.getUser().value!!, planId = planDetail?.id!!, paymentMonth = paymentMonth!!, this)
        } catch (e: Exception) {
            Log.d(TAG, "error ${e.message}")
            dialog.dismiss()
        }
    }

    private fun onPaymentError() {
        binding.root.snackbar("Payment Failed! Try again later!")
        dialog.dismiss()
    }

    override fun onSuccessListener(user: User?) {
        Log.d(TAG, "user $user")
        viewModel.setUser(user!!)
        binding.root.snackbar("Payment Successful!")
        dialog.dismiss()
    }

    override fun onFailureListener(e: Exception) {
        Log.d(TAG, "error $e ")
        dialog.dismiss()
        binding.root.snackbar("Something Went Wrong! ${e.message}")
    }

}