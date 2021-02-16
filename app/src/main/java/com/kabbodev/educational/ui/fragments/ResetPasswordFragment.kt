package com.kabbodev.educational.ui.fragments

import android.graphics.Color
import android.text.Editable
import android.text.TextWatcher
import android.transition.TransitionManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AccelerateInterpolator
import android.view.animation.Animation
import android.view.animation.ScaleAnimation
import androidx.core.content.ContextCompat
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import com.kabbodev.educational.R
import com.kabbodev.educational.databinding.FragmentResetPasswordBinding
import com.kabbodev.educational.ui.base.BaseFragment
import com.kabbodev.educational.ui.viewModels.LoginViewModel

class ResetPasswordFragment : BaseFragment<FragmentResetPasswordBinding, LoginViewModel>() {

    override fun getFragmentBinding(inflater: LayoutInflater, container: ViewGroup?) =
        FragmentResetPasswordBinding.inflate(inflater, container, false)

    override fun getViewModel() = LoginViewModel::class.java

    override fun setupTheme() {
        val textWatcher = object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                checkInputs()
            }

            override fun afterTextChanged(s: Editable?) {}
        }
        binding.forgotPassEmail.addTextChangedListener(textWatcher)
    }

    override fun setupClickListeners() {
        binding.tvGoBack.setOnClickListener {
            requireActivity().onBackPressed()
        }
        binding.resetPassBtn.setOnClickListener {
            sendResetPassEmail()
        }
    }

    private fun sendResetPassEmail() {
        TransitionManager.beginDelayedTransition(binding.emailIconContainer)
        binding.emailIconText.visibility = View.GONE

        TransitionManager.beginDelayedTransition(binding.emailIconContainer)
        binding.emailIcon.visibility = View.VISIBLE
        binding.progressBar.visibility = View.VISIBLE

        binding.resetPassBtn.isEnabled = false
        binding.resetPassBtn.setTextColor(Color.argb(50, 255, 255, 255))

        Firebase.auth.sendPasswordResetEmail(binding.forgotPassEmail.text.toString())
            .addOnSuccessListener {
                val scaleAnimation = ScaleAnimation(
                    1F,
                    0F,
                    1F,
                    0F,
                    (binding.emailIcon.width / 2).toFloat(),
                    (binding.emailIcon.height / 2).toFloat()
                )
                scaleAnimation.duration = 100
                scaleAnimation.interpolator = AccelerateInterpolator()
                scaleAnimation.repeatMode = Animation.REVERSE
                scaleAnimation.repeatCount = 1

                val animationListener = object : Animation.AnimationListener {
                    override fun onAnimationStart(animation: Animation?) {}
                    override fun onAnimationEnd(animation: Animation?) {
                        binding.emailIconText.text = getString(R.string.recovery_email_sent)
                        binding.emailIconText.setTextColor(ContextCompat.getColor(requireContext(), R.color.green))
                        TransitionManager.beginDelayedTransition(binding.emailIconContainer)
                        binding.emailIconText.visibility = View.VISIBLE
                    }

                    override fun onAnimationRepeat(animation: Animation?) {
                        binding.emailIcon.setImageResource(R.drawable.green_email)
                    }
                }

                scaleAnimation.setAnimationListener(animationListener)
                binding.emailIcon.startAnimation(scaleAnimation)
                binding.progressBar.visibility = View.GONE
            }
            .addOnFailureListener {
                binding.emailIcon.setImageResource(R.drawable.red_email)
                binding.resetPassBtn.isEnabled = true
                binding.resetPassBtn.setTextColor(ContextCompat.getColor(requireContext(), R.color.white))
                binding.emailIconText.text = it.message
                binding.emailIconText.setTextColor(ContextCompat.getColor(requireContext(), R.color.red))
                TransitionManager.beginDelayedTransition(binding.emailIconContainer)
                binding.emailIconText.visibility = View.VISIBLE
                binding.progressBar.visibility = View.GONE
            }
    }

    private fun checkInputs() {
        if (binding.forgotPassEmail.text.isNotEmpty()) {
            binding.resetPassBtn.isEnabled = true
            binding.resetPassBtn.setTextColor(ContextCompat.getColor(requireContext(), R.color.white))
            return
        }
        binding.resetPassBtn.isEnabled = false
        binding.resetPassBtn.setTextColor(Color.argb(50, 255, 255, 255))
    }

}