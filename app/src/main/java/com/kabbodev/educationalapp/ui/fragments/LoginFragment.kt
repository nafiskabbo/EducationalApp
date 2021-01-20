package com.kabbodev.educationalapp.ui.fragments

import android.graphics.Color
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Patterns
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.navigation.NavController
import androidx.navigation.NavOptions
import androidx.navigation.Navigation
import com.kabbodev.educationalapp.R
import com.kabbodev.educationalapp.databinding.FragmentLoginBinding
import com.kabbodev.educationalapp.ui.`interface`.FirebaseCallback
import com.kabbodev.educationalapp.ui.base.BaseFragment
import com.kabbodev.educationalapp.ui.utils.snackbar
import com.kabbodev.educationalapp.ui.viewModels.LoginViewModel

class LoginFragment : BaseFragment<FragmentLoginBinding, LoginViewModel>(), FirebaseCallback {

    private lateinit var navController: NavController

    override fun getFragmentBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ) = FragmentLoginBinding.inflate(inflater, container, false)

    override fun getViewModel() = LoginViewModel::class.java

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        navController = Navigation.findNavController(view)
        setupTheme()
        setupClickListeners()
    }

    private fun setupTheme() {
        val textWatcher: TextWatcher = object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                checkInputs()
            }

            override fun afterTextChanged(s: Editable?) {
            }
        }
        binding.emailEt.editText?.addTextChangedListener(textWatcher)
        binding.passwordEt.editText?.addTextChangedListener(textWatcher)
    }

    private fun setupClickListeners() {
        binding.forgotPassText.setOnClickListener {
            navController.navigate(R.id.action_loginFragment_to_resetPasswordFragment)
        }
        binding.dontHaveAcc.setOnClickListener {
            navController.navigate(R.id.action_loginFragment_to_registrationFragment)
        }
        binding.loginBtn.setOnClickListener {
            checkEmailAndPassword()
        }
    }

    private fun checkInputs() {
        if (binding.emailEt.editText?.text!!.isNotEmpty() &&
            binding.passwordEt.editText?.text!!.isNotEmpty()
        ) {
            binding.loginBtn.isEnabled = true
            binding.loginBtn.setTextColor(ContextCompat.getColor(requireContext(), R.color.white))
            return
        }
        binding.loginBtn.isEnabled = false
        binding.loginBtn.setTextColor(Color.argb(50, 255, 255, 255))
    }

    private fun checkEmailAndPassword() {
        val customErrorIcon = ContextCompat.getDrawable(requireContext(), R.drawable.error_icon)
        customErrorIcon!!.setBounds(
            -16,
            0,
            customErrorIcon.intrinsicWidth - 16,
            customErrorIcon.intrinsicHeight
        )

        if (Patterns.EMAIL_ADDRESS.matcher(binding.emailEt.editText?.text.toString()).matches()) {
            binding.emailEt.isErrorEnabled = false

            if (binding.passwordEt.editText?.length()!! >= 8) {
                binding.passwordEt.isErrorEnabled = false

                binding.loginBtn.startAnimation {
                    viewModel.login(
                        binding.emailEt.editText?.text.toString(),
                        binding.passwordEt.editText?.text.toString(),
                        this
                    )
                }

            } else {
                binding.passwordEt.isErrorEnabled = true
                binding.passwordEt.error = getString(R.string.invalid_password)
                binding.passwordEt.errorIconDrawable = customErrorIcon
            }
        } else {
            binding.emailEt.isErrorEnabled = true
            binding.emailEt.error = getString(R.string.invalid_email)
            binding.emailEt.errorIconDrawable = customErrorIcon
        }
    }

    override fun onSuccessListener() {
        binding.loginBtn.revertAnimation()

        val navOptions = NavOptions.Builder()
            .setEnterAnim(R.anim.slide_in_right)
            .setExitAnim(R.anim.slide_out_left)
            .setPopEnterAnim(R.anim.slide_in_left)
            .setPopExitAnim(R.anim.slide_out_right)
            .setPopUpTo(R.id.loginFragment, true)
            .build()

        navController.navigate(R.id.action_global_homeFragment, null, navOptions)
    }

    override fun onFailureListener(e: Exception) {
        binding.loginBtn.revertAnimation()
        binding.rootLayout.snackbar(e.message.toString())
    }

}