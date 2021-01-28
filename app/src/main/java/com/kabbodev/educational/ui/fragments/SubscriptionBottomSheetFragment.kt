package com.kabbodev.educational.ui.fragments

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.kabbodev.educational.R
import com.kabbodev.educational.data.model.Plan
import com.kabbodev.educational.data.model.User
import com.kabbodev.educational.databinding.FragmentSubscriptionBottomSheetBinding
import com.kabbodev.educational.ui.`interface`.FirebaseCallback
import com.kabbodev.educational.ui.utils.snackbar
import com.kabbodev.educational.ui.viewModels.DashboardViewModel
import com.razorpay.Checkout
import org.json.JSONObject

class SubscriptionBottomSheetFragment : BottomSheetDialogFragment(), FirebaseCallback {

    private val TAG = "Subscription"
    private lateinit var binding: FragmentSubscriptionBottomSheetBinding
    private lateinit var viewModel: DashboardViewModel
    private var planDetails: Plan? = null
    private var paymentAmount1Month: String? = null
    private var paymentAmount: String? = null
    private var paymentMonth: String? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentSubscriptionBottomSheetBinding.inflate(inflater, container, false)
        val factory =
            ViewModelProvider.AndroidViewModelFactory.getInstance(requireActivity().application)
        viewModel =
            ViewModelProvider(requireActivity(), factory).get(DashboardViewModel::class.java)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupTheme()
        setupClickListeners()
    }

    override fun onResume() {
        super.onResume()

    }

    private fun setupTheme() {
        viewModel.getSelectedPlan().observe(viewLifecycleOwner, {
            it?.let { plan ->
                planDetails = plan
                updateUI(plan)
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

    private fun setupClickListeners() {
        binding.oneMonth.setOnClickListener {
            paymentMonth = "1"
            paymentAmount = paymentAmount1Month
            razorpay()
        }
        binding.threeMonth.setOnClickListener {
            paymentMonth = "3"
            paymentAmount = (paymentAmount1Month!!.toInt() * 3).toString()
            razorpay()
        }
        binding.sixMonth.setOnClickListener {
            paymentMonth = "6"
            paymentAmount = (paymentAmount1Month!!.toInt() * 6).toString()
            razorpay()
        }
    }

    private fun updateUI(plan: Plan) {
        (context as AppCompatActivity).supportActionBar?.title = plan.title

        paymentAmount1Month = plan.price

        binding.title.text = plan.title
        binding.oneMonth.text = String.format(getString(R.string.rs_month), paymentAmount1Month)
        binding.threeMonth.text =
            String.format(getString(R.string.rs_months), (paymentAmount1Month!!.toInt() * 3), "3")
        binding.sixMonth.text =
            String.format(getString(R.string.rs_months), (paymentAmount1Month!!.toInt() * 6), "6")
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
            viewModel.saveSubscriptionDetails(
                viewModel.getUser().value!!,
                planDetails?.id!!,
                paymentMonth!!,
                this
            )

        } catch (e: Exception) {
            Log.d(TAG, "error ${e.message}")
            this.dismiss()
        }
    }

    private fun onPaymentError() {
        binding.bottomSheetContainer.snackbar("Payment Failed! Try again later!")
        this.dismiss()
    }

    override fun onSuccessListener(user: User?) {
        Log.d(TAG, "user $user")
        viewModel.setUser(user!!)
        binding.bottomSheetContainer.snackbar("Payment Successful!")
        this.dismiss()
    }

    override fun onFailureListener(e: Exception) {
        Log.d(TAG, "error $e ")
        this.dismiss()
        binding.bottomSheetContainer.snackbar("Something Went Wrong! ${e.message}")
    }

}