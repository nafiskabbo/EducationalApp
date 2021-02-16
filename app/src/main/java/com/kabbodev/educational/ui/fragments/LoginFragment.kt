package com.kabbodev.educational.ui.fragments

import android.graphics.Color
import android.text.Editable
import android.text.TextWatcher
import android.util.Patterns
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.navigation.NavOptions
import com.kabbodev.educational.R
import com.kabbodev.educational.data.model.User
import com.kabbodev.educational.databinding.FragmentLoginBinding
import com.kabbodev.educational.ui.interfaces.FirebaseCallback
import com.kabbodev.educational.ui.base.BaseFragment
import com.kabbodev.educational.ui.utils.snackbar
import com.kabbodev.educational.ui.viewModels.LoginViewModel

class LoginFragment : BaseFragment<FragmentLoginBinding, LoginViewModel>(), FirebaseCallback {

    override fun getFragmentBinding(inflater: LayoutInflater, container: ViewGroup?) =
        FragmentLoginBinding.inflate(inflater, container, false)

    override fun getViewModel() = LoginViewModel::class.java

    override fun setupTheme() {
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

    override fun setupClickListeners() {
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
            with(binding.loginBtn) {
                isEnabled = true
                setTextColor(ContextCompat.getColor(requireContext(), R.color.white))
            }
            return
        }
        with(binding.loginBtn) {
            isEnabled = false
            setTextColor(Color.argb(50, 255, 255, 255))
        }
    }

    private fun checkEmailAndPassword() {
        if (Patterns.EMAIL_ADDRESS.matcher(binding.emailEt.editText?.text.toString()).matches()) {
            binding.emailEt.isErrorEnabled = false

            if (binding.passwordEt.editText?.length()!! >= 8) {
                binding.passwordEt.isErrorEnabled = false

                binding.loginBtn.startAnimation {
                    viewModel.login(binding.emailEt.editText?.text.toString(), binding.passwordEt.editText?.text.toString(), this)
                }
            } else {
                with(binding.passwordEt) {
                    isErrorEnabled = true
                    error = getString(R.string.invalid_password)
                }
            }
        } else {
            with(binding.emailEt) {
                isErrorEnabled = true
                error = getString(R.string.invalid_email)
            }
        }
    }

    override fun onSuccessListener(user: User?) {
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
        with(binding) {
            loginBtn.revertAnimation()
            rootLayout.snackbar(e.message.toString())
        }
    }

}